package pt.utl.ist.repox.accessPoint.database;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointRecordRepoxFull;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.IdExtracted;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.ZipUtil;
import pt.utl.ist.util.sql.SqlUtil;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Implementation of an AccessPointsManager based on a Sql database
 * @author Nuno Freire
 *
 */
public class AccessPointsManagerSql extends AccessPointsManager {
    private static final Logger log = Logger.getLogger(AccessPointsManagerSql.class);

    protected DatabaseAccess databaseAccess;

    public AccessPointsManagerSql(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    private void initDataSource(Connection con, DataSource dataSource, boolean isUpdate) {
        String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());

        for (AccessPoint ap : dataSource.getAccessPoints().values()) {
            String table = ap.getId().toLowerCase();

            if(!tableExists(table, con)) {
                String valueType = databaseAccess.getVarType(ap.typeOfIndex());
                boolean indexValue = getIsValueIndexable(ap.typeOfIndex());

                databaseAccess.createTableIndexes(con, idType, table, valueType, indexValue);
            }
        }
    }

    private void renameTableIndexes(Connection con, boolean indexValue, String oldTableName, String newTableName) {
        String renameTableQuery = databaseAccess.renameTableString(oldTableName, newTableName);
        log.info(renameTableQuery);
        SqlUtil.runUpdate(renameTableQuery, con);

        String iSystemRenameIndexQuery = databaseAccess.renameIndexString(oldTableName + "_i_nc ", newTableName + "_i_nc ");
        SqlUtil.runUpdate(iSystemRenameIndexQuery, con);

        if(indexValue) {
            iSystemRenameIndexQuery = databaseAccess.renameIndexString(oldTableName + "_i_val ", newTableName + "_i_val ");
            SqlUtil.runUpdate(iSystemRenameIndexQuery, con);
        }
    }

