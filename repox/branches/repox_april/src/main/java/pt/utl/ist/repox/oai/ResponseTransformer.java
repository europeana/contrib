package pt.utl.ist.repox.oai;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.FileUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.XmlUtil;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseTransformer {
	private static final Logger log = Logger.getLogger(ResponseTransformer.class);
	private static final String XSLT_OAI_REQ2RECORDS_FILENAME = "oairesponse2records.xsl";
	private static final String RECORD_ELEMENT_NAME = "record";

	private Transformer transformer;

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public ResponseTransformer() throws TransformerConfigurationException {
		super();
		// Loads the resource from [REPOX_CLASSES]/[XSLT_OAI_REQ2RECORDS_FILENAME]
		InputStream inputXsltStream = ResponseTransformer.class.getClassLoader().getResourceAsStream(XSLT_OAI_REQ2RECORDS_FILENAME);
		Source xsltSource = new StreamSource(inputXsltStream);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		this.transformer = transformerFactory.newTransformer(xsltSource);
	}

	private Iterator<Element> getIteratorSplitResponse(File xmlFile, File logFile) throws IOException, DocumentException, TransformerException {
		if(logFile == null || (!logFile.exists() && !logFile.createNewFile())) {
			throw new IOException("Unable to create log file: " + logFile.getAbsolutePath());
		}

		SAXReader reader = new SAXReader();
		Document xmlSource = reader.read(xmlFile);
		DocumentSource source = new DocumentSource(xmlSource);
		DocumentResult result = new DocumentResult();
		StringUtil.simpleLog("Starting to split OAI-PMH request to Record Files", this.getClass(), logFile);
		transformer.transform(source, result);
		Document transformedDoc = result.getDocument();
		Element rootElement = transformedDoc.getRootElement();

		return rootElement.elementIterator(RECORD_ELEMENT_NAME);
	}

	/**
	 * Extracts each record from the xmlFile and returns a list of records.
	 *
	 * @param xmlFile a file with OAI-PMH requests in XML
	 * @return a list of RecordRepox
	 * @throws Exception
	 */
	public List<RecordRepox> splitResponseToRecords(File xmlFile, DataSource dataSource, File logFile)
            throws Exception, TransformerException {
		List<RecordRepox> splitRecords = new ArrayList<RecordRepox>();
		Iterator<Element> iterator = getIteratorSplitResponse(xmlFile, logFile);

		// iterate through child elements of root
		while (iterator.hasNext()) {
			Element currentElement = iterator.next();

			String recordId = currentElement.element("identifier").getText();

			boolean deleted = false;
			Element recordElement = null;
			if(currentElement.attribute("status") != null
					&& currentElement.attributeValue("status").equals("deleted")) {
				deleted = true;
			}
			else {
				try {
					recordElement = (Element) currentElement.element("metadata").elements().get(0);
				}
				catch(Exception e) {
					log.error("Error getting metadata from dataSource " + dataSource.getId() + " in xmlFile " + xmlFile
							+ " record identifier " + recordId, e);
					continue;
				}
			}
			RecordRepox record = dataSource.getRecordIdPolicy().createRecordRepox(recordElement, recordId, true, deleted);
			splitRecords.add(record);
		}

		StringUtil.simpleLog("Finished splitting OAI-PMH request to List", this.getClass(), logFile);

		return splitRecords;
	}

	/**
	 * Extracts each record from the xmlFile to the directory recordsOutputDirname.
	 *
	 * @param xmlFile a file with OAI-PMH requests in XML
	 * @param recordsOutputDirname the output String where the records will be saved
	 * @throws Exception
	 */
	public void splitResponseToFiles(File xmlFile, String recordsOutputDirname, File logFile) throws TransformerException, IOException, DocumentException {
		File recordsOutputDir = new File(recordsOutputDirname);
		if(!recordsOutputDir.exists() && !recordsOutputDir.mkdir()) {
			throw new RuntimeException("Unable to create dir: " + recordsOutputDir.getAbsolutePath());
		}

		Iterator<Element> iterator = getIteratorSplitResponse(xmlFile, logFile);

		// iterate through child elements of root
		while (iterator.hasNext()) {
			Element currentElement = iterator.next();
			String identifier = sanitizeRecordElement(currentElement);

			String recordPath = recordsOutputDir.getAbsolutePath() + "/" + FileUtil.sanitizeToValidFilename(identifier) + ".xml";
			log.debug("recordPath " + recordPath);

			OutputStream outputStream = new FileOutputStream(recordPath);
			XmlUtil.writePrettyPrint(outputStream, currentElement);
		}

		StringUtil.simpleLog("Finished splitting OAI-PMH request to Record Files", this.getClass(), logFile);
	}

	/**
	 * Extract the first record from an XML Request as a String to the transformed version.
	 *
	 * @param xmlRequest a String of an OAI-PMH response with records in XML
	 * @return the transformed Record
	 * @throws Exception
	 */
	public String splitResponseToRecord(String xmlRequest) throws DocumentException, TransformerException {
		Document xmlSource = DocumentHelper.parseText(xmlRequest);
		DocumentResult result = new DocumentResult();
		transformer.transform(new DocumentSource(xmlSource), result);
		Document transformedDoc = result.getDocument();
		Element rootElement = transformedDoc.getRootElement();

		Element recordElement = (Element) rootElement.elementIterator(RECORD_ELEMENT_NAME).next();
		sanitizeRecordElement(recordElement);

		return recordElement.asXML();
	}

	/**
	 * Change the internal properties of the Record DOM representation to allow its usage in the file system.
	 */
	private String sanitizeRecordElement(Element recordElement) {
		Element identifierElement = getFirstInternalElement(recordElement, "identifier");
		// Here we remove all the ":" because it's the char used for URN separation
		identifierElement.setText(identifierElement.getText().replaceAll(":", "_"));
		String identifier = identifierElement.getText();
		//      String datestamp = getInternalNode(currentNode, "datestamp").getTextContent();
		//      String setSpec = getInternalNode(currentNode, "setSpec").getTextContent();

		return identifier;
	}

	private Element getFirstInternalElement(Element currentElement, String nodeName) {
		Iterator<Element> iterator = currentElement.elementIterator(nodeName);
		if(iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	public static void main(String[] args) throws Exception {
		String recordsDir = "/home/dreis/test/records";
		/*
		File xmlDir = new File("/home/dreis/test");
		File logFile = new File("/home/dreis/test/log.txt");
		ResponseTransformer transformer = new ResponseTransformer();
		System.out.println("asdasd");
		if(xmlDir.isDirectory()) {
			for (File  currentXMLFile : xmlDir.listFiles()) {
				System.out.println("asdasd2");
				if(currentXMLFile.isFile() && currentXMLFile.getName().startsWith("request")) {
					transformer.splitResponseToRecords(currentXMLFile, recordsDir, logFile);
				}

			}
		}
		 */

		// Conversion
		File stylesheet = new File("/home/dreis/projects/repox/WEB-INF/classes/conversion/unimarc2tel.xsl");
		File[] records = new File(recordsDir).listFiles();
		SAXReader reader = new SAXReader();
		File file = records[0];
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String currentLine;
		while ((currentLine = bufferedReader.readLine()) != null) {
			System.out.println(currentLine);
		}

		Document document = reader.read(new FileInputStream(file));

		// load the transformer using JAXP
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer2 = factory.newTransformer(new StreamSource(stylesheet));

		// now lets style the given document
		DocumentSource source = new DocumentSource(document);
		DocumentResult result = new DocumentResult();
		transformer2.transform(source, result);

		// return the transformed document
		Document transformedDoc = result.getDocument();
		System.out.println("Doc: \n" + transformedDoc.asXML());
	}
}
