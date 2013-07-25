<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:oai="http://www.openarchives.org/OAI/2.0/"
		xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
		exclude-result-prefixes="xsl oai oai_dc">
	
	<xsl:output method="xml" indent="yes"/>
	<xsl:template match="/">
		<records>
			<xsl:for-each select="//oai:record">
				<xsl:choose>
				  <xsl:when test="oai:header[@status='deleted']">
				    <record status="deleted">
						<identifier><xsl:value-of select="oai:header/oai:identifier" /></identifier>
						<datestamp><xsl:value-of select="oai:header/oai:datestamp" /></datestamp>
						<setSpec><xsl:value-of select="oai:header/oai:setSpec" /></setSpec>
	
						<metadata><xsl:copy-of select="oai:metadata/*" /></metadata>
					</record>
				  </xsl:when>
				  <xsl:otherwise>
				  	<record>
							<identifier><xsl:value-of select="oai:header/oai:identifier" /></identifier>
							<datestamp><xsl:value-of select="oai:header/oai:datestamp" /></datestamp>
							<setSpec><xsl:value-of select="oai:header/oai:setSpec" /></setSpec>
		
							<metadata><xsl:copy-of select="oai:metadata/*" /></metadata>
						</record>
				  </xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</records>
	</xsl:template>

</xsl:stylesheet>
