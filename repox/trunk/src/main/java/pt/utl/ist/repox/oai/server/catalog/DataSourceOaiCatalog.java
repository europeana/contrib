
/**
 *Copyright (c) 2000-2002 OCLC Online Computer Library Center,
 *Inc. and other contributors. All rights reserved.  The contents of this file, as updated
 *from time to time by the OCLC Office of Research, are subject to OCLC Research
 *Public License Version 2.0 (the "License"); you may not use this file except in
 *compliance with the License. You may obtain a current copy of the License at
 *http://purl.oclc.org/oclc/research/ORPL/.  Software distributed under the License is
 *distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 *or implied. See the License for the specific language governing rights and limitations
 *under the License.  This software consists of voluntary contributions made by many
 *individuals on behalf of OCLC Research. For more information on OCLC Research,
 *please see http://www.oclc.org/oclc/research/.
 *
 *The Original Code is DummyOAICatalog.java.
 *The Initial Developer of the Original Code is Jeff Young.
 *Portions created by Diogo Mena Reis
 */


package pt.utl.ist.repox.oai.server.catalog;

import eu.europeana.repox2sip.Repox2SipException;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.*;
import org.oclc.oai.util.OAIUtil;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;

public class DataSourceOaiCatalog extends AbstractCatalog {
    private static final Logger log = Logger.getLogger(DataSourceOaiCatalog.class);
    /**
     * maximum number of entries to return for ListRecords and ListIdentifiers
     */
    private static int maxListSize;

    /**
     * pending resumption tokens
     */
    private HashMap resumptionResults = new HashMap();

    /**
     * Construct a DummyOAICatalog object
     *
     * @param properties a properties object containing initialization parameters
     */
    public DataSourceOaiCatalog(Properties properties) {
        String maxListSize = properties.getProperty("DataSourceOAICatalog.maxListSize");
        if (maxListSize == null) {
            throw new IllegalArgumentException("DataSourceOAICatalog.maxListSize is missing from the properties file");
        } else {
            DataSourceOaiCatalog.maxListSize = Integer.parseInt(maxListSize);
        }
    }

    @Override
    public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
        return null;
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * identifier.
     *
     * @param identifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception IdDoesNotExistException the specified identifier can't be found
     * @exception NoMetadataFormatsException the specified identifier was found
     * but the item is flagged as deleted and thus no schemaLocations (i.e.
     * metadataFormats) can be produced.
     */
    public Map<String, String> getRepoxSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException {
        Map<String, String> schemaLocationsMap = new HashMap<String, String>();

        try {
            if(identifier == null || identifier.isEmpty()) {
                List<DataSource> dataSources = RepoxContextUtil.getRepoxManager().getDataManager().loadDataSources();
                for (DataSource dataSource : dataSources) {
                    addDataSourceSchemaLocations(schemaLocationsMap, dataSource);
                }
            }
            else {
                Urn urn = new Urn(identifier);
                if(urn == null
                        || urn.getRecordId() == null
                        || RepoxContextUtil.getRepoxManager().getAccessPointsManager().getRecord(urn) == null) {
                    throw new IdDoesNotExistException("Record with identifier: " + identifier + " does not exist.");
                }

                DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(urn.getDataSourceId());
                addDataSourceSchemaLocations(schemaLocationsMap, dataSource);
            }
        }
        catch (Exception e) {
            log.error("Error retrieving schema locations for identifier: " + identifier, e);
            return null;
        }

        return schemaLocationsMap;
    }

    private void addDataSourceSchemaLocations(Map<String, String> schemaLocationsMap, DataSource dataSource) {
        if(dataSource.getMetadataFormat().equals("ISO2709")) {
            schemaLocationsMap.put("MarcXchange", getSchemaLocation("MarcXchange"));
        }
        else {
            schemaLocationsMap.put(dataSource.getMetadataFormat(), getSchemaLocation(dataSource.getMetadataFormat()));
        }

        for (String currentDestinationFormat : dataSource.getMetadataTransformations().keySet()) {
            schemaLocationsMap.put(currentDestinationFormat, getSchemaLocation(currentDestinationFormat));
        }
    }

    private String getSchemaLocation(String metadataPrefix) {
        String schemaURL = null;

        if (metadataPrefix != null) {
            if(metadataPrefix.equals("MarcXchange")) {
                schemaURL = "info:lc/xmlns/marcxchange-v1 info:lc/xmlns/marcxchange-v1.xsd";
            }
            else if(metadataPrefix.equals("oai_dc") || metadataPrefix.equals("tel")) {
                schemaURL = "http://krait.kb.nl/coop/tel/handbook/telterms.html";
            }
            else {
                schemaURL = "";
            }
        }
        return schemaURL;
    }

