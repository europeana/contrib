<#ftl encoding="ISO-8859-1"/>
<#assign cssClass=parameters.cssClass?default('button_product')>
<a href="product.action?id=${parameters.id}" class="${cssClass}"
	<#if parameters.style?exists>style="${parameters.style}"</#if>
	<#if parameters.target?exists>target="${parameters.target}"</#if>>
	<img class="${cssClass}" src="pics/${parameters.pic}"/>
	<p class="${cssClass}">${parameters.product}</p>
	<p class="${cssClass}">(€ ${parameters.price})</p>
</a>