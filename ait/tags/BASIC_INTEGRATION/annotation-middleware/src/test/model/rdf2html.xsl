<?xml version="1.0"?>
<!-- 
    Style sheet to transform RDF descriptions to HTML
    Author: http://rhizomik.net/~roberto

	This work is licensed under the Creative Commons Attribution-ShareAlike License.
	To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/
	or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA.
	License: http://rhizomik.net/redefer/rdf2html.xsl.rdf
-->

<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
	
	<xsl:output media-type="text/xhtml" encoding="UTF-8" indent="yes" omit-xml-declaration="no"
		method="html" doctype-public="-//W3C//DTD XHTML+RDFa 1.0//EN" doctype-system="http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd"/>
	
	<xsl:strip-space elements="*"/>

	<xsl:param name="language">en</xsl:param>
	
	<xsl:template match="/">
		<html xml:lang="en" version="XHTML+RDFa 1.0" xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/xhtml; charset=UTF-8"/>
				<title>Rhizomik - ReDeFer - RDF2HTML</title>
				<link href="http://rhizomik.net/style/rhizomer.css" type="text/css" rel="stylesheet" />
			</head>
			<body>
		<xsl:apply-templates select="rdf:RDF"/>
				<div id="footlogo">
					<div id="logo">
						<a href="http://rhizomik.net">
							<img src="http://rhizomik.net/images/rhizomer.small.png" alt="Rhizomik"/> Powered by Rhizomik
						</a>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="rdf:RDF">
		<div id="metadata">
			<!-- Generate the xmlns for RDFa from those in the RDF/XML and attach to div#declarations -->
			<xsl:for-each select="/rdf:RDF/namespace::*[not(name()='' or name()='rdf' or name()='rdfs' or name()='xml')]">
				<xsl:attribute name="{ concat(name(), ':dummy-for-xmlns') }" namespace="{.}"/>
			</xsl:for-each>
			
			<!-- If no RDF descriptions... -->
			<xsl:if test="count(child::*)=0">
				<p>No data retrieved.</p>
			</xsl:if>
			<!-- If rdf:RDF has child elements, they are descriptions... -->
			<xsl:for-each select="child::*">
				<xsl:sort select="@rdf:about" order="ascending"/>
				<xsl:call-template name="rdfDescription"/>
			</xsl:for-each>
		</div>
	</xsl:template>
  	
	<xsl:template name="rdfDescription">
		<xsl:choose>
			<!-- RDF Description that contains more than labels (e.g. just type instead of rdf:Description) or is the only description -->
			<xsl:when test="(count(following-sibling::*)=0 and count(preceding-sibling::*)=0) or not(local-name()='Description') or
							*[not(name()='http://www.w3.org/2000/01/rdf-schema#') and not(local-name()='label')] | 
							@*[not(namespace-uri()='http://www.w3.org/2000/01/rdf-schema#') and not(local-name()='label' or local-name()='about')]">
				<div class="description" about="{@rdf:ID|@rdf:about}">
				<table>
					<xsl:call-template name="header"/>
					<xsl:call-template name="attributes"/>
					<xsl:call-template name="properties"/>
				</table>
				</div>
			</xsl:when>
			<xsl:otherwise><!-- Ignore RDF Descriptions with just labels --></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template name="embeddedRdfDescription">
		<xsl:choose>
			<!-- Embedded RDF Description that contains more than labels -->
			<xsl:when test="*[not(name()='http://www.w3.org/2000/01/rdf-schema#') and not(local-name()='label')] |
							@*[not(namespace-uri()='http://www.w3.org/2000/01/rdf-schema#') and not(local-name()='label' or local-name()='about')]">
				<table about="{@rdf:ID|@rdf:about}">
					<xsl:if test="not(@rdf:parseType='Resource')">
						<xsl:call-template name="header"/>
					</xsl:if>
					<xsl:call-template name="attributes"/>
					<xsl:call-template name="properties"/>
				</table>
			</xsl:when>
			<!-- Embeded RDF Description with just labels, just take resource -->
			<xsl:when test="parent::*[not(name()='rdf:RDF')]">
				<xsl:call-template name="resourceDetailLink">
					<xsl:with-param name="property" select="local-name(parent::*)"/>
					<xsl:with-param name="namespace" select="''"/>
					<xsl:with-param name="localname" select="@rdf:about"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><!-- Ignore other --></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Build the description header with the resource identifier and types, if available -->
	<xsl:template name="header">
		<xsl:if test="@rdf:ID|@rdf:about or not(local-name()='Description') or 
							count(*[namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type'])>0">
			<tr>
				<th colspan="2">					
					<xsl:if test="@rdf:ID|@rdf:about">
								<xsl:call-template name="resourceDetailLink">
									<xsl:with-param name="property" select="'about'"/>
									<xsl:with-param name="namespace" select="''"/>
									<xsl:with-param name="localname" select="@rdf:ID|@rdf:about|@rdf:aboutEach|@rdf:aboutEachPrefix|@rdf:bagID"/>
								</xsl:call-template>
					</xsl:if>
					<xsl:call-template name="types"/>
				</th>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="types">
		<!-- textual decoration if there are types-->
		<xsl:if test="not(local-name()='Description') or count(*[namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type'])>0">
			<xsl:text> a </xsl:text>
		</xsl:if>
		<!-- embedded rdf:type -->
		<xsl:if test="not(local-name()='Description')">
			<xsl:call-template name="resourceDetailLink">
				<xsl:with-param name="property" select="'type'"/>
				<xsl:with-param name="namespace" select="namespace-uri()"/>
				<xsl:with-param name="localname" select="local-name()"/>
			</xsl:call-template>
			<span about="{@rdf:ID|@rdf:about}" typeof="{name()}"/>
			<xsl:if test="*[namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type']">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:if>
		<!-- rdf:type properties -->
		<xsl:for-each select="*[namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type']">
			<xsl:variable name="name">
				<xsl:call-template name="get-name">
					<xsl:with-param name="uri" select="@rdf:resource"/>
				</xsl:call-template>
			</xsl:variable>
			<span about="{../@rdf:about|../@rdf:ID}" typeof="{name(namespace::*[starts-with(@rdf:resource,.)])}:{$name}"/>
			<xsl:call-template name="property-objects"/>
			<xsl:if test="following-sibling::*[namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type']">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="attributes">
		<xsl:for-each select="@*[not(namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#') and 
													not(local-name='about' or local-name='ID' or local-name='type')]">
			<xsl:sort select="local-name()" order="ascending"/>
			<tr>
				<td>
					<xsl:call-template name="resourceDetailLink">
						<xsl:with-param name="property" select="''"/>
						<xsl:with-param name="namespace" select="namespace-uri()"/>
						<xsl:with-param name="localname" select="local-name()"/>
					</xsl:call-template>
				</td>
				<td property="{name()}">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="property-objects">
		<xsl:choose>
			<xsl:when test="@rdf:resource">
				<xsl:call-template name="rdf_resource-attribute"/>
			</xsl:when>
			<xsl:when test="@rdf:parseType='Resource'">
				<xsl:call-template name="embeddedRdfDescription"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="child::*">
					<xsl:call-template name="embeddedRdfDescription"/>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="rdf_resource-attribute">
		<xsl:if test="@rdf:resource">
			<xsl:call-template name="resourceDetailLink">
				<xsl:with-param name="property" select="local-name()"/>
				<xsl:with-param name="namespace" select="''"/>
				<xsl:with-param name="localname" select="@rdf:resource"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="properties">
		<xsl:for-each select="*[not(namespace-uri()='http://www.w3.org/1999/02/22-rdf-syntax-ns#' and local-name()='type')]"> <!-- and not(namespace-uri()='http://www.w3.org/2000/01/rdf-schema#' and local-name()='label') -->
			<xsl:sort select="local-name()" order="ascending"/>
			<xsl:choose>
				<xsl:when test="text() and count(descendant::*)=0">
					<xsl:variable name="isPreferredLanguage">
						<xsl:call-template name="isPreferredLanguage"/>
					</xsl:variable>
					<xsl:if test="$isPreferredLanguage='true'">
					<tr>
						<td>
							<xsl:call-template name="resourceDetailLink">
								<xsl:with-param name="property" select="''"/>
								<xsl:with-param name="namespace" select="namespace-uri()"/>
								<xsl:with-param name="localname" select="local-name()"/>
							</xsl:call-template>
						</td>
						<td>
							<!--xsl:call-template name="property-attributes"/-->
							<xsl:call-template name="textDetailLink">
								<xsl:with-param name="property" select="local-name()"/>
								<xsl:with-param name="value" select="."/>
							</xsl:call-template>
							<span property="{name()}" content="{.}"/>
						</td>
					</tr>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
				<tr>
					<td>
						<xsl:call-template name="resourceDetailLink">
							<xsl:with-param name="property" select="''"/>
							<xsl:with-param name="namespace" select="namespace-uri()"/>
							<xsl:with-param name="localname" select="local-name()"/>
						</xsl:call-template>
					</td>
					<td>
						<span rel="{name()}" resource="{@rdf:resource|.//@rdf:about[1]}"/>
						<xsl:call-template name="property-objects"/>
					</td>
				</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="isPreferredLanguage">
		<xsl:variable name="element">
			<xsl:value-of select="name()"/>
		</xsl:variable>
		<xsl:choose>
			<!-- Firstly, select if is preferred language -->
			<xsl:when test="contains(@xml:lang,$language)">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<!-- Secondly, select default language version if there is not a preferred language version -->
			<xsl:when test="contains(@xml:lang,'en') and count(parent::*/*[name()=$element and contains(@xml:lang,$language)])=0">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<!-- Thirdly, select version without language tag if there is not preferred language nor default language version -->
			<xsl:when test="not(@xml:lang) and count(parent::*/*[name()=$element and contains(@xml:lang,$language)])=0 and 
									   count(parent::*/*[name()=$element and contains(@xml:lang,'en')])=0 ">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<!-- Otherwise, ignore -->
			<xsl:otherwise><xsl:value-of select="false()"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="getLabel">
		<xsl:param name="uri"/>
		<xsl:variable name="namespace">
			<xsl:call-template name="get-ns">
				<xsl:with-param name="uri" select="$uri"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="localname">
			<xsl:call-template name="get-name">
				<xsl:with-param name="uri" select="$uri"/>
			</xsl:call-template>
		</xsl:variable>		
		<xsl:choose>
			<xsl:when test="//*[@rdf:about=$uri]/rdfs:label[contains(@xml:lang,$language)]">
				<xsl:value-of select="//*[@rdf:about=$uri]/rdfs:label[contains(@xml:lang,$language)]"/>
			</xsl:when>
			<xsl:when test="//*[@rdf:about=$uri]/rdfs:label[contains(@xml:lang,'en')]">
				<xsl:value-of select="//*[@rdf:about=$uri]/rdfs:label[contains(@xml:lang,'en')]"/>
			</xsl:when>
			<xsl:when test="//*[@rdf:about=$uri]/rdfs:label">
				<xsl:value-of select="//*[@rdf:about=$uri]/rdfs:label"/>
			</xsl:when>
			<!-- xsl:when test="contains($uri,'#')">
				<xsl:value-of select="substring-after($uri,'#')"/>
			</xsl:when -->
			<xsl:when test="name(namespace::*[.=$namespace])!=''">
				<xsl:variable name="namespaceAlias">
					<xsl:value-of select="name(namespace::*[.=$namespace])"/>
				</xsl:variable>
				<xsl:value-of select="concat(concat($namespaceAlias,':'),$localname)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$uri"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Create a browsable link if rdf:about or rdf:ID property.
		 Otherwise, create a link to describe the property value URI -->
	<xsl:template name="resourceDetailLink">
		<xsl:param name="property"/>
		<xsl:param name="namespace"/>
		<xsl:param name="localname"/>
		<xsl:variable name="uri">
			<xsl:value-of select="concat($namespace,$localname)"/>
		</xsl:variable>
		<xsl:variable name="linkName">
			<xsl:call-template name="getLabel">
				<xsl:with-param name="uri" select="$uri"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="escaped-uri">
			<xsl:call-template name="replace-string">
				<xsl:with-param name="text" select="$uri"/>
				<xsl:with-param name="replace" select="'#'"/>
				<xsl:with-param name="with" select="'%23'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="linkTextPrePre">
			<xsl:call-template name="replace-string">
				<xsl:with-param name="text" select="$linkName"/>
				<xsl:with-param name="replace" select="'_'"/>
				<xsl:with-param name="with" select="' '"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="linkTextPre">
			<xsl:call-template name="replace-string">
				<xsl:with-param name="text" select="$linkTextPrePre"/>
				<xsl:with-param name="replace" select="'/'"/>
				<xsl:with-param name="with" select="'/ '"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="linkText">
			<xsl:call-template name="replace-string">
				<xsl:with-param name="text" select="$linkTextPre"/>
				<xsl:with-param name="replace" select="'/ / '"/>
				<xsl:with-param name="with" select="'//'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($linkText, '://')">
				<a class="browse" href="{$uri}"	title="{$uri}">
					<xsl:value-of disable-output-escaping="yes" select="$linkText"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<a href="{$uri}" title="{$uri}">
					<xsl:value-of disable-output-escaping="yes" select="$linkText"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Create a browsable link if property value is a URL. 
		 Otherwise, write the property value  -->
	<xsl:template name="textDetailLink">
		<xsl:param name="property"/>
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="contains($value, '://')">
				<xsl:variable name="linkTextPre">
					<xsl:call-template name="replace-string">
						<xsl:with-param name="text" select="$value"/>
						<xsl:with-param name="replace" select="'/'"/>
						<xsl:with-param name="with" select="'/ '"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="linkText">
					<xsl:call-template name="replace-string">
						<xsl:with-param name="text" select="$linkTextPre"/>
						<xsl:with-param name="replace" select="'/ / '"/>
						<xsl:with-param name="with" select="'//'"/>
					</xsl:call-template>
				</xsl:variable>
				<a class="browse" href="{$value}">
					<xsl:value-of disable-output-escaping="yes" select="$linkText"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="substring-after-last">
		<xsl:param name="text"/>
		<xsl:param name="chars"/>
		<xsl:choose>
		  <xsl:when test="contains($text, $chars)">
			<xsl:variable name="last" select="substring-after($text, $chars)"/>
			<xsl:choose>
			  <xsl:when test="contains($last, $chars)">
				<xsl:call-template name="substring-after-last">
				  <xsl:with-param name="text" select="$last"/>
				  <xsl:with-param name="chars" select="$chars"/>
				</xsl:call-template>
			  </xsl:when>
			  <xsl:otherwise>
				<xsl:value-of select="$last"/>
			  </xsl:otherwise>
			</xsl:choose>
		  </xsl:when>
		  <xsl:otherwise>
			<xsl:value-of select="$text"/>
		  </xsl:otherwise>
		</xsl:choose>
  	</xsl:template>
  
  	<xsl:template name="substring-before-last">
		<xsl:param name="text"/>
		<xsl:param name="chars"/>
		<xsl:choose>
		  <xsl:when test="contains($text, $chars)">
			<xsl:variable name="before" select="substring-before($text, $chars)"/>
			<xsl:variable name="after" select="substring-after($text, $chars)"/>
			<xsl:choose>
			  <xsl:when test="contains($after, $chars)">
			    <xsl:variable name="before-last">
					<xsl:call-template name="substring-before-last">
				  		<xsl:with-param name="text" select="$after"/>
				  		<xsl:with-param name="chars" select="$chars"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="concat($before,concat($chars,$before-last))"/>
			  </xsl:when>
			  <xsl:otherwise>
				<xsl:value-of select="$before"/>
			  </xsl:otherwise>
			</xsl:choose>
		  </xsl:when>
		  <xsl:otherwise>
			<xsl:value-of select="$text"/>
		  </xsl:otherwise>
		</xsl:choose>
  	</xsl:template>
  
  	<xsl:template name="replace-string">
		<xsl:param name="text"/>
		<xsl:param name="replace"/>
		<xsl:param name="with"/>
		<xsl:choose>
			<xsl:when test="contains($text,$replace)">
				<xsl:value-of select="substring-before($text,$replace)"/>
				<xsl:value-of select="$with"/>
				<xsl:call-template name="replace-string">
					<xsl:with-param name="text" select="substring-after($text,$replace)"/>
					<xsl:with-param name="replace" select="$replace"/>
					<xsl:with-param name="with" select="$with"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
  	</xsl:template>
  
    <xsl:template name="get-ns">
		<xsl:param name="uri"/>
		<xsl:choose>
	  		<xsl:when test="contains($uri,'#')">
				<xsl:value-of select="concat(substring-before($uri,'#'),'#')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="ns-without-slash">
					<xsl:call-template name="substring-before-last">
						<xsl:with-param name="text" select="$uri"/>
						<xsl:with-param name="chars" select="'/'"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="concat($ns-without-slash, '/')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
    <xsl:template name="get-name">
		<xsl:param name="uri"/>
		<xsl:choose>
	  		<xsl:when test="contains($uri,'#')">
				<xsl:value-of select="substring-after($uri,'#')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="substring-after-last">
					<xsl:with-param name="text" select="$uri"/>
					<xsl:with-param name="chars" select="'/'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
