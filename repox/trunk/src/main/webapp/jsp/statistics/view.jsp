<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
	<head>
		<title><fmt:message key="statistics.repox" /></title>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />

		<table class="mainTable">
            <thead>
				<tr>
					<th class="mainHeader"><fmt:message key="aggregators" /></th>
					<th class="mainHeader">${actionBean.repoxStatistics.aggregators}</th>
				</tr>
			</thead>

			<thead>
				<tr>
					<th class="mainHeader"><fmt:message key="dataProviders" /></th>
					<th class="mainHeader">${actionBean.repoxStatistics.dataProviders}</th>
				</tr>
			</thead>

			<thead>
				<tr>
					<th class="mainHeader"><fmt:message key="dataSources" /></th>
					<th class="mainHeader">${actionBean.repoxStatistics.dataSourcesIdExtracted
											 + actionBean.repoxStatistics.dataSourcesIdGenerated
											 + actionBean.repoxStatistics.dataSourcesIdProvided}</th>
				</tr>
			</thead>
			<thead>
				<tr>
					<th class="secondaryHeader" colspan="2"><fmt:message key="dataSource.type.short" /></th>
				</tr>
			</thead>
			<tr>
				<td class="description" colspan="1"><fmt:message key="dataSource.oai.short" /></td>
				<td class="value" colspan="2"><c:out value="${actionBean.repoxStatistics.dataSourcesOai}"/></td>
			</tr>
			<tr>
				<td class="description" colspan="1"><fmt:message key="common.folder" /></td>
				<td class="value" colspan="2"><c:out value="${actionBean.repoxStatistics.dataSourcesDirectoryImporter}"/></td>
			</tr>

			<thead>
				<tr>
					<th class="secondaryHeader" colspan="2"><fmt:message key="dataSource.metadataFormat" /></th>
				</tr>
			</thead>
			<c:forEach items="${actionBean.repoxStatistics.dataSourcesMetadataFormats}" var="currentEntry">
				<tr>
					<td class="description">${currentEntry.key}</td>
					<td class="value">${currentEntry.value}</td>
				</tr>
			</c:forEach>

			<thead>
				<tr>
					<th class="secondaryHeader" colspan="2"><fmt:message key="dataSource.idPolicy.short" /></th>
				</tr>
			</thead>

			<tr>
				<td class="description"><fmt:message key="dataSource.idExtracted.short" /></td>
				<td class="value"><c:out value="${actionBean.repoxStatistics.dataSourcesIdExtracted}"/></td>
			</tr>
			<tr>
				<td class="description"><fmt:message key="dataSource.idGenerated.short" /></td>
				<td class="value"><c:out value="${actionBean.repoxStatistics.dataSourcesIdGenerated}"/></td>
			</tr>

			<thead>
				<tr>
					<th class="mainHeader"><fmt:message key="records" /></th>
					<th class="mainHeader">${actionBean.repoxStatistics.recordsTotal}</th>
				</tr>
			</thead>
			<thead>
				<tr>
					<th class="secondaryHeader" colspan="2"><fmt:message key="common.averages" /></th>
				</tr>
			</thead>
			<tr>
				<td class="description" colspan="1"><fmt:message key="statistics.avgRecordsDataProvider" /></td>
				<td class="value" colspan="2"><fmt:formatNumber maxFractionDigits="2" value="${actionBean.repoxStatistics.recordsAvgDataProvider}" /></td>
			</tr>
			<tr>
				<td class="description" colspan="1"><fmt:message key="statistics.avgRecordsDataSource" /></td>
				<td class="value" colspan="2"><fmt:formatNumber maxFractionDigits="2" value="${actionBean.repoxStatistics.recordsAvgDataSource}" /></td>
			</tr>

			<thead>
				<tr>
					<th class="secondaryHeader" colspan="2"><fmt:message key="common.countries" /></th>
				</tr>
			</thead>
			<c:forEach items="${actionBean.repoxStatistics.countriesRecords}" var="currentEntry">
				<tr>
					<td class="description">

                        <%-- Country --%>
                        <c:set var="countryString" value="${fn:trim(currentEntry.key)}"/>
                        <img title="${countryString}" alt="${countryString}" src="${pageContext.request.contextPath}/jsp/images/countries/${repox:getCountries()[countryString]}.png" />
                        ${countryString}
                        
                        <%--
                        <img title="${repox:getCountries()[currentEntry.key]}" alt="${repox:getCountries()[currentEntry.key]}"
					        src="${pageContext.request.contextPath}/jsp/images/countries/${currentEntry.key}.png" />
					    ${repox:getCountries()[currentEntry.key]}
					    --%>
                    </td>
					<td class="value">${currentEntry.value}</td>
				</tr>
			</c:forEach>

			<tfoot>
				<tr>
					<td colspan="3"><fmt:message key="statistics.generationDate" />: <c:out value="${actionBean.repoxStatistics.generationDate}"/></td>
<!-- 				<td>
						<stripes:form action="/statistics/Statistics.action" acceptcharset="UTF-8">
							<stripes:submit name="generate"><fmt:message key="common.generate" /></stripes:submit>
						</stripes:form>
					</td>  -->
				</tr>
			</tfoot>
		</table>

	</body>
</html>
