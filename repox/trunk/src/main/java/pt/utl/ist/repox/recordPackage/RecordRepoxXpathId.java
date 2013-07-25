/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.repox.recordPackage;

import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.MetadataRecord;
import eu.europeana.repox2sip.models.MetadataRecordStatus;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TreeMap;


public class RecordRepoxXpathId implements RecordRepox {
    private static final Logger log = Logger.getLogger(RecordRepoxXpathId.class);

    protected Element dom;
    protected XPath idXpath;
    protected boolean isDeleted = false;

    public Object getId() {
        return idXpath.valueOf(dom);
    }

    public Element getDom() {
        return dom;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public RecordRepoxXpathId() {
    }

    public RecordRepoxXpathId(Element dom, XPath idXpath) {
        this.dom = dom;
        this.idXpath = idXpath;
    }

    public RecordRepoxXpathId(Element dom, XPath idXpath, boolean isDeleted) {
        this(dom, idXpath);
        this.isDeleted = isDeleted;
    }

    public byte[] serialize() {
        try {
            if(dom == null) {
                return null;
            }
            else {
                byte[] domToBytes = dom.asXML().getBytes("UTF-8");
                return domToBytes;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deserialize(byte[] bytes) throws DocumentException, UnsupportedEncodingException {
        dom = XmlUtil.getRootElement(bytes);
    }

    public MetadataRecord createRecordSip(){
        MetadataRecord mdRecord = new MetadataRecord();

        mdRecord.setStatus(MetadataRecordStatus.CREATED);


        Element element = getDom();
        if(element != null){
            mdRecord.setSourceData(element.asXML());
        }

        //TODO
        mdRecord.setRepoxMetadataId(this.getId().toString());

        Date date = new Date();
        mdRecord.setCreationDate(new Date());
        mdRecord.setLastModifiedDate(date);
        mdRecord.setRepoxID(getId().toString());

        return mdRecord;
    }

    public void addRecord2DataBase(Long requestId,Repox2Sip repox2sip) throws Repox2SipException {
        MetadataRecord mdRecord = createRecordSip();
        repox2sip.addMetadataRecord(requestId, mdRecord);
    }



    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        Element recordElement = reader.read(new FileInputStream("/home/dreis/lixo/polandsample.xml")).getRootElement();
        System.out.println(recordElement.asXML());
        String xPathString = "/mx:record/mx:controlfield[@tag=\"001\"]";
        XPath xPath = DocumentHelper.createXPath(xPathString);
        TreeMap<String, String> namespaces = new TreeMap<String, String>();
        namespaces.put("mx", "info:lc/xmlns/marcxchange-v1");
        xPath.setNamespaceURIs(namespaces);
        RecordRepoxXpathId record = new RecordRepoxXpathId(recordElement, xPath);

        log.info("record id = " + record.getId());
        log.info("record1 nodes size = " + xPath.selectNodes(record.getDom()).size());
    }

}
