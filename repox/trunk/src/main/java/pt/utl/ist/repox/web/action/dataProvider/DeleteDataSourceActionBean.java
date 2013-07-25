package pt.utl.ist.repox.web.action.dataProvider;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;
import java.net.URLEncoder;

public class DeleteDataSourceActionBean extends RepoxActionBean {
//	private static final Logger log = Logger.getLogger(DeleteDataSourceActionBean.class);
	String dataProviderId;
	String dataSourceId;

	public String getDataProviderId() {
		return dataProviderId;
	}

	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	@ValidationMethod(on = {"delete"})
	public void validateDataSource(ValidationErrors errors) throws DocumentException, IOException {
		DataProvider dataProvider = context.getRepoxManager().getDataManager().getDataProvider(dataProviderId);
		if(dataProvider == null) {
			errors.add("dataProviderId", new LocalizableError("error.dataSource.delete.dataProviderId", dataProviderId));
		}

		DataSource dataSource = dataProvider.getDataSource(dataSourceId);
		if(dataSource == null) {
			errors.add("dataProviderId", new LocalizableError("error.dataSource.delete.dataSourceId", dataSourceId));
		}
	}

	public Resolution delete() throws DocumentException, IOException, Repox2SipException {
		DataProvider dataProvider = context.getRepoxManager().getDataManager().getDataProvider(dataProviderId);
		DataSource dataSource = dataProvider.getDataSource(dataSourceId);

		context.getRepoxManager().getDataManager().deleteDataSource(dataSource.getId());
		dataProvider.getDataSources().remove(dataSource);
		context.getRepoxManager().getDataManager().updateDataProvider(dataProvider, dataProvider.getId());
		context.getMessages().add(new LocalizableMessage("dataSource.delete.success", dataSourceId));

		return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + URLEncoder.encode(dataProviderId, "UTF-8"));
	}
}