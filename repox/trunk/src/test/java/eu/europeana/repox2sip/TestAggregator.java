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

import eu.europeana.definitions.domain.Country;
import eu.europeana.repox2sip.fixture.DatabaseFixture;
import eu.europeana.repox2sip.models.Aggregator;
import eu.europeana.repox2sip.models.Provider;
import eu.europeana.repox2sip.models.ProviderType;
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
public class TestAggregator {
    private Logger log = Logger.getLogger(Repox2Sip.class);

    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;


    @Autowired
    @Qualifier("dbFixture")
    private DatabaseFixture databaseFixture;

    private String testName = "AggregatorTest";

    @Test
    public void addAggregator() throws Repox2SipException {

        log.info("Testing addAggregator: ");

        Aggregator aggregator = databaseFixture.createAggregator(testName, 1).get(0);
        Aggregator persistedAggregator = repox.addAggregator(aggregator);
        System.out.println("********* PersistedAggregator:" + aggregator);
        assertNotNull(persistedAggregator);
        assertTrue(persistedAggregator.getId() > -1);
        assertEquals(persistedAggregator.getName(), aggregator.getName());
        assertEquals(persistedAggregator.getNameCode(), aggregator.getNameCode());
        assertEquals(persistedAggregator.getRepoxAggregatorId(), aggregator.getRepoxAggregatorId());
        log.info("addAggregator Test is OK! ");
    }

    @Test
    public void getAggregator() throws Repox2SipException {

        log.info("Testing getAggregator: ");
        Aggregator aggregator = databaseFixture.createAggregator(testName, 1).get(0);
        Aggregator persistedAggregator = repox.addAggregator(aggregator);

        Aggregator dbAggregator = repox.getAggregator(persistedAggregator.getId());
        assertNotNull(dbAggregator);
        assertEquals(persistedAggregator.getId(), dbAggregator.getId());
        assertEquals(persistedAggregator.getName(), dbAggregator.getName());
        assertEquals(persistedAggregator.getNameCode(), dbAggregator.getNameCode());
        assertEquals(persistedAggregator.getRepoxAggregatorId(), dbAggregator.getRepoxAggregatorId());
        log.info("getAggregator Test is OK! ");
        System.out.println("********* dbAggregator:" + dbAggregator);
    }

    @Test
    public void addAggregators() throws Repox2SipException {

        int count = 3;

        List<Aggregator> aggregators = databaseFixture.createAggregator(testName, count);
        System.out.println("********* aggregators:" + aggregators);
        List<Aggregator> persistedAggregator = repox.addAggregators(aggregators);

        assertNotNull(persistedAggregator);
        assertTrue(persistedAggregator.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedAggregator.get(walk).getId() > -1);
            assertEquals(persistedAggregator.get(walk).getName(), aggregators.get(walk).getName());
            assertEquals(persistedAggregator.get(walk).getNameCode(), aggregators.get(walk).getNameCode());
            assertEquals(persistedAggregator.get(walk).getRepoxAggregatorId(), aggregators.get(walk).getRepoxAggregatorId());
        }

        System.out.println("********* PersistedAggregator:" + persistedAggregator);
        log.info("addAggregators Test is OK! ");
    }

    @Test
    public void updateAggregator() throws Repox2SipException {
        log.info("Testing updateAggregator: ");

        String updatedName = "updateName";

        Aggregator aggregator = databaseFixture.createAggregator(testName, 1).get(0);
        Aggregator persistedAggregator = repox.addAggregator(aggregator);

        persistedAggregator.setName(updatedName);
        Aggregator dbAggregator = repox.updateAggregator(persistedAggregator);
        assertNotNull(dbAggregator);
        assertEquals(persistedAggregator.getId(), dbAggregator.getId());
        assertEquals(updatedName, dbAggregator.getName());
        log.info("updateAggregator Test is OK! ");
        System.out.println("********* dbAggregator:" + dbAggregator);
    }

    @Test
    public void removeAggregator() throws Repox2SipException {
        log.info("Testing removeAggregator: ");

        Aggregator aggregator = databaseFixture.createAggregator(testName, 1).get(0);
        Aggregator persistedAggregator = repox.addAggregator(aggregator);

        assertNotNull(persistedAggregator);

        long id = persistedAggregator.getId();
        repox.removeAggregator(persistedAggregator);
        Aggregator dbAggregator = repox.getAggregator(id);
        assertNull(dbAggregator);

        log.info("removeAggregator Test is OK! ");
        System.out.println("********* dbAggregator:" + dbAggregator);
    }

    @Test
    public void updateAggregatorProviders() throws Repox2SipException {
        log.info("Testing updateAggregatorProviders: ");

        Aggregator aggregator = databaseFixture.createAggregator(testName, 1).get(0);
        repox.addAggregator(aggregator);

        List<Provider> providers = databaseFixture.createProvider(aggregator, testName, Country.ITALY, ProviderType.AGGREGATOR, 3);
        aggregator.setProviders(providers);
        Aggregator dbAggregator = repox.updateAggregator(aggregator);

        assertNotNull(dbAggregator);

        List<Provider> persisteProviders = repox.getAggregatorProviders(dbAggregator.getId());

        assertNotNull(persisteProviders);
        assertEquals(3, persisteProviders.size());
        assertEquals(providers, persisteProviders);
        log.info("updateAggregatorProviders Test is OK! ");
        System.out.println("********* dbAggregator:" + dbAggregator);
    }


}
