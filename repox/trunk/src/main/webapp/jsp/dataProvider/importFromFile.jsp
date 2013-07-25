<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="dataProvider.importFromFile" />
		</title>
		<script>
			
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<br />
		<stripes:form action="/dataProvider/ImportFromFile.action" acceptcharset="UTF-8">
			<fieldset>
				<legend><fmt:message key="dataProvider.importFromFile" /></legend>
				
				<p>
					<stripes:file name="dataProvidersFile"/>
					<stripes:submit name="submitFile"><fmt:message key="common.upload" /></stripes:submit>
		    	</p>
			</fieldset>
		</stripes:form>
				
	</body>
</html>
