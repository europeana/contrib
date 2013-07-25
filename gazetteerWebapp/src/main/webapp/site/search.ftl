<#ftl encoding="UTF-8"
	ns_prefixes={
    "rdf":"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	"rdfs":"http://www.w3.org/2000/01/rdf-schema#",
    "gaz":"http://www.digmap.eu/gazetteer/version1.0#",
    "gml":"http://www.opengis.net/gml"}
/>
<#include "taglib.ftl"/>

<#macro searchBar page pages>

<#assign lpage = (page - 5)?int/>
<#if lpage &lt; 1><#assign lpage = 1/></#if>
<#assign upage = (page + 5)?int/>
<#if upage &gt; pages><#assign upage = pages/></#if>

<table cellspacing="2" cellspadding="0"><tr>
<#if page &gt; 1>
	<td style="padding: 0 5 0 5"><@s.url value="" id="_url" includeParams="all" page=1/><a href="${_url}">&lt;&lt;</a></td>
	<td style="padding: 0 5 0 5"><@s.url value="" id="_url" includeParams="all" page=(page-1)/><a href="${_url}">&lt;</a></td>
</#if>
<#list lpage..upage as cursor>
	<#if page=cursor>
	<td>${cursor}</td>
	<#else>
	<td><@s.url value="" id="_url" includeParams="all" page=cursor/><a href="${_url}">${cursor}</a></td>
	</#if>
</#list>
<#if page &lt; pages>
	<td style="margin: 0 5 0 5"><@s.url value="" id="_url" includeParams="all" page=(page+1)/><a href="${_url}">&gt;</a></td>
	<td style="margin: 0 5 0 5"><@s.url value="" id="_url" includeParams="all" page=pages/><a href="${_url}">&gt;&gt;</a></td>
</#if>
</tr></table>
</#macro>

<#if action.total=0>

<table class="search_result" cellspacing="4" cellpadding="0">
<tr><th colspan="3" width="100%">
<table cellspacing="0" cellpadding="0" width="100%"><tr>
	<td align="left"><b>...all</b></td>
	<td align="right">No results were found for "<b>${action.name}</b>".</td>
</tr></table>
</table>

<#else>

<table class="search_result" cellspacing="4" cellpadding="0">

<tr><th width="100%">
<table cellspacing="0" cellpadding="4" width="100%"><tr>
	<td align="right">Results: <b>${action.lowerBound}</b> - <b>${action.upperBound}</b> of about <b>${action.total}</b> for "<b>${action.name}</b>".</td>
</tr></table>
</th></tr>

<tr><td>
<table style="width:100%;height:100%;" cellspacing="0" cellspadding="0">
<tr><td colspan="4" style="background-color: white; padding: 0px 0px 4px 0px"><img src="style/imgs/dot.png" style="height:1px; width: 100%;"/></td></tr>
<#list action.result as id>
	<#assign doc=action.getFeature(id)!>
	<#if doc[0]??>
		<#assign feature=doc?children[0]?children[0]>
		<#assign name=feature["gaz:hasName"]["gaz:Name"]>
		<#assign alts=feature["gaz:hasAltName"]>
		<#assign center=feature["gml:centerOf"]["gml:Point"]["gml:coord"]!>
		<@s.url value="feature.action" id="_url" identifier="${id}" includeParams="none"/>
		<tr <#if id_index%2=1>style="background-color:rgb(239,239,239)"</#if>>
			<td style="width:20px;height:100%">
				<table style="width:100%;height:100%"><tr><td style="width:100%;height:100%" class="searchIndex">${action.lowerBound+id_index}</td></tr></table>
			</td>
			<td width="140">
			<#if center[0]??>
				<img src="http://nail.digmap.eu/thumbnail.jsp?url=reliefmap&width=100&height=60&transparency=false&lat=${center["gml:X"]}&lon=${center["gml:Y"]}"/>
			</#if>
			</td>
			<td style="vertical-align:top" width="100%">

			<table style="height: 100%; width:100%">
			<tr>
			<td class="tagTitle">Name(s)</td>
			<td class="tagContent"><a class="text" href="${_url}"><@renderName/><#if alts[0]??>, <@renderAltNames/></#if></a></td>
			</tr>
			<#assign class=feature["rdfs:type"]["@rdf:resource"]!/>
			<#if class[0]??>
			<tr>
				<td class="tagTitle">Type</td><td style="height:0%" class="tagContent">
					<@renderClass class=class/>, <#list feature["gaz:altType"]["@rdf:resource"] as class><@renderClass class=class/></#list>
				</td>
			</tr>
			</#if>
			</table>
			</td>
			<td width="0px" align="center" valign="middle">
				<#assign source=feature["gaz:belongsTo"]["gaz:Source"]["@rdf:resource"]!>
				<#if source[0]??>
					<img height="40" src="sources/<@sourceImage value=source+".png"/>">
				</#if>
			</td>
		</tr>
		
		<tr><td colspan="4" style="background-color: white; padding: 4px 0px 4px 0px"><img src="style/imgs/dot.png" style="height:1px; width: 100%;"/></td></tr>

	</#if>
</#list>
</table></td></tr>

	<tr><td align="center">

<#assign pages = action.totalPages/>
<#assign cursor = action.page/>

<#assign lpage = (cursor - 5)?int/>
<#if lpage &lt; 1><#assign lpage = 1/></#if>
<#assign upage = (cursor + 5)?int/>
<#if upage &gt; pages><#assign upage = pages/></#if>

		<table class="pagebar" cellspacing="10" cellpadding="0"><tr>
		<#if cursor &gt; 1>
			<@s.url value="" id="_url" includeParams="all" page=1/>
			<td><a href="${_url}">&lt;&lt;</a></td>
			<@s.url value="" id="_url" includeParams="all" page=(cursor-1)/>
			<td><a href="${_url}">&lt;</a></td>
		</#if>
		<#list lpage..upage as page>
			<#if page=cursor>
			<td class="cursor">${page}</td>
			<#else>
			<@s.url value="" id="_url" includeParams="all" page=page/>
			<td><a href="${_url}">${page}</a></td>
			</#if>
		</#list>
		<#if cursor &lt; pages>
			<@s.url value="" id="_url" includeParams="all" page=(page+1)/>
			<td><a href="${_url}">&gt;</a></td>
			<@s.url value="" id="_url" includeParams="all" page=pages/>
			<td><a href="${_url}">&gt;&gt;</a></td>
		</#if>
		</tr></table>
	</td></tr>
</table>

</#if>