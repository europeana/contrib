package pt.utl.ist.repox.oai;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class DataSourceOaiFileImporter {

	public void doImport(File sourceFile) throws IOException, DocumentException {
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			String[] tokens = currentLine.split("\t");
			String dataProviderName = tokens[0];
			String dataSourceName = tokens[1];
			String oaiUrl = tokens[2];
			
			DataProvider newDataProvider = new DataProvider();
			newDataProvider.setName(dataProviderName);
			newDataProvider.setId(DataProvider.generateId(dataProviderName));
			Collection<DataSource> dataSources = new ArrayList<DataSource>();
			DataSourceOai dataSourceOai = new DataSourceOai(newDataProvider, dataSourceName, dataSourceName,
					MetadataFormat.oai_dc.toString(), oaiUrl, null, new IdGenerated(), null);
			dataSources.add(dataSourceOai);
			
			newDataProvider.setDataSources(dataSources );
			
			RepoxContextUtil.getRepoxManager().getDataProviderManager().saveDataProvider(newDataProvider);
		}
	}
	
	public static void main(String[] args) throws IOException, DocumentException {
		DataSourceOaiFileImporter importer = new DataSourceOaiFileImporter();
		
		File file = new File("f:/dreis/Desktop/sources.txt");
		importer.doImport(file);
	}
}
