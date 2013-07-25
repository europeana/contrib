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

import eu.europeana.repox2sip.models.Aggregator;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 7-apr-2010
 *         Time: 15.28.39
 */
public class AggregatorDao {

    private Logger log = Logger.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;


    public AggregatorDao() {
    }

    public Aggregator addAggregator(Aggregator aggregator) {
        log.info("Testing addAggregator: ");

        try {

            entityManager.persist(aggregator);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        log.info("addAggregator Test is OK! ");
        return aggregator;
    }
}
