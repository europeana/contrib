<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<!-- Stylesheet to convert the liability warning to HTML -->
	
	<xsl:template match="liabilityWarning">
		<xsl:apply-templates select="message">
			<xsl:with-param name="acceptURL">
				<xsl:value-of select="@acceptURL"/>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="message">
		<xsl:param name="acceptURL"/>
		<html>
			<head>
				<title>Warning...</title>
			</head>
			<body>
				<pre>
				<xsl:value-of select="."/>
				</pre>
				<br/>
				<a href="{$acceptURL}">I agree</a>
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
