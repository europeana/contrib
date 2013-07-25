package pt.utl.ist.repox.web.action.dataProvider;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.*;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataProviderManager;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.RepoxActionBean;
import pt.utl.ist.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreateEditDataProviderActionBean extends RepoxActionBean {
	private static final Logger log = Logger.getLogger(CreateEditDataProviderActionBean.class);

	private String dataProviderId;
	@ValidateNestedProperties({
	    @Validate(field = "name", required = true, on = {"submitDataProvider"})
	    /*, @Validate(field = "description", required = true, on = {"submitDataProvider"}) */
	  })
	private DataProvider dataProvider;
	private Boolean editing;
	private FileBean logoFile;

	public String getDataProviderId() {
		return dataProviderId;
	}

	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public Boolean getEditing() {
		return editing;
	}

	public void setEditing(Boolean editing) {
		this.editing = editing;
	}

	public FileBean getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(FileBean logoFile) {
		this.logoFile = logoFile;
	}

	@DefaultHandler
	public Resolution preEdit() throws DocumentException, IOException {
		if(dataProviderId != null && !dataProviderId.isEmpty()) {
			editing = true;
			dataProvider = context.getRepoxManager().getDataProviderManager().getDataProvider(dataProviderId);
		}

		FlashScope.getCurrent(getContext().getRequest(), true).put(this);
		return new ForwardResolution("/jsp/dataProvider/create.jsp");
	}

	public Resolution cancel() {
//		context.getMessages().add(new LocalizableMessage("common.cancel.message"));
		if(dataProvider == null && (context.getRequest().getParameter("dataProviderId") == null
				|| context.getRequest().getParameter("dataProviderId").isEmpty())) {
			return new RedirectResolution("/Homepage.action");
		}
		return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + context.getRequest().getParameter("dataProviderId"));
	}


	@ValidationMethod(on = {"submitDataProvider"})
	public void validataDataProvider(ValidationErrors errors) throws DocumentException, IOException {
		DataProviderManager dataProviderManager = context.getRepoxManager().getDataProviderManager();
		if(editing == null || !editing) {
			List<DataProvider> dataProviders = dataProviderManager.loadDataProviders();
			for (DataProvider currentDataProvider : dataProviders) {
				if(currentDataProvider.getName().equals(dataProvider.getName())) {
					errors.add("dataProvider", new LocalizableError("error.dataProvider.existingName", dataProvider.getName()));
				}
			}

			if (logoFile != null) {
				if(!logoFile.getFileName().endsWith(".jpg")
						&& !logoFile.getFileName().endsWith(".gif")
						&& !logoFile.getFileName().endsWith(".png")) {
					errors.add("dataProvider", new LocalizableError("error.dataProvider.invalidLogoExtension"));
				}
			}
			/*
			if(!dataProviderManager.isIdValid(dataProvider.getId())) {
				errors.add("dataSource", new LocalizableError("error.dataProvider.invalidId", dataProvider.getId()));
			}
			*/
		}
		else if(!dataProviderId.equals(dataProvider.getId())) { //ID Changed
			if(dataProviderManager.getDataProvider(dataProvider.getId()) != null) {
				errors.add("dataProvider", new LocalizableError("error.dataSource.existingId", dataProvider.getId()));
			}
		}

	}

	public Resolution submitDataProvider() throws DocumentException, IOException {
		DataProviderManager dataProviderManager = context.getRepoxManager().getDataProviderManager();
		if(dataProviderId == null) {
			String generatedId = DataProvider.generateId(dataProvider.getName());
			dataProvider.setId(generatedId);
		}
		else {
			dataProvider.setId(dataProviderId);
		}
		DataProvider loadedDataProvider = dataProviderManager.getDataProvider(dataProviderId);

		if(logoFile != null) { // Logo file was sent
			String outputFilename = dataProvider.getId() + logoFile.getFileName().substring(logoFile.getFileName().lastIndexOf("."));
			File outputLogoFile = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getLogosDir(), outputFilename);
			logoFile.save(outputLogoFile);
			dataProvider.setLogo(outputFilename);
		}
		else if(loadedDataProvider != null && loadedDataProvider.getLogoFile() != null) { // Logo file exists
			System.out.println("* PATH LOGO: " + loadedDataProvider.getLogoFile().getAbsolutePath());
			
			File logoFile = loadedDataProvider.getLogoFile();
			String outputFilename;
			String imageExtension = logoFile.getName().substring(logoFile.getName().lastIndexOf("."));
			if(dataProvider.getId().equals(dataProviderId)) {
				outputFilename = dataProvider.getId() + imageExtension;
			}
			else {
				String oldOutputFilename = dataProviderId + imageExtension;
				outputFilename = dataProvider.getId() + imageExtension;
				File oldOutputLogoFile = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getLogosDir(), oldOutputFilename);
				File outputLogoFile = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getLogosDir(), outputFilename);
				FileUtil.copyFile(oldOutputLogoFile, outputLogoFile);
				oldOutputLogoFile.delete();
			}
			dataProvider.setLogo(outputFilename);
		}

		if(editing != null && editing == true) {
			dataProvider.setDataSources(loadedDataProvider.getDataSources());

			dataProviderManager.updateDataProvider(dataProvider, dataProviderId);
			log.debug("Updated existing DataProvider with id " + dataProviderId + " to id " + dataProvider.getId());
			context.getMessages().add(new LocalizableMessage("dataProvider.edit.success", dataProvider.getName(), dataProvider.getId(), context.getServletContext().getContextPath()));
		}
		else {
			dataProviderManager.saveDataProvider(dataProvider);
			log.debug("Saved new DataProvider with id " + dataProvider.getId());
			context.getMessages().add(new LocalizableMessage("dataProvider.create.success", dataProvider.getName(), dataProvider.getId(), context.getServletContext().getContextPath()));
		}

//		return new RedirectResolution("/Homepage.action");

		return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataProvider.getId());
	}


	public Resolution deleteLogoFile() throws DocumentException, IOException {
		DataProviderManager dataProviderManager = context.getRepoxManager().getDataProviderManager();
		DataProvider dataProvider = dataProviderManager.getDataProvider(dataProviderId);

		File logoFile = dataProvider.getLogoFile();
		if(logoFile != null) { // Logo file was sent
			logoFile.delete();
			dataProvider.setLogo(null);
			dataProviderManager.saveDataProvider(dataProvider);
			context.getMessages().add(new LocalizableMessage("dataProvider.edit.success", dataProvider.getId(), context.getServletContext().getContextPath()));
			return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataProvider.getId());
		}
		else {
			return new ForwardResolution("/jsp/common/unknownResource.jsp");
		}
	}

}