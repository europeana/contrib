<%@ taglib prefix="s" uri="/struts-tags" %>


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
	<s:if test="current.getValidBySchemaName(#portalSchema)>0">
	<s:if test="du.getBySchemaName(#portalSchema).isPublished()" >
	<div title="Unpublish from portal"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=portalUnpublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Unpublish from Europeana" }'
		class="items navigable">
		<div class="label">Unpublish from Portal</div>
	</div>
	</s:if>
	<s:else>
	<div title="Publish to portal"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish?cmd=portalPublish&datasetId=<s:property value='uploadId'/>", "kTitle":"Publish to Portal" }'
		class="items navigable">
		<div class="label">Publish to Portal</div>
		<div class="tail"></div>
	</div>
	</s:else>
	</s:if>
	
	<!--  link to modified item browser page, that allows to send and remove items from portal -->
	<s:if test="current.getValidBySchemaName(#portalSchema)>0">
	<div title="Portal status"
	data-load='{"kConnector":"html.page", "url":"EuscreenPublish.action?cmd=portalState&datasetId=<s:property value='uploadId'/>", "kTitle":"Set portal visibility" }'
		class="items navigable">
		<div class="label">Items Portal status</div>
		<div class="tail"></div>
	</div>
	</s:if>
		