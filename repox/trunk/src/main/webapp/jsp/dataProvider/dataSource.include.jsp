<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<table class="dataSourceTable">
<tr>
    <td><fmt:message key="common.type" /></td>
    <td>
        <c:choose>
            <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
                <fmt:message key="dataSource.oai.short" />

                <stripes:link class="imageLink" href="/jsp/testOAI-PMH.jsp" target="_blank">
                    <stripes:param name="serverURL" value="${dataSource.oaiSourceURL}" />
                    <stripes:param name="set" value="${dataSource.oaiSet}" />
                    <stripes:param name="metadataPrefix" value="${dataSource.metadataFormat}" />

                    <img src="${pageContext.request.contextPath}/jsp/images/network-transmit.png" title="Test OAI-PMH" alt="Test OAI-PMH">
                </stripes:link>
            </c:when>
            <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter'}">
                <fmt:message key="common.folder" /> ${dataSource.metadataFormat}

                <c:if test="${dataSource.metadataFormat != null && dataSource.metadataFormat == 'ISO2709'}">
                    <c:choose>
                        <c:when test="${dataSource.extractStrategy.isoImplementationClass == 'pt.utl.ist.marc.iso2709.IteratorIso2709'}">
                            <fmt:message key="dataSource.iteratorIso2709" />
                        </c:when>
                        <c:when test="${dataSource.extractStrategy.isoImplementationClass == 'pt.utl.ist.marc.iso2709.IteratorIso2709Albania'}">
                            <fmt:message key="dataSource.iteratorIso2709Albania" />
                        </c:when>
                        <c:when test="${dataSource.extractStrategy.isoImplementationClass == 'pt.utl.ist.marc.iso2709.IteratorIso2709Ukraine'}">
                            <fmt:message key="dataSource.iteratorIso2709Ukraine" />
                        </c:when>
                    </c:choose>
                    (<c:out value="${dataSource.characterEncoding}" />)
                </c:if>
            </c:when>
            <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.z3950.DataSourceZ3950'}">
                <fmt:message key="dataSource.z3950.short" />
            </c:when>
            <c:otherwise><%-- <fmt:message key="error"><fmt:param value="${dataSource.class.name}" /></fmt:message> --%></c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <td><fmt:message key="dataSource.localMetadataFormat" /></td>
    <td>
        <c:if test="${dataSource.metadataFormat != null && dataSource.metadataFormat != 'ISO2709'}">
            ${dataSource.metadataFormat}
        </c:if>
        <c:if test="${dataSource.metadataFormat != null && dataSource.metadataFormat == 'ISO2709'}">
            MarcXchange
        </c:if>
    </td>
</tr>

<c:choose>
    <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
        <tr>
            <td><fmt:message key="dataSource.oai.sourceURL" /></td>
            <td><c:out value="${dataSource.oaiSourceURL}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.oai.set" /></td>
            <td><c:out value="${dataSource.oaiSet}"/></td>
        </tr>
    </c:when>
    <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter'}">
        <tr>
            <td><fmt:message key="dataSource.sourcesDirPath" /></td>
            <td><c:out value="${dataSource.sourcesDirPath}"/></td>
        </tr>

        <c:if test="${dataSource.URLSourcesPath != null}">
            <tr>
                <td><fmt:message key="dataSource.URLsourcesPath" /></td>
                <td><c:out value="${dataSource.URLSourcesPath}"/></td>
            </tr>

        </c:if>

        <c:if test="${dataSource.recordXPath != null}">
            <tr>
                <td><fmt:message key="dataSource.recordXPath" /></td>
                <td><c:out value="${dataSource.recordXPath}"/></td>
            </tr>
            <%--
            <tr>
                <td><fmt:message key="dataSource.namespacePrefixUri" /></td>
                <td>
                    <c:forEach items="${dataSource.namespaces}" var="namespace" varStatus="status">
                        <c:if test="${status.index > 0}"><br /></c:if>
                        <c:out value="${namespace.key}" /> - <c:out value="${namespace.value}" />
                    </c:forEach>
                </td>
            </tr>
            --%>
        </c:if>
    </c:when>
    <c:when test="${dataSource.class.name == 'pt.utl.ist.repox.z3950.DataSourceZ3950'}">
        <tr>
            <td><fmt:message key="dataSource.z3950.address" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.address}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.port" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.port}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.database" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.database}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.user" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.user}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.password" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.password}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.charset" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.charset}"/></td>
        </tr>
        <tr>
            <td><fmt:message key="dataSource.z3950.recordSyntax" /></td>
            <td><c:out value="${dataSource.harvestMethod.target.recordSyntax}"/></td>
        </tr>

        <tr>
            <td><fmt:message key="dataSource.z3950.harvestMethod" /></td>
            <td><c:out value="${dataSource.harvestMethod.class.simpleName}"/></td>
        </tr>

        <c:choose>
            <c:when test="${dataSource.harvestMethod.class.simpleName == 'TimestampHarvester'}">
                <tr>
                    <td><fmt:message key="dataSource.z3950.earliestTimestamp" /></td>
                    <td><c:out value="${dataSource.harvestMethod.earliestTimestamp}"/></td>
                </tr>
            </c:when>
            <c:when test="${dataSource.harvestMethod.class.simpleName == 'IdListHarvester'}">
                <tr>
                    <td><fmt:message key="dataSource.z3950.idListFile" /></td>
                    <td><c:out value="${dataSource.harvestMethod.idListFile.absolutePath}"/></td>
                </tr>
            </c:when>
            <c:when test="${dataSource.harvestMethod.class.simpleName == 'IdSequenceHarvester'}">
                <tr>
                    <td><fmt:message key="dataSource.z3950.maximumId" /></td>
                    <td><c:out value="${dataSource.harvestMethod.maximumId}"/></td>
                </tr>
            </c:when>
            <c:otherwise><!-- Error: HarvestMethod not recognized --></c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise><%-- <fmt:message key="error"><fmt:param value="${dataSource.class.name}" /></fmt:message> --%></c:otherwise>
