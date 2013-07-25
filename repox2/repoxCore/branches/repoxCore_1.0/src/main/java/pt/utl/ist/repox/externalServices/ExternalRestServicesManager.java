package pt.utl.ist.repox.externalServices;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 12-12-2011
 * Time: 14:18
 */
public class ExternalRestServicesManager {
    private static final Logger log = Logger.getLogger(ExternalRestServicesManager.class);

    // Map of sourceMetadataFormat to list of available ExternalRestServices for that source
    private List<ExternalRestService> externalRestServices;
    private File configurationFile;

    public ExternalRestServicesManager(File configurationFile) throws IOException, DocumentException {
        super();
        this.configurationFile = configurationFile;
        loadExternalRestServices();
    }

    public List<ExternalRestService> getExternalRestServices() throws IOException, DocumentException {
        return externalRestServices;
    }

    protected void setExternalRestServices(List<ExternalRestService> externalRestServices) {
        this.externalRestServices = externalRestServices;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }


    //	public File loadStylesheet(MetadataTransformation metadataTransformation) {
//		return new File(xsltDir, metadataTransformation.getStylesheet());
//	}
//    public Transformer loadStylesheet(MetadataTransformation metadataTransformation) throws TransformerException {
//        File xsltFile=new File(xsltDir, metadataTransformation.getStylesheet());
//        if(metadataTransformation.isVersionTwo()){
//            return xslt2StylesheetCache.createTransformer(xsltFile);
//        } else{
//            TransformerFactory tfactory = new org.apache.xalan.processor.TransformerFactoryImpl();
//            Source xsltSource = new StreamSource(xsltFile);
//            return tfactory.newTransformer(xsltSource);
//        }
//    }

//    public synchronized void saveMetadataTransformation(MetadataTransformation metadataTransformation) throws IOException, DocumentException {
//        if(loadMetadataTransformation(metadataTransformation.getId()) != null) {
//            deleteMetadataTransformation(metadataTransformation.getId());
//        }
//
////		metadataTransformations = loadExternalRestServices();
//        List<MetadataTransformation> transformationsList = metadataTransformations.get(metadataTransformation.getSourceFormat());
//        if(transformationsList == null) {
//            transformationsList = new ArrayList<MetadataTransformation>();
//        }
//        transformationsList.add(metadataTransformation);
//        metadataTransformations.put(metadataTransformation.getSourceFormat(), transformationsList);
//        saveMetadataTransformations();
//    }

//    public synchronized boolean deleteMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
////		metadataTransformations = loadExternalRestServices();
//
//        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeTransformationFromDataSource(metadataTransformationId);
//
//        for (String sourceFormat : metadataTransformations.keySet()) {
//            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
//
//            Iterator<MetadataTransformation> transformationIterator = transformationsList.iterator();
//            while (transformationIterator.hasNext()) {
//                MetadataTransformation metadataTransformation = (MetadataTransformation) transformationIterator.next();
//                if(metadataTransformation.getId().equals(metadataTransformationId)) {
//                    // Delete XSLT File
//                    String fileName = metadataTransformation.getStylesheet();
//                    String path = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath()
//                            + File.separator + "xslt" + File.separator + fileName;
//                    File xsltFile = new File(path);
//                    xsltFile.delete();
//
//                    transformationIterator.remove();
//                    metadataTransformations.put(sourceFormat, transformationsList);
//                    saveMetadataTransformations();
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

