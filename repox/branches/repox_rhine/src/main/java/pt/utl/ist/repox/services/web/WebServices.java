package pt.utl.ist.repox.services.web;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.Urn;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public interface WebServices {

	/**
	 * Retrieves Record with urn and writes it to OutputStream.
	 *
	 * @param out
	 * @param urn
	 * @throws Exception
	 */
	public abstract void writeRecord(OutputStream out, Urn recordUrn) throws Exception;

	/**
	 * Retrieves the Data Source list.
	 *
	 * @param out
	 * @throws DocumentException
	 */
	public void writeDataSources(OutputStream out) throws DocumentException, UnsupportedEncodingException, IOException;

	/**
	 * Saves a of Record with representation recordString in DataSource with id dataSourceId and writes the result of the
	 * operation to OutputStream.
	 *
	 * @param out
	 * @param dataSourceId
	 * @param recordString
	 * @param recordId
	 * @throws Exception
	 */
	public abstract void writeSaveRecord(OutputStream out, String dataSourceId, String recordString, String recordId) throws Exception;

	/**
	 * Marks Record with urn recordId
	 * of the operation to OutputStream.
	 *
	 * @param out
	 * @param recordId
	 * @throws Exception
	 */
	public abstract void writeDeleteRecord(OutputStream out, String recordId) throws Exception;

	/**
	 * Permanently removes the Record with urn recordId
	 * of the operation to OutputStream.
	 *
	 * @param out
	 * @param recordId
	 * @throws Exception
	 */
	public abstract void writeEraseRecord(OutputStream out, String recordId) throws Exception;
}