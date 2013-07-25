<#ftl encoding="ISO-8859-1"/>
<#assign cssClass=parameters.cssClass?default('toolbar_button')>
<a href="${parameters.action}" class="${cssClass}">
	<#if parameters.pic?exists>
	<img class="${cssClass}" src="${parameters.pic}"/>
	</#if>
	<#if parameters.text?exists>
	${parameters.text}
	</#if>
</a>