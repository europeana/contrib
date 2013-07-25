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
public class TestProvider {
    private Logger log = Logger.getLogger(Repox2Sip.class);

    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;

    @Autowired
    @Qualifier("dbFixture")
    private DatabaseFixture databaseFixture;

    private String testName = "ProviderTest";
    private int count = 3;

    @Test
    public void addProvider() throws Repox2SipException {

        log.info("Testing addProvider: ");

        Provider provider = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
        repox.addProvider(provider);
        System.out.println("********* PersistedProvider:" + provider);
        assertNotNull(provider);
        assertTrue(provider.getId() > -1);
        assertEquals(provider.getName(), testName + 0);
        assertEquals(provider.getNameCode(), testName + 0);
        assertEquals(provider.getCountry(), Country.ITALY);
        assertEquals(provider.getType(), ProviderType.ARCHIVE);
        assertEquals(provider.getRepoxProviderId(), testName + 0);
        log.info("addProvider Test is OK! ");
    }

    @Test
    public void getProvider() throws Repox2SipException {

        log.info("Testing getProvider: ");

        Provider provider = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
        repox.addProvider(provider);
        Provider dbProvider = repox.getProvider(provider.getId());
        assertNotNull(dbProvider);
        assertEquals(provider.getId(), dbProvider.getId());
        assertEquals(provider.getName(), testName + 0);
        assertEquals(provider.getNameCode(), testName + 0);
        assertEquals(provider.getRepoxProviderId(), testName + 0);
        log.info("getProvider Test is OK! ");
        System.out.println("********* dbProvider:" + dbProvider);
    }

    @Test
    public void addProviders() throws Repox2SipException {
        log.info("Testing addProviders: ");
        List<Provider> providers = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, count);
        System.out.println("********* providers:" + providers);
        List<Provider> persistedProvider = repox.addProviders(providers);

        assertNotNull(persistedProvider);
        assertTrue(persistedProvider.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedProvider.get(walk).getId() > -1);
            assertEquals(persistedProvider.get(walk).getName(), providers.get(walk).getName());
            assertEquals(persistedProvider.get(walk).getNameCode(), providers.get(walk).getNameCode());
            assertEquals(persistedProvider.get(walk).getRepoxProviderId(), providers.get(walk).getRepoxProviderId());
        }

        System.out.println("********* PersistedProvider:" + persistedProvider);
        log.info("addProviders Test is OK! ");
    }

    @Test
    public void updateProvider() throws Repox2SipException {
        log.info("Testing updateProvider: ");

        String updatedName = "updateName";

        Provider provider = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
        repox.addProvider(provider);
        provider.setName(updatedName);
        Provider dbProvider = repox.updateProvider(provider);
        assertNotNull(dbProvider);
        assertEquals(provider.getId(), dbProvider.getId());
        assertEquals(updatedName, dbProvider.getName());
        log.info("updateProvider Test is OK! ");
        System.out.println("********* dbProvider:" + dbProvider);
    }

    @Test
    public void removeProvider() throws Repox2SipException {
        log.info("Testing removeProvider: ");

        Provider provider = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
        repox.addProvider(provider);

        repox.removeProvider(provider);
        Provider dbProvider = repox.getProvider(provider.getId());
        assertNull(dbProvider);

        log.info("removeProvider Test is OK! ");
        System.out.println("********* dbProvider:" + dbProvider);
    }

    @Test
    public void getAggregatorProviders() throws Repox2SipException {
        log.info("Testing getAggregatorProviders: ");

        List<Provider> providers = databaseFixture.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, count);
        providers = repox.addProviders(providers);

        List<Provider> persistedProviders = repox.getAggregatorProviders(providers.get(0).getAggregator().getId());
        assertNotNull(persistedProviders);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(providers.get(walk).getId(), persistedProviders.get(walk).getId());
        }

        log.info("getAggregatorProviders Test is OK! ");
        System.out.println("********* dbProvider:" + persistedProviders);
    }
}