    private boolean tableExists(String table, Connection con) {
        try {
            SqlUtil.getSingleValue("select 1 from " + table, con);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    private void setStatementParameter(PreparedStatement insStatement, Object value, int paramCounter, Class classOfParameter)
            throws SQLException {
        if(classOfParameter.equals(String.class)) {
            String valString = (String) value;
            insStatement.setString(paramCounter, (valString.length() > 255 ? valString.substring(0, 255) : valString));
        } else if(classOfParameter.equals(Integer.class)) {
            insStatement.setInt(paramCounter, (Integer) value);
        } else if(classOfParameter.equals(Date.class)) {
            insStatement.setDate(paramCounter, new java.sql.Date(((Date) value).getTime()));
        } else if(classOfParameter.equals(byte[].class)) {
            insStatement.setBytes(paramCounter, (byte[]) value);
        } else {
            throw new RuntimeException("Index type not implemented");
        }
    }

    private List<Object[]> getTripleArraysToInsert(String identifier, boolean isDeleted, Object value) {
        Integer isDeletedInteger = (isDeleted ? 1 : 0);
        List<Object[]> allValues = new ArrayList<Object[]>();
        if(value instanceof List) {
            List valueList = (List) value;
            for (Object aValueList : valueList) {
                allValues.add(new Object[]{identifier, isDeletedInteger, aValueList});
            }
        } else {
            allValues.add(new Object[] {identifier, isDeletedInteger, value });
        }

        return allValues;
    }

    private Collection getFieldFromDataSource(DataSource dataSource, String fromDateString, String toDateString,
                                              Integer offset, Integer numberResults, String field, Class itemClass) throws SQLException {
        String recordTable = dataSource.getId() + "_record";
        String timestampTable = dataSource.getId() + "_timestamp";

        if(offset == null || offset < 0) {
            offset = 0;
        }
        boolean noResultLimit = (numberResults == null || numberResults <= 0);

        Connection con = databaseAccess.openDbConnection();

        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if(!noResultLimit) {
                stmt.setMaxRows(numberResults + offset);
            }
            String query = "select " + recordTable.toLowerCase() + "." + field + " from " + recordTable.toLowerCase();
            if(fromDateString != null || toDateString != null) {
                query += ", " + timestampTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = "
                        + timestampTable.toLowerCase() + ".nc";
                if(fromDateString != null) {
                    query += " and " + timestampTable.toLowerCase() + ".value >= '" + fromDateString + "'";
                }
                if(toDateString != null) {
                    query += " and " + timestampTable.toLowerCase() + ".value <= '" + toDateString + "'";
                }
            }

            log.debug(query);

            ResultSet rs = stmt.executeQuery(query);
            rs.absolute(offset);
            ArrayList allResults = new ArrayList();
            int counter = 0;

            while (rs.next()) {
                if(noResultLimit || counter <= numberResults) {
                    if(itemClass.equals(String.class)) {
                        allResults.add(rs.getString(1));
                    } else if(itemClass.equals(byte[].class)) {
                        allResults.add(rs.getBytes(1));
                    } else if(itemClass.equals(Date.class)) {
                        allResults.add(rs.getDate(1));
                    } else {
                        throw new SQLException("Unsupported class: " + itemClass.getName());
                    }
                }
                counter++;
            }

            return allResults;
        }
        finally {
            con.close();
        }
    }

    private String getField(Urn urnOfRecord, String tableSuffix, Class classofField) throws SQLException, IOException, DocumentException {
        Connection con = databaseAccess.openDbConnection();

        try {
            Statement stmt = con.createStatement();

            String recordTable = urnOfRecord.getDataSourceId() + tableSuffix;
            String query = "select " + recordTable.toLowerCase() + ".value from " + recordTable.toLowerCase() + " where "
                    + recordTable.toLowerCase() + ".nc = '" + urnOfRecord.getRecordId() + "'";

            log.debug(query);
            ResultSet rs = stmt.executeQuery(query);

            String returnString = null;
            if(rs.next()) {
                if(classofField.equals(Date.class)) {
                    returnString = new SimpleDateFormat(TimeUtil.SHORT_DATE_FORMAT).format(new Date(rs.getDate(1).getTime()));
                } else if(classofField.equals(byte[].class)) {
                    returnString = new String(rs.getBytes(1), "UTF-8");
                }
            }

            return returnString;
        }
        finally {
            con.close();
        }
    }

    private OaiListResponse getHeaderAndRecordFromDataSource(DataSource dataSource, String fromDateString,
                                                             String toDateString, Integer offset, Integer numberResults, boolean retrieveFullRecord, Class itemClass)
            throws SQLException, IOException {
        OaiListResponse oaiListResponse = new OaiListResponse();

        String recordTable = dataSource.getId() + "_record";
        String timestampTable = dataSource.getId() + "_timestamp";

        if(offset == null || offset < 0) {
            offset = 0;
        }
        boolean noResultLimit = (numberResults == null || numberResults <= 0);

        Connection con = databaseAccess.openDbConnection();

        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if(!noResultLimit) {
                stmt.setMaxRows(numberResults);
            }
            String query = "select " + recordTable.toLowerCase() + ".nc, "
                    + timestampTable.toLowerCase() + ".deleted" + ", "
                    + timestampTable.toLowerCase() + ".value" + ", "
                    + recordTable.toLowerCase() + ".id";
            if(retrieveFullRecord) {
                query += ", " + recordTable.toLowerCase() + ".value";
            }

            query += " from " + recordTable.toLowerCase() + ", " + timestampTable.toLowerCase() + " where "
                    + recordTable.toLowerCase() + ".nc = " + timestampTable.toLowerCase() + ".nc";

            if(fromDateString != null || toDateString != null) {
                if(fromDateString != null) {
                    query += " and " + timestampTable.toLowerCase() + ".value >= '" + fromDateString + "'";
                }
                if(toDateString != null) {
                    query += " and " + timestampTable.toLowerCase() + ".value <= '" + toDateString + "'";
                }
            }

            query += " and " + recordTable.toLowerCase() + ".id > " + offset + " order by " + recordTable.toLowerCase()
                    + ".id";

            log.info(query);

            ResultSet rs = stmt.executeQuery(query);

            oaiListResponse.setOaiItems(new ArrayList<OaiItem>());
            while (rs.next()) {
//				byte[] recordBytes = (retrieveFullRecord ? rs.getBytes(5) : null);
                byte[] recordBytes = (retrieveFullRecord && rs.getBytes(5) != null ? ZipUtil.unzip(rs.getBytes(5)) : null);

                boolean recordDeleted = (rs.getInt(2) == 1);
                oaiListResponse.getOaiItems().add(
                        oaiListResponse.new OaiItem(rs.getObject(1).toString(),
                                DateFormatUtils.format(rs.getDate(3), TimeUtil.SHORT_DATE_FORMAT),
                                dataSource.getId(),
                                recordDeleted,
                                recordBytes));
            }

            if(rs.previous() && rs.previous()) { // get the result before last
                int lastIdentifier = rs.getInt(4);
                oaiListResponse.setLastRequestedIdentifier(lastIdentifier);
            }

            return oaiListResponse;
        }
        finally {
            con.close();
        }
    }