</c:choose>
<tr>
    <td><fmt:message key="dataSource.idPolicy.short" /></td>
    <td>
        <c:choose>
            <c:when test="${dataSource.recordIdPolicy.class.simpleName == 'IdGenerated'}">
                <fmt:message key="dataSource.idGenerated.short" /></c:when>
            <c:when test="${dataSource.recordIdPolicy.class.simpleName == 'IdExtracted'}">
                <fmt:message key="dataSource.idExtracted.short" /></c:when>
        </c:choose>
    </td>
</tr>
<c:if test="${dataSource.recordIdPolicy.class.simpleName == 'IdExtracted'}">
    <tr>
        <td>
            <fmt:message key="dataSource.idXpath" />
        </td>
        <td>
            <c:out value="${dataSource.recordIdPolicy.identifierXpath}" />
        </td>
    </tr>
    <tr>
        <td><fmt:message key="dataSource.namespacePrefixUri" /></td>
        <td>
            <c:forEach items="${dataSource.recordIdPolicy.namespaces}" var="namespace" varStatus="status">
                <c:if test="${status.index > 0}"><br /></c:if>
                <c:out value="${namespace.key}" /> - <c:out value="${namespace.value}" />
            </c:forEach>
        </td>
    </tr>
</c:if>

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
</tr>
<tr>
    <td><fmt:message key="dataSource.numberRecords" /></td>
    <td>
        <stripes:form action="/dataProvider/ViewDataProvider.action" acceptcharset="UTF-8">
            <input type="hidden" name="dataProviderId" value="${dataSource.dataProvider.id}" />
            <input type="hidden" name="dataSourceId" value="${dataSource.id}" />
            

            ${dataSource.numberRecords}

            <stripes:image name="countRecords" alt="update" title="update" src="/jsp/images/refresh.png" />
            <%--
            <stripes:submit name="countRecords"><fmt:message key="common.update" /></stripes:submit>

            <c:set var="confirmationMessageDataSource"><fmt:message key="dataSource.countRecords.confirmationMessage" /></c:set>
            <stripes:button onclick="javascript:forceRecordCount('${confirmationMessageDataSource}',
			   		'${repox:encode(dataSource.dataProvider.id)}', '${repox:encode(dataSource.id)}');" name="countRecords"><fmt:message key="common.update" /></stripes:button>
            --%>
        </stripes:form>
    </td>
</tr>
<tr>
    <td>
        <fmt:message key="dataSource.ingest" />
    </td>
    <td>
        <stripes:form action="/dataProvider/ViewDataProvider.action" acceptcharset="UTF-8">
            <input type="hidden" name="dataSourceId" value="${dataSource.id}" />
            <stripes:submit name="harvest"><fmt:message key="dataSource.ingestNow" /></stripes:submit>

            <c:set var="scheduleHarvestKey"> <fmt:message key="dataSource.scheduleHarvest" /></c:set>
            <c:if test="${dataSource.recordIdPolicy.class.simpleName == 'IdExtracted' || dataSource.class.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
                <c:set var="fullIngest" value="false" />
            </c:if>
            <input type="button" onclick="javascript:ingestDialog('/dataProvider/ViewDataProvider.action', '${dataSource.dataProvider.name}', '${dataSource.id}', '${fullIngest}')"
                   name="${scheduleHarvestKey}" value="${scheduleHarvestKey}" />
        </stripes:form>
    </td>
