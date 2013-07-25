<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
<head>
    <title><fmt:message key="aggregator" /> ${actionBean.aggregator.name}</title>

    <script type="text/javascript">
        function deleteAggregator(confirmationMessage, aggregatorId) {
//            var Location = '/aggregator/DeleteAggregator.action?aggregatorId=' + aggregatorId;
//            confirmationHandling(confirmationMessage, Location);
            var Location ='/springAggregator/deleteAggregator.html?aggregatorId='+aggregatorId;
            confirmationHandling(confirmationMessage, Location);
        }

        function editAggregator(aggregatorId) {
            var Location ='/springAggregator/editAggregator.html?aggregatorId='+aggregatorId;
            location.href = '${pageContext.request.contextPath}' + Location;
        }

        function deleteDataProvider(confirmationMessage, dataProviderId) {
            var Location = '/dataProvider/DeleteDataProvider.action?dataProviderId=' + dataProviderId;
            confirmationHandling(confirmationMessage, Location);
        }

        function confirmationHandling(confirmationMessage, Location) {
            var answer = confirm (confirmationMessage);
            if (answer) {
                location.href = '${pageContext.request.contextPath}' + Location;
            }
            else {

            }
        }
    </script>
</head>
<body>
<stripes:errors />
<stripes:messages />

<table class="mainTable">
    <tr>
        <th class="mainHeader" colspan="7">
            <stripes:form action="/aggregator/CreateEditAggregator.action" acceptcharset="UTF-8">
                <stripes:hidden name="aggregatorId" value="${actionBean.aggregator.id}" />
                <div style="float:left; width: 80%;">

                    <b><c:out value="${actionBean.aggregator.name}"/></b>
                    <c:out value="${actionBean.aggregator.nameCode}"/><br/>
                    <stripes:link href="${actionBean.aggregator.homePage}">
                        <c:out value="${actionBean.aggregator.homePage}"/>
                    </stripes:link>
                    (<stripes:link class="header" href="/springAggregator/viewAggregator.html?aggregatorId=${actionBean.aggregator.id}">
                    <fmt:message key="common.refresh" />
                </stripes:link>)
                </div>

                <div style="float:right">
                    <stripes:button onclick="javascript:editAggregator('${repox:encode(actionBean.aggregator.id)}');" name="edit"><fmt:message key="common.edit" /></stripes:button>
                    <c:set var="confirmationMessageAggregator"><fmt:message key="aggregator.delete.confirmationMessage" /></c:set>
                    <stripes:button onclick="javascript:deleteAggregator('${confirmationMessageAggregator}', '${repox:encode(actionBean.aggregator.id)}');" name="delete"><fmt:message key="common.delete" /></stripes:button>
                </div>
            </stripes:form>
        </th>
    </tr>
    <tr>
        <th class="mainHeader"><fmt:message key="dataProvider.country" /></th>
        <th class="mainHeader"><fmt:message key="dataProvider.id" /></th>
        <th class="mainHeader"><fmt:message key="dataProvider.name" /></th>
        <th class="mainHeader"><fmt:message key="dataProvider.description" /></th>
        <th class="mainHeader"><fmt:message key="dataProvider.type" /></th>
        <th class="mainHeader"><fmt:message key="common.nameCode" /></th>
        <th class="mainHeader"><fmt:message key="dataProvider.url" /></th>
    </tr>

    <c:forEach var="dataProvider" items="${actionBean.aggregator.dataProviders}" varStatus="currentIndex">

        <c:choose>
            <c:when test="${currentIndex.index % 2 == 0}"><c:set var="cellClass" value="even" /></c:when>
            <c:otherwise><c:set var="cellClass" value="odd" /></c:otherwise>
        </c:choose>


        <tr>
            <c:set var="editingView" value="true" scope="request" />
            <c:set var="dataProvider" value="${dataProvider}" scope="request" />
            <c:set var="cellClass" value="${cellClass}" scope="request" />
            <td class="${cellClass}">
                <%-- Country --%>
                <c:if test="${!empty dataProvider.country}">
                    <c:set var="countryString" value="${fn:trim(dataProvider.country)}"/>
                    <img title="${dataProvider.country}" alt="${dataProvider.country}" src="${pageContext.request.contextPath}/jsp/images/countries/${repox:getCountries()[countryString]}.png" />
                </c:if>
            </td>
            <td class="${cellClass}" style="text-align: left">
                <%-- Id --%>
                <c:out escapeXml="true" value="${dataProvider.id}"/>
            </td>
            <td class="${cellClass}" style="text-align: left">
                <c:set var="confirmationMessageDataProvider"><fmt:message key="dataProvider.delete.confirmationMessage" /></c:set>
                <img class="action" alt="delete" title="delete" src="${pageContext.request.contextPath}/jsp/images/delete.png" onclick="javascript:deleteDataProvider('${confirmationMessageDataProvider}',
	    				'${repox:encode(dataProvider.id)}');" />
                <%-- Name --%>
                <stripes:link href="/dataProvider/ViewDataProvider.action?dataProviderId=${repox:encode(dataProvider.id)}">
                    <c:out escapeXml="true" value="${dataProvider.name}"/>
                </stripes:link>
            </td>

            <td class="${cellClass}" style="text-align: left">
                <%-- Description --%>
                <c:out value="${dataProvider.description}"/>
            </td>
            <td class="${cellClass}" style="text-align: left">
                <%-- Data Provider Type --%>
                <c:out value="${dataProvider.dataSetType}"/>
            </td>
            <td class="${cellClass}" style="text-align: left">
                <%-- Name code --%>
                <c:out value="${dataProvider.nameCode}"/>
            </td>
            <td class="${cellClass}" style="text-align: left">
                <%-- Homepage --%>
                <c:out value="${dataProvider.homePage}"/>
            </td>

        </tr>
    </c:forEach>

    <tr><td colspan="7">
        <stripes:link href="/dataProvider/CreateEditDataProvider.action">
            ...<fmt:message key="dataProvider.create" />...
            <stripes:param name="aggregatorId" value="${actionBean.aggregator.id}"/>
        </stripes:link>
    </td></tr>
</table>
</body>
</html>
