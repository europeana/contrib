<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<html>
    <head>
        <title>
            <fmt:message key="aggregator.edit" />
        </title>
    </head>

    <form:form action="editCreateAggregator.html" modelAttribute="aggregatorForm">
        <form:hidden path="aggregator_id"/>
        <fieldset>

            <legend><fmt:message key="aggregator" /></legend>

            <p>
                <label for="aggregator_name"><fmt:message key="aggregator.name" /><em>*</em></label>
                <form:input path="aggregator_name"></form:input>
                <font color=red><form:errors path="aggregator_name" cssClass="error"/></font>
            </p>

            <p>
                <label for="aggregator_nameCode"><fmt:message key="common.nameCode" /><em>*</em></label>
                <form:input path="aggregator_nameCode"></form:input>
                <font color=red><form:errors path="aggregator_nameCode" cssClass="error"/></font>
            </p>

            <p>
                <label for="aggregator_homePage"><fmt:message key="aggregator.url" /></label>
                <form:input path="aggregator_homePage"></form:input>
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
