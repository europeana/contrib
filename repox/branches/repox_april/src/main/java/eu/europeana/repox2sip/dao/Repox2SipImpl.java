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

import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.dao.impl.AggregatorDao;
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

    @Override
    public Aggregator addAggregator(Aggregator aggregator) {
        return aggregatorDao.addAggregator(aggregator);
    }

    @Override
    public List<Aggregator> addAggregators(List<Aggregator> aggregators) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Aggregator getAggregator(Long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Aggregator updateAggregator(Aggregator aggregator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAggregator(Aggregator aggregator) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataSet addDataSet(DataSet dataSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DataSet> addDataSets(List<DataSet> dataSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataSet getDataSet(Long dataSetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Request> getDataSetRequests(Long dataSetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataSet updateDataSet(DataSet dataSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeDataSet(DataSet dataSet) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Provider addProvider(Provider provider) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Provider> addProviders(List<Provider> providers) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Provider getProvider(Long providerId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Provider> getAggregatorProviders(Long aggregatorId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Provider updateProvider(Provider provider) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeProvider(Provider provider) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Request addRequest(Request request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Request> addRequests(List<Request> requests) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Request getRequest(Long requestId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MetadataRecord> getRequestMetadataRecords(Long requestId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Request updateRequest(Request request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeRequest(Request request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetadataRecord addMetadataRecord(MetadataRecord metadataRecord) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<MetadataRecord> addMetadataRecords(List<MetadataRecord> metadataRecords) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetadataRecord getMetadataRecord(Long metadataRecordId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Request> getMetadataRecordRequests(Long metadataRecordId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetadataRecord updateMetadataRecord(MetadataRecord metadataRecord) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeMetadataRecord(MetadataRecord metadataRecord) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
