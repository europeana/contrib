package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;
import pt.utl.ist.characters.RecordCharactersConverter;
import pt.utl.ist.characters.UnderCode32Remover;
import pt.utl.ist.marc.Record;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.StringUtil;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Z39.50 Harvester by control number-local (sequential number)
 * maximumId MUST be higher than current server MAX to allow future records with higher control number
 */
public class IdSequenceHarvester extends AbstractHarvester{
	private static final Logger log = Logger.getLogger(IdSequenceHarvester.class);
	private static final int stopAfterNotFoundCount = 5000;
    
	private String idBibAttribute="12";
	private Integer maximumId;
		
	public Integer getMaximumId() {
		return maximumId;
	}

	public void setMaximumId(Integer maximumId) {
		this.maximumId = maximumId;
	}

	/**
	 * @param target
	 * @param maximumId must be higher than current server MAX to allow future records with higher control number
	 */
	public IdSequenceHarvester(Target target, Integer maximumId) {
		super(target);
		this.maximumId = maximumId;
	}
	private class RecordIterator implements Iterator<RecordRepox> {
		private DataSource dataSource;
		private File logFile;
		
		private Enumeration<InformationFragment> currentInformationFragment;

		public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
			this.dataSource = dataSource;
			this.logFile = logFile;
		}

		private boolean getNextBatch() throws HarvestFailureException {
			int consecutiveErrors = 0;
			int currentId = 1;

			while(true) {
				String queryStr="@attrset bib-1 " + "@attr 1=" + idBibAttribute + " \"" + currentId + "\"";

				IRResultSet results = runQuery(queryStr);
				if(results == null) {

					consecutiveErrors++;

					if(consecutiveErrors > stopAfterNotFoundCount) {
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

				currentId++;
				if(maximumId != null && currentId > maximumId) {
					return true;
				}
			}
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
		return true;
	}
}
