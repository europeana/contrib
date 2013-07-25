<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<%@page import="java.net.InetAddress"%>

<html>
	<head>
		<title>OAI-PMH Testing</title>
		<script type="text/javascript">
			function Identify() {
				var Location = getServerURL() + '?verb=Identify';
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function ListSets() {
				var Location = getServerURL() + '?verb=ListSets';
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function GetRecord() {
				var Location = getServerURL() + '?verb=GetRecord';
				Location += addParameter('identifier', document.getForm.identifier.value);
				Location += addParameter('metadataPrefix', document.getForm.metadataPrefix.value);
								 
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function ListMetadataFormats() {
				var Location = getServerURL() + '?verb=ListMetadataFormats';
				Location += addParameter('identifier', document.getForm.identifier.value);
								 
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function ListIdentifiers() {
				var Location = getServerURL() + '?verb=ListIdentifiers';
				Location += getListParameters();
								 
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function ListRecords() {
				var Location = getServerURL() + '?verb=ListRecords';
				Location += getListParameters();
								 
				parent.frames['resultFrame'].location.href = Location; 
				setLabelUrlLocaltion(Location);
			}
			
			function URLencode(sStr) {
				return escape(sStr).replace(/\+/g, '%2B').replace(/\"/g,'%22')
				.replace(/\'/g, '%27').replace(/\//g, '%2F');
			}
			
			function setLabelUrlLocaltion(Location)
			{
				document.getElementById('URLLocation').innerHTML = Location;
			}
			
			function getServerURL()
			{
				return document.getForm.serverURL.value;
			}
			
			function getListParameters() {
				var parameters = '';
				
				if(document.getForm.resumptionToken.value.length != 0) {
					parameters += addParameter('resumptionToken', document.getForm.resumptionToken.value);
				}
				else {
					parameters += addParameter('from', document.getForm.from.value);
					parameters += addParameter('until', document.getForm.until.value);
					parameters += addParameter('metadataPrefix', document.getForm.metadataPrefix.value);
					parameters += addParameter('set', document.getForm.set.value);
				}
				
				return parameters;
			}
			
			function addParameter(parameterName, parameterValue) {
				if(parameterValue.length != 0) {
				var parameterString = '&' + parameterName + '=' +  URLencode(parameterValue);
					return (parameterString);
				}
				else {
					return ('');
				}
			}
		</script>
	</head>
	
	<body onload="javascript:setLabelUrlLocaltion()">
		<div class="testOAI">
			<table><tr>
				<td width="30%" style="vertical-align:top">
					<fieldset>
						<legend>Parameters</legend>
						<form name="getForm" action="/repox/OAIHandler" method="GET">
							<c:set var="serverURL">
								<c:choose>
									<c:when test="${not empty param['serverURL']}">${param['serverURL']}</c:when>
									<c:otherwise>http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/OAIHandler</c:otherwise>
								</c:choose>
							</c:set>
							<table>
								<tr><td class="label">Server URL:</td><td><input type="text" name="serverURL" value="${serverURL}" />
								</td></tr>
								<tr><td class="label">metadataPrefix:</td><td><input type="text" name="metadataPrefix" value="${param['metadataPrefix']}" /></td></tr>
								<tr><td class="label">from:</td><td><input type="text" name="from" /></td></tr>
								<tr><td class="label">until:</td><td><input type="text" name="until"  /></td></tr>
								<tr><td class="label">set:</td><td><input type="text" name="set" value="${param['set']}"/></td></tr>
								<tr><td class="label">identifier:</td><td><input type="text" name="identifier" /></td></tr>
								<tr><td class="label">resumptionToken:</td><td><input type="text" name="resumptionToken" /></td></tr>
							</table>
						</form>
					</fieldset>
					<fieldset>
						<legend>Operations</legend>
						<a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify" target="_blank">&nbsp;?&nbsp;</a>
						<a href="javascript:Identify();">Identify</a>
						<br /><a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListMetadataFormats" target="_blank">&nbsp;?&nbsp;</a>
							  <a href="javascript:ListMetadataFormats();">ListMetadataFormats</a>
						<br /><a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets" target="_blank">&nbsp;?&nbsp;</a>
							  <a href="javascript:ListSets();">ListSets</a>
						<br /><a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers" target="_blank">&nbsp;?&nbsp;</a>
							  <a href="javascript:ListIdentifiers();">ListIdentifiers</a>
						<br /><a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords" target="_blank">&nbsp;?&nbsp;</a>
							  <a href="javascript:ListRecords();">ListRecords</a>
						<br /><a class="help" href="http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord" target="_blank">&nbsp;?&nbsp;</a>
							  <a href="javascript:GetRecord();">GetRecord</a>
					</fieldset>
					
					<br /><br /><br /><br /><br /><br /><br />
					Others: <a href="${pageContext.request.contextPath}/jsp/REST.jsp">Record Operations</a>
				</td>
				<td style="vertical-align:top">
					<!-- <p>URL: <span id="URLLocation"></span></p> -->
						<c:set var="targetURL">
							<c:choose>
								<c:when test="${not empty param['verb']}">${param['serverURL']}?verb=${param['verb']}&metadataPrefix=${param['metadataPrefix']}&set=${param['set']}</c:when>
								<c:otherwise>http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/OAIHandler?verb=Identify</c:otherwise>
							</c:choose>
						</c:set>
					<b>Response:</b>
					<div class="iframe">
						<iframe src="${targetURL}" name="resultFrame" height="500px" width="700px"></iframe>
					</div>
				</td>
			</tr></table>
		</div>
	</body>
</html>
