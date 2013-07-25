<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
	<head>
		<title>
			<c:choose>
		        <c:when test="${not actionBean.editing}"><fmt:message key="dataProvider.create" /></c:when>
		        <c:otherwise><fmt:message key="dataProvider.edit" /></c:otherwise>
		    </c:choose>
		</title>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/dataProvider/CreateEditDataProvider.action" acceptcharset="UTF-8">
			<stripes:hidden name="aggregatorId" value="${actionBean.aggregator.id}"/>
            <stripes:hidden name="dataProviderId" value="${actionBean.dataProvider.id}"/>
            <c:if test="${actionBean.editing}"><stripes:hidden name="editing" value="true" /></c:if>
			<fieldset>
				<legend><fmt:message key="dataProvider" /></legend>
				<p>
                    <label for="dataProvider.country"><fmt:message key="common.country" /></label>
                    <stripes:select name="dataProvider.country">
                        <stripes:options-enumeration enum="eu.europeana.definitions.domain.Country"/>
                    </stripes:select>
				</p>
				<p>
	    			<label for="dataProvider.name"><fmt:message key="dataProvider.nameHeader" /><em>*</em> </label>
				    <stripes:text name="dataProvider.name" />
				</p>
	    		<p>
	    			<label for="dataProvider.description"><fmt:message key="common.description" /> </label>
						<stripes:text name="dataProvider.description" />
				</p>
                <p>
	    			<label for="dataProvider.dataSetType"><fmt:message key="dataProvider.type" /></label>
                    <stripes:select name="dataProvider.dataSetType">
                        <stripes:options-enumeration enum="eu.europeana.repox2sip.models.ProviderType"/>
                    </stripes:select>
				</p>
                <p>
                    <label for="dataProvider.nameCode"><fmt:message key="common.nameCode" /><em>*</em> </label>
                    <stripes:text name="dataProvider.nameCode"/>
                </p>

                <p>
                    <label for="dataProvider.homePage"><fmt:message key="dataProvider.url" /></label>
                    <stripes:text name="dataProvider.homePage"/>
                </p>

				<p class="right"><stripes:submit name="submitDataProvider"><fmt:message key="common.save" /></stripes:submit></p>
				<p class="left"><stripes:submit name="cancel"><fmt:message key="common.cancel" /></stripes:submit>&nbsp;&nbsp;</p>
				
			</fieldset>
		</stripes:form>
	</body>
</html>
