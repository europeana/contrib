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

import eu.europeana.repox2sip.models.*;

import java.util.List;

/**
 * This interface represents a middleware for integrating Repox and SIP Manager systems.
 * Following the current requirements, the integration between the two mentioned systems is done at data level.
 * A global database schema has been designed to allows data sharing between Repox and SIP Manager.
 * The methods of this interface allows Repox to manage the database tables needed by the SIP Manager.
 *
 * @author Nicola Aloia   <nicola.aloia@isti.cnr.it>
 *         <p/>
 *         Date: 22-mar-2010
 *         Time: 12.15.44
 */
public interface Repox2Sip {

    /**
     * Persist the given Aggregator's values into database. An instance of the Aggregator class,
     * containing the generated Aggregator's Identifier, is returned.
     * An Exception is raised if the all the given values already exit into database;
     * <p/>
     *
     * @param aggregator the Aggregator instance to add
     * @return Aggregator
     */
    Aggregator addAggregator(Aggregator aggregator) throws Repox2SipException;

    /**
     * Persist the given List of Aggregator's values into database. A List of  Aggregator's instances,
     * containing the generated Aggregator's Identifiers, is returned. If all the given values for some given
     * Aggregator's instance already exit into database, the Identifier of returned class instance for those Aggregators
     * is set to -1L.
     *
     * @param aggregators List<Aggregator>
     * @return List<Aggregator> Duplicate instances of Aggregators contains -1L as Identifier.
     */
    List<Aggregator> addAggregators(List<Aggregator> aggregators) throws Repox2SipException;

    /**
     * Return the Aggregator instance with the given Identifier.
     * <p/>
     *
     * @param id Long   the Aggregator's identifier
     * @return Aggregator
     */
    Aggregator getAggregator(Long id) throws Repox2SipException;

    /**
     * Update the database values for the given Aggregator instance.
     * <p/>
     *
     * @param aggregator Aggregator
     * @return Aggregator
     */
    Aggregator updateAggregator(Aggregator aggregator) throws Repox2SipException;

    /**
     * Remove the given Aggregator from database. A cascade delete is activated on all depending objects
     * (e.g. Providr, Dataset, ...)
     * <p/>
     *
     * @param aggregator Aggregator
     */
    void removeAggregator(Aggregator aggregator) throws Repox2SipException;

    /**
     * Persist the given DataSet's values into database. An instance of the DataSet class,
     * containing the generated DataSet's Identifier, is returned.
     * An Exception is raised if the all the given values already exit into database;
     * <p/>
     *
     * @param dataSet DataSet
     * @return DataSet
     */
    DataSet addDataSet(DataSet dataSet) throws Repox2SipException;

    /**
     * Persist the given List of DataSet's values into database. A List of  DataSet's instances,
     * containing the generated DataSet's Identifiers, is returned. If all the given values for some given
     * DataSet's instance already exit into database, the Identifier of returned class instance for those DataSet
     * is set to -1L.
     *
     * @param dataSet List<DataSet>
     * @return List<DataSet> - Duplicate instances of DataSet contains -1L as Identifier.
     */
    List<DataSet> addDataSets(List<DataSet> dataSet) throws Repox2SipException;

    /**
     * Return the Provider instance with the given Identifier.
     * <p/>
     *
     * @param dataSetId Long
     * @return DataSet
     */
    DataSet getDataSet(Long dataSetId) throws Repox2SipException;

    /**
     * Return the List of Request of the DataSet with the given Identifier.
     * <p/>
     *
     * @param dataSetId Long
     * @return List<Provider>
     */
    List<Request> getDataSetRequests(Long dataSetId) throws Repox2SipException;

    /**
     * Update the database values for the given DataSet instance.
     * <p/>
     *
     * @param dataSet DataSet
     * @return DataSet
     */
    DataSet updateDataSet(DataSet dataSet) throws Repox2SipException;

    /**
     * Remove the given DataSet from database. A cascade delete is activated on all depending objects
     * (e.g. Request, ...)
     * <p/>
     *
     * @param dataSet DataSet
     */
    void removeDataSet(DataSet dataSet) throws Repox2SipException;

    /**
     * Persist the given Provider's values into database. An instance of the Provider class,
     * containing the generated Provider's Identifier, is returned.
     * An Exception is raised if the all the given values already exit into database;
     * <p/>
     *
     * @param provider Provider
     * @return Provider
     */
    Provider addProvider(Provider provider) throws Repox2SipException;

    /**
     * Persist the given List of Provider's values into database. A List of  Provider's instances,
     * containing the generated Provider's Identifiers, is returned. If all the given values for some given
     * Provider's instance already exit into database, the Identifier of returned class instance for those Providers
     * is set to -1L.
     *
     * @param providers List<Provider>
     * @return List<Provider> Duplicate instances of Providers contains -1L as Identifier.
     */
    List<Provider> addProviders(List<Provider> providers) throws Repox2SipException;

    /**
     * Return the Provider instance with the given Identifier.
     * <p/>
     *
     * @param providerId Long
     * @return Provider
     */
    Provider getProvider(Long providerId) throws Repox2SipException;

