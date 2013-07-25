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

import eu.europeana.repox2sip.models.Aggregator;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
    private Aggregator aggregator;

    @Test
    public void addAggregator() {
        aggregator.setName("NicolaAgg1");
        aggregator.setNameCode("nA001");
        aggregator.setRepoxAggregatorId("repoxId");

        Aggregator persistedAggregator = repox.addAggregator(aggregator);

        System.out.println("********* PersistedAggregator:"+persistedAggregator);
        assertNotNull(persistedAggregator);
        assertTrue(persistedAggregator.getId()>-1);
        assertEquals(persistedAggregator.getName(),aggregator.getName());
        assertEquals(persistedAggregator.getNameCode(),aggregator.getNameCode());
    }
}
