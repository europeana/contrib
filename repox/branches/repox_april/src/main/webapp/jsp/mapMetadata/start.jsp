<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="mapMetadata" />
		</title>
		<script>
			function viewMapping(mappingId)
			{
				var allMappings = new Array();
				<c:if test="${fn:length(actionBean.allMetadataTransformations) > 0}">
					<c:forEach var="currentTransformation" items="${actionBean.allMetadataTransformations}">
						allMappings.push('${currentTransformation.id}');
					</c:forEach>
					
					for( var i = 0 ; i < ${fn:length(actionBean.allMetadataTransformations)} ; i++) {
						var currentMapping = document.getElementById(allMappings[i]);
						currentMapping.style.display = 'none';
					}
				</c:if>
				
				var mappingToShow = document.getElementById(mappingId);
				if(mappingToShow != null) {
					mappingToShow.style.display = '';
				}
			}
			
			function deleteMapping(confirmationMessage, metadataTransformationId) {
				var Location = '${pageContext.request.contextPath}' + '/mapMetadata/MapMetadata.action?delete=delete&metadataTransformation.id=' + metadataTransformationId;
				var answer = confirm (confirmationMessage);
				if (answer) {
					location.href = Location;
				}
				else {
					
				}
			}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<br />
		<fieldset>
			<legend><fmt:message key="mapMetadata.mapVisually" /></legend>
			<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
				<fmt:message key="mapMetadata.submitSample" />
				<br /><br />
				<stripes:file name="sampleFile1"/>
				<stripes:submit name="submitSampleData"><fmt:message key="common.upload" /></stripes:submit>
			</stripes:form>
		</fieldset>
		
		<br /><br />
		<fieldset>
			<legend><fmt:message key="mapMetadata.submitTransformation" /></legend>
		
			<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
				<p>
					<label for="metadataTransformation.id"><fmt:message key="metadataTransformation.identifier" /><em>*</em> </label>
	    			<input type="text" name="metadataTransformation.id" />
		    	</p>
				<p>
	    			<label for="metadataTransformation.description"><fmt:message key="metadataTransformation.description" /><em>*</em> </label>
		    		<input type="text" name="metadataTransformation.description" />
		    	</p>
				<p>
	    			<label for="metadataTransformation.sourceFormat"><fmt:message key="metadataTransformation.sourceFormat" /><em>*</em> </label>
		    		<input type="text" name="metadataTransformation.sourceFormat" />
		    	</p>
				<p>
	    			<label for="metadataTransformation.destinationFormat"><fmt:message key="metadataTransformation.destinationFormat" /><em>*</em> </label>
		    		<input type="text" name="metadataTransformation.destinationFormat" />
		    	</p>
		    	<p>
	    			<label for="sampleFile1"><fmt:message key="metadataTransformation.transformationFile" /><em>*</em> </label>
		    		<stripes:file name="sampleFile1"/>
		    	</p>
		    	
		    	
   				<stripes:submit name="submitXsltFile"><fmt:message key="common.save" /></stripes:submit>
		   	</stripes:form>
   		</fieldset>
	   	
		<c:if test="${fn:length(actionBean.allMetadataTransformations) > 0}">
			<br /><br />
			<fieldset>
				<legend>
					<fmt:message key="mapMetadata.existing" />
					<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
						<stripes:select name="tagGroup.commonXpath" onchange="javascript:viewMapping(this.value)">
							<stripes:option label="-" value="" />
							<c:forEach var="currentTransformation" items="${actionBean.allMetadataTransformations}">
								<stripes:option label="${currentTransformation.id}" value="${currentTransformation.id}" />
							</c:forEach>
						</stripes:select>
					</stripes:form>
				</legend>
				<c:forEach var="currentTransformation" items="${actionBean.allMetadataTransformations}">
					<span id="${currentTransformation.id}" style="display: none;">
						<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
							<input type="hidden" name="metadataTransformation.id" value="${currentTransformation.id}" />
							
							<p>
								<label for="metadataTransformation.id"><fmt:message key="metadataTransformation.identifier" /></label>
				    			&nbsp; ${currentTransformation.id}
				    			
				    			<c:if test="${currentTransformation.editable}">
							    	<stripes:submit name="edit"><fmt:message key="common.edit" /></stripes:submit>
						    	</c:if>
						    	
						    	<c:set var="confirmationMessageDataProvider"><fmt:message key="metadataTransformation.delete.confirmationMessage" /></c:set>
						    	<stripes:button onclick="javascript:deleteMapping('${confirmationMessageDataProvider}', '${currentTransformation.id}');" name="delete"><fmt:message key="common.delete" /></stripes:button>
					    	</p>
							<p>
				    			<label for="metadataTransformation.description"><fmt:message key="metadataTransformation.description" /></label>
					    		&nbsp; ${currentTransformation.description}
					    	</p>
							<p>
				    			<label for="metadataTransformation.sourceFormat"><fmt:message key="metadataTransformation.sourceFormat" /></label>
					    		&nbsp; ${currentTransformation.sourceFormat}
					    	</p>
							<p>
				    			<label for="metadataTransformation.destinationFormat"><fmt:message key="metadataTransformation.destinationFormat" /></label>
					    		&nbsp; ${currentTransformation.destinationFormat}
					    	</p>
					    	<p>
				    			<label for="downloadXslt"><fmt:message key="metadataTransformation.transformationFile" /></label>
					    		<stripes:submit name="downloadXslt"><fmt:message key="common.download" /></stripes:submit>
					    	</p>
					    	
		   				</stripes:form>
		   			</span>
		    	</c:forEach>
   			</fieldset>
		</c:if>
	</body>
</html>
