package pt.utl.ist.repox.web.action.statistics;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.statistics.RepoxStatistics;
import pt.utl.ist.repox.statistics.StatisticsManager;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;
import java.sql.SQLException;

public class StatisticsActionBean extends RepoxActionBean {
	private RepoxStatistics repoxStatistics;

	public RepoxStatistics getRepoxStatistics() {
		return repoxStatistics;
	}

	public void setRepoxStatistics(RepoxStatistics repoxStatistics) {
		this.repoxStatistics = repoxStatistics;
	}

	@DefaultHandler
	public Resolution view() throws IOException, DocumentException, SQLException {
		StatisticsManager manager = getContext().getRepoxManager().getStatisticsManager();

		repoxStatistics = manager.generateStatistics();
//		repoxStatistics = manager.loadRepoxStatistics();
		return new ForwardResolution("/jsp/statistics/view.jsp");
	}
	
	public Resolution generate() throws IOException, DocumentException, SQLException {
		StatisticsManager manager = getContext().getRepoxManager().getStatisticsManager();
		
		repoxStatistics = manager.generateStatistics();
		return new ForwardResolution("/jsp/statistics/view.jsp");
	}

	public StatisticsActionBean() {
		super();
	}
}