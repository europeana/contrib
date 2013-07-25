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

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Z39.50 Harvester by control number-local (from provided list)
 * Ids are extracted from a file (1 per line)
 */
public class IdListHarvester extends AbstractHarvester {
	private static final Logger log = Logger.getLogger(IdListHarvester.class);
    
	private String idBibAttribute="12";
	private File idListFile;
	
	public String getIdBibAttribute() {
		return idBibAttribute;
	}

	public void setIdBibAttribute(String idBibAttribute) {
		this.idBibAttribute = idBibAttribute;
	}

	public File getIdListFile() {
		return idListFile;
	}

	public void setIdListFile(File idListFile) {
		this.idListFile = idListFile;
	}

	/**
	 * 
	 * @param target
	 * @param idListFile file with one id per line
	 */
	public IdListHarvester(Target target, File idListFile) {
		super(target);
		this.idListFile = idListFile;
	}

	private class RecordIterator implements Iterator<RecordRepox> {
		private DataSource dataSource;
		private File logFile;
		private BufferedReader reader;
		
		private Enumeration<InformationFragment> currentInformationFragment;

		public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
			this.dataSource = dataSource;
			this.logFile = logFile;
			
			try {
				this.reader = new BufferedReader(new FileReader(idListFile));
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException("File not found: " + idListFile.getAbsolutePath(), e);
			}
		}

		private boolean getNextBatch() throws HarvestFailureException {
			int consecutiveErrors = 0;
			
			try {
				String recordId;
				while((recordId = reader.readLine()) != null) {
					recordId = recordId.replace('\"', ' ');
					String queryStr="@attrset bib-1 " + "@attr 1=" + idBibAttribute + " \"" + recordId + "\"";

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
				}
			}
			catch(IOException e) {
				throw new HarvestFailureException("Error reading file: " +  idListFile.getAbsolutePath(), e);
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
		return true;
	}
	
}
