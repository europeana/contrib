<#ftl encoding="UTF-8"/>
<#include "taglib.ftl"/>

<#assign thesaurus=action.classThesaurus>

<#assign ontology=thesaurus.getOntology(action.id)>
<#if ontology[0]??>

<fieldset>
	<legend>Thesaurus</legend>
	<table class="record">
	<tr><th>Namespace:</th><td>${ontology.namespace}</td></tr>
	<#assign label = ontology.getDatatypeByPrefix('rdfs','label')!>
	<tr><th>Name:</th><td>${label}</td></tr>
	<#assign comment = ontology.getDatatypeByPrefix('rdfs','comment')!>
	<#if comment[0]??><tr><th>Description:</th><td>${comment}</td></tr></#if>
	</table>
</fieldset>


<#assign subClassOf=thesaurus.getTerm('http://www.w3.org/2000/01/rdf-schema#subClassOf')>
<#assign sameAs=thesaurus.getTerm('http://www.w3.org/2002/07/owl#sameAs')>
<#assign seeAlso=thesaurus.getTerm('http://www.w3.org/2000/01/rdf-schema#seeAlso')>
<#assign feature=thesaurus.getTerm('http://www.digmap.eu/gazetteer/version1.0#Feature')>
<#assign null=action.nullTerm>

<#macro renderRelNode ontology term level>
	<@renderRelLine ontology=ontology term=term level=level/>
	<#list action.getRelations(ontology, null, subClassOf, term) as rel>
		<@renderRelNode ontology=ontology term=rel.source level=(level+1)/>
	</#list>
</#macro>

<#macro renderOntology ontology>
	<#assign label = ontology.getDatatypeByPrefix('rdfs','label')!>
	<#assign comment = ontology.getDatatypeByPrefix('rdfs','comment')!>
	<@url _var="page" _url="thesaurus.page.action" id=ontology.namespace/>
	<a class="text" href="${page}" <#if comment[0]??>title='${comment}'</#if>>${label}</a>
</#macro>


<#macro renderTerm term>
	<#assign label = term.getDatatypeByPrefix('rdfs','label')!>
	<#assign comment = term.getDatatypeByPrefix('rdfs','comment')!>
	<a class="text" href="#${term.ID}" <#if label[0]??>title='${label}'</#if>>${term.ID}</a></#macro>

<#macro renderRelLine ontology term level>
	<tr><td>
		<table><tr>
		<#if level &gt; 0><td style="vertical-align:top"><#list 1..level as i>&nbsp;-&nbsp;</#list></td></#if>
		<#assign label = term.getDatatypeByPrefix('rdfs','label')!>
		<#assign comment = term.getDatatypeByPrefix('rdfs','comment')!>
		<td valign="top">
			<a class="text" id="${term.ID}" href="#${term.ID}" <#if comment[0]??>title='${comment}'</#if>>${term.ID}</a>
			<span style="white-space: nowrap"><#if label[0]??>(${label})</#if></span>
			<#list action.getRelations(ontology, term, seeAlso, null) as rel>
				<#if rel_index=0><p style="margin: 1pt; font-style: italic">(<b>see also</b>:</#if>
				<@renderTerm term=rel.target/><#if rel_has_next>,<#else>)</p></#if>
			</#list>
		</td>
		</tr></table>
	</td>
	<#list action.fttOntologies as onto>
		<#if onto!=ontology>
		<td>
			<#list action.getRelations(onto, term, sameAs, null) as rel>
				<@renderTerm term=rel.target/>
			</#list>
		</td>
		</#if>
	</#list>
	</tr>
</#macro>

<fieldset>
	<legend>Thesaurus Terms and Relationships</legend>
	<table  class="list" width="100%" cellpadding="3" cellspacing="2">
	<tr>
		<th style="white-space:nowrap"><@renderOntology ontology=ontology/></th>
		<#list action.fttOntologies as onto>
			<#if onto!=ontology>
			<th style="white-space:nowrap"><@renderOntology ontology=onto/></th>
			</#if>
		</#list>
		</tr>
	<#list action.getRelations(ontology, null, subClassOf, feature) as relation>
		<@renderRelNode ontology=ontology term=relation.source level=0/>
	</#list>
	</table>
	</#if>
</fieldset>
