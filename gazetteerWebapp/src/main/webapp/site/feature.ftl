<#ftl encoding="UTF-8"
	ns_prefixes={
    "rdf":"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	"rdfs":"http://www.w3.org/2000/01/rdf-schema#",
    "gaz":"http://www.digmap.eu/gazetteer/version1.0#",
    "gml":"http://www.opengis.net/gml"}
/>
<#include "taglib.ftl"/>
<#escape x as x?html>


<#assign doc=action.feature>

<#assign feature=doc?children[0]?children[0]>

<#assign id=action.identifier>

<!-- ${feature["@rdf:ID"]?string} -->

<h1 class="text" style="margin:4px 0px 4px 0px;">Recurso ${id}</h1>


<table style="width:100%" cellspacing="5"><tr><td valign="top" width="50%">

<table cellspacing="0" cellpadding="0" style="width:100%"><tr><td>

<!-- METADATA -->
<fieldset>
	<legend>Registration</legend>
<table class="record">
<!-- Ids -->
<tr><th>Id:</th><td>${id}</td></tr>
<#assign source=feature["gaz:belongsTo"]["gaz:Source"]["@rdf:resource"]!>
<#if source[0]??>
<tr><th>Source:</th><td><a target="_new" href="${source}">${source}</a></td></tr>
</#if>
<!-- Dates -->
<#list feature["gaz:metadata"] as metadata>
	<#list metadata["gaz:creationDate"] as creation>
<tr><th>Created:</th><td>${creation}</td></tr>
	</#list>
	<#list metadata["gaz:modificationDate"] as mod>
<tr><th>Modified:</th><td>${mod}</td></tr>
	</#list>
</#list>
</table>
</fieldset>

<!-- DESCRIPTION -->
</td></tr><tr><td>
<fieldset>
	<legend>Description</legend>
<table class="record">
<!-- Names -->
<#assign name=feature["gaz:hasName"]["gaz:Name"]>
<#if name[0]??>
<tr><th>Name:</th><td><@renderName/></td></tr>
</#if>
<#assign alts=feature["gaz:hasAltName"]>
<#if alts[0]??>
<tr><th>Alternative names:</th><td><@renderAltNames/></td></tr>
</#if>
<!-- Description -->
<#list feature["gaz:description"] as description>
<tr><th>Description:</th><td>${description} (${description["@xml:lang"]})</td></tr>
</#list>
<!-- Notes -->
<#list feature["gaz:note"] as note>
<tr><th>Note:</th><td>${note} (${note["@xml:lang"]})</td></tr>
</#list>
<!-- Links -->
<#list feature["gaz:link"] as link>
<tr><th>Links:</th><td>${link["gaz:url"]}</td></tr>
</#list>
</table>
</fieldset>

<!-- DATES -->
<#assign dates=feature["gaz:during"]>
<#if dates[0]??>
</td></tr><tr><td>
<fieldset>
	<legend>Dates</legend>
<table class="record">
<#list dates["gaz:TimeInterval"] as date>
<tr><th>Date:</th><td><i>from</i> ${date["@start"]} <i>to</i> ${date["@end"]}</td></tr>
</#list>
</table>
</fieldset>
</#if>

<!-- CLASSIFICATION -->
</td></tr><tr><td>
<fieldset>
	<legend>Classification</legend>
	<table class="record">
<!-- Classes -->

<tr><th>Class(es):</th><td>
<#list feature["rdfs:type"]["@rdf:resource"] as class><@renderClass class=class/></#list>,
<#list feature["gaz:altType"]["@rdf:resource"] as class><@renderClass class=class/></#list>
</td></tr>
	</table>
</fieldset>

<!-- DATA -->
<#assign code=feature["gaz:countryCode"]>
<#assign postal=feature["gaz:postalCode"]>
<#assign population=feature["gaz:population"]>
<#if code[0]?? || postal[0]?? || population[0]??>
</td></tr><tr><td>
<fieldset>
	<legend>Data</legend>
	<table class="record">
<!-- Codes -->
<#if code[0]??>
<tr><th> Country Code:</th><td>${code}</td></tr>
</#if>
<#if postal[0]??>
<tr><th>Postal Code(s):</th><td><@renderPostals/></td></tr>
</#if>
<#if population[0]??>
<tr><th>Population:</th><td>${population}</td></tr>
</#if>
	</table>
</fieldset>
</#if>

<!-- RELATIONSHIPS -->
</td></tr><tr><td>
<fieldset>
	<legend>Relationships</legend>
	<table class="record">

<!-- Address -->
<#assign relationships=feature["gaz:inContinent"]>
<#if relationships[0]??>
<tr><th>Continent:</th><td><@renderRels/></td></tr>
</#if>
<#assign relationships=feature["gaz:inCountry"]>
<#if relationships[0]??>
<tr><th>Country:</th><td><@renderRels/></td></tr>
</#if>

