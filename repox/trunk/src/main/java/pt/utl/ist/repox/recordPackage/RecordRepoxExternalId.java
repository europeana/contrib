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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class RecordRepoxExternalId implements RecordRepox {
    private static final Logger log = Logger.getLogger(RecordRepoxExternalId.class);
    static final long serialVersionUID = 1;

    protected Element dom;
    protected Object recordId;
    protected boolean isDeleted = false;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Object getId() {
        return recordId;
    }

    public Element getDom() {
        return dom;
    }

    public RecordRepoxExternalId() {
    }

    public RecordRepoxExternalId(Element dom, Object recordId) {
        this.dom = dom;
        this.recordId = recordId;
    }

    public RecordRepoxExternalId(Element dom, Object recordId, boolean isDeleted) {
        this(dom, recordId);
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

    public void addRecord2DataBase(Long requestId, Repox2Sip repox2sip) throws Repox2SipException {
        MetadataRecord mdRecord = createRecordSip();
        repox2sip.addMetadataRecord(requestId, mdRecord);
    }


}
