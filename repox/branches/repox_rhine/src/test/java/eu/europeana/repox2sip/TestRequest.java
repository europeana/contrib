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
public class TestRequest {
    private Logger log = Logger.getLogger(Repox2Sip.class);

    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;

    @Autowired
    @Qualifier("dbFixture")
    private DatabaseFixture databaseFixture;

    private String testName = "RequestTest";
    private int count = 3;

    @Test
    public void addRequest() throws Repox2SipException {

        log.info("Testing addRequest: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        System.out.println("********* PersistedRequest:" + request);
        assertNotNull(request);
        assertTrue(request.getId() > -1);
        assertEquals(request.getName(), testName + 0);
        assertEquals(request.getStatus(), RequestStatus.UNDER_CONSTRUCTION);
        assertNotNull(request.getCreationDate());
        log.info("addRequest Test is OK! ");
    }

    @Test
    public void getRequest() throws Repox2SipException {

        log.info("Testing getRequest: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        Request dbRequest = repox.getRequest(request.getId());
        assertNotNull(dbRequest);
        assertEquals(request.getId(), dbRequest.getId());
        assertEquals(request.getName(), testName + 0);
        assertEquals(request.getStatus(), RequestStatus.UNDER_CONSTRUCTION);
        assertNotNull(request.getCreationDate());
        log.info("getRequest Test is OK! ");
        System.out.println("********* dbRequest:" + dbRequest);
    }

    @Test
    public void addRequests() throws Repox2SipException {
        log.info("Testing addRequests: ");
        List<Request> requests = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, count);
        System.out.println("********* requests:" + requests);
        List<Request> persistedRequest = repox.addRequests(requests);

        assertNotNull(persistedRequest);
        assertTrue(persistedRequest.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedRequest.get(walk).getId() > -1);
            assertEquals(persistedRequest.get(walk).getName(), requests.get(walk).getName());
            assertEquals(persistedRequest.get(walk).getStatus(), requests.get(walk).getStatus());
            assertEquals(persistedRequest.get(walk).getCreationDate(), requests.get(walk).getCreationDate());
        }

        System.out.println("********* PersistedRequest:" + persistedRequest);
        log.info("addRequests Test is OK! ");
    }

    @Test
    public void updateRequest() throws Repox2SipException {
        log.info("Testing updateRequest: ");

        String updatedName = "updateName";

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        request.setName(updatedName);
        Request dbRequest = repox.updateRequest(request);
        assertNotNull(dbRequest);
        assertEquals(request.getId(), dbRequest.getId());
        assertEquals(updatedName, dbRequest.getName());
        log.info("updateRequest Test is OK! ");
        System.out.println("********* dbRequest:" + dbRequest);
    }

    @Test
    public void removeRequest() throws Repox2SipException {
        log.info("Testing removeRequest: ");

        Request request = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        repox.removeRequest(request);
        Request dbRequest = repox.getRequest(request.getId());
        assertNull(dbRequest);

        log.info("removeRequest Test is OK! ");
        System.out.println("********* dbRequest:" + dbRequest);
    }

    @Test
    public void getDataSetRequests() throws Repox2SipException {
        log.info("Testing getDataSetRequests: ");

        List<Request> requests = databaseFixture.createRequest(null, testName, RequestStatus.UNDER_CONSTRUCTION, count);
        requests = repox.addRequests(requests);

        List<Request> persistedRequests = null;
        try {
            persistedRequests = repox.getDataSetRequests(requests.get(0).getDataSet().getId());
        } catch (Repox2SipException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertNotNull(persistedRequests);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(requests.get(walk).getId(), persistedRequests.get(walk).getId());
        }

        log.info("getDataSetRequests Test is OK! ");
        System.out.println("********* dbRequest:" + persistedRequests);
    }
}