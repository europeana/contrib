<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="gr.ntua.ivml.mint.persistent.Dataset" %>
<%@ page import="java.util.*" %>
<%@ page import="gr.ntua.ivml.mint.util.*" %>
<%@ page import="com.opensymphony.xwork2.util.TextParseUtil" %>

	<s:set var="portalSchema" value="@gr.ntua.ivml.mint.util.Config@get('euscreen.portal.schema')"/>
	<s:set var="aggregateSchema" value="@gr.ntua.ivml.mint.util.Config@get('euscreen.aggregate.schema')"/>
	<!--  two way publication 
	      publish on portal and
	      publish on oai
	      If we dont support automated crosswalks, this should be simple 
	-->
	<!--  if schema for europeana is there, with valid items -->
	<s:if test="current.getValidBySchemaName(#aggregateSchema)>0">
	<!--  check publish or unpublish -->
	<s:if test="du.getBySchemaName(#aggregateSchema).isPublished()" >
	<div title="Unpublish from europeana"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=europeanaUnpublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Unpublish from Europeana" }'
		class="items navigable">
		<div class="label">Unpublish from Europeana</div>
	</div>
	</s:if>
	<s:else>
	<div title="Publish to europeana"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=europeanaPublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Publish to Europeana" }'
		class="items navigable">
		<div class="label">Publish to Europeana</div>
	</div>
	</s:else>
	</s:if>
	<!--  if schema for euscreen is there, with valid items -->
	<!--  The code here should be in a java class, but its specific to euscreen
	      and its harder to make project specific view classes, than it is to make
	      project specific jsp's  -->
	<% 
		Dataset ds = (Dataset) request.getAttribute( "du");
		Set<String> schemas = TextParseUtil.commaDelimitedStringToSet(Config.get("euscreen.portal.schema"));
		boolean published = false;
		boolean hasValidItems = false;
		for( String schema: schemas ) { 
			Dataset publishedDs = ds.getBySchemaName( schema );
			// only one should be in here
			if( publishedDs != null ) {
				published |= publishedDs.isPublished();
				hasValidItems |= ( publishedDs.getValidItemCount() > 0 );
			}
		}
		
		if( hasValidItems ) {
			if( published ) {			
	%>
	<div title="Unpublish from portal"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=portalUnpublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Unpublish from Europeana" }'
		class="items navigable">
		<div class="label">Unpublish from Portal</div>
	</div>
	<% } else { %>
	<div title="Publish to portal"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=portalPublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Publish to Portal" }'
		class="items navigable">
		<div class="label">Publish to Portal</div>
		<div class="tail"></div>
	</div>
<% } } %>	
		