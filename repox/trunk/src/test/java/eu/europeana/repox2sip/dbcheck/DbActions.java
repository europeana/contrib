package eu.europeana.repox2sip.dbcheck;



import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.europeana.definitions.domain.Country;
import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.dao.Repox2SipImpl;

import eu.europeana.repox2sip.models.Aggregator;
import eu.europeana.repox2sip.models.DataSet;
import eu.europeana.repox2sip.models.DataSetType;
import eu.europeana.repox2sip.models.MetadataRecord;
import eu.europeana.repox2sip.models.MetadataRecordStatus;
import eu.europeana.repox2sip.models.Provider;
import eu.europeana.repox2sip.models.ProviderType;
import eu.europeana.repox2sip.models.Request;
import eu.europeana.repox2sip.models.RequestStatus;

/**
 * This contains methods that test Repox2Sip functionalities.
 * After every call the db status is modified, this is to allow tester to check the content of tables.
 */

public class DbActions {

    private Repox2Sip repox;

    private ApplicationContext context;

    private String record="<dc:identifier>DE_ArchLABW_1_358099</dc:identifier> " +
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
            "<europeana:language>de</europeana:language>";

    public DbActions(){
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"/repox2sip-applicationRepox-context.xml"});
        this.repox = (Repox2Sip) context.getBean("repox2sip");
    }
    /**
     * This method inserts an aggregator in the db
     *
     */
    public void insertAggregator() {

        Aggregator aggregator = null;
        Repox2SipImpl repoximpl=new Repox2SipImpl();
        System.out.println("repox: "+ repox);

        try {
            aggregator = createAggregator("MyAgg", 1).get(0);
        } catch (Repox2SipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Aggregator persistedAggregator = repox.addAggregator(aggregator);
            repox.addAggregators(createAggregator("MyAggs", 100));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("********* Persisted Aggregator:" + aggregator);

    }
    /**
     * This method inserts a provider in the db
     *
     */

    public void insertProvider() throws Repox2SipException {

        String testName="MyProv";

        // Provider provider = createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 1).get(0);
        //repox.addProvider(provider);
        List <Provider> prvs=createProvider(null, testName, Country.ITALY, ProviderType.ARCHIVE, 30000);
        repox.addProviders(prvs);
        //System.out.println("********* PersistedProvider:" + provider);

    }

    /**
     * A  metadata record is added to the db
     *
     * @throws Repox2SipException
     */

    public void addMetadataRecord() throws Repox2SipException {

        System.out.println("Testing addMetadataRecord: ");

        Request request =createRequest(null, "requestmdrecord", RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        MetadataRecord metadataRecord = createMetadataRecord("recordname", MetadataRecordStatus.PROCESSING,null, 1).get(0);
        repox.addMetadataRecord(request.getId(), metadataRecord);
        System.out.println("********* PersistedMetadataRecord:" + metadataRecord);
        assertNotNull(metadataRecord);
        assertTrue(metadataRecord.getId() > -1);

        assertEquals(MetadataRecordStatus.PROCESSING, metadataRecord.getStatus());
        assertNotNull(metadataRecord.getCreationDate());
        System.out.println("addMetadataRecord Test is OK! ");
    }


    /**
     * A number of metadata record are inserted in the db
     *
     * @throws Repox2SipException
     */
    public void addMetadataRecords(int count) throws Repox2SipException {
        // int count=2000;
        Request request = createRequest(null, "myrecord", RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);
        long startTime = System.currentTimeMillis();
        List<MetadataRecord> metadataRecords = createMetadataRecord("myrecord", MetadataRecordStatus.PROCESSING, null, count);
        System.out.println("Record list created::" + (System.currentTimeMillis() - startTime));

        System.out.println("********* adding metadataRecords");// + metadataRecords);
        startTime = System.currentTimeMillis();
        List<MetadataRecord> persistedMetadataRecord = repox.addMetadataRecords(request.getId(), metadataRecords);
        //System.out.println("Test:  = " + persistedMetadataRecord.get(0).getRequests().get(0).getDataSet());

        System.out.println("Test:  = " + persistedMetadataRecord.get(0).getRequests().get(0));

        System.out.println("metadata records added::" + (System.currentTimeMillis() - startTime));
        assertNotNull(persistedMetadataRecord);
        assertTrue(persistedMetadataRecord.size() == count);

        for (int walk = 0; walk < count; walk++) {
            assertTrue(persistedMetadataRecord.get(walk).getId() > -1);
            assertEquals(metadataRecords.get(walk).getStatus(), persistedMetadataRecord.get(walk).getStatus());
            assertEquals(metadataRecords.get(walk).getCreationDate(), persistedMetadataRecord.get(walk).getCreationDate());
            assertEquals(metadataRecords.get(walk).getRepoxMetadataId(), persistedMetadataRecord.get(walk).getRepoxMetadataId());
        }

        //System.out.println("********* PersistedMetadataRecord:" + persistedMetadataRecord);
        System.out.println("addMetadataRecords is OK! ");
    }

    public void getRequestMetadataRecords(int count) throws Repox2SipException {
        System.out.println("Testing getRequestMetadataRecords: ");

        Request request = createRequest(null, "myRequestMRTest", RequestStatus.UNDER_CONSTRUCTION, 1).get(0);
        repox.addRequest(request);

        List<MetadataRecord> metadataRecords = createMetadataRecord("myRequestMRTest", MetadataRecordStatus.PROCESSING, null, count);
        System.out.println("********* metadataRecords:" + metadataRecords);
        List<MetadataRecord> persistedMetadataRecords = repox.addMetadataRecords(request.getId(), metadataRecords);

        assertNotNull(persistedMetadataRecords);
        assertTrue(persistedMetadataRecords.size() == count);

        persistedMetadataRecords = null;
        try {
            persistedMetadataRecords = repox.getRequestMetadataRecords(request.getId());
        } catch (Repox2SipException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertNotNull(persistedMetadataRecords);
        for (int walk = 0; walk < count; walk++) {
            assertEquals(request.getId(), persistedMetadataRecords.get(walk).getRequests().get(0).getId());
        }

        System.out.println("getRequestMetadataRecords Test is OK! ");
        System.out.println("********* dbMetadataRecord:" + persistedMetadataRecords);
    }

    private  List<Aggregator> createAggregator(String testName, int count) throws Repox2SipException {

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


    private List<Provider> createProvider(Aggregator persistedAggregator, String testName, Country country, ProviderType type, int count) throws Repox2SipException {

        List<Provider> providers = new ArrayList<Provider>();

        Aggregator aggregator;
        if (persistedAggregator != null) {
            aggregator = persistedAggregator;
        } else {
            aggregator = this.createAggregator(testName, 1).get(0);
            repox.addAggregator(aggregator) ;
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




    private List<Request> createRequest(DataSet persistedDataSet, String testName, RequestStatus status, int count) throws Repox2SipException {

        List<Request> requests = new ArrayList<Request>();

        DataSet dataSet;
        if (persistedDataSet != null) {
            dataSet = persistedDataSet;
        } else {
            dataSet = createDataSet(null, testName, Language.IT, DataSetType.ESE, 1).get(0);
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


    private List<MetadataRecord> createMetadataRecord(String testName, MetadataRecordStatus status, String dataSource, int count) throws Repox2SipException {

        List<MetadataRecord> metadataRecords = new ArrayList<MetadataRecord>();

        for (int walk = 0; walk < count; walk++) {
            MetadataRecord metadataRecord = new MetadataRecord();
            metadataRecord.setStatus(status);
            metadataRecord.setRepoxID(testName + walk);
            metadataRecord.setRepoxMetadataId(testName + walk);

            if (dataSource != null) {
                metadataRecord.setSourceData(changeRecord(walk));
            } else {
                // metadataRecord.setSourceData(testName + walk);
                metadataRecord.setSourceData(changeRecord(walk));
            }
            metadataRecord.setPid(Float.valueOf(String.valueOf(walk)));
            metadataRecords.add(metadataRecord);
        }
        return metadataRecords;
    }
    private List<DataSet> createDataSet(Provider persistedProvider, String testName, Language language, DataSetType type, int count) throws Repox2SipException {

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


    private String changeRecord(int walk ){
        String temp=record;
        return (temp.replace ("<dc:identifier>DE_ArchLABW_1_358099", "<dc:identifier>DE_ArchLABW_1_358099"+walk));
    }

}
