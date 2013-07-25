package pt.utl.ist.repox.accessPoint.database;

import org.apache.log4j.Logger;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.util.sql.SqlUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class DatabaseAccessPostgresql implements DatabaseAccess {
	private static final Logger log = Logger.getLogger(DatabaseAccessPostgresql.class);

	protected RepoxConfiguration configuration;
	protected String dbUrl;
	protected Properties dbProps;

	public DatabaseAccessPostgresql(RepoxConfiguration configuration) {
		super();

		try {
			this.configuration = configuration;

			dbUrl = configuration.getDatabaseUrl();

			dbProps = new Properties();
			dbProps.setProperty("user", configuration.getDatabaseUser());
			dbProps.setProperty("password", configuration.getDatabasePassword());
			log.info("Database URL connection: " + dbUrl);

			Class.forName(configuration.getDatabaseDriverClassName()).newInstance();
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getVarType(Class classOfValue) {
		String valueType = "varchar(255)";

		if(classOfValue.equals(Date.class)) {
			valueType = "date";
		} else if(classOfValue.equals(Integer.class)) {
			valueType = "integer";
		} else if(classOfValue.equals(Long.class)) {
			valueType = "bigint";
		} else if(classOfValue.equals(byte[].class)) {
			valueType = "bytea";
		}

		return valueType;
	}

	public Connection openDbConnection() {
		try {
			return DriverManager.getConnection(dbUrl, dbProps);
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public void createTableIndexes(Connection con, String idType, String table, String valueType, boolean indexValue) {
		String createTableQuery = "CREATE SEQUENCE idseq_" + table + ";"
				+ "CREATE TABLE " + table + " (id integer NOT NULL PRIMARY KEY DEFAULT nextval('idseq_" +  table + "'), " + "nc "
		+ idType + " NOT NULL, " + "value " + valueType + ", deleted SMALLINT)";
		log.info(createTableQuery);
		SqlUtil.runUpdate(createTableQuery, con);

		String iSystemIndexQuery = "CREATE INDEX " + table + "_i_nc ON " + table + "(nc)";
		SqlUtil.runUpdate(iSystemIndexQuery, con);

		if(indexValue) {
			String valueIndexQuery = "CREATE INDEX " + table + "_i_val ON " + table + "(value)";
			SqlUtil.runUpdate(valueIndexQuery, con);
		}
	}

	public void deleteTable(Connection con, String table) throws SQLException {
		PreparedStatement tableStatement = con.prepareStatement("drop table " + table);
		PreparedStatement sequenceStatement = con.prepareStatement("drop sequence idseq_" + table);
		SqlUtil.runUpdate(tableStatement);
		SqlUtil.runUpdate(sequenceStatement);
	}


	public String renameTableString(String oldTableName, String newTableName) {
		return "ALTER TABLE " + oldTableName + " RENAME TO " + newTableName;
	}

	public String renameIndexString(String oldIndexName, String newIndexName) {
		return "ALTER INDEX " + oldIndexName + " RENAME TO " + newIndexName;
	}
}
