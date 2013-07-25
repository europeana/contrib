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

public class SimpleFileExtract implements FileExtractStrategy {
    private static final Logger log = Logger.getLogger(SimpleFileExtract.class);

    public void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding,
                               File logFile){
        new RecordIterator(recordHandler, dataSource, file, characterEncoding, logFile);
    }

    public boolean isXmlExclusive() {
        return true;
    }

    private class RecordIterator {
        RecordHandler recordHandler;
        private DataSource dataSource;
        private File file;
        private CharacterEncoding characterEncoding;
        private File logFile;

        public RecordIterator(final RecordHandler recordHandler, final DataSource dataSource, final File file, CharacterEncoding characterEncoding, final File logFile) {
            this.recordHandler=recordHandler;
            this.dataSource = dataSource;
            this.file = file;
            this.characterEncoding = characterEncoding;
            this.logFile = logFile;

            try {
                if(dataSource.getClass() == DataSourceDirectoryImporter.class &&
                        ((DataSourceDirectoryImporter)dataSource).getRecordXPath() != null){

                    RecordSAXParser handler = new RecordSAXParser(((DataSourceDirectoryImporter)dataSource).getRecordXPath(), new RecordSAXParser.RecordHandler(){
                        public void handleRecord(Element recordElement){
                            try {
                                RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordElement, null, false, false);
                                //System.out.println("record.getId() = " + record.getId());
                                recordHandler.handleRecord(recordRepox);
                            }
                            catch (Exception e) {
                                StringUtil.simpleLog("Error importing record from file: " + file.getName() + " ERROR: " + e.getMessage(),
                                        this.getClass(), logFile);
                                log.error(file.getName() + ": " + e.getMessage(), e);
                            }
                        }
                    });
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader parser = saxParser.getXMLReader();
                    parser.setContentHandler(handler);
                    InputSource inSource=new InputSource(new FileInputStream(file));
                    parser.parse(inSource);


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
                    //List<Element> records = new ArrayList<Element>();
                    //records.add(document.getRootElement());
                    //recordIterator = records.iterator();

                    RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(document.getRootElement(), null, false, false);
                    //System.out.println("record.getId() = " + record.getId());
                    recordHandler.handleRecord(recordRepox);
                }
            }
            catch (Exception e) {
                StringUtil.simpleLog("Error parsing record(s) from file: " + file.getName() + " ERROR: " + e.getMessage(),
                        this.getClass(), logFile);
                e.printStackTrace();
            }
        }
    }
}