    @Override
    public void initialize(Collection<DataSource> dataSources) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {
            for (DataSource dataSource : dataSources) {
                initDataSource(con, dataSource, false);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            con.close();
        }
    }

    @Override
    public void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex, String oldAccessPointId,
                                            String newAccessPointId) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {
            String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());
            String valueType = databaseAccess.getVarType(typeOfIndex);
            boolean indexValue = getIsValueIndexable(typeOfIndex);

            String oldTableName = oldAccessPointId.toLowerCase();
            String newTableName = newAccessPointId.toLowerCase();

            if(!tableExists(oldTableName, con)) {
                databaseAccess.createTableIndexes(con, idType, newTableName, valueType, indexValue);
            } else {
                renameTableIndexes(con, indexValue, oldTableName, newTableName);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            con.close();
        }
    }

    @Override
    public void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {

            String table = accessPoint.getId().toLowerCase();

            if(tableExists(table, con)) {
                deleteIndex(accessPoint);
            }

            String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());
            String valueType = databaseAccess.getVarType(accessPoint.typeOfIndex());
            boolean indexValue = getIsValueIndexable(accessPoint.typeOfIndex());

            databaseAccess.createTableIndexes(con, idType, table, valueType, indexValue);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            con.close();
        }
    }

    @Override
    public void deleteIndex(AccessPoint accessPoint) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {
            String table = accessPoint.getId().toLowerCase();

            boolean tableExists = tableExists(table, con);

            if(tableExists) {
                databaseAccess.deleteTable(con, table);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            con.close();
        }
    }

    @Override
    protected void updateIndex(RecordRepox record, Collection values, AccessPoint accessPoint) throws SQLException, IOException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        TimeUtil.getTimeSinceLastTimerArray(8);
        try {
            String delim = ((record.getId() instanceof Integer || record.getId() instanceof Long) ? ""
                    : "'");
            String recId = delim + record.getId() + delim;
            String table = accessPoint.getId().toLowerCase();

            String query = "delete from " + table + " where nc = " + recId;

            PreparedStatement delPs = con.prepareStatement(query);
            delPs.executeUpdate();
            delPs.close();

            if(values.size() > 0) {
                int deletedInt = (record.isDeleted() ? 1 : 0);
                String statementString = "insert into " + table + "(nc, deleted, value) values (" + recId + ", " + deletedInt + ", ?)";
                PreparedStatement insPs = con.prepareStatement(statementString);

                for (Object val : values) {
                    log.debug(statementString + "; value = " + val);

                    insPs.clearParameters();
                    if(accessPoint.typeOfIndex().equals(String.class)) {
                        if(((String) val).length() > 255) {
                            insPs.setString(1, ((String) val).substring(0, 255));
                        } else {
                            insPs.setString(1, (String) val);
                        }
                    } else if(accessPoint.typeOfIndex().equals(Integer.class)) {
                        insPs.setInt(1, (Integer) val);
                    } else if(accessPoint.typeOfIndex().equals(Date.class)) {
                        insPs.setDate(1, new java.sql.Date(((Date) val).getTime()));
                    } else if(accessPoint.typeOfIndex().equals(byte[].class)) {
                        byte[] valueToInsert = ZipUtil.zip((byte[]) val);
                        insPs.setBytes(1, valueToInsert);
                    } else {
                        throw new RuntimeException("Index type not implemented");
                    }

                    try {
                        TimeUtil.getTimeSinceLastTimerArray(9);
                        insPs.executeUpdate();
                        insPs.clearParameters();
                    }
                    catch (SQLException e) {
                        log.error(e.getMessage() + "rec: " + recId + " table: " + table + " value:" + val.toString(), e);
                        throw e;
                    }
                }
                insPs.close();
            }
        }
        finally {
            con.close();
        }
    }

    @Override
    protected void updateIndex(DataSource dataSource, List<RecordRepox> records, List values, AccessPoint accessPoint) throws SQLException, IOException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        if(records == null || records.isEmpty()) {
            return;
        }

        try {
            TimeUtil.getTimeSinceLastTimerArray(5);
            //String delim = ((records.get(0).getId() instanceof Integer || records.get(0).getId() instanceof Long) ? "" : "'");
            String table = accessPoint.getId().toLowerCase();

            /* DELETE old indexes */
            //If Data Source has ids extracted or is DataSourceOai, the ids are extracted from the records and need
            // to be deleted before being inserted again (for performance: it's faster than updating)
            if(dataSource.getRecordIdPolicy() instanceof IdExtracted || dataSource instanceof DataSourceOai) {
                String deleteQuery = "delete from " + table + " where nc = ?";

                PreparedStatement delStatement = con.prepareStatement(deleteQuery);

                int deletedRecords = 0;
                for (RecordRepox record : records) {
                    delStatement.setString(1, (String) record.getId());

                    int result = delStatement.executeUpdate();

                    deletedRecords += result;
                    delStatement.clearParameters();
                }

                if(accessPoint instanceof AccessPointRecordRepoxFull && deletedRecords > 0) {
                    RepoxContextUtil.getRepoxManager().getRecordCountManager()
                            .updateDeletedRecordsCount(dataSource.getId(), deletedRecords);
                }

                delStatement.close();
            }

            /* INSERT new indexes */
            List<Object[]> pairsForInsertion = new ArrayList<Object[]>();
            for (int i = 0; i < records.size(); i++) {
                Object valueToInsert;

                if(values.get(i) != null && values.get(i).getClass().equals(byte[].class)) {
                    valueToInsert = ZipUtil.zip((byte[]) values.get(i));
                }
                else {
                    valueToInsert = values.get(i);
                }
                pairsForInsertion.addAll(getTripleArraysToInsert((String) records.get(i).getId(), records.get(i).isDeleted(), valueToInsert));
            }

            String pairsValues = "(?, ?, ?)";
            for (int i = 1; i < pairsForInsertion.size(); i++) {
                pairsValues += ", (?, ?, ?)";
            }

            String insStatementString = "insert into " + table + "(nc, deleted, value) values " + pairsValues;

            PreparedStatement insStatement = con.prepareStatement(insStatementString);
            int paramCounter = 1;
            for (Object[] aPairsForInsertion : pairsForInsertion) {
                setStatementParameter(insStatement, aPairsForInsertion[0], paramCounter, String.class);
                paramCounter++;
                setStatementParameter(insStatement, aPairsForInsertion[1], paramCounter, Integer.class);
                paramCounter++;
                setStatementParameter(insStatement, aPairsForInsertion[2], paramCounter, accessPoint.typeOfIndex());
                paramCounter++;
            }

            double prepareTime = TimeUtil.getTimeSinceLastTimerArray(5) / 1000.0;
            insStatement.executeUpdate();
            insStatement.clearParameters();
            double executeTime = TimeUtil.getTimeSinceLastTimerArray(5) / 1000.0;
            double totalTime = prepareTime + executeTime;
            log.info(accessPoint.getId() + " DB statement prepared/executed/total (s) " + prepareTime + "/" + executeTime
                    + "/" + totalTime);

            insStatement.close();
        }
        finally {
            con.close();
        }
    }

    @Override
    protected void deleteFromIndex(Urn recordUrn, AccessPoint accessPoint) throws DocumentException, SQLException, IOException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {
            String delim = ((recordUrn.getRecordId() instanceof Integer || recordUrn.getRecordId() instanceof Long) ? ""
                    : "'");
            String recId = delim + recordUrn.getRecordId() + delim;
            String table = accessPoint.getId().toLowerCase();

            String query = "update " + table + " set deleted = 1 where nc = " + recId;
            log.debug("delete query: " + query);

            PreparedStatement delPs = con.prepareStatement(query);
            delPs.executeUpdate();
            delPs.close();
        }
        finally {
            con.close();
        }
    }

    @Override
    protected void removeFromIndex(Urn recordUrn, AccessPoint accessPoint) throws SQLException, IOException, DocumentException {
        Connection con = databaseAccess.openDbConnection();
        if(con == null) {
            return;
        }

        try {
            String delim = ((recordUrn.getRecordId() instanceof Integer || recordUrn.getRecordId() instanceof Long) ? ""
                    : "'");
            String recId = delim + recordUrn.getRecordId() + delim;
            String table = accessPoint.getId().toLowerCase();

            String query = "delete from " + table + " where nc = " + recId;
            log.debug("remove query: " + query);

            PreparedStatement delPs = con.prepareStatement(query);
            delPs.executeUpdate();
            delPs.close();
        }
        finally {
            con.close();
        }
    }

    @Override
    public int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException {
        int[] countLastrowPair = {0, 0 };

        String recordTable = dataSource.getId() + "_record";
        String timestampTable = dataSource.getId() + "_timestamp";
        Connection con = null;

        try {
            con = databaseAccess.openDbConnection();

            TimeUtil.startTimers();
            TimeUtil.getTimeSinceLastTimerArray(1);

            String maxRowQuery = "select max(" + recordTable.toLowerCase() + ".id) from " + recordTable.toLowerCase();
            String countQuery = "select count(" + recordTable.toLowerCase() + ".nc) from " + recordTable.toLowerCase();

            String fromQueryHelper = "";
            if(fromDate != null || toDate != null) {
                fromQueryHelper += ", " + timestampTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = "
                        + timestampTable.toLowerCase() + ".nc";
                if(fromDate != null) {
                    fromQueryHelper += " and " + timestampTable.toLowerCase() + ".value >= '" + fromDate + "'";
                }
                if(toDate != null) {
                    fromQueryHelper += " and " + timestampTable.toLowerCase() + ".value <= '" + toDate + "'";
                }
            }

            maxRowQuery += fromQueryHelper;

//			countLastrowPair[1] = SqlUtil.getCount(maxQuery, con);
            Statement statementMaxRow = con.createStatement();
            ResultSet rsMaxRow = statementMaxRow.executeQuery(maxRowQuery);
            if(rsMaxRow.next()) {
                countLastrowPair[1] = rsMaxRow.getInt(1);
            }
            log.debug(maxRowQuery + ": " + TimeUtil.getTimeSinceLastTimerArray(1) + "ms");

            if(fromRow == null || countLastrowPair[1] > fromRow) {
                countQuery += fromQueryHelper;
                countQuery += (fromQueryHelper.length() > 0 ? " and " : " where ")
                        + (fromRow != null ? recordTable.toLowerCase() + ".id > " + fromRow + " and " : "")
                        + recordTable.toLowerCase() + ".id <= " + countLastrowPair[1];

                Statement statementCount = con.createStatement();
                ResultSet rsCount = statementCount.executeQuery(countQuery);
                if(rsCount.next()) {
                    countLastrowPair[0] = rsCount.getInt(1);
                }
                log.info(countQuery + ": " + TimeUtil.getTimeSinceLastTimerArray(1) + "ms");
            }

            return countLastrowPair;
        }
        catch (Exception e) {
            if(con != null && !con.isClosed()) {
                con.close();
            }

            log.error("ERROR Counting records", e);

            return new int[]{0, 0};
        }
        finally {
            if(con != null && !con.isClosed()) {
                con.close();
            }
        }
    }

    @Override
    public Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset,
                                           Integer numberResults) throws SQLException {
        return getFieldFromDataSource(dataSource, fromDate, toDate, offset, numberResults, "nc", String.class);
    }

    @Override
    public OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException {
        DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(urnOfRecord.getDataSourceId());

        String recordTable = dataSource.getId() + "_record";
        String timestampTable = dataSource.getId() + "_timestamp";

        Connection con = databaseAccess.openDbConnection();

        try {
            Statement stmt = con.createStatement();
            String query = "select " + recordTable.toLowerCase() + ".nc, "
                    + timestampTable.toLowerCase() + ".deleted" + ", "
                    + timestampTable.toLowerCase() + ".value" + ", "
                    + recordTable.toLowerCase() + ".value";

            query += " from " + recordTable.toLowerCase() + ", " + timestampTable.toLowerCase() + " where "
                    + recordTable.toLowerCase() + ".nc = '" + urnOfRecord.getRecordId() + "'"
                    + " and " + recordTable.toLowerCase() + ".nc = " + timestampTable.toLowerCase() + ".nc";

            log.info(query);

            ResultSet rs = stmt.executeQuery(query);

            OaiItem item = null;
            if (rs.next()) {
                byte[] recordBytes = (rs.getBytes(4) != null ? ZipUtil.unzip(rs.getBytes(4)) : null);
                boolean recordDeleted = (rs.getInt(2) == 1);
                OaiListResponse oaiListResponse = new OaiListResponse();
                item = oaiListResponse.new OaiItem(rs.getObject(1).toString(),
                        DateFormatUtils.format(rs.getDate(3), TimeUtil.SHORT_DATE_FORMAT),
                        dataSource.getId(),
                        recordDeleted,
                        recordBytes);
            }

            return item;
        }
        finally {
            con.close();
        }
    }

    @Override
    public String getRecordTimestamp(Urn urnOfRecord) throws IOException, DocumentException, SQLException {
        return getField(urnOfRecord, "_timestamp", Date.class);
    }

    @Override
    public OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate,
                                                       Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException {
        if(headersOnly) {
            return getHeaderAndRecordFromDataSource(dataSource, fromDate, toDate, offset, numberResults, false, String.class);
        } else {
            return getHeaderAndRecordFromDataSource(dataSource, fromDate, toDate, offset, numberResults, true, byte[].class);

        }
    }
}
