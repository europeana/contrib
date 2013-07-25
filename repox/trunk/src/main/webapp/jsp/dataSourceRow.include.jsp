<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>



<!--Data Set Name-->
<td class="${cellClass}" style="text-align: left">
    <c:choose>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
            <c:set var="editAction" value="/dataProvider/CreateEditDataSourceOai.action" /></c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter'}">
            <c:set var="editAction" value="/dataProvider/CreateEditDataSourceDirectoryImporter.action" /></c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.horizon.DataSourceHorizon'}">
            <c:set var="editAction" value="/dataProvider/CreateEditDataSourceHorizon.action" /></c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.z3950.DataSourceZ3950'}">
            <c:set var="editAction" value="/dataProvider/CreateEditDataSourceZ3950.action" /></c:when>
        <c:otherwise><%-- <fmt:message key="error"><fmt:param value="${dataSource.class.name}" /></fmt:message> --%></c:otherwise>
    </c:choose>

    <c:if test="${editingView == 'true'}">
        <stripes:form action="${editAction}" acceptcharset="UTF-8">
            <stripes:hidden name="dataSourceId" value="${dataSource.id}" />
            <stripes:hidden name="dataProviderId" value="${dataSource.dataProvider.id}" />

            <stripes:image name="edit" alt="edit" title="edit" src="/jsp/images/edit.png" />

            <c:set var="confirmationMessageDeleteRecords"><fmt:message key="dataSource.deleteRecords.confirmationMessage" /></c:set>
            <img class="action" alt="empty" title="empty" src="${pageContext.request.contextPath}/jsp/images/empty.png" onclick="javascript:deleteAllRecords('${confirmationMessageDeleteRecords}',
		   		'${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" />

            <c:set var="confirmationMessageDataSource"><fmt:message key="dataSource.delete.confirmationMessage" /></c:set>
            <img class="action" alt="delete" title="delete" src="${pageContext.request.contextPath}/jsp/images/delete.png" onclick="javascript:deleteDataSource('${confirmationMessageDataSource}',
	    				'${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" />
        </stripes:form>
    </c:if>
    <c:if test="${editingView != 'true'}">${dataSource.id}</c:if>

</td>

<!--OAI-PMH Schemas-->
<td class="${cellClass}">
    <c:choose>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter' &&
					dataSource.metadataFormat != null && dataSource.metadataFormat == 'ISO2709'}">
            MarcXchange
        </c:when>
        <c:otherwise>${dataSource.metadataFormat}</c:otherwise>
    </c:choose>
    <c:forEach var="currentTransformation" items="${dataSource.metadataTransformations}">
        | ${currentTransformation.key}
    </c:forEach>
</td>

<!--Ingest Type-->
<td class="${cellClass}" style="text-align: left">
    <c:choose>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter'}">
            <fmt:message key="common.folder" />
        </c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
            <fmt:message key="dataSource.oai.short" />
        </c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.horizon.DataSourceHorizon'}">
            <fmt:message key="dataSource.horizon.short" /></c:when>
        <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.z3950.DataSourceZ3950'}">
            <fmt:message key="dataSource.z3950.short" /></c:when>
    </c:choose>
    ${dataSource.metadataFormat}
</td>

<!--Last Ingest Date-->
<td class="${cellClass}" style="text-align: right;">
    ${dataSource.synchronizationDateString}
</td>

<!--Next Ingest Date-->
<td class="${cellClass}" style="text-align: left;">
    <c:forEach var="scheduledTask" items="${actionBean.scheduledTasks}" varStatus="currentIndex">
        <c:choose>
            <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
                <c:set var="dataSourceId" value="${scheduledTask.parameters[1]}" />
                <c:set var="fullIngest" value="${scheduledTask.parameters[2]}" />
            </c:when>
            <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
                <c:set var="dataSourceId" value="${scheduledTask.parameters[1]}" />
                <c:set var="exportFolder" value="${scheduledTask.parameters[2]}" />
            </c:when>
            <c:otherwise>
                <%-- <fmt:message key="error"></fmt:message> --%>
            </c:otherwise>
        </c:choose>

        <c:if test="${dataSourceId == dataSource.id
						&& scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
            <!--<c:set var="scheduledTask" value="${scheduledTask}" scope="request"/>
            <c:import url="/jsp/scheduledTask.include.jsp" /> -->
            ${scheduledTask.nextIngestDate}
            <br />
        </c:if>
    </c:forEach>
</td>

<!--Number of Records-->
<td class="${cellClass}" style="text-align: right;">${dataSource.numberRecords}</td>

<!--Ingest Status-->
<td class="${cellClass}">

    <c:if test="${dataSource.statusString eq 'OK'}">
        <img src="${pageContext.request.contextPath}/jsp/images/ok.png" alt="Ingesting completed with success"/>
    </c:if>
    <c:if test="${dataSource.statusString eq 'RUNNING'}">
        <img src="${pageContext.request.contextPath}/jsp/images/processing.gif" alt="Ingesting" height="25px"/>
    </c:if>
    <c:if test="${dataSource.statusString eq 'ERROR'}">
        <img src="${pageContext.request.contextPath}/jsp/images/error.png" alt="Error"/>
        <c:if test="${fn:length(dataSource.logFilenames) > 0}">
            <c:forEach var="currentLogFile" varStatus="status" items="${dataSource.logFilenames}">
                <c:if test="${status.index == 0}">
                    <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewLogFile=viewLogFile&dataSourceId=${dataSource.id}&logFilename=${currentLogFile}" title="View Log">
                        <img border="0" src="${pageContext.request.contextPath}/jsp/images/log_icon.gif" alt="View log"/>
                    </a>
                </c:if>
            </c:forEach>
        </c:if>
    </c:if>
    <c:if test="${dataSource.statusString eq 'WARNING'}">
        <img src="${pageContext.request.contextPath}/jsp/images/warning.png" alt="Warning"/>

        <c:if test="${fn:length(dataSource.logFilenames) > 0}">
            <c:forEach var="currentLogFile" varStatus="status" items="${dataSource.logFilenames}">
                <c:if test="${status.index == 0}">
                    <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewLogFile=viewLogFile&dataSourceId=${dataSource.id}&logFilename=${currentLogFile}" title="View Log">
                        <img border="0" src="${pageContext.request.contextPath}/jsp/images/log_icon.gif" alt="View log"/>
                    </a>
                </c:if>
            </c:forEach>
        </c:if>
    </c:if>
</td>
