<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>


<p>
    <label for="dataSource.name"><fmt:message key="dataSource.name" /><em>*</em> </label>
    <stripes:text name="dataSource.name"/>
</p>

<p>
    <label for="dataSource.nameCode"><fmt:message key="common.nameCode" /><em>*</em> </label>
    <stripes:text name="dataSource.nameCode"/>
</p>

<p>
    <label for="dataSource.exportPath"><fmt:message key="dataSource.exportPath" /></label>
    <stripes:text name="dataSource.exportPath"/>
</p>


<div class="headerDiv"><fmt:message key="common.output" /></div>
<p>
	<label for="dataSource.id"><fmt:message key="dashboard.table.recordSet" /> 
	(<fmt:message key="common.noSpacesAllowed" />)<em>*</em> </label>
    <stripes:text name="dataSource.id" />
</p>
<p>
 	<label for="dataSource.description"><fmt:message key="common.description" /><em>*</em> </label>
	<stripes:text name="dataSource.description" />
</p>

<c:forEach var="currentSelectedId" items="${actionBean.selectedTransformationIds}" varStatus="status">
	<p>
		<label for="selectedTransformationIds[${status.index}]"><fmt:message key="common.transformation" /> 
			<c:if test="${status.index == 0}"><stripes:submit name="addTransformation"><fmt:message key="common.add" /></stripes:submit></c:if>
		</label>
		<stripes:select name="selectedTransformationIds[${status.index}]">
			<stripes:option label="-" value="" />
			<c:forEach var="metadataTransformationEntry" items="${actionBean.metadataTransformations}">
				<c:forEach var="metadataTransformation" items="${metadataTransformationEntry.value}">
					<stripes:option value="${metadataTransformation.id}">
						${metadataTransformation.sourceFormat} <fmt:message key="common.to" /> ${metadataTransformation.destinationFormat}:
						${metadataTransformation.id}
					</stripes:option>
					
				</c:forEach>
			</c:forEach>
		</stripes:select>
		<c:if test="${status.index == 0}">
			<stripes:link href="/mapMetadata/MapMetadata.action"><fmt:message key="common.new" /></stripes:link>
			<c:if test="${actionBean.dataSource.numberRecords > 0}">
				| <stripes:link href="/mapMetadata/MapMetadata.action?createFromDataSource=&id=${actionBean.dataSource.id}" >
					<fmt:message key="mapMetadata.createFromRecords" /></stripes:link>
			</c:if>
		</c:if>
	</p>
</c:forEach>
  	
<p class="right"><stripes:submit name="submitDataSource"><fmt:message key="common.save" /></stripes:submit></p>
<p class="left"><stripes:submit name="cancelDataSource"><fmt:message key="common.cancel" /></stripes:submit>&nbsp;&nbsp;</p>

