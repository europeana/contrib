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

import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.fixture.DatabaseFixture;
import eu.europeana.repox2sip.models.*;
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
public class TestDataset {
    private Logger log = Logger.getLogger(Repox2Sip.class);

    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;

    @Autowired
    @Qualifier("dbFixture")
    private DatabaseFixture databaseFixture;

    private String testName = "DataSetTest";
    private int count = 3;

    @Test
    public void addDataSet() throws Repox2SipException {

        log.info("Testing addDataSet: ");

        DataSet dataSet = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
        repox.addDataSet(dataSet);
        System.out.println("********* PersistedDataSet:" + dataSet);
        assertNotNull(dataSet);
        assertTrue(dataSet.getId() > -1);
        assertEquals(dataSet.getName(), testName + 0);
        assertEquals(dataSet.getNameCode(), testName + 0);
        assertEquals(dataSet.getLanguage(), Language.IT);
        assertEquals(dataSet.getType(), DataSetType.ESE);
        assertEquals(dataSet.getIdQName(), testName + 0);
        log.info("addDataSet Test is OK! ");
    }

    @Test
    public void getDataSet() throws Repox2SipException {

        log.info("Testing getDataSet: ");

        DataSet dataSet = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
        repox.addDataSet(dataSet);
        DataSet dbDataSet = repox.getDataSet(dataSet.getId());
        assertNotNull(dbDataSet);
        assertEquals(dataSet.getId(), dbDataSet.getId());
        assertEquals(dataSet.getName(), testName + 0);
        assertEquals(dataSet.getNameCode(), testName + 0);
        assertEquals(dataSet.getIdQName(), testName + 0);
        log.info("getDataSet Test is OK! ");
        System.out.println("********* dbDataSet:" + dbDataSet);
    }

    @Test
    public void addDataSets() throws Repox2SipException {
        log.info("Testing addDataSets: ");
        List<DataSet> dataSets = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, count);
        System.out.println("********* dataSets:" + dataSets);
        List<DataSet> persistedDataSet = repox.addDataSets(dataSets);

        assertNotNull(persistedDataSet);
        assertTrue(persistedDataSet.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedDataSet.get(walk).getId() > -1);
            assertEquals(persistedDataSet.get(walk).getName(), dataSets.get(walk).getName());
            assertEquals(persistedDataSet.get(walk).getNameCode(), dataSets.get(walk).getNameCode());
            assertEquals(persistedDataSet.get(walk).getIdQName(), dataSets.get(walk).getIdQName());
        }

        System.out.println("********* PersistedDataSet:" + persistedDataSet);
        log.info("addDataSets Test is OK! ");
    }

    @Test
    public void updateDataSet() throws Repox2SipException {
        log.info("Testing updateDataSet: ");

        String updatedName = "updateName";

        DataSet dataSet = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
        repox.addDataSet(dataSet);
        dataSet.setName(updatedName);
        DataSet dbDataSet = repox.updateDataSet(dataSet);
        assertNotNull(dbDataSet);
        assertEquals(dataSet.getId(), dbDataSet.getId());
        assertEquals(updatedName, dbDataSet.getName());
        log.info("updateDataSet Test is OK! ");
        System.out.println("********* dbDataSet:" + dbDataSet);
    }

    @Test
    public void removeDataSet() throws Repox2SipException {
        log.info("Testing removeDataSet: ");

        DataSet dataSet = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
        repox.addDataSet(dataSet);

        repox.removeDataSet(dataSet);
        DataSet dbDataSet = repox.getDataSet(dataSet.getId());
        assertNull(dbDataSet);

        log.info("removeDataSet Test is OK! ");
        System.out.println("********* dbDataSet:" + dbDataSet);
    }

    @Test
    public void removeDataSetCascade() throws Repox2SipException {
        log.info("Testing removeDataSetCascade: ");

        DataSet dataSet = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
        repox.addDataSet(dataSet);

        Request request = databaseFixture.createRequest(dataSet, testName, RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        MetadataRecord metadataRecord = databaseFixture.createMetadataRecord(testName, MetadataRecordStatus.PROCESSING, null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);

        System.out.println("********* PersistedMetadataRecord:" + metadataRecord);
        assertNotNull(metadataRecord);
        assertTrue(metadataRecord.getId() > -1);

        Long metadataRecordId = metadataRecord.getId();

        repox.removeDataSet(dataSet);
        DataSet dbDataSet = repox.getDataSet(dataSet.getId());
        assertNull(dbDataSet);

        MetadataRecord metadataRecordDeleted = repox.getMetadataRecord(metadataRecordId);
        assertNull(metadataRecordDeleted);

        log.info("removeDataSetCascade Test is OK! ");
        System.out.println("********* dbDataSet:" + dbDataSet);
    }

    @Test
    public void getProviderDataSets() throws Repox2SipException {
        log.info("Testing getProviderDataSets: ");

        List<DataSet> dataSets = databaseFixture.createDataSet(null, testName, Language.IT, DataSetType.ESE, count);
        dataSets = repox.addDataSets(dataSets);

        List<DataSet> persistedDataSets = repox.getProviderDataSets(dataSets.get(0).getProvider().getId());
        assertNotNull(persistedDataSets);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(dataSets.get(walk).getId(), persistedDataSets.get(walk).getId());
        }

        log.info("getProviderDataSets Test is OK! ");
        System.out.println("********* dbDataSet:" + persistedDataSets);
    }
}