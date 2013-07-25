<#ftl encoding="ISO-8859-1"/>
<#assign cssClass=parameters.cssClass?default('button')>
<a href="${parameters.action}" class="${cssClass}" 
	<#if parameters.style?exists>style="${parameters.style}"</#if>
	<#if parameters.title?exists>title="${parameters.title}"</#if>
	<#if parameters.target?exists>target="${parameters.target}"</#if>>
	<#if parameters.img?exists>
	<img class="${cssClass}" src="${parameters.img}"/>
	</#if>
	<#if parameters.text?exists>
	<p class="${cssClass}">${parameters.text}</p>
	</#if>
</a>