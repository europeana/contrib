package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;
import pt.utl.ist.characters.RecordCharactersConverter;
import pt.utl.ist.characters.UnderCode32Remover;
import pt.utl.ist.marc.Record;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.util.DateUtil;

import java.io.File;
import java.util.*;

/**
 * Z39.50 Harvester by Date/time last modified
 */
public class TimestampHarvester extends AbstractHarvester {
	private static final Logger log = Logger.getLogger(TimestampHarvester.class);

//	private String creationDateBibAttribute="1011";
    private String modificationDateBibAttribute = "1012";
    private Date earliestTimestamp;
    	
	public Date getEarliestTimestamp() {
		return earliestTimestamp;
	}

	public void setEarliestTimestamp(Date earliestTimestamp) {
		this.earliestTimestamp = earliestTimestamp;
	}

	/**
	 * 
	 * @param target
	 * @param earliestTimestamp Date from which records will be harvested
	 */
	public TimestampHarvester(Target target, Date earliestTimestamp) {
		super(target);
		this.earliestTimestamp = earliestTimestamp;
	}
	
	private class RecordIterator implements Iterator<RecordRepox> {
		private DataSource dataSource;
		private File logFile;
		private boolean fullIngest;
		private Date currentDay;
		private Date tomorrow;
		
		private Enumeration<InformationFragment> currentInformationFragment;

		public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
			this.dataSource = dataSource;
			this.logFile = logFile;
			this.fullIngest = fullIngest;
			this.currentDay = earliestTimestamp;
			
            if(!this.fullIngest && dataSource.getLastUpdate() != null) {
            	this.currentDay = dataSource.getLastUpdate(); 
            }
            this.tomorrow=DateUtil.add(new Date(), 1, Calendar.DAY_OF_MONTH);
		}

		private boolean getNextBatch() throws HarvestFailureException {
			int consecutiveErrors=0;
			
            while(currentDay.before(tomorrow)) {
            	StringUtil.simpleLog("Harvesting " + DateUtil.date2String(currentDay, "yyyyMMdd"), this.getClass(), logFile);

	            String queryStr="@attrset bib-1 ";
	            queryStr+="@and @attr 2=4 @attr 4=5  @attr 6=1  @attr 1="+modificationDateBibAttribute+" \""+DateUtil.date2String(currentDay, "yyyyMMdd")+"\"";
	            queryStr+=" @attr 2=2 @attr 4=5  @attr 6=1  @attr 1="+modificationDateBibAttribute+" \""+DateUtil.date2String(currentDay, "yyyyMMdd")+"\"";
		         	
	            IRResultSet results = runQuery(queryStr);
	            if(results == null) {
	            	
	            	consecutiveErrors++;

	            	if(consecutiveErrors > 10) {
	            		throw new HarvestFailureException("Importing aborted - Too many consecutive errors");
	            	}
	            }
	            else {
	            	consecutiveErrors = 0;
	            	
	            	StringUtil.simpleLog("Iterate over results (status=" + results.getStatus() + "), count=" + results.getFragmentCount(),
	            			this.getClass(), logFile);
		            currentInformationFragment = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(results);
		            
		            results.close();
		            
		            return true;
	            }
	            
	            currentDay = DateUtil.add(currentDay, 1, Calendar.DAY_OF_MONTH);
            }
            
            return false;
		}
		
		public boolean hasNext() {
			if(currentInformationFragment != null && currentInformationFragment.hasMoreElements()) {
				return true;
			}
			else {
				try {
					return getNextBatch();
				}
				catch(HarvestFailureException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public RecordRepox next() {
			try {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				
				Record record = handleRecord(currentInformationFragment.nextElement());
				
				RecordCharactersConverter.convertRecord(record, new UnderCode32Remover());
				boolean isRecordDeleted = (record.getLeader().charAt(5) == 'd');
				RecordRepoxMarc recordMarc = new RecordRepoxMarc(record);
				
				RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordMarc.getDom(), recordMarc.getId(), false, isRecordDeleted);

				return recordRepox;
			}
			catch (Exception e) {
				StringUtil.simpleLog("Error importing record: " + e.getMessage(), this.getClass(), logFile);
				log.error("Error importing record", e);
				
				return null;
			}
		}

		public void remove() {
		}

	}

	public Iterator<RecordRepox> getIterator(DataSource dataSource, File logFile, boolean fullIngest) {
		return new RecordIterator(dataSource, logFile, fullIngest);
	}

	public boolean isFullIngestExclusive() {
		return false;
	}

}
