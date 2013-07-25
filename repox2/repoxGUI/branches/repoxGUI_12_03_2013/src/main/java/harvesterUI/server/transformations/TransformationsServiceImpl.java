package harvesterUI.server.transformations;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.transformations.TransformationsService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.projects.Light.LightSaveData;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.MdrDataStatistics;
import harvesterUI.shared.mdr.*;
import harvesterUI.shared.servletResponseStates.ResponseState;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.util.ConfigSingleton;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class TransformationsServiceImpl extends RemoteServiceServlet implements TransformationsService {

    private TransformationMatcher transformationMatcher;

    public TransformationsServiceImpl() {
        transformationMatcher = new TransformationMatcher();
    }

    public List<TransformationUI> getFullTransformationsList() throws ServerSideException{
        List<TransformationUI> results = new ArrayList<TransformationUI>();
        transformationMatcher.processMatches();

        try {
            Map<String,List<MetadataTransformation>> transformations =
                    RepoxServiceImpl.getRepoxManager().getMetadataTransformationManager().getMetadataTransformations();
            Iterator iterator=transformations.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                for (MetadataTransformation metadataTransformation : (List<MetadataTransformation>)mapEntry.getValue()) {
                    MdrDataStatistics mdrDataStatistics = transformationMatcher.getTransformationStatisticsMap().get(metadataTransformation.getId());
                    TransformationUI transformationUI;
                    if(mdrDataStatistics != null){
                        transformationUI = new TransformationUI(metadataTransformation.getId(),
                                metadataTransformation.getDescription(),metadataTransformation.getSourceFormat(),
                                metadataTransformation.getDestinationFormat(),metadataTransformation.getSchema(),
                                metadataTransformation.getNamespace(),metadataTransformation.getStylesheet(),
                                metadataTransformation.isVersionTwo(), mdrDataStatistics);
                    }else{
                        transformationUI = new TransformationUI(metadataTransformation.getId(),
                                metadataTransformation.getDescription(),metadataTransformation.getSourceFormat(),
                                metadataTransformation.getDestinationFormat(),metadataTransformation.getSchema(),
                                metadataTransformation.getNamespace(),metadataTransformation.getStylesheet(),
                                metadataTransformation.isVersionTwo());
                    }
                    results.add(transformationUI);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return results;
    }

    public PagingLoadResult<TransformationUI> getPagedTransformations(PagingLoadConfig config) throws ServerSideException{
        List<TransformationUI> transformationUIList = getFullTransformationsList();

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(transformationUIList, config.getSortInfo().getSortDir().comparator(new Comparator<TransformationUI>() {
                    public int compare(TransformationUI p1, TransformationUI p2) {
                        if (sortField.equals("identifier")) {
                            return p1.getIdentifier().compareTo(p2.getIdentifier());
                        } else if (sortField.equals("srcFormat")) {
                            return p1.getSrcFormat().compareTo(p2.getSrcFormat());
                        } else if (sortField.equals("destFormat")) {
                            return p1.getDestFormat().compareTo(p2.getDestFormat());
                        } else if (sortField.equals("usage")) {
                            int used1 = p1.getMdrDataStatistics().getNumberTimesUsed();
                            int used2 = p2.getMdrDataStatistics().getNumberTimesUsed();

                            if(used1 > used2)
                                return 1;
                            else if(used1 < used2)
                                return -1;
                            else
                                return 0;
                        }
                        return 0;
                    }
                }));
            }
        }
        ArrayList<TransformationUI> sublist = new ArrayList<TransformationUI>();
        int start = config.getOffset();
        int limit = transformationUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(transformationUIList.get(i));
        }
        return new BasePagingLoadResult<TransformationUI>(sublist, config.getOffset(), transformationUIList.size());
    }

    // Schema mapper
    public String saveTransformation(TransformationUI transformationUI,String oldTransId) throws ServerSideException{
        try {
            String xslFilePath = transformationUI.getXslFilePath().toLowerCase();

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(new MetadataTransformation(transformationUI.getIdentifier(),
                            transformationUI.getDescription(),transformationUI.getSrcFormat(),
                            transformationUI.getDestFormat(),xslFilePath,
                            false,transformationUI.getIsXslVersion2(),transformationUI.getSchema(),transformationUI.getMetadataNamespace()),oldTransId);
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException{
        try {
            for(String transformationID : transfomationIDs){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                        deleteMetadataTransformation(transformationID);
            }
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<TransformationUI> getMdrMappings(String schema, String metadataNamespace) throws ServerSideException{
        return parseMdrMapping(schema, metadataNamespace);
    }

    private List<TransformationUI> parseMdrMapping(String schema, String metadataNamespace) throws ServerSideException{
        List<TransformationUI> transformationsList = new ArrayList<TransformationUI>();

        String schemaUrn = "";
        if(!schema.isEmpty() && !metadataNamespace.isEmpty()){
            schemaUrn = getSchemaUrn(schema, metadataNamespace);
            if(schemaUrn.isEmpty())
                return transformationsList;
        }

        try {
            SAXReader reader = new SAXReader();

            schemaUrn = forURL(schemaUrn);
            String statusUri = RepoxServiceImpl.getRepoxManager().getConfiguration().getMdrUrl() + "/services/search/listMappings?source="+schemaUrn+"&target=";

            Document document = reader.read(new URL(statusUri));

            document.getRootElement().addNamespace("mdrp","urn:mdr:system:/protocol#");
            document.getRootElement().addNamespace("mdr", "urn:mdr:system:/administration#");
            document.getRootElement().addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            document.getRootElement().addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

            List<Node> listResponse = document.getRootElement().selectNodes("mdrp:ListResponse");
            for(Node listResponseNode : listResponse){
                List<Node> recordList = listResponseNode.selectNodes("mdrp:record");
                for(Node recordNode : recordList){
                    Node mappingModelNode = recordNode.selectSingleNode("rdf:RDF/mdr:MappingModel");

                    String mappingUrn = mappingModelNode.valueOf("@id");
                    if(hasXSL(mappingUrn)){
                        String identifier = LightSaveData.encodeMDRId(mappingUrn);
                        String description = mappingModelNode.valueOf("mdr:AdministrationRecord/mdr:hasNaming/mdr:TerminologyEntry/mdr:hasSection/mdr:LanguageSection/mdr:shortDesignation");
                        String destFormat = getShortSchemaDescription(mappingModelNode.valueOf("mdr:target/@rdf:resource")).get("schemaShortDescription");
                        BaseModel srcData = getShortSchemaDescription(mappingModelNode.valueOf("mdr:source/@rdf:resource"));
                        String srcFormat = srcData.get("schemaShortDescription");
                        //
                        String mappingSchema = srcData.get("schemaXsd");
                        String mtdNamespace = srcData.get("schemaNamespace");
                        String xslFilePath = identifier + ".xsl";
                        // todo : get this version of xsl
                        boolean isXslVersion2 = true;

                        TransformationUI transformationUI = new TransformationUI(identifier,description,srcFormat,destFormat,mappingSchema,mtdNamespace,xslFilePath,isXslVersion2);
                        transformationUI.setMdr(true);
                        transformationUI.setDSStringFormat("MDR - " +transformationUI.getDSStringFormat());
                        transformationsList.add(transformationUI);
                    }
                }
            }
//            System.out.println("MAPPING URN = " + mappingUrn);

        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return transformationsList;
    }

    public static String forURL(String aURLFragment){
        String result = null;
        try {
            result = URLEncoder.encode(aURLFragment, "UTF-8");
        }
        catch (UnsupportedEncodingException ex){
            throw new RuntimeException("UTF-8 not supported", ex);
        }
        return result;
    }

    private String getSchemaUrn(String schema, String metadataNamespace) throws ServerSideException{
        // list schemas for namespace
        String schemaUrn = "";
        try {
            SAXReader reader = new SAXReader();

            String statusUri = RepoxServiceImpl.getRepoxManager().getConfiguration().getMdrUrl() + "/services/search/listSchemas?namespace=" + metadataNamespace + "&location=" + forURL(schema);

            Document document = reader.read(new URL(statusUri));

            document.getRootElement().addNamespace("mdrp","urn:mdr:system:/protocol#");
            document.getRootElement().addNamespace("mdr", "urn:mdr:system:/administration#");
            document.getRootElement().addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            document.getRootElement().addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

            List<Node> listResponse = document.getRootElement().selectNodes("mdrp:ListResponse");
            for(Node listResponseNode : listResponse){
                List<Node> recordList = listResponseNode.selectNodes("mdrp:record");
                for(Node recordNode : recordList){
                    schemaUrn = recordNode.valueOf("rdf:RDF/mdr:Schema/@id");
                }
            }
            System.out.println("SCHEMA URN = " + schemaUrn);
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return schemaUrn;
    }

    public List<TransformationUI> getAllMdrMappings() throws ServerSideException{
        return parseMdrMapping("","");
    }

    public BaseModel importMdrTransformation(List<TransformationUI> transformationUIs) throws ServerSideException{
        BaseModel baseModel = new BaseModel();

        try {
            int numberOfAlreadyExists = 0;
            int numberOfAdded = 0;
            int errorImports = 0;
            List<TransformationUI> alreadyExistingTransformations = new ArrayList<TransformationUI>();

            for(TransformationUI transformationUI : transformationUIs){
                MetadataTransformationManager metadataTransformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
                String id = transformationUI.getIdentifier();
                MetadataTransformation metadataTransformation = metadataTransformationManager.loadMetadataTransformation(id);

                if(metadataTransformation != null){
                    numberOfAlreadyExists++;
                    alreadyExistingTransformations.add(transformationUI);
                    continue;
                }

                MetadataTransformation metadataTransformation1 = LightSaveData.saveMdrTransformation(transformationUI);
                if(metadataTransformation1 == null)
                    errorImports++;
                else
                    numberOfAdded++;
            }
            baseModel.set("numberOfAlreadyExists",numberOfAlreadyExists);
            baseModel.set("numberOfAdded",numberOfAdded);
            baseModel.set("errorImports",errorImports);
            baseModel.set("alreadyExistingTransformations",alreadyExistingTransformations);
            return baseModel;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public BaseModel updateMdrTransformations(List<TransformationUI> transformationUIs) throws ServerSideException{
        BaseModel baseModel = new BaseModel();
        try {
            int numberOfAdded = 0;
            int errorImports = 0;

            for(TransformationUI transformationUI : transformationUIs){
                MetadataTransformationManager metadataTransformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
                String id = transformationUI.getIdentifier();
                MetadataTransformation metadataTransformation = metadataTransformationManager.loadMetadataTransformation(id);

                if(metadataTransformation != null){
                    metadataTransformationManager.deleteMetadataTransformation(metadataTransformation.getId());
                }

                MetadataTransformation metadataTransformation1 = LightSaveData.saveMdrTransformation(transformationUI);
                if(metadataTransformation1 == null)
                    errorImports++;
                else
                    numberOfAdded++;
            }

            baseModel.set("numberOfAdded",numberOfAdded);
            baseModel.set("errorImports",errorImports);
            return baseModel;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private BaseModel getShortSchemaDescription(String schemaUrl) throws ServerSideException{
        BaseModel baseModel = new BaseModel();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(schemaUrl));

            document.getRootElement().addNamespace("mdrp","urn:mdr:system:/protocol#");
            document.getRootElement().addNamespace("mdr", "urn:mdr:system:/administration#");
            document.getRootElement().addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            document.getRootElement().addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

            String schemaShortDescription = document.getRootElement().valueOf("mdr:Schema/mdr:AdministrationRecord/mdr:hasNaming" +
                    "/mdr:TerminologyEntry/mdr:hasSection/mdr:LanguageSection/mdr:shortDesignation");
            String schemaNamespace = document.getRootElement().valueOf("mdr:Schema/@namespace");
            String schemaXsd = document.getRootElement().valueOf("mdr:Schema/mdr:AdministrationRecord/rdfs:seeAlso/mdr:Schema/@location");

//            System.out.println("Schema short Description = " + schemaShortDescription);
            baseModel.set("schemaShortDescription",schemaShortDescription);
            baseModel.set("schemaNamespace",schemaNamespace);
            baseModel.set("schemaXsd",schemaXsd);
            return baseModel;
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return baseModel;
    }

    private boolean hasXSL(String urn) throws ServerSideException{
        try{
            String statusUri = RepoxServiceImpl.getRepoxManager().getConfiguration().getMdrUrl() +
                    "/services/provider/getTranslation?uri=" + TransformationsServiceImpl.forURL(urn) + "&mimetype="+TransformationsServiceImpl.forURL("application/xslt+xml");

            SAXReader reader = new SAXReader();
            reader.read(new URL(statusUri));
        } catch (MalformedURLException e) {
            return false;
        } catch (DocumentException e) {
            return false;
        }
        return true;
    }

    // METADATA SCHEMA PART

    public List<SchemaTreeUI> getSchemasTree() throws ServerSideException {
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(transformationMatcher);

        List<SchemaTreeUI> schemaTreeUIs = new ArrayList<SchemaTreeUI>();

        for(SchemaUI schemaUI : schemaUIList){
            schemaUI.createTreeChildren();
        }

        schemaTreeUIs.addAll(schemaUIList);

        return schemaTreeUIs;
    }

    public PagingLoadResult<SchemaUI> getPagedSchemas(PagingLoadConfig config) throws ServerSideException{
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(transformationMatcher);

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(schemaUIList, config.getSortInfo().getSortDir().comparator(new Comparator<SchemaUI>() {
                    public int compare(SchemaUI p1, SchemaUI p2) {
                        if (sortField.equals("shortDesignation")) {
                            return p1.getShortDesignation().compareTo(p2.getShortDesignation());
                        } else if (sortField.equals("designation")) {
                            return p1.getDesignation().compareTo(p2.getDesignation());
                        }
//                        else if (sortField.equals("xsdLink")) {
//                            return p1.getXsdLink().compareTo(p2.getXsdLink());
//                        }
                        else if (sortField.equals("namespace")) {
                            return p1.getNamespace().compareTo(p2.getNamespace());
                        }
                        return 0;
                    }
                }));
            }
        }
        ArrayList<SchemaUI> sublist = new ArrayList<SchemaUI>();
        int start = config.getOffset();
        int limit = schemaUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(schemaUIList.get(i));
        }
        return new BasePagingLoadResult<SchemaUI>(sublist, config.getOffset(), schemaUIList.size());
    }

    public List<SchemaUI> getAllMetadataSchemas() throws ServerSideException{
        return RepoxDataExchangeManager.convertRepoxSchemas(transformationMatcher);
    }

    public ResponseState saveMetadataSchema(SchemaUI schemaUI,String oldSchemaUIId) throws ServerSideException{
        try {
            List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
            for(SchemaVersionUI schemaVersionUI : schemaUI.getSchemaVersions()){
                metadataSchemaVersions.add(new MetadataSchemaVersion(schemaVersionUI.getVersion(),schemaVersionUI.getXsdLink()));
            }

            MessageType messageType = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    saveMetadataSchema(schemaUI.getDesignation(),schemaUI.getShortDesignation(),
                            schemaUI.getDescription(),schemaUI.getNamespace(),
                            schemaUI.getNotes(),oldSchemaUIId,metadataSchemaVersions);

            if(messageType == MessageType.ALREADY_EXISTS)
                return ResponseState.ALREADY_EXISTS;
            else
                return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState deleteMetadataSchema(List<String> schemaIds) throws ServerSideException{
        try {
            for(String schemaID : schemaIds){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                        deleteMetadataSchema(schemaID);
            }
            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public static void main(String[] args){
//        try {
//            TransformationsServiceImpl.testMDrServices();
//        } catch (ServerSideException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }
}
