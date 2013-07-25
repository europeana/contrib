<#ftl encoding="UTF-8"/>
<#include "taglib.ftl"/>

<#assign ontologies=action.fttOntologies>

<table class="list" width="100%" cellpadding="3" cellspacing="2">
<tr><th colspan="3">Thesaurus</th></tr>
<tr><th>Namespace</th><th>Name</th><th>Description</th></tr>
<#list ontologies as onto>
<tr>
	<@url _var="page" _url="thesaurus.page.action" id=onto.namespace/>
	<td><a href="${page}">${onto.namespace}<a></td>
	<#assign label = onto.getDatatypeByPrefix('rdfs','label')!>
	<td><#if label[0]??>${label}</#if></td>
	<#assign comment = onto.getDatatypeByPrefix('rdfs','comment')!>
	<td><#if comment[0]??>${comment}</#if></td>
</tr>
</#list>
</table>
