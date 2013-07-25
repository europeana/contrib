<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
>
	<xsl:output method='html' indent='yes'/>

	<xsl:template match="/">
<html>
<head>
	<title>Geoparsing result</title>

    <meta http-equiv="content-type" content="text/html;charset=utf-8"/>
	<link href="../styles/geoparser.css" rel="stylesheet" type="text/css"/>
	<link href="../styles/screen.css" rel="stylesheet" type="text/css"/>
	<link href="../styles/styles.css" rel="stylesheet" type="text/css"/>
	<link href="../styles/econnect.css" rel="stylesheet" type="text/css"/>
</head>

<body>
	<!-- Header -->
	<div id="header">
	  <table align="center" border="0" cellpadding="0" cellspacing="0" width="976">
		<tbody><tr>
		  <td align="right" height="250" valign="bottom"></td>
		</tr>
	  </tbody></table>
	</div>

	<!-- Content -->
	<div class="gradient">

<br/><br/>
<center>
<table class="tablemain">
    <tr>
        <td id="news">
            <h3>Geoparsing result</h3>
			
			<xsl:choose>				
				<xsl:when test="geoparsingResult/error">
				Error: <xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="geoparsingResult/entities" />
					<xsl:apply-templates select="geoparsingResult/annotatedText" />
					<xsl:apply-templates select="geoparsingResult/annotatedRecord" />
				</xsl:otherwise>
			</xsl:choose>	
        </td>
    </tr>
</table>
</center>
</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="entities">
		<xsl:if test="PLACE">
			<br/>
			<h2>Places:</h2>
			<xsl:for-each select="PLACE">
				- <a target="_blank">
								<xsl:attribute name="href">
					   http://digmap3.ist.utl.pt:8080/gaz_econnect/feature.action?identifier=<xsl:choose><xsl:when test="contains(@entityURI,'#')"><xsl:value-of select="substring-before(@entityURI, '#')"/>%23<xsl:value-of select="substring-after(@entityURI, '#')"/></xsl:when><xsl:otherwise><xsl:value-of select="@entityURI"/></xsl:otherwise></xsl:choose>
								</xsl:attribute>
					<xsl:value-of select="."/> 
				</a>
					- longitude:<xsl:value-of select="@longitude"/> latitude:<xsl:value-of select="@latitude"/> (confidence: <xsl:value-of select='format-number(@confidence, "0.00")'/>)<br/>
			</xsl:for-each>
		</xsl:if>
		
		<xsl:if test="TIME">
			<br/>
			<h2>Time periods:</h2>
			<xsl:for-each select="TIME">
				- <a target="_blank"> 
					<xsl:attribute name="href">
					   http://digmap3.ist.utl.pt:8080/gaz_econnect/feature.action?identifier=<xsl:choose><xsl:when test="contains(@entityURI,'#')"><xsl:value-of select="substring-before(@entityURI, '#')"/>%23<xsl:value-of select="substring-after(@entityURI, '#')"/></xsl:when><xsl:otherwise><xsl:value-of select="@entityURI"/></xsl:otherwise></xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="."/> 
				</a>
				- <xsl:value-of select="."/> (begin:<xsl:value-of select="@begin"/> end:<xsl:value-of select="@end"/>) (confidence: <xsl:value-of select='format-number(@confidence, "0.00")'/>)<br/>
			</xsl:for-each>
			
		</xsl:if>
	</xsl:template>

	<xsl:template match="annotatedText">
		<br/>
		<h2>Text:</h2>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="TIME">
		<b><u><xsl:value-of select="."/></u></b>
	</xsl:template>
	
	<xsl:template match="PLACE">
		<b><u><xsl:value-of select="."/></u></b>
	</xsl:template>
	
	
	
	<xsl:template match="annotatedRecord">
		<br/>
		<h2>Record:</h2>
		(note: only descriptive metadata elements are geoparsed)<br/>
		<xsl:for-each select="child::*">
			<b><xsl:value-of select="local-name(.)"/>: </b>
			<xsl:apply-templates />
			<br/>
		</xsl:for-each>
	</xsl:template>
	
	
	
	
	
</xsl:stylesheet>





