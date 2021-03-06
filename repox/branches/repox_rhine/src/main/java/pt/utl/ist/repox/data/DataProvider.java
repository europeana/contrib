package pt.utl.ist.repox.data;

import eu.europeana.definitions.domain.Country;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.Provider;
import eu.europeana.repox2sip.models.ProviderType;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataProvider {
    private String id;
    private String name;
    private String description;
    private String aggregatorId;
    private String nameCode;
    private URL homePage;
    private Collection<DataSource> dataSources;

    private AggregatorRepox aggregatorRepox;

    private long idDb = -1;
    private long aggregatorIdDb = -1;

    private ProviderType dataSetType;
    private Country country;


    public ProviderType getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(ProviderType dataSetType) {
        this.dataSetType = dataSetType;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getHomePage() {
        return homePage;
    }

    public void setHomePage(URL homePage) {
        this.homePage = homePage;
    }

    /**
     * Get the database ID of the Data Provider
     *
     * @return idDb
     */
    public Long getIdDb() {
        return idDb;
    }

    /**
     * Set the database ID of the Data Provider
     *
     * @param idDb - URL
     */
    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

    public String getAggregatorId() {
        return aggregatorId;
    }

    public void setAggregatorId(String aggregatorId) {
        this.aggregatorId = aggregatorId;
    }

    /**
     * Get the id generated by the Repox
     *
     * @return String
     */
    public long getAggregatorIdDb() {
        return aggregatorIdDb;
    }

    /**
     * Set the id generated by the Repox
     *
     * @param aggregatorIdDb String
     */
    public void setAggregatorIdDb(long aggregatorIdDb) {
        this.aggregatorIdDb = aggregatorIdDb;
    }

    public AggregatorRepox getAggregator() {
        return aggregatorRepox;
    }

    public void setAggregator(AggregatorRepox aggregatorRepox) {
        this.aggregatorRepox = aggregatorRepox;
    }



    public static String generateId(String name) throws DocumentException, IOException {
        String generatedIdPrefix = "";

        for (int i = 0; (i < name.length() && i < 32); i++) {
            if((name.charAt(i) >= 'a' && name.charAt(i) <= 'z')
                    || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z')) {
                generatedIdPrefix += name.charAt(i);
            }
        }
        generatedIdPrefix += "r";

        return generatedIdPrefix + generateNumberSufix(generatedIdPrefix);
    }

    private static int generateNumberSufix(String basename) throws DocumentException, IOException {
        int currentNumber = 0;
        String currentFullId = basename + currentNumber;

        while(RepoxContextUtil.getRepoxManager().getDataManager().getDataProvider(currentFullId) != null) {
            currentNumber++;
            currentFullId = basename + currentNumber;
        }

        return currentNumber;
    }

    /**
     * Retrieves this DataProvider's DataSource with identifier dataSourceId if this DataProvider contains
     * the DataSource with dataSourceId or null otherwise
     *
     * @param dataSourceId
     * @return DataSource with id dataSourceId or null otherwise
     */
    public DataSource getDataSource(String dataSourceId) {
        for(DataSource dataSource : dataSources) {
            if(dataSource.getId().equals(dataSourceId)) {
                return dataSource;
            }
        }

        return null;
    }

    public Collection<DataSource> getDataSources() {
        return dataSources;
    }

    public Collection<DataSource> getReversedDataSources() {
        List<DataSource> reversedDataSources = new ArrayList<DataSource>();
        for (DataSource currentDataSource : dataSources) {
            reversedDataSources.add(0, currentDataSource);
        }
        return reversedDataSources;
    }

    public void setDataSources(Collection<DataSource> dataSources) throws MalformedURLException {
        this.dataSources = dataSources;
    }


    /**
     * Add a Data Source
     * @param dataSource
     */
    public void addDataSource(DataSource dataSource) {
        dataSources.add(dataSource);
    }



    public DataProvider() {
        super();
        dataSources = new ArrayList<DataSource>();
    }

    public DataProvider(AggregatorRepox aggregatorRepox, String id, String name, String description, Collection<DataSource> dataSources) throws MalformedURLException {
        this();

        this.aggregatorRepox = aggregatorRepox;
        this.id = id;
        this.name = name;
        this.description = description;
        setDataSources(dataSources);
        this.aggregatorId = aggregatorRepox.getId();
        Long aggregatorIdDb = aggregatorRepox.getIdDb();
        if(aggregatorIdDb != null)
            this.aggregatorIdDb = aggregatorIdDb;
    }


    public DataProvider(AggregatorRepox aggregatorRepox, String id, String name, String description, Collection<DataSource> dataSources, String nameCode, ProviderType type, Country country, URL homePage, long idDb) throws MalformedURLException {
        this();

        this.aggregatorRepox = aggregatorRepox;
        this.id = id;
        this.idDb = idDb;
        this.name = name;
        this.description = description;
        setDataSources(dataSources);
        this.aggregatorId = aggregatorRepox.getId();
        this.homePage = homePage;

        Long aggregatorIdDb = aggregatorRepox.getIdDb();
        if(aggregatorIdDb != null)
            this.aggregatorIdDb = aggregatorIdDb;

        this.nameCode = nameCode;
        this.dataSetType = type;
        this.country = country;
    }



    /**
     * Create Provider object and add it to the database
     * @param repox2sip
     * @throws IOException
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public synchronized void addDataProvider2Database(Repox2Sip repox2sip) throws IOException, Repox2SipException {
        Provider provider = new Provider();
        provider.setName(this.getName());
        provider.setAggregator(repox2sip.getAggregator(this.getIdDb()));
        provider.setHomePage(this.getHomePage());
        provider.setDescription(this.getDescription());
        provider.setRepoxProviderId(this.getId());
        provider.setNameCode(this.getNameCode());
        provider.setCountry(this.getCountry());
        provider.setType(this.getDataSetType());
        provider.setAggregator(repox2sip.getAggregator(this.getAggregatorIdDb()));

        provider = repox2sip.addProvider(provider);
        this.setIdDb(provider.getId());
    }


    /**
     * Update the Data Provider in the database
     * @param repox2sip
     * @throws IOException
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public synchronized void updateDataProvider2Database(Repox2Sip repox2sip) throws IOException, Repox2SipException {
        Provider provider = repox2sip.getProvider(this.getIdDb());
        provider.setName(this.getName());
        provider.setAggregator(repox2sip.getAggregator(this.getIdDb()));
        provider.setHomePage(this.getHomePage());
        provider.setDescription(this.getDescription());
        provider.setRepoxProviderId(this.getId());
        provider.setNameCode(this.getNameCode());
        provider.setCountry(this.getCountry());
        provider.setType(this.getDataSetType());
        provider.setAggregator(repox2sip.getAggregator(this.getAggregatorIdDb()));

        repox2sip.updateProvider(provider);
    }


    /**
     * Remove the Data Provider from the database
     * @param repox2sip
     * @throws IOException
     * @throws Repox2SipException
     */
    public synchronized void deleteDataProviderFromDatabase(Repox2Sip repox2sip) throws IOException, Repox2SipException {
        repox2sip.removeProvider(repox2sip.getProvider(this.getIdDb()));
    }


}
