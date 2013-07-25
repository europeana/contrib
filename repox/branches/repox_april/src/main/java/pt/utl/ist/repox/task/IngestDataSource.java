package pt.utl.ist.repox.task;


import org.apache.log4j.Logger;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.dataProvider.DataProviderManager;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.util.RepoxContextUtil;

/**
 * Ingest records from Data Source to REPOX
 *
 * @author dreis
 *
 */
public class IngestDataSource implements RunnableStoppable {
	private static final Logger log = Logger.getLogger(IngestDataSource.class);
	private DataSource dataSource;

	private String taskId;
	private String dataSourceId;
	private boolean fullIngest;
	private Task.Status exitStatus;
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public boolean isFullIngest() {
		return fullIngest;
	}

	public void setFullIngest(boolean fullIngest) {
		this.fullIngest = fullIngest;
	}
	
	public Task.Status getExitStatus() {
		return exitStatus;
	}

	public IngestDataSource() {
		super();
	}

	public IngestDataSource(String taskId, String dataSourceId, String fullIngest) {
		this();
		this.taskId = taskId;
		this.dataSourceId = dataSourceId;
		this.fullIngest = new Boolean(fullIngest);
	}

	public void run() {
		try {
                dataSource = RepoxContextUtil.getRepoxManager().getDataProviderManager().getDataSource(dataSourceId);
            
                RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true); //force updated count before ingesting
			    exitStatus = dataSource.startIngest(taskId, fullIngest);

		}
		catch (Exception e) {            
			log.error("Unable to start dataSource with id " + dataSource.getId() + " of Data Provider "
					+ dataSource.getDataProvider().getName(), e);
		}
	}

	public void stop() {
		if(dataSource == null) {
			log.error("Unable to stop dataSource (not running) with id " + dataSource.getId() + " of Data Provider "
					+ dataSource.getDataProvider().getName());
		}
		
		log.warn("Received stop signal for execution of Data Source " + dataSource.getId() + " of Data Provider "
				+ dataSource.getDataProvider().getName());
		dataSource.stopIngest();
	}

}
