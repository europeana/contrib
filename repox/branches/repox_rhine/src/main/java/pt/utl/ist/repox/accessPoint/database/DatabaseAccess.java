package pt.utl.ist.repox.accessPoint.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAccess {
	public abstract String getVarType(Class classOfValue);
	public abstract Connection openDbConnection();
	public abstract void createTableIndexes(Connection con, String idType, String table, String valueType, boolean indexValue);
	public abstract void deleteTable(Connection con, String table) throws SQLException;
	public abstract String renameTableString(String oldTableName, String newTableName);
	public abstract String renameIndexString(String oldIndexName, String newIndexName);
}
