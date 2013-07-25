/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.repox.marc;

import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.MetadataRecord;
import eu.europeana.repox2sip.models.MetadataRecordStatus;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.util.RecordComparer;
import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.*;
import java.util.Date;
import java.util.HashSet;


public class RecordRepoxMarc implements RecordRepox, Serializable{
    private static final Logger log = Logger.getLogger(RecordRepoxMarc.class);
    static final long serialVersionUID = 1;

    protected Record record;
    protected boolean isDeleted = false;

    public RecordRepoxMarc() {
    }

    public RecordRepoxMarc(Element dom) {
        record = MarcXChangeDom4jBuilder.parseRecord(dom);
    }

    public RecordRepoxMarc(Element dom, boolean isDeleted) {
        this(dom);
        this.isDeleted = isDeleted;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream s = new ObjectOutputStream(out);
        s.writeObject(record);
        s.flush();
        return out.toByteArray();
    }

    public void deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream s = new ObjectInputStream(in);
        record=(Record) s.readObject();
        s.close();
        in.close();
    }

    public RecordRepoxMarc(Record record) {
        this.record = record;
    }

    public String getId() {
        return record.getNc();
    }

    public void toDom(Element doElement) {
        MarcXChangeDom4jBuilder.record2DomElement(record, doElement);
    }

    public Element getDom() {
        return MarcXChangeDom4jBuilder.record2Dom(record).getRootElement();
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object arg0) {
        if(!(arg0 instanceof RecordRepoxMarc)) {
            return false;
        }
        RecordRepoxMarc other=(RecordRepoxMarc)arg0;
        RecordComparer comparer=new RecordComparer(new HashSet<Integer>(0));
        return comparer.areEqual(record, getRecord());
    }


    public MetadataRecord createRecordSip(){
        MetadataRecord mdRecord = new MetadataRecord();

        mdRecord.setStatus(MetadataRecordStatus.CREATED);


        Element element = getDom();
        if(element != null){
            mdRecord.setSourceData(element.asXML());
        }

        //TODO
        mdRecord.setRepoxMetadataId(this.getId());

        Date date = new Date();
        mdRecord.setCreationDate(new Date());
        mdRecord.setLastModifiedDate(date);

        mdRecord.setRepoxID(getId().toString());
        return mdRecord;
    }

    /**
     * Add record to database
     * @param requestId
     * @param repox2sip
     * @throws Repox2SipException
     */
    public void addRecord2DataBase(Long requestId, Repox2Sip repox2sip) throws Repox2SipException {
        MetadataRecord mdRecord = createRecordSip();
        repox2sip.addMetadataRecord(requestId, mdRecord);
    }
}
