package pt.utl.ist.repox.metadataTransformation;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.XmlUtil;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MetadataTransformationManager {
    private static final Logger log = Logger.getLogger(MetadataTransformationManager.class);

    // Map of sourceMetadataFormat to list of available MetadataTransformations for that source
    private Map<String, List<MetadataTransformation>> metadataTransformations;
    private File xsltDir;
    private File configurationFile;
    private Xslt2StylesheetCache xslt2StylesheetCache = new Xslt2StylesheetCache();

    public MetadataTransformationManager(File configurationFile, File xsltDir) throws IOException, DocumentException {
        super();
        this.configurationFile = configurationFile;
        this.xsltDir = xsltDir;
        loadMetadataTransformations();
    }

    public Map<String, List<MetadataTransformation>> getMetadataTransformations() throws IOException, DocumentException {
        return Collections.unmodifiableMap(metadataTransformations);
    }

    protected void setMetadataTransformations(Map<String, List<MetadataTransformation>> metadataTransformations) {
        this.metadataTransformations = metadataTransformations;
    }

    public File getXsltDir() {
        return xsltDir;
    }

    public void setXsltDir(File xsltDir) {
        this.xsltDir = xsltDir;
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
    public Transformer loadStylesheet(MetadataTransformation metadataTransformation) throws TransformerException {
        File xsltFile=new File(xsltDir, metadataTransformation.getStylesheet());
        if(metadataTransformation.isVersionTwo()){
            return xslt2StylesheetCache.createTransformer(xsltFile);
        } else{
            TransformerFactory tfactory = new org.apache.xalan.processor.TransformerFactoryImpl();
            Source xsltSource = new StreamSource(xsltFile);
            return tfactory.newTransformer(xsltSource);
        }
    }

    public synchronized void saveMetadataTransformation(MetadataTransformation metadataTransformation, String oldMtdTransId) throws IOException, DocumentException {
        MetadataTransformation savedMtdTrans;
        if(oldMtdTransId.equals(metadataTransformation.getId()))
            savedMtdTrans = loadMetadataTransformation(metadataTransformation.getId());
        else
            savedMtdTrans = loadMetadataTransformation(oldMtdTransId);

        if(savedMtdTrans == null){
            // New Transformation
            savedMtdTrans = metadataTransformation;
        }else{
            // Update Transformation
            savedMtdTrans.setId(metadataTransformation.getId());
            savedMtdTrans.setDescription(metadataTransformation.getDescription());
            savedMtdTrans.setDestinationFormat(metadataTransformation.getDestinationFormat());
            savedMtdTrans.setEditable(metadataTransformation.isEditable());
            savedMtdTrans.setVersionTwo(metadataTransformation.isVersionTwo());
            savedMtdTrans.setNamespace(metadataTransformation.getNamespace());
            savedMtdTrans.setSchema(metadataTransformation.getSchema());
            savedMtdTrans.setSourceFormat(metadataTransformation.getSourceFormat());
            savedMtdTrans.setStylesheet(metadataTransformation.getStylesheet());

            deleteMetadataTransformationFromHash(savedMtdTrans.getId());
        }

//        if(loadMetadataTransformation(metadataTransformation.getId()) != null) {
//            deleteMetadataTransformation(metadataTransformation.getId());
//        }

//		metadataTransformations = loadMetadataTransformations();
        List<MetadataTransformation> transformationsList = metadataTransformations.get(savedMtdTrans.getSourceFormat());
        if(transformationsList == null) {
            transformationsList = new ArrayList<MetadataTransformation>();
        }
        transformationsList.add(savedMtdTrans);
        metadataTransformations.put(savedMtdTrans.getSourceFormat(), transformationsList);
        saveMetadataTransformations();
    }

    private synchronized boolean deleteMetadataTransformationFromHash(String metadataTransformationId) throws IOException, DocumentException {
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeTransformationFromDataSource(metadataTransformationId);

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            Iterator<MetadataTransformation> transformationIterator = transformationsList.iterator();
            while (transformationIterator.hasNext()) {
                MetadataTransformation metadataTransformation = (MetadataTransformation) transformationIterator.next();
                if(metadataTransformation.getId().equals(metadataTransformationId)) {
                    transformationIterator.remove();
                    metadataTransformations.put(sourceFormat, transformationsList);
                    saveMetadataTransformations();
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized boolean deleteMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
//		metadataTransformations = loadMetadataTransformations();

        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeTransformationFromDataSource(metadataTransformationId);

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            Iterator<MetadataTransformation> transformationIterator = transformationsList.iterator();
            while (transformationIterator.hasNext()) {
                MetadataTransformation metadataTransformation = (MetadataTransformation) transformationIterator.next();
                if(metadataTransformation.getId().equals(metadataTransformationId)) {
                    // Delete XSLT File
                    String fileName = metadataTransformation.getStylesheet();
                    String path = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath()
                            + File.separator + "xslt" + File.separator + fileName;
                    File xsltFile = new File(path);
                    xsltFile.delete();

                    transformationIterator.remove();
                    metadataTransformations.put(sourceFormat, transformationsList);
                    saveMetadataTransformations();
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized MetadataTransformation loadMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
//		Map<String, List<MetadataTransformation>> metadataTransformations = loadMetadataTransformations();

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            for (MetadataTransformation metadataTransformation : transformationsList) {
                if(metadataTransformation.getId().equals(metadataTransformationId)) {
                    return metadataTransformation;
                }
            }
        }

        return null;
    }

    public synchronized void saveMetadataTransformations() throws IOException {
        Document document = DocumentHelper.createDocument();

        Element rootNode = document.addElement("metadataTransformations");

        Set<String> metadataFormats = metadataTransformations.keySet();
        for (String currentMetadataFormat : metadataFormats) {
            for (MetadataTransformation metadataTransformation : metadataTransformations.get(currentMetadataFormat)) {
                Element metadataTransformationElement = rootNode.addElement("metadataTransformation");
                metadataTransformationElement.addElement("id").setText(metadataTransformation.getId());
                metadataTransformationElement.addElement("description").setText(metadataTransformation.getDescription());
                metadataTransformationElement.addElement("sourceFormat").setText(metadataTransformation.getSourceFormat());
                metadataTransformationElement.addElement("destinationFormat").setText(metadataTransformation.getDestinationFormat());
                metadataTransformationElement.addElement("stylesheet").setText(metadataTransformation.getStylesheet());
                metadataTransformationElement.addElement("schema").setText(metadataTransformation.getSchema());
                metadataTransformationElement.addElement("namespace").setText(metadataTransformation.getNamespace());
                metadataTransformationElement.addAttribute("editable", String.valueOf(metadataTransformation.isEditable()));
                metadataTransformationElement.addAttribute("version", metadataTransformation.isVersionTwo() ? "2.0" : "1.0");
            }
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }

    protected synchronized void loadMetadataTransformations() {

        metadataTransformations = new HashMap<String, List<MetadataTransformation>>();
        if(!configurationFile.exists()) {
            return;
        }

        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            List<Element> metadataTransformationElements = document.getRootElement().elements();

            for (Element currentElement : metadataTransformationElements) {
                String id = currentElement.element("id").getText();
                String description = currentElement.elementText("description");
                String sourceFormat = currentElement.elementText("sourceFormat");
                String destinationFormat = currentElement.elementText("destinationFormat");
                String stylesheet = currentElement.elementText("stylesheet");
                String namespace = currentElement.elementText("namespace");
                String schema = currentElement.elementText("schema");

                if(namespace == null){
                    namespace = getNamespace(destinationFormat);
                }
                if(schema == null){
                    schema = getSchema(destinationFormat);
                }

                boolean editable = Boolean.parseBoolean(currentElement.attributeValue("editable"));
                boolean version2 = currentElement.attributeValue("version") != null && currentElement.attributeValue("version").equals("2.0");
                MetadataTransformation metadataTransformation = new MetadataTransformation(id, description, sourceFormat,
                        destinationFormat, stylesheet, editable, version2, schema, namespace);

                List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
                if(transformationsList == null) {
                    transformationsList = new ArrayList<MetadataTransformation>();
                }
                transformationsList.add(metadataTransformation);
                Collections.sort(transformationsList, getTransformationComparator());

                metadataTransformations.put(sourceFormat, transformationsList);
            }
        } catch (DocumentException e) {
            log.error("Error loading the metadata transformations file (metadataTransformations.xml).");
        }
    }

    public File getXsltFile(String transformationId) throws IOException, DocumentException {
        File xsltDir = getXsltDir();
        MetadataTransformation metadataTransformation = loadMetadataTransformation(transformationId);
        String xsltFilename = ((metadataTransformation != null && metadataTransformation.getStylesheet() != null)
                ? metadataTransformation.getStylesheet()
                : transformationId + ".xsl");
        return new File(xsltDir, xsltFilename);
    }

    public TransformationComparator getTransformationComparator() {
        return new TransformationComparator();
    }


    public static String getSchema(String metadataFormat){
        if(metadataFormat.equalsIgnoreCase("ese")){
            return "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("MarcXchange")|| (metadataFormat.equalsIgnoreCase("ISO2709"))){
            return "info:lc/xmlns/marcxchange-v1.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("tel")){
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        }
        else if(metadataFormat.equalsIgnoreCase("oai_dc")){
            return "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("NLM-AI") ||
                metadataFormat.equalsIgnoreCase("NLM-Book")){
            return "ncbi-mathml2/mathml2.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("lido")){
            return "http://www.lido-schema.org/schema/v1.0/lido-v1.0.xsd";
        }
        else if(metadataFormat.equalsIgnoreCase("edm")){
            return "http://www.europeana.eu/schemas/edm/EDM.xsd";
        }
        return "";
    }

    public static String getNamespace(String metadataFormat){
        if(metadataFormat.equalsIgnoreCase("ese")){
            return "http://www.europeana.eu/schemas/ese/";
        }
        else if(metadataFormat.equalsIgnoreCase("MarcXchange")|| (metadataFormat.equalsIgnoreCase("ISO2709"))){
            return "info:lc/xmlns/marcxchange-v1";
        }
        else if(metadataFormat.equalsIgnoreCase("tel")){
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        }
        else if(metadataFormat.equalsIgnoreCase("oai_dc")){
            return "http://www.openarchives.org/OAI/2.0/";
        }
        else if(metadataFormat.equalsIgnoreCase("NLM-AI") ||
                metadataFormat.equalsIgnoreCase("NLM-Book")){
            return "http://www.w3.org/1998/Math/MathML";
        }
        else if(metadataFormat.equalsIgnoreCase("lido")){
            return "http://www.lido-schema.org";
        }
        else if(metadataFormat.equalsIgnoreCase("edm")){
            return "http://www.europeana.eu/schemas/edm/";
        }
        return "";
    }


    public static void main(String[] args) throws IOException, DocumentException {
        MetadataTransformationManager manager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
        Map<String, List<MetadataTransformation>> transformationsMap = manager.getMetadataTransformations();
        List<MetadataTransformation> transformationsList = new ArrayList<MetadataTransformation>();
        transformationsList.add(new MetadataTransformation("marc212tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "marc212tel.xsl", false, false, "info:lc/xmlns/marcxchange-v1.xsd", "info:lc/xmlns/marcxchange-v1"));
        transformationsList.add(new MetadataTransformation("unimarc2tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
        transformationsList.add(new MetadataTransformation("unimarc2tel-notbn", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel-notbn.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
        transformationsMap.put(MetadataFormat.MarcXchange.toString(), transformationsList);
        manager.setMetadataTransformations(transformationsMap);
        manager.saveMetadataTransformations();
    }
}

class TransformationComparator implements Comparator<MetadataTransformation> {
    public int compare(MetadataTransformation mT1, MetadataTransformation mT2) {
        return mT1.getId().compareTo(mT2.getId());
    }
}