package pt.utl.ist.repox.statistics;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public interface StatisticsManager {
    public RepoxStatistics generateStatistics() throws IOException, DocumentException, SQLException;

    public void saveStatistics(RepoxStatistics repoxStatistics) throws IOException;

    public RepoxStatistics loadRepoxStatistics() throws IOException, DocumentException, SQLException, ParseException;
}
