package pt.utl.ist.repox.data.dataSource;

import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.io.File;
import java.util.Iterator;


public interface FileExtractStrategy {

	/**
	 * True if this file extracting strategy only for XML files, false otherwise.
	 */
	public abstract boolean isXmlExclusive();

	/**
	 * Returns an Iterator of records.
	 */
	public abstract Iterator<RecordRepox> getIterator(DataSource dataSource, File file, CharacterEncoding characterEncoding,
			File logFile) throws Exception;
}
