package pt.utl.ist.repox.web.action.aggregator;

import net.sourceforge.stripes.action.*;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class ViewAggregatorActionBean  extends RepoxActionBean {
    private static final Logger log = Logger.getLogger(ViewAggregatorActionBean.class);

    private AggregatorRepox aggregator;

    public AggregatorRepox getAggregator() {
        return aggregator;
    }

    public void setAggregator(AggregatorRepox aggregatorRepox) {
        this.aggregator = aggregatorRepox;
    }

    public ViewAggregatorActionBean() {
        super();
    }

    @DefaultHandler
    public Resolution view() throws DocumentException, IOException {
        String aggregatorId = context.getRequest().getParameter("aggregatorId");
        if(aggregatorId == null) {
            aggregatorId = (String) context.getRequest().getAttribute("aggregatorId");
        }
        return view(aggregatorId);
    }

    private Resolution view(String aggregatorId) throws DocumentException, IOException {
        aggregatorId = URLDecoder.decode(aggregatorId, "UTF-8");
        aggregator = context.getRepoxManager().getDataManager().getAggregator(aggregatorId);

        if(aggregator == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }


        String resolutionPage = "/jsp/aggregator/view.jsp";
        if(aggregatorId != null) {
            resolutionPage += "?aggregatorId=" + aggregatorId;
        }

        return new ForwardResolution(resolutionPage);

    }


    private DataSource getDataSourceFromParameter() throws DocumentException, IOException {
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        if(dataSourceId == null) {
            dataSourceId = (String) context.getRequest().getAttribute("dataSourceId");
        }

        DataSource dataSource = context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
        return dataSource;
    }



    private Resolution returnNotFound() {
        return new StreamingResolution("text/html") {
            @Override
            public void stream(HttpServletResponse response) throws IOException {
                response.sendError(404);
            }
        };
    }

}