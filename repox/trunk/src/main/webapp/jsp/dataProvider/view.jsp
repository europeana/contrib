<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
	<head>
		<title><fmt:message key="dataProvider" />: ${actionBean.dataProvider.name}</title>

		<script type="text/javascript">
			function deleteDataProvider(confirmationMessage, dataProviderId) {
				var Location = '/dataProvider/DeleteDataProvider.action?dataProviderId=' + dataProviderId;
				confirmationHandling(confirmationMessage, Location);
			}

			function deleteDataSource(confirmationMessage, dataProviderId, dataSourceId) {
				var Location = '/dataProvider/DeleteDataSource.action?dataProviderId='
								 + dataProviderId + '&dataSourceId=' + dataSourceId;
				confirmationHandling(confirmationMessage, Location);
			}

			function deleteScheduledTask(confirmationMessage, taskId, dataProviderId, dataSourceId) {
				var Location = '/dataProvider/ViewDataProvider.action?deleteScheduledTask&taskId=' + taskId
					 + '&dataProviderId=' + dataProviderId + '&dataSourceId=' + dataSourceId;
				confirmationHandling(confirmationMessage, Location);
			}

			function forceRecordCount(confirmationMessage, dataProviderId, dataSourceId) {
				var Location = '/dataProvider/ViewDataProvider.action?countRecords'
								+ '&dataProviderId=' + dataProviderId + '&dataSourceId=' + dataSourceId;
				confirmationHandling(confirmationMessage, Location);
			}

			function deleteAllRecords(confirmationMessage, dataProviderId, dataSourceId) {
				var Location = '/dataProvider/ViewDataProvider.action?deleteRecords'
								+ '&dataProviderId=' + dataProviderId + '&dataSourceId=' + dataSourceId;
				confirmationHandling(confirmationMessage, Location);
			}

			function expand(dataSourceId) {
				var compactedArrowId = dataSourceId + '-compacted';
				var expandedArrowId = dataSourceId + '-expanded';
				var fullViewId = dataSourceId + '-full';
				$('#' + compactedArrowId).hide();
				$('#' + expandedArrowId).show();
				$('#' + fullViewId).show();
			}

			function compact(dataSourceId) {
				var compactedArrowId = dataSourceId + '-compacted';
				var expandedArrowId = dataSourceId + '-expanded';
				var fullViewId = dataSourceId + '-full';
				$('#' + compactedArrowId).show();
				$('#' + expandedArrowId).hide();
				$('#' + fullViewId).hide();
			}

			function confirmationHandling(confirmationMessage, Location) {
				var answer = confirm (confirmationMessage);
				if (answer) {
					location.href = '${pageContext.request.contextPath}' + Location;
				}
				else {

				}
			}

			function viewOtherLogs()
			{
				var otherLogs = document.getElementById('otherLogs');
				otherLogs.style.display = (otherLogs.style.display != 'none' ? 'none' : '');
			}

            function viewOtherExports()
			{
				var otherExports = document.getElementById('otherExports');
				otherExports.style.display = (otherExports.style.display != 'none' ? 'none' : '');
			}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />

		<c:import url="/jsp/scheduler/scheduleTaskPopup.include.jsp" />

		<table class="mainTable">
			<thead>
				<tr>
					<th class="mainHeader" colspan="9">
						<stripes:form action="/dataProvider/CreateEditDataProvider.action" acceptcharset="UTF-8">
							<stripes:hidden name="aggregatorId" value="${actionBean.dataProvider.aggregatorId}" />
                            <stripes:hidden name="dataProviderId" value="${actionBean.dataProvider.id}" />
                            
							<div style="float:left; width: 80%;">
								 <c:if test="${!empty actionBean.dataProvider.country}">
							        <c:set var="countryString" value="${fn:trim(actionBean.dataProvider.country)}"/>

                                    <img title="${actionBean.dataProvider.country}" alt="${actionBean.dataProvider.country}"
										 src="${pageContext.request.contextPath}/jsp/images/countries/${repox:getCountries()[countryString]}.png" />
								</c:if>
								<b><c:out value="${actionBean.dataProvider.name}"/></b>
								<c:if test="${fn:length(actionBean.dataProvider.description) > 0}">
									- ${actionBean.dataProvider.description}
								</c:if>
                                -  <c:out value="${actionBean.dataProvider.nameCode}"/><br/>
                                <stripes:link href="${actionBean.dataProvider.homePage}">
                                    <c:out value="${actionBean.dataProvider.homePage}"/>
                                </stripes:link>
								[<c:out value="${actionBean.dataProvider.aggregator.name}"/>]
								(<stripes:link class="header" href="/dataProvider/ViewDataProvider.action?dataProviderId=${actionBean.dataProvider.id}">
									<fmt:message key="common.refresh" />
								</stripes:link>)
							</div>

							<div style="float:right">
								<stripes:submit name="edit"><fmt:message key="common.edit" /></stripes:submit>
								<c:set var="confirmationMessageDataProvider"><fmt:message key="dataProvider.delete.confirmationMessage" /></c:set>
								<stripes:button onclick="javascript:deleteDataProvider('${confirmationMessageDataProvider}', '${repox:encode(actionBean.dataProvider.id)}');" name="delete"><fmt:message key="common.delete" /></stripes:button>
							</div>
						</stripes:form>
					</th>
				</tr>
				<tr>
					<th class="mainHeader" colspan="2"><fmt:message key="dataSource.set" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.operations" /></th>
					<th class="mainHeader"><fmt:message key="dataSource.oai.schemas" /></th>
					<th class="mainHeader"><fmt:message key="dataSource.ingest" /> <fmt:message key="common.type" /></th>
					<th class="mainHeader"><fmt:message key="common.last" /> <fmt:message key="dataSource.ingest" /> <fmt:message key="common.date" /></th>
					<th class="mainHeader"><fmt:message key="common.next" /> <fmt:message key="dataSource.ingest" /> <fmt:message key="common.date" /></th>
					<th class="mainHeader"><fmt:message key="dataSource.numberRecords" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.ingestStatus" /></th>
				</tr>
			</thead>
			<c:forEach var="dataSource" items="${actionBean.dataProvider.reversedDataSources}" varStatus="currentIndex">
				<c:choose>
			        <c:when test="${currentIndex.index % 2 == 0}"><c:set var="cellClass" value="even" /></c:when>
			        <c:otherwise><c:set var="cellClass" value="odd" /></c:otherwise>
				</c:choose>

				<c:choose>
			        <c:when test="${fn:length(actionBean.dataProvider.dataSources) == 1 || param.dataSourceId == dataSource.id}">
			        	<c:set var="displayCompacted" value="none" />
			        	<c:set var="displayExpanded" value="" />
			        </c:when>
			        <c:otherwise>
			        	<c:set var="displayCompacted" value="" />
			        	<c:set var="displayExpanded" value="none" />
					</c:otherwise>
				</c:choose>

				<tr>
					<c:set var="editingView" value="true" scope="request" />
					<c:set var="dataSource" value="${dataSource}" scope="request" />
					<c:set var="cellClass" value="${cellClass}" scope="request" />
					<td class="${cellClass}">
						<img id="${dataSource.id}-compacted" title="expand" alt="expand" class="action"
							onclick="javascript:expand('${dataSource.id}')" style="display: ${displayCompacted}"
							src="${pageContext.request.contextPath}/jsp/images/arrow-compacted.png" />

						<img id="${dataSource.id}-expanded" title="compact" alt="compact" class="action"
							onclick="javascript:compact('${dataSource.id}')" style="display: ${displayExpanded}"
							src="${pageContext.request.contextPath}/jsp/images/arrow-expanded.png" />
					</td>
                    <td class="${cellClass}" style="text-align: left">
                        <c:out value="${dataSource.id}"/>
                    </td>

					<c:import url="/jsp/dataSourceRow.include.jsp" />
				</tr>
				<tr id="${dataSource.id}-full" style="display: ${displayExpanded}" >
					<td colspan="8" class="dataSourceExpanded">
						<c:set var="editDataSource" value="${true}" scope="request"/>
						<c:set var="dataSource" value="${dataSource}" scope="request"/>

						<c:import url="/jsp/dataProvider/dataSource.include.jsp" />
					</td>
				</tr>
			</c:forEach>
			<tr>
                <td colspan="9">
                    <stripes:link href="/dataProvider/CreateEditDataSourceDirectoryImporter.action">
                        ...<fmt:message key="dataSource.create" />...
                        <stripes:param name="dataProviderId" value="${actionBean.dataProvider.id}"/>
                        <stripes:param name="dataSource.metadataFormat" value="ese"/>
                    </stripes:link>
	            </td>
            </tr>
		</table>
	</body>
</html>
