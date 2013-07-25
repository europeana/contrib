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

import eu.europeana.repox2sip.ObjectNotFoundException;
import eu.europeana.repox2sip.models.Aggregator;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    @Transactional
    public Aggregator addAggregator(Aggregator aggregator) {

        try {
            entityManager.persist(aggregator);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return aggregator;
    }

    @Transactional
    public List<Aggregator> addAggregators(List<Aggregator> aggregators) {

        try {
            for (Aggregator aggregator : aggregators) {
                entityManager.persist(aggregator);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return aggregators;
    }

    @Transactional(readOnly = true)
    public Aggregator getAggregator(Long id) {
        return entityManager.find(Aggregator.class, id);
    }

    @Transactional
    public Aggregator updateAggregator(Aggregator aggregator) {
        return entityManager.merge(aggregator);
    }

    @Transactional
    public void removeAggregator(Aggregator aggregator) throws ObjectNotFoundException {
        Aggregator aggregatorToRemove = entityManager.getReference(Aggregator.class, aggregator.getId());
        if (aggregatorToRemove == null) {
            throw new ObjectNotFoundException("Aggregator id=" + aggregator.getId());
        }

        entityManager.remove(aggregatorToRemove);
    }
}
