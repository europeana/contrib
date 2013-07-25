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
import eu.europeana.repox2sip.models.Request;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 7-apr-2010
 *         Time: 15.28.39
 */
public class RequestDao {

    private Logger log = Logger.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;


    public RequestDao() {
    }

    @Transactional
    public Request addRequest(Request request) {

        try {
            DataSet dataSet = entityManager.find(DataSet.class, request.getDataSet().getId());
            if (dataSet == null) {
                throw new ObjectNotFoundException("DataSet id=" + request.getDataSet().getId());
            }

            List<Request> requests = dataSet.getRequests();
            if (requests == null) {
                requests = new ArrayList<Request>();
            }
            request.setCreationDate(new Date());
            entityManager.persist(request);
            requests.add(request);
            dataSet.setRequests(requests);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return request;
    }

    @Transactional
    public List<Request> addRequests(List<Request> requests) {

        try {

            for (Request request : requests) {
                List<Request> addedRequest = request.getDataSet().getRequests();
                entityManager.persist(request);
                if (addedRequest == null) {
                    addedRequest = new ArrayList<Request>();
                }
                addedRequest.add(request);
                request.getDataSet().setRequests(addedRequest);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public Request getRequest(Long id) {
        return entityManager.find(Request.class, id);
    }

    @Transactional
    public Request updateRequest(Request request) {
        return entityManager.merge(request);
    }

    @Transactional
    public void removeRequest(Request request) throws ObjectNotFoundException {
        Request requestToRemove = entityManager.getReference(Request.class, request.getId());
        if (requestToRemove == null) {
            throw new ObjectNotFoundException("Request id=" + request.getId());
        }

        entityManager.remove(requestToRemove);
    }

    @Transactional(readOnly = true)
    public List<Request> getDataSetRequests(Long dataSetId) {
        Query query = entityManager.createQuery("from Request where dataSet.id = :id");
        query.setParameter("id", dataSetId);
        return query.getResultList();
    }
}