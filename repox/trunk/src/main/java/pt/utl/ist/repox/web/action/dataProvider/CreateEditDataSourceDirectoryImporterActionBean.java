package pt.utl.ist.repox.web.action.dataProvider;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.SimpleFileExtract;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.marc.MarcXchangeFileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.*;

public class CreateEditDataSourceDirectoryImporterActionBean extends CreateEditDataSourceActionBean {
    private static final Logger log = Logger.getLogger(CreateEditDataSourceDirectoryImporterActionBean.class);

    private DataSourceDirectoryImporter dataSource;
    private String isoImplementationClass;

    public DataSourceDirectoryImporter getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDirectoryImporter dataSource) {
        this.dataSource = dataSource;
    }

    public String getIsoImplementationClass() {
        return isoImplementationClass;
    }

    public void setIsoImplementationClass(String isoImplementationClass) {
        this.isoImplementationClass = isoImplementationClass;
    }

    public CharacterEncoding[] getCharacterEncodings() {
        return CharacterEncoding.getValues();
    }

    public CreateEditDataSourceDirectoryImporterActionBean() throws IOException, DocumentException {
        super();
    }

    @Override
    public DataSource getBeanDataSource() {
        return dataSource;
    }

    @Override
    public Class<? extends DataSource> getDataSourceClass() {
        return DataSourceDirectoryImporter.class;
    }

    @Override
    public void validateDataSource(ValidationErrors errors) throws DocumentException {
        if(dataSource == null || dataSource.getSourcesDirPath() == null || dataSource.getSourcesDirPath().trim().isEmpty()) {
            errors.add("dataSource", new LocalizableError("error.dataSource.dirPath"));
        }
        if(dataSource != null && dataSource.getMetadataFormat().equals(MetadataFormat.ISO2709.name())) {
            if(dataSource == null || dataSource.getCharacterEncoding() == null
                    || dataSource.getCharacterEncoding().toString().isEmpty()) {
                errors.add("dataSource", new LocalizableError("error.dataSource.characterEncoding"));
            }
        }
        //Check if data source folder path is an http URL
        if(dataSource == null || dataSource.getSourcesDirPath() == null){
            errors.add("dataSource", new LocalizableError("error.dataSource.folderPath"));
        }
        else{
            String dataSourceDir = dataSource.getSourcesDirPath();
            if(dataSourceDir.startsWith("http://")){
                dataSource.setURLSourcesPath(dataSourceDir);
                //create a directory to store the downloaded file
                RepoxConfiguration config = RepoxContextUtil.getRepoxManager().getConfiguration();
                String repositoryPath = config.getRepositoryPath();
                String dataSourceDirTemp = dataSourceDir.replaceAll("[:/*<>|?.]", "");
                dataSourceDirTemp = repositoryPath+"/"+dataSourceDirTemp;
                dataSource.setSourcesDirPath(dataSourceDirTemp);
                //download the file
                dataSource.updateURLFile();
            }
        }
    }

    @Override
    public void loadDataSource() throws DocumentException, IOException {
        dataSource = (DataSourceDirectoryImporter) context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
        if(dataSource.getExtractStrategy() instanceof Iso2709FileExtract) {
            Iso2709FileExtract extractStrategy = (Iso2709FileExtract) dataSource.getExtractStrategy();
            isoImplementationClass = extractStrategy.getIsoImplementationClass();
        }
    }

    @Override
    public Resolution getCreationResolution() {
        return new ForwardResolution("/jsp/dataProvider/createDataSourceDirectoryImporter.jsp");
    }

    @Override
    public void prepareSubmission() {

        if(dataSource.getMetadataFormat().equals(MetadataFormat.ISO2709.toString())) {
            dataSource.setExtractStrategy(new Iso2709FileExtract(isoImplementationClass));
        } else if(dataSource.getMetadataFormat().equals(MetadataFormat.MarcXchange.toString())) {
            dataSource.setExtractStrategy(new MarcXchangeFileExtract());
        } else {
            dataSource.setExtractStrategy(new SimpleFileExtract());
        }
    }
}