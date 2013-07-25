package pt.utl.ist.repox.accessPoint.database;

import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;

public class AccessPointManagerFactory {
	public static AccessPointsManager getInstance(RepoxConfiguration configuration) {
		if(configuration.getDatabaseDriverClassName() != null
				&& configuration.getDatabaseDriverClassName().equals(org.apache.derby.jdbc.EmbeddedDriver.class.getName())) {
			return new AccessPointsManagerSql(new DatabaseAccessDerby(configuration));
		} else if(configuration.getDatabaseDriverClassName() != null
				&& configuration.getDatabaseDriverClassName().equals(org.postgresql.Driver.class.getName())) {
			return new AccessPointsManagerSql(new DatabaseAccessPostgresql(configuration));
		}
        /* TODO
        todo AccessPointsManagerSip
        else if(configuration.getDatabaseDriverClassName() != null
				&& configuration.getDatabaseDriverClassName().equals(org.postgresql.Driver.class.getName())) {
			return new AccessPointsManagerSql(new DatabaseAccessPostgresql(configuration));
		}
		*/

		throw new UnsupportedOperationException("Database driver: " + configuration.getDatabaseDriverClassName() + " unsupported.");
	}
}
