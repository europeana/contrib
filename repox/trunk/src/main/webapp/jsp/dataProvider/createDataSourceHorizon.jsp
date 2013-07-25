<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="dataProvider" />: ${actionBean.dataProvider.name} - 
			<c:choose>
		        <c:when test="${not actionBean.editing}"><fmt:message key="dataSource.create" /></c:when>
		        <c:otherwise><fmt:message key="dataSource.edit" /></c:otherwise>
		    </c:choose>
		</title>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/dataProvider/CreateEditDataSourceHorizon.action" acceptcharset="UTF-8">
			<stripes:hidden name="dataProviderId" />
			<stripes:hidden id="lastHidden" name="dataSourceId" />
			<c:if test="${actionBean.editing}"><stripes:hidden name="editing" value="true" /></c:if>
			
			<table class="selectTable">
				<tr>
					<td class="selectCell">
						<c:import url="/jsp/dataProvider/dataSourceMenu.include.jsp" />
					</td>
					<td>
						<div class="headerDiv"><fmt:message key="dataSource" /></div>
						<p>
			    			<label for="dataSource.ip"><fmt:message key="dataSource.ip" /><em>*</em> </label>
				    		<stripes:text name="dataSource.ip" />
				    	</p>
						<p>
			    			<label for="dataSource.port"><fmt:message key="dataSource.port" /><em>*</em> </label>
				    		<stripes:text name="dataSource.port" />
				    	</p>
						<p>
			    			<label for="dataSource.database"><fmt:message key="dataSource.database" /><em>*</em> </label>
				    		<stripes:text name="dataSource.database" />
				    	</p>
						<p>
			    			<label for="dataSource.user"><fmt:message key="dataSource.user" /><em>*</em> </label>
				    		<stripes:text name="dataSource.user" />
				    	</p>
						<p>
			    			<label for="dataSource.password"><fmt:message key="dataSource.password" /><em>*</em> </label>
				    		<stripes:text name="dataSource.password" />
				    	</p>
						<p>
			    			<label for="dataSource.onlyCompleteCataloging"><fmt:message key="dataSource.onlyCompleteCataloging" /><em>*</em> </label>
							<stripes:select name="dataSource.onlyCompleteCataloging">
								<stripes:option label="False" value="false" />
								<stripes:option label="True" value="true" />
							</stripes:select>
				    	</p>
						<p>
			    			<label for="dataSource.onlyAvailableInDigital"><fmt:message key="dataSource.onlyAvailableInDigital" /><em>*</em> </label>
							<stripes:select name="dataSource.onlyAvailableInDigital">
								<stripes:option label="False" value="false" />
								<stripes:option label="True" value="true" />
							</stripes:select>
				    	</p>
						<c:import url="/jsp/dataProvider/createDataSource.include.jsp" />
					</td>
				</tr>
			</table>
		</stripes:form>
	</body>
</html>
