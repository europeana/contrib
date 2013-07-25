<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<c:if test="${actionBean.dataSourceClass.name == 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter'}">
	<div class="selected"><fmt:message key="common.folder" /></div>
</c:if>
<c:if test="${actionBean.dataSourceClass.name != 'pt.utl.ist.repox.marc.DataSourceDirectoryImporter' && not actionBean.editing}">
	<div>
		<stripes:link href="/dataProvider/CreateEditDataSourceDirectoryImporter.action?dataSource.metadataFormat=ISO2709">
			<fmt:message key="common.folder" />
			<stripes:param name="dataProviderId" value="${actionBean.dataProviderId}"/>
		</stripes:link>
	</div>
</c:if>

<c:if test="${actionBean.dataSourceClass.name == 'pt.utl.ist.repox.oai.DataSourceOai'}">
	<div class="selected"><fmt:message key="dataSource.oai.short" /></div>
</c:if>
<c:if test="${actionBean.dataSourceClass.name != 'pt.utl.ist.repox.oai.DataSourceOai' && not actionBean.editing}">
	<div>
		<stripes:link href="/dataProvider/CreateEditDataSourceOai.action">
			<fmt:message key="dataSource.oai.short" />
			<stripes:param name="dataProviderId" value="${actionBean.dataProviderId}"/>
		</stripes:link>
	</div>
</c:if>

<c:if test="${actionBean.dataSourceClass.name == 'pt.utl.ist.repox.z3950.DataSourceZ3950'}">
	<div class="selected"><fmt:message key="dataSource.z3950.short" /></div>
</c:if>
<c:if test="${actionBean.dataSourceClass.name != 'pt.utl.ist.repox.z3950.DataSourceZ3950' && not actionBean.editing}">
	<div>
		<stripes:link href="/dataProvider/CreateEditDataSourceZ3950.action">
			<fmt:message key="dataSource.z3950.short" />
			<stripes:param name="dataProviderId" value="${actionBean.dataProviderId}"/>
		</stripes:link>
	</div>
</c:if>

<!-- 
<c:if test="${actionBean.dataSourceClass.name == 'pt.utl.ist.repox.horizon.DataSourceHorizon'}">
	<div class="selected"><fmt:message key="dataSource.horizon.short" /></div>
</c:if>
<c:if test="${actionBean.dataSourceClass.name != 'pt.utl.ist.repox.horizon.DataSourceHorizon' && not actionBean.editing}">
	<div>
		<stripes:link href="/dataProvider/CreateEditDataSourceHorizon.action">
			<fmt:message key="dataSource.horizon.short" />
			<stripes:param name="dataProviderId" value="${actionBean.dataProviderId}"/>
		</stripes:link>
	</div>
</c:if>
 -->