<#ftl encoding="ISO-8859-1"/>

<h1 class="text">Acesso ao Gazetteer por Identificadores Unívocos</h1>

<form name="urn" method="post" action="urn.action">
<table class="record">
<tr><th>ID:</th>
<td>
	<input name="id" size="100" value="<#if Parameters.id?exists>${Parameters.id}</#if>" type="text">
	<input type="submit" name="button" value="Get"/>
</td></tr>
<tr><th>Esquema:</th>
<td>
	<#list action.schemas as schema>
		<input name="namespace" value="${schema.namespace}" type="radio">
		<b>${schema.prefix}</b> = ${schema.name}<br>
	</#list>
</td></tr>

</td></tr>

</table>
</form>

<#if Parameters.id?exists>
<#if Parameters.namespace?exists>
<@s.url value="feature.action" id="page" identifier="${Parameters.id}"/>
<h1 class="text">Recurso: <a href="${page}">${Parameters.id}</a></h1>
<table>
<tr><td colspan="2">
<@s.url value="services/urn" id="url" includeParams="all"/>
<iframe src="${url}" height="350" width="650">
</td></tr>
</table>
</#if>
</#if>
