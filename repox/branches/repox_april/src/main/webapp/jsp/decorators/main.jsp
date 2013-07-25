<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<link rel="stylesheet" type="text/css" title="telplus" href="${pageContext.request.contextPath}/jsp/styles/telplus.css" />
<!--  <link rel="alternate stylesheet" type="text/css" title="green" href="${pageContext.request.contextPath}/jsp/styles/styles.css" /> -->

<html>
    <head>
        <title><decorator:title /></title>
        <decorator:head />
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/jsp/images/repox-logo-16.png"/>
        
		<!-- Jquery Stuff -->
        <link type="text/css" href="${pageContext.request.contextPath}/jsp/javascript/jquery/css/base/ui.all.css" rel="stylesheet" />

     	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/ui.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/ui.draggable.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/ui.resizable.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/ui.dialog.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/ui.datepicker.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/effects.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/ui/effects.highlight.js"></script>
		
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/external/bgiframe/jquery.bgiframe.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/external/ajaxQueue/jquery.ajaxQueue.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/external/thickbox/thickbox-compressed.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/external/autocomplete/jquery.autocomplete.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/jquery/external/numeric/jquery.numeric.pack.js"></script>
		
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsp/javascript/jquery/external/autocomplete/jquery.autocomplete.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsp/javascript/jquery/external/thickbox/thickbox.css" />
		
		<script type="text/javascript">
			var pageContext = '${pageContext.request.contextPath}';
				
			$(function() {
				
				/*
				RETURN FORMAT: 	dp: "type"|"dp.id"|"dp.name"|"dp.descr"
								ds: "type"|"dp.id"|"dp.name"|"ds.descr"|"ds.id"
				*/

				function formatItem(row) {
					var resultString;
					if(row[0] == 'dp') {
						resultString = row[2] + ' - ' + row[3];
					}
					else if(row[0] == 'ds') {
						resultString = row[2] + ' : ' + row[4] + ' - ' + row[3];
					}
					
					return resultString;
				}
				
				function formatResult(row) {
					return formatItem(row).replace(/(<.+?>)/gi, '');
				}

				$("#suggest").autocomplete('${pageContext.request.contextPath}/search', {
					width: 400,
					matchContains: true,
					formatItem: formatItem,
					formatResult: formatResult
				}).result(function(event, row) {
					var newLocation = '${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?dataProviderId='
						+ row[1]
			      	if(row[0] == 'ds') {
			      		newLocation = newLocation + '&dataSourceId=' + row[4];
					}
					location.href = newLocation;
				});
			});
		</script>
    </head>

    <body>
		<!-- header start -->
		<div class="navigation">
			<a href="${pageContext.request.contextPath}">
				<img style="text-decoration: none; border: none;" src="${pageContext.request.contextPath}/jsp/images/repox-logo-16.png" />
				<fmt:message key="menu.homePage"/>
			</a>
			
			<a href="${pageContext.request.contextPath}/scheduler/Scheduler.action"><fmt:message key="menu.tasks"/></a>
			<a href="${pageContext.request.contextPath}/statistics/Statistics.action"><fmt:message key="menu.statistics"/></a>
			<a href="${pageContext.request.contextPath}/mapMetadata/MapMetadata.action"><fmt:message key="menu.mapMetadata"/></a>
			<a href="${pageContext.request.contextPath}/jsp/testOAI-PMH.jsp">OAI-PMH Tests</a>
<!--  		<a href="${pageContext.request.contextPath}/jsp/REST.jsp">REST Tests</a> -->
			<a href="${pageContext.request.contextPath}/documentation/index.html"><fmt:message key="menu.docs"/></a>
			
			<c:choose>
	        	<c:when test="${!empty param.lang}">
		            <fmt:setLocale value="${param.lang}" scope="session" />
		            <c:set var="lang" value="${param.lang}" scope="session"/>
	        	</c:when>
	        	<c:otherwise>
	        		<fmt:setLocale value="${lang}" scope="session" />
	        	</c:otherwise>
        	</c:choose>

			<div class="searchBar">Go to: <input type="text" id='suggest' style="width: 150px;" /></div>
		
<!--     
			<div class="language">		
				<form onsubmit="return true;" action="${pageContext.request.contextPath}" method="get">
			    	<fmt:message key="lang.change"/>:
			    	
					<select name="lang" onchange="this.form.submit();">
						<c:set var="selected" scope="page">selected="selected"</c:set>
						<option value="en" ${(lang == 'en') ? selected : ''}><fmt:message key="lang.en"/></option>
						<option value="pt" ${(lang == 'pt') ? selected : ''}><fmt:message key="lang.pt"/></option>
					</select>
				</form>
			</div>
 -->			
		</div>
		<!-- header finish -->
		
		<h1><decorator:title /></h1>
        <div><decorator:body /></div>

		<!-- footer -->
    </body>
</html>