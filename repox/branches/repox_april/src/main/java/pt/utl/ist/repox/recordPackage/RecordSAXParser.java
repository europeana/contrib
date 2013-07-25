package pt.utl.ist.repox.recordPackage;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 15/Mar/2010
 * Time: 17:35:09
 * To change this template use File | Settings | File Templates.
 */

public class RecordSAXParser extends DefaultHandler {
    List<Element> records;

    private String rootElementName;
    private boolean insideElement = false;
    private String currentCharacters;
    private File file;

    /** Creates a new instance of RecordSAXParser */
    public RecordSAXParser(File fileInput, String rootNodeValue) {
        rootElementName = rootNodeValue;
        file = fileInput;
    }


    public List<Element> getRecords(){
        return records;
    }


    public void startDocument() throws SAXException {
        records = new ArrayList<Element>();
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException  {
        try{
            if (qName.equals(rootElementName)){
                currentCharacters = "";
                insideElement = true;
            }
            if(insideElement){
                String att = "";
                for (int i = 0; i < attributes.getLength(); i++) {
                    // Get names and values for each attribute
                    String name = attributes.getQName(i);
                    String value = attributes.getValue(i);

                    value = value.replace("<", "&lt;");
                    value = value.replace(">", "&gt;");
                    value = value.replace("&", "&amp;");
                    value = value.replace("\"", "&quot;");
                    value = value.replace("\'", "&#039;");

                    //if(!name.equals("") && !name.contains("xsi")){
                    if(!name.equals("")){
                        att+= " " + name + "=\"" + value + "\"";
                    }

                    //System.out.println("att = " + att);
                }
                currentCharacters+="<" + qName + att + ">";
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException  {
        try{
            if (qName.equals(rootElementName)){
                currentCharacters+="</" + qName + ">";

                System.out.println("currentCharacters = " + currentCharacters);

                SAXReader reader = new SAXReader();
                Document doc = reader.read(new StringReader(currentCharacters));

                records.add(doc.getRootElement());
                insideElement = false;
            }
            if(insideElement){
                currentCharacters+="</" + qName + ">";
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void characters(char buf[], int offset, int len) throws SAXException {
        String value = new String(buf,offset,len);

        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");
        value = value.replace("&", "&amp;");
        value = value.replace("\"", "&quot;");
        value = value.replace("\'", "&#039;");
        currentCharacters+=value;
    }

    /**
     * @throws SAXException
     * @return List<Element>
     */
    public static List<Element> parse(File file, String rootElementName) throws SAXException{
        try{
            RecordSAXParser handler = new RecordSAXParser(file, rootElementName);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLReader parser = saxParser.getXMLReader();
            parser.setContentHandler(handler);
            InputSource inSource=new InputSource(new FileInputStream(file));
            parser.parse(inSource);

            return handler.getRecords();

        }catch(Exception e){
            e.printStackTrace();
            throw new SAXException(e);
        }
    }



    public static void main( final String [] args ) {
        try{
            List<Element> records = RecordSAXParser.parse(new File("C:\\Users\\GPedrosa\\Desktop\\inesc\\00101_M_PT_Gulbenkian_biblioteca_digital_ese.xml"), "ese:ese");
            System.out.println("records = " + records.size());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
