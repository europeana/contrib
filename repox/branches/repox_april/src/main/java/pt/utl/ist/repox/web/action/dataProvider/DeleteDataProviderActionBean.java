package pt.utl.ist.repox.web.action.dataProvider;

import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;

public class DeleteDataProviderActionBean extends RepoxActionBean {
//	private static final Logger log = Logger.getLogger(DeleteDataProviderActionBean.class);
	String dataProviderId;

	public String getDataProviderId() {
		return dataProviderId;
	}

	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	@ValidationMethod(on = {"delete"})
	public void validateDataProvider(ValidationErrors errors) throws DocumentException, IOException {
		DataProvider dataProvider = context.getRepoxManager().getDataProviderManager().getDataProvider(dataProviderId);

		if(dataProvider == null) {
			errors.add("dataProviderId", new LocalizableError("error.dataProvider.delete", dataProviderId));
		}
	}

	public Resolution delete() throws DocumentException, IOException {
		DataProvider dataProvider = context.getRepoxManager().getDataProviderManager().getDataProvider(dataProviderId);

		context.getRepoxManager().getDataProviderManager().deleteDataProvider(dataProvider.getId());
		context.getMessages().add(new LocalizableMessage("dataProvider.delete.success", dataProvider.getName()));

		return new RedirectResolution("/Homepage.action");
	}
}