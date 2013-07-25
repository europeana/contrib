<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="mapMetadata.result" />
		</title>
		
		<script type="text/javascript">
			function enableExtras() {
				document.getElementById('otherDestinationFormat').readOnly = false;
				showObject('rootElement');
				showObject('namespaces');
			}
	
			function disableExtras() {
				document.getElementById('otherDestinationFormat').readOnly = true;
				hideObject('rootElement');
				hideObject('namespaces');
			}

			function showObject(id) {
				document.getElementById(id).style.display = '';
			}
	
			function hideObject(id) {
				document.getElementById(id).style.display = 'none';
			}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
			<c:if test="${actionBean.editing}">
				<stripes:hidden name="metadataTransformation.id" value="${actionBean.metadataTransformation.id}" />
			</c:if>
	   		<fieldset>
				<legend><fmt:message key="mapMetadata.data" /></legend>
				<p>
					<c:choose>
						<c:when test="${actionBean.editing}">
							<label for="metadataTransformation.id"><fmt:message key="metadataTransformation.identifier" /> </label>
							${actionBean.metadataTransformation.id}
						</c:when>
						<c:otherwise>
							<label for="metadataTransformation.id"><fmt:message key="metadataTransformation.identifier" /><em>*</em> </label>
			    			<stripes:text name="metadataTransformation.id" />
						</c:otherwise>
					</c:choose>
		    	</p>
				<p>
	    			<label for="metadataTransformation.description"><fmt:message key="metadataTransformation.description" /><em>*</em> </label>
		    		<stripes:text name="metadataTransformation.description" />
		    	</p>
				<p>
	    			<label for="metadataTransformation.sourceFormat"><fmt:message key="metadataTransformation.sourceFormat" /><em>*</em> </label>
		    		<stripes:text name="metadataTransformation.sourceFormat" />
		    	</p>

				<p>
	    			<label for="metadataTransformation.destinationFormat"><fmt:message key="metadataTransformation.destinationFormat" /><em>*</em> </label>
	    			<stripes:radio onclick="disableExtras()" name="metadataTransformation.destinationFormat" value="tel"/>tel
		    	</p>
				<p>
	    			<label>&nbsp;</label>
	    			<stripes:radio  onclick="disableExtras()" name="metadataTransformation.destinationFormat" value="oai_dc"/>oai_dc
		    	</p>
		    	
		    	<c:choose>
			    	<c:when test="${actionBean.metadataTransformation.destinationFormat != 'other'}">
			    		<c:set var="otherDisplay" value="none" />
			    	</c:when>
			    	<c:otherwise><c:set var="otherDisplay" value="block" /></c:otherwise>
		    	</c:choose>
				<p>
	    			<label>&nbsp;</label>
	    			<stripes:radio onclick="enableExtras()" name="metadataTransformation.destinationFormat" value="other"/>other
	    			<c:if test="${actionBean.metadataTransformation.destinationFormat == 'other'}">
	    				<stripes:text id="otherDestinationFormat" name="otherDestinationFormat" size="100px" />
					</c:if>
					<c:if test="${actionBean.metadataTransformation.destinationFormat != 'other'}">
	    				<stripes:text id="otherDestinationFormat" readonly="true" name="otherDestinationFormat" size="100px" />
					</c:if>
		    	</p>
				<p id="rootElement" style="display: ${otherDisplay}">
					<label for="rootElement"><fmt:message key="metadataTransformation.rootElement" /></label>
  					<stripes:text name="rootElement" />
				</p>
				<p id="namespaces" style="display: ${otherDisplay}">
					<label for="namespaces"><fmt:message key="metadataTransformation.namespaces" /></label>
		  			<stripes:textarea name="namespaces" />
				</p>

		    	<p>
		    		<label>&nbsp;</label>
		    		<stripes:submit name="submitConfirmation"><fmt:message key="common.save" /></stripes:submit>
		    	</p>
   			</fieldset>
   			
			<table class="metadataMap">
	   			<tr>
	   				<th>
	   					<b><fmt:message key="mapMetadata.sample" /></b>
	   					<br /><fmt:message key="common.new" />:
						<stripes:file name="sampleFile1"/>
						<stripes:submit name="submitNewSampleFile"><fmt:message key="common.upload" /></stripes:submit>
	   					
	   				</th>
	   				<th><b><fmt:message key="mapMetadata.xslt" /></b></th>
	   				<th><b><fmt:message key="mapMetadata.transformationResult" /></b></th>
   				</tr>
	   			<tr>
	   				<td>
						<pre><c:out value="${actionBean.sampleXml}" escapeXml="true" /></pre>
		   			</td>
		   			<td>
		   				<pre><c:out value="${actionBean.resultXslt}" escapeXml="true" /></pre>
		   			</td>
		   			<td>
		   				<pre><c:out value="${actionBean.resultTransformation}" escapeXml="true" /></pre>
		   			</td>
	   			</tr>
	   		</table>
		</stripes:form>
	</body>
</html>
