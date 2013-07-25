package pt.utl.ist.repox.metadataTransformation;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.*;
import pt.utl.ist.repox.util.RepoxContextUtil;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class MetadataTransformation {
	private static final Logger log = Logger.getLogger(MetadataTransformation.class);
	
	private String id;
	private String description;
	private String sourceFormat;
	private String destinationFormat;
	private String stylesheet;
	private boolean editable;
	private Transformer transformer;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourceFormat() {
		return sourceFormat;
	}
	
	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}
	
	public String getDestinationFormat() {
		return destinationFormat;
	}
	
	public void setDestinationFormat(String destinationFormat) {
		this.destinationFormat = destinationFormat;
	}
	
	public String getStylesheet() {
		return stylesheet;
	}
	
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public MetadataTransformation() {
		super();
	}

	public MetadataTransformation(String id, String description, String sourceFormat, String destinationFormat, String stylesheet, boolean editable) {
		super();
		this.id = id;
		this.description = description;
		this.sourceFormat = sourceFormat;
		this.destinationFormat = destinationFormat;
		this.stylesheet = stylesheet;
		this.editable = editable;
	}

	public String transform(String identifier, String xmlSourceString) throws DocumentException, TransformerException {
		if(transformer == null) {
			File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadStylesheet(this);
			Source xsltSource = new StreamSource(xsltFile);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			ErrorListener listener = new ErrorListener() {
				
				public void warning(TransformerException exception) throws TransformerException {
					log.error("Transformation Error", exception);
				}
				
				public void fatalError(TransformerException exception) throws TransformerException {
					log.error("Transformation Error", exception);
				}
				
				public void error(TransformerException exception) throws TransformerException {
					log.error("Transformation Error", exception);
				}
			};
			transformerFactory.setErrorListener(listener);
			
			transformer = transformerFactory.newTransformer(xsltSource);
		}
		
		Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
        DocumentSource source = new DocumentSource(sourceDocument);
        DocumentResult result = new DocumentResult();
        
        transformer.clearParameters();
        transformer.setParameter("recordIdentifier", identifier);
        transformer.transform(source, result);
        Document transformedDoc = result.getDocument();
        
		return transformedDoc.asXML();
	}
	
	public static void main(String[] args) throws Exception {
		String xsltFilename = "C:/LavoriMiei/Europeana/Sources/Contrib/Repox/work/1.testdeploy/repoxdata/configuration/xslt/marc21ToqdcPlusId.xsl";
		String xmlFilename = "C:/LavoriMiei/Europeana/Sources/Contrib/Repox/work/1.testdeploy/testrecords/testSamples/marcxchange/800055.marcxchange.xml";

		Source xsltSource = new StreamSource(new File(xsltFilename));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(xsltSource);

		SAXReader reader = new SAXReader();
		Document sourceDocument = reader.read(new File(xmlFilename));
		DocumentSource source = new DocumentSource(sourceDocument);
		DocumentResult result = new DocumentResult();
		transformer.clearParameters();
		transformer.setParameter("recordIdentifier", "identificador");
		transformer.transform(source, result);
		Document transformedDoc = result.getDocument();

		XMLWriter writer = new XMLWriter(System.out, OutputFormat.createPrettyPrint());
        writer.write(transformedDoc);

	}
}
