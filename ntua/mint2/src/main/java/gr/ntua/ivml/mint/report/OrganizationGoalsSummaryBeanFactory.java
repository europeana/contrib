package gr.ntua.ivml.mint.report;

import gr.ntua.ivml.mint.actions.UrlApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrganizationGoalsSummaryBeanFactory {

	String organizationId;
	Date startDate;
	Date endDate;

	private PublicationDetailsBeanFactory publicationDetailsBeanFactory;
	private TransformationDetailsBeanFactory transformationDetailsBeanFactory;
	private OrgOAIBeanFactory orgoaibeanfactory;

	// OaiPublicationDetailsBeanFactory oaipublicationbeanFactory ;

	public OrganizationGoalsSummaryBeanFactory(String id, Date startdate,
			Date enddate) {
		super();

		this.organizationId = id;
		this.startDate = startdate;
		this.endDate = enddate;

//		this.publicationDetailsBeanFactory = new PublicationDetailsBeanFactory(
//				id, startDate, endDate);
//		this.transformationDetailsBeanFactory = new TransformationDetailsBeanFactory(
//				id, startDate, endDate);
//		this.orgoaibeanfactory = new OrgOAIBeanFactory(id);
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationid(String organizationId) {
		this.organizationId = organizationId;
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

	public PublicationDetailsBeanFactory getPublicationDetailsBeanFactory() {
		return publicationDetailsBeanFactory;
	}

	public void setPublicationDetailsBeanFactory(
			PublicationDetailsBeanFactory publicationDetailsBeanFactory) {
		this.publicationDetailsBeanFactory = publicationDetailsBeanFactory;
	}

	public TransformationDetailsBeanFactory getTransformationDetailsBeanFactory() {
		return transformationDetailsBeanFactory;
	}

	public void setTransformationDetailsBeanFactory(
			TransformationDetailsBeanFactory transformationDetailsBeanFactory) {
		this.transformationDetailsBeanFactory = transformationDetailsBeanFactory;
	}

	public OrgOAIBeanFactory getOrgoaibeanfactory() {
		return orgoaibeanfactory;
	}

	public void setOrgoaibeanfactory(OrgOAIBeanFactory orgoaibeanfactory) {
		this.orgoaibeanfactory = orgoaibeanfactory;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public List<OrganizationGoalsSummaryBean> getOrgGoalBeans() {
		UrlApi api = new UrlApi();
		api.setOrganizationId(this.organizationId);
		JSONObject json = api.listOrganizations();
		JSONArray result = (JSONArray) json.get("result");
		List<OrganizationGoalsSummaryBean> all = new ArrayList<OrganizationGoalsSummaryBean>();
		Iterator it = result.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			String organizationId = jsonObject.get("dbID").toString();
			String organizationName = jsonObject.getString("englishName");
			if (organizationName.equals("NTUA"))
				continue;
			if (organizationName.equals("Old euscreen data")) continue;
			List<OrganizationGoalsSummaryBean> progbeans = new ArrayList<OrganizationGoalsSummaryBean>();
			progbeans = getOrgGoalBeans(organizationId);
			all.addAll(progbeans);
		}

		return all;

	}

	public List<OrganizationGoalsSummaryBean> getOrgGoalBeans(String orgid) {

		UrlApi api = new UrlApi();
		api.setOrganizationId(orgid);
		JSONObject json = api.listOrganizations();
		JSONArray result = (JSONArray) json.get("result");
		JSONObject jsonObject = result.getJSONObject(0);
		return this.getOrgGoalBeans(jsonObject);

	}

	public List<OrganizationGoalsSummaryBean> getOrgGoalBeans(
			JSONObject jsonObject) {

		List<OrganizationGoalsSummaryBean> goals = new ArrayList<OrganizationGoalsSummaryBean>();

		String name = jsonObject.get("englishName").toString();
		String country = jsonObject.get("country").toString();
		String organizationId = jsonObject.get("dbID").toString();
		
		this.populateFactories(organizationId);

		JSONObject targets = new JSONObject();
		JSONArray result = new JSONArray();

		try {
			if (jsonObject.has("targets")) {
				targets = jsonObject.getJSONObject("targets");
				if (targets.has("periods")) {
					result = (JSONArray) targets.get("periods");
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		Integer finaltargetItems = 0;
		Date finalDate = null;

		Integer targetItems = 0;
		Date targetDate = null;

		// Date today = new Date();
		Date today = endDate;

		Iterator it = result.iterator();
		while (it.hasNext()) {
			JSONObject jsObject = (JSONObject) it.next();
			finaltargetItems += (Integer) jsObject.get("count");
			String end = jsObject.get("end").toString();
			try {
				finalDate = new SimpleDateFormat("dd-MM-yyyy").parse(end);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (today.before(finalDate)) {
				targetDate = finalDate;
			}
		}
		it = result.iterator();
		while (it.hasNext()) {
			JSONObject jsObject = (JSONObject) it.next();
			targetItems += (Integer) jsObject.get("count");
			String end = jsObject.get("end").toString();
			try {
				targetDate = new SimpleDateFormat("dd-MM-yyyy").parse(end);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (today.before(targetDate)) {
				break;
			}
		}

		Integer totalTransformedValidItems = transformationDetailsBeanFactory
				.getTransformedItems(organizationId);
		Integer totalPublishedValidItems = publicationDetailsBeanFactory
				.getPublishedItems(organizationId);
		Integer totalOaiPublishedValidItems = orgoaibeanfactory.getItemCount();
		// Integer totalOaiPublishedValidItems =
		// oaipublicationbeanFactory.gettotalValidItemsPublished();

		OrganizationGoalsSummaryBean transformedgoalbean = new OrganizationGoalsSummaryBean(
				"Transformed Valid Items", today, totalTransformedValidItems,
				name, organizationId, "transformed");
		goals.add(transformedgoalbean);

		OrganizationGoalsSummaryBean publishedgoalbean = new OrganizationGoalsSummaryBean(
				"Published Valid Items", today, totalPublishedValidItems, name,
				organizationId, "published");
		goals.add(publishedgoalbean);

		OrganizationGoalsSummaryBean oaipublishedgoalbean = new OrganizationGoalsSummaryBean(
				"OAI Published Items", today, totalOaiPublishedValidItems,
				name, organizationId, "oaipublished");
		goals.add(oaipublishedgoalbean);

		OrganizationGoalsSummaryBean currentGoalbean = new OrganizationGoalsSummaryBean(
				"Current Target Number of Items", targetDate, targetItems,
				name, organizationId, "current goal");
		goals.add(currentGoalbean);

		OrganizationGoalsSummaryBean finalGoalbean = new OrganizationGoalsSummaryBean(
				"Final Target Number of Items", finalDate, finaltargetItems,
				name, organizationId, "final goal");
		goals.add(finalGoalbean);

		return goals;
	}

	public List<OrganizationGoalsSummaryBean> getProjectGoalBeans() {

		Integer projectTransformed = 0;
		Integer projectPublished = 0;
		Integer projectOaiPublished = 0;
		Integer currenttargetItems = 0;
		Integer finaltargetItems = 0;

		// Date today = new Date();
		Date today = endDate;
		Date targetDate = null;
		Date finalDate = null;

		List<OrganizationGoalsSummaryBean> goals = new ArrayList<OrganizationGoalsSummaryBean>();

		List<OrganizationGoalsSummaryBean> result = new ArrayList<OrganizationGoalsSummaryBean>();

		goals = getOrgGoalBeans();
		Iterator it = goals.iterator();

		while (it.hasNext()) {

			OrganizationGoalsSummaryBean goalbean = (OrganizationGoalsSummaryBean) it
					.next();
			if (goalbean.getType().equals("transformed")) {
				projectTransformed += goalbean.getItems();
			} else if (goalbean.getType().equals("published")) {
				projectPublished += goalbean.getItems();
			} else if (goalbean.getType().equals("oaipublished")) {
				if (goalbean.getItems() != null)
					projectOaiPublished += goalbean.getItems();
			} else if (goalbean.getType().equals("current goal")) {
				currenttargetItems += goalbean.getItems();
				targetDate = goalbean.getDate();
			} else if (goalbean.getType().equals("final goal")) {
				finaltargetItems += goalbean.getItems();
				finalDate = goalbean.getDate();
			}
		}

		OrganizationGoalsSummaryBean transformedgoalbean = new OrganizationGoalsSummaryBean(
				"Transformed Valid Items", today, projectTransformed, null,
				null, "transformed");
		result.add(transformedgoalbean);

		OrganizationGoalsSummaryBean publishedgoalbean = new OrganizationGoalsSummaryBean(
				"Published Valid Items", today, projectPublished, null, null,
				"published");
		result.add(publishedgoalbean);

		OrganizationGoalsSummaryBean oaipublishedgoalbean = new OrganizationGoalsSummaryBean(
				"OAI Published Valid Items", today, projectOaiPublished, null,
				null, "oaipublished");
		result.add(oaipublishedgoalbean);

		OrganizationGoalsSummaryBean currentGoalbean = new OrganizationGoalsSummaryBean(
				"Current Target Number of Items", targetDate,
				currenttargetItems, null, null, "current goal");
		result.add(currentGoalbean);

		OrganizationGoalsSummaryBean finalGoalbean = new OrganizationGoalsSummaryBean(
				"Final Target Number of Items", finalDate, finaltargetItems,
				null, null, "final goal");
		result.add(finalGoalbean);

		return result;
	}
	
	protected void populateFactories(String organizationId) {
		transformationDetailsBeanFactory = new TransformationDetailsBeanFactory(
				organizationId, startDate, endDate);
		publicationDetailsBeanFactory = new PublicationDetailsBeanFactory(
				organizationId, startDate, endDate);
		orgoaibeanfactory = new OrgOAIBeanFactory(organizationId);
	}

}