    public synchronized ExternalRestService loadExternalRestService(String externalServiceId) throws IOException, DocumentException {
//		Map<String, List<MetadataTransformation>> metadataTransformations = loadExternalRestServices();
        for (ExternalRestService externalRestService : externalRestServices) {
            if(externalRestService.getId().equals(externalServiceId)) {
                return externalRestService;
            }
        }

        return null;
    }

//    public synchronized void saveMetadataTransformations() throws IOException {
//        Document document = DocumentHelper.createDocument();
//
//        Element rootNode = document.addElement("metadataTransformations");
//
//        Set<String> metadataFormats = metadataTransformations.keySet();
//        for (String currentMetadataFormat : metadataFormats) {
//            for (MetadataTransformation metadataTransformation : metadataTransformations.get(currentMetadataFormat)) {
//                Element metadataTransformationElement = rootNode.addElement("metadataTransformation");
//                metadataTransformationElement.addElement("id").setText(metadataTransformation.getId());
//                metadataTransformationElement.addElement("description").setText(metadataTransformation.getDescription());
//                metadataTransformationElement.addElement("sourceFormat").setText(metadataTransformation.getSourceFormat());
//                metadataTransformationElement.addElement("destinationFormat").setText(metadataTransformation.getDestinationFormat());
//                metadataTransformationElement.addElement("stylesheet").setText(metadataTransformation.getStylesheet());
//                metadataTransformationElement.addAttribute("editable", String.valueOf(metadataTransformation.isEditable()));
//                metadataTransformationElement.addAttribute("version", metadataTransformation.isVersionTwo() ? "2.0" : "1.0");
//            }
//        }
//
//        XmlUtil.writePrettyPrint(configurationFile, document);
//    }

    public synchronized void loadExternalRestServices() {

        externalRestServices = new ArrayList<ExternalRestService>();
        if(!configurationFile.exists()) {
            return;
        }

        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            List<Element> externalServicesElements = document.getRootElement().elements();

            for (Element currentElement : externalServicesElements) {
                String id = currentElement.attributeValue("id");
                String name = currentElement.attributeValue("name");
                String uri = currentElement.attributeValue("uri");
                String statusUri = currentElement.attributeValue("statusUri");
                String type = currentElement.attributeValue("type");

                ExternalRestService externalRestService = new ExternalRestService(id, name, uri,statusUri, type);

                List list = currentElement.selectNodes("parameters/parameter");
                for(Object node: list) {
                    Node n = (Node) node;
                    String parameterName = n.valueOf("@name");
                    String parameterType = n.valueOf("@type");
//                    String value = n.valueOf("value");
                    boolean required = Boolean.parseBoolean(n.valueOf("@required"));
                    String exampleStr = n.valueOf("@example");
                    String semanticsStr = n.valueOf("@semantics");

                    ServiceParameter serviceParameter = new ServiceParameter(parameterName,parameterType,required,
                            exampleStr,semanticsStr);
                    if(parameterType.equals("COMBO_FIELD")){
                        List comboList = n.selectNodes("comboValues/comboValue");
                        for(Object comboNode: comboList) {
                            Node nodeC = (Node) comboNode;
                            String comboVal = nodeC.getText();
                            serviceParameter.getComboValues().add(comboVal);
                        }
                    }
                    externalRestService.getServiceParameters().add(serviceParameter);
                }

                externalRestServices.add(externalRestService);
            }
        } catch (DocumentException e) {
            log.error("Error loading the external services file (externalServices.xml).");
        }
    }

    public static void main(String[] args) throws IOException, DocumentException {
//        ExternalRestServicesManager manager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getExternalRestServicesManager();
//        Map<String, List<MetadataTransformation>> transformationsMap = manager.getMetadataTransformations();
//        List<MetadataTransformation> transformationsList = new ArrayList<MetadataTransformation>();
//        transformationsList.add(new MetadataTransformation("marc212tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "marc212tel.xsl", false, false, "info:lc/xmlns/marcxchange-v1.xsd", "info:lc/xmlns/marcxchange-v1"));
//        transformationsList.add(new MetadataTransformation("unimarc2tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
//        transformationsList.add(new MetadataTransformation("unimarc2tel-notbn", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel-notbn.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
//        transformationsMap.put(MetadataFormat.MarcXchange.toString(), transformationsList);
//        manager.setMetadataTransformations(transformationsMap);
//        manager.saveMetadataTransformations();
    }

}