<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>
<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form"%>

<html>
    <head>

        <title><fmt:message key="dataProvider" />: ${dataProvider.name}</title>

        <script type="text/javascript">
            function deleteDataProvider(confirmationMessage, dataProviderId) {
                var Location = '/springDataProvider/deleteDataProvider.html?dataProviderId=' + dataProviderId;
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
        </script>
    </head>

    <body>
        <c:if test="${!empty message}">
            <div class="messages"><c:out value="${message}" escapeXml="false"/></div>
        </c:if>
        <table class="mainTable">
            <thead>
                <tr>
                    <th class="mainHeader" colspan="8">
                        <div style="float:left; width: 80%;">
                            <c:if test="${!empty dataProvider.country}">
                                <c:set var="countryString" value="${fn:trim(dataProvider.country)}"/>

                                <img title="${dataProvider.country}" alt="${dataProvider.country}"
                                     src="${pageContext.request.contextPath}/jsp/images/countries/${repox:getCountries()[countryString]}.png" />
                            </c:if>
                            <b><c:out value="${dataProvider.name}"/></b>
                            <c:if test="${fn:length(dataProvider.description) > 0}">
									- ${dataProvider.description}
                            </c:if>
                            -  <c:out value="${dataProvider.nameCode}"/><br/>
                            <stripes:link href="${dataProvider.homePage}">
                                <c:out value="${dataProvider.homePage}"/>
                            </stripes:link>
								[<c:out value="${aggregator.name}"/>]
								(<stripes:link class="header" href="/dataProvider/ViewDataProvider.action?dataProviderId=${dataProvider_id}">
                                <fmt:message key="common.refresh" />
                            </stripes:link>)
                        </div>

                        <div style="float:right">
                            <form action="editDataProvider.html" method="get" accept-charset="UTF-8">
                                <input name="dataProviderId" value="${dataProvider.id}" type="hidden" />

                                <c:set var="edit_txt"><fmt:message key="common.edit"/></c:set>
                                <input value="${edit_txt}" type="submit"/>

                                <c:set var="delete_txt"><fmt:message key="common.delete"/></c:set>
                                <c:set var="confirmationMessageAggregator"><fmt:message key="aggregator.delete.confirmationMessage" /></c:set>
                                <input value="${delete_txt}" onclick="javascript:deleteDataProvider('${confirmationMessageAggregator}', '${repox:encode(dataProvider.id)}');" type="button" />
                            </form>
                        </div>
                    </th>
                </tr>
                <tr>
                    <th class="mainHeader" colspan="2"><fmt:message key="dataSource.id" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.set" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.oai.schemas" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.ingest" /> <fmt:message key="common.type" /></th>
                    <th class="mainHeader"><fmt:message key="common.last" /> <fmt:message key="dataSource.ingest" /> <fmt:message key="common.date" /></th>
                    <th class="mainHeader"><fmt:message key="common.next" /> <fmt:message key="dataSource.ingest" /> <fmt:message key="common.date" /></th>
                    <th class="mainHeader"><fmt:message key="dataSource.numberRecords" /></th>
                </tr>
            </thead>
            <c:forEach var="dataSource" items="${dataSources}" varStatus="currentIndex">
                <c:choose>
                    <c:when test="${currentIndex.index % 2 == 0}"><c:set var="cellClass" value="even" /></c:when>
                    <c:otherwise><c:set var="cellClass" value="odd" /></c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${fn:length(dataProvider.dataSources) == 1 || param.dataSourceId == dataSource.id}">
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
                        <c:out value="${dataSource.idDb}"/>
                    </td>
                    <td class="${cellClass}" style="text-align: left">
                        <%--<form action="/repox/dataProvider/CreateEditDataSourceDirectoryImporter.action" method="post" accept-charset="UTF-8">--%>
                        <form action="dataProvider/CreateEditDataSourceDirectoryImporter.action" method="post" accept-charset="UTF-8">

                            <input name="dataSourceId" value="${dataSource.id}" type="hidden" />
                            <input name="dataProviderId" value="${dataProvider.id}" type="hidden" />

                            <input title="edit" alt="edit" name="edit" src="${pageContext.request.contextPath}/jsp/images/edit.png" type="image" />

                            <c:set var="confirmationMessageDeleteRecords"><fmt:message key="dataSource.deleteRecords.confirmationMessage" /></c:set>
                            <img class="action" alt="empty" title="empty" src="${pageContext.request.contextPath}/jsp/images/empty.png" onclick="javascript:deleteAllRecords('${confirmationMessageDeleteRecords}',
		   		'${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" />

                            <c:set var="confirmationMessageDataSource"><fmt:message key="dataSource.delete.confirmationMessage" /></c:set>
                            <img class="action" alt="delete" title="delete" src="${pageContext.request.contextPath}/jsp/images/delete.png" onclick="javascript:deleteDataSource('${confirmationMessageDataSource}',
                                '${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" />

                            ${dataSource.id}
                        </form>
                    </td>

                    <td class="${cellClass}">${dataSource.metadataFormat}</td>

                    <td class="${cellClass}">?</td>
                    <td class="${cellClass}">?</td>
                    <td class="${cellClass}">?</td>

                    <td class="${cellClass}" style="text-align: right;">${dataSource.numberRecords}</td>

                </tr>

                <tr id="${dataSource.id}-full" style="display: ${displayExpanded}" >
                    <td colspan="7" class="dataSourceExpanded">
                        <c:set var="editDataSource" value="${true}" scope="request"/>
                        <c:set var="dataSource" value="${dataSource}" scope="request"/>
                        <table class="dataSourceTable">
                            <tr>
                                <td><fmt:message key="common.type" /></td>
                                <td>?</td>
                            </tr>
                            <tr>
                                <td><fmt:message key="dataSource.localMetadataFormat" /></td>
                                <td>${dataSource.metadataFormat}</td>
                            </tr>
                            <tr>
                                <td><fmt:message key="dataSource.idPolicy.short" /></td>
                                <td>?</td>
                            </tr>
                            <tr><td><fmt:message key="dashboard.table.recordSet" /></td><td><c:out value="${dataSource.id}"/></td></tr>
                            <tr><td><fmt:message key="common.description" /></td><td><c:out value="${dataSource.description}"/></td></tr>
                            <tr><td><fmt:message key="dataSource.name" /></td><td><c:out value="${dataSource.name}"/></td></tr>
                            <tr><td><fmt:message key="common.nameCode" /></td><td><c:out value="${dataSource.nameCode}"/></td></tr>
                            <tr>
                                <td><fmt:message key="common.transformations" /></td>
                                <td>
                                    <c:forEach items="${dataSource.metadataTransformations}" var="metadataTransformation" varStatus="status">
                                        <c:if test="${status.index > 0}"><br /></c:if>
                                        [${metadataTransformation.value.sourceFormat} -> ${metadataTransformation.value.destinationFormat}]
                                        ${metadataTransformation.value.id}
                                    </c:forEach>
                                </td>
                            <tr>
                                <td><fmt:message key="dataSource.numberRecords" /></td>
                                <td>
                                    <%--<form action="/repox/dataProvider/ViewDataProvider.action" method="post" accept-charset="UTF-8">--%>
                                    <form action="dataProvider/ViewDataProvider.action" method="post" accept-charset="UTF-8">

                                        <input type="hidden" name="dataProviderId" value="${dataSource.dataProvider.id}" />
                                        <input type="hidden" name="dataSourceId" value="${dataSource.id}" />


                                        ${dataSource.numberRecords}

                                        <input title="update" alt="update" name="countRecords" src="${pageContext.request.contextPath}/jsp/images/refresh.png" type="image" />

                                        <%--
                                        <stripes:submit name="countRecords"><fmt:message key="common.update" /></stripes:submit>

                                        <c:set var="confirmationMessageDataSource"><fmt:message key="dataSource.countRecords.confirmationMessage" /></c:set>
                                        <stripes:button onclick="javascript:forceRecordCount('${confirmationMessageDataSource}',
			   		'${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" name="countRecords"><fmt:message key="common.update" /></stripes:button>
                                        --%>
                                    </form>
                                </td>
                            <tr>
                                <td><fmt:message key="dataSource.ingest" /></td>
                                <td>
                                    <form action="/repox/dataProvider/ViewDataProvider.action" method="post" accept-charset="UTF-8">
                                        <input type="hidden" name="dataSourceId" value="${dataSource.id}"/>

                                        <c:set var="ingestNowKey"><fmt:message key="dataSource.ingestNow"/></c:set>
                                        <input name="harvest" value="${ingestNowKey}" type="submit" />

                                        <c:set var="scheduleHarvestKey"><fmt:message key="dataSource.scheduleHarvest"/></c:set>
                                        <c:if test="${dataSource.recordIdPolicy.class.simpleName == 'IdExtracted' || dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
                                            <c:set var="fullIngest" value="false" />
                                        </c:if>
                                        <input type="button" onclick="javascript:ingestDialog('/dataProvider/ViewDataProvider.action', '${dataSource.dataProvider.name}', '${dataSource.id}', '${fullIngest}')"
                                               name="Schedule Ingest" value="Schedule Ingest" />
                                    </form>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="8">
                    <%--<a href="/repox/dataProvider/CreateEditDataSourceDirectoryImporter.action?_sourcePage=%2Fjsp%2FdataProvider%2Fview.jsp&amp;dataSource.metadataFormat=ese&amp;dataProviderId=${dataProvider.id}">...<fmt:message key="dataSource.create" />...</a>--%>
                    <a href="dataProvider/CreateEditDataSourceDirectoryImporter.action?_sourcePage=%2Fjsp%2FdataProvider%2Fview.jsp&amp;dataProviderId=${dataProvider.id}">...<fmt:message key="dataSource.create" />...</a>
                </td>
            </tr>

        </table>
    </body>
</html>
