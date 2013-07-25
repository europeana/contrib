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


package eu.europeana.repox2sip;

import eu.europeana.repox2sip.fixture.DatabaseFixture;
import eu.europeana.repox2sip.models.MetadataRecord;
import eu.europeana.repox2sip.models.MetadataRecordStatus;
import eu.europeana.repox2sip.models.Request;
import eu.europeana.repox2sip.models.RequestStatus;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 7-apr-2010
 *         Time: 16.47.07
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/repox2sip-application-context.xml",
        "/test-application-context.xml"
})

@Transactional
public class TestMetadataRecord {
    private Logger log = Logger.getLogger(Repox2Sip.class);

    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;

    @Autowired
    @Qualifier("dbFixture")
    private DatabaseFixture databaseFixture;

    private String testName = "MetadataRecordTest";
    private int count = 3;


    @Test
    public void addMetadataRecord() throws Repox2SipException {

        log.info("Testing addMetadataRecord: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        MetadataRecord metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);
        System.out.println("********* PersistedMetadataRecord:" + metadataRecord);
        assertNotNull(metadataRecord);
        assertTrue(metadataRecord.getId() > -1);

        assertEquals(MetadataRecordStatus.PROCESSING, metadataRecord.getStatus());
        assertNotNull(metadataRecord.getCreationDate());
        log.info("addMetadataRecord Test is OK! ");
    }

    @Test
    public void getMetadataRecord() throws Repox2SipException {

        log.info("Testing getMetadataRecord: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        MetadataRecord metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);

        MetadataRecord dbMetadataRecord = repox.getMetadataRecord(metadataRecord.getId());
        assertNotNull(dbMetadataRecord);
        assertEquals(metadataRecord.getId(), dbMetadataRecord.getId());
        assertEquals(MetadataRecordStatus.PROCESSING, metadataRecord.getStatus());
        assertNotNull(metadataRecord.getCreationDate());
        log.info("getMetadataRecord Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + dbMetadataRecord);
    }

    @Test
    public void addMetadataRecords() throws Repox2SipException {
        log.info("Testing addMetadataRecords: ");
        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        List<MetadataRecord> metadataRecords = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, count);
        System.out.println("********* metadataRecords:" + metadataRecords);
        List<MetadataRecord> persistedMetadataRecord = repox.addMetadataRecords(request.getId(), metadataRecords);

        assertNotNull(persistedMetadataRecord);
        assertTrue(persistedMetadataRecord.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedMetadataRecord.get(walk).getId() > -1);
            assertEquals(metadataRecords.get(walk).getStatus(), persistedMetadataRecord.get(walk).getStatus());
            assertEquals(metadataRecords.get(walk).getCreationDate(), persistedMetadataRecord.get(walk).getCreationDate());
            assertEquals(metadataRecords.get(walk).getRepoxMetadataId(), persistedMetadataRecord.get(walk).getRepoxMetadataId());
        }

        System.out.println("********* PersistedMetadataRecord:" + persistedMetadataRecord);
        log.info("addMetadataRecords Test is OK! ");
    }

    @Test
    public void updateMetadataRecord() throws Repox2SipException {
        log.info("Testing updateMetadataRecord: ");

        String updatedName = "updateName";
        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        assertNotNull(request);
        MetadataRecord metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);

        metadataRecord.setRepoxMetadataId(updatedName);
        MetadataRecord dbMetadataRecord = repox.updateMetadataRecord(metadataRecord);
        assertNotNull(dbMetadataRecord);
        assertEquals(metadataRecord.getId(), dbMetadataRecord.getId());
        assertEquals(updatedName, dbMetadataRecord.getRepoxMetadataId());
        log.info("updateMetadataRecord Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + dbMetadataRecord);
    }

    @Test
    public void removeMetadataRecord() throws Repox2SipException {
        log.info("Testing removeMetadataRecord: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        assertNotNull(request);
        MetadataRecord metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);


        repox.removeMetadataRecord(metadataRecord);
        MetadataRecord dbMetadataRecord = repox.getMetadataRecord(metadataRecord.getId());
        assertNull(dbMetadataRecord);

        log.info("removeMetadataRecord Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + dbMetadataRecord);
    }

    @Test
    public void getRequestMetadataRecords() throws Repox2SipException {
        log.info("Testing getRequestMetadataRecords: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        List<MetadataRecord> metadataRecords = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, count);
        System.out.println("********* metadataRecords:" + metadataRecords);
        List<MetadataRecord> persistedMetadataRecords = repox.addMetadataRecords(request.getId(), metadataRecords);

        assertNotNull(persistedMetadataRecords);
        assertTrue(persistedMetadataRecords.size() == count);

        persistedMetadataRecords = null;
        try {
            persistedMetadataRecords = repox.getRequestMetadataRecords(request.getId());
        } catch (Repox2SipException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertNotNull(persistedMetadataRecords);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(request.getId(), persistedMetadataRecords.get(walk).getRequests().get(0).getId());
        }

        log.info("getRequestMetadataRecords Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + persistedMetadataRecords);
    }


    @Test
    public void getMetadataRecordRequests() throws Repox2SipException {
        log.info("Testing getRequestMetadataRecords: ");

        List<Request> requests = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, count);
        List<MetadataRecord> metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1);

        MetadataRecord mdRecord = metadataRecord.get(0);

        List<Request> persistedRequests = repox.addRequests(requests);
        for (int walk = 0; walk < count; walk++) {
            mdRecord = repox.addMetadataRecord(requests.get(walk).getId(), mdRecord);
            assertNotNull(mdRecord);
        }
        Long mdRecordId = mdRecord.getId();
        assertNotNull(persistedRequests);
        assertEquals(count, persistedRequests.size());

        persistedRequests = null;
        try {
            persistedRequests = repox.getMetadataRecordRequests(mdRecordId);
        } catch (Repox2SipException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        assertEquals(count, persistedRequests.size());
        assertNotNull(persistedRequests);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(mdRecordId, persistedRequests.get(walk).getMetadataRecords().get(0).getId());
        }

        log.info("getRequestMetadataRecords Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + persistedRequests);
    }

    @Test
    public void addMetadataRecordsWithDuplicateHash() throws Repox2SipException {
        log.info("Testing addMetadataRecords: ");
        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        List<MetadataRecord> metadataRecords = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, this.testName, count);
        System.out.println("********* metadataRecords:" + metadataRecords);
        List<MetadataRecord> persistedMetadataRecord = repox.addMetadataRecords(request.getId(), metadataRecords);

        assertNotNull(persistedMetadataRecord);
        assertEquals(count, persistedMetadataRecord.size());

        assertTrue(persistedMetadataRecord.get(0).getId() > -1);
        assertEquals(metadataRecords.get(0).getStatus(), persistedMetadataRecord.get(0).getStatus());
        assertEquals(metadataRecords.get(0).getCreationDate(), persistedMetadataRecord.get(0).getCreationDate());
        assertEquals(metadataRecords.get(0).getRepoxMetadataId(), persistedMetadataRecord.get(0).getRepoxMetadataId());

        for (int walk = 1; walk < count; walk++) {
            assertTrue(persistedMetadataRecord.get(walk).getId() == -1);
            assertEquals(metadataRecords.get(walk).getStatus(), persistedMetadataRecord.get(walk).getStatus());
            assertEquals(metadataRecords.get(walk).getCreationDate(), persistedMetadataRecord.get(walk).getCreationDate());
            assertEquals(metadataRecords.get(walk).getRepoxMetadataId(), persistedMetadataRecord.get(walk).getRepoxMetadataId());
        }

        System.out.println("********* PersistedMetadataRecord:" + persistedMetadataRecord);
        log.info("addMetadataRecords Test is OK! ");
    }
}