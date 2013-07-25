package pt.utl.ist.repox.web.action.dataProvider;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.z3950.*;
import pt.utl.ist.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

public class CreateEditDataSourceZ3950ActionBean extends CreateEditDataSourceActionBean {
	private static final Logger log = Logger.getLogger(CreateEditDataSourceZ3950ActionBean.class);

	private DataSourceZ3950 dataSource;
	private Target target;
	private String harvestMethodString;
	private String earliestTimestampString;
	private Integer maximumId;
	private FileBean idListFile;
	
	public DataSourceZ3950 getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceZ3950 dataSource) {
		this.dataSource = dataSource;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	public String getHarvestMethodString() {
		return harvestMethodString;
	}

	public void setHarvestMethodString(String harvestMethodString) {
		this.harvestMethodString = harvestMethodString;
	}

	public String getEarliestTimestampString() {
		return earliestTimestampString;
	}

	public void setEarliestTimestampString(String earliestTimestampString) {
		this.earliestTimestampString = earliestTimestampString;
	}
	
	public Integer getMaximumId() {
		return maximumId;
	}

	public void setMaximumId(Integer maximumId) {
		this.maximumId = maximumId;
	}
	
	public FileBean getIdListFile() {
		return idListFile;
	}

	public void setIdListFile(FileBean idListFile) {
		this.idListFile = idListFile;
	}

	public CreateEditDataSourceZ3950ActionBean() throws IOException, DocumentException {
		super();
	}

	@Override
	public DataSource getBeanDataSource() {
		return dataSource;
	}

	@Override
	public Class<? extends DataSource> getDataSourceClass() {
		return DataSourceZ3950.class;
	}

	@Override
	public void validateDataSource(ValidationErrors errors) throws DocumentException {
		if(target == null || target.getAddress() == null || target.getAddress().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.targetAddress"));
		}
		if(target == null || target.getPort() <= 0) {
			errors.add("dataSource", new LocalizableError("error.dataSource.targetPort"));
		}
		if(target == null ||target.getDatabase() == null || target.getDatabase().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.targetDatabase"));
		}
		if(target == null || target.getCharset() == null || target.getCharset().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.targetCharset"));
		}
		if(target == null || target.getRecordSyntax() == null || target.getRecordSyntax().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.targetRecordSyntax"));
		}
				
		if(TimestampHarvester.class.getSimpleName().equals(harvestMethodString)) {
			if(earliestTimestampString != null && !earliestTimestampString.isEmpty()) {
				try {
					DateUtil.string2Date(earliestTimestampString, "yyyyMMdd");
				}
				catch (ParseException e) {
					errors.add("dataSource", new LocalizableError("error.dataSource.earliestTimestamp"));
				}
			}
		}
		else if(IdListHarvester.class.getSimpleName().equals(harvestMethodString)) {
			if((editing == null || !editing) && idListFile == null) {
				errors.add("dataSource", new LocalizableError("error.dataSource.idListFile"));
			}
		}
		else if(IdSequenceHarvester.class.getSimpleName().equals(harvestMethodString)) {
//			maximumId; Javascript forces numeric
		}
		else {
			throw new UnsupportedOperationException("Unsupported harvestMethodString: " + harvestMethodString);
		}
	}

	@Override
	public void loadDataSource() throws DocumentException, IOException {
		dataSource = (DataSourceZ3950) context.getRepoxManager().getDataProviderManager().getDataSource(dataSourceId);
		harvestMethodString = dataSource.getHarvestMethod().getClass().getSimpleName();
		target = dataSource.getHarvestMethod().getTarget();
		
		if(TimestampHarvester.class.getSimpleName().equals(dataSource.getHarvestMethod().getClass().getSimpleName())) {
			Date earliestTimestamp = ((TimestampHarvester)dataSource.getHarvestMethod()).getEarliestTimestamp();
			earliestTimestampString = (earliestTimestamp != null ? DateUtil.date2String(earliestTimestamp, "yyyyMMdd")  : null);
		}
		else if(IdListHarvester.class.getSimpleName().equals(dataSource.getHarvestMethod().getClass().getSimpleName())) {
//			idListFile = ((IdListHarvester)dataSource.getHarvestMethod()).getIdListFile(); 
		}
		else if(IdSequenceHarvester.class.getSimpleName().equals(dataSource.getHarvestMethod().getClass().getSimpleName())) {
			maximumId = ((IdSequenceHarvester)dataSource.getHarvestMethod()).getMaximumId(); 
		}
	}

	@Override
	public Resolution getCreationResolution() {
		return new ForwardResolution("/jsp/dataProvider/createDataSourceZ3950.jsp");
	}

	@Override
	public void prepareSubmission() {
		HarvestMethod harvestMethod = null;
		if(TimestampHarvester.class.getSimpleName().equals(harvestMethodString)) {
			Date earliestTimestamp = null;
			if(earliestTimestampString != null && !earliestTimestampString.isEmpty()) {
				try {
					earliestTimestamp = DateUtil.string2Date(earliestTimestampString, "yyyyMMdd");
				}
				catch (ParseException e) {
					log.error("Error parsing Date " + earliestTimestampString + " with format yyyyMMdd", e);
				}
			}
			harvestMethod = new TimestampHarvester(target, earliestTimestamp); 
		}
		else if(IdListHarvester.class.getSimpleName().equals(harvestMethodString)) {
			File idListFilePermanent = null;
			
			if((editing == null || !editing) && idListFile != null) {
				idListFilePermanent = getIdListFilePermanent();
				
				try {
					idListFile.save(idListFilePermanent);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				idListFilePermanent = ((IdListHarvester) dataSource.getHarvestMethod()).getIdListFile();
			}
			
			harvestMethod = new IdListHarvester(target, idListFilePermanent); 
		}
		else if(IdSequenceHarvester.class.getSimpleName().equals(harvestMethodString)) {
			harvestMethod = new IdSequenceHarvester(target, maximumId); 
		}
		else {
			throw new UnsupportedOperationException("Unsupported harvestMethodString: " + harvestMethodString);
		}
		
		dataSource.setHarvestMethod(harvestMethod);
	}

	private File getIdListFilePermanent() {
		File baseDir = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getXmlConfigPath(), "z3950");
		baseDir.mkdir();
		Random generator = new Random(new Date().getTime());
		String finalFilename = Math.abs(generator.nextInt()) + "z3950idList.txt";
		
		File permanentFile = new File(baseDir, finalFilename);
		return permanentFile;
	}

}