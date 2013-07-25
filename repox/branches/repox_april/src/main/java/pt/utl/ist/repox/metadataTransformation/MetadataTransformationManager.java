package pt.utl.ist.repox.metadataTransformation;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MetadataTransformationManager {
	private static final Logger log = Logger.getLogger(MetadataTransformationManager.class);
		
	// Map of sourceMetadataFormat to list of available MetadataTransformations for that source 
	private Map<String, List<MetadataTransformation>> metadataTransformations;
	private File xsltDir;
	private File configurationFile;

	public Map<String, List<MetadataTransformation>> getMetadataTransformations() {
		return metadataTransformations;
	}

	public void setMetadataTransformations(Map<String, List<MetadataTransformation>> metadataTransformations) {
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
	
	public MetadataTransformationManager(File configurationFile, File xsltDir) {
		super();
		this.configurationFile = configurationFile;
		this.xsltDir = xsltDir;
	}
	
	public File loadStylesheet(MetadataTransformation metadataTransformation) {
		return new File(xsltDir, metadataTransformation.getStylesheet());
	}

	public void saveMetadataTransformation(MetadataTransformation metadataTransformation) throws IOException, DocumentException {
		if(loadMetadataTransformation(metadataTransformation.getId()) != null) {
			deleteMetadataTransformation(metadataTransformation.getId());
		}
		
		metadataTransformations = loadMetadataTransformations();
		List<MetadataTransformation> transformationsList = metadataTransformations.get(metadataTransformation.getSourceFormat());
		if(transformationsList == null) {
			transformationsList = new ArrayList<MetadataTransformation>();
		}
		transformationsList.add(metadataTransformation);
		metadataTransformations.put(metadataTransformation.getSourceFormat(), transformationsList);
		saveMetadataTransformations();
	}

	public boolean deleteMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
		metadataTransformations = loadMetadataTransformations();
		
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
	
	public MetadataTransformation loadMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
		Map<String, List<MetadataTransformation>> metadataTransformations = loadMetadataTransformations();

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
				metadataTransformationElement.addAttribute("editable", String.valueOf(metadataTransformation.isEditable()));
			}
		}

		XmlUtil.writePrettyPrint(configurationFile, document);
	}

	public synchronized Map<String, List<MetadataTransformation>>  loadMetadataTransformations() throws IOException, DocumentException {
		if(!configurationFile.exists()) {
			return new HashMap<String, List<MetadataTransformation>>();
		}
		
		SAXReader reader = new SAXReader();
        Document document = reader.read(configurationFile);
        List<Element> metadataTransformationElements = document.getRootElement().elements();
        
        metadataTransformations = new HashMap<String, List<MetadataTransformation>>();
        
        for (Element currentElement : metadataTransformationElements) {
        	String id = currentElement.element("id").getText();
        	String description = currentElement.elementText("description");
        	String sourceFormat = currentElement.elementText("sourceFormat");
        	String destinationFormat = currentElement.elementText("destinationFormat");
        	String stylesheet = currentElement.elementText("stylesheet");
        	boolean editable = Boolean.parseBoolean(currentElement.attributeValue("editable"));
        	MetadataTransformation metadataTransformation = new MetadataTransformation(id, description, sourceFormat, destinationFormat, stylesheet, editable);
        	
        	List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
        	if(transformationsList == null) {
        		transformationsList = new ArrayList<MetadataTransformation>();
        	}
        	transformationsList.add(metadataTransformation);
        	
        	Collections.sort(transformationsList, getTransformationComparator());

        	metadataTransformations.put(sourceFormat, transformationsList);
		}
        
		return metadataTransformations;
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
	
	public static void main(String[] args) throws IOException, DocumentException {
		MetadataTransformationManager manager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();
		Map<String, List<MetadataTransformation>> transformationsMap = manager.loadMetadataTransformations();
		List<MetadataTransformation> transformationsList = new ArrayList<MetadataTransformation>();
		transformationsList.add(new MetadataTransformation("marc212tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "marc212tel.xsl", false));
		transformationsList.add(new MetadataTransformation("unimarc2tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel.xsl", false));
		transformationsList.add(new MetadataTransformation("unimarc2tel-notbn", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel-notbn.xsl", false));
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