    /**
     * Return the List of Providers of the Aggregator with the given Identifier.
     * <p/>
     *
     * @param aggregatorId Long
     * @return List<Provider>
     */
    List<Provider> getAggregatorProviders(Long aggregatorId) throws Repox2SipException;

    /**
     * Return the List of DataSetd of the Provider with the given Identifier.
     * <p/>
     *
     * @param providerId Long
     * @return List<DataSet>
     */
    List<DataSet> getProviderDataSets(Long providerId) throws Repox2SipException;

    /**
     * Update the database values for the given Provider instance.
     * <p/>
     *
     * @param provider Provider
     * @return Provider
     */
    Provider updateProvider(Provider provider) throws Repox2SipException;

    /**
     * Remove the given Provider from database. A cascade delete is activated on all depending objects
     * (e.g. Dataset, ...)
     * <p/>
     *
     * @param provider Provider
     */
    void removeProvider(Provider provider) throws Repox2SipException;

    /**
     * Persist the given Request's values into database. An instance of the Provider class,
     * containing the generated Request's Identifier, is returned.
     * An Exception is raised if the all the given values already exit into database;
     * <p/>
     *
     * @param request Request
     * @return Request
     */
    Request addRequest(Request request) throws Repox2SipException;

    /**
     * Persist the given List of Request's values into database. A List of  Request's instances,
     * containing the generated Request's Identifiers, is returned. If all the given values for some given
     * Request's instance already exit into database, the Identifier of returned class instance for those Request
     * is set to -1L.
     *
     * @param requests List<Request>
     * @return List<Request> Duplicate instances of Request contains -1L as Identifier.
     */
    List<Request> addRequests(List<Request> requests) throws Repox2SipException;

    /**
     * Return the Request instance with the given Identifier.
     * <p/>
     *
     * @param requestId Long
     * @return Request
     */
    Request getRequest(Long requestId) throws Repox2SipException;

    /**
     * Return the List of MetadataRecord of the Request with the given Identifier.
     * <p/>
     *
     * @param requestId Long
     * @return List<MetadataRecord>
     */
    List<MetadataRecord> getRequestMetadataRecords(Long requestId) throws Repox2SipException;

    /**
     * Update the database values for the given Request instance.
     * <p/>
     *
     * @param request Request
     * @return Request
     */
    Request updateRequest(Request request) throws Repox2SipException;

    /**
     * Remove the given Request from database. A cascade delete is activated on all depending objects
     * (e.g. MetadataRecord, ...)
     * <p/>
     *
     * @param request Request
     */
    void removeRequest(Request request) throws Repox2SipException;


    /**
     * Persist the given MetadataRecord's values for the Request with the given Identifier.
     * An instance of the MetadataRecord class,
     * containing the generated MetadataRecord's Identifier, is returned.
     * An Exception is raised if the given metadataRecord values already exists into database or
     * the Request with the given Identifier  does'nt exists.;
     * <p/>
     *
     * @param requestId      Long
     * @param metadataRecord MetadataRecord
     * @return MetadataRecord
     */
    MetadataRecord addMetadataRecord(Long requestId, MetadataRecord metadataRecord) throws Repox2SipException;

    /**
     * Persist the given List of MetadataRecord's values for the Request with the given Identifier.
     * A List of  MetadataRecord's instances,
     * containing the generated MetadataRecord's Identifiers, is returned. If all the given values for some given
     * MetadataRecord's instance already exit into database, the Identifier of returned class instance for those Request
     * is set to -1L.
     * <p/>
     * An Exception is raised if  the Request with the given Identifier  does'nt exists.;
     *
     * @param requestId       Long
     * @param metadataRecords List<MetadataRecord>
     * @return List<MetadataRecord> Duplicate instances of MetadataRecord contains -1L as Identifier.
     */
    List<MetadataRecord> addMetadataRecords(Long requestId, List<MetadataRecord> metadataRecords) throws Repox2SipException;

    /**
     * Return the MetadataRecord instance with the given Identifier.
     * <p/>
     *
     * @param metadataRecordId Long
     * @return MetadataRecord
     */
    MetadataRecord getMetadataRecord(Long metadataRecordId) throws Repox2SipException;

    /**
     * Return the List of Request of the MetadataRecord with the given Identifier.
     * <p/>
     *
     * @param metadataRecordId Long
     * @return List<Request>
     */
    List<Request> getMetadataRecordRequests(Long metadataRecordId) throws Repox2SipException;

    /**
     * Update the database values for the given MetadataRecord instance.
     * <p/>
     *
     * @param metadataRecord MetadataRecord
     * @return MetadataRecord
     */
    MetadataRecord updateMetadataRecord(MetadataRecord metadataRecord) throws Repox2SipException;

    /**
     * Remove the given MetadataRecord from database.
     * <p/>
     *
     * @param metadataRecord MetadataRecord
     */
    void removeMetadataRecord(MetadataRecord metadataRecord) throws Repox2SipException;
}
