package eu.europeana.repox2sip.fixture;

import eu.europeana.definitions.domain.Country;
import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola Aloia <nicola.aloia@isti.cnr.it>
 *         Date: 19-apr-2010
 *         Time: 18.33.03
 */
public class DatabaseFixture {


    @Autowired
    @Qualifier("repox2sip")
    private Repox2Sip repox;

    private String record = "<record>" +
            "<dc:identifier>DE_ArchLABW_1_358099</dc:identifier>" +
            "<dcterms:isPartOf>Ministerium des Innern: Kommission für die Adelsmatrikel</dcterms:isPartOf>" +
            "<dc:source>Landesarchiv Baden-Württemberg: Hauptstaatsarchiv Stuttgart</dc:source>" +
            "<dc:source>http://www.landesarchiv-bw.de/hstas</dc:source>" +
            "<dc:relation>http://www.landesarchiv-bw.de/plink/?f=1-5584</dc:relation>" +
            "<dc:identifier>E 157/1 Bü 47</dc:identifier>" +
            "<dc:description>Kontext:Ministerium des Innern: Kommission für die Adelsmatrikel &gt;&gt; 2. Anlage der Personalmatrikel &gt;&gt; 2.02 Familien des begüterten Erbadels &gt;&gt; 2.02.01 Anfangsbuchstabe A</dc:description>" +
            "<dc:subject>Adelmann von Adelmannsfelden; Grafen</dc:subject>" +
            "<dcterms:spatial>Aalen, AA; Oberamt</dcterms:spatial>" +
            "<dcterms:spatial>Ellwangen (Jagst), AA; Regierung des Jagstkreises</dcterms:spatial>" +
            "<dc:subject>Urkunden; Reichsgrafendiplome</dc:subject>" +
            "<dc:subject>Wappen; Abbildungen</dc:subject>" +
            "<dc:title>Adelmann von Adelmannsfelden, Grafen Persönliche Verhältnisse der Familie</dc:title>" +
            "<europeana:unstored>Enthält u. a.: Einsendung des Familienwappens; Berichte des Oberamts Aalen an die Regierung des Jagstkreises in Ellwangen über Veränderungen in den persönlichen Verhältnissen der FamilieDarin: " +
            "1. Reichsgrafendiplom vom 22. Sept. 1790 (Abschrift) mit farbiger Abbildung des Wappens; " +
            "2. Personalmatrikelbögen (Konzepte und Reinschrift); " +
            "3. Todesanzeigen</europeana:unstored>" +
            "<europeana:unstored>3 cm, /_ 1-92</europeana:unstored>" +
            "<europeana:isShownAt>http://www.landesarchiv-bw.de/plink/?f=1-358099</europeana:isShownAt>" +
            "<europeana:object>http://www.landesarchiv-bw.de/plink/?f=1-358099-1&amp;ext=1</europeana:object>" +
            "<europeana:isShownBy>http://www.landesarchiv-bw.de/plink/?f=1-358099-1</europeana:isShownBy>" +
            "<europeana:country>germany</europeana:country>" +
            "<europeana:provider>Landesarchiv Baden-Württemberg</europeana:provider>" +
            "<europeana:type>IMAGE</europeana:type>" +
            "<europeana:language>de</europeana:language>" +
            "</record>";

    @Transactional
    public List<Provider> createProvider(Aggregator persistedAggregator, String testName, Country country, ProviderType type, int count) throws Repox2SipException {

        List<Provider> providers = new ArrayList<Provider>();

        Aggregator aggregator;
        if (persistedAggregator != null) {
            aggregator = persistedAggregator;
        } else {
            aggregator = this.createAggregator(testName, 1).get(0);
            repox.addAggregator(aggregator);
        }

        for (int walk = 0; walk < count; walk++) {
            Provider provider = new Provider();
            provider.setName(testName + walk);
            provider.setNameCode(testName + walk);
            provider.setCountry(country);
            provider.setType(type);
            provider.setRepoxProviderId(testName + walk);
            provider.setAggregator(aggregator);
            providers.add(provider);
        }

        return providers;
    }

    @Transactional
    public List<Aggregator> createAggregator(String testName, int count) throws Repox2SipException {

        List<Aggregator> aggregators = new ArrayList<Aggregator>();

        for (int walk = 0; walk < count; walk++) {
            Aggregator aggregator = new Aggregator();
            aggregator.setName(testName + walk);
            aggregator.setNameCode(testName + walk);
            aggregator.setRepoxAggregatorId(testName + walk);
            aggregators.add(aggregator);
        }

        return aggregators;
    }


    @Transactional
    public List<DataSet> createDataSet(Provider persistedProvider, String testName, Language language, DataSetType type, int count) throws Repox2SipException {

        List<DataSet> dataSets = new ArrayList<DataSet>();

        Provider provider;
        if (persistedProvider != null) {
            provider = persistedProvider;
        } else {
            provider = this.createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
            repox.addProvider(provider);
        }

        for (int walk = 0; walk < count; walk++) {
            DataSet dataSet = new DataSet();
            dataSet.setName(testName + walk);
            dataSet.setNameCode(testName + walk);
            dataSet.setLanguage(language);
            dataSet.setType(type);
            dataSet.setIdQName(testName + walk);
            dataSet.setQName(testName + walk);
            dataSet.setOaiSetInput(testName + walk);
            dataSet.setOaiSetOutput(testName + walk);
            dataSet.setDescription(testName + walk);
            dataSet.setProvider(provider);
            dataSets.add(dataSet);
        }
        return dataSets;
    }


    @Transactional
    public List<Request> createRequest(DataSet persistedDataSet, String testName, RequestStatus status, int count) throws Repox2SipException {

        List<Request> requests = new ArrayList<Request>();

        DataSet dataSet;
        if (persistedDataSet != null) {
            dataSet = persistedDataSet;
        } else {
            dataSet = this.createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
            repox.addDataSet(dataSet);
        }

        for (int walk = 0; walk < count; walk++) {
            Request request = new Request();
            request.setName(testName + walk);
            request.setStatus(status);
            request.setDataSet(dataSet);
            requests.add(request);
        }
        return requests;
    }


    @Transactional
    public List<MetadataRecord> createMetadataRecord(String testName, MetadataRecordStatus status, String dataSource, int count) throws Repox2SipException {

        List<MetadataRecord> metadataRecords = new ArrayList<MetadataRecord>();

        for (int walk = 0; walk < count; walk++) {
            MetadataRecord metadataRecord = new MetadataRecord();
            metadataRecord.setStatus(status);
            metadataRecord.setRepoxID(testName + walk);
            metadataRecord.setRepoxMetadataId(testName + walk);

            if (dataSource != null) {
                metadataRecord.setSourceData(record);
            } else {
                metadataRecord.setSourceData(changeRecord(walk));
            }

            metadataRecord.setPid(Float.valueOf(String.valueOf(walk)));
            metadataRecords.add(metadataRecord);
        }
        return metadataRecords;
    }

    private String changeRecord(int walk) {
        String temp = record;
        return (temp.replace("<dc:identifier>DE_ArchLABW_1_358099", "<dc:identifier>DE_ArchLABW_1_358099" + walk));
    }
}

