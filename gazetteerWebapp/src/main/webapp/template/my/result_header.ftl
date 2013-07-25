<#ftl encoding="ISO-8859-1"/>
<#assign cssClass=parameters.cssClass?default('result')>

<table cellspacing="0" cellpadding="0">
<tr>
	<th class="${cssClass}" width="100%">${parameters.count} itens</th>
	<th class="${cssClass}">
		<table cellspacing="0" cellpadding="0" style="font-family: Arial"><tr>
			<td><@s.component template="toolbar_button.ftl" cssClass="cursor" action="#" text="<"/></td>
			<td><@s.component template="toolbar_button.ftl" cssClass="cursor" action="#" text="<<"/></td>
			<td><@s.component template="toolbar_button.ftl" cssClass="cursor" action="#" text=">>"/></td>
			<td><@s.component template="toolbar_button.ftl" cssClass="cursor" action="#" text=">"/></td>
		</tr></table>
	</th>
	<th class="${cssClass}" style="width:200">
		<@s.component template="toolbar_button.ftl" action="products_thumb.action" pic="imgs/view_thumb.png"/>
	</th>
	<th class="${cssClass}" style="text-align:center">
		<@s.component template="toolbar_button.ftl" action="products_list.action" pic="imgs/view_list.png"/>
	</th>
</tr>
</table>