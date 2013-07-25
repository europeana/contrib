package pt.utl.ist.repox.web.action.aggregator;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;

public class DeleteAggregatorActionBean extends RepoxActionBean {
//	private static final Logger log = Logger.getLogger(DeleteAggregatorActionBean.class);
	String aggregatorId;

    public String getAggregatorId() {
        return aggregatorId;
    }

    public void setAggregatorId(String aggregatorId) {
        this.aggregatorId = aggregatorId;
    }

    @ValidationMethod(on = {"delete"})
	public void validateAggregator(ValidationErrors errors) throws DocumentException, IOException {
		AggregatorRepox aggregatorRepox = context.getRepoxManager().getDataManager().getAggregator(aggregatorId);

		if(aggregatorRepox == null) {
			errors.add("aggregatorId", new LocalizableError("error.aggregator.delete", aggregatorId));
		}
	}

	public Resolution delete() throws DocumentException, IOException, Repox2SipException {
		AggregatorRepox aggregatorRepox = context.getRepoxManager().getDataManager().getAggregator(aggregatorId);

		context.getRepoxManager().getDataManager().deleteAggregator(aggregatorRepox.getId());
		context.getMessages().add(new LocalizableMessage("aggregator.delete.success", aggregatorRepox.getName()));

		return new RedirectResolution("/Homepage.action");
	}
}