package gr.ntua.ivml.mint.report;

import gr.ntua.ivml.mint.actions.UrlApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrganizationStatisticsBeanFactory {

	private DataUploadDetailsBeanFactory datauploadDetailsFactory;
	private TransformationDetailsBeanFactory transformationDetailsBeanFactory;
	private PublicationDetailsBeanFactory publicationDetailsBeanFactory;
	private OrgOAIBeanFactory orgOaiBeanFactory;

	private Date startDate;
	private Date endDate;

	public OrganizationStatisticsBeanFactory(Date startDate, Date endDate) {
		super();

		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<OrganizationStatisticsBean> getOrgStatisticsBeans() {

		UrlApi api = new UrlApi();
		JSONObject json = api.listOrganizations();
		return getOrgStatisticsBeans(json);

	}

	public List<OrganizationStatisticsBean> getOrgStatisticsBeans(
			JSONObject json) {
		List<OrganizationStatisticsBean> organizations = new ArrayList<OrganizationStatisticsBean>();
		JSONArray result = (JSONArray) json.get("result");

		Iterator it = result.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			String organizationId = jsonObject.get("dbID").toString();
			String name = jsonObject.get("englishName").toString();
			if (name.equals("Old euscreen data")) continue;
			String country = jsonObject.get("country").toString();
			String publishAllowed = jsonObject.get("publishAllowed").toString();

			this.populateFactories(organizationId);

		//	List<DataUploadDetailsBean> uploadsbeanCollection = null;
		//	uploadsbeanCollection = datauploadDetailsFactory.getUploads(organizationId);

			List<TransformationDetailsBean> transformationsbeanCollection = null;
			transformationsbeanCollection = transformationDetailsBeanFactory
					.getTransformations(organizationId);

			List<PublicationDetailsBean> publicationsbeanCollection = null;
			publicationsbeanCollection = publicationDetailsBeanFactory
					.getPublications(organizationId);

		/*	Iterator ite = uploadsbeanCollection.iterator();
			int uploadedItems = 0;
			while (ite.hasNext()) {
				DataUploadDetailsBean uploadbean = (DataUploadDetailsBean) ite
						.next();
				if (uploadbean.getItemCount() == -1) {
					continue;
				}
				uploadedItems += uploadbean.getItemCount();
			}
*/
			int uploadedItems = datauploadDetailsFactory.getUploadedItems(organizationId);
			int transformedItems = transformationDetailsBeanFactory.getTransformedItems(organizationId);
			int publishedItems = publicationDetailsBeanFactory.getPublishedItems(organizationId);
			int oaicommited = orgOaiBeanFactory.getItemCount();
			
//			Iterator ite = transformationsbeanCollection.iterator();
//			int transformedItems = 0;
//			while (ite.hasNext()) {
//				TransformationDetailsBean transformationbean = (TransformationDetailsBean) ite
//						.next();
//
//				if (transformationbean.getValidItems() == -1) {
//					continue;
//				}
//				transformedItems += transformationbean.getValidItems();
//			}
//
//			ite = publicationsbeanCollection.iterator();
//			int publishedItems = 0;
//			while (ite.hasNext()) {
//				PublicationDetailsBean publicationbean = (PublicationDetailsBean) ite
//						.next();
//				if (publicationbean.getItemCount() == -1) {
//					continue;
//				}
//				publishedItems += publicationbean.getItemCount();
//			}

//			int numberofpublications = publicationsbeanCollection.size();
//			int numberoftransformations = transformationsbeanCollection.size();
//			int numberofuploads = uploadsbeanCollection.size();


			OrganizationStatisticsBean organizationStatisticsbean = new OrganizationStatisticsBean(
					name, country, uploadedItems, transformedItems,
					publishedItems, oaicommited);

			organizations.add(organizationStatisticsbean);
		}

		Collections.sort(organizations);
		return organizations;

	}

	protected void populateFactories(String organizationId) {
		transformationDetailsBeanFactory = new TransformationDetailsBeanFactory(
				organizationId, startDate, endDate);
		publicationDetailsBeanFactory = new PublicationDetailsBeanFactory(
				organizationId, startDate, endDate);
		datauploadDetailsFactory = new DataUploadDetailsBeanFactory(
				organizationId, startDate, endDate);
		orgOaiBeanFactory = new OrgOAIBeanFactory(organizationId);
	}

}
