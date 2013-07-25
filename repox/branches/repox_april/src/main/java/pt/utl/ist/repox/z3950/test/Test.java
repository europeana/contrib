package pt.utl.ist.repox.z3950.test;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.z3950.*;
import pt.utl.ist.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Test {
	private enum HarvestType { timestamp, idList, idSequence };

	// harvester.setIdBibAttribute("14");
	// harvester.setIdBibAttribute("1007");

	private Target target;
	private Date earliestTimestamp;
	private File idListFile;
	private Integer maxId;

	public Test(Target target, Date earliestTimestamp, File idListFile, Integer maxId) {
		super();
		this.target = target;
		this.earliestTimestamp = earliestTimestamp;
		this.idListFile = idListFile;
		this.maxId = maxId;
	}

	public HarvestMethod getHarvestMethod(HarvestType harvestType) {
		switch (harvestType) {
			case timestamp:
				return new TimestampHarvester(target, earliestTimestamp);
			case idList:
				return new IdListHarvester(target, idListFile);
			case idSequence:
				return new IdSequenceHarvester(target, maxId);

			default:
				throw new RuntimeException("Unknown Harvest Type");
		}
	}

	public DataSourceZ3950 createDummyDataSource(HarvestMethod harvestMethod) throws IOException, DocumentException, SQLException {
		DataProvider dummyDP = new DataProvider("tempDP", "tempDP", null, "temporary Data Provider - delete", new ArrayList<DataSource>(), null);
		DataSourceZ3950 dataSourceZ3950 = new DataSourceZ3950(dummyDP, "tempZ3950", "tempZ3950", harvestMethod, new IdGenerated(), null);
		dummyDP.getDataSources().add(dataSourceZ3950);
		RepoxContextUtil.getRepoxManager().getDataProviderManager().saveDataProvider(dummyDP);
		dataSourceZ3950.initAccessPoints();
		RepoxContextUtil.getRepoxManager().getAccessPointsManager().initialize(dummyDP.getDataSources());
		
		return dataSourceZ3950;
	}

	private void deleteDummyDataSource(DataSourceZ3950 dataSourceZ3950) throws IOException, DocumentException {
		String dataProviderId = dataSourceZ3950.getDataProvider().getId();
		RepoxContextUtil.getRepoxManager().getDataProviderManager().deleteDataProvider(dataProviderId);
	}
	
	public static void main(String[] args) throws ParseException, IOException, DocumentException, SQLException {
		// Target target = new Target(1, "roze.lanet.lv", 9991, "lnc04", "z39_lnc04", "_zlnc04__", "", "usmarc");
		// Target target = new Target(1, "porbase.bnportugal.pt", 210, "porbase", "", "", "", "unimarc");
		// Target target = new Target("porbase.bnportugal.pt", 210, "porbase", "", "", "ISO-5426", "unimarc");
//		Target target = new Target("z3950.porbase.org", 21000, "bnd", "", "", "ISO8859-1", "unimarc");
		Target target = new Target("193.6.201.205", 1616, "B1", "LIBRI", "vision98", "ANSEL", "usmarc");
		
		// 193.6.201.205  1616 B1 LIBRI vision98 ANSEL usmarc f:/dreis/Desktop/Z39.50Harvester2/HungaryIdList.txt

		Date earliestTimestamp = DateUtil.string2Date("2009-01-05", "yyyy-MM-dd");
		File idListFile = new File("f:/dreis/Desktop/Z39.50Harvester2/HungaryIdList.txt");
		Integer maxId = null;
		
		Test test = new Test(target, earliestTimestamp, idListFile, maxId);
		DataSourceZ3950 dataSourceZ3950 = test.createDummyDataSource(test.getHarvestMethod(HarvestType.idList));
//		test.deleteDummyDataSource(dataSourceZ3950); System.exit(0);

		File logFile = new File("f:/dreis/Desktop/Z39.50Harvester2/apagamez3950TimestampHarvester.log");
		dataSourceZ3950.ingestRecords(logFile, false);
	}

}
