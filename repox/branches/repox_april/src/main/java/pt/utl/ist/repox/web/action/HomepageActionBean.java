package pt.utl.ist.repox.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataProviderPageable;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.DataSourcesMonitor;
import pt.utl.ist.repox.dataProvider.sorter.NameSorter;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.Pager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HomepageActionBean extends RepoxActionBean {
	private Pager pager;
	private List<DataProvider> dataProviders;
	private List<ScheduledTask> scheduledTasks;
	private List<MetadataTransformation> allMetadataTransformations;
	private Map<String, File> dataSourceLastLogMap;

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public List<DataProvider> getDataProviders() {
		return dataProviders;
	}

	public void setDataProviders(List<DataProvider> dataProviders) {
		this.dataProviders = dataProviders;
	}

	public List<ScheduledTask> getScheduledTasks() {
		return scheduledTasks;
	}

	public void setScheduledTasks(List<ScheduledTask> scheduledTasks) {
		this.scheduledTasks = scheduledTasks;
	}

	public List<MetadataTransformation> getAllMetadataTransformations() {
		return allMetadataTransformations;
	}

	public void setAllMetadataTransformations(List<MetadataTransformation> allMetadataTransformations) {
		this.allMetadataTransformations = allMetadataTransformations;
	}

	public Map<String, File> getDataSourceLastLogMap() {
		return dataSourceLastLogMap;
	}

	public void setDataSourceLastLogMap(Map<String, File> dataSourceLastLogMap) {
		this.dataSourceLastLogMap = dataSourceLastLogMap;
	}
	
	public Map<String, Boolean> getDataSourcesState() {
		return DataSourcesMonitor.getInstance().getDataSourcesState();
	}

	@DefaultHandler
	public Resolution homepage() throws IOException, DocumentException {
		pager = new Pager(new DataProviderPageable(new NameSorter()));
		String pageIndex = context.getRequest().getParameter("pageIndex");
		
		if(pageIndex != null) {
			pager.updatePager(Integer.valueOf(pageIndex));
		}
		
//		dataProviders = new ArrayList<DataProvider>(context.getRepoxManager().getDataProviderManager().loadDataProviders());
//		Collections.reverse(dataProviders);
		scheduledTasks = getContext().getRepoxManager().getTaskManager().getScheduledTasks();
		allMetadataTransformations = new ArrayList<MetadataTransformation>();

		Map<String, List<MetadataTransformation>> metadataTransformationMap = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformations();
		for (List<MetadataTransformation> metadatatransformations : metadataTransformationMap.values()) {
			for (MetadataTransformation currentMetadataTransformation : metadatatransformations) {
				allMetadataTransformations.add(currentMetadataTransformation);
			}
		}

		dataSourceLastLogMap = new TreeMap<String, File>();
		List<DataSource> dataSources = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataSources();
		for (DataSource currentDataSource : dataSources) {
			File logDir = currentDataSource.getLogsDir();
			String[] logs = logDir.list();
			if(logs != null && logs.length > 0) {
				File lastLog = new File(logDir, logs[logs.length - 1]);
				dataSourceLastLogMap.put(currentDataSource.getId(), lastLog);
			}
		}

		return new ForwardResolution("/jsp/index.jsp");
	}
	
	public Resolution monitorDataSource() throws IOException, DocumentException {
		String dataSourceId = context.getRequest().getParameter("dataSourceId");
		
		DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataProviderManager().getDataSource(dataSourceId);
		DataSourcesMonitor dataSourceMonitor = DataSourcesMonitor.getInstance();
		dataSourceMonitor.updateDataSource(dataSource);
		context.getMessages().add(new LocalizableMessage("dataSource.monitoringStarted", dataSource.getId()));
		
		return homepage();
	}

	public HomepageActionBean() {
		super();
	}
}