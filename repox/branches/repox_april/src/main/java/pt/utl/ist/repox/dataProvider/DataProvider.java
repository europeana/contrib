package pt.utl.ist.repox.dataProvider;

import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.dao.Repox2SipImpl;
import eu.europeana.repox2sip.models.DataSet;
import eu.europeana.repox2sip.models.DataSetType;
import eu.europeana.repox2sip.models.Provider;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataProvider {
	private String id;
	private String name;
	private String country;
	private String description;
	private String logo;
	private Collection<DataSource> dataSources;
	
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
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public File getLogoFile() {
		File logosDir = RepoxContextUtil.getRepoxManager().getConfiguration().getLogosDir();
		if(logo != null && !logo.isEmpty() && new File(logosDir, logo).exists()) {
			return new File(logosDir, logo);
		}
		else {
			return null;
		}
	}
	
	public static String generateId(String name) throws DocumentException, IOException {
		String generatedIdPrefix = new String();

		for (int i = 0; (i < name.length() && i < 32); i++) {
			if((name.charAt(i) >= 'a' && name.charAt(i) <= 'z')
					|| (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z')) {
				generatedIdPrefix += name.charAt(i);
			}
		}
		generatedIdPrefix += "r"; 
		
		String fullId = generatedIdPrefix + generateNumberSufix(generatedIdPrefix);
		
		return fullId;
	}
	
	private static int generateNumberSufix(String basename) throws DocumentException, IOException {
		int currentNumber = 0;
		String currentFullId = basename + currentNumber;
		
		while(RepoxContextUtil.getRepoxManager().getDataProviderManager().getDataProvider(currentFullId) != null) {
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
	
	public void setDataSources(Collection<DataSource> dataSources) {
		this.dataSources = dataSources;
        //TODO: REPOX2SIP add the dataSources to the DB (DataSet table)
//        Repox2Sip repox2sip = new Repox2SipImpl();
//        for(DataSource dt : dataSources){
//            DataSet dataSet = new DataSet();
//            //TODO: fill the dataSet object
//            ??dataSet.setName(dt.getId());
//            //the DataSet id in the DB is a Long but in the code is a String...
//            ??dataSet.setProvider(repox2sip.getProvider(new Long(id)));
//            dataSet.setType(DataSetType.ESE);
//            dataSet.setHomePage(??);
//            repox2sip.addDataSet(dataSet);
//        }
	}


    public void addDataSource(DataSource dataSource) {
		dataSources.add(dataSource);
        //TODO: REPOX2SIP add the dataSources to the DB (DataSet table)
//        Repox2Sip repox2sip = new Repox2SipImpl();
//        DataSet dataSet = new DataSet();
//        //TODO: fill the dataSet object
//        ??dataSet.setName(dataSource.getId());
//        //the DataSet id in the DB is a Long but in the code is a String...
//        ??dataSet.setProvider(repox2sip.getProvider(new Long(id)));
//        dataSet.setType(DataSetType.ESE);
//        dataSet.setHomePage(??);
//        repox2sip.addDataSet(dataSet);
	}

	
	public DataProvider() {
		super();
		dataSources = new ArrayList<DataSource>();
	}
	
	public DataProvider(String id, String name, String country, String description, Collection<DataSource> dataSources, String logo) {
		this();
		this.id = id;
		this.name = name;
		this.country = country;
		this.description = description;
		//this.dataSources = dataSources;
        setDataSources(dataSources);
		this.logo = logo;
	}

    public Provider createProviderSip(){
        Provider provider = new Provider();

        provider.setName(this.getName());
        /*
        todo
        provider.setAggregator();
        provider.setCountry();
        provider.setDataSets();
        provider.setNameCode();
        provider.setHomePage();
        provider.setType();
        provider.setDescription();
        provider.setProviderId();
        */

        return provider;
    }
	
}
