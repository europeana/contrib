<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
    <head>
        <title>
            <c:choose>
		        <c:when test="${not actionBean.editing}"><fmt:message key="aggregator.create" /></c:when>
		        <c:otherwise><fmt:message key="aggregator.edit" /></c:otherwise>
		    </c:choose>
        </title>
    </head>
    <body>
        <stripes:errors />
        <stripes:messages />

        <stripes:form action="/aggregator/CreateEditAggregator.action" acceptcharset="UTF-8">
            <stripes:hidden name="aggregatorId" value="${actionBean.aggregator.id}"/>
            <c:if test="${actionBean.editing}"><stripes:hidden name="editing" value="true" /></c:if>
            <fieldset>

                <legend><fmt:message key="aggregator" /></legend>

                <p>
                    <label for="aggregator.name"><fmt:message key="aggregator.name" /><em>*</em> </label>
                    <stripes:text name="aggregator.name"/>
                </p>

                <p>
                    <label for="aggregator.nameCode"><fmt:message key="common.nameCode" /><em>*</em> </label>
                    <stripes:text name="aggregator.nameCode"/>
                </p>

                <p>
                    <label for="aggregator.homePage"><fmt:message key="aggregator.url" /></label>
                    <stripes:text name="aggregator.homePage"/>
                </p>

                <p class="right">
                    <stripes:submit name="submitAggregator"><fmt:message key="common.save" /></stripes:submit>
                </p>

                <p class="left">
                    <stripes:submit name="cancel"><fmt:message key="common.cancel" /></stripes:submit>
                    &nbsp;&nbsp;
                </p>

            </fieldset>
        </stripes:form>

    </body>

</html>
