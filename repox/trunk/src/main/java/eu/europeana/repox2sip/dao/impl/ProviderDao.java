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
import eu.europeana.repox2sip.models.Provider;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 7-apr-2010
 *         Time: 15.28.39
 */
public class ProviderDao {

    private Logger log = Logger.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;


    public ProviderDao() {
    }

    @Transactional
    public Provider addProvider(Provider provider) {

        try {
            Aggregator aggregator = entityManager.find(Aggregator.class, provider.getAggregator().getId());
            if (aggregator == null) {
                throw new ObjectNotFoundException("Aggregator id=" + provider.getAggregator().getId());
            }

            List<Provider> providers = aggregator.getProviders();
            if (providers == null) {
                providers = new ArrayList<Provider>();
            }
            entityManager.persist(provider);
            providers.add(provider);
            aggregator.setProviders(providers);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return provider;
    }

    @Transactional
    public List<Provider> addProviders(List<Provider> providers) {

        try {

            for (Provider provider : providers) {
                List<Provider> addedProvider = provider.getAggregator().getProviders();
                entityManager.persist(provider);
                if (addedProvider == null) {
                    addedProvider = new ArrayList<Provider>();
                }
                addedProvider.add(provider);
                provider.getAggregator().setProviders(addedProvider);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return providers;
    }

    @Transactional(readOnly = true)
    public Provider getProvider(Long id) {
        return entityManager.find(Provider.class, id);
    }

    @Transactional
    public Provider updateProvider(Provider provider) {
        return entityManager.merge(provider);
    }

    @Transactional
    public void removeProvider(Provider provider) throws ObjectNotFoundException {
        Provider providerToRemove = entityManager.getReference(Provider.class, provider.getId());
        if (providerToRemove == null) {
            throw new ObjectNotFoundException("Provider id=" + provider.getId());
        }

        entityManager.remove(providerToRemove);
    }

    @Transactional(readOnly = true)
    public List<Provider> getAggregatorProviders(Long aggregatorId) {
        Query query = entityManager.createQuery("from Provider where aggregator.id = :id");
        query.setParameter("id", aggregatorId);
        return query.getResultList();
    }
}