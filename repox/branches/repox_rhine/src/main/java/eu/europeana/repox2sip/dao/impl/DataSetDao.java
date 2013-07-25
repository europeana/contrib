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
import eu.europeana.repox2sip.models.DataSet;
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
public class DataSetDao {

    private Logger log = Logger.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;


    public DataSetDao() {
    }

    @Transactional
    public DataSet addDataSet(DataSet dataSet) {

        try {
            Provider provider = entityManager.find(Provider.class, dataSet.getProvider().getId());
            if (provider == null) {
                throw new ObjectNotFoundException("Provider id=" + dataSet.getProvider().getId());
            }

            List<DataSet> dataSets = provider.getDataSets();
            if (dataSets == null) {
                dataSets = new ArrayList<DataSet>();
            }
            entityManager.persist(dataSet);
            dataSets.add(dataSet);
            provider.setDataSets(dataSets);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return dataSet;
    }

    @Transactional
    public List<DataSet> addDataSets(List<DataSet> dataSets) {

        try {

            for (DataSet dataSet : dataSets) {
                List<DataSet> addedDataSet = dataSet.getProvider().getDataSets();
                entityManager.persist(dataSet);
                if (addedDataSet == null) {
                    addedDataSet = new ArrayList<DataSet>();
                }
                addedDataSet.add(dataSet);
                dataSet.getProvider().setDataSets(addedDataSet);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return dataSets;
    }

    @Transactional(readOnly = true)
    public DataSet getDataSet(Long id) {
        return entityManager.find(DataSet.class, id);
    }

    @Transactional
    public DataSet updateDataSet(DataSet dataSet) {
        return entityManager.merge(dataSet);
    }

    @Transactional
    public void removeDataSet(DataSet dataSet) throws ObjectNotFoundException {
        if (dataSet == null){
            log.warn("input parameter null in Repox2Sip:removeDataSet");
            return;        
        }
        DataSet dataSetToRemove = entityManager.getReference(DataSet.class, dataSet.getId());
        if (dataSetToRemove == null) {
            throw new ObjectNotFoundException("DataSet id=" + dataSet.getId());
        }

        entityManager.remove(dataSetToRemove);
    }

    @Transactional(readOnly = true)
    public List<DataSet> getProviderDataSets(Long providerId) {
        Query query = entityManager.createQuery("from DataSet where provider.id = :id");
        query.setParameter("id", providerId);
        return query.getResultList();
    }
}