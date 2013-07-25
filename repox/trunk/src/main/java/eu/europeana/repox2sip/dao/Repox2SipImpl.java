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
package eu.europeana.repox2sip.dao;

import eu.europeana.repox2sip.ObjectNotFoundException;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.dao.impl.*;
import eu.europeana.repox2sip.models.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * This class is an implementation of the Repox2Sip
 *
 * @author Nicola Aloia   <nicola.aloia@isti.cnr.it>
 *         Date: 23-mar-2010
 *         Time: 17.27.40
 */
public class Repox2SipImpl implements Repox2Sip {

    @Autowired
    private AggregatorDao aggregatorDao;

    @Autowired
    private ProviderDao providerDao;

    @Autowired
    private DataSetDao dataSetDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private MetadataRecordDao metadataRecordDao;


    @Override
    public Aggregator addAggregator(Aggregator aggregator) {
        return aggregatorDao.addAggregator(aggregator);
    }

    @Override
    public List<Aggregator> addAggregators(List<Aggregator> aggregators) {
        return aggregatorDao.addAggregators(aggregators);
    }

    @Override
    public Aggregator getAggregator(Long id) {
        return aggregatorDao.getAggregator(id);
    }

    @Override
    public Aggregator updateAggregator(Aggregator aggregator) {
        return aggregatorDao.updateAggregator(aggregator);
    }

    @Override
    public void removeAggregator(Aggregator aggregator) throws ObjectNotFoundException {
        aggregatorDao.removeAggregator(aggregator);
    }

    @Override
    public DataSet addDataSet(DataSet dataSet) {
        return dataSetDao.addDataSet(dataSet);
    }

    @Override
    public List<DataSet> addDataSets(List<DataSet> dataSet) {
        return dataSetDao.addDataSets(dataSet);
    }

    @Override
    public DataSet getDataSet(Long dataSetId) {
        return dataSetDao.getDataSet(dataSetId);
    }

    @Override
    public List<Request> getDataSetRequests(Long dataSetId) {
        return requestDao.getDataSetRequests(dataSetId);
    }

    @Override
    public DataSet updateDataSet(DataSet dataSet) {
        return dataSetDao.updateDataSet(dataSet);
    }

    @Override
    public void removeDataSet(DataSet dataSet) throws ObjectNotFoundException {
        dataSetDao.removeDataSet(dataSet);
    }

    @Override
    public Provider addProvider(Provider provider) {
        return providerDao.addProvider(provider);
    }

    @Override
    public List<Provider> addProviders(List<Provider> providers) {
        return providerDao.addProviders(providers);
    }

    @Override
    public Provider getProvider(Long providerId) {
        return providerDao.getProvider(providerId);
    }

    @Override
    public List<Provider> getAggregatorProviders(Long aggregatorId) {
        //return aggregatorDao.getAggregator(aggregatorId).getProviders();
        return providerDao.getAggregatorProviders(aggregatorId);
    }

    @Override
    public List<DataSet> getProviderDataSets(Long providerId) throws Repox2SipException {
        return dataSetDao.getProviderDataSets(providerId);
    }

    @Override
    public Provider updateProvider(Provider provider) {
        return providerDao.updateProvider(provider);
    }

    @Override
    public void removeProvider(Provider provider) throws ObjectNotFoundException {
        providerDao.removeProvider(provider);
    }

    @Override
    public Request addRequest(Request request) {
        return requestDao.addRequest(request);
    }

    @Override
    public List<Request> addRequests(List<Request> requests) {
        return requestDao.addRequests(requests);
    }

    @Override
    public Request getRequest(Long requestId) {
        return requestDao.getRequest(requestId);
    }

    @Override
    public List<MetadataRecord> getRequestMetadataRecords(Long requestId) {
        return metadataRecordDao.getRequestMetadataRecords(requestId);
    }

    @Override
    public Request updateRequest(Request request) {
        return requestDao.updateRequest(request);
    }

    @Override
    public void removeRequest(Request request) throws ObjectNotFoundException {
        requestDao.removeRequest(request);
    }

    @Override
    public MetadataRecord addMetadataRecord(Long requestId, MetadataRecord metadataRecord) throws Repox2SipException {
        return metadataRecordDao.addMetadataRecord(requestId, metadataRecord);
    }

    @Override
    public List<MetadataRecord> addMetadataRecords(Long requestId, List<MetadataRecord> metadataRecords) throws Repox2SipException {
        return metadataRecordDao.addMetadataRecords(requestId, metadataRecords);
    }

    @Override
    public MetadataRecord getMetadataRecord(Long metadataRecordId) {
        return metadataRecordDao.getMetadataRecord(metadataRecordId);
    }

    @Override
    public List<Request> getMetadataRecordRequests(Long metadataRecordId) {
        return metadataRecordDao.getMetadataRecordRequests(metadataRecordId);
    }

    @Override
    public MetadataRecord updateMetadataRecord(MetadataRecord metadataRecord) {
        return metadataRecordDao.updateMetadataRecord(metadataRecord);
    }

    @Override
    public void removeMetadataRecord(MetadataRecord metadataRecord) {
        metadataRecordDao.removeMetadataRecord(metadataRecord);
    }
}
