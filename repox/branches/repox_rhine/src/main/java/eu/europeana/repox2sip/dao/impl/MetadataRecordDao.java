/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.europeana.repox2sip.dao.impl;

import eu.europeana.repox2sip.InvalidValueException;
import eu.europeana.repox2sip.ObjectNotFoundException;
import eu.europeana.repox2sip.models.MetadataRecord;
import eu.europeana.repox2sip.models.Request;
import eu.europeana.repox2sip.util.ObjectHash;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 7-apr-2010
 *         Time: 15.28.39
 */
public class MetadataRecordDao {

    private Logger log = Logger.getLogger(getClass());

    private ObjectHash objectHash;
    @PersistenceContext
    protected EntityManager entityManager;

    //@Autowired
    //private ObjectHash objectHash;

    public MetadataRecordDao() {
        objectHash = new ObjectHash();
    }

    @Transactional
    public MetadataRecord addMetadataRecord(Long requestId, MetadataRecord metadataRecord) {

        try {
            if (metadataRecord.getSourceData() == null) {
                throw new InvalidValueException("sourceData=" + "null");
            }

            String contentHash = objectHash.encodeRecord(metadataRecord.getSourceData());
            //String uniqueContentHash = objectHash.uniqueEncodeRecord(metadataRecord.getSourceData());

            Query query = entityManager.createQuery("from MetadataRecord where contentHash = :hash");
            query.setParameter("hash", contentHash);

            List<MetadataRecord> dbMetadataRecords = query.getResultList();

            if (dbMetadataRecords != null && dbMetadataRecords.size() > 0) {
                metadataRecord = dbMetadataRecords.get(0);
            } else {
                metadataRecord.setContentHash(contentHash);
                //metadataRecord.setUniquenessHash(uniqueContentHash);
                Date date = new Date();
                metadataRecord.setCreationDate(date);
                metadataRecord.setLastModifiedDate(date);
                entityManager.persist(metadataRecord);
            }

            Request request = entityManager.find(Request.class, requestId);

            if (request == null) {
                throw new ObjectNotFoundException("request id=" + requestId);
            }
            request.addMetadataRecord(metadataRecord);
            metadataRecord.addRequest(request);
//            entityManager.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return metadataRecord;
    }

    @Transactional
    public List<MetadataRecord> addMetadataRecords(Long requestId, List<MetadataRecord> metadataRecords) {


        for (MetadataRecord metadataRecord : metadataRecords) {
            this.addMetadataRecord(requestId, metadataRecord);
        }

        return metadataRecords;
    }

    @Transactional(readOnly = true)
    public MetadataRecord getMetadataRecord(Long id) {
        return entityManager.find(MetadataRecord.class, id);
    }

    @Transactional
    public MetadataRecord updateMetadataRecord(MetadataRecord metadataRecord) {
        return entityManager.merge(metadataRecord);
    }

    @Transactional
    public void removeMetadataRecord(MetadataRecord metadataRecord) {
        entityManager.remove(metadataRecord);
    }

    @Transactional(readOnly = true)
    public List<MetadataRecord> getRequestMetadataRecords(Long requestId) {
        return entityManager.find(Request.class, requestId).getMetadataRecords();
    }

    @Transactional(readOnly = true)
    public List<Request> getMetadataRecordRequests(Long metadataRecordId) {
        return entityManager.find(MetadataRecord.class, metadataRecordId).getRequests();
    }
}