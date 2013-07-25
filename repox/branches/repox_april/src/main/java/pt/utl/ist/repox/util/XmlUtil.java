package pt.utl.ist.repox.util;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.Charset;

public class XmlUtil {
	private static final Logger log = Logger.getLogger(XmlUtil.class);
	private static final String XML_FILE_ENCODING = "UTF-8";

	public static void writePrettyPrint(File destinationFile, Document document) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destinationFile), XML_FILE_ENCODING);
		XMLWriter writer = new XMLWriter(outputStreamWriter, format);
		writer.write(document);
		writer.close();
	}

	public static void writePrettyPrint(OutputStream outputStream, Document document) throws IOException {
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, XML_FILE_ENCODING);
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(outputStreamWriter, format);
		writer.write(document);
		writer.close();
	}

	public static void writePrettyPrint(OutputStream outputStream, Element element) throws IOException {
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, XML_FILE_ENCODING);
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(outputStreamWriter, format);
		writer.write(element);
		writer.close();
	}

	public static Element getRootElement(String xmlString) throws DocumentException, UnsupportedEncodingException {
		SAXReader reader = new SAXReader();
		InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		Document document = reader.read(inputStream);
		return document.getRootElement();
	}

	public static Element getRootElement(byte[] bytes) throws DocumentException, UnsupportedEncodingException {
		if(bytes == null) {
			return null;
		}

		SAXReader reader = new SAXReader();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		Document document = reader.read(inputStream);
		return document.getRootElement();
	}

	public static Element getRootElement(File xmlFile) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlFile);
		return document.getRootElement();
	}

	public static boolean elementsEqual(Element firstElement, Element secondElement) {
		try {
			OutputFormat format = OutputFormat.createCompactFormat();

			StringWriter firstElementWriter = new StringWriter();
			XMLWriter writerNew = new XMLWriter(firstElementWriter, format);
			writerNew.write(firstElement);

			StringWriter secondElementWriter = new StringWriter();
			XMLWriter writerOld = new XMLWriter(secondElementWriter, format);
			writerOld.write(secondElement);

			log.debug("XML Elements Equal: " + firstElementWriter.toString().equals(secondElementWriter.toString()));

			return firstElementWriter.toString().equals(secondElementWriter.toString());
		} catch (IOException e) {
			log.error("Error Writing XML", e);
			throw new RuntimeException("Error Writing XML", e);
		}
	}

    public static void writeXml(Element element, File destinationFile, Charset charset, boolean escapeText) throws IOException {
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding(charset.toString());

		Document document = DocumentHelper.createDocument((Element) element.detach());

		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(destinationFile), charset), outputFormat);
		writer.setEscapeText(escapeText);
		writer.write(document);
		writer.close();
	}

	public static void writeXml(Element element, File destinationFile, Charset charset) throws IOException {
		writeXml(element, destinationFile, charset, true);
	}

}
