/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import org.apache.log4j.Logger;
import pt.utl.ist.characters.RecordCharactersConverter;
import pt.utl.ist.characters.UnderCode32Remover;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.iso2709.IsoNavigator;
import pt.utl.ist.marc.iso2709.IteratorIso2709;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.FileExtractStrategy;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;

import java.io.File;
import java.util.Iterator;


public class Iso2709FileExtract implements FileExtractStrategy {
	//	MetadataFormat.ISO2709;

	private static final Logger log = Logger.getLogger(Iso2709FileExtract.class);

	private String isoImplementationClass;

	public String getIsoImplementationClass() {
		return isoImplementationClass;
	}

	public void setIsoImplementationClass(String isoImplementationClass) {
		this.isoImplementationClass = isoImplementationClass;
	}

	public Iso2709FileExtract(String isoImplementationClass) {
		this.isoImplementationClass = isoImplementationClass;
	}

    public void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding,
			File logFile){
		Iterator<RecordRepox> it= new RecordIterator(dataSource, file, characterEncoding, isoImplementationClass, logFile);
        while(it.hasNext()){
            recordHandler.handleRecord(it.next());
        }
	}

    public boolean isXmlExclusive() {
        return true;
    }

	private class RecordIterator implements Iterator<RecordRepox> {
		private DataSource dataSource;
		private File file;
		private File logFile;

		private IteratorIso2709 iteratorIso2709;

		public RecordIterator(DataSource dataSource, File file, CharacterEncoding characterEncoding,
				String isoImplementationClass, File logFile) {
			this.dataSource = dataSource;
			this.file = file;
			this.logFile = logFile;

			try {
				if (!IsoNavigator.canRead(file)) {  // if file is readable, it's valid
					StringUtil.simpleLog("File is unreadable as ISO2709: " + file.getName(), this.getClass(), logFile);
					iteratorIso2709 = null;
				}
				else {
					TimeUtil.getTimeSinceLastTimerArray(2);
					iteratorIso2709 = (IteratorIso2709) Class.forName(isoImplementationClass).getConstructor(File.class,
							String.class).newInstance(file, characterEncoding.toString());
					StringUtil.simpleLog("Created Iterator for file: " + file.getName(), this.getClass(), logFile);
				}
			}
			catch (Exception e) {
				iteratorIso2709 = null;
				StringUtil.simpleLog("Error creating Iso2709 iterator from file: " + file.getName() + " ERROR: " + e.getMessage(),
						this.getClass(), logFile);
			}
		}

		public boolean hasNext() {
            return iteratorIso2709 != null && iteratorIso2709.hasNext();
		}

		public RecordRepox next() {
			try {
				TimeUtil.getTimeSinceLastTimerArray(2);
				Record currentRecord = iteratorIso2709.next();
				RecordCharactersConverter.convertRecord(currentRecord, new UnderCode32Remover());

				boolean isRecordDeleted = (currentRecord.getLeader().charAt(5) == 'd');
				RecordRepoxMarc recordMarc = new RecordRepoxMarc(currentRecord);
				RecordRepox record = dataSource.getRecordIdPolicy().createRecordRepox(recordMarc.getDom(), recordMarc.getId(), false, isRecordDeleted);
				log.debug("Adding to import list record with id:" + record.getId());

				return record;
			}
			catch (Exception e) {
				StringUtil.simpleLog("Error importing record from file: " + file.getName() + " ERROR: " + e.getMessage(),
						this.getClass(), logFile);
				log.error(file.getName() + ": " + e.getMessage(), e);
				return null;
			}
		}

		public void remove() {
		}

	}

    /*
	public static void main(String[] args) throws Exception {
		Iso2709FileExtract extract = new Iso2709FileExtract(IteratorIso2709.class.getName());
//		String filename = "/home/dreis/testrecords/hu/error/B1_15_27.mrc";
//		Iterator<RecordRepox> iterator = extract.getIterator(null, new File(filename), CharacterEncoding.ANSEL,
//				new File(logfile));
		String filename = "/home/dreis/ANACOM.ISO";
		String logfile = "/home/dreis/testrecords/hu/error/log.txt";

		try {
			Iterator<RecordRepox> iterator = extract.getIterator(null, new File(filename), CharacterEncoding.UTF_8,
					new File(logfile));
			System.out.println("START");
			int i = 0;
			while (iterator.hasNext()) {
				RecordRepox next = iterator.next();
				if((i++ % 1000) == 0) {
					System.out.println("Records iterated: " + i);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

}
