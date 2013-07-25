<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>

    <head>
        <title>
            <fmt:message key="aggregator.create" />
        </title>
    </head>

    <body>

        <stripes:errors />
        <stripes:messages />

        <stripes:form action="/aggregator/CreateEditAggregator.action" acceptcharset="UTF-8">
            <fieldset>

                <legend><fmt:message key="aggregator" /></legend>

                <p>
                    <label for="common.name"><fmt:message key="common.name" /><em>*</em> </label>
                    <stripes:text name="name"/>
                </p>

                <p>
                    <label for="aggregator.url"><fmt:message key="aggregator.url" /></label>
                    <stripes:text name="url"/>
                </p>

                <p>
                    <label for="aggregator.code"><fmt:message key="aggregator.code" /><em>*</em> </label>
                    <stripes:text name="code"/>
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
