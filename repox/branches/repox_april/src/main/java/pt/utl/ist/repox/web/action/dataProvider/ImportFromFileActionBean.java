package pt.utl.ist.repox.web.action.dataProvider;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataProviderManager;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.load.DataProviderLoader;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ImportFromFileActionBean extends RepoxActionBean {
	private static final Logger log = Logger.getLogger(ImportFromFileActionBean.class);

	private FileBean dataProvidersFile;
	
	public FileBean getDataProvidersFile() {
		return dataProvidersFile;
	}

	public void setDataProvidersFile(FileBean dataProvidersFile) {
		this.dataProvidersFile = dataProvidersFile;
	}

	public Resolution cancel() {
		return new RedirectResolution("/Homepage.action");
	}

	private File saveFile() throws IOException, DocumentException {
		File tempDir = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getTempDir(),
				String.valueOf(new Date().getTime()));
		tempDir.mkdirs();
		File tempFile = new File(tempDir, "temp.xml");
		dataProvidersFile.save(tempFile);
		
		return tempFile;
	}
	
	public List<DataProvider> loadDataProviders(File tempFile) {
		try {
			DataProviderLoader loader = new DataProviderLoader();
			List<DataProvider> dataProvidersToImport = loader.loadDataProviders(tempFile);
			if(dataProvidersToImport == null || dataProvidersToImport.isEmpty()) {
				getContext().getValidationErrors().add("dataProvider", new LocalizableError("error.dataProvider.fileEmpty"));
			}
			else {
				return dataProvidersToImport;
			}
		}
		catch (IOException e) {
			log.error("Error saving file", e);
			getContext().getValidationErrors().add("dataProvider", new LocalizableError("error.dataProvider.savingFile"));
		}
		catch (DocumentException e) {
			log.error("Error loading XML", e);
			getContext().getValidationErrors().add("dataProvider", new LocalizableError("error.dataProvider.loadingXml"));
		}
		
		return null;
	}

	/**
	 * Returns true (and removes it from collection) if dataSources contains Data Source with id dataSourceId and returns false otherwise.
	 */
	private boolean removeDataSourceWithId(Collection<DataSource> dataSources, String dataSourceId) {
		Iterator<DataSource> iterator = dataSources.iterator();
		while(iterator.hasNext()) {
			DataSource dataSource = iterator.next();
			if(dataSource.getId().equals(dataSourceId)) {
				iterator.remove();
				return true;
			}
		}
		
		return false;
	}
	
	private void initializeDataSource(DataSource dataSource) throws SQLException {
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
		dataSources.add(dataSource);
		dataSource.initAccessPoints();
		context.getRepoxManager().getAccessPointsManager().initialize(dataSources);	
	}
	
	@ValidationMethod(on = {"submitFile"})
	public void validate(ValidationErrors errors) {
		if (dataProvidersFile == null) {
			errors.add("dataProvider", new LocalizableError("error.dataProvider.noFileSent"));
		}
	}
	
	public Resolution submitFile() {
		try {
			File tempFile = saveFile();
			List<DataProvider> dataProvidersToImport = loadDataProviders(tempFile);
				
			if(dataProvidersToImport == null) {
				return new ForwardResolution("/jsp/dataProvider/importFromFile.jsp");
			}
			
			DataProviderManager dataProviderManager = context.getRepoxManager().getDataProviderManager();
			
			for (DataProvider dataProviderToImport : dataProvidersToImport) {
				DataProvider loadedDataProvider = dataProviderManager.getDataProvider(dataProviderToImport.getId());
				
				Collection<DataSource> currentDataSources = (loadedDataProvider != null ? loadedDataProvider.getDataSources() : new ArrayList<DataSource>());
				Collection<DataSource> newDataSources = dataProviderToImport.getDataSources();
				List<DataSource> dataSourcesToInitialize = new ArrayList<DataSource>();
				
				for (DataSource dataSource : newDataSources) {
					// Try to remove Data Source, if it's not there it must be initialized
					if(!removeDataSourceWithId(currentDataSources, dataSource.getId())) {
						dataSourcesToInitialize.add(dataSource);
					}
					
					currentDataSources.add(dataSource);
				}
				
				dataProviderToImport.setDataSources(currentDataSources);
				
				if(loadedDataProvider != null) {
					dataProviderManager.updateDataProvider(dataProviderToImport, dataProviderToImport.getId());
				}
				else {
					dataProviderManager.saveDataProvider(dataProviderToImport);
				}
				
				for (DataSource dataSource : dataSourcesToInitialize) {
					initializeDataSource(dataSource);
				}
			}
		}
		catch(Exception e) {
			log.error("Failed to import from file", e);
			getContext().getValidationErrors().add("dataProvider", new LocalizableError("error.dataProvider.importFromFile"));
			
			return new ForwardResolution("/jsp/dataProvider/importFromFile.jsp");
		}
		
		return new RedirectResolution("/Homepage.action");
	}

}