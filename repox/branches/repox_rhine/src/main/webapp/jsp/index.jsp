<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<c:choose>
	<c:when test="${!empty param.lang}">
		<fmt:setLocale value="${param.lang}" scope="session" />
		<c:set var="lang" value="${param.lang}" scope="session"/>
	</c:when>
	<c:otherwise>
		<fmt:setLocale value="${lang}" scope="session" />
	</c:otherwise>
</c:choose>

<html>
	<head>
		<title><fmt:message key="repox.homePage"/></title>
		
		<script type="text/javascript">
			function toggleView() {
				var normalElement = document.getElementById('normal');
				var extendedElement = document.getElementById('extended');
				
				if(normalElement.style.display != 'none') {
				  normalElement.style.display = 'none';
				  extendedElement.style.display = '';
				 }
				 else {
				 	normalElement.style.display = '';
				  	extendedElement.style.display = 'none';
				 }
			}
		</script>
	</head>

	<body>
		<stripes:errors />
		<stripes:messages />
				
		<div id="normal">
			<c:import url="/jsp/dataProviderList.include.jsp" />
    	</div>

		<br /><br />
	</body>
</html>
