<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<html>
    <head>
        <title>
            <fmt:message key="dataProvider.create" />
        </title>
    </head>

    <c:if test="${!empty message}">
        <div class="messages"><c:out value="${message}"/></div>
    </c:if>

    <form:form action="editCreateDataProvider.html" modelAttribute="dataProviderForm">

        <form:hidden path="dataProvider_id"/>

        <input id="aggregatorId" name="aggregatorId" type="hidden" value="${aggregatorId}"/>

        <fieldset>

            <legend><fmt:message key="aggregator" /></legend>

            <p>
                <label for="dataProvider_country">
                    <fmt:message key="common.country" />
                </label>
                <form:select path="dataProvider_country">
                    <form:options items="${countryList}"></form:options>
                </form:select>
            </p>

            <p>
                <label for="dataProvider_name">
                    <fmt:message key="dataProvider.nameHeader" /><em>*</em>
                </label>
                <form:input path="dataProvider_name"></form:input>
                <font color=red><form:errors path="dataProvider_name"/></font>
            </p>

            <p>
                <label for="dataProvider_description">
                    <fmt:message key="common.description" />
                </label>
                <form:input path="dataProvider_description"></form:input>
            </p>

            <p>
                <label for="dataProvider_dataSetType">
                    <fmt:message key="dataProvider.type" />
                </label>
                <form:select path="dataProvider_dataSetType">
                    <form:options items="${typeList}"></form:options>
                </form:select>
            </p>

            <p>
                <label for="dataProvider_nameCode">
                    <fmt:message key="common.nameCode" /><em>*</em>
                </label>
                <form:input path="dataProvider_nameCode"></form:input>
                <font color=red><form:errors path="dataProvider_nameCode"/></font>
            </p>

            <p>
                <label for="dataProvider_homePage">
                    <fmt:message key="dataProvider.url" />
                </label>
                <form:input path="dataProvider_homePage"></form:input>
                <font color=red><form:errors path="dataProvider_homePage"/></font>
            </p>

            <p class="right">
                <c:set var="save_txt"><fmt:message key="common.save"/></c:set>
                <input type="submit" name="submitAggregator" value="${save_txt}"/>
            </p>

            <p class="left">
                <c:set var="cancel_txt"><fmt:message key="common.cancel"/></c:set>
                <input name="cancel" value="${cancel_txt}" type="submit" />
                &nbsp;&nbsp;
            </p>

        </fieldset>
    </form:form>

</html>
