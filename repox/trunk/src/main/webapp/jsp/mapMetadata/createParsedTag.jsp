<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="mapMetadata.result" />
		</title>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
			<fieldset>
				<legend><fmt:message key="mapMetadata.createParsedTag" /></legend>
	    		<p>
	    			<label for="newParsedTag.xpath"><fmt:message key="common.xpath" /><em>*</em> </label>
						<stripes:text name="newParsedTag.xpath" />
				</p>
	    		<p>
	    			<label for="newParsedTag.name"><fmt:message key="common.name" /> </label>
						<stripes:text name="newParsedTag.name" />
				</p>
	    		<p>
	    			<label for="newParsedTag.description"><fmt:message key="common.description" /> </label>
						<stripes:text name="newParsedTag.description" />
				</p>
				
				<p class="right">
					<stripes:submit name="cancelToMappings" ><fmt:message key="common.cancel" /></stripes:submit>
					<stripes:submit name="submitNewParsedTag"><fmt:message key="common.save" /></stripes:submit>
				</p>
			</fieldset>
		</stripes:form>
	</body>
</html>
