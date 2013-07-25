package eu.europeana.repox2sip.util;

import eu.europeana.repox2sip.Repox2SipException;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.w3c.dom.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import javax.xml.parsers.*;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * A utility class containing methods for creating and managing hash objects.
 * 
 * @author Cesare Concordia <cesare.concordia@isti.cnr.it>
 */

public class ObjectHash {

	private static final String[] HEX = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "A", "B", "C", "D", "E", "F" };

	private static final String[] fields = { "//urecord//title" };
	
	private static String prolog="<?xml version=\"1.0\" encoding=\"UTF-8\"?><urecord>";
	
	private static String end="</urecord>";

	/**
	 * This method encodes the metadata record using SH-256 algorithm.
	 * 
	 * @param mdRecord
	 *            a string representing the dump of a metadata record
	 * @return the hash code of the metadata record
	 */
	public String encodeRecord(String mdRecord) throws Repox2SipException {
		MessageDigest algorithm = null;
		byte[] recordBytes;
		try {

			algorithm = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException nsae) {
			System.out.println("Cannot find digest algorithm");
			throw new Repox2SipException(nsae);
		}
		algorithm.reset();
		try {
			recordBytes = mdRecord.replaceAll("[\r\n]", "").getBytes("UTF-8");

		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace(System.err);
			throw new Repox2SipException(e);
		}
		algorithm.update(recordBytes);

		byte recordDigest[] = algorithm.digest();
		char[] hex = new char[2 * recordDigest.length];
		int index = 0;
		for (byte b : recordDigest) {
			int v = b & 0xFF;
			hex[index++] = HEX[v >>> 4].charAt(0);
			hex[index++] = HEX[v & 0xF].charAt(0);
		}
		return new String(hex);

	}

	public String uniqueEncodeRecord(String record) throws Repox2SipException {
		record =prolog+record+end;
		StringBuffer resultHash = new StringBuffer();
		String unique = "";
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(false);
		DocumentBuilder builder = null;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new Repox2SipException(e);
		}
		Document doc = null;
		try {
			byte currentXMLBytes[] = record.getBytes("UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					currentXMLBytes);
			doc = builder.parse(byteArrayInputStream);
		} catch (SAXException e) {
			throw new Repox2SipException(e);
		} catch (IOException e) {
			throw new Repox2SipException(e);
		}
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query
		XPathExpression expr = null;
		Object result = null;
		for (int j = 0; j < fields.length; j++) {
			try {

				expr = xpath.compile(fields[j]);
			} catch (XPathExpressionException e) {
				throw new Repox2SipException(e);
			}

			try {
				//print(doc);
				result = expr.evaluate(doc, XPathConstants.NODESET);

			} catch (XPathExpressionException e) {
				throw new Repox2SipException(e);
			}
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				resultHash.append(nodes.item(i).getTextContent());
			}
		}

		try {
			unique = encodeRecord(resultHash.toString());
		} catch (Repox2SipException e) {

			throw new Repox2SipException(e);
		}
		return unique;
	}

	private void print(Document doc) {
		// ///////////////
		// Output the XML

		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;
		try {
			trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);

			trans.transform(source, result);
			String xmlString = sw.toString();
			System.out.println("Here's the xml:\n\n" + xmlString);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// print xml
		

	}

}