</tr>
<tr>
    <td>
        <fmt:message key="dataSource.export" />
    </td>
    <td>
        <stripes:form action="/dataProvider/ViewDataProvider.action" acceptcharset="UTF-8">
            <input type="hidden" name="dataSourceId" value="${dataSource.id}" />
            <input type="hidden" name="exportDirectory" value="${dataSource.exportPath}" />


            <fmt:message key="common.fullPath" />:
            <stripes:text name="exportDirectory" value="${dataSource.exportPath}" disabled="true"/>

            <fmt:message key="export.recordsPerFile" />:
            <stripes:select name="recordsPerFile">
                <stripes:option label="1" value="1"/>
                <stripes:option label="10" value="10"/>
                <stripes:option label="100" value="100"/>
                <stripes:option label="250" value="250"/>
                <stripes:option label="1000" value="1000"/>
                <stripes:option value="-1"><fmt:message key="common.all" /></stripes:option>
            </stripes:select>
            <stripes:submit name="exportToFilesystem"><fmt:message key="export.exportNow" /></stripes:submit>

            <c:set var="scheduleExportKey"><fmt:message key="export.scheduleExportFilesystem" /></c:set>
            <input type="button" onclick="javascript:exportDirDialog('/dataProvider/ViewDataProvider.action', '${dataSource.dataProvider.name}', '${dataSource.id}')"
                   name="${scheduleExportKey}" value="${scheduleExportKey}" />
        </stripes:form>


        <c:if test="${fn:length(dataSource.exportFileNames) > 0}">
            <c:forEach var="currentExportFile" varStatus="status" items="${dataSource.exportFileNames}">
                <c:if test="${status.index < 3}">
                    <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewExportFile=viewExportFile&dataSourceId=${dataSource.id}&exportFileName=${currentExportFile}">
                            ${currentExportFile}
                    </a>
                    <br />
                </c:if>
            </c:forEach>

            <c:if test="${fn:length(dataSource.exportFileNames) > 3}">
                <br />(<a href="javascript:viewOtherExports()"><fmt:message key="export.showAllExportFiles" /></a>)
            </c:if>
        </c:if>

        <c:if test="${fn:length(dataSource.exportFileNames) > 3}">
				<span id="otherExports" style="display: none;">
					<br />
					<c:forEach var="currentExportFile" varStatus="status" items="${dataSource.exportFileNames}">
                        <c:if test="${status.index >= 3}">
                            <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewExportFile=viewExportFile&dataSourceId=${dataSource.id}&exportFileName=${currentExportFile}">
                                    ${currentExportFile}
                            </a>
                            <br />
                        </c:if>
                    </c:forEach>
				</span>
        </c:if>

    </td>
</tr>
<tr>
    <td>
        <fmt:message key="scheduledTasks" />
    </td>
    <td>
        <c:set var="confirmationMessageScheduledTask"><fmt:message key="scheduledTask.delete.confirmationMessage" /></c:set>
        <c:forEach var="scheduledTask" items="${actionBean.scheduledTasks}" varStatus="currentIndex">
            <c:if test="${scheduledTask.parameters[1] == dataSource.id}">
                <c:if test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
                    <c:set var="scheduledTask" value="${scheduledTask}" scope="request"/>
                    <stripes:form action="/scheduler/Scheduler.action" acceptcharset="UTF-8">
                        <fmt:message key="dataSource.ingest" /> <c:import url="/jsp/scheduledTask.include.jsp" />
                        <stripes:button onclick="javascript:deleteScheduledTask('${confirmationMessageScheduledTask}', '${scheduledTask.id}', '${actionBean.dataProvider.id}', '${dataSource.id}');"
                                        name="delete"><fmt:message key="common.delete" /></stripes:button>
                    </stripes:form>
                </c:if>

                <c:if test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
                    <c:set var="scheduledTask" value="${scheduledTask}" scope="request"/>
                    <stripes:form action="/scheduler/Scheduler.action" acceptcharset="UTF-8">
                        <fmt:message key="dataSource.export" /> <c:import url="/jsp/scheduledTask.include.jsp" />
                        <stripes:button onclick="javascript:deleteScheduledTask('${confirmationMessageScheduledTask}', '${scheduledTask.id}', '${actionBean.dataProvider.id}', '${dataSource.id}');"
                                        name="delete"><fmt:message key="common.delete" /></stripes:button>
                    </stripes:form>
                </c:if>
            </c:if>
        </c:forEach>
    </td>
</tr>
<tr>
    <td><fmt:message key="dataSource.logFiles" /></td>
    <td>
        <c:if test="${fn:length(dataSource.logFilenames) > 0}">
            <c:forEach var="currentLogFile" varStatus="status" items="${dataSource.logFilenames}">
                <c:if test="${status.index < 3}">
                    <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewLogFile=viewLogFile&dataSourceId=${dataSource.id}&logFilename=${currentLogFile}">
                            ${currentLogFile}
                    </a>
                    <br />
                </c:if>
            </c:forEach>
            <c:if test="${fn:length(dataSource.logFilenames) > 3}">
                <br />(<a href="javascript:viewOtherLogs()"><fmt:message key="dataSource.showAllLogFiles" /></a>)
            </c:if>
        </c:if>
        <c:if test="${fn:length(dataSource.logFilenames) > 3}">
				<span id="otherLogs" style="display: none;">
					<br />
					<c:forEach var="currentLogFile" varStatus="status" items="${dataSource.logFilenames}">
                        <c:if test="${status.index >= 3}">
                            <a href="${pageContext.request.contextPath}/dataProvider/ViewDataProvider.action?viewLogFile=viewLogFile&dataSourceId=${dataSource.id}&logFilename=${currentLogFile}">
                                    ${currentLogFile}
                            </a>
                            <br />
                        </c:if>
                    </c:forEach>
				</span>
        </c:if>
    </td>
</tr>
</table>