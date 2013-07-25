/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.data.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordSAXParser;
import pt.utl.ist.repox.util.StringUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleFileExtract implements FileExtractStrategy {
    private static final Logger log = Logger.getLogger(SimpleFileExtract.class);

    public Iterator<RecordRepox> getIterator(DataSource dataSource, File file, CharacterEncoding characterEncoding,
                                             File logFile){
        return new RecordIterator(dataSource, file, characterEncoding, logFile);
    }

    public boolean isXmlExclusive() {
        return true;
    }

    private class RecordIterator implements Iterator<RecordRepox> {
        private DataSource dataSource;
        private File file;
        private CharacterEncoding characterEncoding;
        private File logFile;

        private Iterator<Element> recordIterator;

        public RecordIterator(DataSource dataSource, File file, CharacterEncoding characterEncoding, File logFile) {
            this.dataSource = dataSource;
            this.file = file;
            this.characterEncoding = characterEncoding;
            this.logFile = logFile;

            try {
                if(dataSource.getClass() == DataSourceDirectoryImporter.class &&
                        ((DataSourceDirectoryImporter)dataSource).getRecordXPath() != null){

                    RecordSAXParser handler = new RecordSAXParser(((DataSourceDirectoryImporter)dataSource).getRecordXPath());
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader parser = saxParser.getXMLReader();
                    parser.setContentHandler(handler);
                    InputSource inSource=new InputSource(new FileInputStream(file));
                    parser.parse(inSource);

                    if(handler.getRecords() == null || handler.getRecords().size() == 0){
                        StringUtil.simpleLog("-> Error: 0 records were found in the file name: " + file.getName() + " with record's root name: " + ((DataSourceDirectoryImporter)dataSource).getRecordXPath() +".", this.getClass(), logFile);
                    }
                    else{
                        recordIterator = handler.getRecords().iterator();
                    }

                    /*
                    //DOM
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);

                    if(((DataSourceDirectoryImporter)dataSource).getNamespaces() != null){
                        XPath xpath2 = DocumentHelper.createXPath(((DataSourceDirectoryImporter)dataSource).getRecordXPath());
                        xpath2.setNamespaceContext(new SimpleNamespaceContext(((DataSourceDirectoryImporter)dataSource).getNamespaces()));
                        recordIterator = xpath2.selectNodes(document).iterator();
                    }
                    else
                        recordIterator = document.selectNodes(((DataSourceDirectoryImporter)dataSource).getRecordXPath()).iterator();
                    */
                }
                else{
                    StringUtil.simpleLog("Creating record iterator for file: " + file.getName(), this.getClass(), logFile);
                    Document document = new SAXReader().read(file);
                    List<Element> records = new ArrayList<Element>();
                    records.add(document.getRootElement());
                    recordIterator = records.iterator();
                }
            }
            catch (Exception e) {
                recordIterator = null;
                StringUtil.simpleLog("Error parsing record(s) from file: " + file.getName() + " ERROR: " + e.getMessage(),
                        this.getClass(), logFile);
                e.printStackTrace();
            }
        }

        public boolean hasNext() {
            return recordIterator != null && recordIterator.hasNext();
        }

        public RecordRepox next() {
            StringUtil.simpleLog("Extracting record from file: " + file.getName(), this.getClass(), logFile);

            try {
                Element currentElement = recordIterator.next();
                RecordRepox record = dataSource.getRecordIdPolicy().createRecordRepox(currentElement, null, false, false);
                //System.out.println("record.getId() = " + record.getId());
                return record;
            }
            catch (Exception e) {
                StringUtil.simpleLog("Error importing record from file: " + file.getName() + " ERROR: " + e.getMessage(),
                        this.getClass(), logFile);
                log.error(file.getName() + ": " + e.getMessage(), e);
                return null;
            }
        }

        public void remove() {
        }

    }

}