<!-- Hierarchy Information -->
<#assign relationships=feature["gaz:partOf"]>
<#if relationships[0]??>
<tr><th style="white-space:nowrap">Part Of:</th><td><@renderRels/></td></tr>
</#if>
<#assign relationships=feature["gaz:contains"]>
<#if relationships[0]??>
<tr><th>Contains:</th><td><@renderRels/></td></tr>
</#if>
<#assign relationships=feature["gaz:adjacentTo"]>
<#if relationships[0]??>
<tr><th>Adjacent:</th><td><@renderRels/></td></tr>
</#if>
<#assign relationships=feature["gaz:nearby"]>
<#if relationships[0]??>
<tr><th>Nearby:</th><td><@renderRels/></td></tr>
</#if>
	</table>
</fieldset>



<!-- EXTERNAL INFORMATION -->

</td></tr><tr><td>
<fieldset>
	<legend>External Information</legend>
	<table class="record">
<!-- Articles -->
<#assign resources=feature["gaz:sameAsArticle"]>
<#if resources[0]??>
<tr><th>Articles:</th><td><@renderResources/></td></tr>
</#if>
<#assign resources=feature["gaz:wikipediaArticle"]>
<#if resources[0]??>
<tr><th>Wikipedia:</th><td><@renderResources/></td></tr>
</#if>
<!-- Maps -->
<#assign resources=feature["gaz:inMap"]>
<#if resources[0]??>
<tr><th>Maps:</th><td><@renderResources/></td></tr>
</#if>
	</table>
</fieldset>

</td></tr></table>

<!-- FOOTPRINTS -->

</td><td valign="top" width="50%">

<#assign foot=feature["gaz:hasFootprint"]["gaz:Footprint"]!>
<#if foot[0]??>

<#assign box=feature["gml:boundedBy"]>
<#assign center=feature["gml:centerOf"]>

<table cellspacing="0" cellpadding="0"><tr><td>

<fieldset>
	<legend>Geographic Information</legend>
	<table class="record">
		<#assign geo=center>
		<tr><th>Center:</th><td><@renderGeometry/></td></tr>
		<#assign geo=box>
		<tr><th>Bounding Box:</th><td><@renderGeometry/></td></tr>
		<#list feature["gaz:hasFootprint"]["gaz:Footprint"] as foot>
		<tr><th>Footprint:</th><td><@wkt var="foot"/></td></tr>
		</#list>
	</table>
</fieldset>

</td></tr><tr><td>

<fieldset>
	<legend>Earth's Location</legend>
	<#assign coord=center["gml:Point"]["gml:coord"]!>
	<#if coord[0]??>
	<center>
	<img width="500px" height="200px" src="http://nail.digmap.eu/thumbnail.jsp?url=reliefmap&width=400&height=200&radius=10&transparency=false&lat=${coord["gml:X"]}&lon=${coord["gml:Y"]}"/></center>
	</#if>
</fieldset>

</td></tr><tr><td>

<fieldset>
	<legend>Zoom to Location</legend>

<!--<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAfFyRD4FCo2aSqqgi5tGMDBSp2bLliVIpYzSDPXNtEYWhruI3JBScDDlbzCB3nxHuV4dLUNTZ-tzK3A" type="text/javascript"></script>-->
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=true&amp;key=ABQIAAAAfFyRD4FCo2aSqqgi5tGMDBQC_Gt6LQGO31K3ivihSbLtDI7QGxQD2KC2efwVh-kSwA91nuwNgHFTKA" type="text/javascript"></script>

<script type="text/javascript">
    //<![CDATA[

	function initLocationMap(center) {
        var map = new GMap2(document.getElementById("map"));
        map.setCenter(center, 8);
		map.addControl(new GOverviewMapControl());
		map.addControl(new GSmallMapControl());
		map.addControl(new GMapTypeControl());
		map.enableDoubleClickZoom();
		map.setMapType(G_HYBRID_MAP);
		var marker = new GMarker(center);
        map.addOverlay(marker);
	}

    function load() {
		if(GBrowserIsCompatible()) {
<#if coord[0]??>
	initLocationMap(new GLatLng(${coord["gml:X"]},${coord["gml:Y"]}));
</#if>
		}
	}

    //]]>
</script>
	<div id="map" style="border: 1px solid black; width: 500px; height: 300px;"/>

</fieldset>

</td></tr></table>

<#else>

<script type="text/javascript">
    //<![CDATA[
	 function load() {
	 }

    //]]>
</script>

</#if>

</td></tr>
<tr><td colspan="2" width="100%">
	<table cellspacing="0" cellpadding="0" style="width:100%"><tr><td>
	<ul class="tabpane">
	<#list action.schemas as schema>
		<@url _var="page" _url="services/urn" id=id namespace=schema.namespace/>
		<li><a id="preview" title="${schema.name}" href="#preview" onclick="window.metadata.location='${page}';">${schema.prefix}</a></li>
	</#list>
	</ul>
	</td></tr><tr><td>
	<@url _var="url" _url="services/urn" id=id namespace="http://xldb.di.fc.ul.pt/geo-net.owl#"/>
	<iframe src="${url}" name="metadata" height="350" width="100%">
	</td></tr></table>
</td></tr>
</table>

</#escape>