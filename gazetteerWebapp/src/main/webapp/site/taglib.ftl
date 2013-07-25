<#ftl encoding="UTF-8"
	ns_prefixes={
    "rdf":"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "gaz":"http://www.digmap.eu/gazetteer/version1.0#",
    "gml":"http://www.opengis.net/gml"}
/>

<#assign wkt = "freemarker.ext.dom.WKTTemplate"?new()>

<#assign for = "freemarker.template.ForTemplate"?new()>

<#assign url = "freemarker.template.URLTemplate"?new()>

<#assign sourceImage = "freemarker.template.SourceImage"?new()>

<#macro renderFootprint>
	<#list geo["gaz:Footprint"] as foot>
		<#assign point = foot["gml:Point"]!>
		<#if point[0]??>
		<#list point["gml:coord"] as coord><b>( </b>${coord["gml:X"]}<b> , </b>${coord["gml:Y"]}<b> )</b> </#list>
		</#if>
		<#assign envelope = foot["gml:Envelope"]!>
		<#if envelope[0]??><@renderEnvelope/></#if>
	</#list>
</#macro>


<#macro renderGeometry>
	<#assign point = geo["gml:Point"]!>
	<#if point[0]??>
	<#list point["gml:coord"] as coord><b>( </b>${coord["gml:X"]}<b> , </b>${coord["gml:Y"]}<b> )</b> </#list>
	</#if>
	<#assign envelope = geo["gml:Envelope"]!>
	<#if envelope[0]??><@renderEnvelope/></#if>
</#macro>

<#macro renderPoint>
<#if point[0]??>
	<#list point["gml:coord"] as coord><b>( </b>${coord["gml:X"]}<b> , </b>${coord["gml:Y"]}<b> )</b> </#list>
</#if>
</#macro>

<#macro renderEnvelope>
<#if envelope[0]??>
	<#assign point = envelope["gml:lowerCorner"]!>
	<@renderPoint/>
	<#assign point = envelope["gml:upperCorner"]!>
	<@renderPoint/>
</#if>
</#macro>


<#macro renderName><#list name as n><#assign lang = n["@xml:lang"]>${n}<#if lang[0]??>(${lang})</#if></#list></#macro>

<#macro renderAltNames>
	<#if alts[0]??>
		<#list alts["gaz:Name"] as name><#assign lang = name["@xml:lang"]>${name}<#if lang[0]??>(${name["@xml:lang"]})</#if><#if name_has_next>, </#if></#list>
	</#if>
</#macro>

<#macro renderClass class>
	<#assign term=action.classThesaurus.getTerm(class)!>
	<#if term[0]??>
		<#assign label = term.getDatatypeByPrefix('rdfs','label')!>
		<#assign comment = term.getDatatypeByPrefix('rdfs','comment')!>
		<#if label[0]??><a href="#${term.ID}" <#if comment[0]??>title='${comment}'</#if>>${label}</a><#else>${class}</#if></#if></#macro>

<#macro renderClassTerm term>
	<#if term[0]??>
		<#assign label = term.getDatatypeByPrefix('rdfs','label')!>
		<#assign comment = term.getDatatypeByPrefix('rdfs','comment')!>
		<#if label[0]??><a href="#${term.ID}" <#if comment[0]??>title='${comment}'</#if>>${label}</a><#else>${class}</#if>
	</#if>
</#macro>


<#macro renderRels>
<#list relationships as related>
	<#assign rel=related["@rdf:resource"]?string>
	<#assign doc2=action.getFeature(rel)!>
	<#if doc2[0]??>
		<#assign feature2=doc2?children[0]?children[0]>
		<@url _var="_url" _url="" identifier=rel/>
		<a href="${_url}">${feature2["gaz:hasName"]["gaz:Name"]}</a><#else>${rel}</#if><#if related_has_next>, </#if>
</#list>
</#macro>

<#macro renderResources>
<#list resources as rsrc>
	<#assign link=rsrc["@rdf:resource"]?string>
	<a target="_blank" href="${link}">${link}</a>
	<#if rsrc_has_next>, </#if>
</#list>
</#macro>

<#macro renderPostals>
<#list postal as pst>
	${pst}
	<#if pst_has_next>, </#if>
</#list>
</#macro>

<!-- -->

<#macro adlQuery key>
	<#switch key>
		<#case "get-capabilities">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <get-capabilities-request/>
</gazetteer-service>
			<#break>
		<#case "name">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <name-query operator="equals" text="lisboa"/>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "name&class">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <name-query operator="equals" text="lisboa"/>
        <class-query thesaurus="USGS Feature Type Thesaurus"
          term="stream"/>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "name&footprint_overlaps">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    xmlns:gml="http://www.opengis.net/gml"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <name-query operator="equals" text="Albers Wash"/>
        <footprint-query operator="overlaps">
          <gml:Box>
            <gml:coordinates>-110.69,34.65
              -120.35,42.34</gml:coordinates>
          </gml:Box>
        </footprint-query>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "name&status">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <name-query operator="equals" text="lisboa"/>
        <place-status-query status="former"/>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "class&footprint">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    xmlns:gml="http://www.opengis.net/gml"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <class-query thesaurus="USGS Feature Type Thesaurus"
          term="airport features"/>
        <footprint-query operator="within">
          <gml:Box>
            <gml:coordinates>-120,35
              -110,38</gml:coordinates>
          </gml:Box>
        </footprint-query>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "id">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <identifier-query identifier="567"/>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "rel&class">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <relationship-query relation="part of"
          target-identifier="567"/>
        <class-query thesaurus="ADL Feature Type Thesaurus"
          term="cemeteries"/>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "name&footprint_within">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">
  <query-request>
    <gazetteer-query>
      <and>
        <name-query operator="contains-all-words"
          text="cherokee"/>
        <footprint-query operator="within">
          <identifier>adlgaz-1-600-1c</identifier>
        </footprint-query>
      </and>
    </gazetteer-query>
    <report-format>standard</report-format>
  </query-request>
</gazetteer-service>
			<#break>
		<#case "debug">
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service
    xmlns="http://www.alexandria.ucsb.edu/gazetteer"
    version="1.2">  
  <query-request>
    <gazetteer-query>
      <identifier-query identifier="debug"/>
    </gazetteer-query>
    <report-format>extended</report-format>
  </query-request>
</gazetteer-service>
			<#break>
	</#switch>
</#macro>