    /**
     * Retrieve a list of identifiers that satisfy the specified criteria
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * It may seem strange for the map to include both "headers" and "identifiers"
     * since the identifiers can be obtained from the headers. This may be true, but
     * AbstractCatalog.listRecords() can operate quicker if it doesn't
     * need to parse identifiers from the XML headers itself. Better
     * still, do like I do below and override AbstractCatalog.listRecords().
     * AbstractCatalog.listRecords() is relatively inefficient because given the list
     * of identifiers, it must call getRecord() individually for each as it constructs
     * its response. It's much more efficient to construct the entire response in one fell
     * swoop by overriding listRecords() as I've done here.
     * @throws BadArgumentException
     */
    @Override
    public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        try {
            return getListRecords(from, until, set, metadataPrefix, 0, false);
        } catch (CannotDisseminateFormatException e) { return null;}
    }

    /**
     * Retrieve the next set of identifiers associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listIdentifiers() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @Override
    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        try {
            return getListRecords(resumptionToken, false);
        } catch (BadArgumentException e) {
            return null;
        }
        catch (CannotDisseminateFormatException e) {
            return null;
        } catch (RuntimeException e) {
            log.error("Error retrieving record ids with resumptionToken: " + resumptionToken, e);
            throw new BadResumptionTokenException();
        }
    }


    /**
     * Retrieve a list of records that satisfy the specified criteria. Note, though,
     * that unlike the other OAI verb type methods implemented here, both of the
     * listRecords methods are already implemented in AbstractCatalog rather than
     * abstracted. This is because it is possible to implement ListRecords as a
     * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
     * I suggest that you override both the AbstractCatalog.listRecords methods
     * here since it will probably improve the performance if you create the
     * response in one fell swoop rather than construct it one GetRecord at a time.
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for a "records" Iterator object
     * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
     * @exception CannotDisseminateFormatException the metadataPrefix isn't
     * supported by the item.
     * @throws BadArgumentException
     */
    @Override
    public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, BadArgumentException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        return getListRecords(from, until, set, metadataPrefix, 0, true);
    }

    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * @exception BadResumptionTokenException the value of the resumptionToken argument
     * is invalid or expired.
     * @throws CannotDisseminateFormatException
     */
    @Override
    public Map listRecords(String resumptionToken) throws BadResumptionTokenException, NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        try {
            return getListRecords(resumptionToken, true);
        } catch (BadArgumentException e) {
            return null;
        } catch (CannotDisseminateFormatException e) {
            return null;
        } catch (RuntimeException e) {
            log.error("Error retrieving records with resumptionToken: " + resumptionToken, e);
            throw new BadResumptionTokenException();
        }
    }

    private Map getListRecords(String resumptionToken, boolean fullRecord) throws CannotDisseminateFormatException, BadArgumentException, NoItemsMatchException {
        log.debug("resumptionToken: " + resumptionToken);

        // parse resumptionToken
        String[] resumptionParameters = resumptionToken.split(":");

        if(resumptionParameters.length < 4 || resumptionParameters.length > 6) {
            throw new IllegalArgumentException("Invalid resumptionToken");
        }

        String resumptionId = resumptionParameters[0];
        log.debug("resumptionId: " + resumptionId);
        String set = resumptionParameters[1];
        log.debug("set: " + set);
        String metadataPrefix = resumptionParameters[2];
        log.debug("metadataPrefix: " + metadataPrefix);
        int offset = Integer.parseInt(resumptionParameters[3]);
        log.debug("offset: " + offset);
        String from= null;
        String until= null;
        if(resumptionParameters.length >= 5) {
            String fromToken = resumptionParameters[4];
            log.debug("fromToken: " + fromToken);
            from = (fromToken.isEmpty() ? null : fromToken);
        }
        if(resumptionParameters.length >= 6) {
            String untilToken = resumptionParameters[5];
            log.debug("untilToken: " + untilToken);
            until = (untilToken.isEmpty() ? null : untilToken);
        }

        return getListRecords(from, until, set, metadataPrefix, offset, fullRecord);
    }

    private Map getListRecords(String from, String until, String set, String metadataPrefix, int offset, boolean fullRecord)
            throws CannotDisseminateFormatException, BadArgumentException, NoItemsMatchException {
        DataSource dataSource = null;

        try {
            dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(set);
            if(dataSource == null) {
                throw new BadArgumentException();
            }

            if(!isMetadataPrefixValid(metadataPrefix, dataSource)) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            }
        } catch (DocumentException e) {
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }

        from = (from == null || from.startsWith("0001-01-01") ? null : from.substring(0, 10));
        until = (until == null || until.startsWith("9999-12-31") ? null : until.substring(0, 10));

        Map listObjectsMap = new HashMap();
        List<String> headers = new ArrayList<String>();
        List<String> items = new ArrayList<String>();

        int totalSetRecords;
        OaiListResponse oaiListResponse;
        try {
            AccessPointsManager accessPointsManager = RepoxContextUtil.getRepoxManager().getAccessPointsManager();
            totalSetRecords = RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId()).getCount();
            log.debug("Total number of Records : " + totalSetRecords);

            //NOTE: we get 1 more record than maxlist for the resumptionToken and to avoid a last request with no results
            oaiListResponse = accessPointsManager.getOaiRecordsFromDataSource(dataSource, from, until, offset, maxListSize + 1, !fullRecord);

        }
        catch(Exception e) {
            log.error("Error getting Records", e);
            return null;
        }
        int counter = 0;

        for (OaiItem currentItem : oaiListResponse.getOaiItems()) { // load the headers and identifiers ArrayLists.
            try {
                if(counter >= maxListSize) {
                    break;
                }
                counter++;
                String identifier = new Urn(currentItem.getSetSpec(), currentItem.getIdentifier()).toString();
                String encodedIdentifier = OAIUtil.xmlEncode(identifier);
                String oaiRecordHeader = "<header";
                if(currentItem.isDeleted()) {
                    oaiRecordHeader += " status=\"deleted\"";
                }
                oaiRecordHeader += "><identifier>" + encodedIdentifier + "</identifier>" + "<datestamp>"
                        + currentItem.getDatestamp() + "</datestamp>" + "<setSpec>" + currentItem.getSetSpec()
                        + "</setSpec></header>";
                String xmlRecordString = (currentItem.getMetadata() != null ? new String(currentItem.getMetadata(),
                        "UTF-8") : "");
                if(!currentItem.isDeleted()) {
                    xmlRecordString = getTransformedRecord(encodedIdentifier, metadataPrefix, dataSource,
                            xmlRecordString);
                    if(xmlRecordString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                        xmlRecordString = xmlRecordString.substring(new String(
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>").length());
                    }
                }
                String oaiMetadata = (currentItem.isDeleted() ? "" : "<metadata>" + xmlRecordString + "</metadata>");
                String oaiProvenance = "";
                if(dataSource instanceof DataSourceOai) {
                    oaiProvenance = getOaiProvenance(dataSource, currentItem, xmlRecordString);
                }
                String oaiRecord = "<record>" + oaiRecordHeader + oaiMetadata + oaiProvenance + "</record>";
                if(fullRecord) {
                    try {
                        String record = DocumentHelper.parseText(oaiRecord).getRootElement().asXML();
                        items.add(record);
                    }
                    catch (Exception e) {
                        log.error("Error parsing Record", e);
                    }
                } else {
                    headers.add(oaiRecordHeader);
                    items.add(encodedIdentifier);
                }
            }
            catch (Exception e) {
                log.error("Error getting Record in the correct format -" +
                        " Set: " + currentItem.getSetSpec() + " ID: " + currentItem.getIdentifier(), e);
            }
        }
        if (counter == 0)
            throw new NoItemsMatchException();


        log.debug("offset + recordObjects.size(): " + (offset + oaiListResponse.getOaiItems().size()));
        log.debug("numberRecords: " + oaiListResponse.getOaiItems().size());

        // we got one more record than required in the request, if it exists, a resumptionToken is required
        if (oaiListResponse.getOaiItems().size() > maxListSize) {
            String resumptionToken = getResumptionToken(set, metadataPrefix, oaiListResponse.getLastRequestedIdentifier(), from, until);
            listObjectsMap.put("resumptionMap", getResumptionMap(resumptionToken, totalSetRecords, offset));
        }

        if(fullRecord) {
            listObjectsMap.put("records", items.iterator());
        }
        else {
            listObjectsMap.put("headers", headers.iterator());
            listObjectsMap.put("identifiers", items.iterator());
        }
        return listObjectsMap;
    }


    private String getOaiProvenance(DataSource dataSource, OaiItem oaiItem, String xmlRecordString) {
        String oaiProvenance;
        String metadataNamespace = "";

        try {
            Namespace namespace = DocumentHelper.parseText(xmlRecordString).getRootElement().getNamespace();
            if(!namespace.equals(Namespace.NO_NAMESPACE)) {
                metadataNamespace = namespace.getURI();
            }
        }
        catch (Exception e) {
        }

        DataSourceOai dataSourceOai = (DataSourceOai) dataSource;

        String originDescription =  "<originDescription harvestDate=\"" + oaiItem.getDatestamp() + "\" altered=\"true\">"
                + "<baseURL>" + dataSourceOai.getOaiSourceURL() + "</baseURL>"
                + "<identifier>" + oaiItem.getIdentifier() + "</identifier>"
                + "<datestamp>" + oaiItem.getDatestamp() + "</datestamp>"
                + "<metadataNamespace>" + metadataNamespace + "</metadataNamespace>"
                + "</originDescription>";

        oaiProvenance = "<about><provenance xmlns=\"http://www.openarchives.org/OAI/2.0/provenance\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + "  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/provenance "
                + "http://www.openarchives.org/OAI/2.0/provenance.xsd\">"
                + originDescription
                +"</provenance></about>";
        return oaiProvenance;
    }

    /*****************************************************************
     * Construct the resumptionToken String however you see fit.
     * [id]:[set]:[metadataPrefix]:[offset]:[from]:[until]
     *****************************************************************/
    private String getResumptionToken(String set, String metadataPrefix, int offset, String from, String until) {
        StringBuffer resumptionTokenSb = new StringBuffer();
        resumptionTokenSb.append(getResumptionId());
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(set);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(metadataPrefix);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(offset);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(from == null ? "" : from);
        resumptionTokenSb.append(":");
        resumptionTokenSb.append(until == null ? "" : until);
        return resumptionTokenSb.toString();
    }


    /**
     * Retrieve the specified metadata for the specified identifier
     *
     * @param identifier the OAI identifier
     * @param metadataPrefix the OAI metadataPrefix
     * @return the <record/> portion of the XML response.
     * @exception CannotDisseminateFormatException the metadataPrefix is not
     * supported by the item.
     * @exception IdDoesNotExistException the identifier wasn't found
     */
    @Override
    public String getRecord(String identifier, String metadataPrefix) throws CannotDisseminateFormatException, IdDoesNotExistException {
        Urn urn = null;
        DataSource dataSource = null;

        try {
            urn = new Urn(identifier);

            if(urn == null
                    || urn.getRecordId() == null
                    || RepoxContextUtil.getRepoxManager().getAccessPointsManager().getRecord(urn) == null) {
                throw new IdDoesNotExistException("Record with identifier: " + identifier + " does not exist.");
            }
            dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(urn.getDataSourceId());

            if(!isMetadataPrefixValid(metadataPrefix, dataSource)) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            }
        } catch(CannotDisseminateFormatException e) {
            log.error(e.getMessage(),e);
            throw new CannotDisseminateFormatException(metadataPrefix);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }

        try {
            OaiItem oaiItem = RepoxContextUtil.getRepoxManager().getAccessPointsManager().getRecord(urn);
            Element rootElement = XmlUtil.getRootElement(oaiItem.getMetadata());
            RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(rootElement, urn.getRecordId().toString(), false, oaiItem.isDeleted());

            String oaiRecordHeader = "<header";

            if(recordRepox.isDeleted()) {
                oaiRecordHeader += " status=\"deleted\"";
            }

            String encodedIdentifier = OAIUtil.xmlEncode(identifier);
            oaiRecordHeader = oaiRecordHeader + "><identifier>" + encodedIdentifier + "</identifier>"
                    + "<datestamp>" + oaiItem.getDatestamp() + "</datestamp>"
                    + "<setSpec>" + urn.getDataSourceId() + "</setSpec></header>";

            String xmlRecordString = "";

            if(!recordRepox.isDeleted()) {
                xmlRecordString = getTransformedRecord(encodedIdentifier, metadataPrefix, dataSource, recordRepox.getDom().asXML());
                if(xmlRecordString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                    xmlRecordString = xmlRecordString.substring(new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").length());
                }
            }

            String oaiMetadata = (recordRepox.isDeleted() ? "" : "<metadata>" + xmlRecordString + "</metadata>");

            String oaiProvenance = "";
            if(dataSource instanceof DataSourceOai) {
                oaiProvenance = getOaiProvenance(dataSource, oaiItem, xmlRecordString);
            }

            String oaiRecord = "<record>" + oaiRecordHeader + oaiMetadata + oaiProvenance + "</record>";

            String record = DocumentHelper.parseText(oaiRecord).getRootElement().asXML();
            return record;
        }
        catch(Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    private String getTransformedRecord(String encodedIdentifier, String metadataPrefix, DataSource dataSource, String xmlRecordString)
            throws DocumentException, TransformerException {
        if(metadataPrefix.equals("MarcXchange") && dataSource.getMetadataFormat().equals("ISO2709")) {
            return xmlRecordString;
        }
        else if(!dataSource.getMetadataFormat().equals(metadataPrefix) && !xmlRecordString.isEmpty()) {
            MetadataTransformation transformation = dataSource.getMetadataTransformations().get(metadataPrefix);
            xmlRecordString = transformation.transform(encodedIdentifier, xmlRecordString);
        }

        return xmlRecordString;
    }

    private boolean isMetadataPrefixValid(String metadataPrefix, DataSource dataSource) {
        boolean isValid = true;

        if(metadataPrefix.equals("MarcXchange") && dataSource.getMetadataFormat().equals("ISO2709")) {
            isValid = true;
        }
        else if(dataSource == null ||
                (!dataSource.getMetadataFormat().equals(metadataPrefix)
                        && dataSource.getMetadataTransformations().get(metadataPrefix) == null)) {
            isValid = false;
        }

        return isValid;
    }


    /**
     * Retrieve a list of sets that satisfy the specified criteria
     *
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception OAIBadRequestException signals an http status code 400 problem
     */
    @Override
    public Map listSets() {
        purge(); // clean out old resumptionTokens

        Map listSetsMap = new HashMap();
        List sets = new ArrayList();

        try {
            List<DataSource> dataSources = RepoxContextUtil.getRepoxManager().getDataManager().loadDataSources();

            for (DataSource dataSource : dataSources) {
                sets.add("<set><setSpec>" + dataSource.getId() + "</setSpec><setName>"
                        + dataSource.getDescription() + "</setName></set>");
            }
        }
        catch(Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }

        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * Retrieve the next set of sets associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listSets() Map result.
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * is invalid or expired.
     */
    @Override
    public Map listSets(String resumptionToken)
            throws BadResumptionTokenException {
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();
        purge(); // clean out old resumptionTokens

        /**********************************************************************
         * YOUR CODE GOES HERE
         **********************************************************************/
        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }

        /* Get some more sets */
        String[] dbSets = (String[])resumptionResults.remove(resumptionId);
        if (dbSets == null) {
            throw new BadResumptionTokenException();
        }
        int count;

        /* load the sets ArrayList */
        for (count = 0; count < maxListSize && count+oldCount < dbSets.length; ++count) {
            sets.add(dbSets[count+oldCount]);
        }

        /* decide if we're done */
        if (count+oldCount < dbSets.length) {
            resumptionId = getResumptionId();

            /*****************************************************************
             * Store an object appropriate for your database API in the
             * resumptionResults Map in place of nativeItems. This object
             * should probably encapsulate the information necessary to
             * perform the next resumption of ListIdentifiers. It might even
             * be possible to encode everything you need in the
             * resumptionToken, in which case you won't need the
             * resumptionResults Map. Here, I've done a silly combination
             * of the two. Stateless resumptionTokens have some advantages.
             *****************************************************************/
            resumptionResults.put(resumptionId, dbSets);

            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(oldCount + count));

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            listSetsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
                    dbSets.length,
                    oldCount));
            //          listSetsMap.put("resumptionMap",
            //                                 getResumptionMap(resumptionTokenSb.toString()));
        }
        /***********************************************************************
         * END OF CUSTOM CODE SECTION
         ***********************************************************************/
        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * close the repository
     */
    @Override
    public void close() { }

    /**
     * Purge tokens that are older than the configured time-to-live.
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date now = new Date();
        for (Object o : resumptionResults.keySet()) {
            String key = (String) o;
            Date then = new Date(Long.parseLong(key) + getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        for (Object anOld : old) {
            String key = (String) anOld;
            resumptionResults.remove(key);
        }
    }

    /**
     * Use the current date as the basis for the resumptiontoken
     *
     * @return a String version of the current time
     */
    private synchronized static String getResumptionId() {
        Date now = new Date();
        return Long.toString(now.getTime());
    }
}
