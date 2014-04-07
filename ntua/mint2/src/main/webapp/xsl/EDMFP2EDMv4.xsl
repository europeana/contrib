<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet exclude-result-prefixes="foaf1 mrel gr edmfp xml"
  version="2.0"
  xmlns:crm="http://www.cidoc-crm.org/rdfs/cidoc_crm_v5.0.2_english_label.rdfs#"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:edm="http://www.europeana.eu/schemas/edm/"
  xmlns:edmfp="http://www.europeanafashion.eu/edmfp/"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  xmlns:foaf1="http://xmlns.com/foaf/0.1/"
  xmlns:gr="http://www.heppnetz.de/ontologies/goodrelations/v1#"
  xmlns:mrel="http://id.loc.gov/vocabulary/relators/"
  xmlns:ore="http://www.openarchives.org/ore/terms/"
  xmlns:owl="http://www.w3.org/2002/07/owl#"
  xmlns:rdaGr2="http://rdvocab.info/ElementsGr2/"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:skos="http://www.w3.org/2004/02/skos/core#"
  xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes"/>
  
  <xsl:variable name="var0">
    <item>TEXT</item>
    <item>VIDEO</item>
    <item>IMAGE</item>
    <item>SOUND</item>
    <item>3D</item>
  </xsl:variable>

  <xsl:param name="var4" select="'http://mint-projects.image.ntua.gr'" />  
  
  <xsl:param name="var2" select="'c1116e0e1d27b592d91d6e6281241d15f50fdb898b99d3c68ac5ada0400c9119'" />  
  <xsl:param name="var3" select="'true'" />  
    
  <xsl:template match="/">
    <xsl:apply-templates select="/rdf:RDF"/>
  </xsl:template>
  <xsl:template match="/rdf:RDF">
    <xsl:for-each select=".">
      <rdf:RDF>
        <xsl:for-each select="edm:ProvidedCHO">
          <edm:ProvidedCHO>
            <xsl:if test="@rdf:about">
              <xsl:attribute name="rdf:about">
                <xsl:for-each select="@rdf:about">
                  <xsl:if test="position() = 1">
                    <xsl:value-of select="concat($var4, substring-after(.,'localID'))"/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:attribute>
            </xsl:if>
            
            
            <xsl:for-each select="mrel:clb/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
            <dc:contributor>
                <xsl:value-of select="."/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:stylist/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Stylist)')"/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:model/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Model)')"/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            
           
            <xsl:for-each select="mrel:cur/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Curator)')"/>
              </dc:contributor>
            </xsl:if>  
            </xsl:for-each>
            
            <xsl:for-each select="mrel:aut/edm:Agent/skos:prefLabel">
              <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Author)')"/>
              </dc:contributor>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="mrel:ill/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Illustrator)')"/>
              </dc:contributor>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="mrel:std/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
            <dc:contributor>
                <xsl:value-of select="concat(. ,' (Set designer)')"/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:hairstylist/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. ,' (Hair stylist)')"/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:makeupArtist/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
                <xsl:value-of select="concat(. , ' (Make up artist)')"/>
              </dc:contributor>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="mrel:edt/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:contributor>
               <xsl:value-of select="concat(. ,' (Editor)')"/>
              </dc:contributor>
              </xsl:if>
            </xsl:for-each>
            
            
            
            <!-- Exclude contributor -->
            <!-- <xsl:for-each select="dc:contributor/edm:Agent/skos:prefLabel">
              <dc:contributor>
                <xsl:value-of select="."/>
              </dc:contributor>
            </xsl:for-each>  -->
            
            <xsl:for-each select="dc:coverage">
              <xsl:if test=". and not(. = '')">
              <dc:coverage>
                <xsl:value-of select="."/>
              </dc:coverage>
              </xsl:if>
            </xsl:for-each>
            
            
            <xsl:for-each select="dcterms:creator/edm:Agent/skos:prefLabel">
              <xsl:if test=". and not(. = '')">
              <dc:creator>
                <xsl:value-of select="."/>
              </dc:creator>
              </xsl:if>
            </xsl:for-each>
            
            
            <xsl:for-each select="mrel:dsr/edm:Agent/skos:prefLabel">
              <xsl:if test=". and not(. = '')">
              <dc:creator>
                <xsl:value-of select="concat(.,' (Designer)')"/>
              </dc:creator>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="mrel:pht/edm:Agent/skos:prefLabel">
              <xsl:if test=". and not(. = '')">
              <dc:creator>
                <xsl:value-of select="concat(. ,' (Photographer)')"/>
              </dc:creator>
              </xsl:if>
            </xsl:for-each>
            
            <!-- Exclude contributor -->
            <!--<xsl:for-each select="dc:creator/edm:Agent/skos:prefLabel">
              <dc:creator>
                <xsl:value-of select="."/>
              </dc:creator>
            </xsl:for-each> -->
            
            <xsl:for-each select="dc:date">
            <xsl:if test=". and not(. = '')">
              <dc:date>
                <xsl:value-of select="."/>
              </dc:date>
            </xsl:if>
            </xsl:for-each>
           
            <xsl:for-each select="dc:description">
	        <xsl:if test=". and not(. = '') and ($var3 = 'true')">
              <dc:description>
                <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dc:description>
               </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dc:format">
            <xsl:if test=". and not(. = '')">
              <dc:format>
                <xsl:value-of select="."/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- The resource -->
            <xsl:for-each select="edmfp:technique/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <dc:format>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Dereferencing -->
            <xsl:for-each select="edmfp:technique/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <xsl:variable name="partID" select="." />              
              <xsl:variable name="prefLabel" select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel[@xml:lang='en']" />
              <dc:format>
                 <xsl:attribute name="xml:lang">
                    <xsl:value-of select="'en'"/>
                 </xsl:attribute>
                 <xsl:value-of select="concat('Technique: ',$prefLabel)"/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal when not vocab url -->
            <xsl:for-each select="edmfp:technique/@rdf:resource">
              <xsl:if test="not(starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/'))">
              <dc:format>
                <xsl:value-of select="concat('Technique: ',.)"/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal to literal -->
            <xsl:for-each select="edmfp:technique">
            <xsl:if test=". and not(. = '')">
              <dc:format>
              <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
                <!-- The resource -->
            <xsl:for-each select="gr:color/skos:Concept/@rdf:about">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <dc:format>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Dereferencing -->
            <xsl:for-each select="gr:color/skos:Concept/@rdf:about">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <xsl:variable name="partID" select="." />              
              <xsl:variable name="prefLabel" select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel[@xml:lang='en']" />
              <dc:format>
                 <xsl:attribute name="xml:lang">
                    <xsl:value-of select="'en'"/>
                 </xsl:attribute>
                 <xsl:value-of select="concat('Color: ',$prefLabel)"/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal when not vocab url -->
            <xsl:for-each select="gr:color/skos:Concept/@rdf:about">
              <xsl:if test="not(starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/'))">
              <dc:format>
                <xsl:value-of select="concat('Color: ',.)"/>
              </dc:format>
            </xsl:if>
            </xsl:for-each>
            
            
            
            <xsl:for-each select="dc:identifier">
            <xsl:if test=". and not(. = '')">
              <dc:identifier>
                <xsl:value-of select="."/>
              </dc:identifier>
            </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="dc:language">
            <xsl:if test=".">
              <dc:language>
                <xsl:value-of select="."/>
              </dc:language>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dc:publisher/edm:Agent/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dc:publisher>
                <xsl:value-of select="."/>
              </dc:publisher>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dc:relation">
            <xsl:if test=". and not(. = '')">
              <dc:relation>
                <xsl:value-of select="."/>
              </dc:relation>
            </xsl:if>
            </xsl:for-each>
     
            <xsl:for-each select="dc:rights">
              <xsl:if test=". and not(. = '')">
              <dc:rights>
                <xsl:value-of select="."/>
              </dc:rights>
              </xsl:if>
            </xsl:for-each>
            
            <!-- The resource -->
            <xsl:for-each select="dc:subject/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <dc:subject>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
              </dc:subject>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Dereferencing -->
            <xsl:for-each select="dc:subject/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <xsl:variable name="partID" select="." />              
              <xsl:variable name="prefLabel" select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel[@xml:lang='en']" />
              <dc:subject>
                 <xsl:attribute name="xml:lang">
                    <xsl:value-of select="'en'"/>
                 </xsl:attribute>
                 <xsl:value-of select="$prefLabel"/>
              </dc:subject>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal when not vocab url -->
            <xsl:for-each select="dc:subject/@rdf:resource">
              <xsl:if test="not(starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/'))">
              <dc:subject>
                <xsl:value-of select="."/>
              </dc:subject>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal to literal -->
            <xsl:for-each select="dc:subject">
            <xsl:if test="dc:subject">
              <dc:subject>
              <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dc:subject>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:isSubjectOf">
            <xsl:if test=". and not(. = '')">
              <dc:subject>
                <xsl:value-of select="."/>
              </dc:subject>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dc:title">
            <xsl:if test=". and not(. = '')">
              <dc:title>
                <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dc:title>
             </xsl:if>
            </xsl:for-each>
            
            <!-- The resource -->
            <xsl:for-each select="dc:type/skos:Concept/@rdf:about">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <dc:type>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
              </dc:type>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Dereferencing -->
            <xsl:for-each select="dc:type/skos:Concept/@rdf:about">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <xsl:variable name="partID" select="." />              
              <xsl:variable name="prefLabel" select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel[@xml:lang='en']" />
              <dc:type>
                 <xsl:attribute name="xml:lang">
                    <xsl:value-of select="'en'"/>
                 </xsl:attribute>
                 <xsl:value-of select="$prefLabel"/>
              </dc:type>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal when not vocab url -->
            <xsl:for-each select="dc:type/skos:Concept/@rdf:about">
              <xsl:if test="not(starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/'))">
              <dc:type>
                <xsl:value-of select="."/>
              </dc:type>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edmfp:localType">
            <xsl:if test=". and not(. = '')">
              <dc:type>
                <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dc:type>
               </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dcterms:alternative">
             <xsl:if test=". and not(. = '')">
              <dcterms:alternative>
                <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dcterms:alternative>
              </xsl:if>
            </xsl:for-each>
           
            <xsl:for-each select="dcterms:created">
            <xsl:if test=". and not(. = '')">
              <dcterms:created>
                <xsl:value-of select="."/>
              </dcterms:created>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dcterms:extent">
            <xsl:if test=". and not(. = '')">
              <dcterms:extent>
                <xsl:value-of select="."/>
              </dcterms:extent>
            </xsl:if>
            </xsl:for-each>
  
            <xsl:for-each select="dcterms:issued">
            <xsl:if test=". and not(. = '')">
              <dcterms:issued>
                <xsl:value-of select="."/>
              </dcterms:issued>
              </xsl:if>
            </xsl:for-each>
            
            
            <!-- The resource -->
            <xsl:for-each select="dcterms:medium/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <dcterms:medium>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
              </dcterms:medium>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Dereferencing -->
            <xsl:for-each select="dcterms:medium/@rdf:resource">
            <xsl:if test="starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')">
              <xsl:variable name="partID" select="." />              
              <xsl:variable name="prefLabel" select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel[@xml:lang='en']" />
              <dcterms:medium>
                 <xsl:attribute name="xml:lang">
                    <xsl:value-of select="'en'"/>
                 </xsl:attribute>
                 <xsl:value-of select="concat('Material: ', $prefLabel)"/>
              </dcterms:medium>
            </xsl:if>
            </xsl:for-each>
            
            <!-- Map to literal when not vocab url -->
            <xsl:for-each select="dcterms:medium/@rdf:resource">
              <xsl:if test="not(starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/'))">
              <dcterms:medium>
                <xsl:value-of select="."/>
              </dcterms:medium>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dcterms:medium">
            <xsl:if test="dcterms:medium">
              <dcterms:medium>
              <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dcterms:medium>
            </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dcterms:provenance">
            <xsl:if test=". and not(. = '')">
              <dcterms:provenance>
                <xsl:value-of select="."/>
              </dcterms:provenance>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="dcterms:spatial/edm:Place/skos:prefLabel">
            <xsl:if test=". and not(. = '')">
              <dcterms:spatial>
                <xsl:value-of select="."/>
              </dcterms:spatial>
              </xsl:if>
            </xsl:for-each>
            
            
            <xsl:for-each select="dcterms:temporal">
            <xsl:if test=". and not(. = '')">
              <dcterms:temporal>
                <xsl:if test="@xml:lang">
                  <xsl:attribute name="xml:lang">
                    <xsl:for-each select="@xml:lang">
                      <xsl:if test="position() = 1">
                        <xsl:value-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
              </dcterms:temporal>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edm:type">
              <xsl:if test="position() = 1">
                <xsl:if test="index-of($var0/item, replace(.,'^\s*(.+?)\s*$', '$1')) > 0">
                  <edm:type>
                    <xsl:value-of select="."/>
                  </edm:type>
                </xsl:if>
              </xsl:if>
            </xsl:for-each>
          </edm:ProvidedCHO>
        </xsl:for-each>
        
        <xsl:for-each select="ore:Aggregation/edm:object/edm:WebResource">
          <xsl:if test="@rdf:about[starts-with(., 'http') or starts-with(., 'https')]">
            <edm:WebResource>
            <xsl:attribute name="rdf:about">
                <xsl:if test="position() = 1">
                  <xsl:value-of select="."/>
                </xsl:if>
            </xsl:attribute>
            </edm:WebResource>
          </xsl:if>
          </xsl:for-each>
        
        
        <xsl:for-each select="ore:Aggregation/edm:isShownBy/edm:WebResource">
        <xsl:if test="@rdf:about[starts-with(., 'http') or starts-with(., 'https')]">
          <edm:WebResource>
              <xsl:attribute name="rdf:about">
                <xsl:for-each select="@rdf:about">
                  <xsl:if test="position() = 1">
                    <xsl:value-of select="."/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:attribute>
            <xsl:for-each select="dc:rights">
              <dc:rights>
                <xsl:value-of select="."/>
              </dc:rights>
            </xsl:for-each>
          </edm:WebResource>
          </xsl:if>
        </xsl:for-each>
        
        
        <edm:WebResource>
              <xsl:attribute name="rdf:about">
                <xsl:value-of select="concat('http://www.europeanafashion.eu/record/a/',$var2)"/>
              </xsl:attribute>
        </edm:WebResource>
        
        <xsl:for-each select="ore:Aggregation/edm:hasView/edm:WebResource">
          <xsl:if test="@rdf:about[starts-with(., 'http') or starts-with(., 'https')]">
          <xsl:if test="not(@rdf:about = //edm:isShownBy/edm:WebResource/@rdf:about)">
          <edm:WebResource>
              <xsl:attribute name="rdf:about">
                <xsl:for-each select="@rdf:about">
                  <xsl:if test="position() = 1">
                    <xsl:value-of select="."/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:attribute>
            <xsl:for-each select="dc:rights">
              <dc:rights>
                <xsl:value-of select="."/>
              </dc:rights>
            </xsl:for-each>
          </edm:WebResource>
          </xsl:if>
          </xsl:if>
        </xsl:for-each>
        
        
         <xsl:for-each select="//dcterms:medium">
        <xsl:for-each select="@rdf:resource[starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')]"> 
        <skos:Concept>
            <xsl:attribute name="rdf:about"> 
                  <xsl:value-of select="."/>
            </xsl:attribute>
            <!-- include prefLabel from Partage vocabulary -->
            <xsl:if test=".[starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')]">
    			<xsl:variable name="partID" select="." />
                
                <!-- prefLabel -->
			    <xsl:for-each select ="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel">
				<skos:prefLabel>
					<xsl:attribute name="xml:lang" select="@xml:lang" />
					<xsl:value-of select="." />
				</skos:prefLabel>
                </xsl:for-each>
                <!-- altLabel -->
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:altLabel">
    			<skos:altLabel>
                    <xsl:attribute name="xml:lang" select="@xml:lang" />
					<xsl:value-of select="."/>
				</skos:altLabel>
				<!-- broader -->
                </xsl:for-each>
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:broader/@rdf:resource">
    			<skos:broader>
                    <xsl:attribute name="rdf:resource" select="." />
                </skos:broader>
				<!-- narrower -->
                </xsl:for-each>
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:narrower/@rdf:resource">
    			<skos:narrower>
                    <xsl:attribute name="rdf:resource" select="." />
				</skos:narrower>
                </xsl:for-each>
				
            </xsl:if>
        </skos:Concept>
      </xsl:for-each>      
      </xsl:for-each>   
        
        
        <xsl:for-each select="//skos:Concept">
        <xsl:for-each select="@rdf:about[starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')]"> 
        <skos:Concept>
            <xsl:attribute name="rdf:about"> 
                  <xsl:value-of select="."/>
            </xsl:attribute>
            <!-- include prefLabel from Partage vocabulary -->
            <xsl:if test=".[starts-with(., 'http://thesaurus.europeanafashion.eu/thesaurus/')]">
    			<xsl:variable name="partID" select="." />
                
                <!-- prefLabel -->
			    <xsl:for-each select ="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:prefLabel">
				<skos:prefLabel>
					<xsl:attribute name="xml:lang" select="@xml:lang" />
					<xsl:value-of select="." />
				</skos:prefLabel>
                </xsl:for-each>
                <!-- altLabel -->
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:altLabel">
    			<skos:altLabel>
                    <xsl:attribute name="xml:lang" select="@xml:lang" />
					<xsl:value-of select="."/>
				</skos:altLabel>
				<!-- broader -->
                </xsl:for-each>
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:broader/@rdf:resource">
    			<skos:broader>
                    <xsl:attribute name="rdf:resource" select="." />
                </skos:broader>
				<!-- narrower -->
                </xsl:for-each>
                <xsl:for-each select="$FashionVocab/skos:Concept[@rdf:about=$partID]/skos:narrower/@rdf:resource">
    			<skos:narrower>
                    <xsl:attribute name="rdf:resource" select="." />
				</skos:narrower>
                </xsl:for-each>
				
            </xsl:if>
        </skos:Concept>
      </xsl:for-each>      
      </xsl:for-each>   
        
        <xsl:for-each select="ore:Aggregation">
          <ore:Aggregation>
            <xsl:if test="@rdf:about">
              <xsl:attribute name="rdf:about">
                <xsl:for-each select="@rdf:about">
                  <xsl:if test="position() = 1">
                    <xsl:value-of select="concat($var4, substring-after(.,'localID'))"/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="edm:aggregatedCHO">
              <xsl:if test="position() = 1">
                <edm:aggregatedCHO>
                  <xsl:if test="@rdf:resource">
                    <xsl:attribute name="rdf:resource">
                      <xsl:for-each select="@rdf:resource">
                        <xsl:if test="position() = 1">
                         <xsl:value-of select="concat($var4, substring-after(.,'localID'))"/>
                        </xsl:if>
                      </xsl:for-each>
                    </xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="."/>
                </edm:aggregatedCHO>
              </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="edm:dataProvider/edm:Agent/skos:prefLabel">
              <xsl:if test="position() = 1">
                <edm:dataProvider>
                  <xsl:value-of select="."/>
                </edm:dataProvider>
              </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="edm:hasView/edm:WebResource/@rdf:about">
              <xsl:if test=".[starts-with(., 'http://') or starts-with(., 'https://')]">
              <xsl:if test="not(. = //edm:isShownBy/edm:WebResource/@rdf:about)">
              <edm:hasView>
                <xsl:attribute name="rdf:resource">
                      <xsl:value-of select="."/>
                </xsl:attribute>
                </edm:hasView>
              </xsl:if>
              </xsl:if>
            </xsl:for-each>
            
            
            <edm:isShownAt>    
                <xsl:attribute name="rdf:resource">
                  <xsl:value-of select="concat('http://www.europeanafashion.eu/record/a/',$var2)"/>
                </xsl:attribute>
            </edm:isShownAt>
            
            <xsl:if test="edm:isShownBy/edm:WebResource/@rdf:about[starts-with(., 'http://') or starts-with(., 'https://')]">
            <edm:isShownBy>
                <xsl:attribute name="rdf:resource">
                  <xsl:for-each select="edm:isShownBy/edm:WebResource/@rdf:about">
                    <xsl:if test="position() = 1">
                      <xsl:value-of select="."/>
                    </xsl:if>
                  </xsl:for-each>
                </xsl:attribute>
            </edm:isShownBy>
            </xsl:if>
            
            <xsl:if test="edm:object/edm:WebResource/@rdf:about[starts-with(., 'http://') or starts-with(., 'https://')]">
                <edm:object>
                <xsl:attribute name="rdf:resource">
                  <xsl:for-each select="edm:object/edm:WebResource/@rdf:about">
                    <xsl:if test="position() = 1">
                      <xsl:value-of select="."/>
                    </xsl:if>
                  </xsl:for-each>
                </xsl:attribute>
            </edm:object>
            </xsl:if>
            
            <xsl:if test="not(edm:object/edm:WebResource/@rdf:about)">
            <xsl:if test="edm:isShownBy/edm:WebResource/@rdf:about[starts-with(., 'http://') or starts-with(., 'https://')]">
                <edm:object>
                <xsl:attribute name="rdf:resource">
                  <xsl:for-each select="edm:isShownBy/edm:WebResource/@rdf:about">
                    <xsl:if test="position() = 1">
                      <xsl:value-of select="."/>
                    </xsl:if>
                  </xsl:for-each>
                </xsl:attribute>
            </edm:object>
            </xsl:if>
            </xsl:if>
            
            <xsl:for-each select="edm:provider/edm:Agent/skos:prefLabel">
              <xsl:if test="position() = 1">
                <edm:provider>
                  <xsl:value-of select="."/>
                </edm:provider>
              </xsl:if>
            </xsl:for-each>
            
            
              <xsl:if test="edm:rights/@rdf:resource">
              <edm:rights>
                <xsl:attribute name="rdf:resource">
                  <xsl:for-each select="edm:rights/@rdf:resource">
                    <xsl:if test="position() = 1">
                      <xsl:value-of select="."/>
                    </xsl:if>
                  </xsl:for-each>
                </xsl:attribute>
                </edm:rights>
              </xsl:if>
            
          </ore:Aggregation>
        </xsl:for-each>
      </rdf:RDF>
    </xsl:for-each>
  </xsl:template>
   <xsl:variable name="FashionVocab">
  
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10672">
    <skos:prefLabel xml:lang="it">Party</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Party</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10047">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Blanketlike garments, usually square or oblong with a slit in the center for the head, originating in South America. Also, similar garments used elsewhere, including those made of water-repellent fabric and usually having a hood, and when not worn, used as a blanket or tarpaulin. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Pončo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046187\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046187"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="pt">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Poncho</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πόντσο</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10142">
    <skos:prefLabel xml:lang="de">Fächer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Leques</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Waaier</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Lepeze</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Hand-held implements used to produce a current of air or that serve as purely decorative accessories; may be rigid or collapsible. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="es">aventador</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Solfjädrar</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βεντάλια</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">fans</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ventaglio</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">éventail</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300258857\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300258857"/>
    <skos:altLabel xml:lang="es">Ventalle</skos:altLabel>
    <skos:prefLabel xml:lang="es">Abanico</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10388">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:scopeNote xml:lang="en">"With ""needle lace,"" one of two primary types of handmade lace. It is characterized by being made by intertwisting threads that are wound on spools or bobbins and worked over a pillow on which the pattern is marked out by pins; the lace is worked with both hands moving the bobbins from side to side to form a twist, a braid, or a fabric called ""toile."" Lead or bone weights are used. It probably was developed in the early 16th century in Flanders. Early bobbin lace was often used for ruffs and collars and is characterized by rows of deeply angled points on a narrow band. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Encaje de bolillos</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čipka na kalem</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300132869\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300132869"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10386"/>
    <skos:prefLabel xml:lang="pt">Renda de bilros</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Knypplad spets</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Klöppelspitze</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">dentelle aux fuseaux</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kloskant</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bobbin lace</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Δαντέλα στο κοπανέλι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10074">
    <skos:altLabel xml:lang="fr">Robe de chambre</skos:altLabel>
    <skos:altLabel xml:lang="nl">Duster</skos:altLabel>
    <skos:prefLabel xml:lang="en">Housecoat</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Bata</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bata de estar en casa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Hausmantel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kućna haljina</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Vestaglia</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ochtendjas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ρόμπα</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Women's dresslike garments made in various lengths, worn informally around the house. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Morgonrock</skos:prefLabel>
    <skos:altLabel xml:lang="es">Batón de entrecasa</skos:altLabel>
    <skos:prefLabel xml:lang="fr">manteau d'intérieur</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046165\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046165"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10633">
    <skos:altLabel xml:lang="de">aus Haaren gefertigter Schmuck</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Hårsmycken</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bijoux en cheveux</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Joyería hecha de pelo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="de">Schmuck aus Haaren</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jewelry made of hair</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Nakit napravljen od kose</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10152">
    <skos:altLabel xml:lang="nl">Juweel</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">jewelry</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schmuck</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sieraden</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Ornaments such as bracelets, necklaces, and rings, of precious or semiprecious materials worn or carried on the person for adornment; also includes similar articles worn or carried for devotional or mourning purposes. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Joalharia</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="sv">Smycken</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gioielli</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">bijoux</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit</skos:prefLabel>
    <skos:altLabel xml:lang="en">jewellery</skos:altLabel>
    <skos:prefLabel xml:lang="es">Joyería</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κόσμημα</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209286\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209286"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10069">
    <skos:prefLabel xml:lang="de">Nacht- und Hauskleidung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">vêtement de nuit</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Nattkläder</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211604\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211604"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Noćna i kućna odeća</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupa de noite e de dormir</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da notte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Night and dressing wear</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ενδύματα ύπνου και σπιτιού</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lencería</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Nacht- en ochtendkleding</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10068"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10623">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Bikini</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bikini</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10123"/>
    <skos:prefLabel xml:lang="el">Μπικίνι</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10398">
    <skos:prefLabel xml:lang="en">Decoration techniques</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10366"/>
    <skos:prefLabel xml:lang="de">Dekorationstechniken</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Décoration</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Técnicas de decoração</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Decoratietechnieken</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Διακόσμηση</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:altLabel xml:lang="de">Verzierungstechniken</skos:altLabel>
    <skos:prefLabel xml:lang="es">Técnicas decorativas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tehnike ukrašavanja</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10037">
    <skos:scopeNote xml:lang="en">"Knee-length trousers commonly worn by men and boys in the 17th, 18th, and early 19th centuries. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Παντελόνι</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Culotte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">Pantalon</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Knäbyxor</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Reithose</skos:prefLabel>
    <skos:altLabel xml:lang="fr">haut-de-chausse(s)</skos:altLabel>
    <skos:altLabel xml:lang="sr">Bridž pantalone</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046135\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046135"/>
    <skos:altLabel xml:lang="de">Kniehose</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Kniebroek</skos:prefLabel>
    <skos:altLabel xml:lang="el">Μπουραζάνα/Μπουντούρι</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10036"/>
    <skos:prefLabel xml:lang="es">Calzón</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Breeches</skos:prefLabel>
    <skos:altLabel xml:lang="es">"Calzas, breeches"</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Calções masculinos (comprimento máximo até ao joelho)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čakšire</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10088">
    <skos:prefLabel xml:lang="pt">Fralda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pañal</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Square, oblong, or triangular cloths, usually without fastenings, worn by infants. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Πάνα μωρού</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pannolino</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">lange</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210599\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210599"/>
    <skos:prefLabel xml:lang="de">Windel</skos:prefLabel>
    <skos:altLabel xml:lang="en">Nappy</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Blöja</skos:prefLabel>
    <skos:altLabel xml:lang="fr">couche</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Pelena</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Luier</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Diaper</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10095">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210564\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210564"/>
    <skos:prefLabel xml:lang="sr">Kombinezon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Onderjurk</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Combinação</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Slip</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μισοφόρι</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Unterrock</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Viso</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">combinaison (femme)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:altLabel xml:lang="el">Κομπινεζόν</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Underklänning</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Mutande</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Underwear usually made the length of the garment under which it is to be worn, thereby taking the place of a lining. Usually applied to full slips with a bodice and shoulder straps made in dress length. Prefer ""half slips"" for skirtlike underwear extending from the waist to near the hemline of the garment with which it is worn. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10573">
    <skos:prefLabel xml:lang="es">Papel sobre cartón</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">papier cartonné</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="sv">Kartong</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Papir na kartonu</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Papier auf Karton</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Papier op karton</skos:prefLabel>
    <skos:prefLabel xml:lang="en">paper on cardboard</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10378">
    <skos:prefLabel xml:lang="nl">Sits</skos:prefLabel>
    <skos:altLabel xml:lang="es">Zaraza</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Textile, usually cotton or linen, dyed in a number of colors and usually glazed. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τσιντς</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="sr">Činc</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Chintz</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Chintz</skos:altLabel>
    <skos:prefLabel xml:lang="de">Chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chintz</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300132876\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300132876"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Chita</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10687">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Fish skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10017">
    <skos:altLabel xml:lang="fr">Haut du corps</skos:altLabel>
    <skos:prefLabel xml:lang="el">Βασικά ενδύματα άνω κορμού</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräktdelar överkropp</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Main garments upper body</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10002"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209278\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209278"/>
    <skos:prefLabel xml:lang="pt">Vestuário principal parte superior corpo</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Principali capi di abbigliamento per la parte superiore del corpo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">wichtigste Kleidungsstücke oberhalb der Taille getragen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Prendas principales parte superior del cuerpo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Vêtements principaux haut du corps</skos:prefLabel>
    <skos:altLabel xml:lang="de">Hauptkleidungsstücke oberhalb der Taille getragen</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Bovenkleding voor het bovenlichaam</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Glavni odevni predmeti – gornji delovi tela</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10574">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="sr">Papir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Papel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">paper</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Papper</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014109\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014109"/>
    <skos:prefLabel xml:lang="de">Papier</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Papier</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Papier</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Carta</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10589">
    <skos:prefLabel xml:lang="sr">Perje</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="es">Pluma</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Veer</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fjäder</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011809\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011809"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Feder</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">feather</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Plume</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10508">
    <skos:prefLabel xml:lang="es">Charreteras</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">épaulette</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="sv">Epålett</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schulterklappe</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300224235\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300224235"/>
    <skos:prefLabel xml:lang="nl">Epaulet</skos:prefLabel>
    <skos:altLabel xml:lang="en">Epaulet</skos:altLabel>
    <skos:prefLabel xml:lang="de">Epaulette</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Epaulette</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Epolete</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10397">
    <skos:prefLabel xml:lang="pt">Feltro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="el">Τσόχα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Textile constructed usually of wool and fur fiber often mixed with natural or synthetic fiber by the interlocking of the loose fiber through the action of heat, moisture, chemicals, and pressure without spinning, weaving, or knitting. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Filt</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">feutre</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Filz</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10396"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014107\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014107"/>
    <skos:prefLabel xml:lang="en">felt</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fieltro</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Vilt</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Filc</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10172">
    <skos:prefLabel xml:lang="nl">Horloges</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">montre</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ρολόι</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Reloj de pulsera</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Timepieces small and convenient enough to be carried about on a person. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Orologi</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Uhr</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">"Džepni sat, ručni sat"</skos:prefLabel>
    <skos:prefLabel xml:lang="en">watche</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300041615\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300041615"/>
    <skos:prefLabel xml:lang="sv">Klockor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Relógio</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Uurwerk</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10008">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="el">Κιμονό</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Loose, wide-sleeved garments fastened around the waist with an obi or broad sash, traditionally worn by Japanese men and women (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046171\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046171"/>
    <skos:prefLabel xml:lang="sr">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kimono</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Kimono</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Quimono</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Quimono</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10054">
    <skos:prefLabel xml:lang="sv">Fickor</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Tasche</skos:prefLabel>
    <skos:altLabel xml:lang="de">Brusttasche</skos:altLabel>
    <skos:altLabel xml:lang="de">Tasche</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="el">Τσέπες</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">poches</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Džepovi</skos:prefLabel>
    <skos:altLabel xml:lang="de">Gesäßtasche</skos:altLabel>
    <skos:prefLabel xml:lang="en">Pockets</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Bolsos</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210517\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210517"/>
    <skos:prefLabel xml:lang="de">Hosentasche</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zak</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Buidel</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Flat pouchlike components stitched into or onto a garment, accessible through a finished opening typically convenient to the hand. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Bolsillo (componente de vestuario)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10027">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Close-fitting main garments extending to the waist or just below. Sleeveless and usually collarless, and often having buttons or pockets. For close-fitting main garments extending below the waistline that are usually front-buttoning and may have sleeves, that are worn over a shirt and under a coat or jacket, use ""waistcoats."" (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="en">Vest</skos:altLabel>
    <skos:altLabel xml:lang="nl">Vest (kleding)</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Topje</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Top</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Top</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Top</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209904\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209904"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Top</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Top</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Top</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Topp</skos:prefLabel>
    <skos:altLabel xml:lang="de">Oberteil</skos:altLabel>
    <skos:altLabel xml:lang="fr">Top</skos:altLabel>
    <skos:prefLabel xml:lang="el">Γιλέκο</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Haut</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10162">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Ornaments worn around the neck, usually in the form of chains or strands of beads, pearls, stones, or decorative or precious materials, and often including a suspended ornamental pendant. Use ""chokers"" for short, narrow necklaces worn close to the throat. Use ""dog collars (necklaces)"" for wide ornamental bands worn tightly around the neck. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Ogrlica</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Halsketting</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Collar</skos:prefLabel>
    <skos:altLabel xml:lang="sv">halsband</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046001\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046001"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Περιλαίμιο</skos:prefLabel>
    <skos:altLabel xml:lang="el">Κολιέ</skos:altLabel>
    <skos:prefLabel xml:lang="it">Collane</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Colar</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">collier</skos:prefLabel>
    <skos:prefLabel xml:lang="en">necklace</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Halsband</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10161"/>
    <skos:prefLabel xml:lang="de">Halskette</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10223">
    <skos:prefLabel xml:lang="it">Accessori da collo</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Halsduk</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Dassen</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria para cuello</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ λαιμού</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Marama za vrat</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires pour le cou</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kravatt</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10220"/>
    <skos:prefLabel xml:lang="en">neckcloth</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Lenço de pescoço</skos:prefLabel>
    <skos:altLabel xml:lang="de">Halsbinde</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210069\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210069"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">General term for cloths of varying form worn at the neck. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Halstuch</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10368">
    <skos:prefLabel xml:lang="es">Cardado</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Preparing relatively short fibers for spinning by cleansing, disentangling, and collecting them together using a card. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">cardage</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kardieren</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Λανάρισμα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Karda</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="nl">Kaarden</skos:prefLabel>
    <skos:altLabel xml:lang="de">Streichen</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053751\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053751"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
    <skos:prefLabel xml:lang="pt">Cardagem</skos:prefLabel>
    <skos:prefLabel xml:lang="en">carding</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Češljanje</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10673">
    <skos:prefLabel xml:lang="it">Cocktail</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Cocktail</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10048">
    <skos:altLabel xml:lang="es">Bolero con botones</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Close-fitting jackets of waist length or shorter, often trimmed with fur and worn by women and children in the 19th century. Also, similar outer garments without fur worn for warmth; use especially for those worn by men in the 18th and 19th centuries. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Spencer: giacca da donna chiusa da un solo bottone</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209862\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209862"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="pt">Casaco curto (masculino) Casaquinha (feminino)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Spenser kaputić</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Spencer</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spencer</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Spencer</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Spencer</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Spencer</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Spencer</skos:prefLabel>
    <skos:altLabel xml:lang="de">Spenzer</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Spencer (jassen)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10075">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:prefLabel xml:lang="sv">Nattlinne</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046175\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046175"/>
    <skos:prefLabel xml:lang="fr">chemise de nuit (femme)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Spavaćica</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Camisa de noite feminina com rendas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">(Damen-) Nachthemd</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Veste da casa</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Νυχτικό</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Nachtjapon</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Loose dresslike garments of varying lengths and made with or without sleeves, worn by women and children for sleeping. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">Nightgown</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Camisón</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10560">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Hartkautschuk</skos:altLabel>
    <skos:prefLabel xml:lang="de">Hartgummi</skos:prefLabel>
    <skos:altLabel xml:lang="en">Vulcanite</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Eboniet</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Vulcaniet</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Ebonit</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Ebonita</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ebonit</skos:prefLabel>
    <skos:prefLabel xml:lang="en">ebonite</skos:prefLabel>
    <skos:altLabel xml:lang="de">Ebonit</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300380359\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300380359"/>
    <skos:prefLabel xml:lang="fr">ébonite</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10559"/>
    <skos:altLabel xml:lang="nl">Hardrubber</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10634">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10157"/>
    <skos:prefLabel xml:lang="en">ear pendant</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10141">
    <skos:prefLabel xml:lang="es">Bolso</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sac porté à l'épaule</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216945\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216945"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Axelremsväskor</skos:prefLabel>
    <skos:prefLabel xml:lang="en">shoulder bag</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="sr">Tašna za rame</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Mala a tiracolo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoudertas</skos:prefLabel>
    <skos:altLabel xml:lang="fr">sacs (à) bandoulière</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Τσάντα ώμου</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Medium-sized bags suspended at the top corners from one or two long straps, designed to be suspended from one shoulder or across the chest. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Schultertasche</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Borsa tracolla</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10389">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10385"/>
    <skos:prefLabel xml:lang="pt">Tricô</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Πλέξιμο</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stricken</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The forming and interlacing of loops by means of needles according to a prescribed manner or pattern, either by machine or by hand; most often used for textiles or costume. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Stickning</skos:prefLabel>
    <skos:prefLabel xml:lang="en">knitting</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="fr">tricot</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Breien</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053634\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053634"/>
    <skos:prefLabel xml:lang="sr">Štrikanje</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Pletenje</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Labor de punto</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10399">
    <skos:prefLabel xml:lang="en">colour and pattern</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Boja i šara</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10398"/>
    <skos:altLabel xml:lang="fr">couleurs et motifs</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Kleur en patroon</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Couleurs et dessin</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Colores y motivos</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cor e padrão</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">Farbe und Muster</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χρώμα και σχέδιο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Färg och mönster</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10624">
    <skos:prefLabel xml:lang="sv">Baddräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bañador</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Swim suit</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">maillot de bain</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Badpak</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ολόσωμο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kupaći kostim</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Badeanzug</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10123"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10151">
    <skos:prefLabel xml:lang="it">Cinture</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cintos (acessórios de traje)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cinturón</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Riemen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gürtel</skos:prefLabel>
    <skos:altLabel xml:lang="sv">skärp</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ζώνη</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210002\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210002"/>
    <skos:prefLabel xml:lang="en">belt (costume accessories)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Kaiš</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">ceintures</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bälten</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:scopeNote xml:lang="en">"Flexible straps or bands generally encircling the waist or hips or passing over the shoulder and usually having some type of fastener, such as a buckle; worn for decoration, support, or to carry such items as weapons, tools, or money."</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10038">
    <skos:prefLabel xml:lang="sv">Ytterkläder</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Garments worn over other garments as the outer layer, especially those worn for protection from the natural elements. For garments worn for protection from dirt or danger, use descriptors listed under ""&lt;protective wear&gt;."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Πανωφόρι</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209265\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209265"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Soprabiti</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Odeća za nošenje van kuće</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário exterior</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Prendas exteriores</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Überbekleidung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Outerwear</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Vêtement de dessus</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Overkleding</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10002"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10089">
    <skos:prefLabel xml:lang="sr">Podvezica</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Hüftgürtel</skos:altLabel>
    <skos:scopeNote xml:lang="en">Ties or bands worn to support stockings or socks. (AAT)</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="it">Giarrettiera</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Kousenband</skos:altLabel>
    <skos:prefLabel xml:lang="fr">jarretière</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jarretelgordel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καλτσοδέτα</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210559\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210559"/>
    <skos:prefLabel xml:lang="en">Garter belt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Sockenband</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Strumpeband</skos:prefLabel>
    <skos:altLabel xml:lang="de">Strumpfhalter</skos:altLabel>
    <skos:altLabel xml:lang="pt">Jarreteira</skos:altLabel>
    <skos:altLabel xml:lang="fr">fixe-chaussette</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Liga</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Liga</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10094">
    <skos:altLabel xml:lang="de">Strampelanzug</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κορμάκι</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Body</skos:altLabel>
    <skos:prefLabel xml:lang="es">Pelele</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Minituta</skos:prefLabel>
    <skos:altLabel xml:lang="de">Spielhöschen</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="sv">Sparkdräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Strampler</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Romper</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Romper</skos:prefLabel>
    <skos:scopeNote xml:lang="en">One-piece garments with gathered leg openings and buttons or snaps closing the crotch. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Dečji kombinezon</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Macacão curto</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209855\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209855"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">barboteuse</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10575">
    <skos:prefLabel xml:lang="en">papier-mâché</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10574"/>
    <skos:prefLabel xml:lang="sr">Papir maše</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Papier-maché</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Papier-maché</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014245\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014245"/>
    <skos:altLabel xml:lang="de">Papiermaschee</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Papier mâché</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Papiermasché</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Papel maché</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10018">
    <skos:prefLabel xml:lang="es">Blusa</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Main garments for the upper body, usually lightweight and loose-fitting, made with or without sleeves and worn over or tucked in the waistband of a skirt or trousers, especially by workmen, peasants and artists. Also, women's garments cut in the style of a man's classic, tailored-cut shirt, having a notch collar, collar band, front placket opening, and usually long sleeves with cuffs (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046133\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046133"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Blouse</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Blouse</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Blouse</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="sv">Blus</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μπλούζα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bluse</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bluza</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Chemisier</skos:altLabel>
    <skos:altLabel xml:lang="el">Μπόλκα</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Blusa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Blusa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10379">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300183741\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300183741"/>
    <skos:prefLabel xml:lang="nl">Crêpe</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Krepp</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Crespón</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Krep</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Lightweight textile made of various types of fiber, having a crinkled surface obtained by using hard twisted thread or yarn, by printing with caustic soda, by weaving with varied tensions, or by embossing. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Κρεπ</skos:prefLabel>
    <skos:prefLabel xml:lang="en">crepe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="pt">Crepe</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Crepe</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">crêpe</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10588">
    <skos:prefLabel xml:lang="fr">Corne</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011826\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011826"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Hoorn</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="en">horn</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Horn</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Horn</skos:prefLabel>
    <skos:altLabel xml:lang="es">Cuerno</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Rog</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Asta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10688">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Bird skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10007">
    <skos:prefLabel xml:lang="it">Tuta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Mono</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"One-piece garments worn by parachutists for jumping. Also, similar garments combining a shirt or bodice with trousers or shorts in one piece (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">Combinaison-pantalon</skos:prefLabel>
    <skos:altLabel xml:lang="en">jump suit</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209835\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209835"/>
    <skos:prefLabel xml:lang="el">Jumpsuit</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jumpsuit</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jumpsuit</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Jumpsuit</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kombinezon</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Macacão</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="fr">Combinaison-pantalon</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10171">
    <skos:prefLabel xml:lang="en">costume jewelry</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Jewelry for wear with current fashions usually made of inexpensive materials (metals, shells, plastic, wood) often set with imitation or semiprecious stones. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">bijouterie fantaisie</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräktsmycken</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modeschmuck</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Namaakjuwelen</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gioielli complementari a un vestito</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κόσμημα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:altLabel xml:lang="nl">Kostuumjuweel</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211360\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211360"/>
    <skos:prefLabel xml:lang="pt">Joalharia de traje</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bižuterija</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Bisutería</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10509">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Tyg</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tkanina</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="fr">Tissu</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Stof</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Tejido</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stoff</skos:prefLabel>
    <skos:altLabel xml:lang="de">Gewebe</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300162391\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300162391"/>
    <skos:prefLabel xml:lang="en">Fabric</skos:prefLabel>
    <skos:altLabel xml:lang="en">cloth</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10369">
    <skos:prefLabel xml:lang="pt">Fiação</skos:prefLabel>
    <skos:scopeNote xml:lang="en">The process of making fibers or filaments into yarn or thread. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Predenje</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
    <skos:prefLabel xml:lang="es">Hilado</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">filage</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053661\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053661"/>
    <skos:prefLabel xml:lang="sv">Spinna</skos:prefLabel>
    <skos:prefLabel xml:lang="en">spinning</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γνέσιμο</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Spinnen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Spinnen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10161">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Sieraden gedragen rond de hals of op het bovenlijf</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit koji se nosi na vratu ili na gornjem delu tela</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jewelry worn around the neck or on the upper body</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Smycken burna runt halsen eller på överkroppen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="es">Joyería para el cuello y parte superior del cuerpo</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Joalharia usada no pescoço ou na parte superior do corpo</skos:prefLabel>
    <skos:altLabel xml:lang="fr">bijoux portés autour du cou ou sur le haut du corps</skos:altLabel>
    <skos:prefLabel xml:lang="de">um den Hals oder am Körper getragener Schmuck</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Κοσμήματα λαιμού ή κορμού</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gioielli per il collo o per la parte alta del corpo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209290\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209290"/>
    <skos:prefLabel xml:lang="fr">bijoux portées autour du cou et en haut du corps</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10055">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="pt">Cauda</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ουρά φορέματος</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schleppe</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300224243\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300224243"/>
    <skos:prefLabel xml:lang="en">Train</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cola</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Strascico</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">traine</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Extended parts of a skirt, gown, or state robe that lie on the floor and trail behind the wearer, either separate or attached. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Šlep</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Släp</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sleep</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10224">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10223"/>
    <skos:prefLabel xml:lang="sr">Askot kravata</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210052\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210052"/>
    <skos:prefLabel xml:lang="en">ascot</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Corbata Ascot</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">lavallière</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Kravatt</skos:prefLabel>
    <skos:altLabel xml:lang="de">Krawattenschal</skos:altLabel>
    <skos:altLabel xml:lang="de">Ascot</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Herenhalsdoek</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ascot</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Ascot (gravata)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ascotkrawatte</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cravattino Ascot</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Neckcloths with broad ends usually looped and tied under the chin and sometimes secured by a stickpin. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="fr">ascot</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10674">
    <skos:prefLabel xml:lang="it">Presentazione</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Launch</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10028">
    <skos:prefLabel xml:lang="sr">Majica</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Maglietta a maniche corte</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Camiseta</skos:prefLabel>
    <skos:altLabel xml:lang="es">Camisa</skos:altLabel>
    <skos:altLabel xml:lang="fr">T-shirt</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209903\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209903"/>
    <skos:altLabel xml:lang="el">Μακό</skos:altLabel>
    <skos:prefLabel xml:lang="el">T-shirt</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="sv">T-shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">T-shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="en">T-shirt</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Popular name for pullover shirts with short sleeves and a round or V-shaped collarless neck. Usually made of a lightweight machine-knit textile. For similar garments worn as underwear, use ""undershirts."" (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="sv">T-tröja</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">T-Shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Tee-shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">T-Shirt</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10057">
    <skos:prefLabel xml:lang="de">Flicken</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Zakrpa</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">écusson</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Emblema</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Patch</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Patch</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Parche</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Μπάλωμα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Patte</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Lapp</skos:prefLabel>
    <skos:altLabel xml:lang="nl">opzetstuk</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sr">Aplikacija</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10635">
    <skos:prefLabel xml:lang="en">hairpin</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">épingle à cheveux</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hårnål</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Igla za kosu</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">Agujón</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Aguja para el pelo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Haarnadel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10220">
    <skos:altLabel xml:lang="sv">Flugor</skos:altLabel>
    <skos:prefLabel xml:lang="el">Λαιμοδέτης</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sr">Kragna</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209284\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209284"/>
    <skos:altLabel xml:lang="sv">Kravatter</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Boorden en dassen</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Slipsar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">um den Hals getragene Bekleidungsdetails</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Krås</skos:altLabel>
    <skos:prefLabel xml:lang="it">Accessori da collo</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Peças de pescoço</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Articles of costume worn about the neck. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="en">neckwear</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Okovratnik</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires pour le cou</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Accesorio para el cuello</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10217"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10076">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209952\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209952"/>
    <skos:prefLabel xml:lang="sv">Nattskjorta</skos:prefLabel>
    <skos:altLabel xml:lang="es">Camisa de cama</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Camisa de dormir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Nightshirt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">(Herren-) Nachthemd</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Νυχτικιά</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Camisa de dormir curta estilo masculino (parte de cima de pijama)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Loose shirtlike garments, often knee-length, worn for sleeping. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Camicia da notte</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Nachthemd</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:prefLabel xml:lang="fr">chemise de nuit (homme)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Košulja za spavanje</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10140">
    <skos:altLabel xml:lang="nl">Damestas</skos:altLabel>
    <skos:prefLabel xml:lang="es">Monedero</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Geldbörse</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046219\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046219"/>
    <skos:prefLabel xml:lang="nl">Handtas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τσάντα μικρή χεριού</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Handväskor</skos:altLabel>
    <skos:altLabel xml:lang="de">Geldbeutel</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Small to medium-sized receptacles made of leather or other flexible material and frequently having several inner compartments, used for carrying money and other personal items, fastened with a zipper, clasp, press stud, etc., and carried as a ladies' handbag or shoulderbag."</skos:scopeNote>
    <skos:prefLabel xml:lang="en">purse (ladies accessories)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">porte-monnaie</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Tašnica za novac / novčanik / ručna tašnea</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Penningpungar</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Bolsas (acessórios de senhora)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">porte monnaie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Börsar</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Borsellino</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Portmonnäer</skos:altLabel>
    <skos:altLabel xml:lang="de">Portemonnaie</skos:altLabel>
    <skos:altLabel xml:lang="fr">sac à main</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10561">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Kamen</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011176\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011176"/>
    <skos:prefLabel xml:lang="es">Piedra</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Pierre</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sten</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Steen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stein</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">stone</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10049">
    <skos:prefLabel xml:lang="de">Stola (Überbekleidung)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Estola</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Etole</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Estola (prenda exterior)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300138741\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300138741"/>
    <skos:prefLabel xml:lang="sv">Pälskrage</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Εσάρπα</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoudermanteltje</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Stole (Outerwear)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Long and narrow unshaped garments made from cloth or fur and used as a covering for the shoulders and arms. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Stola</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Stola</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10086">
    <skos:prefLabel xml:lang="es">Corsé</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Espartilho</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Corset</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Korset</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Korset</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Corsetto</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Corpete</skos:altLabel>
    <skos:altLabel xml:lang="sr">Mider</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">corset</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κορσές</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Smoothly fitted undergarments extending from or below the bust down to the waist or below, stiffened by strips of steel or whalebone, or with casing for a busk, sometimes limbered by elastic goring, sometimes tightened by lacing, and fastened by hooks. It is usually worn by women for support and molding of the figure including the ribcage and possibly the hips. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Korsett</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Korsett</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210582\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210582"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10621">
    <skos:prefLabel xml:lang="sv">Tanga</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10099"/>
    <skos:prefLabel xml:lang="sr">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tanga</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10035">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Gonna</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Main garments of varying length extending from the waist or hip and covering a part of the lower body. Also, the lower part of a dress, coat, or other garment. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Saia</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209932\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209932"/>
    <skos:prefLabel xml:lang="fr">Jupe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Skirt</skos:prefLabel>
    <skos:altLabel xml:lang="es">Saya</skos:altLabel>
    <skos:prefLabel xml:lang="el">Φούστα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Rock</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kjol</skos:prefLabel>
    <skos:altLabel xml:lang="es">pollera</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:prefLabel xml:lang="es">Falda</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Suknja</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Rok</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10571">
    <skos:prefLabel xml:lang="de">Werkstoffe pflanzlichen Ursprungs</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Materijali biljnog porekla</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10543"/>
    <skos:prefLabel xml:lang="fr">Matériaux d'origine végétale</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vegetabiliska material</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Materialen van plantaardige origine</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Materiales de origen vegetal</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Vegetable origin materials</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10210">
    <skos:prefLabel xml:lang="sr">Veo</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Véu</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Veletta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Slöjor</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βέλο</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sluiers</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Thin and lightweight coverings worn over the head or face, and sometimes extending to the shoulders, for ornament, protection, or concealment; also, similar coverings made as component parts of headgear. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Schleier</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10208"/>
    <skos:prefLabel xml:lang="es">Velo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">voile</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046128\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046128"/>
    <skos:prefLabel xml:lang="en">veil (headcloths)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10097">
    <skos:prefLabel xml:lang="sr">Potkošulje</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Corpinho</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210570\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210570"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">Bodices worn next to the body or under other bodices or garments. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Corpete</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="de">Untertaille</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καμιζόλα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sous-vêtement</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Canottiera intima</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Body (ropa interior)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">caraco</skos:altLabel>
    <skos:altLabel xml:lang="de">Miederleibchen</skos:altLabel>
    <skos:prefLabel xml:lang="en">Underbodices</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Linne</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Onderlijfje</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10154">
    <skos:prefLabel xml:lang="sv">Diadem</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Diadem</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">diadème</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Diadema</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Diadema</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046021\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046021"/>
    <skos:prefLabel xml:lang="it">Diadema</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Ornamental headbands of metal or cloth; use especially for those worn as a sign of royalty. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Diademen</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dijadema</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Διάδημα</skos:prefLabel>
    <skos:prefLabel xml:lang="en">diadem</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10211">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10208"/>
    <skos:altLabel xml:lang="sv">Haklin</skos:altLabel>
    <skos:prefLabel xml:lang="es">Toca</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212997\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212997"/>
    <skos:altLabel xml:lang="es">Griñón</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Soggolo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Nonnenkappen</skos:prefLabel>
    <skos:altLabel xml:lang="fr">guimpes</skos:altLabel>
    <skos:prefLabel xml:lang="el">Πλερέζα</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Halskrage</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">cornette</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kaluđerički veo</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Toucado</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Šlajer</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">(Nonnen-) Schleier</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Dok</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Huvudduk</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft headcloths, especially of linen or silk, wrapped around the throat and passing under the chin from one side of the head to the other, and pinned to a band, hat or the hair. Of a type originating in the Medieval period and worn by nuns of various religions to the present day. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">wimple</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10015">
    <skos:prefLabel xml:lang="pt">Fato</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kostim</skos:altLabel>
    <skos:altLabel xml:lang="fr">Tailleur (femme)</skos:altLabel>
    <skos:prefLabel xml:lang="en">Suit</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kostym</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Pak</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Kostuum</skos:prefLabel>
    <skos:altLabel xml:lang="el">Ταγιέρ</skos:altLabel>
    <skos:prefLabel xml:lang="de">Anzug</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="sr">Odelo</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Sets of two or more garments made to harmonize or match in color. Also, any costume designed to be worn for a special purpose or under particular conditions, such as a space suit or a snowsuit (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Traje</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Costume (homme)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Completo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209863\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209863"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Κοστούμι</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10066">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Plain or decorative strips of material worn to fill in a low neckline, especially on a dress. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Επιστήθιο</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Modestie</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Fazzoletto da spalle</skos:prefLabel>
    <skos:altLabel xml:lang="de">Anstandstüchlein</skos:altLabel>
    <skos:prefLabel xml:lang="fr">cravate</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Halstuch</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Modesty piece</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modestie</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300224534\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300224534"/>
    <skos:prefLabel xml:lang="sv">Fischy</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fita ou lenço de pescoço</skos:prefLabel>
    <skos:altLabel xml:lang="el">Τραχηλιά</skos:altLabel>
    <skos:altLabel xml:lang="de">Sittsamkeitsblende</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:altLabel xml:lang="de">Einsatz</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Marama za grudi</skos:prefLabel>
    <skos:altLabel xml:lang="en">Neckcloth</skos:altLabel>
    <skos:prefLabel xml:lang="es">Modestia</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10174">
    <skos:prefLabel xml:lang="es">Lunar</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schönheitsfleck</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Patches</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tygmärken</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Beauty spots, sometimes in decorative shapes, applied to the face for ornament. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Διακοσμητικά σημάδια</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Toppa</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Emblema (acessórios de traje)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pièces</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Remendo</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">patch (costume accessory)</skos:prefLabel>
    <skos:altLabel xml:lang="de">Mouche</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210515\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210515"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="de">Schönheitspflaster</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Zakrpa</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Aplikacija</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10034">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Korte broek</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šorts</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Shorts</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Short</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Shorts</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Shorts</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Shorts</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Exposed bifurcated garments extending from the waist or hip to any portion of the leg above the knee. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="es">Short</skos:altLabel>
    <skos:altLabel xml:lang="nl">Short</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Calções</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σορτς</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pantalón corto</skos:prefLabel>
    <skos:altLabel xml:lang="de">kurze Hose</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209930\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209930"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10685">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Pig skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10675">
    <skos:prefLabel xml:lang="fr">tyvek</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tyvek</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tyvek</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tyvek®</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Tyvek</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tivek</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="sv">Polyetylenfibrer</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10029">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Weste</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γιλέκο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Prsluk</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Väst</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Close-fitting main garments extending to below the waistline. Usually front buttoning and sleeveless, but may have sleeves, especially garments from the 18th century. Worn over a shirt and under a coat or jacket. For close-fitting main garments extending to the waist or just below that are sleeveless and usually collarless, use ""vests (garments)."" (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="it">Gilet</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Gilet</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Herenvest</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216053\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216053"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Chaleco</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Vest</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Gilet</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Colete</skos:prefLabel>
    <skos:altLabel xml:lang="en">Waistcoat</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10649">
    <skos:prefLabel xml:lang="fr">chapeau mou</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Filthattar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Hamburg šešor</skos:prefLabel>
    <skos:altLabel xml:lang="de">Homburger Hut</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ρεπούμπλικα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrero Homburg</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Homburg</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Homburg</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10366">
    <skos:prefLabel xml:lang="en">techniques</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Techniken</skos:prefLabel>
    <skos:altLabel xml:lang="it">Tecniche</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="el">Τεχνικές</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tekniker</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Techniques</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Technieken</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The manner or method by which an activity is performed. Use ""processes"" when referring generally to the activities or procedures followed to produce some end, and for the actions or changes that take place in materials or objects. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Tecniche</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tehnike</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300138082\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300138082"/>
    <skos:prefLabel xml:lang="pt">Técnicas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Técnicas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10385">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Otras técnicas</skos:prefLabel>
    <skos:prefLabel xml:lang="en">other techniques</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">autres techniques</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Άλλες τεχνικές</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Outras técnicas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Andere technieken</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10370"/>
    <skos:prefLabel xml:lang="sr">Ostale tehnike</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Andra tekniker</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">andere Techniken</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10160">
    <skos:scopeNote xml:lang="en">"Ornamental fillets, wreaths, or similar encircling ornaments for the head worn for personal adornment or as a mark of honor or achievement; also, coronal wreaths of leaves or flowers. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Κορόνα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Kroon</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kruna</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">crown</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Krone</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kronor</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">couronne</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046020\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046020"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:prefLabel xml:lang="es">Corona</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Corona</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Coroa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10056">
    <skos:altLabel xml:lang="fr">Plastron</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Borststuk</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šteker</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215860\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215860"/>
    <skos:altLabel xml:lang="fr">parure de bustier</skos:altLabel>
    <skos:altLabel xml:lang="es">Estomaguero</skos:altLabel>
    <skos:prefLabel xml:lang="es">Peto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Stomacher</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bröstplatta</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Stiff panels, usually triangular in shape and often heavily decorated, inserted in an open bodice to cover the corset. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Pettorina</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pièce d'estomac</skos:prefLabel>
    <skos:altLabel xml:lang="es">pechera</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Stecker</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Borstlap</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Pièce d'estomac (elemento triangular e rígido usado na zona do estômago)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Στομαχικό</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10387">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10386"/>
    <skos:prefLabel xml:lang="fr">dentelle à l'aiguille</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Δαντέλα με τη βελόνα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="nl">Naaldkant</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300231662\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300231662"/>
    <skos:prefLabel xml:lang="sr">Čipka na iglu</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Encaje a la aguja</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Croché</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Nadelspitze</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Virkad spets</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"With ""bobbin lace,"" one of the two primary types of handmade lace. It is characterized by being created with a needle and thread on threads laid down over a pattern, using basically only one stitch is used, buttonhole stitch and the knotted buttonhole stitch. The process is believed to have been developed in Italy in the fifteenth century, inspired by drawn-thread work and cutwork on linen. Generally, the design is drawn on a piece of paper or parchment that is backed with cloth or another material; the design is outlined with a stitch that serves as the supporting framework for the piece. The filling and pattern are worked with a needle and a single thread in a succession of buttonhole stitches that do not penetrate the backing; stitches are worked in close rows to form the solid parts of the pattern and loosely to form a mesh. Straight lines of stitching may be added to support further stitches; bobbin-made or woven tape is sometimes used for parts of the design. A knife is passed between the two layers of backing to release the design. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">needle lace</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Trina</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10221">
    <skos:prefLabel xml:lang="es">Cuello (accesorio para el cuello)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10220"/>
    <skos:prefLabel xml:lang="sv">Kragar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Shaped articles worn at the neckline of a garment, either separate or attached. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sr">Kragna</skos:altLabel>
    <skos:prefLabel xml:lang="fr">col</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κολάρο</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Kragen</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210058\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210058"/>
    <skos:prefLabel xml:lang="nl">Kragen</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Gola (peça de pescoço)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Colletti</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Halsboord</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Okovratnik</skos:prefLabel>
    <skos:prefLabel xml:lang="en">collar (neckwear)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10636">
    <skos:prefLabel xml:lang="de">Hutnadel</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Aguja de sombrero</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">épingle à chapeau</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:prefLabel xml:lang="sv">Hattnål</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hatpin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Igla za šešir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10058">
    <skos:prefLabel xml:lang="es">Cuello (accesorio para el cuello)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Κολάρο</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">col</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Collar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210058\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210058"/>
    <skos:prefLabel xml:lang="de">Kragen</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Shaped articles worn at the neckline of a garment, either separate or attached. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Kraag</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kragna</skos:altLabel>
    <skos:prefLabel xml:lang="it">Colletto</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Gola</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Okovratnik</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Krage</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:altLabel xml:lang="nl">Halsboord</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10077">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Pijama</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πιτζάμα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pijama</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pyjamas</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215942\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215942"/>
    <skos:scopeNote xml:lang="en">"Loose-fitting one- or two-piece garments consisting of short or long trousers and a shirt or pullover top, worn especially for sleeping or lounging around the house. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="es">Piyama</skos:altLabel>
    <skos:altLabel xml:lang="de">Schlafanzug</skos:altLabel>
    <skos:prefLabel xml:lang="de">Pyjama</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pigiama</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pyjama</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pyjama</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Pyjama</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pidžama</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10562">
    <skos:prefLabel xml:lang="fr">Aventurine</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Aventurin</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Aventurin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Venturin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">venturine</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10561"/>
    <skos:prefLabel xml:lang="es">Venturina</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10572">
    <skos:prefLabel xml:lang="sr">Karton</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Karton</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Karton</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Kartong</skos:altLabel>
    <skos:prefLabel xml:lang="en">cardboard</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Papp</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cartulina</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="fr">Carton</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014224\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014224"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10096">
    <skos:altLabel xml:lang="fr">Blouse</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="pt">Avental</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Särk</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="pt">Bata</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to loose garments, generally garments that cover the upper body, the term being derived from an old English word for shirt or chemise. It can refer to loose, outer garments designed to protect more valuable attire from soil or paint. It can also refer to loose undergarments, which were often gathered or embroidered at the neck and sleeves and were originally intended to protect more valuable outer garments from perspiration and other bodily soil and fluids. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216050\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216050"/>
    <skos:altLabel xml:lang="pt">Casaco camuflado de soldado</skos:altLabel>
    <skos:prefLabel xml:lang="es">Blusón</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">"Radni mantil, potkošulja"</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Smock</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Smock</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kittel</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Grembiule</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Hemd</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Kiel</skos:altLabel>
    <skos:altLabel xml:lang="fr">Sarrau</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Blouse de travail</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10087">
    <skos:prefLabel xml:lang="sv">Krinolin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210554\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210554"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Crinoline</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Crinoline</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Petticoats originally made with a stiff, woven fabric of horsehair, linen, cotton, or wool. Later often used in conjunction with hoops of whalebone or steel. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Crinolina</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Crinolina</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Crinolina</skos:prefLabel>
    <skos:altLabel xml:lang="es">Miriñaque</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Krinolina</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:altLabel xml:lang="it">Sottogonna rigida</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κρινολίνο</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">crinoline</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Krinoline</skos:prefLabel>
    <skos:altLabel xml:lang="de">Reifrock Mitte des 19. Jahrhunderts</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10036">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:altLabel xml:lang="de">Hosen</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Pantalone</skos:prefLabel>
    <skos:altLabel xml:lang="de">Beinkleider</skos:altLabel>
    <skos:prefLabel xml:lang="en">Trousers</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Calças</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Hose</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παντελόνι</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pantaloni</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Pantalon</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209935\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209935"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Byxor</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Pantalon</skos:altLabel>
    <skos:prefLabel xml:lang="es">Pantalón</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Broek</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Exposed bifurcated garments which extend from waist or hips to the ankle or sometimes to the knee or just below. (AAT)</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10609">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Bermuda</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bermuda</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Bermuda</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10034"/>
    <skos:prefLabel xml:lang="de">Bermuda</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bermuda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bermudas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Bermude</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bermudahorts</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sv">Bermudas</skos:altLabel>
    <skos:altLabel xml:lang="de">Bermudashorts</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10622">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Tanga</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stringtanga</skos:prefLabel>
    <skos:prefLabel xml:lang="el">String</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10099"/>
    <skos:prefLabel xml:lang="fr">String</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">String</skos:prefLabel>
    <skos:prefLabel xml:lang="en">String</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">String</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Tanga de hilo dental</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10153">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210466\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210466"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:altLabel xml:lang="nl">Schuifspelje</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Aigrettes</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εγκρέτα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fjädrar</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Aigrette (Federn)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Garzota</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">aigrette</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Piume di airone</skos:prefLabel>
    <skos:altLabel xml:lang="fr">aigrettes (plumes)</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Plumas</skos:prefLabel>
    <skos:prefLabel xml:lang="en">aigrette (plumes)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Perjanice</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Upright plumes of feathers of an egret or heron arranged as a hair ornament or in a turban. Also, similar ornaments, often jeweled, in the shape of feathers, especially those worn on the head. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="es">Aigrette</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10068">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10001"/>
    <skos:prefLabel xml:lang="it">"Biancheria, abiti da casa e da notte"</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Natt- och underkläder. Informella kläder</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">vêtement de nuit</skos:prefLabel>
    <skos:altLabel xml:lang="fr">vêtement d'interieur</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Noćna i kućna odeća i donji veš</skos:prefLabel>
    <skos:prefLabel xml:lang="el">"Ενδύματα ύπνου, σπιτιού και εσώρουχα"</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">"Roupa de dormir, de casa e interior"</skos:prefLabel>
    <skos:altLabel xml:lang="fr">sous vêtement</skos:altLabel>
    <skos:prefLabel xml:lang="es">Lencería y ropa interior</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">"Night, home and underwear"</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">"Nacht, huis- en onderkleding"</skos:prefLabel>
    <skos:prefLabel xml:lang="de">"Nacht- und Unterwäsche, Hauskleidung"</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10009">
    <skos:prefLabel xml:lang="it">Manteau</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Mantua</skos:prefLabel>
    <skos:altLabel xml:lang="fr">manteau de cour</skos:altLabel>
    <skos:prefLabel xml:lang="el">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Mantua</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Robe de cour</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209837\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209837"/>
    <skos:scopeNote xml:lang="en">"Loose, gownlike garments open at the front, with a pleated and fitted bodice, very short sleeves, and added cuffs. Worn belted at the waist and with its long train draped at the hips to reveal the front of the petticoat below (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Vestido formal e sumptuoso</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Englisches Hofkleid</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10067">
    <skos:prefLabel xml:lang="it">cucitura</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="en">Seam</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Naht</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Sömmar</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">couture</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Naad</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Generally, the lines of junction formed by the abutment or attachment of two edges, especially the edges of two portions of a single object, such as a garment or metal cylinder. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300228472\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300228472"/>
    <skos:prefLabel xml:lang="el">Ραφή</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Zoom</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Costura</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šav</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Costura</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10016">
    <skos:scopeNote xml:lang="en">"Simple slip-on garments made with or without sleeves and usually knee-length or longer and belted at the waist; especially those worn by men and women of ancient Greece and Rome. Also, garments extending from the neckline to the waist or longer, usually high-necked and worn over other garments. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">Tunique</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Tunic</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="pt">Túnica</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τουνίκα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Túnica</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Tunica</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tunika</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tunika</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tunika</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Tuniek</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209869\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209869"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10173">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Matched sets of ornaments intended to be worn together, either jewels or trimmings for costume, usually a collar and cuffs. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">parure</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Set</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Parures</skos:prefLabel>
    <skos:altLabel xml:lang="de">Parure</skos:altLabel>
    <skos:altLabel xml:lang="es">Parure</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300261068\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300261068"/>
    <skos:prefLabel xml:lang="it">Parure</skos:prefLabel>
    <skos:altLabel xml:lang="el">Parures</skos:altLabel>
    <skos:altLabel xml:lang="en">Parures</skos:altLabel>
    <skos:prefLabel xml:lang="de">Set</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Garnityr</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σετ κοσμημάτων</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="sr">Garnitura nakita</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">set</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Conjunto</skos:prefLabel>
    <skos:altLabel xml:lang="fr">ensembles</skos:altLabel>
    <skos:prefLabel xml:lang="es">Juego</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Garnitur</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10212">
    <skos:prefLabel xml:lang="de">Kopfschmuck</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Elaborate coverings or objects for the head, especially those worn for ceremonial or ornamental purposes. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="nl">Hoofdtooi</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Cobertura de cabeça</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tocado</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Huvudbonad</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Acconciature</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Haaraccessoire</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Oglavlja</skos:prefLabel>
    <skos:prefLabel xml:lang="en">headdress</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="nl">Coiffures</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">coiffe féminine</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κάλυμμα κεφαλιού</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046023\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046023"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10686">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Deer skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10367">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">Refajo</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Underskirts, extending from the waist to usually just slightly shorter than the outside skirt, which provide fullness at some point. For similar skirtlike underwear which does not provide fullness use ""half slips."" (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Underkjol</skos:prefLabel>
    <skos:altLabel xml:lang="fr">combinaison</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10101"/>
    <skos:altLabel xml:lang="de">Wipprock</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μισοφόρι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209927\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209927"/>
    <skos:prefLabel xml:lang="sr">Podsuknja</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Anágua</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">jupon</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Saiote</skos:altLabel>
    <skos:prefLabel xml:lang="de">Petticoat</skos:prefLabel>
    <skos:altLabel xml:lang="fr">jupon</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Petticoat</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Petticoat</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Stubb</skos:altLabel>
    <skos:prefLabel xml:lang="es">Enagua</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10676">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Kaycel®</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="sv">Polyuretan</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Kaycel</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Kaycel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Kaycel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Kaycel</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kajcel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10563">
    <skos:prefLabel xml:lang="sr">Gagat</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Gagat</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gagat</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10561"/>
    <skos:altLabel xml:lang="de">Jet</skos:altLabel>
    <skos:altLabel xml:lang="nl">Git</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Jais</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300045514\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300045514"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Jett</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Jet</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Azabache</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jet</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10222">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Žabo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10220"/>
    <skos:prefLabel xml:lang="fr">jabot</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jabot</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chorrera</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Jabotten</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Detachable, made-up cascades of soft fabric, often lace, worn at the center front of the neckline over other garments. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Φιόγκος λαιμού</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210062\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210062"/>
    <skos:prefLabel xml:lang="sv">Halskrås</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Jabot</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jabot</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Jabot</skos:prefLabel>
    <skos:altLabel xml:lang="es">Jabot</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10386">
    <skos:prefLabel xml:lang="sr">Čipka</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Δαντέλα</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Renda</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Spitze</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spets</skos:prefLabel>
    <skos:prefLabel xml:lang="en">lace</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300132861\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300132861"/>
    <skos:prefLabel xml:lang="fr">dentelle</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Encaje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kant</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to a fine, openwork ornamental textile work formed by looping, interlacing, twisting, plaiting, or braiding threads of linen, cotton, silk, hair, metal, or another fiber to form designs or patterns. Lace may be made with a needle or with bobbins. Embroidery may be added. Modern lace may be made by machine. Openwork fabrics made on a loom and ornamental openwork knitting are generally not classified as lace. Lace is often white or monochromatic. True lace developed in the fourteenth century in Europe and the Middle East, although ornamented openwork fabrics were known in ancient cultures, including the Egyptian culture. Lace may be used as a border, edging, or insert on linens or apparel; it is also formed into large pieces of cloth used for hangings, draperies, apparel, or other items. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="it">Merletto</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10385"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10637">
    <skos:prefLabel xml:lang="de">Brustnadel</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kråsnål</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:altLabel xml:lang="de">Anstecknadel für die Brust</skos:altLabel>
    <skos:prefLabel xml:lang="es">Alfiler de pecho</skos:prefLabel>
    <skos:prefLabel xml:lang="en">breastpin</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Igla za grudi</skos:prefLabel>
    <skos:altLabel xml:lang="de">Brosche</skos:altLabel>
    <skos:prefLabel xml:lang="fr">broche</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10012">
    <skos:altLabel xml:lang="de">Spielhöschen</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Speelpakje</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Playsuit</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Špilhozne</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Tenuta sportiva</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Vêtement de jeu</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Σύνολο για  άθληση</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300256813\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300256813"/>
    <skos:prefLabel xml:lang="es">Traje infantil de juego</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Macacão curto de alças</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lekdress</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Playsuit (children's)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Spielanzug</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Play and sports outfits made for women and children, often consisting of a one-piece suit combining shorts and shirt and an overskirt; introduced in the early 20th century (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">Habits de jeu</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10608">
    <skos:prefLabel xml:lang="nl">Bustier</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="en">Bustier</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10309">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">arti grafiche</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Broadly, any presentation in two-dimensional visual form, including most arts on paper, panel, or canvas, including painting. In current usage, typically refers to the arts of printmaking and illustration that depend upon line and not color to render the design. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Γραφικές τέχνες</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Grafika</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Grafisk konst</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Artes gráficas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Artes gráficas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Grafik</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Grafische kunsten</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300264849\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300264849"/>
    <skos:prefLabel xml:lang="fr">arts graphiques</skos:prefLabel>
    <skos:prefLabel xml:lang="en">graphic arts</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10373">
    <skos:prefLabel xml:lang="es">Batista</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Baptista</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Batist</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Batist</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Batist</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Batist</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:scopeNote xml:lang="en">"Fine, soft, sheer cloth of plain weave made of any of the principal types of fiber, such as cotton, linen, rayon, silk, or wool. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Cambraia</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300183634\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300183634"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="fr">batiste</skos:prefLabel>
    <skos:prefLabel xml:lang="en">batiste</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βατίστα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10167">
    <skos:prefLabel xml:lang="de">auf der Kleidung getragener Schmuck</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Joyería usada en vestuario</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bijoux portés sur un costume</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräktsmycken</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">bijoux portés sur le costume</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Gioielli da indossare sui vestiti</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit za odeću</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">jewelry worn on costume</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="pt">Joalharia usada no traje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sieraden gedragen op kleding</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209292\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209292"/>
    <skos:prefLabel xml:lang="el">Κόσμημα ενδύματος</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10363">
    <skos:prefLabel xml:lang="de">Lanital</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">lanitel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">lanitel</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lanitel</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lanitel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lanital (melkwol/caseïnevezel)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10362"/>
    <skos:prefLabel xml:lang="sr">Polimerska vlakna</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibras de Caseína</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">lanital</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10618">
    <skos:scopeNote xml:lang="en">"Fabric color changes under natural and artificial light. Swatches of fabric are gathered to determine which will work best for each character, scene and color palette. These are shared with the production designer and sometimes the cinematographer (UCLA)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">Échantillon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Staaltje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10509"/>
    <skos:prefLabel xml:lang="en">Swatch</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10078">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">dors-bien ( bébé)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pyjamas</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pijama de uma peça (sem pés)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Hansopje</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φόρμα ύπνου</skos:prefLabel>
    <skos:prefLabel xml:lang="de">einteiliger Schlafanzug für Kinder</skos:prefLabel>
    <skos:altLabel xml:lang="en">Sleeper</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216858\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216858"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:scopeNote xml:lang="en">"A type of pajamas, usually one-piece and footed, worn especially by children. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Pigiama pagliaccetto per bambini</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dečji kombinezon za spavanje</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Enterizo</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Babygro (com pés)</skos:altLabel>
    <skos:prefLabel xml:lang="en">One-piece sleeping suit (for children)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10091">
    <skos:prefLabel xml:lang="nl">Onderrok</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Skirtlike underwear extending from the waist to near the hemline of the outside skirt. For similar skirtlike underwear which provides fullness at some point use ""petticoats."" (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Rechte onderrok</skos:altLabel>
    <skos:prefLabel xml:lang="fr">jupon</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Underkjol</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Saiote</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Podsuknja</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Halbunterrock</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="en">Half slip</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Enagua</skos:prefLabel>
    <skos:altLabel xml:lang="es">Media combinación</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μισοφόρι φούστα</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sottogonna</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300214655\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300214655"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10291">
    <skos:prefLabel xml:lang="it">Speroni</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Espuelas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">spurs (costume accessories)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Sporrar</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σπιρούνια</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">éperons</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300036923\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300036923"/>
    <skos:altLabel xml:lang="nl">Rijsporen</skos:altLabel>
    <skos:altLabel xml:lang="de">Sporen</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Mamuze</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Sporen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Sporn</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Metal goads attached to a rider's boot heels, used to drive the horse on. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
    <skos:prefLabel xml:lang="pt">Esporas (acessório de traje)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10697">
    <skos:prefLabel xml:lang="en">Hand knitted</skos:prefLabel>
    <skos:prefLabel xml:lang="de">handgestrickt</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Tricoté main</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10389"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Πλεκτό</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="sr">Ručno pletenje</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tejido a mano</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Stickat för hand</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10627">
    <skos:altLabel xml:lang="de">Geldbeutel</skos:altLabel>
    <skos:prefLabel xml:lang="fr">portefeuille</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="el">Πορτοφόλι</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Novčanik</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Brieftasche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">wallet</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Plånbok</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Cartera</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10022">
    <skos:prefLabel xml:lang="sr">Duble</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Farsetto</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Jubón</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209829\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209829"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="el">Ντουμπλέτι</skos:altLabel>
    <skos:prefLabel xml:lang="el">Γιλέκο</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Close-fitting waist-length or hip-length garments with or without sleeves worn by men from the 15th to the 17th century and for similar garments reinforced by mail and worn under armor. Also refers to sleeved, hip-length garments worn as part of military uniforms by the Scottish units of the British army after 1855 (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Wambuis</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Wams</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Tröja</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Pourpoint</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Doublet</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Doublet</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Gibão</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10677">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="es">Reemay</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Reemay</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Reemay</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Reemay</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fiberduk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Reemay®</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Rimej</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10079">
    <skos:prefLabel xml:lang="pt">Roupa interior</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Sous-vêtements</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10068"/>
    <skos:altLabel xml:lang="nl">Onderkleding</skos:altLabel>
    <skos:prefLabel xml:lang="en">Underwear</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Ondergoed</skos:prefLabel>
    <skos:altLabel xml:lang="de">Bodystocking</skos:altLabel>
    <skos:prefLabel xml:lang="el">Εσώρουχα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Underkläder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Donji veš</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sous-vêtement</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209267\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209267"/>
    <skos:prefLabel xml:lang="es">Ropa interior</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Garments worn beneath main garments, usually next to the skin. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Unterwäsche</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10260">
    <skos:prefLabel xml:lang="es">Chanclas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Badskor</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Japanke</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Teenslipper</skos:altLabel>
    <skos:altLabel xml:lang="de">Flipflops</skos:altLabel>
    <skos:altLabel xml:lang="de">Badeschuhe</skos:altLabel>
    <skos:prefLabel xml:lang="en">thongs</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Slippers</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046080\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046080"/>
    <skos:prefLabel xml:lang="fr">tongs</skos:prefLabel>
    <skos:prefLabel xml:lang="it">infradito</skos:prefLabel>
    <skos:altLabel xml:lang="es">Chancletas</skos:altLabel>
    <skos:prefLabel xml:lang="de">Zehensandalen</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Sandals held on the foot by inverted V-straps that pass between the first and second toes. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Σαγιονάρες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Chinelos de enfiar no dedo</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10259"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10657">
    <skos:prefLabel xml:lang="de">Oxford-Schuhe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Oksford cipele</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapatos Oxford</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Derbies</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Snörskor</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Oxfords</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Oxfords</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10050">
    <skos:prefLabel xml:lang="it">Completo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Conjunto (vestimenta)</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209844\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209844"/>
    <skos:altLabel xml:lang="pt">Conjunto</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Complete costume, including garments and accessories, worn for a harmonious effect. Also, two or more garments or accessories designed to complement one another."</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σύνολο</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräkt</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Ensamble</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Komplet</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10002"/>
    <skos:prefLabel xml:lang="pt">Coordenado</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10166">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">jewelry worn on legs and feet</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Smycken för ben och fötter</skos:prefLabel>
    <skos:prefLabel xml:lang="de">an Beinen und Füßen getragener Schmuck</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">"Sieraden, gedragen aan benen en voeten"</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit za noge i stopala</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bijou porté sur les jambes et les pieds</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="el">Κόσμημα ποδιών</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Joalharia usada nas pernas e pés</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Gioielli da indossare alle gambe o ai piedi</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209291\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209291"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Joyería usadas en piernas y pies</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10647">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sv">Tvåhörning</skos:altLabel>
    <skos:prefLabel xml:lang="de">Zweispitz</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Δίκοχο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="sv">Båthattar</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bicorne</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Bicorn</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dvorogi šešir</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bicornio</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10250">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:prefLabel xml:lang="en">shoes</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schuhe</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Sapatos</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Footwear with a relatively stiff sole and heel, and generally covering the foot at or below the ankle joint. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Skor</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapato</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">scarpe</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoenen</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046065\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046065"/>
    <skos:prefLabel xml:lang="el">Παπούτσια</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Cipele</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10176">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216871\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216871"/>
    <skos:prefLabel xml:lang="fr">obi</skos:prefLabel>
    <skos:prefLabel xml:lang="en">obi</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Obi pojas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kimonobälte</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Obi: cintura del kimono</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10175"/>
    <skos:prefLabel xml:lang="es">Obi</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Sashes of varying widths and sometimes stiffened, tied around the waist in intricate patterns of bows and knots usually at the back, but also in front, worn over a kimono; also, similar sashes worn for fashionable dress. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Obi's</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10013">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Redingote (dress)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Redengot</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Casaco redingote</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Redingot</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Dresses open in front to show an underskirt or with a front gore of contrasting material, with collar and lapels and long sleeves. Bodice may be double-breasted and high-waisted; worn especially during the 1820s through the 1860s. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα σε στυλ ρετινγκότας</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="nl">Redingote (japon)</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300254632\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300254632"/>
    <skos:prefLabel xml:lang="nl">Robe redingote</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10064">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10060"/>
    <skos:prefLabel xml:lang="nl">Vetersluiting</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Pertla</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Atacador</skos:altLabel>
    <skos:altLabel xml:lang="nl">Veter</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">Bändchen</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Snörning</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cordón</skos:prefLabel>
    <skos:altLabel xml:lang="sv">spets</skos:altLabel>
    <skos:altLabel xml:lang="de">Verschnürung</skos:altLabel>
    <skos:prefLabel xml:lang="en">Lace (fastening)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tresse</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schnürband</skos:altLabel>
    <skos:prefLabel xml:lang="fr">lacet</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cordão</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κορδόνια</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Laccio (allacciatura)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10638">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:prefLabel xml:lang="en">Ferronière</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10308">
    <skos:prefLabel xml:lang="en">fashion illustration</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modeillustration</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modeillustration</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Ilustración de moda</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300015593\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300015593"/>
    <skos:prefLabel xml:lang="fr">dessin de mode</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modna ilustracija</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="pt">Figurino de moda</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Εικονογράφηση μόδας</skos:prefLabel>
    <skos:altLabel xml:lang="fr">illustration de mode</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Mode-illustratie</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10307"/>
    <skos:prefLabel xml:lang="pt">Ilustração de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="it">illustrazioni/figurini</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Images other than photographs provided to show fashionable apparel and accessories, especially for advertisement. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10374">
    <skos:prefLabel xml:lang="sv">Bukram</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Buckram</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bakram</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tafetán empastado</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Entretela</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="el">Ταρλατάν</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bongran</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:altLabel xml:lang="es">Tela ahulada</skos:altLabel>
    <skos:prefLabel xml:lang="de">Steifleinen</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Heavy weave cotton, jute, or linen textile stiffened with glue, size, or starch and used for interlinings in garments, box making, bookbinding, etc. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300253474\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300253474"/>
    <skos:prefLabel xml:lang="en">buckram</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10290">
    <skos:prefLabel xml:lang="de">Blickdichte Strümpfe</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Collants opacos</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Maillots</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Heavy, opaque stockings woven in one with panties. For sheer stockings woven in one with panties, use ""pantyhose."" For one-piece, skin-tight combination garments, use ""leotards."" (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:altLabel xml:lang="de">Strumpfhosen</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Calzamaglia</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">tights (opaque stockings)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">collant  opaque</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209999\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209999"/>
    <skos:prefLabel xml:lang="es">Medias opacas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Hula hop čarape</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κολάν</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tights</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10570">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Emalj</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Emaille</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014910\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014910"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="nl">Email</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Email</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Esmalte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Emajl</skos:prefLabel>
    <skos:prefLabel xml:lang="en">enamel</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Émail</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10607">
    <skos:prefLabel xml:lang="nl">Bolero</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="en">Bolero</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10200">
    <skos:scopeNote xml:lang="en">"Shaped coverings for the head having a brim and crown, or one of the two. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="en">hat</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chapeau</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Chapéu</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šešir</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046106\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046106"/>
    <skos:prefLabel xml:lang="nl">Hoeden</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hut</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrero</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Hattar</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cappelli</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Καπέλο</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10617">
    <skos:prefLabel xml:lang="nl">Poppenkleding</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10433"/>
    <skos:prefLabel xml:lang="en">Doll's clothes</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10090">
    <skos:altLabel xml:lang="en">Waist cincher</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210585\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210585"/>
    <skos:prefLabel xml:lang="fr">gaine</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Girdle</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cinta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Ζώνη</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Step-in</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:altLabel xml:lang="nl">Gaine</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Women's underwear, often partly or entirely of elastic or boned, for supporting and shaping the appearance of the abdomen, hips, and buttocks. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Faja (ropa interior)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Busto a mezza guaina</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hüfthalter</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Höfthållare</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pojas / steznik</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10364">
    <skos:altLabel xml:lang="nl">Kunstvezel</skos:altLabel>
    <skos:prefLabel xml:lang="fr">fibres synthètiques</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Synthetische vezels</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Syntetiska fibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras sintéticas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Sintetička vlakna</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Fibras sintéticas (fibras derivadas do petróleo)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Fiber made from chemical substances, used for textile production. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10358"/>
    <skos:prefLabel xml:lang="de">Synthetische Fasern auf Erdölbasis</skos:prefLabel>
    <skos:prefLabel xml:lang="en">synthetic fibres (petrol-derived fibres)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Συνθετικές ύλες (παράγωγα πετρελαίου)</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014058\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014058"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10023">
    <skos:prefLabel xml:lang="el">Μπλούζα πόλο</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Pullover shirts with short or long sleeves and a turnover collar or a round banded neck. Usually made of a soft absorbent fabric, such as knitted cotton. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">Polo</skos:altLabel>
    <skos:prefLabel xml:lang="en">Polo shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Polotröja</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="pt">Pólo</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Maglietta Polo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="es">Camisa de tenis</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Polo majica</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Poloshirt</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Poloshirt</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Polo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Polo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209896\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209896"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10628">
    <skos:altLabel xml:lang="es">Morral</skos:altLabel>
    <skos:prefLabel xml:lang="de">Tasche</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pochette</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Pouch</skos:prefLabel>
    <skos:altLabel xml:lang="de">Beutel</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="es">Bolsa</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Börs</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Torbica</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πουγκί</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10678">
    <skos:prefLabel xml:lang="en">Polyamide</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="el">Polyamide</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Polyamide</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Polyamid</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Polyamide</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Poliamid</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Poliamida</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10024">
    <skos:altLabel xml:lang="nl">Pullover</skos:altLabel>
    <skos:prefLabel xml:lang="it">Maglione</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πουλόβερ</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209897\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209897"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Main garments, usually made without a placket or similar opening, that must be drawn over the head to be put on. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="fr">Pull-over</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Pulover</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pullover</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pullover</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pullover</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Pullover</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="fr">Pull</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Trui</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Pulôver</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10051">
    <skos:prefLabel xml:lang="sv">Dräktdelar</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Komponente kostima</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10001"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212998\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212998"/>
    <skos:prefLabel xml:lang="en">Costume components</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kledingonderdelen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Einzelteile der Bekleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Pièces de costumes</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Elementi/Componenti dell'abbigliamento</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Componente de vestuario</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Τμήματα ενδυμάτων</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">Kleidungsdetails</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Componentes de traje</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kleidteile</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10165">
    <skos:prefLabel xml:lang="el">Δαχτυλίδι</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Small circular bands worn on the finger; typically circlets of real or simulated precious metal, and frequently set with precious stones or imitations of these, intended for wearing upon the finger either as an ornament or as a token. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Anelli</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Prsten</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ring (Schmuck)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ringen (sieraden)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bague</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046012\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046012"/>
    <skos:altLabel xml:lang="es">Sortija</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Ringar</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Anillo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Anel (joalharia)</skos:prefLabel>
    <skos:prefLabel xml:lang="en">ring (jewelry)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10163"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10365">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Nylon (poliamida)</skos:prefLabel>
    <skos:prefLabel xml:lang="en">nylon</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">nylon</skos:prefLabel>
    <skos:altLabel xml:lang="es">Nilón</skos:altLabel>
    <skos:prefLabel xml:lang="el">Νάιλον</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014462\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014462"/>
    <skos:scopeNote xml:lang="en">Any of a variety of thermoplastic polymers originally developed as textile fibers and used in fabrics. They have a straight-chain polyamide structure and are largely heat-resistant.</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Najlon</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Nylon</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Nylon</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Nylon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Nylon</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10648">
    <skos:prefLabel xml:lang="de">Kochmütze</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καπέλο σεφ</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kockmössor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Gorro de cocinero</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kuvarska kapa</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Chef’s hat</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Toque</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10658">
    <skos:prefLabel xml:lang="en">Platform-sole</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Platåskor</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πλατφόρμες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Obuća sa platformom</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Plateausohle</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Chaussures compensées</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Plataformas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10261">
    <skos:prefLabel xml:lang="fr">bottes</skos:prefLabel>
    <skos:prefLabel xml:lang="it">stivali</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Stiefel</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kängor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:prefLabel xml:lang="nl">Laarzen</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπότες</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046057\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046057"/>
    <skos:prefLabel xml:lang="en">boots</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Footwear, the leg of which extends above the ankle joint. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Čizme</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10175">
    <skos:prefLabel xml:lang="fr">pompon</skos:prefLabel>
    <skos:prefLabel xml:lang="en">pompon</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pompón</skos:prefLabel>
    <skos:altLabel xml:lang="de">Bommel</skos:altLabel>
    <skos:prefLabel xml:lang="el">Πομ-πον</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pompom</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tofsar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pompons</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pompons</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Jewels or ornaments attached to a long pin; also tufts or bunches of ribbon, velvet, flowers, or threads of silk, worn in the hair, on a cap, or dress. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Pompon</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300224619\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300224619"/>
    <skos:prefLabel xml:lang="de">Pompon</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10550">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Aluminio</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aluminijum</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011015\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011015"/>
    <skos:altLabel xml:lang="en">Aluminum</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10251">
    <skos:prefLabel xml:lang="el">Παπούτσια μπαλέτου</skos:prefLabel>
    <skos:prefLabel xml:lang="en">ballet slippers</skos:prefLabel>
    <skos:prefLabel xml:lang="it">ballerine</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Ballerina</skos:altLabel>
    <skos:prefLabel xml:lang="es">Zapatillas de ballet</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Baletske patike</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Balletschoenen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Sapatilhas de balé</skos:prefLabel>
    <skos:altLabel xml:lang="it">scarpette di danza</skos:altLabel>
    <skos:altLabel xml:lang="it">scarpette da ballerina</skos:altLabel>
    <skos:prefLabel xml:lang="de">Ballettschuhe</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">chaussons de danse</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Balettskor</skos:prefLabel>
    <skos:altLabel xml:lang="fr">ballerines</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10065">
    <skos:scopeNote xml:lang="en">"Separate or detachable shirt fronts, often with a collar, worn under another garment. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Peitilho</skos:prefLabel>
    <skos:altLabel xml:lang="de">Betrügerchen</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Frontje</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">plastron</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="sv">Skjortbröst</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">Dickey</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210060\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210060"/>
    <skos:prefLabel xml:lang="el">Πλαστρόν</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sparato (di camicia)</skos:prefLabel>
    <skos:altLabel xml:lang="de">Vorhemd</skos:altLabel>
    <skos:prefLabel xml:lang="es">Pechera</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Plastron</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Serviteur</skos:altLabel>
    <skos:prefLabel xml:lang="en">Shirtfront</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hemdbrust</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10014">
    <skos:altLabel xml:lang="el">Τήβεννος</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Long, loose garments, which tend to be of one piece and to open down the front, cut with flowing lines; worn for ordinary wear by men and women during the Middle Ages and until the modern period, especially in Asian and African countries. Also, similar garments often of elegant style worn for ceremonial or official occasions or as a symbol of office or profession. Also, term used generally from the 18th century through the early 20th century for fashionable women's dress of varying form. "</skos:scopeNote>
    <skos:altLabel xml:lang="nl">Toga</skos:altLabel>
    <skos:altLabel xml:lang="sv">kaftan</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="sv">rock</skos:altLabel>
    <skos:altLabel xml:lang="fr">toge</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Robe</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Robe</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Robe</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Robe</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Toga</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Robe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Robe</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209852\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209852"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μανδύας</skos:prefLabel>
    <skos:altLabel xml:lang="it">Abito ufficiale</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Odora</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Gewaad</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10606">
    <skos:altLabel xml:lang="nl">Bavet</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Bavoir</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Portikla</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Slab</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Hakklapp</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Bib</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="el">Σαλιάρα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Lätzchen</skos:prefLabel>
    <skos:altLabel xml:lang="de">Brustlatz</skos:altLabel>
    <skos:prefLabel xml:lang="es">Babero</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10093">
    <skos:prefLabel xml:lang="fr">panier</skos:prefLabel>
    <skos:altLabel xml:lang="de">Reifrock im 18. Jahrhundert.</skos:altLabel>
    <skos:altLabel xml:lang="es">Guardainfantes</skos:altLabel>
    <skos:altLabel xml:lang="es">verdugado</skos:altLabel>
    <skos:prefLabel xml:lang="es">Tontillo</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Oval hoops worn to extend a woman's skirt at the side.</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216740\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216740"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="sv">Panier</skos:prefLabel>
    <skos:altLabel xml:lang="it">Sottogonna rigida con stecche che si sviluppa sui fianchi</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Anquinhas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Panniers</skos:prefLabel>
    <skos:altLabel xml:lang="de">Panier à coudes</skos:altLabel>
    <skos:prefLabel xml:lang="it">Panniers</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Panniers</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Panijeri</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Reifrock</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pannier</skos:prefLabel>
    <skos:altLabel xml:lang="de">Panier</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="en">Side-hoop</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10375">
    <skos:altLabel xml:lang="nl">Ongebleekt katoen</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Calico</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kaliko</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="el">Τσίτι</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Cotton textile, heavier than muslin, plain, dyed, or with patterns printed in one or more colors. In the 18th and 19th centuries, the term referred to printed, colored or plain cloth from India; now it refers generally to cotton prints with small, stylized patterns. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="de">Nessel</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="en">calico</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Kattun</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kalikå</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300132872\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300132872"/>
    <skos:prefLabel xml:lang="pt">Pano crú fino</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calicó</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">calicot</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10179">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10177"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Women's scarves of some light fabric, often white and sheer, draped around the neck and shoulders and tied in a knot with ends hanging loosely. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="sv">Damhalsduk</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Fichu</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fichu</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Fichu: scialle leggero</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Fichu's</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fichu</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216738\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216738"/>
    <skos:prefLabel xml:lang="sr">Marama za grudi</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fichú</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φισού</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fichu</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fichu</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10150">
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi na telu</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Αξεσουάρ που φοριούνται</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräkttillbehör</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10132"/>
    <skos:prefLabel xml:lang="nl">Gedragen accessoires</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accessori indossati</skos:prefLabel>
    <skos:prefLabel xml:lang="de">getragene Bekleidungsaccessoires</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorio de vestir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">accessoires de costumes portés</skos:altLabel>
    <skos:prefLabel xml:lang="en">costume accessories worn</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Accessoires portés</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Acessórios de traje usados no corpo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209274\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209274"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10307">
    <skos:prefLabel xml:lang="sr">Crtež</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">dessin</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Teckning</skos:prefLabel>
    <skos:prefLabel xml:lang="en">drawing</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Tekening</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300033973\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300033973"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:scopeNote xml:lang="en">"Visual works produced by drawing, which is the application of lines on a surface, often paper, by using a pencil, pen, chalk, or some other tracing instrument to focus on the delineation of form rather than the application of color. This term is often defined broadly to refer to computer-generated images as well. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Desenho</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zeichnung</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σχέδιο</skos:prefLabel>
    <skos:prefLabel xml:lang="it">disegno</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Dibujo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10010">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046178\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046178"/>
    <skos:prefLabel xml:lang="nl">Tuinbroek</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fato-macaco</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Pardessus</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Radni kombinezon</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Salopette</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="pt">Bata</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="fr">Salopette</skos:altLabel>
    <skos:scopeNote xml:lang="en">"One-piece garments consisting of trousers with a bib and having straps extending from the bib to the back. For one-piece garments consisting of a trouserlike portion and a full top with or without sleeves worn over other garments for protection use ""coveralls."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Σαλοπέτα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Sobretodo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10639">
    <skos:prefLabel xml:lang="sv">Medaljong</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Medaljon</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10161"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Candado</skos:prefLabel>
    <skos:prefLabel xml:lang="en">locket</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">médaillon</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Medaillon</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10039">
    <skos:prefLabel xml:lang="sr">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Anorak</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Anorak</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Anorak</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Impermeável</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Anoraque</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300256721\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300256721"/>
    <skos:prefLabel xml:lang="el">Ανορακ</skos:prefLabel>
    <skos:altLabel xml:lang="it">Giacca a vento</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:scopeNote xml:lang="en">"Hooded pullover hip-length garments made of weather-resistant material often with zipper closure at neck and drawstring hem; may be lined and have a front-center pocket; worn for sports. For hooded jackets or pullover garments reaching to the thighs or knees made of skins or hides or water-repellent or windproof fabric, use ""parkas."" (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10625">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10123"/>
    <skos:prefLabel xml:lang="sr">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tankini</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tankini</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10361">
    <skos:prefLabel xml:lang="el">Ρεγιόν</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">rayonne</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Rayon</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Rayon</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Rayon</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014059\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014059"/>
    <skos:prefLabel xml:lang="nl">Rayon</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Rayón</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Kunstzijde</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">rayon</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Synthetic fiber made from regenerated cellulose. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Veštačka svila</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10360"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10164">
    <skos:prefLabel xml:lang="de">Armband</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pulsera</skos:prefLabel>
    <skos:altLabel xml:lang="es">Brazalete</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300045991\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300045991"/>
    <skos:prefLabel xml:lang="el">Βραχιόλι</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pulseira (joalharia)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Braccialetti</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bracelet</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10163"/>
    <skos:altLabel xml:lang="de">Armreif (Schmuck)</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Ornamental bands or circlets worn on the lower arm. Use ""armlets"" for similar articles worn on the upper arm. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Narukvica</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Armbanden (sieraden)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">bracelet (jewelry)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Armband</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10052">
    <skos:altLabel xml:lang="it">(Paio di) maniche</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Manga</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μανίκια</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mouw</skos:altLabel>
    <skos:prefLabel xml:lang="fr">manches</skos:prefLabel>
    <skos:altLabel xml:lang="de">Paar Ärmel</skos:altLabel>
    <skos:prefLabel xml:lang="en">Sleeve</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Manga</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Manica</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Manche</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Mouwen</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Rukav</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ärmel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210530\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210530"/>
    <skos:altLabel xml:lang="pt">(Par de) mangas</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">Sleeves</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Ärmar</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Shaped coverings for the arm. Originally made separate from the main garment and attached by lacing through eyelets at the shoulder; later, often made as component parts of garments. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10025">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Maglietta</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Chemise</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Camisa</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Camisa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Skjorta</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:scopeNote xml:lang="en">"Generally refers to various types of main garments for the upper body, made with or without sleeves and worn over or tucked in the waistband of a skirt, trousers, or the like. Usually having a collar, which may be detachable, often a front opening, and sometimes pockets. Specifically often refers to an article of male attire with long sleeves terminating in wristbands or cuffs. It originally referred to undergarments for the upper part of the body, made of linen, calico, flannel, silk, or other washable material; originally always worn next to the skin. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Shirt</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Košulja</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πουκάμισο</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212499\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212499"/>
    <skos:prefLabel xml:lang="nl">Overhemd</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10679">
    <skos:prefLabel xml:lang="de">Acryl</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ακριλικό</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Acrílico</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Akrilik</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Acrylique</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Akryl</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Acrylic</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10262">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Botas de esquí</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ski Stiefel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Skischoenen</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Skilaars</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Rigid boots securely fastened to the foot by various means such as laces, buckles, or hinges, and which lock into position in a ski binding. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">chaussures de ski</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μπότες σκι</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210046\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210046"/>
    <skos:prefLabel xml:lang="en">ski boots</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Skijaške čizme</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Skidpjäxor</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botas de esqui</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:prefLabel xml:lang="it">scarponi da sci</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10689">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10504"/>
    <skos:prefLabel xml:lang="en">Bovine leather</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10645">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="fr">Bonnet de bain</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σκούφια μπάνιου</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Badekappe</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kapa za kupanje</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Badmössa</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Swim cap</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="sr">Kapa za plivanje</skos:altLabel>
    <skos:altLabel xml:lang="de">Bademütze</skos:altLabel>
    <skos:prefLabel xml:lang="es">Gorro de baño</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10062">
    <skos:prefLabel xml:lang="pt">Botões</skos:prefLabel>
    <skos:altLabel xml:lang="fr">boutons</skos:altLabel>
    <skos:prefLabel xml:lang="de">Knöpfe</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10060"/>
    <skos:prefLabel xml:lang="sr">Dugmad</skos:prefLabel>
    <skos:altLabel xml:lang="sv">knappar</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Knappar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Knoop (bevestigingsmiddel)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Disks, balls, or devices of other shape having holes or a shank by which they are sewn or secured to an article and that are used as fasteners by passing through a buttonhole or loop or a trimming. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Bottoni</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Botón</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κουμπιά</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bouton</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Buttons</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300239261\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300239261"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10659">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Obuća sa kaišem preko pete</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sling-back</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures à lanières</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Slingbackskor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Ξώφτερνα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Slingbacks</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chinelas con trabilla</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10376">
    <skos:altLabel xml:lang="fr">canevas</skos:altLabel>
    <skos:prefLabel xml:lang="de">Stramin</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lienzo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014078\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014078"/>
    <skos:prefLabel xml:lang="sv">Kanvas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Platno</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Tela</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Canvas</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Closely woven textile made in various weights, usually of flax, hemp, jute, or cotton, used especially for sails, tarpaulins, awnings, upholstery, and as a support for oil painting. Also used for a loosely woven, latticelike mesh made of similar material, used as a needlepoint foundation. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="el">Καμβάς</skos:prefLabel>
    <skos:altLabel xml:lang="es">Canvas</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">canvas</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">toile</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10605">
    <skos:scopeNote xml:lang="en">Any time in the past other than the present. It is sometimes confusing that modern costumes are considered period clothes when the setting is just a few years past (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="en">Period costume (film)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10178">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">großes Taschentuch</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Large silk or cotton handkerchiefs or scarves that usually have a solid ground of red or blue with simple figures or geometric forms in white or yellow resulting from a mode of dyeing in which the cloth is tied in different places, to prevent the parts from receiving the dye. From a Hindustani word for this process. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Μπαντάνα</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bandanna</skos:prefLabel>
    <skos:altLabel xml:lang="de">quadratisches Kopf- oder Halstuch</skos:altLabel>
    <skos:altLabel xml:lang="de">kleines Halstuch</skos:altLabel>
    <skos:prefLabel xml:lang="de">"großes, bunt-oder weiß geflecktes Halstuch"</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Bandana</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bandana</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bandana's</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bandana</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221733\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221733"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10177"/>
    <skos:prefLabel xml:lang="es">Bandana</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Bandana</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">bandana</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10011">
    <skos:altLabel xml:lang="sv">byxdräkt</skos:altLabel>
    <skos:prefLabel xml:lang="it">Completo tailleur giacca pantalone</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Pantalon de costume</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hosenanzug</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Broekpak</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046183\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046183"/>
    <skos:prefLabel xml:lang="sr">Žensko odelo sa pantalonama</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Trouser suit</skos:prefLabel>
    <skos:altLabel xml:lang="en">pantsuit</skos:altLabel>
    <skos:prefLabel xml:lang="el">Σύνολο με παντελόνι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Women's suits consisting of trousers and a matching top in the form of a jacket, tunic or the like. May also have a matching blouse or belt. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Fato de calças</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="fr">Tailleur-pantalon</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Byxdress</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Trajes pantalón</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10619">
    <skos:prefLabel xml:lang="fr">Chemisette</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10084"/>
    <skos:prefLabel xml:lang="en">Chemisette</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10626">
    <skos:prefLabel xml:lang="fr">Paréo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Παρεό</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10123"/>
    <skos:prefLabel xml:lang="sr">Pareo</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Sarong</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Pareo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kanga</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Pareo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pareo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pareo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10092">
    <skos:prefLabel xml:lang="de">halbversteifte Schnürbrust</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210588\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210588"/>
    <skos:prefLabel xml:lang="fr">corset sans baleine</skos:prefLabel>
    <skos:altLabel xml:lang="es">Corpiño</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="sr">Pojas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Gördel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπουστάκι</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jump</skos:prefLabel>
    <skos:altLabel xml:lang="es">justillo</skos:altLabel>
    <skos:scopeNote xml:lang="en">Underbodices similar to stays but looser and without bones. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Corsetto intimo da casa senza stecche</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Babygro</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Jumps</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Corsé sin ballenas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10362">
    <skos:prefLabel xml:lang="pt">Proteicas</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres d'origine protéique</skos:prefLabel>
    <skos:prefLabel xml:lang="en">proteinic</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10359"/>
    <skos:prefLabel xml:lang="es">Fibras proteínicas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πρωτεινικές ύλες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Regenerierte Proteinfasern</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Proteïne</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Proteinska vlakna</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Proteinfibrer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10306">
    <skos:prefLabel xml:lang="es">Estampa de moda</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Tessuto stampato</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10305"/>
    <skos:prefLabel xml:lang="sv">Modetryck</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modeprent</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Estampado</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Τυπωτά σχέδια</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modedruck</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">estampe de mode</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Modna štampa</skos:prefLabel>
    <skos:altLabel xml:lang="de">Modegrafik</skos:altLabel>
    <skos:prefLabel xml:lang="en">fashion print</skos:prefLabel>
    <skos:altLabel xml:lang="el">Εμπριμέ</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10163">
    <skos:prefLabel xml:lang="nl">Sieraden gedragen aan armen en handen</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gioielli da indossare alle braccia o alle mani</skos:prefLabel>
    <skos:prefLabel xml:lang="de">an Armen und Händen getragener Schmuck</skos:prefLabel>
    <skos:altLabel xml:lang="fr">bijoux portés sur les bras et les mains</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Armsmycken</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit za ruke i šake</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bijoux de bras et de mains</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jewelry worn on arms and hands</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κόσμημα χεριών</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Joalharia usada nos braços e mãos</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209288\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209288"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Joyería para brazos y manos</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10026">
    <skos:prefLabel xml:lang="sr">Džemper</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Pull-over</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Sweat shirt</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Knitted or crocheted garments worn on the upper body which extend to the waist or below. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Camisola</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pullover</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sweater</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sweater</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Πουλόβερ με μανίκια</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Trui</skos:altLabel>
    <skos:prefLabel xml:lang="es">Suéter</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="it">Maglioncino</skos:prefLabel>
    <skos:altLabel xml:lang="de">Sweater</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209900\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209900"/>
    <skos:prefLabel xml:lang="sv">Tröja</skos:prefLabel>
    <skos:altLabel xml:lang="es">Chompa</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10263">
    <skos:altLabel xml:lang="sv">Vinterkängor</skos:altLabel>
    <skos:altLabel xml:lang="sv">Vinterstövlar</skos:altLabel>
    <skos:prefLabel xml:lang="fr">après ski</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:prefLabel xml:lang="sr">Čizme za posle skijanja</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Après-Ski Stiefel</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Boots</skos:prefLabel>
    <skos:prefLabel xml:lang="it">calzature dopo sci</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botas de neve après-ski</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sv">Afterskistövlar</skos:altLabel>
    <skos:prefLabel xml:lang="es">Botas de après-ski</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Après-skilaars</skos:prefLabel>
    <skos:prefLabel xml:lang="en">après-ski boots</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπότες après-ski</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10053">
    <skos:altLabel xml:lang="it">(Paio di) polsini</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Manžetna</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="nl">Manchetten</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Puño</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Manchet (kledingsaccessoire)</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Manschetter</skos:prefLabel>
    <skos:altLabel xml:lang="pt">(Par de) punhos</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Any of various folds or bands that serve as a finish or trimming on items of costume or costume accessories, such as on trousers, gloves, or boots. In a military context, sleeve cuffs may be employed to designate unit or rank. Includes those made as separate items in addition to those made as component parts of garments or costume accessories. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="de">Ärmelaufschlag</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">poignet</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μανσέτες</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Punho</skos:prefLabel>
    <skos:altLabel xml:lang="en">Cuffs</skos:altLabel>
    <skos:altLabel xml:lang="de">Manschettenpaar</skos:altLabel>
    <skos:altLabel xml:lang="de">Hosenaufschlag</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Manschette</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Polsino</skos:prefLabel>
    <skos:altLabel xml:lang="fr">manchette</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210488\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210488"/>
    <skos:prefLabel xml:lang="en">Cuff</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10177">
    <skos:altLabel xml:lang="fr">foulards (accessoires de costumes)</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046123\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046123"/>
    <skos:prefLabel xml:lang="de">Schal</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φουλάρι</skos:prefLabel>
    <skos:altLabel xml:lang="sv">halsduk</skos:altLabel>
    <skos:prefLabel xml:lang="es">Bufanda</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sjaals</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">écharpe</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kopftuch</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Tuch</skos:altLabel>
    <skos:prefLabel xml:lang="en">scarve (costume accessory)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Lenço (acessórios de traje)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sciarpe</skos:prefLabel>
    <skos:altLabel xml:lang="de">Halstuch</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:scopeNote xml:lang="en">"Pieces of cloth made in varying widths and lengths and worn for decoration or warmth across the shoulders, around the neck, over the head, or about the waist. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Scarf</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sv">Sjal</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10377">
    <skos:scopeNote xml:lang="en">"Sheer, lightweight plain-woven textile of fine, tightly-twisted yarn, originally of silk now also of various synthetic fibers, used, for example, for scarves, dresses, blouses, underwear, and veils. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Chiffong</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Chiffon</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σιφόν</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chiffon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Chiffon</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300249449\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300249449"/>
    <skos:prefLabel xml:lang="en">chiffon</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šifon</skos:prefLabel>
    <skos:altLabel xml:lang="de">Chiffon</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="fr">mousseline de soie</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10372"/>
    <skos:prefLabel xml:lang="de">Crêpe</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10063">
    <skos:altLabel xml:lang="pt">Colchete</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Gancho</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Hook</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">crochet</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hake</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Haken</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10060"/>
    <skos:prefLabel xml:lang="it">Gancetti</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300033537\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300033537"/>
    <skos:prefLabel xml:lang="el">Κόπιτσα</skos:prefLabel>
    <skos:altLabel xml:lang="es">anzuelo</skos:altLabel>
    <skos:prefLabel xml:lang="es">Presilla</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="es">Gancho</skos:altLabel>
    <skos:scopeNote xml:lang="en">Bent or curved devices used for suspending or fastening objects or for attaching objects to a surface. (AAT)</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Haakje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Kopča</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Crocher</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10646">
    <skos:prefLabel xml:lang="sv">Mössa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sv">Hätta</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Κουάφ</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Coiffe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Coif</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="es">Cofia</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Haube</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kapa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10285">
    <skos:prefLabel xml:lang="en">leg warmers</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209985\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209985"/>
    <skos:prefLabel xml:lang="pt">Caneleiras</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:prefLabel xml:lang="fr">jambières</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Beinstulpen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Beenwarmers</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Calentadores</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γκέτες πλεκτές</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Beinwärmer</skos:altLabel>
    <skos:prefLabel xml:lang="it">Scaldamuscoli</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Grejači</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Benvärmare</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Footless leg coverings usually worn over tights, trousers, boots, or the like, for warmth or as a fashionable accessory. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10217">
    <skos:prefLabel xml:lang="en">accessories worn above the waist</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorios de cintura para arriba</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires portées au dessu de la taille</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ  για το μπούστο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi iznad pojasa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accessori indossati sopra la vita</skos:prefLabel>
    <skos:prefLabel xml:lang="de">oberhalb der Taille getragene Accessoires</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211602\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211602"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Accessoirer burna ovan midjan</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Acessórios usados acima da cintura</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Accessoires gedragen boven het middel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10060">
    <skos:prefLabel xml:lang="nl">Sluiting</skos:prefLabel>
    <skos:altLabel xml:lang="de">Verschluss</skos:altLabel>
    <skos:altLabel xml:lang="fr">Fermeture</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Abotoamento</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Bevestigingsuitrusting</skos:altLabel>
    <skos:altLabel xml:lang="fr">lien</skos:altLabel>
    <skos:scopeNote xml:lang="en">Devices that fasten or hold together separate parts. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Schliesse</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Chiusure</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pasador</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Fastener</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">"Kopča, šnala, driker, zatvarač"</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κούμπωμα</skos:prefLabel>
    <skos:altLabel xml:lang="sv">ihopfästning av plagg</skos:altLabel>
    <skos:altLabel xml:lang="de">Haken und Öse</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300036363\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300036363"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">attache</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ihopsättning</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10480">
    <skos:prefLabel xml:lang="el">Ραμί</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014071\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014071"/>
    <skos:prefLabel xml:lang="es">Ramio</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Ramija</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">ramie</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ramee</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="en">Ramie</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ramie</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Rami</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Rami</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10500">
    <skos:prefLabel xml:lang="pt">Cerimónia de abertura</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">cérémonie d'ouverture</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Openingsceremonie</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Opening Ceremony</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Inauguración</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Otvaranje</skos:prefLabel>
    <skos:altLabel xml:lang="de">Eröffnungsveranstaltung</skos:altLabel>
    <skos:prefLabel xml:lang="de">Eröffnungszeremonie</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Inaugurazione</skos:prefLabel>
    <skos:altLabel xml:lang="de">Eröffnungsfeier</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Invigning</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10692">
    <skos:prefLabel xml:lang="sv">Handgjort</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Handmade</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hecho a mano</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Fait main</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10366"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">handgemacht</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χειροποίητο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Ručni rad</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10568">
    <skos:prefLabel xml:lang="nl">Stras</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:altLabel xml:lang="en">strass</skos:altLabel>
    <skos:altLabel xml:lang="nl">Rijnsteen</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Štras</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Estrás</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010806\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010806"/>
    <skos:prefLabel xml:lang="en">Rhinestone</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">paste</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Strass</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Strass</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Strass</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10275">
    <skos:prefLabel xml:lang="es">Herramientas de creación textil</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Gereedschap en machines voor textielcreatie</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Verktyg för textil tillverkning</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Εργαλεία υφαντουργίας</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Tools for textile creation</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10274"/>
    <skos:prefLabel xml:lang="de">Werkzeuge der Textilherstellung</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Alat za pravljenje tekstila</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Handwerkgerei</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Ferramentas para a criação têxtil</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">outil pour la création textile</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10203">
    <skos:prefLabel xml:lang="es">Sombrero de vaquero</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210737\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210737"/>
    <skos:prefLabel xml:lang="fr">chapeau de cow boy</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καουμπόικο</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Wide-brimmed hats with a large soft crown of the type worn by ranch hands in the American West. (AAT)</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Chapéu de cowboy</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Cowboyhattar</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cappello da cowboy</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">cowboy hat</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Kaubojski šešir</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Cowboyhut</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Cowboyhoeden</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10295">
    <skos:prefLabel xml:lang="fr">châle</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Xaile</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Omslagdoek</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Sjalar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sjaals</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Large pieces of square, oblong, or triangular cloth worn over main garments as a covering for the shoulders and arms. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">shawl</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šal</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schal</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σάλι</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209991\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209991"/>
    <skos:prefLabel xml:lang="es">Echarpe</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="it">Scialli</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10080">
    <skos:prefLabel xml:lang="it">Guaina</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bodystocking</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bodystocking</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Body stocking</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bodi</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"One-piece, skin-tight garments made of knitted or stretch material, usually covering the feet, legs, and torso, and sometimes arms; worn under other clothing. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Collant de gymnastique</skos:prefLabel>
    <skos:altLabel xml:lang="en">Body suit</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300253714\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300253714"/>
    <skos:prefLabel xml:lang="el">Κορμάκι</skos:prefLabel>
    <skos:altLabel xml:lang="sv">kroppsstrumpa</skos:altLabel>
    <skos:altLabel xml:lang="de">Body</skos:altLabel>
    <skos:altLabel xml:lang="it">Body</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Body</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Body</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Body</skos:prefLabel>
    <skos:altLabel xml:lang="fr">justaucorps</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10518">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Tarjeta postal</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Briefkaart</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Carte Postale</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300026816\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300026816"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10305"/>
    <skos:prefLabel xml:lang="sv">Vykort</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Postkaart</skos:altLabel>
    <skos:altLabel xml:lang="sv">Brevkort</skos:altLabel>
    <skos:prefLabel xml:lang="it">Cartolina postale</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Razglednica</skos:prefLabel>
    <skos:prefLabel xml:lang="en">postcard</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Postkarte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10594">
    <skos:prefLabel xml:lang="sv">Djurmage</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Pansen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Tripa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pens</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">tripe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">tripe</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:altLabel xml:lang="sv">Tarm</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Tripe</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10318">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">music</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Muzika</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Música</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">musique</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Música</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Muziek</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The art concerned with the combining of vocal or instrumental sounds in measured time to communicate emotions, ideas, or states of mind, usually according to cultural standards of rhythm, melody, and, in most Western music, harmony. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300054146\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300054146"/>
    <skos:prefLabel xml:lang="it">musica</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Musik</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Musik</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Μουσική</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10554">
    <skos:prefLabel xml:lang="es">Cromo</skos:prefLabel>
    <skos:prefLabel xml:lang="en">chrome</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Chroom</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Hrom</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011019\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011019"/>
    <skos:prefLabel xml:lang="sv">Krom</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="de">Chrom</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Chrome</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10528">
    <skos:altLabel xml:lang="de">Raffiabast</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Raphia</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="en">raffia</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Rafija</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Palm</skos:prefLabel>
    <skos:altLabel xml:lang="de">Raphia</skos:altLabel>
    <skos:prefLabel xml:lang="es">Rafia</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014051\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014051"/>
    <skos:prefLabel xml:lang="de">Raphiabast</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10526"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Raffia</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10404">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300128438\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300128438"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="sr">Zelena</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Grön</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Grün</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Groen</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πράσινο</skos:prefLabel>
    <skos:altLabel xml:lang="it">Verde</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Green</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Vert</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Verde</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Verde</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Verde</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10604">
    <skos:scopeNote xml:lang="en">"Required protection for perspiration, loss and damage, weather, continuity and aging, stunt doubles, second and third units, squibs and action sequences. Multiple garments (replacements) means the director never has to wait for a shot (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="en">Multiples (costume)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10580">
    <skos:prefLabel xml:lang="nl">Kurk</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Corcho</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cork</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kork</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Liège</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cortiça</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011862\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011862"/>
    <skos:altLabel xml:lang="de">Korken</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Pluta</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kork</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10400">
    <skos:altLabel xml:lang="de">Farben</skos:altLabel>
    <skos:prefLabel xml:lang="en">Colours</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Boje</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Couleurs</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300080438\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300080438"/>
    <skos:scopeNote xml:lang="en">"Refers to individual hues or tints, particularly the chromatic colors in the spectrum and the achromatic colors or neutrals. With reference to color in theory and the science of perception, see ""color (perceived attribute)."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Färger</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kleuren</skos:prefLabel>
    <skos:altLabel xml:lang="de">Muster</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="it">Colori</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="es">Colores</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Colori</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Farbe</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χρώματα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:prefLabel xml:lang="pt">Cores</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10614">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="en">Overcoat</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10418">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Impressão com fio</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Impression sur fil</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Štampa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kettingdruk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Sydda mönster</skos:prefLabel>
    <skos:prefLabel xml:lang="en">thread print</skos:prefLabel>
    <skos:prefLabel xml:lang="de">kettbedruckt</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Estampación por hilos de urdimbre</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:altLabel xml:lang="de">Kettdruck</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10061">
    <skos:prefLabel xml:lang="fr">fermeture éclair</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Zipper</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Fermeture- éclair</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Reißverschluss</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cremallera</skos:prefLabel>
    <skos:prefLabel xml:lang="it">cerniera</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Dragkedja</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fecho de correr</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300239276\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300239276"/>
    <skos:prefLabel xml:lang="el">Φερμουάρ</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ritssluiting</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10060"/>
    <skos:altLabel xml:lang="nl">Rits</skos:altLabel>
    <skos:scopeNote xml:lang="en">Fasteners consisting usually of two rows of metal or plastic teeth on strips of tape for binding to the edges of an opening and having a sliding piece that closes the opening by drawing the teeth into interlocking position. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Rajsferšlus</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10218">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Flor para el ojal</skos:prefLabel>
    <skos:altLabel xml:lang="de">Blume im Knopfloch</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Flowers or small bouquets worn, usually by a man, in the buttonhole of the lapel. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Flor de lapela</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10217"/>
    <skos:prefLabel xml:lang="sr">Rupica na reveru</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Knapphålssmycken</skos:prefLabel>
    <skos:altLabel xml:lang="de">Knopflochsträußchen</skos:altLabel>
    <skos:prefLabel xml:lang="it">Fiore all'occhiello</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Knopflochblume</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπουτονιέρα</skos:prefLabel>
    <skos:prefLabel xml:lang="en">boutonniere</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">boutonnière</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216952\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216952"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Boutonnières</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10284">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">calzebrache</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">calzebrache</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Meias-calções</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Benholkar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Calzebrache</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Calzebrache</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nogavice</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Chausses</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:scopeNote xml:lang="en">"Hosiery that covers the calf and thigh, having ties or garters attached at the top to hold them up at the level of the waist or crotch, worn especially in Medieval and Renaissance Europe. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300378956\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300378956"/>
    <skos:prefLabel xml:lang="de">Beinlinge</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10481">
    <skos:prefLabel xml:lang="el">Ίνες καρύδας</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kokosnötsfiber</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibra de coco</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibra de côco</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kokosnussfaser</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kokosovo vlakno</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kokosvezel</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kokosfaser</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">fibre de coco</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014056\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014056"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Coconut fibre</skos:prefLabel>
    <skos:altLabel xml:lang="en">Coir</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10693">
    <skos:scopeNote xml:lang="en">"An old Hollywood expression for aging the costumes by helping the clothes look “lived in” and essential for creating authentic characters. Aging is accomplished by sandpaper, sponges, spray bottles, steel brushes, dye, bleaching (fading), glue, spray paint, matches, cigarette lighters, motor oil, cooking oil, food coloring, Kensington gore (movie blood) and Fuller’s earth (sterile movie dirt). There are also useful commercial wax crayons available like “Schmutzstik” and “Schmere” for an on-set kit (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
    <skos:prefLabel xml:lang="en">Distressing (aging/breaking down)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10540">
    <skos:prefLabel xml:lang="sr">Zečije krzno</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10539"/>
    <skos:prefLabel xml:lang="es">Piel de conejo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Kaninpäls</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Kaninchenpelz</skos:prefLabel>
    <skos:prefLabel xml:lang="en">rabbit fur</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300386669\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300386669"/>
    <skos:prefLabel xml:lang="fr">Fourrure de lapin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Konijnenpels</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10569">
    <skos:prefLabel xml:lang="sr">Kristal</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kristal</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221157\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221157"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="es">Cristal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cristal</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Crystal</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kristall</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Kristall</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10501">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
    <skos:prefLabel xml:lang="sv">Digital bild</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215302\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215302"/>
    <skos:altLabel xml:lang="fr">image numérisée</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Digitaal beeld</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Imagen digital</skos:prefLabel>
    <skos:prefLabel xml:lang="en">digital image</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">image numérique</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Digitalna slika</skos:prefLabel>
    <skos:prefLabel xml:lang="it">immagine digitale</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Digitale foto</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Imagem digital</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ψηφιακή εικόνα</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">digitales Bild</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10274">
    <skos:prefLabel xml:lang="nl">Gereedschap voor de mode-industrie</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tool for fashion industry</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">outil</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Herramientas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εργαλεία βομηχανίας μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Verktyg för modeindustri</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300022238\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300022238"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Werkzeuge der Modeindustrie</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Ferramentas para a indústria da moda</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"descriptors for equipment used in processing materials and fabricating objects as well as descriptors associated with activities and disciplines in the construction industry, design professions, the fine and decorative arts, and other aspects of material culture. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Alat za modnu industriju</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Werkzeuge der Bekleidungsindustrie</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10603">
    <skos:scopeNote xml:lang="en">"Every piece of clothing worn in a movie is a costume whether that garment is borrowed, rented, purchased, of designed and created especially for the film. Those films with no custom-made garments are still “designed”. Each piece is chosen, altered and aged for the character, the story and composition of the frame (UCLA)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Kostuum (film)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="en">Costume (film)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10294">
    <skos:prefLabel xml:lang="sv">Manschetter</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="pt">Punhos (componentes de traje)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210488\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210488"/>
    <skos:prefLabel xml:lang="en">cuffs (costume components)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Manžetne</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">manchette</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Any of various folds or bands that serve as a finish or trimming on items of costume or costume accessories, such as on trousers, gloves, or boots. In a military context, sleeve cuffs may be employed to designate unit or rank. Includes those made as separate items in addition to those made as component parts of garments or costume accessories. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Μανσέτες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Puños (componente de vestuario)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Manschetten</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Manchetten</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Polsini</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10541">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10539"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Fourrure de renard</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fox fur</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fuchspelz</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Rävpäls</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Lisičije krzno</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel de zorro</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Vossenpels</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10593">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="es">Pergamino</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Perkament</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Pergament</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Parchemin</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pergament</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pergament</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011851\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011851"/>
    <skos:prefLabel xml:lang="en">parchment</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pergamena</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10204">
    <skos:altLabel xml:lang="pt">Borsalino</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">fedora</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chapeau mou</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="pt">Fedora</skos:altLabel>
    <skos:altLabel xml:lang="de">Fedora</skos:altLabel>
    <skos:prefLabel xml:lang="de">mittelhoher Herrenfilzhut</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Felt hats with a medium-sized rolled brim and a high crown creased lengthwise. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Gleufhoeden</skos:prefLabel>
    <skos:altLabel xml:lang="fr">fedoras</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:altLabel xml:lang="de">Chasseur</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Fedora šešir</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Hamburg šešir</skos:altLabel>
    <skos:prefLabel xml:lang="it">Fedora</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fedora</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210743\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210743"/>
    <skos:prefLabel xml:lang="el">Φεντόρα</skos:prefLabel>
    <skos:altLabel xml:lang="el">Ρεπούμπλικα</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Chapéu de feltro</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Filthattar</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10519">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300026823\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300026823"/>
    <skos:prefLabel xml:lang="nl">Visitekaartje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Visitkort</skos:prefLabel>
    <skos:altLabel xml:lang="de">Carte de visite</skos:altLabel>
    <skos:prefLabel xml:lang="es">Tarjeta de visita</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Vizit karta</skos:prefLabel>
    <skos:prefLabel xml:lang="en">visiting card</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">carte de visite</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Visitenkarte</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10305"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10081">
    <skos:scopeNote xml:lang="en">"Women's close-fitting underwear worn for bust support and varying greatly in style, ranging in width from a band to a waist-length bodice, made with or without cups or straps and often boned or wired for additional support or separation. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="fr">Soutien-gorge</skos:altLabel>
    <skos:prefLabel xml:lang="es">Sujetador</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Büstenhalter</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">soutien gorge</skos:prefLabel>
    <skos:altLabel xml:lang="es">Sostén</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Sutiã</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Bysthållare</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Beha</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">bh</skos:altLabel>
    <skos:prefLabel xml:lang="el">Στηθόδεσμος</skos:prefLabel>
    <skos:altLabel xml:lang="de">BH</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210538\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210538"/>
    <skos:prefLabel xml:lang="it">Reggiseno</skos:prefLabel>
    <skos:altLabel xml:lang="en">Brassiere</skos:altLabel>
    <skos:prefLabel xml:lang="en">Bra</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Brushalter</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Bustehouder</skos:altLabel>
    <skos:altLabel xml:lang="sr">Grudnjak</skos:altLabel>
    <skos:prefLabel xml:lang="sv">BH</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10529">
    <skos:prefLabel xml:lang="nl">Riet</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Caña de junco</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:altLabel xml:lang="de">Schilfgras</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011878\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011878"/>
    <skos:prefLabel xml:lang="sr">Trska</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Roseau</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vass</skos:prefLabel>
    <skos:prefLabel xml:lang="en">reed</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10526"/>
    <skos:altLabel xml:lang="de">Schilfrohr</skos:altLabel>
    <skos:prefLabel xml:lang="de">Reet</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10319">
    <skos:prefLabel xml:lang="en">spoken text</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Προφορικό κείμενο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Izgovoren tekst</skos:prefLabel>
    <skos:prefLabel xml:lang="de">gesprochener Text</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Texto falado</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Tal</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Talad text</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">testo parlato</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Texto hablado</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Gesproken tekst</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">texte récité</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10555">
    <skos:prefLabel xml:lang="es">Latón</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mässing</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010946\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010946"/>
    <skos:prefLabel xml:lang="en">brass</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">Azófar</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Mesing</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Laiton</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Messing</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Messing</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ottone</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10403">
    <skos:prefLabel xml:lang="de">Braun</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Marrone</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="it">Marrone</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Castanho</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Smeđa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="nl">Bruin</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Brun</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Brun</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Marrón</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Brown</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="el">Καφέ</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300127490\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300127490"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10417">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="fr">Jaune</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Geel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Yellow</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Κίτρινο</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Amarillo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300127794\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300127794"/>
    <skos:prefLabel xml:lang="sv">Gul</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Amarelo</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Žuta</skos:prefLabel>
    <skos:altLabel xml:lang="it">Giallo</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Gelb</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Giallo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10613">
    <skos:prefLabel xml:lang="nl">Manteltje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10046"/>
    <skos:prefLabel xml:lang="en">Mantelet</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10551">
    <skos:altLabel xml:lang="sr">Lim</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Tin</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Estaño</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Étain</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="sv">Tenn</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zinn</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Kalaj</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300133748\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300133748"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10287">
    <skos:prefLabel xml:lang="es">Calcetines</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">chaussettes</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σοσόνια</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">Hosiery that covers the foot and extends to somewhere below the knee. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Meias</skos:prefLabel>
    <skos:altLabel xml:lang="de">Strümpfe</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Sockor</skos:prefLabel>
    <skos:prefLabel xml:lang="en">socks</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Calzini</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Socken</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čarape</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046087\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046087"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:prefLabel xml:lang="nl">Sokken</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10002">
    <skos:altLabel xml:lang="fr">Vêtements</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209263\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209263"/>
    <skos:prefLabel xml:lang="es">Prendas principales</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Oberbekleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário principal</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βασικά ενδύματα</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bovenkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Main garments</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Principali capi di vestiario</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10001"/>
    <skos:prefLabel xml:lang="fr">Vêtements principaux</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kläder/Dräkt</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Main layer of dress, usually exclusive of accessories (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Glavni odevni predmeti</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10219">
    <skos:prefLabel xml:lang="fr">bretelles</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τιράντες</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bandje</skos:prefLabel>
    <skos:altLabel xml:lang="es">Bretel</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10217"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Hängslen</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Bretelle</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Hosenträger</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Tirantes</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Decorative attachments resembling suspenders, with shaped straps which pass over each shoulder, and sometimes connect to a matching waistband. The straps are sometimes connected by horizontal bands across the chest or back. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216763\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216763"/>
    <skos:prefLabel xml:lang="en">bretelle</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bretele</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Bretelle</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Suspensórios</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10273">
    <skos:prefLabel xml:lang="fr">bottes en caoutchouc</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botines de goma</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Botas de borracha</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Galochas</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Gummistövlar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Gumene čizme</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Galoschen</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Overshoes of rubber, especially those without buckles and not extending as high as the ankle. For overshoes of rubber or waterproof fabric that extend above the ankle and often have buckles or other fasteners, use ""galoshes."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">rubbers</skos:prefLabel>
    <skos:prefLabel xml:lang="it">calzature di gomma</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Rubber overschoenen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10269"/>
    <skos:prefLabel xml:lang="de">Gummiüberschuhe</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kaljače</skos:altLabel>
    <skos:prefLabel xml:lang="el">Λαστιχένιες μπότες</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046076\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046076"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10502">
    <skos:prefLabel xml:lang="de">Einladung</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pozivnica</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Uitnodiging</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Invitación</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Invitation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Inbjudan</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10301"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300027083\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300027083"/>
    <skos:prefLabel xml:lang="en">invitation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10229">
    <skos:prefLabel xml:lang="de">Krawatte</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Stropdassen</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210068\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210068"/>
    <skos:prefLabel xml:lang="fr">cravate</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Corbata</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cravatte</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10220"/>
    <skos:altLabel xml:lang="de">Halsbinde</skos:altLabel>
    <skos:prefLabel xml:lang="en">necktie</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γραβάτα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Gravata</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Rather long, narrow lengths of soft material worn about the neck, usually under a collar, tied in front into a knot, loop, or bow and often with the two ends falling free vertically. For neckcloths of fine cloth wound around the neck, usually over the shirt, and tied in front into a bow or knot, use ""cravats."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Kravata</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Slipsar</skos:prefLabel>
    <skos:altLabel xml:lang="de">Binder</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10694">
    <skos:scopeNote xml:lang="en">After the fabric is draped on the dress form artfully with pins it is marked and removed. A flat pattern for the garment is created from the marked fabric (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
    <skos:prefLabel xml:lang="en">Draping</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10499">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300069186\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300069186"/>
    <skos:prefLabel xml:lang="it">Concorso</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">concours</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Contest</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Concurso</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Concurso</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Wettbewerb</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εγκαίνια</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Takmičenje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Wedstrijd</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Tävling</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10695">
    <skos:scopeNote xml:lang="en">Over-dyeing fabric to create an ivory or warm tech or with a charcoal stain to create a pale gray fabric to modify the brightness and reflectivity of a white shirt or costume (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
    <skos:prefLabel xml:lang="en">Teching (dipping costumes)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10516">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Chaparajos</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Čaps</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zahones</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaps</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Benskydd</skos:altLabel>
    <skos:altLabel xml:lang="de">Cowboyhosen</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Chaps</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Chaps</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Chaps</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Chaps</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046141\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046141"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">Lederbeinlinge</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10629">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Ruksak</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Mochila</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Σακίδιο πλάτης</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Ranac</skos:altLabel>
    <skos:prefLabel xml:lang="de">Rucksack</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Ryggsäck</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Backpack</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="fr">sac à dos</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10592">
    <skos:prefLabel xml:lang="es">Hueso</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ben</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="pt">Osso</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Osso</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Been</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Os</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kost</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011798\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011798"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:altLabel xml:lang="de">Knochen</skos:altLabel>
    <skos:prefLabel xml:lang="de">Bein</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bone</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10542">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Beverpels</skos:prefLabel>
    <skos:prefLabel xml:lang="en">beaver fur</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Castor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10539"/>
    <skos:prefLabel xml:lang="sv">Bäverpäls</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bieberpelz</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Krzno dabra</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Fourrure de castor</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10416">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Blanc</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Weiß</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Bianco</skos:prefLabel>
    <skos:prefLabel xml:lang="en">White</skos:prefLabel>
    <skos:altLabel xml:lang="it">Bianco</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300129784\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300129784"/>
    <skos:prefLabel xml:lang="sv">Vit</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="sr">Bela</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Branco</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Blanco</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Wit</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Λευκό</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10201">
    <skos:prefLabel xml:lang="it">Cuffia (rigida)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Haube</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Bonnet</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210720\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210720"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Touca (chapéu)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Bonete</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bone</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft hats usually tied under the chin and having a front brim; formerly worn by women, but now mostly children. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Capotinha</skos:altLabel>
    <skos:prefLabel xml:lang="fr">bonnet</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπονέ</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bonnet (hat)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Muts</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bahytter</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10616">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="en">Cutaway (coat)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10293">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="sv">Förkläden</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Avental (roupa de protecção)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schorten</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tablier</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ποδιά</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schürze (Schutzkleidung)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Delantal</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Camice</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046131\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046131"/>
    <skos:scopeNote xml:lang="en">"Garments worn over main garments for protection and sometimes ornamentation. Usually cover the front of the body and tie at the waist with strings, but may have a bib or shoulder straps. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">apron (protective wear)</skos:prefLabel>
    <skos:altLabel xml:lang="es">Mandil</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Kecelja</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10406">
    <skos:prefLabel xml:lang="nl">Metallic</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Metallic</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Metalizado</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Metalizado</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Metalik</skos:prefLabel>
    <skos:altLabel xml:lang="it">Metallico</skos:altLabel>
    <skos:prefLabel xml:lang="it">Metallico</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">métallique</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Metallfärgad</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μεταλλικό</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300311171\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300311171"/>
    <skos:altLabel xml:lang="nl">Metaalkleur</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="de">Metallic</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10602">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="sr">Farba</skos:altLabel>
    <skos:prefLabel xml:lang="es">Pintura</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Peinture</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Boja</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:prefLabel xml:lang="en">paint</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Lack</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Målarfärg</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Verf</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300015029\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300015029"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10288">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:altLabel xml:lang="de">Kniestrümpfe</skos:altLabel>
    <skos:prefLabel xml:lang="en">stockings</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Damenstrumpf</skos:prefLabel>
    <skos:altLabel xml:lang="de">Strümpfe</skos:altLabel>
    <skos:prefLabel xml:lang="fr">bas</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Collant</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Hosiery that covers the foot and extends to the knee or above; can be heavy or lightweight. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Kousen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Strumpor</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čarape</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Meias-liga</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Kous</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κάλτσες</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046088\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046088"/>
    <skos:prefLabel xml:lang="es">Medias</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10001">
    <skos:prefLabel xml:lang="it">Abiti</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Kleidung</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kostüm</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209261\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209261"/>
    <skos:prefLabel xml:lang="el">Ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The Costume hierarchy contains descriptors for objects worn or carried for warmth, protection, embellishment, or for symbolic purposes. It includes descriptors for garments considered as the main item of dress (e.g.g., ""shirts,"" ""trousers""), descriptors for garments worn under the main garments (e.g., ""undershirts""), and descriptors for garments worn over the main garments (e.g., ""parkas""). Also included are descriptors for protective wear, including types of armor; vestments and other ceremonial garments; uniforms; and an extensive listing of accessories, including those worn on the body (e.g.g., ""headgear,"" ""footwear"") and those carried on the person (e.g.g., ""evening bags,"" ""parasols""). Relation to Other Hierarchies: The descriptors ""pocket watches"" and ""wrist watches,"" along with other timepieces, appear in the Measuring Devices hierarchy. The descriptors ""pocket pistols"" and ""dress swords"" appear with other forms of weapons in the Weapons and Ammunition hierarchy. Descriptors for objects that may be used in the grooming and care of costume or the person (e.g.g., ""clothes brushes,"" ""nail clippers"") appear in the Tools and Equipment hierarchy. Descriptors for objects used to store or transport costume or other personal effects (e.g.g., ""glove boxes,"" ""suitcases"") appear in the containers hierarchy. Constituent parts of costume (e.g.g., ""busks,"" ""waistbands"") appear in the Components hierarchy (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Kleding</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Kledingstukken</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10000"/>
    <skos:prefLabel xml:lang="sv">Dräkt</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Kostim</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Costume</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Costume</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Traje</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10552">
    <skos:prefLabel xml:lang="sr">Gvožđe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Iron</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ferro</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ferro</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Eisen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011002\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011002"/>
    <skos:prefLabel xml:lang="fr">Fer</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hierro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Järn</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="nl">Ijzer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10402">
    <skos:altLabel xml:lang="it">Blu</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Plava</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300129361\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300129361"/>
    <skos:prefLabel xml:lang="en">Blue</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Blauw</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Azul</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Azul</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="sv">Blå</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bleu</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μπλε</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Blu</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Blau</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10582">
    <skos:prefLabel xml:lang="nl">Schelp</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Snäckskal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Coquille</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="en">shell</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Školjka</skos:prefLabel>
    <skos:altLabel xml:lang="en">seashell</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011829\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011829"/>
    <skos:prefLabel xml:lang="de">Muschel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Concha</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10286">
    <skos:altLabel xml:lang="fr">collant</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Sheer stockings woven in one with panties. For heavy, opaque stockings woven in one with panties use ""tights (opaque stockings)."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Καλσόν</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Calzamaglia</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Collants</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Colãs</skos:altLabel>
    <skos:prefLabel xml:lang="en">pantyhose</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Hula hop čarape</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209987\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209987"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:prefLabel xml:lang="de">Feinstrumpfhose</skos:prefLabel>
    <skos:altLabel xml:lang="es">Pantimedias</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Panty's</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Strumpbyxor</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">collant</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Pantis</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10503">
    <skos:prefLabel xml:lang="de">Cruise/ Resort Collection</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αρχειακή συλλογή</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Putna kolekcija</skos:prefLabel>
    <skos:altLabel xml:lang="de">elegante Kreuzfahrtkollektion</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Collection croisière</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Cruise/Resort Collection</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Colección crucero</skos:prefLabel>
    <skos:altLabel xml:lang="de">Collection Croisière</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Cruise/Resort-collectie</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Exklusiv resegarderob</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10272">
    <skos:prefLabel xml:lang="el">Γκέτες</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210047\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210047"/>
    <skos:prefLabel xml:lang="fr">guêtres</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10269"/>
    <skos:prefLabel xml:lang="pt">Polainas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Polainas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gamaschen</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Slobkousen</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Damasker</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kamašne</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">spats</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Short gaiters worn over the instep and usually fastened under the foot with a strap; worn especially in the late 19th and early 20th centuries. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">ghette corte</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10517">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Manila sjaal</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10295"/>
    <skos:altLabel xml:lang="de">Flamenco-Tuch</skos:altLabel>
    <skos:prefLabel xml:lang="de">Manton</skos:prefLabel>
    <skos:altLabel xml:lang="de">spanisches Schultertuch</skos:altLabel>
    <skos:prefLabel xml:lang="fr">châle de Manille</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Mantón de Manila</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Manila shawl</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Manila šal</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sjalar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10543">
    <skos:prefLabel xml:lang="nl">"Materialen voor decoratie, applicatie en technische afwerking"</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10346"/>
    <skos:prefLabel xml:lang="de">"Materialien zur  Dekoration, Besatz und technischen Ausrüstung"</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">"Materiales para decoración, aplicación y acabados técnicos"</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">"Materiau pour pour la décoration, l'application et les finitions"</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">"Materijali za ukrašavanje, aplikaciju i tehničku doradu"</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">"Materials for decoration, application and technical finishing"</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Material för dekoration och applikation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10696">
    <skos:prefLabel xml:lang="sr">Mašinsko pletenje</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tejido a máquina</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πλεκτό μηχανής</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">maschinengestrickt</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Tricoté à la machine</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10389"/>
    <skos:prefLabel xml:lang="sv">Maskinstickat</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Machine knitted</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10202">
    <skos:prefLabel xml:lang="sv">Klockhattar</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κλος</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kloš šešir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Cloche</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210733\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210733"/>
    <skos:altLabel xml:lang="es">Sombrero de campana</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="nl">Pothoeden</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">"Cloche (chapéu em forma de sino, anos 20)"</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chapeau cloche</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cloche (hat)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cappello a Cloches</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrero cloche</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Hats with a bell-shaped crown. (AAT)</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10601">
    <skos:altLabel xml:lang="es">Nitrato de celulosa</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Acetaat</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Acetat</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Acetat</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Acetat</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Acetato</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Acétate</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300255863\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300255863"/>
    <skos:prefLabel xml:lang="en">acetate</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10591">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011857\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011857"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Slonovača</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="en">Ivory</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Ivoire</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Avorio</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Elfenbein</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Marfil</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ivoor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Elfenben</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10415">
    <skos:prefLabel xml:lang="es">Transparente</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="sv">Transparent</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Transparent</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Transparent</skos:prefLabel>
    <skos:altLabel xml:lang="it">Trasparente</skos:altLabel>
    <skos:prefLabel xml:lang="el">Διάφανο</skos:prefLabel>
    <skos:prefLabel xml:lang="de">durchsichtig</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Prozirna</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:altLabel xml:lang="de">transparent</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Transparant</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Trasparente</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Transparente</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10292">
    <skos:prefLabel xml:lang="it">Amuleti</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">amulette</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Amulett</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Amuletten</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="en">amulet</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300266585\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300266585"/>
    <skos:prefLabel xml:lang="pt">Amuleto</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Amuleto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Φυλαχτό</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Amulet</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Small objects worn as protecting charms, as to ward off evil, harm, or illness, or to bring good fortune. For objects specifically cut with astrological or magical symbols, intended to protect the bearer, but not necessarily worn, use ""talismans."" Small objects believed to posess magic powers and worn as a good-luck charm or as jewelry. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Amuletter</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10615">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="en">Gambeson</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10000">
    <skos:prefLabel xml:lang="it">Accessori moda</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Objectos de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Fashion Objects</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modni predmeti</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modeobjecten</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αντικείμενα μόδας</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Modeobjekt</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Objetos de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modeobjekte</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Objets de mode</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10401">
    <skos:prefLabel xml:lang="de">Schwarz</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Nero</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Preto</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Negro</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zwart</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="fr">Noir</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300130920\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300130920"/>
    <skos:prefLabel xml:lang="en">Black</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Svart</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="it">Nero</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μαύρο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Crna</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10405">
    <skos:prefLabel xml:lang="es">Gris</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Gris</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Grijs</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Grigio</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Siva</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γκρι</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Grey</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Grau</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300130811\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300130811"/>
    <skos:altLabel xml:lang="it">Grigio</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Cinzento</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="sv">Grå</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10581">
    <skos:prefLabel xml:lang="sv">Animaliska material</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Matériaux d'origine animale</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Materiales de origen animal</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Materijali životinjskog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Materialen van dierlijke origine</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Animal origin materials</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10543"/>
    <skos:prefLabel xml:lang="de">Werkstoffe tierischen Ursprungs</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10553">
    <skos:prefLabel xml:lang="es">Plomo</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Olovo</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Chumbo</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Piombo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="en">lead</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Plomb</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Blei</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bly</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lood</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011022\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011022"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10599">
    <skos:prefLabel xml:lang="nl">Silicone</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Silicone</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Silikon</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Silikon</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Silikon</skos:prefLabel>
    <skos:prefLabel xml:lang="en">silicone</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:prefLabel xml:lang="es">Silicona</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014558\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014558"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10496">
    <skos:prefLabel xml:lang="el">Ντένιμ</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Denim</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Denim</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Denim</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Denim</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Denim</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10380"/>
    <skos:prefLabel xml:lang="en">Denim</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Jeans</skos:altLabel>
    <skos:prefLabel xml:lang="es">Denim</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ganga</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:altLabel xml:lang="de">Jeansstoff</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014068\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014068"/>
    <skos:altLabel xml:lang="el">Τζην</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10271">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10269"/>
    <skos:prefLabel xml:lang="en">pattens</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Steltschoen</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210042\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210042"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Escarpines</skos:prefLabel>
    <skos:prefLabel xml:lang="it">pattine</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Träskor</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Plataforma para protecção de sapatos</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Overshoes consisting of a sole, usually set on bands of iron, often ring-shaped, and fastened over footwear by tie straps, worn to elevate the foot for protection from mud, dirt, or wetness. For footwear with a thick sole usually of wood, but sometimes of leather or cork, use ""clogs."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Patten</skos:prefLabel>
    <skos:altLabel xml:lang="fr">socques</skos:altLabel>
    <skos:prefLabel xml:lang="el">Τσόκαρα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">patin de promenade</skos:prefLabel>
    <skos:altLabel xml:lang="es">Chanclos</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Natikače</skos:prefLabel>
    <skos:altLabel xml:lang="de">Trippen</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Trippen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10428">
    <skos:scopeNote xml:lang="en">"Refers to the process in which a needle and thread or fine wire are used to stitch decorative designs into cloth, leather, paper, or other material. It may also refer to the process used to create machine-made imitations of hand-made embroidery. For the weft patterning technique of weaving raised patterns on a woven textile, use ""brocading."" (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Vez</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Broderier</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Embroidery</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10427"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="es">Bordados</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Bordado</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053653\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053653"/>
    <skos:prefLabel xml:lang="el">Κέντημα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stickerei</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">brodé</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Broderie</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Borduursel</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Borduurwerk</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10227">
    <skos:altLabel xml:lang="de">Pelzstola</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Bontkraagjes</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Acessórios de pescoço</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ λαιμού</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ukrasi za vrat</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">écharpe</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorio para el cuello</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Halssmycke</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schal</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210071\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210071"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10220"/>
    <skos:prefLabel xml:lang="en">neckpiece</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Bontje</skos:altLabel>
    <skos:altLabel xml:lang="sv">Halsdekor</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Ornamenti da collo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Ornamental pieces, often fur, which are worn at the neck; frequently trimmed with heads and tails of the fur-bearing animal. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10504">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Leer</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cuir</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cuero</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Leder</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Koža</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Läder</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011845\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011845"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Leder</skos:prefLabel>
    <skos:altLabel xml:lang="sv">skinn</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Leather</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10564">
    <skos:altLabel xml:lang="nl">Pleister</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="fr">Plâtre</skos:prefLabel>
    <skos:prefLabel xml:lang="en">plaster</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Plaaster</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Escayola</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Gips</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Gips</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gips</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014922\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014922"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10004">
    <skos:altLabel xml:lang="fr">Robe  indienne</skos:altLabel>
    <skos:altLabel xml:lang="en">Indian gown</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Banyan</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Banyan</skos:prefLabel>
    <skos:altLabel xml:lang="it">vestaglia indiana</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="pt">Veste Indiana</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Men's loose-skirted wraps worn informally from the 17th to early 19th century; so called from their resemblance to similar garments worn by Banyans, a caste of Hindu merchants (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Banyán</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215822\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215822"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Banjan</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Japonse rok</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10281">
    <skos:prefLabel xml:lang="nl">Ginnen</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εκκόκκισμα (Ανεμοδούρα)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">égrenage</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Entkörnung</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Descaroçamento</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Mašinska obrada pamuka</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Desmotado</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="en">ginning</skos:prefLabel>
    <skos:altLabel xml:lang="sv">rensa (förbearbetning till spinning)</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Häckla</skos:prefLabel>
    <skos:altLabel xml:lang="sv">förkarda</skos:altLabel>
    <skos:altLabel xml:lang="sv">sortera</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10280"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10585">
    <skos:prefLabel xml:lang="nl">Balein</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Ballena</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fanon</skos:prefLabel>
    <skos:altLabel xml:lang="de">Barte</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Valbard</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">Whalebone</skos:altLabel>
    <skos:prefLabel xml:lang="en">Baleen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="sr">Usi</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fischbein</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300192974\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300192974"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10578">
    <skos:prefLabel xml:lang="es">Bambú</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Bamboe</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="sr">Bambus</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bambus</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="en">bamboo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bambu</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bambou</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011873\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011873"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10213">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300036794\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300036794"/>
    <skos:prefLabel xml:lang="de">Helm</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Any of various forms of protective coverings for the head, usually made of a hard material. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">helmet</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Caschi o caschetti</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Casco</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hjälmar</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Capacete</skos:prefLabel>
    <skos:altLabel xml:lang="sv">hjälm</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Kaciga</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="fr">casque</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κράνος</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Helmen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10070">
    <skos:prefLabel xml:lang="sv">Badrock</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ρόμπα μπάνιου</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209945\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209945"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Bade-mantil</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Loose-fitting knee-length or ankle-length garments, often tied with a belt, usually of a warm absorbent material, worn before and after bathing or informally around the house. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Badjas</skos:prefLabel>
    <skos:altLabel xml:lang="sv">badrock</skos:altLabel>
    <skos:prefLabel xml:lang="fr">peignoir</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Albornoz</skos:prefLabel>
    <skos:altLabel xml:lang="el">Μπουρνούζι</skos:altLabel>
    <skos:prefLabel xml:lang="en">Bathrobe</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bademantel</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accappatoio</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupão de banho</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10584">
    <skos:prefLabel xml:lang="sr">Korali</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Koraal</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Coral</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="sv">Korall</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Koralle</skos:prefLabel>
    <skos:prefLabel xml:lang="en">coral</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011800\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011800"/>
    <skos:prefLabel xml:lang="fr">Corail</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10084">
    <skos:prefLabel xml:lang="en">Chemise</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210542\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210542"/>
    <skos:altLabel xml:lang="en">Shift</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Loose-fitting, straight-hanging shirtlike underwear with or without sleeves, usually extending to the hip or knee. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Camisa interior</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hemd</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Media combinación</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Hemd</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Onderhemd</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">combinaison-culotte</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πουκάμισο μακρύ</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sottoveste</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="sv">Linne</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dugačka košulja</skos:prefLabel>
    <skos:altLabel xml:lang="it">Sottana</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10099">
    <skos:prefLabel xml:lang="sr">Muške gaće</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzoncillos</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Onderbroek</skos:prefLabel>
    <skos:altLabel xml:lang="es">Calzón interior</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:altLabel xml:lang="fr">caleçon</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Underbyxor</skos:prefLabel>
    <skos:altLabel xml:lang="fr">culotte</skos:altLabel>
    <skos:prefLabel xml:lang="it">Mutande da uomo</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Mamelucker</skos:altLabel>
    <skos:prefLabel xml:lang="el">Βρακί</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cueca calção</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300220740\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300220740"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Unterhosen</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Underpants</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">Garments worn next to the body and under main garments having leg openings or short or long legs. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">slip</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10514">
    <skos:prefLabel xml:lang="sv">Ordensband</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">ceinture-écharpe</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Faja</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10235"/>
    <skos:altLabel xml:lang="es">Ceñidor</skos:altLabel>
    <skos:prefLabel xml:lang="de">Schärpe</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216864\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216864"/>
    <skos:prefLabel xml:lang="nl">Sjerp</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sash</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10610">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Braies</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Braies</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10036"/>
    <skos:prefLabel xml:lang="fr">Braie</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βράκα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Bruch</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10558">
    <skos:prefLabel xml:lang="nl">Spiegel</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Spiegel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">mirror</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Ogledalo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="es">Espejo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Spegelgals</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300037682\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300037682"/>
    <skos:prefLabel xml:lang="fr">Miroir</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10682">
    <skos:prefLabel xml:lang="sr">Vinil</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vinilo</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βινύλιο</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Vinyle</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:altLabel xml:lang="de">PVC</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Vinyl</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Vinyl</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vinyl</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10408">
    <skos:prefLabel xml:lang="de">Silber</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Srebrna</skos:prefLabel>
    <skos:altLabel xml:lang="de">silbern</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Zilver</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Ασημί</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Plata</skos:prefLabel>
    <skos:altLabel xml:lang="it">Argento</skos:altLabel>
    <skos:prefLabel xml:lang="it">Argento</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Silver</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Argent</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Silver</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10406"/>
    <skos:prefLabel xml:lang="pt">Prateado</skos:prefLabel>
    <skos:altLabel xml:lang="fr">argenté</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10600">
    <skos:prefLabel xml:lang="es">Gomaespuma</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Caoutchouc mousse</skos:prefLabel>
    <skos:prefLabel xml:lang="en">foam rubber</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014562\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014562"/>
    <skos:altLabel xml:lang="nl">Polyether</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Schuimrubber</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Moosgummi</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:prefLabel xml:lang="sr">Poliuretanska pena</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Polyetenskum</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10544">
    <skos:prefLabel xml:lang="sr">Materijali mineralnog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Werkstoffe mineralischen Ursprungs</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Materiau d'origine minerale</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Materialen van minerale origine</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10543"/>
    <skos:prefLabel xml:lang="en">Mineral origin materials</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10524">
    <skos:prefLabel xml:lang="en">Non-fibre textile materials</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">"nicht faserhaltige, textile Werkstoffe"</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Niet-vezelige textielmaterialen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Materiales textiles</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Matériaux textile non-fibreux</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ej textila material</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10346"/>
    <skos:prefLabel xml:lang="sr">Nevlaknasti tekstilni materijali</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10207">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210798\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210798"/>
    <skos:prefLabel xml:lang="pt">Sombrero</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Sombrero</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Sombrero</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sombrero</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Sombrero's</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="pt">Chapéu mexicano</skos:altLabel>
    <skos:scopeNote xml:lang="en">"High-crowned hats made of felt or straw with a very wide brim usually rolled at the edges, worn especially in the American Southwest and Mexico. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Sombrero cordobés</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sombreros</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sombrero</skos:prefLabel>
    <skos:prefLabel xml:lang="en">sombrero</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σομπρέρο</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10497">
    <skos:prefLabel xml:lang="sr">Video instalacija</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Videoinstallation</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Videoinstallation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Video-installatie</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Instalación de video</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Video-installazione</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Video installation</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βίντεο</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Instalação de vídeo</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10302"/>
    <skos:prefLabel xml:lang="fr">Installation video</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10598">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Celuloid</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014447\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014447"/>
    <skos:altLabel xml:lang="de">Zellhorn</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10597"/>
    <skos:prefLabel xml:lang="sv">Celluloid</skos:prefLabel>
    <skos:prefLabel xml:lang="en">celluloid</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Celuloide</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Celluloid</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zelluloid</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Celluloïd</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Celluloide</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10299">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Κούκλα ραπτικής</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mannequin</skos:prefLabel>
    <skos:prefLabel xml:lang="en">mannequin</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Three-dimensional representations of human figures, often stylized or otherwise abstracted, used by tailors, dressmakers, and sales personnel for fitting or displaying clothing. For models of the human body used for teaching anatomy or demonstrating surgical operations, use ""manikins."" For jointed figure of humans or animals used by artists, use ""lay figures."" (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Manequim</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300239006\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300239006"/>
    <skos:prefLabel xml:lang="de">Schaufensterpuppe</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Krojačka lutka</skos:prefLabel>
    <skos:prefLabel xml:lang="it">manichino</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mannequin</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Maniquí (equipo para vestuario)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Paspop</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Skyltdocka</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10270">
    <skos:prefLabel xml:lang="es">Botas de agua</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Galoschen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10269"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300204798\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300204798"/>
    <skos:scopeNote xml:lang="en">"Overshoes of rubber or other waterproof fabric, especially those that extend above the ankle and that often have buckles or other fasteners. Use ""rubbers"" for overshoes without buckles or fasteners that are cut below the ankle. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Botas de borracha</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Galoshes</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Galochas</skos:prefLabel>
    <skos:altLabel xml:lang="es">Galochas</skos:altLabel>
    <skos:prefLabel xml:lang="fr">galoche</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Galoscher</skos:prefLabel>
    <skos:prefLabel xml:lang="en">galoshes</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Kaljače</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γαλότσες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">galosce</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10228">
    <skos:prefLabel xml:lang="pt">Boa (peças de pescoço)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">boa</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215870\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215870"/>
    <skos:prefLabel xml:lang="nl">Boa's</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10227"/>
    <skos:prefLabel xml:lang="sr">Boa</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Boa</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Boa</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Boa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Boa</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Very long, elliptical neckpieces made of feathers, fur, or similar fluffy materials, especially popular in the 1890s. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">boa (neckpieces)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μποά</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10565">
    <skos:prefLabel xml:lang="nl">Keramiek</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Keramika</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="es">Cerámica</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Céramique</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300235507\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300235507"/>
    <skos:prefLabel xml:lang="en">Ceramics</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Keramik</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Keramik</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10505">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209850\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209850"/>
    <skos:prefLabel xml:lang="en">Raincoat</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gabardina</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kišni mantil</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Regnkappa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Regenjas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Imperméable</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Kabanica</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Regenmantel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="es">"Chubasquero, impermeable"</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10003">
    <skos:prefLabel xml:lang="es">Principales prendas de cuerpo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">wichtigste Kleidungsstücke</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Principali capi di vestiario per il corpo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kläder/Dräkt</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Vêtements principaux corps</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Vestuário principal do corpo</skos:prefLabel>
    <skos:altLabel xml:lang="de">Hauptkleidungsstücke</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10002"/>
    <skos:prefLabel xml:lang="sr">Glavni odevni predmeti – telo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Vêtements portés sur le corps</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Main garments body</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Βασικά ενδύματα σώματος</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bovenkleding voor het hele lichaam</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10684">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:prefLabel xml:lang="en">Reptile skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10214">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Capuchon</skos:altLabel>
    <skos:prefLabel xml:lang="it">Cappucci</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Capuz</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046121\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046121"/>
    <skos:prefLabel xml:lang="el">Κουκούλα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kapuze (Kopfbedeckung)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Capucha</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="es">Capucho</skos:altLabel>
    <skos:prefLabel xml:lang="fr">capuche</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft coverings for the head and neck, and sometimes extending to the shoulders, either separate or attached to a garment. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Kapuljača</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hood (headgear)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Huvor</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kappen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10579">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011860\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011860"/>
    <skos:prefLabel xml:lang="fr">Écorce</skos:prefLabel>
    <skos:altLabel xml:lang="de">Baumrinde</skos:altLabel>
    <skos:prefLabel xml:lang="de">Borke</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Corteza</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="nl">Schors</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bark</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Kora</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bark</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10280">
    <skos:prefLabel xml:lang="fr">transformations mécaniques</skos:prefLabel>
    <skos:prefLabel xml:lang="en">mechanical transformations</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Transformaciones mecánicas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Mechanische bewerkingen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Mechanische Veränderungen</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mechanische transformatie</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Mehaničke transformacije</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Transformações mecânicas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μηχανική σχηματοποίηση</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Mekaniska tekniker</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10366"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10427">
    <skos:prefLabel xml:lang="es">Aplicaciones</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="sv">Applikationer</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εφαρμογές</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aplikacije</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Applikation</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">damassé</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Applicaties</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10398"/>
    <skos:prefLabel xml:lang="en">Applications</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Aplicações</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10098">
    <skos:prefLabel xml:lang="el">Μισοφόρι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Enagua de cuerpo entero</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupa interior</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Donja haljina</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:scopeNote xml:lang="en">Dresses worn beneath another; use especially for those underdresses designed to be seen. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">fond de robe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Underdress</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Underklänning</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Onderjurk</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210572\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210572"/>
    <skos:prefLabel xml:lang="de">Unterkleid</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sottoveste</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10085">
    <skos:prefLabel xml:lang="sv">Kombination</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kombination</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210544\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210544"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="fr">combinaison</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Combinação</skos:prefLabel>
    <skos:altLabel xml:lang="de">Combinaison</skos:altLabel>
    <skos:altLabel xml:lang="nl">Combinatie</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Unterciger</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Combinaison</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Combinazione: sottoveste tuta</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"One-piece garments combining an upper body covering, with or without sleeves, with a bifurcated lower body covering. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Κομπινεζόν</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Combinación</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Combination</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10208">
    <skos:prefLabel xml:lang="sv">Huvuddukar</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kopfschutz</skos:altLabel>
    <skos:prefLabel xml:lang="de">Kopftuch</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fichu (coiffure)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Complementos de cabeza (tocados)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">foulard (coiffure)</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κάλυμμα κεφαλιού</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hoofddoeken</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300213003\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300213003"/>
    <skos:scopeNote xml:lang="en">Term generally applied to various cloth coverings for the head. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Pokrivači za glavu</skos:prefLabel>
    <skos:prefLabel xml:lang="en">headcloth (headgear)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pano para cobrir a cabeça</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Véu</skos:altLabel>
    <skos:altLabel xml:lang="de">Kopfbedeckung</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Copricapi in stoffa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10515">
    <skos:altLabel xml:lang="de">Abarcas</skos:altLabel>
    <skos:prefLabel xml:lang="en">Abarca</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Avarcas</skos:prefLabel>
    <skos:altLabel xml:lang="de">Menorquinas</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Sandaler</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:prefLabel xml:lang="es">Albarcas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Avarke</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">avarca</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Avarca</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10583">
    <skos:prefLabel xml:lang="sv">Sköldpadd</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="es">Carey</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Ecaille de tortue</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">turtle</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schildkröte</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kornjačevina</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schilpad(schaal)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10683">
    <skos:scopeNote xml:lang="en">Nondescript or neutral fabric or clothing that is meant to disappear (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10520"/>
    <skos:prefLabel xml:lang="en">ND (Nondescript)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10559">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="sr">Guma</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Gummi</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gummi</skos:prefLabel>
    <skos:prefLabel xml:lang="en">rubber</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Goma</skos:prefLabel>
    <skos:altLabel xml:lang="es">Caucho</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Caoutchouc</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Rubber</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300012941\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300012941"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10545">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010900\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010900"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="en">metal</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Metal</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Metal</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Métal</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Metall</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Metall</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Metaal</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10407">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10406"/>
    <skos:prefLabel xml:lang="sr">Zlatna</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Guld</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gold</skos:prefLabel>
    <skos:altLabel xml:lang="fr">doré</skos:altLabel>
    <skos:prefLabel xml:lang="en">Gold</skos:prefLabel>
    <skos:altLabel xml:lang="de">golden</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Or</skos:prefLabel>
    <skos:altLabel xml:lang="it">Oro</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Dourado</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Oro</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300311191\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300311191"/>
    <skos:prefLabel xml:lang="es">Oro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="nl">Goud</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χρυσαφί</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10298">
    <skos:prefLabel xml:lang="it">cappelliera</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrerera</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Caixa de chapéu</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hattaskar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Hutschachtel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">boîte à chapeau</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kutija za šešire</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hoedendoos</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:prefLabel xml:lang="el">Καπελιέρα</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">hatbox</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Boxes intended primarily for holding hats while in storage. Also, pieces of hand luggage that are usually round and deep, have handles, and are designed especially for carrying hats, though are often used as traveling bags by women. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300198927\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300198927"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10498">
    <skos:prefLabel xml:lang="es">Proyección</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Projektion</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Projektion</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="nl">projectie</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Projekcija</skos:prefLabel>
    <skos:prefLabel xml:lang="it">proiezione</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Προβολή</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">projection</skos:prefLabel>
    <skos:prefLabel xml:lang="en">projection</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Projecção</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10071">
    <skos:scopeNote xml:lang="en">Waist-length jackets or capes worn in bed. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="es">salto de cama</skos:altLabel>
    <skos:prefLabel xml:lang="es">Mañanita</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:altLabel xml:lang="de">Morgenrock</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Casaquinho de dormir</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kućna jakna</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bettjacke</skos:prefLabel>
    <skos:altLabel xml:lang="de">Negligé</skos:altLabel>
    <skos:altLabel xml:lang="sr">Šlafrok</skos:altLabel>
    <skos:altLabel xml:lang="de">Schlafrock</skos:altLabel>
    <skos:altLabel xml:lang="es">Matinée</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Bedjacket</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Nattkofta</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bedjasje</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Liseuse</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Liseuse</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Λιζέζ</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209946\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209946"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10525">
    <skos:prefLabel xml:lang="es">Materiales textiles de origen mineral</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">"Material av mineralhärkomst, ej textila material "</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Niet-vezelige textielmaterialen van minerale oorsprong</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nevlaknasti tekstilni materijali mineralnog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Materiau textile non-fibreux d'origine minérale</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Mineral origin non-fibre textile materials</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10524"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">"nicht faserhaltige, textile Werkstoffe mineralischen Ursprungs"</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10566">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010662\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010662"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Porcelana</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Porslin</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Porzellan</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Porcelaine</skos:prefLabel>
    <skos:prefLabel xml:lang="en">porcelain</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Porselein</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10565"/>
    <skos:prefLabel xml:lang="sr">Porcelan</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10690">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10539"/>
    <skos:prefLabel xml:lang="en">Mink fur</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10225">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Gravata</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">foulard pour homme</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kravata</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kravatt</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210059\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210059"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10223"/>
    <skos:prefLabel xml:lang="el">Κραβάτ</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cravatte</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Plastron kravata</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Formal neckcloths consisting of long strips of fine cloth wound around the neck and tied in front into a bow or knot. Ends may also tuck inside a coat. Worn especially from the end of the 17th century through the 19th century. For long, narrow lengths of cloth worn around the neck and usually under a collar, tied in a knot, loop, or bow, and often with two ends falling free vertically, use ""neckties."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Corbatín</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">cravat</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Cravatten</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Krawatte</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10494">
    <skos:prefLabel xml:lang="el">Διαγώνιο</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kruiskepers</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kreuzköper</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sarja cruzada</skos:prefLabel>
    <skos:altLabel xml:lang="el">Diagonal</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Cross twill</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sarga cruzada</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10380"/>
    <skos:prefLabel xml:lang="sv">Korskypert</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="sr">Ukršteni keper</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">sergé croisé</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10597">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Plastique</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Plastica</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Plastika</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Plástico</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Plastik</skos:prefLabel>
    <skos:altLabel xml:lang="es">Pasta</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014570\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014570"/>
    <skos:prefLabel xml:lang="en">plastic</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Plast</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Plastic</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10587">
    <skos:prefLabel xml:lang="en">mother of pearl</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Nácar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Sedef</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="sv">Pärlemor</skos:prefLabel>
    <skos:altLabel xml:lang="es">Madreperla</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Paarlemoer</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Perlmutt</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Nacre</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011835\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011835"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10006">
    <skos:prefLabel xml:lang="es">Conjunto (vestimenta)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sr">Kostim</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Coordenado</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Completo</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σύνολο</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Dräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Ensemble</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ensemble</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Conjunto</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Komplet</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10170">
    <skos:altLabel xml:lang="fr">broche</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Pins</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sierspelden</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10167"/>
    <skos:prefLabel xml:lang="pt">Alfinete (joalharia)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Alfiler (joya)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">pin (jewelry)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καρφίτσα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sv">Bröstnålar</skos:altLabel>
    <skos:prefLabel xml:lang="it">Spille</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">épingle</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Nadel (Schmuck)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Ornaments consisting essentially or partly of a pointed penetrating wire or shaft. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Igla</skos:prefLabel>
    <skos:altLabel xml:lang="de">Anstecknadel</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046006\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046006"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10283">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">hosiery</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bonneterie</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Meias</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Strumpfwaren</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Calzetteria</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kousen en sokken</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calcetería</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Strumpor</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čarape</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
    <skos:scopeNote xml:lang="en">"Leg coverings, sometimes woven in one with panties, that extend to the ankle and usually cover the foot; generally knitted or woven, sheer or opaque, and of lightweight or heavy fabric. Distinguish from ""footwear"", which is primarily worn as coverings for the feet."</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Κάλτσες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209984\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209984"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10019">
    <skos:altLabel xml:lang="es">Justillo</skos:altLabel>
    <skos:prefLabel xml:lang="it">corsetto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Originally, tight fitting, sleeveless garments for the torso and bust, quilted and strengthened with whalebone, worn chiefly by women, but also by men. Bodices were typically cross-laced at the side or back, and worn over a blouse or chemise. The term comes from ""a pair of bodies,"" the use of the plural referring to the fact that the garment was made in two pieces laced together. The term also now refers to the generally tight-fitting upper part of a woman's dress or to any tight-fitting outer vest or waistcoat (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="it">vitina</skos:altLabel>
    <skos:prefLabel xml:lang="es">Corpiño</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Corsage</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Corpete</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μπούστος</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209874\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209874"/>
    <skos:prefLabel xml:lang="en">Bodice</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lijfje</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Keurslijfje</skos:altLabel>
    <skos:altLabel xml:lang="el">Τζάκος</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Liv</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Gornji deo haljine</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="de">Mieder</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10576">
    <skos:prefLabel xml:lang="de">Seidenpapier</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10574"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014145\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014145"/>
    <skos:prefLabel xml:lang="fr">Papier de soie</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">Papier mousseline</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Toaletni papir</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zijdepapier</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">tissue paper</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Silkepapper</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Papel de seda</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10215">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="pt">Mitra</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Mitra</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Mitra</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Mitra</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μίτρα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mitror</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Mijters</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212995\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212995"/>
    <skos:altLabel xml:lang="nl">Mijter</skos:altLabel>
    <skos:prefLabel xml:lang="en">miter</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">mitre</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Term applied to several types of tall pieces of headgear, such as headbands worn by women of ancient Greece, the turbanlike official headdresses of ancient Jewish high priests, or the liturgical headdresses worn by bishops and abbots. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Mitria</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10506">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="sv">Cape</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300197421\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300197421"/>
    <skos:prefLabel xml:lang="sr">Ćebe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Omslagdeken</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Couverture</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Blanket</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Decke</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Manta</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10680">
    <skos:prefLabel xml:lang="es">Papel de aluminio</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Foil paper</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Foil paper</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Foliepapper</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Folija</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Papierfolie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Papier métallisé</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10612">
    <skos:prefLabel xml:lang="en">Tailcoat</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10042"/>
    <skos:prefLabel xml:lang="fr">Queue de pie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Frack</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Frack</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φράκο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Chaqueta de frac</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10556">
    <skos:prefLabel xml:lang="sr">Bronza</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010957\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010957"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="en">bronze</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Bronze</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bronze</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Brons</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Brons</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bronce</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10082">
    <skos:prefLabel xml:lang="nl">Tournure</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sellino: sottogonna posteriore</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Pads or frames worn at or below the waist in the back to distend the garment backward at the hips. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="fr">tournure</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Turnir</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Τουρνούρι</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Turnyr</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Turnüre</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Tournure (armação para dar volume posterior)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Polisón</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Bustle</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210580\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210580"/>
    <skos:prefLabel xml:lang="fr">bustier</skos:prefLabel>
    <skos:altLabel xml:lang="de">Crinolette</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Tournure</skos:altLabel>
    <skos:altLabel xml:lang="de">Gesäßpolster</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10512">
    <skos:prefLabel xml:lang="es">Ramo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Boeket</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Bouquet</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Buket</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Buketter</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">bouquet</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bouquet</skos:prefLabel>
    <skos:altLabel xml:lang="de">Blumenstrauß</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10526">
    <skos:prefLabel xml:lang="nl">Niet-vezelige textielmaterialen van plantaardige oorsprong</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Materiau textile non-fibreux d'origine végetale</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">"Material av vegetabilisk härkomst, ej textila material"</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Materiales textiles de origen vegetal</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Vegetable origin non-fibre textile materials</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10524"/>
    <skos:prefLabel xml:lang="de">"nicht faserhaltige, textile Werkstoffe pflanzlichen Ursprungs"</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nevlaknasti tekstilni materijali biljnog porekla</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10205">
    <skos:prefLabel xml:lang="fr">casque</skos:prefLabel>
    <skos:prefLabel xml:lang="en">helmet</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="nl">Helmen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Casco</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300036794\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300036794"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Caschi o caschetti</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kaciga</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Helm</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Capacete</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κράνος</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hjälmar</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Any of various forms of protective coverings for the head, usually made of a hard material. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10072">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Loose, informal garments worn when partly or fully undressed, generally warm and often full-length with a rope belt. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Kućna haljina</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupão matinal (usado antes de vestir)</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Kamerjas</skos:altLabel>
    <skos:altLabel xml:lang="pt">Penteador</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Morgonrock</skos:prefLabel>
    <skos:altLabel xml:lang="fr">déshabillé</skos:altLabel>
    <skos:altLabel xml:lang="it">Vestaglietta</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ρόμπα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bata</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Robe de chambre</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Peignoir</skos:prefLabel>
    <skos:altLabel xml:lang="de">Pudermantel</skos:altLabel>
    <skos:prefLabel xml:lang="it">Negligee</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Dressing gown</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Douillette</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Ochtendjas</skos:altLabel>
    <skos:altLabel xml:lang="es">Traje de casa</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209947\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209947"/>
    <skos:prefLabel xml:lang="de">Hausmantel</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10297">
    <skos:prefLabel xml:lang="sv">Kontextuella objekt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kontextuelle Objekte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kontekstualni predmeti</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">objets contextuels</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Objetos contextuales</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Συναφή αντικείμενα</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Kostuumattributen</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Contextual objects</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Objectos contextuais</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Contextuele objecten</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10000"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10670">
    <skos:prefLabel xml:lang="it">Mostra</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Exhibition/</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/Colours">
    <skos:prefLabel xml:lang="en">Colours</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10546">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Or</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Guld</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gold</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Gold</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Oro</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Oro</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Goud</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ouro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011021\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011021"/>
    <skos:prefLabel xml:lang="sr">Zlato</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10226">
    <skos:altLabel xml:lang="sv">Scarves</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Scoutingdas</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300214627\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300214627"/>
    <skos:prefLabel xml:lang="el">Μαντίλι λαιμού</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Fazzoletto da collo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Halstuch</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mouchoir de cou</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Marama za vrat</skos:prefLabel>
    <skos:altLabel xml:lang="fr">tour de cou</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Any square or strip of linen or other material folded around the neck, often worn as part of a uniform. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Halsduk</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Lenço de pescoço</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Pañuelo de cuello</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Kravatt</skos:altLabel>
    <skos:prefLabel xml:lang="en">neckerchief</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10223"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10596">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Matériaux d'origine chimique</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">chemical origin materials</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kemiska material</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10543"/>
    <skos:prefLabel xml:lang="sr">Materijali hemijskog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Materiales de origen químico</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Materialen van chemische origine</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">anorganische Werkstoffe</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10567">
    <skos:prefLabel xml:lang="sv">Stärkelse</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300012959\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300012959"/>
    <skos:prefLabel xml:lang="fr">Amidon</skos:prefLabel>
    <skos:prefLabel xml:lang="en">starch</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Skrob</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stärke</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Amido</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Amido</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Almidón</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Stijfsel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10429">
    <skos:prefLabel xml:lang="es">Lentejuelas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10427"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">paillettes</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Šljokice</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pailletten</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pailletten</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300183896\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300183896"/>
    <skos:prefLabel xml:lang="el">Πούλιες</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Paljetter</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sequins</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Lantejoulas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10495">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Kypertvariation</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sarja invertida</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schussköper</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schroefkepers</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sergé inversé</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αντίστροφα διαγώνιο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="en">Inverted twill</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sarga invertida</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Keper</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10380"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10691">
    <skos:scopeNote xml:lang="en">Applied to the soles of actor’s shoes to prevent slipping and sliding with the additional advantage of silencing foot falls (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10596"/>
    <skos:prefLabel xml:lang="en">Dance rubber</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10282">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Περικνημίδες</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Damasker</skos:altLabel>
    <skos:prefLabel xml:lang="en">gaiters</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">guêtres</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kamašne</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Caneleiras</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Gamasche</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Snölås</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Beenkap</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210027\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210027"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Beenbeschermers</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Polainas</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ghette</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Coverings of cloth, leather, or similar material worn over the ankle and sometimes lower leg. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10586">
    <skos:prefLabel xml:lang="nl">Was</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cera</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cera</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Vosak</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="de">Wachs</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014585\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014585"/>
    <skos:prefLabel xml:lang="sv">Vax</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cire</skos:prefLabel>
    <skos:prefLabel xml:lang="en">wax</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10439">
    <skos:altLabel xml:lang="el">Πενιουάρ</skos:altLabel>
    <skos:prefLabel xml:lang="el">Νεγκλιζέ</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Negligee</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">"Straßen-, Reise- und Hauskleidung des Adels im 18. Jahrhundert"</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Negligé</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Negligé</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Negligé</skos:prefLabel>
    <skos:altLabel xml:lang="de">Negligée</skos:altLabel>
    <skos:prefLabel xml:lang="es">Negligée</skos:prefLabel>
    <skos:prefLabel xml:lang="en">negligee</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Negliže</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">négligé</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10507">
    <skos:prefLabel xml:lang="en">Short coat</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Jacka</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="sr">Kratki kaput</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Kurzmantel</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Blouson</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chaquetón</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Korte jas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10216">
    <skos:prefLabel xml:lang="it">Turbanti</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Turbante</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Turbante</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τουρμπάνι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Wrong</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Tulbanden</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Headdresses consisting of a tight-fitting cap around which is wound a long cloth, originating in eastern Mediterranean and southern Asian countries. Also, headgear of similar appearance which may be constructed differently. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="fr">turban</skos:prefLabel>
    <skos:prefLabel xml:lang="en">turban</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Turbaner</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Turban</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Turban</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046127\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046127"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10577">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">Bois</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Madera</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hout</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10571"/>
    <skos:prefLabel xml:lang="sr">Drvo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Holz</skos:prefLabel>
    <skos:prefLabel xml:lang="en">wood</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Trä</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011914\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011914"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10005">
    <skos:prefLabel xml:lang="pt">Vestido</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abito</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vestido</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Robe</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Main garments for women, children, or infants consisting of a bodice and skirt made in one or more pieces (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Kleid</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Haljina</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Kolt</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sv">Dräkt</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Dress</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φόρεμα</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046159\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046159"/>
    <skos:prefLabel xml:lang="sv">Klänning</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Jurk</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Japon</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10557">
    <skos:prefLabel xml:lang="sv">Glas</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Glas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Glas</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10544"/>
    <skos:prefLabel xml:lang="en">glass</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Verre</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010797\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010797"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Vidrio</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Staklo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10611">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10040"/>
    <skos:prefLabel xml:lang="en">Pelerine</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10419">
    <skos:prefLabel xml:lang="es">Estampado</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Druck</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Print</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Druk</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">impression</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Štampa</skos:prefLabel>
    <skos:prefLabel xml:lang="en">print</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τύπωμα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tryck</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Impressão</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:altLabel xml:lang="fr">Imprimé</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10681">
    <skos:altLabel xml:lang="de">Zellstoff</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Cellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Cellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Celuloza</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Cellulosa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Celulosa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10513">
    <skos:prefLabel xml:lang="nl">Haarnetje</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210861\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210861"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Redecilla</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="de">Haarnetz</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hairnet</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Nec</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Mrežica za kosu</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Résille</skos:prefLabel>
    <skos:altLabel xml:lang="es">Gandaya</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Hårnät</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10083">
    <skos:scopeNote xml:lang="en">Women's one-piece garments consisting of a camisole and bloomers. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Kombination</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Pagliaccetto</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kombinezon sa gaćicama</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Camiknickers</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Combinação calção</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Teddy</skos:altLabel>
    <skos:prefLabel xml:lang="de">Hemdhose</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Camisola con calzón</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chemise-culotte</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ολόσωμο εσώρουχο</skos:prefLabel>
    <skos:altLabel xml:lang="de">Hemdhöschen</skos:altLabel>
    <skos:altLabel xml:lang="en">"Teddy, cami-knickers"</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210540\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210540"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Spitzenhemdhöschen</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Combinaison</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10527">
    <skos:prefLabel xml:lang="sr">Slama</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011908\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011908"/>
    <skos:prefLabel xml:lang="es">Paja</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Stro</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Halm</skos:prefLabel>
    <skos:prefLabel xml:lang="en">straw</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10526"/>
    <skos:prefLabel xml:lang="de">Stroh</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Paille</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10206">
    <skos:prefLabel xml:lang="pt">Panama</skos:prefLabel>
    <skos:prefLabel xml:lang="en">panama hat</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Panama</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Panamá</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="it">Cappello Panama</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046107\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046107"/>
    <skos:prefLabel xml:lang="el">"""Πάναμα"""</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Panamahattar</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Panama šešir</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Panamahut</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Panamahoed</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Lightweight hats made of fine, pale straw with a molded ridge front to back across the crown; hand-plaited in Central and South America from the leaves of the jipijapa. Also, term used loosely for similar summer hats regardless of the type of straw. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10595">
    <skos:prefLabel xml:lang="es">Perla</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pärla</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10581"/>
    <skos:prefLabel xml:lang="nl">Parel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="en">pearl</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Biseri</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Perle</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Perle</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011827\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011827"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10296">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="en">stole (outerwear)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">étole</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Écharpe</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Estola</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stola (Überbekleidung)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Stole</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Lång halsduk</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εσάρπα</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Long and narrow unshaped garments made from cloth or fur and used as a covering for the shoulders and arms. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Stola</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Lång sjal</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Stola's (overkleding)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Schoudermanteltje</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300138741\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300138741"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Estola (prenda exterior)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10073">
    <skos:prefLabel xml:lang="en">Dressing jacket</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ρόμπα κοντή</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Poedermantel</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">"Casaco matinal (geralmente curto, usado antes de vestir)"</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Morgen- Hausjacke</skos:prefLabel>
    <skos:altLabel xml:lang="de">Frisierjacke (Peignoir)</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10069"/>
    <skos:prefLabel xml:lang="sr">Kućna jakna</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">veste d'intérieur</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Capa matinal</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Hip- to knee-length garments made in jacket or mantle form, worn while dressing. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Batín</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Veste corta da camera</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209949\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209949"/>
    <skos:altLabel xml:lang="nl">Kapmantel</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Bäddjacka</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10130">
    <skos:scopeNote xml:lang="en">Set of garments consisting of sweat pants and sweat shirt. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Fato de treino</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Tuta da ginnastica</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Sweat suit</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Joggingpak</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Trainingspak</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300225928\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300225928"/>
    <skos:altLabel xml:lang="de">Jogginganzug</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Träningsoverall</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sudadera</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">survêtement</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φόρμα γυμναστικής</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:prefLabel xml:lang="sr">Trenerka</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Trainingsanzug</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10671">
    <skos:prefLabel xml:lang="it">Mostra fotografica</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Photographic Exhibition</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10409">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300311190\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300311190"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10406"/>
    <skos:prefLabel xml:lang="it">Rame</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bakarna</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kupfer</skos:prefLabel>
    <skos:altLabel xml:lang="de">kupfern</skos:altLabel>
    <skos:altLabel xml:lang="it">Rame</skos:altLabel>
    <skos:prefLabel xml:lang="en">Copper</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπρονζέ</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Cuivre</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Koper</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cobre</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Acobreado</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Koppar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10547">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011029\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011029"/>
    <skos:prefLabel xml:lang="sr">Srebro</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zilver</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Silber</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Argento</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Prata</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Plata</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Argent</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Silver</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="en">Silver</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10421">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">handbemalt</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:prefLabel xml:lang="el">Ζωγραφική με το χέρι</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hand painting</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">peinture à la main</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Ručno oslikavanje</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Handmålad</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Handbeschilderd</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pintado a mano</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pintura à mão</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10113">
    <skos:prefLabel xml:lang="pt">Traje litúrgico</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210414\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210414"/>
    <skos:altLabel xml:lang="pt">Paramento litúrgico</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <skos:prefLabel xml:lang="el">Άμφια</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">costume liturgique</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Liturgisches Gewand</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Liturgijska odežda</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abito talare</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Liturgical costume</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vestuario litúrgico</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Liturgische kleding</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Liturgisk dräkt</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10537">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10536"/>
    <skos:prefLabel xml:lang="en">kidskin</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Geitenleer (jonge geiten)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Killingskinn</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel de cabritilla</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300255987\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300255987"/>
    <skos:prefLabel xml:lang="fr">Peau de chevreau</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Ziegenleder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Jareća koža</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10132">
    <skos:prefLabel xml:lang="el">Αξεσουάρ ενδυμάτων</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Accessoirer</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209273\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209273"/>
    <skos:prefLabel xml:lang="fr">accessoires de costumes</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Items that do not form the primary articles of clothing, but are instead the smaller articles of dress, such as shoes, gloves, hats, jewelry, etc. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Bekleidungsaccessoires</skos:prefLabel>
    <skos:altLabel xml:lang="de">Accessoires</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Kledingaccessoires</skos:prefLabel>
    <skos:altLabel xml:lang="de">Zubehör</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Acessórios de traje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Aksesoar</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10000"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Accesorio (vestuario)</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Costume accessories</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accessori</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10533">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Paardenhaar</skos:prefLabel>
    <skos:prefLabel xml:lang="en">horsehair</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Crin</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Crin</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Konjska dlaka</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10531"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Tagel</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Rosshaar</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011819\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011819"/>
    <skos:altLabel xml:lang="de">Pferdehaar</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10476">
    <skos:prefLabel xml:lang="el">Ιστός αράχνης</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Spinnenseide</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Fibra de teia de aranha</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hilo de araña</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Spinrag</skos:prefLabel>
    <skos:prefLabel xml:lang="en">spider web fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spindelsilke</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Spinnennetzseide</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nit paukove mreže</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">soie d'araignée</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Spinnenwebvezel</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10350"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10323">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10322"/>
    <skos:prefLabel xml:lang="nl">Modetijdschrift</skos:prefLabel>
    <skos:prefLabel xml:lang="it">rivista di moda</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Modetidning</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Revista de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Revista de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fashion magazine</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modezeitschrift</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Περιοδικό μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modni časopis</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">magazine de mode</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10122">
    <skos:scopeNote xml:lang="en">"Women's suits worn for walking and other similar physical activities, dating from around 1900, usually consisting of a coat or jacket and long skirt. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Abiti da passeggio</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Fato de passeio</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Vestido de paseo</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Odeća za šetnju</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα περιπάτου</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Promenadkläder</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Walking suit</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Wanderkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tenue de marche</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300257138\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300257138"/>
    <skos:altLabel xml:lang="pt">Fato de caminhada</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Wandelkleding</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10337">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Exposição de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modetentoonstelling</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Exposición de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Επίδειξη μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modeutställning</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modna izložba</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modeausstellung</skos:prefLabel>
    <skos:prefLabel xml:lang="it">mostra</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Fashion exhibition</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="fr">exposition de mode</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10103">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210535\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210535"/>
    <skos:scopeNote xml:lang="en">Separate sleeves of light material worn under the sleeves of a main garment and visible through it or extending beyond it.</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Falsas mangas</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ärmhållare</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Armações  de manga</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sous-manches</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sleeve supports</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Ondermouw</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Μανίκια</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Ärmelstützen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="nl">Ondermouwen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Držači za rukave</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10461">
    <skos:prefLabel xml:lang="sv">Karikatyr</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Γελοιογραφία</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10305"/>
    <skos:altLabel xml:lang="el">Καρικατούρα</skos:altLabel>
    <skos:prefLabel xml:lang="fr">caricature</skos:prefLabel>
    <skos:prefLabel xml:lang="en">caricature</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300015634\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300015634"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Caricatura</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Karikatuur</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Caricatura</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Karikatur</skos:prefLabel>
    <skos:prefLabel xml:lang="it">caricatura</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Karikatura</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10523">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10365"/>
    <skos:prefLabel xml:lang="de">Lurex®</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Lureks</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Lurex</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lurex</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lurex</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Lurex</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lurex</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10435">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Herrenbekleidung</skos:altLabel>
    <skos:altLabel xml:lang="fr">vêtement pour homme</skos:altLabel>
    <skos:prefLabel xml:lang="es">Indumentaria masculina</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10433"/>
    <skos:prefLabel xml:lang="nl">Herenkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Traje masculino</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Herrenanzug</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">men's costume</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ανδρικό ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Costume masculin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Muški kostim</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Herrkläder</skos:prefLabel>
    <skos:altLabel xml:lang="de">Herrenkleidung</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10123">
    <skos:prefLabel xml:lang="sr">Odeća za plivanje i plažu</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221470\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221470"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">tenue de plage</skos:altLabel>
    <skos:prefLabel xml:lang="de">Bade- und Strandkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Badkläder och strandkläder</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μαγιό και ρούχα παραλίας</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Costumi da bagno</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Swim and beachwear</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zwem- en strandkleding</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Zwem- en strandkledij</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Trajes de baño</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fatos de banho e roupa de praia</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">maillot de bain</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10193">
    <skos:prefLabel xml:lang="es">Gorro plano</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καλπάκι</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Engelse arbeiderspet</skos:altLabel>
    <skos:prefLabel xml:lang="fr">couvre-chef</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210746\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210746"/>
    <skos:prefLabel xml:lang="pt">Boina</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kepsar</skos:prefLabel>
    <skos:altLabel xml:lang="fr">bonnet</skos:altLabel>
    <skos:prefLabel xml:lang="de">Tellerbarett</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Arbeiderspetten</skos:prefLabel>
    <skos:prefLabel xml:lang="en">flatcap</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Round caps with low, flat crowns, worn in the 16th and 17th centuries by London citizens. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="sr">Kačket</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schiebermütze</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Berretto floscio</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10449">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">clogs</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sabot</skos:prefLabel>
    <skos:altLabel xml:lang="es">Almadreñas</skos:altLabel>
    <skos:altLabel xml:lang="sr">Nanule</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Träskor</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zuecos</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Holzschuhe</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τσόκαρα</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Socas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Klompe</skos:prefLabel>
    <skos:altLabel xml:lang="de">Holzpantinen</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Klompen</skos:prefLabel>
    <skos:prefLabel xml:lang="it">zoccoli</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10493">
    <skos:prefLabel xml:lang="de">Brokatelle</skos:prefLabel>
    <skos:altLabel xml:lang="de">Brocatelle</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Brocatel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Brocatel</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Brocatel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Brocatel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10488"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Brocatelle</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Brocatelle</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Brokad</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="sr">Brokatel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10112">
    <skos:prefLabel xml:lang="sr">Oklop</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Rustning</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <skos:prefLabel xml:lang="nl">Harnas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">Suits of armor</skos:altLabel>
    <skos:prefLabel xml:lang="fr">armure</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Armatura</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300036745\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300036745"/>
    <skos:altLabel xml:lang="nl">Wapenrusting</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Refers generally to that category of costume designed to be worn or carried to protect the body in combat. Armor pieces which are always physical parts of or are affixed to other pieces and cannot function alone are collocated under the guide term ""&lt;armor components&gt;."" For specifically groups of armor pieces designed as a whole to possess particular physical characteristics in order to suit a particular purpose or occasion, see ""armors."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Armaduras</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Armor</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Rüstung</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Armadura</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πανοπλία</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10422">
    <skos:prefLabel xml:lang="el">Ικάτ</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300249861\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300249861"/>
    <skos:prefLabel xml:lang="pt">Ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ikat</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:scopeNote xml:lang="en">Technique of resist dyeing in which hanks of thread are bound and dyed before weaving. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">ikat</skos:prefLabel>
    <skos:prefLabel xml:lang="en">ikat</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10131">
    <skos:prefLabel xml:lang="it">Costumi folkloristici</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">"Pozorišni kostim, filmski kostim, kostim za maskenbal"</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Folkloristisch kostuum</skos:prefLabel>
    <skos:altLabel xml:lang="de">Ländliche Tracht</skos:altLabel>
    <skos:prefLabel xml:lang="de">Ländliche Kleidung</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kostim</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Folkdräkt</skos:prefLabel>
    <skos:altLabel xml:lang="es">Traje regional</skos:altLabel>
    <skos:altLabel xml:lang="sv">Folklig dräkt</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10108"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Traje folclórico</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Folk costume</skos:prefLabel>
    <skos:altLabel xml:lang="it">Abiti folkloristici</skos:altLabel>
    <skos:prefLabel xml:lang="es">Indumentaria tradicional</skos:prefLabel>
    <skos:altLabel xml:lang="es">indumentaria poular</skos:altLabel>
    <skos:altLabel xml:lang="nl">Kostuum</skos:altLabel>
    <skos:prefLabel xml:lang="fr">costume folkloristique</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300266810\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300266810"/>
    <skos:prefLabel xml:lang="el">Λαϊκή φορεσιά</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10475">
    <skos:altLabel xml:lang="de">Aluminiumgespinstfaden</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Aluminiumdraad</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aluminijum</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10521"/>
    <skos:prefLabel xml:lang="pt">Alumínio</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Aluminiumfaden</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fil d'aluminium</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Aluminium thread</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αλουμίνιο</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hilo de aluminio</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Aluminiumtråd</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10532">
    <skos:prefLabel xml:lang="fr">poil de chèvre</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011818\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011818"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Gethår</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pelo de cabra</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Ziegenhaar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Geitenhaar</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10531"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">goat hair</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kozja dlaka</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10536">
    <skos:prefLabel xml:lang="sr">Kozja koža</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300255984\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300255984"/>
    <skos:altLabel xml:lang="de">Ziegenfell</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Geitenhuid</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">goat skin</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Peau de chèvre</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel de cabra</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ziegenleder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:altLabel xml:lang="nl">Geitenleer</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Getskinn</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10322">
    <skos:altLabel xml:lang="it">giornale</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Časopis</skos:prefLabel>
    <skos:prefLabel xml:lang="it">rivista</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Περιοδικό</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="nl">Tijdschrift</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Tidning</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Revista</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Revista</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">magazine</skos:prefLabel>
    <skos:prefLabel xml:lang="en">magazine</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Zeitschrift</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Magazine</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Periodicals containing articles, essays, poems, or other writings by different authors, usually on a variety of topics and intended for a general reading public or treating a particular area of interest for a popular audience. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215389\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215389"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10489">
    <skos:prefLabel xml:lang="el">Taquete</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10488"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Taqueté</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Taqueté</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10522">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Agavenfaser</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="nl">Agavevezel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="en">Agave fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Agavefiber</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sisal</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Pita</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Agavino vlakno</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10121">
    <skos:prefLabel xml:lang="en">Sports and leisurewear</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ενδύματα σπορ και αναψυχής</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Ropa deportiva</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">vêtements de sport et loisir</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbiglio sportivo e per il tempo libero</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10108"/>
    <skos:scopeNote xml:lang="en">Garments and accessories designed to be worn while engaged in a specific active sport. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Sport- und Freizeitkleidung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221269\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221269"/>
    <skos:altLabel xml:lang="nl">Sport- en vrijetijdskledij</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Sportkläder / Fritidskläder</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Odeća za sport i rekreaciju</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sport- en vrijetijdskleding</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Roupa de desporto e lazer</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10102">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Taille</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Prsluče</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300217634\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300217634"/>
    <skos:prefLabel xml:lang="fr">Gaine</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Gördel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Waist</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Waist</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Undergarments or garmets worn around the anatomical waist and up onto the chest, to which petticoats and drawers are buttoned. They are lightly boned or unboned, usually made of sturdy fabric, with shoulder straps and buttons at the side for attaching to a lower body garment; usually worn by children and women. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="nl">Keursje</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Cintura</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Liguero</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Vita</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10460">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Fotografija</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φωτογραφία</skos:prefLabel>
    <skos:altLabel xml:lang="fr">photo</skos:altLabel>
    <skos:prefLabel xml:lang="es">Fotografía</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046300\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046300"/>
    <skos:prefLabel xml:lang="de">Fotografie</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Foto</skos:prefLabel>
    <skos:altLabel xml:lang="it">Fotografia stampata</skos:altLabel>
    <skos:prefLabel xml:lang="en">Photograph</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fotografia</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fotografi</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="it">Fotografia</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">photographie</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10336">
    <skos:prefLabel xml:lang="de">Modeveranstaltungen</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mode evenemang</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Mode-evenementen</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Eventos de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modna događanja</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Eventos de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">événement lié à la mode</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Έκθεση μόδας</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Fashion Events</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Evento</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10436">
    <skos:prefLabel xml:lang="pt">Traje infantil</skos:prefLabel>
    <skos:altLabel xml:lang="fr">vêtement pour enfant</skos:altLabel>
    <skos:prefLabel xml:lang="de">Kinderanzug</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Kinderkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria infantil</skos:prefLabel>
    <skos:prefLabel xml:lang="en">children's costume</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kinderkleidung</skos:altLabel>
    <skos:altLabel xml:lang="de">Kinderbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Dečiji kostim</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παιδικό ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Costume d'enfant</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Barnkläder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10433"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10194">
    <skos:prefLabel xml:lang="en">jockey cap</skos:prefLabel>
    <skos:altLabel xml:lang="de">Jockeymütze</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Caps with long visors, of the style worn by jockeys; also worn by light infantry from the mid-18th century. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Džokej kapa</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Boné de jockey</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Jockeykepsar</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210763\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210763"/>
    <skos:prefLabel xml:lang="fr">bombe</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τζόκεϊ</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jockeyhut</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Berretto da fantino</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gorra de jockey</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="nl">Jockeypet (AAT)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10492">
    <skos:altLabel xml:lang="de">Pequin</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Peking</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Peking</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Peking</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Peking</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Peking</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Peking</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10488"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">Pekin</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pequi</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pekín</skos:prefLabel>
    <skos:altLabel xml:lang="de">Peking</skos:altLabel>
    <skos:altLabel xml:lang="pt">Pequim / Pequinês</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10474">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Staaldraad</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ατσάλι</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hilo de acero</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Steel thread</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Čelik</skos:prefLabel>
    <skos:altLabel xml:lang="de">Stahlgespinstfaden</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10521"/>
    <skos:prefLabel xml:lang="de">Stahlfaden</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">fil d'acier</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Aço</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ståltråd</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10180">
    <skos:altLabel xml:lang="el">Τσεμπέρι</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Foulard</skos:prefLabel>
    <skos:altLabel xml:lang="fr">voile</skos:altLabel>
    <skos:altLabel xml:lang="nl">Voile</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Sjalett</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Headcloths worn instead of hats for warmth or concealment of the head or face, either where the length of the scarf exceeds the width, or a square scarf that is folded in a triangle and tied under the chin. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Foulard per la testa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Marama</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Lenço de cabeça</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Huvudduk</skos:altLabel>
    <skos:prefLabel xml:lang="fr">foulard</skos:prefLabel>
    <skos:prefLabel xml:lang="en">headscarve</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kopftuch</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pañuelo de cabeza (forma cuadrada)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μαντίλι κεφαλιού</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300256716\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300256716"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10177"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10111">
    <skos:prefLabel xml:lang="sv">Akademisk dräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Amtstracht</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to the various costumes worn by university and collegiate faculty, administrators, and graduating students at graduation ceremonies and other special occasions. The most common element of academic costume is the gown, a tradition dating to the Middle Ages. Decorative hoods and various caps, including the mortarboard, are other common elements of academic costume. Trimmings of various colors may be used to refer to different disciplines of study. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Academisch kostuum</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Completo accademico: toga e tocco</skos:prefLabel>
    <skos:altLabel xml:lang="de">Talar</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300265060\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300265060"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Academische kledij</skos:altLabel>
    <skos:prefLabel xml:lang="fr">costume officiel</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Traje académico</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Traje académico</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Akademska odora</skos:prefLabel>
    <skos:altLabel xml:lang="de">Amtskleidung</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <skos:prefLabel xml:lang="el">Ακαδημαϊκό ένδυμα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Academic costume</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10478">
    <skos:prefLabel xml:lang="pt">Cânhamo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chanvre</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Hemp</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κάνναβη</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014043\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014043"/>
    <skos:prefLabel xml:lang="de">Hanf</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Hennep</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hampa</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="sr">Konoplja</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cáñamo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10549">
    <skos:prefLabel xml:lang="de">Stahl</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:prefLabel xml:lang="sv">Stål</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300133751\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300133751"/>
    <skos:prefLabel xml:lang="it">Acciaio</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Steel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Čelik</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Staal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Acier</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Acero</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10115">
    <skos:altLabel xml:lang="de">Dienstmädchen Kleidung</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Uniforme de servicio doméstico</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kleidung für Dienstpersonal</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <skos:prefLabel xml:lang="sr">Odeća za poslugu</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">tenue de domestique</skos:prefLabel>
    <skos:altLabel xml:lang="fr">tenue des domestiques</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Farda de serviçais</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Farda de pessoal doméstico</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Tjänstefolkets kläder</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Divisa da cameriere</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Servants dress</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα υπηρετών</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Dienstbotenkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Dienstbodekleding</skos:prefLabel>
    <skos:altLabel xml:lang="it">Livrea</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10325">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">catalogue d'exposition</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Tentoonstellingscatalogus</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ausstellungskatalog</skos:prefLabel>
    <skos:prefLabel xml:lang="it">catalogo mostra</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:scopeNote xml:lang="en">Publications that document the works displayed in an exhibition. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Katalog izložbe</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κατάλογος έκθεσης</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300026096\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300026096"/>
    <skos:prefLabel xml:lang="pt">Catálogo de exposição</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Utställningskatalog</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Catálogo de exposición</skos:prefLabel>
    <skos:prefLabel xml:lang="en">exhibition catalogue</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10535">
    <skos:prefLabel xml:lang="nl">Slangenhuid</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10684"/>
    <skos:altLabel xml:lang="de">Schlangenhaut</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Ormskinn</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel de serpiente</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Zmijska koža</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schlangenleder</skos:prefLabel>
    <skos:prefLabel xml:lang="en">snake skin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Peau de serpent</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011850\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011850"/>
    <skos:altLabel xml:lang="nl">Slangenleer</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10101">
    <skos:prefLabel xml:lang="nl">Onderrok</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Saia interior</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Anágua</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fond de jupe</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Sottogonna</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Underskirt</skos:prefLabel>
    <skos:altLabel xml:lang="es">"Refajo, enagua"</skos:altLabel>
    <skos:prefLabel xml:lang="es">Bajofalda</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">jupon</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Underkjol</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Μισοφόρι φούστα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Unterrock</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Skirts worn beneath another; use especially for those underskirts designed to be seen. (AAT)</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="sr">Podsuknja</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210574\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210574"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10521">
    <skos:prefLabel xml:lang="nl">Metaaldraad</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300379384\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300379384"/>
    <skos:prefLabel xml:lang="es">Hilo metálico</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10348"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sv">Metalltråd</skos:prefLabel>
    <skos:altLabel xml:lang="de">Metallgespinstfaden</skos:altLabel>
    <skos:prefLabel xml:lang="de">Metallfaden</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Metalna nit</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Metal thread</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fil métallique</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10488">
    <skos:prefLabel xml:lang="sr">Kombinovane tehnike</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Complex weefsel</skos:altLabel>
    <skos:prefLabel xml:lang="de">Mischtechniken</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Mixed techniques</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Gecombineerde weeftechnieken</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Blandteknik</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">techniques mixtes</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10371"/>
    <skos:prefLabel xml:lang="el">Μεικτές τεχνικές</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Técnicas mistas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Ligamentos compuestos</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10120">
    <skos:prefLabel xml:lang="it">Abito da cerimonia</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Identifying garments or styles of dress worn by the members of a given profession, organization, or rank. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Uniform (ceremoniell)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Galauniform</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Uniform (ceremoniële kleding)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">uniforme</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Στολή</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Uniforme (indumentaria deportiva)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Uniform (ceremoniële kledij)</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212393\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212393"/>
    <skos:altLabel xml:lang="fr">tenue de cérémonie</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Uniforme (traje cerimonial)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ceremonijalna odežda</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10117"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Uniform (ceremonial costume)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10539">
    <skos:altLabel xml:lang="de">Fell</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Fourrure</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel (de pelo largo)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pelz</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Päls</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011811\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011811"/>
    <skos:altLabel xml:lang="nl">Vacht</skos:altLabel>
    <skos:prefLabel xml:lang="en">fur</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Krzno</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10530"/>
    <skos:prefLabel xml:lang="nl">Pels</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10335">
    <skos:prefLabel xml:lang="fr">Collection d'archive</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Arquivo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Colección de archivo</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10333"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Archiefcollectie</skos:altLabel>
    <skos:prefLabel xml:lang="de">Archivsammlung</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Arkivsamling</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300375748\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300375748"/>
    <skos:prefLabel xml:lang="it">Collezione (d'archivio)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Arhivska zbirka</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Archief</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εκδηλώσεις μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="en">archival collection</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Collections of historical records and primary source documents that have accumulated over the course of an individual's or organization's lifetime, and are kept to show the function or history of the person or an organization. Documents may be tangible or digital. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10191">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Schlafhaube</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Touca de dormir</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cuffia (morbida)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft, cloth caps with a gathered crown, worn indoors by women in the 18th century. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">bonete de dormir</skos:altLabel>
    <skos:prefLabel xml:lang="es">Gorro de dormir</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210740\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210740"/>
    <skos:altLabel xml:lang="nl">Nachtmuts</skos:altLabel>
    <skos:altLabel xml:lang="de">Morgenhaube</skos:altLabel>
    <skos:altLabel xml:lang="es">Dormeuse</skos:altLabel>
    <skos:prefLabel xml:lang="fr">bonnet de nuit</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="nl">Slaapmuts</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σκούφια ύπνου</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kapa za spavanje</skos:prefLabel>
    <skos:prefLabel xml:lang="en">dormeuse (cap)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Nattmössor</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10181">
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi na glavi</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Accessoires de tête</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Accesorio para la cabeza</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ για το κεφάλι</skos:prefLabel>
    <skos:prefLabel xml:lang="de">auf dem Kopf getragene Accessoires</skos:prefLabel>
    <skos:prefLabel xml:lang="en">accessories worn on the head</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:altLabel xml:lang="fr">accessoires portés sur la tête</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Accessoires gedragen op het hoofd</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Accessori da indossare sulla testa</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Accessoarer för huvudet</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211601\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211601"/>
    <skos:prefLabel xml:lang="pt">Acessórios usados na cabeça</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10437">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10433"/>
    <skos:prefLabel xml:lang="de">Unisex Anzug</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Traje unissexo</skos:prefLabel>
    <skos:altLabel xml:lang="de">Unisex-Bekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα unisex</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Unisex</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">costume mixte</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria unisex</skos:prefLabel>
    <skos:altLabel xml:lang="de">nicht geschlechtsspezifische Kleidung</skos:altLabel>
    <skos:altLabel xml:lang="de">Unisex-Kleidung</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">unisex costume</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Unisex kleding</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Uniseks kostim</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10100">
    <skos:prefLabel xml:lang="it">Maglietta intima</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210573\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210573"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Shirts, with or without sleeves, worn next to the body and under another shirt. Usually pullovers of a cotton jersey. For similar garments worn as main garments use ""T-shirts."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Camisola interior</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φανέλα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Camiseta interior</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">maillot de corps</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Undershirt</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Onderhemd</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="de">Unterhemd</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hemd (tricot ondergoed)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">maillot de corps</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Potkošulja</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Undertröja</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10447">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">haut-de-forme</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cartola</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Cilindar</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ημίψηλο</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrero de copa</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zylinder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Top hat</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="nl">Hoge hoed</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Cylinderhatt</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10491">
    <skos:prefLabel xml:lang="pt">Lampasso</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lampás</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Lampas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Lampas</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10488"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300254861\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300254861"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10135">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300250566\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300250566"/>
    <skos:prefLabel xml:lang="es">Bandolera (bolsa)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">sac bandoulière</skos:altLabel>
    <skos:altLabel xml:lang="el">Φισεκλίκια</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="fr">sac à bandoulière</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Bandolijer torba</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Σελάχι</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bandolier bag</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Mala a tiracolo / Cinturão de cartucheira</skos:altLabel>
    <skos:scopeNote xml:lang="en">"General purpose bags carried by North American Indians with attached shoulder straps worn over the shoulder and across the breast. Usually made of wool, muslin, or buckskin and heavily decorated with beadwork, quillwork, or embroidery. For shoulder belts worn across the breast from which wallets, small bags, or pockets often containing ammunition are sometimes suspended, use ""bandoliers."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Bandeliertas</skos:prefLabel>
    <skos:prefLabel xml:lang="it">borsa a bandoliera</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Umhängetasche</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Saco de bandoleiro</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bandolierväskor</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10324">
    <skos:prefLabel xml:lang="es">Portada</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10322"/>
    <skos:prefLabel xml:lang="sv">Omslag</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">première de couverture</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Titelblatt</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cover</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Naslovna strana</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Εξώφυλλο</skos:prefLabel>
    <skos:altLabel xml:lang="fr">couverture</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Cover</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Capa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">copertina</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10133">
    <skos:prefLabel xml:lang="de">mitgeführte Bekleidungsaccessoires</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Vastgehouden accessoires</skos:prefLabel>
    <skos:altLabel xml:lang="de">Accessoires</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Acessórios de traje transportáveis</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi u ruci</skos:prefLabel>
    <skos:prefLabel xml:lang="en">costume accessories carried</skos:prefLabel>
    <skos:altLabel xml:lang="fr">accessoires de costume portés</skos:altLabel>
    <skos:altLabel xml:lang="de">Zubehör</skos:altLabel>
    <skos:prefLabel xml:lang="es">Accesorios de mano</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires non solidaires du corps</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accessori portati a mano</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Handburna accessoarer</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Meegedragen kledingaccessoire</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10132"/>
    <skos:prefLabel xml:lang="el">Αξεσουάρ χειρός</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209275\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209275"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10477">
    <skos:prefLabel xml:lang="en">Esparto grass</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σπάρτο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Esparto</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Esparto</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Esparto</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Espartogräs</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="fr">alfa</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Esparto trava</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Esperto</skos:prefLabel>
    <skos:altLabel xml:lang="de">Espartogras</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014036\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014036"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10534">
    <skos:altLabel xml:lang="sv">Hud</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Skinn</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Peau</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Piel</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sv">Läder</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Huid</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Leder</skos:prefLabel>
    <skos:altLabel xml:lang="de">Haut</skos:altLabel>
    <skos:altLabel xml:lang="de">Fell</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="en">skin</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011840\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011840"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10530"/>
    <skos:prefLabel xml:lang="sr">Koža</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10420">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="nl">Sits</skos:prefLabel>
    <skos:altLabel xml:lang="es">Zaraza</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chintz</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="en">chintz</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Chita</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10419"/>
    <skos:altLabel xml:lang="nl">Chintz</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Činc</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γυάλωμα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10114">
    <skos:altLabel xml:lang="fr">bleu de travail</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Jardineiras</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ολόσωμη φόρμα</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Overall</skos:altLabel>
    <skos:prefLabel xml:lang="it">Tuta da lavoro</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"One-piece garments consisting of a trouserlike portion and a full top with or without sleeves worn over other garments for protection. For one-piece garments consisting of trousers with a bib use ""overalls (main garments)."" (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Fato-macaco</skos:altLabel>
    <skos:prefLabel xml:lang="es">Mono de trabajo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Overall</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Radni kombinezon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Overal (professionele kleding)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bleu de travail</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046157\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046157"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10410">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="de">Bunt</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Matizado</skos:prefLabel>
    <skos:altLabel xml:lang="de">multicolour</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Multicolore</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Multicolore</skos:prefLabel>
    <skos:altLabel xml:lang="it">Multicolore</skos:altLabel>
    <skos:prefLabel xml:lang="en">Multicoloured</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Veelkleurig</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="el">Πολύχρωμο</skos:prefLabel>
    <skos:altLabel xml:lang="de">vielfarbig</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300252256\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300252256"/>
    <skos:prefLabel xml:lang="sv">Mångfärgad</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Multicolor</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Višebojna</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10620">
    <skos:prefLabel xml:lang="fr">Corselet</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10086"/>
    <skos:prefLabel xml:lang="en">Corselet</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10487">
    <skos:prefLabel xml:lang="el">Lyocell</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="de">Lyocell</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lyocell</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lyocell</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Lyocell</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lyocell</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">lyocell</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Liocel</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Liocel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10334">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Modna kolkecija</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Colección de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Συλλογή μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modecollectie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">Collection de mode</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10333"/>
    <skos:altLabel xml:lang="sr">Modna zbirka</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Colecção de moda</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Collezione (stagionale)</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fashion collection</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modekollektion</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modekollektion</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10538">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Piel de oveja</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fårskinn</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Schapenleer</skos:altLabel>
    <skos:altLabel xml:lang="de">Schafpelz</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300193374\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300193374"/>
    <skos:prefLabel xml:lang="nl">Schapenhuid</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10534"/>
    <skos:altLabel xml:lang="de">Schafspelz</skos:altLabel>
    <skos:prefLabel xml:lang="en">sheep skin</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Peau de mouton</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schafleder</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schafsfell</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Ovčija koža</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10520">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Fasern</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014024\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014024"/>
    <skos:prefLabel xml:lang="it">Fibra</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Vezels</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10346"/>
    <skos:prefLabel xml:lang="sr">Vlakna</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fibres</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Fibre</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Fibras</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10548">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10545"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Koper</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Rame</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011020\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011020"/>
    <skos:prefLabel xml:lang="en">Copper</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Koppar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kupfer</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cuivre</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bakar</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cobre</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cobre</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10182">
    <skos:prefLabel xml:lang="el">Γυαλιά</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300266808\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300266808"/>
    <skos:scopeNote xml:lang="en">"Devices to compensate for defective vision or to protect the eyes from light, dust, and the like, consisting usually of two lenses set in a frame that includes a nosepiece for resting on the bridge of the nose and which may also have two sidepieces extending over or around the ears. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Naočare</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Brille</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Occhiali</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pince-nez</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gafas</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:prefLabel xml:lang="en">spectacles / glasses</skos:prefLabel>
    <skos:altLabel xml:lang="es">Anteojos</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Brillen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">lunettes</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Glasögon</skos:prefLabel>
    <skos:altLabel xml:lang="sv">glassögon</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Óculos</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10438">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Buckle</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Αγκράφα</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fivela</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">boucle</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spänne</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Gesp</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schnalle</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Hebilla</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kopča</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10060"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10448">
    <skos:prefLabel xml:lang="es">Sombrero hongo</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bowler hat</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="fr">chapeau melon</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bolhoed</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Polucilindar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Melone</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Chapéu de côco</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Plommonstop</skos:prefLabel>
    <skos:altLabel xml:lang="el">Μελόν</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μπόουλερ</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10490">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Samito</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Samito</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Samito</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="nl">Samito</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Samito</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10488"/>
    <skos:prefLabel xml:lang="sv">Samitum</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Samit</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Samit</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Samit</skos:prefLabel>
    <skos:altLabel xml:lang="de">Samitum</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10192">
    <skos:prefLabel xml:lang="en">fez</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Close-fitting, cone-shaped caps with a flat crown usually of felt and often trimmed with a long tassel; of a type originating in Turkey. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Barrete mourisco</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210744\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210744"/>
    <skos:prefLabel xml:lang="sr">Fes</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Fezzen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Φέσι</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:altLabel xml:lang="de">Tarbusch</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Fez</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fez</skos:prefLabel>
    <skos:altLabel xml:lang="de">Fes</skos:altLabel>
    <skos:prefLabel xml:lang="de">Fez</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Fez</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fez</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Fez</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10134">
    <skos:prefLabel xml:lang="de">Tasche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">sac</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Small, medium, or large-sized soft or rigid receptacles, intended for carrying personal articles and usually used as ladies' costume accessories. A bag is closed in on all sides except at the top, where also it generally can be closed, and usually having handles or straps for carrying on the shoulder or in the hand. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Väskor</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bag (costume accessories)</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300198926\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300198926"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Mala (acessórios de traje)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Borse</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tas</skos:prefLabel>
    <skos:altLabel xml:lang="fr">sacs (accessoires de costumes)</skos:altLabel>
    <skos:prefLabel xml:lang="el">Τσάντα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="es">Bolsa (accesorio)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tašna</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10289">
    <skos:altLabel xml:lang="pt">Meias japonesas</skos:altLabel>
    <skos:prefLabel xml:lang="it">Calzini tabi (giapponesi)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Tabi</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tabi</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Tabi</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tabi</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10283"/>
    <skos:prefLabel xml:lang="sr">Tabi čarape</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Japanese ankle-socks, usually of cotton, with a separate stall for the large toe, worn by both sexes. Often with a thick sole, and worn alone or with wooden clogs or 'geta.' (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">tabi (hosiery)</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300375287\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300375287"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Tabi-Socke</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussette avec gros orteil séparé</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κάλτσες με ένα δάχτυλο</skos:prefLabel>
    <skos:altLabel xml:lang="de">Tabi</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10430">
    <skos:scopeNote xml:lang="en">A specially rigged costume that tears in the scene. Several are made to accommodate more than one ‘take’ (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:prefLabel xml:lang="en">Break-away</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10486">
    <skos:prefLabel xml:lang="sr">Elastin (spandeks)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ελαστάνη</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Elastan (spandex)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Elasthaan</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Elastano (spandex)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Elastane (spandex)</skos:altLabel>
    <skos:prefLabel xml:lang="es">Elastano (spandex)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Elastan</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Elastane (spandex)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="fr">élasthanne</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300310125\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300310125"/>
    <skos:altLabel xml:lang="nl">Spandex</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10411">
    <skos:prefLabel xml:lang="sv">Orange</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Orange</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Orange</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Orange</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πορτοκαλί</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="es">Naranja</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="pt">Laranja</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Oranje</skos:prefLabel>
    <skos:altLabel xml:lang="it">Arancione</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300126734\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300126734"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Arancione</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Narandžasta</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10451">
    <skos:prefLabel xml:lang="sr">Podela obuće prema rodu i uzrastu</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoenen naar drager</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:prefLabel xml:lang="de">Schuhe nach dem Geschlecht des Trägers</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">geschlechtsspezifische Fußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="el">Υποδήματα ανάλογα με τη χρήση</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Calçado por utilizador</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Schoeisel per drager</skos:altLabel>
    <skos:prefLabel xml:lang="en">footwear by wearer</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Brukare / Bärare</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzado según el usuario</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">type de chaussures</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">calzature in relazione a chi le indossa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10327">
    <skos:prefLabel xml:lang="nl">Efemera</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">ephèmère</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Kleindrucksachen</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:scopeNote xml:lang="en">"Items manufactured for a specific, limited use, and usually intended to be discarded thereafter, such as printed material of interest for its appearance, association, design, or documentation produced in connection with art exhibitions, etc. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Accessori d'antiquariato</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Impressos efémeros</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Efemerije</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Documentos efímeros</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300028881\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300028881"/>
    <skos:altLabel xml:lang="es">Ephemera</skos:altLabel>
    <skos:prefLabel xml:lang="en">ephemera</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Varukatalog</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Ephemera</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ephemera</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kleinschrift</skos:altLabel>
    <skos:prefLabel xml:lang="el">Εφήμερα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10425">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Caftán</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kafan</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Καφτάνι</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Caftan</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Caftan</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kaftan</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kaftan</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kaftan</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10279">
    <skos:prefLabel xml:lang="en">digital media</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Meios de comunicação digitais</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Digitale media</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Medios digitales</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">digitale Medien</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Digitalni medij</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10301"/>
    <skos:prefLabel xml:lang="el">Ψηφιακά μέσα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Digital media</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">média numérique</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10347">
    <skos:altLabel xml:lang="nl">Natuurvezel</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Fibras naturais</skos:prefLabel>
    <skos:prefLabel xml:lang="en">natural fibres</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Naturfasern</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Fiber obtained from an animal, vegetable, or mineral source used for conversion into non-woven fabrics (felt, paper) or woven cloth. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Naturfiber</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras naturales</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="fr">fibres naturelles</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10520"/>
    <skos:prefLabel xml:lang="nl">Natuurlijke vezels</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014027\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014027"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Φυσικές ύλες</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Prirodna vlakna</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10137">
    <skos:altLabel xml:lang="pt">Bolsa sem alça</skos:altLabel>
    <skos:prefLabel xml:lang="en">clutch bag</skos:prefLabel>
    <skos:altLabel xml:lang="de">oder Clutch</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:scopeNote xml:lang="en">"Bags without handle or strap, but usually with a clasp, of a size that is suitable to be carried in the hand; made of various fabrics or leathers. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">pochette</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Klač tašna</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Pochette</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bolso de cartera</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Enveloptas</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Aftonväska</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Clutch</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kuvertväskor</skos:prefLabel>
    <skos:altLabel xml:lang="es">Clutch</skos:altLabel>
    <skos:altLabel xml:lang="nl">Clutch</skos:altLabel>
    <skos:prefLabel xml:lang="el">Clutch</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Unterarmtasche</skos:prefLabel>
    <skos:altLabel xml:lang="fr">sac pochette</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300256798\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300256798"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10445">
    <skos:prefLabel xml:lang="sv">Monokel</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Monokel</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μονύελο</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Monokl</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Monocle</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Monóculo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Monóculo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">monocle</skos:prefLabel>
    <skos:prefLabel xml:lang="en">monocle</skos:prefLabel>
    <skos:altLabel xml:lang="el">Μονόκλ</skos:altLabel>
    <skos:altLabel xml:lang="de">Einglas</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10471">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Μπρούντζος</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10521"/>
    <skos:prefLabel xml:lang="es">Hilo de cobre</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Koppartråd</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fil de cuivre</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Copper threas</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cobre</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Kupfergespinstfaden</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Koperdraad</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bakar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kupferfaden</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/Materials">
    <skos:prefLabel xml:lang="en">Materials</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10466">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Primavera-Estate</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <skos:prefLabel xml:lang="el">Άνοιξη/Καλοκαίρι</skos:prefLabel>
    <skos:altLabel xml:lang="de">Frühjahr-Sommer</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Prentemps-Eté</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vår-Sommar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Prloeće-leto</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Frühling-Sommer</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Primavera-verano</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lente-Zomer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Primavera-Verão</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Spring-Summer</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10127">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:prefLabel xml:lang="el">Κοστούμι παγοδρομιών</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Iceskating suit</skos:prefLabel>
    <skos:altLabel xml:lang="de">Eislaufanzug</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Fato de patinagem</skos:prefLabel>
    <skos:altLabel xml:lang="fr">tenue  de patinage sur glace</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Odeća za klizanje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schaatspak</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Eislaufkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria de patinaje sobre hielo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tenue de patin à glace</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Skridskodräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da pattinaggio sul ghiaccio</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10485">
    <skos:prefLabel xml:lang="pt">Poliéster</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Poliéster</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">polyester</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Polyester</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Polyester</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Polyester</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Polyester</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πολυέστερ</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300379829\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300379829"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10364"/>
    <skos:prefLabel xml:lang="sr">Poliester</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10431">
    <skos:prefLabel xml:lang="nl">Smokingjasje</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Esmóquin</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Casaco de jantar</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Geklede vest</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Smoking</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Smoking</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Smoking</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Smoking</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σμόκιν</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Dinner jacket</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10412">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300124707\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300124707"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Rose</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ροζ</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Roze</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ružičasta</skos:prefLabel>
    <skos:altLabel xml:lang="de">Rosa</skos:altLabel>
    <skos:altLabel xml:lang="it">Rosa</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Rosa</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Rosa</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Rosa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Rosa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="de">Pink</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Pink</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10450">
    <skos:prefLabel xml:lang="en">espadrilles</skos:prefLabel>
    <skos:prefLabel xml:lang="it">espadrilles</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Espadrile</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Espadrillos</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Espadriller</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sapatos de corda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Alpargatas</skos:prefLabel>
    <skos:altLabel xml:lang="es">Espardeñas</skos:altLabel>
    <skos:prefLabel xml:lang="de">Espadrilles</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Espadrilles</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">espadrille</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εσπαντρίγιες</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10326">
    <skos:prefLabel xml:lang="it">pubblicità</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="nl">Advertentie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Publicidade</skos:prefLabel>
    <skos:prefLabel xml:lang="en">advertisement</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Διαφήμιση</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Annons</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Oglas</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">publicité</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Anuncio publicitario</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Public notices or paid announcements, especially those in print. For announcements paid for by an advertiser and broadcast on radio or television, use ""commercials."" (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300193993\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300193993"/>
    <skos:prefLabel xml:lang="de">Werbeanzeige</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10426">
    <skos:prefLabel xml:lang="es">Peto de esquí</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Σαλοπέτα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Skijaške pantalone</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Hängselbyxor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10003"/>
    <skos:altLabel xml:lang="de">Latzhose</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Salopet</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Skioverall</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Salopette</skos:prefLabel>
    <skos:altLabel xml:lang="de">Skihose</skos:altLabel>
    <skos:altLabel xml:lang="nl">Tuinbroek</skos:altLabel>
    <skos:altLabel xml:lang="sv">Overall</skos:altLabel>
    <skos:prefLabel xml:lang="en">Salopettes</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Snickarbyxor</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10590">
    <skos:prefLabel xml:lang="es">Pluma de marabú</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Maraboeveer</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Maraboufeder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Marabu perje</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Plume de marabou</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10589"/>
    <skos:prefLabel xml:lang="sv">Maraboustorkfjäder</skos:prefLabel>
    <skos:prefLabel xml:lang="en">marabou feather</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/Techniques">
    <skos:prefLabel xml:lang="en">Techniques</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10209">
    <skos:prefLabel xml:lang="nl">Hoofddoeken</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pañuelo de cabeza (forma cuadrada)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Kopftuch</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Marama za glavu</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Lenço de cabeça</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μαντίλι κεφαλιού</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Headcloths worn instead of hats for warmth or concealment of the head or face, either where the length of the scarf exceeds the width, or a square scarf that is folded in a triangle and tied under the chin. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300256716\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300256716"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">foulard</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10208"/>
    <skos:prefLabel xml:lang="it">Foulard per la testa</skos:prefLabel>
    <skos:prefLabel xml:lang="en">headscarve</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Scarves</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10136">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Borsa a sacchetto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Chatelainetasche</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Small bags suspended from a belt or waistband; popular from the mid- to the late 19th century. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="en">chatelaine bag</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216054\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216054"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:altLabel xml:lang="fr">sac châtelaine</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">chatelaines</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">"Kjolväska, ridicule"</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Bolsa de mão (usualmente bordada)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bolso chatelaine</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τσαντάκι μέσης</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Chatelainetas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Šatlen tašna</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10346">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Materijali</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Materialen</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Matériaux</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Material</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The Materials hierarchy contains descriptors for a broad range of substances, from natural and synthetic raw materials to material products. Material products are included here rather than in the Objects facet because they can be used in the construction of various objects (e.g., ""plank"" for floors or walls), and because they are not necessary constituent parts of objects (e.g., ""shingle"" is not essential to roofs in the same way as roof ridges or eaves). (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Materiais</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Materialien</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300010357\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300010357"/>
    <skos:prefLabel xml:lang="el">Υλικά</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Materiales</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Materiali</skos:prefLabel>
    <skos:prefLabel xml:lang="en">materials</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10446">
    <skos:prefLabel xml:lang="pt">Pente</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Peineta</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10185"/>
    <skos:prefLabel xml:lang="sr">Češalj</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">peigne</skos:prefLabel>
    <skos:prefLabel xml:lang="en">comb</skos:prefLabel>
    <skos:altLabel xml:lang="de">Zierkamm</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Χτενάκι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Kam</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kam</skos:prefLabel>
    <skos:altLabel xml:lang="es">Peine</skos:altLabel>
    <skos:prefLabel xml:lang="de">Kamm</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10470">
    <skos:prefLabel xml:lang="nl">Zilverdraad</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ασήμι</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10521"/>
    <skos:prefLabel xml:lang="pt">Prata</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Srebro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Silvertråd</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">fil d'argent</skos:prefLabel>
    <skos:altLabel xml:lang="de">Silbergespinstfaden</skos:altLabel>
    <skos:prefLabel xml:lang="es">Hilo de plata</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Silver thread</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Silberfaden</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10249">
    <skos:prefLabel xml:lang="sv">Skodon</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzado</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoeisel</skos:prefLabel>
    <skos:prefLabel xml:lang="it">calzature</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Coverings for the feet that sometimes extend above the ankle; generally made of durable materials. Distinguished from ""hosiery,"" which is primarily leg coverings. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Υποδήματα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209280\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209280"/>
    <skos:prefLabel xml:lang="sr">Obuća</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
    <skos:prefLabel xml:lang="en">footwear</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Calçado</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fußbekleidung</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10239">
    <skos:prefLabel xml:lang="sv">Obi (Kimonoskärp)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Obi</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10235"/>
    <skos:prefLabel xml:lang="el">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Obi</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Obi pojas</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Obi (Kimono)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Sashes of varying widths and sometimes stiffened, tied around the waist in intricate patterns of bows and knots usually at the back, but also in front, worn over a kimono; also, similar sashes worn for fashionable dress. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Obi's</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216871\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216871"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">obi</skos:prefLabel>
    <skos:prefLabel xml:lang="en">obi</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10465">
    <skos:prefLabel xml:lang="el">Φθινόπωρο/Χειμώνας</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Otoño-invierno</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Automne-Hiver</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Herbst-Winter</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Autunno -Inverno</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Herfst-Winter</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Höst-Vinter</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Jesen-zima</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <skos:prefLabel xml:lang="en">Autumn-Winter</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Outono-Inverno</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10432">
    <skos:prefLabel xml:lang="en">Fur coat</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Abrigo de piel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Bontjas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Casaco de pêlo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Manteau de fourrure</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Γούνα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Päls</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="sr">Bunda</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pelzmantel</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10126">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">tenue de chasse</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Jaktkläder</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário de caça</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Jachtkledij</skos:altLabel>
    <skos:prefLabel xml:lang="es">Ropa de caza</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:prefLabel xml:lang="it">Abbigliamento da caccia</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Lovačka odeća</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα κυνηγιού</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jachtkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jagdkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Hunting / Shootingwear</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10484">
    <skos:prefLabel xml:lang="el">Βισκόζη</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Viskose</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Viscosa</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Viskos</skos:prefLabel>
    <skos:prefLabel xml:lang="en">viscose</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Viscose</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Viscose</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Viscose</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Viskoza</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10360"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10190">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μπερέ</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Baskenmütze</skos:prefLabel>
    <skos:prefLabel xml:lang="en">beret</skos:prefLabel>
    <skos:altLabel xml:lang="de">Barett</skos:altLabel>
    <skos:altLabel xml:lang="nl">Alpinomuts</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Boina</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Boina</skos:prefLabel>
    <skos:altLabel xml:lang="es">Gorro francés</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Baretten</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft, flat-topped, visorless caps with a tight-fitting headband; in a military context, worn as an official item of headgear. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Basco</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046091\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046091"/>
    <skos:prefLabel xml:lang="sr">Beretka</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">béret</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Baskrar</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10329">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="de">Dokumentation</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Conjunto de documentos</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">jeu de document</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Fonti/Insieme di documenti</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dokumentacija</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="it">Documento varia natura</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Dokument set</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Juego de documentos</skos:prefLabel>
    <skos:prefLabel xml:lang="en">document set</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Documentenset</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Έγγραφα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10463">
    <skos:prefLabel xml:lang="it">schizzo</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Esquisso</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300015617\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300015617"/>
    <skos:prefLabel xml:lang="sv">Skiss</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Skica</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">croquis</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Esquiço</skos:altLabel>
    <skos:prefLabel xml:lang="es">Figurín</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Skizze</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σκίτσο</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10307"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">sketch</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schets</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Esboço</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10339">
    <skos:prefLabel xml:lang="de">Performancekunst</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Installation</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Arte de performance</skos:prefLabel>
    <skos:prefLabel xml:lang="it">installazione</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Umetnost performansa</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Performance art</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Performances (kunst)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="en">Performance art</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Refers to works of art that unfold over time and that combine elements of theater and object-oriented art. (AAT)</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300121445\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300121445"/>
    <skos:prefLabel xml:lang="fr">performance artistique</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Arte performativa</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Συνέδριο</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10413">
    <skos:prefLabel xml:lang="sv">Lila</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:altLabel xml:lang="fr">pourpre</skos:altLabel>
    <skos:altLabel xml:lang="de">Violett</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Ljubičasta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="de">Purpur</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Púrpura</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Viola</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300130257\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300130257"/>
    <skos:prefLabel xml:lang="es">Violeta</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Paars</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">violet</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Purple</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Roxo</skos:altLabel>
    <skos:altLabel xml:lang="it">Viola</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μοβ</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10277">
    <skos:prefLabel xml:lang="en">non-digital media</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Medios no digitales</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">media non numérique</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Analog media</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Meios de comunicação não digitais</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Αναλογικά μέσα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10301"/>
    <skos:prefLabel xml:lang="de">analoge Medien</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Niet-digitale media</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Analogni medij</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10531">
    <skos:prefLabel xml:lang="es">Pelo</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Poil</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kosa</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011814\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011814"/>
    <skos:prefLabel xml:lang="sv">Hår</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">hair</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10530"/>
    <skos:prefLabel xml:lang="nl">Haar</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Haar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10423">
    <skos:prefLabel xml:lang="el">Στένσιλ</skos:prefLabel>
    <skos:altLabel xml:lang="fr">dessin au pochoir</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="pt">Estampilha</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10399"/>
    <skos:altLabel xml:lang="pt">Stencil</skos:altLabel>
    <skos:altLabel xml:lang="nl">Stencil</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Šablon</skos:prefLabel>
    <skos:prefLabel xml:lang="en">stencil</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sjabloon</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Screentryck</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Estarcido</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schablone</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pochoir</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10511">
    <skos:prefLabel xml:lang="es">Alforjas</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sacoche</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sadelväska</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Zadeltas</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bisage</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Satteltasche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300237746\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300237746"/>
    <skos:prefLabel xml:lang="en">Saddlebag</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:altLabel xml:lang="sr">Torbe za bicikl</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10110">
    <skos:altLabel xml:lang="fr">Vêtements professionnels</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10108"/>
    <skos:prefLabel xml:lang="nl">Werkkleding</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Roupa ocupacional</skos:altLabel>
    <skos:prefLabel xml:lang="en">Professional wear</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Profesionalna odeća</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">vêtement professionnel</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria profesional</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Yrkesdräkt</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupa de trabalho</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da lavoro</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Professionele kleding</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Επαγγελματικό ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Berufskleidung</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10139">
    <skos:altLabel xml:lang="de">Beutel</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">pochette</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216739\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216739"/>
    <skos:prefLabel xml:lang="es">Faltriquera (accesorio)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:scopeNote xml:lang="en">"Flat pouches of fabric with a bound slit in the outer surface, which is worn suspended from ties which fasten around the waist. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Bolsos (acessórios de traje)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Piccola borsa a tasca</skos:prefLabel>
    <skos:prefLabel xml:lang="en">pockets (costume accessories)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fickor</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Zak</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Τσαντάκι</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Džep-tašna</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Buidel</skos:altLabel>
    <skos:altLabel xml:lang="sv">Kjolsäckar</skos:altLabel>
    <skos:prefLabel xml:lang="de">Tasche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10454">
    <skos:prefLabel xml:lang="en">children's footwear</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kinderschuh</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kinderfußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Kinderschoenen</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures  enfant</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Calzado infantil</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10451"/>
    <skos:prefLabel xml:lang="sv">Barnskor</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υποδήματα παιδικά</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">calzature da bambino</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Calçado infantil</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Dečija obuća</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10473">
    <skos:prefLabel xml:lang="nl">Glasvezel</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibra de vidrio</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Glass fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υαλοβάμβακας</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10348"/>
    <skos:prefLabel xml:lang="pt">Fibra de vidro</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300183781\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300183781"/>
    <skos:prefLabel xml:lang="de">Glasfaser</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Staklena vlakna</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibre de verre</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Glasfiber</skos:prefLabel>
    <skos:altLabel xml:lang="en">Fiberglass</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10443">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">étui à cigarettes</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="sv">Cigarettetuier</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pitillera</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cigarreira</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">porte-cigarettes</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τσιγαροθήκη</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cigarette case</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zigarettenetui</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sigarettenetui</skos:prefLabel>
    <skos:altLabel xml:lang="el">Ταμπακιέρα</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Tabakera</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10349">
    <skos:prefLabel xml:lang="sv">Guldtråd</skos:prefLabel>
    <skos:prefLabel xml:lang="en">gold thread</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Hilo de oro</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Zlato</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Goldfaden</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ouro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="el">Χρυσός</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fil d'or</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Gouddraad</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10521"/>
    <skos:altLabel xml:lang="de">Goldgespinstfaden</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10276">
    <skos:prefLabel xml:lang="pt">Ferramentas para modelação têxtil</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Tools for textile shaping</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Verktyg för textil tillverkning</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Herramientas de fabricación textil</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">outil pour la mise en forme des textiles</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10274"/>
    <skos:prefLabel xml:lang="nl">Gereedschap en  machines voor het maken van textiel</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Alat za oblikovanje tekstila</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εργαλεία ραπτικής</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Werkzeuge der Textilgestaltung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10359">
    <skos:prefLabel xml:lang="es">Fibras artificiales</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Artificiella fibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="en">artificial fibres</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Artificiële vezels</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10358"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="el">Τεχνητές ύλες</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Veštačka vlakna</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kunstmatige vezels</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibras artificiais</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kunstfaser</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres artificielles</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10125">
    <skos:altLabel xml:lang="fr">Tenue de cyclisme</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:prefLabel xml:lang="fr">tenue de cycliste</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Cyclingwear</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário de ciclismo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Fietskleding</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα ποδηλασίας</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da ciclista</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Cykelkläder</skos:prefLabel>
    <skos:altLabel xml:lang="de">Radfahrkleidung</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Biciklistička odeća</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Fietskledij</skos:altLabel>
    <skos:prefLabel xml:lang="de">Radkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria de ciclismo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10433">
    <skos:prefLabel xml:lang="fr">Type de costume</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">trägerspezifische Kleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kleding naar drager</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Bärare</skos:altLabel>
    <skos:prefLabel xml:lang="es">Indumentaria según usuario</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα ανάλογα με τη χρήση</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Brukare</skos:prefLabel>
    <skos:prefLabel xml:lang="en">costume by wearer</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kleidung nach Träger</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Traje por utilizador</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10001"/>
    <skos:prefLabel xml:lang="sr">Podela kostima prema rodu i uzrastu</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10483">
    <skos:prefLabel xml:lang="en">Palm fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Palmvezel</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014049\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014049"/>
    <skos:prefLabel xml:lang="fr">fibre de palmier</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Palmino vlakno</skos:prefLabel>
    <skos:altLabel xml:lang="de">Afrik</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="de">Palmfaser</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibra de palmeira</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="el">Ίνες φοίνικα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibra de palma</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Palmfiber</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10238">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10235"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Livgördlar</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210005\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210005"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Kummerbund</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cummerbund</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fajín</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">ceinture</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Cummerbunden</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pojas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ζωνάρι</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Faixa de smoking</skos:prefLabel>
    <skos:altLabel xml:lang="el">Ζώστρα</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Wide sashes worn around the waist, often horizontally pleated; worn with tuxedoes and as part of miltary uniforms. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Fascia che si annoda in vita (uomo)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10464">
    <skos:prefLabel xml:lang="el">Εποχή</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <skos:prefLabel xml:lang="fr">Saison</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Saison</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Seizoen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jahreszeit</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Temporada</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Estação</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Season</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Säsong</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Stagione</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Sezona</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10338">
    <skos:prefLabel xml:lang="nl">Modeshow</skos:prefLabel>
    <skos:prefLabel xml:lang="it">sfilata</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modna revija</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Exhibitions of clothing on live models, usually on a runway or stage, typically given by a retail store, designer, or manufacturer to promote fashion merchandise. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Modenschau</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300261696\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300261696"/>
    <skos:prefLabel xml:lang="fr">défilé de mode</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Fashion show</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Desfile de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Performance</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modevisning</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Desfile de moda</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10462">
    <skos:altLabel xml:lang="nl">Ontwerptekening</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Boceto</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modni crtež</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">dessin d'étude</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10307"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300069413\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300069413"/>
    <skos:prefLabel xml:lang="el">Σχέδιο</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Desenho técnico</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Drawings intended to work out the scheme of a project, whether the project is expected to be executed or not; more finished than sketches. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Modetekening</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modeteckning</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Entwurfszeichnung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">dessin de conception</skos:altLabel>
    <skos:prefLabel xml:lang="en">design drawing</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10414">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Colours"/>
    <skos:prefLabel xml:lang="es">Rojo</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κόκκινο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Crvena</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vermelho</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Rosso</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="it">Rosso</skos:altLabel>
    <skos:prefLabel xml:lang="en">Red</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Röd</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300126225\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300126225"/>
    <skos:prefLabel xml:lang="nl">Rood</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10400"/>
    <skos:prefLabel xml:lang="fr">Rouge</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Encarnado</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Rot</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10424">
    <skos:prefLabel xml:lang="nl">Catsuit</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10007"/>
    <skos:prefLabel xml:lang="en">Catsuit</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10530">
    <skos:prefLabel xml:lang="sr">Nevlaknasti tekstilni materijali životinjskog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Animal origin non-fibre textile materials</skos:prefLabel>
    <skos:prefLabel xml:lang="de">"nicht faserhaltige, textile Werkstoffe  tierischen Ursprungs"</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Materiales textiles de origen animal</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10524"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Niet-vezelige textielmaterialen van dierlijke oorsprong</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">"Material av animalisk härkomst, ej textila material"</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Materiau non-fibreux d'origine animale</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10278">
    <skos:prefLabel xml:lang="sv">Pressklipp</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10329"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">coupure de presse</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Revista de imprensa</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Press clipping</skos:prefLabel>
    <skos:altLabel xml:lang="fr">revue de presse</skos:altLabel>
    <skos:prefLabel xml:lang="de">Zeitungsausschnitt</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Recorte de prensa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">ritaglio di giornale</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Αποκόμματα Τύπου</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Persknipsel</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Clipping</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300026867\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300026867"/>
    <skos:prefLabel xml:lang="en">press clipping</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Illustrations, pages, articles, or columns of text removed from books, newspapers, journals, or other printed sources and kept for their informational content. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10328">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Book / portfolio</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">look book</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lookbook</skos:prefLabel>
    <skos:altLabel xml:lang="fr">catalogue de collection</skos:altLabel>
    <skos:prefLabel xml:lang="en">lookbook</skos:prefLabel>
    <skos:prefLabel xml:lang="it">lookbook</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10452">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10451"/>
    <skos:prefLabel xml:lang="it">calzature da donna</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures  femme</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Damenschuh</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Calçado feminino</skos:prefLabel>
    <skos:altLabel xml:lang="de">Damenfußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="es">Calzado femenino</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Damskor</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Damesschoenen</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Vrouwenschoen</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Ženska obuća</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υποδήματα γυναικεία</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">women's footwear</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10138">
    <skos:prefLabel xml:lang="el">Τσάντα χεριού</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Mala de mão</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bolso de mano</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Purses, usually with handles or straps, that are designed to be carried in the hand or on the arm and intended as ladies' accessories. Handbags are large enough to carry banknotes, coins, and everyday personal items. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Ručna tašna</skos:prefLabel>
    <skos:prefLabel xml:lang="en">handbag</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Handväskor</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">sac à main</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Handtas</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="it">Borsetta con manico</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300312361\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300312361"/>
    <skos:prefLabel xml:lang="de">Handtasche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10453">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10451"/>
    <skos:prefLabel xml:lang="it">calzature da uomo</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Herrskor</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mannenschoen</skos:altLabel>
    <skos:prefLabel xml:lang="en">men's footwear</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Calçado masculino</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υποδήματα ανδρικά</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures  homme</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Calzado masculino</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Muška obuća</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Herenschoenen</skos:prefLabel>
    <skos:altLabel xml:lang="de">Herrenfußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="de">Herrenschuh</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10510">
    <skos:prefLabel xml:lang="en">Ribbon</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Band</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Band</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lint</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lazo</skos:prefLabel>
    <skos:altLabel xml:lang="es">"Caídas de talle, fachas, colonias"</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Traka</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Ruban</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014668\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014668"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10472">
    <skos:prefLabel xml:lang="fr">amiante</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10348"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Asbest</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Asbest</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Asbest</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Asbestos</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αμίαντος</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Azbest</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300011071\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300011071"/>
    <skos:altLabel xml:lang="es">asbestos</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Amianto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="es">Amianto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10444">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="nl">Luciferdoosje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Kutija za šibice</skos:prefLabel>
    <skos:prefLabel xml:lang="en">matchbox</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Caja de cerillas</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Caixa de fósforos</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Luciferetui</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Tändsticksetuier</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Carteira de fósforos</skos:altLabel>
    <skos:prefLabel xml:lang="de">Streichholzschachtel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Σπιρτόκουτο</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">boîte d'allumettes</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10348">
    <skos:prefLabel xml:lang="es">Fibras de origen mineral</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μέταλλα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres d'origines minérales</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fasern mineralischen Ursprungs</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibras de origem mineral</skos:prefLabel>
    <skos:prefLabel xml:lang="en">mineral origin fibres</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Minerale vezels</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10347"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Vlakna mineralnog porekla</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Vezels van minerale oorsprong</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Mineralfibrer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10124">
    <skos:altLabel xml:lang="nl">Unform (sportkledij)</skos:altLabel>
    <skos:prefLabel xml:lang="de">Sportuniform</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Sportkläder</skos:altLabel>
    <skos:prefLabel xml:lang="es">Uniforme (indumentaria deportiva)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">Uniforms worn by sports teams and athletes. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Sportska odeća</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Uniform (sportkleding)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Uniforme (roupa de desporto)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Divisa sportiva</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:prefLabel xml:lang="en">Uniform (sports clothing)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Träningskläder</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tenue de sport</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300266714\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300266714"/>
    <skos:prefLabel xml:lang="el">Στολή αθλητική</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10482">
    <skos:prefLabel xml:lang="pt">Fibra de ananás</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300375581\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300375581"/>
    <skos:prefLabel xml:lang="de">Ananasfaser</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="en">Pineapple fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibre d'ananas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Ananasvezel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="en">Piña</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Ananasovo vlakno</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibra de piña</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Ίνες ανανά</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ananasfiber</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10434">
    <skos:prefLabel xml:lang="fr">Costume féminin</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γυναικείο ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Traje feminino</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Damenkostüm</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Dameskleding</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Ženski kostim</skos:prefLabel>
    <skos:altLabel xml:lang="de">Damenkleidung</skos:altLabel>
    <skos:prefLabel xml:lang="es">Indumentaria femenina</skos:prefLabel>
    <skos:altLabel xml:lang="fr">vêtement pour femme</skos:altLabel>
    <skos:altLabel xml:lang="de">Damenbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Damkläder</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10433"/>
    <skos:prefLabel xml:lang="en">women's costume</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10358">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="pt">Fibras químicas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras químicas</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Chemische vezels</skos:altLabel>
    <skos:prefLabel xml:lang="el">Χημικές ύλες</skos:prefLabel>
    <skos:prefLabel xml:lang="en">chemical fibres</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Regenererade fibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Hemijska vlakna</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10520"/>
    <skos:prefLabel xml:lang="fr">fibres chimiques</skos:prefLabel>
    <skos:prefLabel xml:lang="de">chemische Fasern</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Synthetische vezels</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10237">
    <skos:scopeNote xml:lang="en">"Appendages in the form of a flap or bag worn at the position of the genitals on close-fitting hose or concealing an opening in the front of men's breeches or armor, worn by men from the 15th to the 17th century; often conspicuous and ornamented. Also, similar appendages of female attire, worn on the breast. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046148\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046148"/>
    <skos:prefLabel xml:lang="es">Bragueta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">braguette</skos:altLabel>
    <skos:prefLabel xml:lang="el">Προστατευτικό γεννητικών οργάνων</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Blygdkapslar</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10235"/>
    <skos:prefLabel xml:lang="en">codpiece</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schamkapsel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Braguette</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Brachetta(uomo)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Suspenzor</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Coquilha</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">cache sexe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Broekklep</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10357">
    <skos:altLabel xml:lang="fr">fibres d'algues</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Vlakno od morskih algi</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10357"/>
    <skos:prefLabel xml:lang="es">Fibra de algas</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sjögräsfibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Alginatfaser</skos:prefLabel>
    <skos:prefLabel xml:lang="en">seaweed fibre</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres marines</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Zeewiervezels</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fibras de algas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Φύκια</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10187">
    <skos:prefLabel xml:lang="es">Peinado</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kapsels</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Frisur</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χτένισμα</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hairstyle</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300262903\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300262903"/>
    <skos:prefLabel xml:lang="pt">Penteado</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">coiffure</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Frizura</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"The style, form, and arrangement of human hair, often enhanced by adding materials or substances to the hair. Hairstyles may have social and religious significance as well as aesthetic and artistic qualities. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Frisyrer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Kapsel</skos:altLabel>
    <skos:prefLabel xml:lang="it">Acconciatura</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10236">
    <skos:prefLabel xml:lang="sv">Gehäng</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Ornamental chains, pins, or clasps usually worn at a woman's waist, to which trinkets, keys, purses, or other articles are attached. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">"Castelã  (cinto decorativo com correntes para pendurar tesoura, chaves, espelho, etc)"</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="sv">Bälteskedja</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Šatlen tašna</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">châtelaine</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209297\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209297"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Chatelaine</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chatelaine</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Chatelaine</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10235"/>
    <skos:prefLabel xml:lang="el">Κρεματζούλια μέσης</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="it">Cintura con catenelle portachiavi</skos:altLabel>
    <skos:prefLabel xml:lang="it">Chatelaines</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Chatelaines</skos:prefLabel>
    <skos:prefLabel xml:lang="en">chatelaine</skos:prefLabel>
    <skos:altLabel xml:lang="de">Châtelaine</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10390">
    <skos:scopeNote xml:lang="en">"Refers to the technique of knotting cords or thick threads to create a coarse lace or decorative fringe. It is generally performed by using a padded cushion mounted on a wooden frame to secure the knot-bearing supporting cords or the hem of a piece of fabric. From the hem or a line of knot-bearing cords, long threads or cords are mounted by knots, and the long threads are then tied to each other with a variety of knots to form patterns or designs. Pins may be used to secure threads to the cushion while the piece is being worked. The technique was developed in 19th-century Genoa, Italy, but the term is derived from the Turkish word for ""towel,"" because the process was inspired by decorative fringes on Turkish towels and other fabrics. It is also based on the techniques used by 16th-century Italian artisans in the creation of ""punto a groppo."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Makrame</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="pt">Macramé</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Macramé</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Macramé</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10385"/>
    <skos:prefLabel xml:lang="sv">Makramé</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Makramé</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">macramé</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053636\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053636"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">macrame</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μακραμέ</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10656">
    <skos:prefLabel xml:lang="en">Saddle shoes</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapato bicolor (masculino)</skos:prefLabel>
    <skos:altLabel xml:lang="de">zweifarbige Schuhe</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Derbies bicolore</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Sedlo cipele</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:altLabel xml:lang="de">Al Capone Schuhe</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Snörskor</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Saddle Shoes</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παπούτσια</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10246">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210014\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210014"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Muf</skos:prefLabel>
    <skos:prefLabel xml:lang="en">muff</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Moffen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Muff</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Manicotti</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Muffar</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10242"/>
    <skos:prefLabel xml:lang="el">Μανσόν</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Regalo</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Tubular coverings for the hands, often open at both ends, worn for warmth. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Manguito</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">manchon</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10666">
    <skos:prefLabel xml:lang="it">Installazione</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Installatie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10302"/>
    <skos:prefLabel xml:lang="en">Installation</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10380">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300227915\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300227915"/>
    <skos:prefLabel xml:lang="el">Δίμιτο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10370"/>
    <skos:scopeNote xml:lang="en">"Refers to a woven textile characterized by parallel diagonal ridges or ribs, produced by passing the weft threads over one and under two or more threads of the warp, instead of over and under in regular succession, as in plain weaving. Regular twill features a diagonal line that is repeated regularly, usually running from the left to right at a 45-degree angle and upward. The weave may be varied in several ways, including changing the angle or direction of the twill line, as exemplified in herringbone twill. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">twill</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sarja</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Tvil</skos:altLabel>
    <skos:prefLabel xml:lang="de">Köper</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Keperbinding</skos:prefLabel>
    <skos:altLabel xml:lang="sv">twill</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:altLabel xml:lang="fr">twill</skos:altLabel>
    <skos:prefLabel xml:lang="fr">serge</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Keperweefsel</skos:altLabel>
    <skos:prefLabel xml:lang="es">Sarga</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kypert</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Keper</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10265">
    <skos:prefLabel xml:lang="el">Μπότες ψηλές</skos:prefLabel>
    <skos:prefLabel xml:lang="en">thigh boots</skos:prefLabel>
    <skos:altLabel xml:lang="de">Überkniestiefel</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Lårhöga stövlar</skos:prefLabel>
    <skos:prefLabel xml:lang="it">cosciali</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botas de cano alto e justo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:prefLabel xml:lang="fr">cuissardes</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Overknee-Stiefel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Dijlaarzen</skos:prefLabel>
    <skos:altLabel xml:lang="de">Überknieschaftstiefel</skos:altLabel>
    <skos:prefLabel xml:lang="es">Botas altas</skos:prefLabel>
    <skos:altLabel xml:lang="de">Overknees</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Čizme iznad kolena</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10343">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="en">showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Showroom (sala de exposição)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Showroom</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εμπορική έκθεση</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">show room</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10456">
    <skos:prefLabel xml:lang="nl">Gereedschap voor textieldecoratie</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">outil pour l'ornement textile</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10274"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Herramientas de decoración textil</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Alat za ukrašavanje tekstila</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Verktyg för textil dekor</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ferramentas para decoração têxtil</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εργαλεία διακόσμησης υφάσματος</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Tools for textile decorating</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Werkzeuge der Textildekoration</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10360">
    <skos:prefLabel xml:lang="el">Κυταρρικές ύλες</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Cellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres cellulosique</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Celulozna vlakna</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras celulósicas</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10359"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Celulósicas</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zellulose</skos:prefLabel>
    <skos:prefLabel xml:lang="en">cellulosic</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Cellulosa</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10342">
    <skos:prefLabel xml:lang="en">award</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Premio</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αίθουσα επιδείξεων μόδας</skos:prefLabel>
    <skos:altLabel xml:lang="es">Galardón</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Award</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Prijs (beloning)</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300026842\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300026842"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Prémio</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Nagrada</skos:prefLabel>
    <skos:prefLabel xml:lang="it">premio</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Honors conferred or bestowed, usually including a document or token indicating or symbolizing the award, or remuneration. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Auszeichnung</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="sv">Utmärkelse</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">prix</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10266">
    <skos:altLabel xml:lang="de">Langschaftstiefel</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μπότες</skos:prefLabel>
    <skos:prefLabel xml:lang="it">stivale di vitello</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:prefLabel xml:lang="pt">Botas de cano médio</skos:prefLabel>
    <skos:altLabel xml:lang="de">wadenhoher Stiefel</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Kuitlaarzen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Vadhöga stövlar</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bottes à hauteur de mollet</skos:prefLabel>
    <skos:prefLabel xml:lang="de">halbhoher Stiefel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">calf boots</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kratke čizme</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10316">
    <skos:prefLabel xml:lang="fr">blog</skos:prefLabel>
    <skos:prefLabel xml:lang="en">blog</skos:prefLabel>
    <skos:prefLabel xml:lang="it">blog</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Blogg</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Blog</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Blog</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Blog</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Blog</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Blog</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Blog</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300265722\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300265722"/>
    <skos:scopeNote xml:lang="en">"Short for 'Web logs' or 'weblogs:' a Web site that functions as an online journal or diary, most often maintained by a single person, but also by groups with common interests. Commentary, images, sound, or video files are posted regularly with the most recent entry appearing first. Blogs may focus on a particular area of interest and contain links to content on external Web pages. Blogs usually have archives of all past entries, with links between similar items of interest. They originated in the United States in 1997 as a few online journals, and became popular ca. 2002, when software designed specifically for creating and maintaining blogs was introduced. For files storing detailed requests from Web servers use 'Web logs.' (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10440">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">baleine (corset)</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Kombinezon</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Ballenas</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Korsett des 18. Jahrhunderts</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="pt">Corpete</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Korsett</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κορσές</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Liv</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Posuklnja</skos:prefLabel>
    <skos:prefLabel xml:lang="en">stays</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schnürbrust</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">keurslijfje</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10370">
    <skos:prefLabel xml:lang="sv">Vävtekniker</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Webtechniken</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300231684\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300231684"/>
    <skos:prefLabel xml:lang="fr">Techniques de tissage</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Técnicas de tecelagem</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10366"/>
    <skos:prefLabel xml:lang="el">Τεχνικές ύφανσης</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tehnike tkanja</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="es">Técnicas de tejido</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Weeftechnieken</skos:prefLabel>
    <skos:prefLabel xml:lang="en">weaving techniques</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10302">
    <skos:prefLabel xml:lang="sv">Konstverk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Obra de arte</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Obra de arte</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">artwork</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="el">Έργο τέχνης</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Works of art in any medium, including performance art. A work of art may exist as a part of a larger object, e.g., a mural painting or a painting on a piece of furniture. When referring to the study or practice of the fine arts or the fine and decorative arts together, use ""art."" In reference to pieces of fine or decorative arts as collectables rather than museum objects, in English use either ""art objects"" or the French term ""objets d'art,"" which emphasizes this meaning. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">illustration</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Umetničko delo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kunstwerk</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300133025\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300133025"/>
    <skos:prefLabel xml:lang="nl">Kunstwerk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">Œuvre d'art</skos:altLabel>
    <skos:prefLabel xml:lang="it">opera d'arte</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10256">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046072\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046072"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Mocasines</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Loafers</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Loafers</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">loafers</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Low-heeled, slip-on shoes of adapted moccasin-type construction with a slotted strap stitched to the vamp. Also, similar shoes decorated on the vamp with a metal chain or tied tassels. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">mocassini</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="de">Loafer</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Loafer</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sapatos de viagem</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Cipele na navlačenje</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παντόφλες κλειστές</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10188">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Hoofddeksel</skos:altLabel>
    <skos:prefLabel xml:lang="it">Copricapi</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">headgear</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Oglavlja</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kopfbedeckung</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Huvudbonader</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209285\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209285"/>
    <skos:scopeNote xml:lang="en">Any covering for the head. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">couvre chef</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Protector de cabeça</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hoofddeksels</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κάλυμμα κεφαλιού</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:prefLabel xml:lang="es">Accesorio para la cabeza</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10254">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300225924\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300225924"/>
    <skos:prefLabel xml:lang="it">scarpe da ginnastica</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Sportschuhe</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Shoes designed to be worn for sports. (AAT)</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="en">sport shoes</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Sportska obuća</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Αθλητικά παπούτσια</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sportskor</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures de sport</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sportschoenen</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapatillas de deporte</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sapatilhas</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Ténis</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10356">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Vlakna od morskih algi</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres à base d'algues</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras algínidas</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Fibrer från sjögräs</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Vezels uit zeewier</skos:prefLabel>
    <skos:prefLabel xml:lang="de">algenbasierte Fasern</skos:prefLabel>
    <skos:prefLabel xml:lang="en">seaweed-origin fibres</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Fibras de origem em algas marinhas</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υλες θαλάσσιας προέλευσης</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10347"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10655">
    <skos:prefLabel xml:lang="sv">Stilettskor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="el">Γόβες στιλέτο</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schuhe mit Bleistiftabsätzen</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Stileto</skos:prefLabel>
    <skos:altLabel xml:lang="es">Stilettos</skos:altLabel>
    <skos:altLabel xml:lang="de">Stilettos</skos:altLabel>
    <skos:prefLabel xml:lang="es">Zapatos de tacón alto</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Stilettos</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Stiletto (ou escarpin à talons) aiguilles</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stöckelschuhe</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10235">
    <skos:prefLabel xml:lang="en">accessories worn at the waist or below</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires portées à la taille  et en dessous</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="it">Accessori indossati dalla vita in giù</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Αξεσουάρ μέσης και κάτω</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorios de cintura y de cintura para abajo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Accessoires gedragen in of onder het middel</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi na struku ili ispod struka</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Acessórios usados na cintura ou abaixo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Accessoires in oder unterhalb der Taille zu tragen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Accessoirer burna under midjan</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209283\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209283"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10469">
    <skos:prefLabel xml:lang="sv">Herrkonfektion</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mannenmode</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Herenmode</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário masculino</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Mode masculine</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Herrenkleidung</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <skos:prefLabel xml:lang="en">Menswear</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Muška odeća</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Moda hombre</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ανδρικό ένδυμα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10245">
    <skos:prefLabel xml:lang="es">Mitones</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210013\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210013"/>
    <skos:prefLabel xml:lang="en">mitts (fingerless handwear)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Γάντια με μισά δάχτυλα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">moufles</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Coverings for the hand which leave the fingers uncovered, especially those which extend to the elbow or above and which are made of lace, net, or the like. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="de">Mitaines</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Torgvantar</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Rukavice bez prstiju</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Mitenes (luvas sem dedos)</skos:prefLabel>
    <skos:altLabel xml:lang="de">auch fingerlose Handschuhe</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Mitaines</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10242"/>
    <skos:prefLabel xml:lang="it">Guanti senza dita</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Halbhandschuhe</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10442">
    <skos:prefLabel xml:lang="de">Opernglas</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Pozorišni dvogled</skos:altLabel>
    <skos:prefLabel xml:lang="en">opera glasses</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Κυάλια όπερας</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Teaterkikare</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Operski dvogled</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="es">Binoculares</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Theaterkijker</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Face à main</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">jumelles de théâtre</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Binóculos de ópera</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10455">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Calzado unisex</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">unisex footwear</skos:prefLabel>
    <skos:altLabel xml:lang="de">Unisex-Fußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Calçado unissexo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Unisex schoenen</skos:prefLabel>
    <skos:altLabel xml:lang="de">nicht geschlechtsspezifische Fußbekleidung</skos:altLabel>
    <skos:prefLabel xml:lang="it">calzature unisex</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10451"/>
    <skos:prefLabel xml:lang="fr">chaussures mixte</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Unisexskor</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υποδήματα unisex</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Uniseks obuća</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Unisex Schuh</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10665">
    <skos:scopeNote xml:lang="en">"Cheesecloth bags created to hold Fuller’s Earth. Allows for a costumer to dust the shoulders, clothes and boots of the actor. Fuller’s Earth labels reflect the landscape such as Monument Valley Red, often used in Westerns (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10456"/>
    <skos:prefLabel xml:lang="en">Ponce bags</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10669">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10329"/>
    <skos:prefLabel xml:lang="en">Costume bible</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"A volume of collected research including photographs, sketches, hair and makeup notes, inspiration, memoirs, fabric and color swatches. It may be shared with the director, production designer, actors and the hair and makeup artists. Some designers create websites as with research online, available to all collaborators and actors as needed (UCLA)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Costume plot</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"The costume changes for every single character in every scene, including day, week, year and seasonal changes in the script in addition to action scenes in which multiple costumes will be needed. The budget is based on the type and number of costumes needed (UCLA)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">Costume breakdown</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Each character’s costume is broken down by scene and numbered chronologically. Costume change numbers become all the more complicated during long chase scenes and action sequences when there are multiple units shooting and when there are special effects (UCLA)</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10264">
    <skos:altLabel xml:lang="fr">cuissardes</skos:altLabel>
    <skos:prefLabel xml:lang="de">Watstiefel</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210032\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210032"/>
    <skos:prefLabel xml:lang="fr">bonne montante</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vadarstövlar</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botas de pescador</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:scopeNote xml:lang="en">"Boots reaching to the hips, worn especially by fishermen. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">hip boots</skos:prefLabel>
    <skos:prefLabel xml:lang="it">stivali da pescatore</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Ribolovačke čizme</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπότες ψηλές</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botas de pescar</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Lieslaarzen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10341">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">web cast</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Webcast</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Webcast</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Transmisión web</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">emission sur le Web</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Webbsändning</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">trasmissione web</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Transmissão via internet</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Webcast</skos:altLabel>
    <skos:prefLabel xml:lang="el">Βραβείο</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Web kasting</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10441">
    <skos:prefLabel xml:lang="pt">Cartão de dança</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dansprogram</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Cartão de baile</skos:altLabel>
    <skos:prefLabel xml:lang="es">Carnet de baile</skos:prefLabel>
    <skos:prefLabel xml:lang="en">dance card</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Dance card</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">programme de bal</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="sr">Red igara</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tanzkarte</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Balboekje</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10317">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Tvit</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tweet</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
    <skos:prefLabel xml:lang="en">tweet</skos:prefLabel>
    <skos:prefLabel xml:lang="it">tweet</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10303">
    <skos:prefLabel xml:lang="sr">Modna fotografija</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fotografía de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φωτογραφία μόδας</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">Photographs made to sell clothing and accessories or show them to advantage. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Modefoto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300226299\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300226299"/>
    <skos:prefLabel xml:lang="pt">Fotografia de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modefotografi</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">photographie de mode</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:altLabel xml:lang="fr">photo  de mode</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Modefotografie</skos:prefLabel>
    <skos:prefLabel xml:lang="it">servizio fotografico</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fashion photograph</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10255">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046083\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046083"/>
    <skos:prefLabel xml:lang="en">slippers</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">pantoufles</skos:altLabel>
    <skos:prefLabel xml:lang="sr">"Obuća za kuću, papuče"</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pantoffels</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapatillas de deporte</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="el">Παντόφλες</skos:prefLabel>
    <skos:prefLabel xml:lang="it">pantofole chiuse</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Slipper</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="pt">Pantufas</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Chinelos</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Light footwear made for indoor wear, generally without means of fastening and made to be easily slipped on and off the foot. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Tofflor</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussons</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10189">
    <skos:altLabel xml:lang="pt">Boné</skos:altLabel>
    <skos:prefLabel xml:lang="en">caps (headgear)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καπέλο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Pet en muts</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Mössor</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schirmmütze</skos:altLabel>
    <skos:altLabel xml:lang="fr">bonnets</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Brimless head coverings, usually made with a visor. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046094\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046094"/>
    <skos:prefLabel xml:lang="fr">casquette</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">"Mutsen(AAT), Petten (AAT), Kapjes (CMU)"</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gorras</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Gorro / touca</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Kapa</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Barrete</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Berretti</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kappen (Kopfbedeckung)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10468">
    <skos:prefLabel xml:lang="sr">Visoka moda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Alta costura</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Haute Couture</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Haute Couture</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Haute Couture</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Haute Couture</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Haute couture</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υψηλή ραπτική</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Alta-Costura</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10392">
    <skos:prefLabel xml:lang="es">Fabricación de alfombras</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10385"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">Teppichknüpferei</skos:prefLabel>
    <skos:prefLabel xml:lang="en">rug-making</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Tapeçaria (tecelagem)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Ćilimarstvo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Knuten matta</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κατασκευή χαλιών</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tapijtknopen</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fabrication des tapis</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053657\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053657"/>
    <skos:altLabel xml:lang="nl">Tapijtmaken</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10234">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10005"/>
    <skos:prefLabel xml:lang="en">Jumper (dress)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10253">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210043\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210043"/>
    <skos:prefLabel xml:lang="fr">Escarpin</skos:prefLabel>
    <skos:altLabel xml:lang="en">Court shoe</skos:altLabel>
    <skos:prefLabel xml:lang="es">Zapato de salón</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="pt">Pumps</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pumps</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pumps</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pumps</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Salonske cipele</skos:prefLabel>
    <skos:prefLabel xml:lang="it">decolletè</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Sapatos de salto alto</skos:altLabel>
    <skos:prefLabel xml:lang="en">pumps</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Slip-on shoes with low-cut, rounded or V-shaped throat, and usually a medium to high heel; sometimes made with open toe or open heel in sling-back style. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Γόβες</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10654">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Salomé</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="el">Παπούτσια με μπαρέτα</skos:prefLabel>
    <skos:prefLabel xml:lang="en">T-strap shoes</skos:prefLabel>
    <skos:altLabel xml:lang="de">T-Strap-Schuhe</skos:altLabel>
    <skos:prefLabel xml:lang="sr">T cipele</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Stegspangenschuhe</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapatos de pulsera</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">T-slejfskor</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10129">
    <skos:prefLabel xml:lang="fr">tenue de ski</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Skikleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Odeća za skijanje</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Skidkläder</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Traje de esquí</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Fato de ski</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210460\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210460"/>
    <skos:altLabel xml:lang="fr">costume de ski</skos:altLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da sci</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Στολή σκι</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Ski suit</skos:prefLabel>
    <skos:altLabel xml:lang="de">Skianzug</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Skipak</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"One- or two-piece costumes consisting of close-fitting trousers and a jacket made of a warm, lightweight, weather-resistant fabric. Sometimes made as an ensemble with a matching sweater, hat or cap, gloves, or other accessories. (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10355">
    <skos:prefLabel xml:lang="el">Λινάρι</skos:prefLabel>
    <skos:prefLabel xml:lang="en">linen</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Leinen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="nl">Linnen</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">lin</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014069\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014069"/>
    <skos:scopeNote xml:lang="en">General name for textile woven from the spun fiber of the flax plant. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Lino</skos:prefLabel>
    <skos:altLabel xml:lang="es">"Estopa, hilo, lienzo"</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Lan</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Lin</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Linho</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10345">
    <skos:prefLabel xml:lang="pt">Semana de moda</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Fashion week</skos:altLabel>
    <skos:altLabel xml:lang="nl">Modeweek</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Modevecka</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Fashion week</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nedelja mode</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">semaine de la mode</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Διαγωνισμός</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fashion week</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modewoche</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Semana de la Moda</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="it">settimana della moda</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10041">
    <skos:prefLabel xml:lang="en">Cloak</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Capa comprida</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Cape</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ogrtač</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046142\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046142"/>
    <skos:altLabel xml:lang="es">Manteo</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Grande cape</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">Sleeveless outer garments which fasten at the neck and fall loosely from the shoulders to cover the entire body; may have a yoke or some shaping from the neck to the shoulders. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Manto</skos:altLabel>
    <skos:prefLabel xml:lang="es">Capa</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μανδύας</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="it">Mantello</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Umhang</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Slängkappa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10148">
    <skos:prefLabel xml:lang="nl">Boekethouders</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">bouquet holder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Suporte de bouquet</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Držač za buket</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Porta bouquet</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Buketthållare</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:altLabel xml:lang="nl">Filigrainhoudertje</skos:altLabel>
    <skos:prefLabel xml:lang="de">Bouquethalter</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Θήκη για μπουκέτο λουλουδιών</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">porte-bouquet</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Supporto per bouquet</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209969\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209969"/>
    <skos:scopeNote xml:lang="en">"Cone- or funnel-shaped decorative accessories for holding flowers in the hand, often with an attached finger ring; may also be pinned or hooked to a garment. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Suporte de buquê</skos:prefLabel>
    <skos:altLabel xml:lang="de">Porte-bouquet</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10199">
    <skos:prefLabel xml:lang="sv">Pannband</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bandeau</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kopfband</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Hoofdbanden</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπαντό</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Traka za glavu</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046115\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046115"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:scopeNote xml:lang="en">"Bands worn around the head, made of cloth, leather, metal, or various other materials, typically worn to keep the hair in place, for decorative purposes, or as a sign of status or rank. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Fita de cabelo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Cinta de cabeza</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Stirnband</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cerchietto</skos:prefLabel>
    <skos:prefLabel xml:lang="en">headband</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10248">
    <skos:prefLabel xml:lang="es">Pantorrillera</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pantorrilha</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">faux-mollet (18e)</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Panturrilha</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Vadförstorare</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10247"/>
    <skos:prefLabel xml:lang="el">downy calves</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Lösvader</skos:altLabel>
    <skos:altLabel xml:lang="de">faux mollets</skos:altLabel>
    <skos:prefLabel xml:lang="en">downy calves</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221719\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221719"/>
    <skos:prefLabel xml:lang="sr">"""Lažni listovi"""</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Wadenpolster</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Pads woven into the calves of stockings to exaggerate and produce manly looking calves; patented in 1788. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="nl">Ingeweven kuitkussentje</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Downy calves</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10668">
    <skos:scopeNote xml:lang="en">The collected notes kept by the script supervisor of each and every shot of every scene in the film. Films are not shot in chronological order. Continuity is a vital tool in post-production for the film editor who will assemble these thousands of pieces of footage (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10321"/>
    <skos:prefLabel xml:lang="en">Continuity book</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10458">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Kolaž</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Collage</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Collage</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300033963\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300033963"/>
    <skos:prefLabel xml:lang="es">Collage</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Collage</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Collage</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Collage</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:altLabel xml:lang="pt">Collage</skos:altLabel>
    <skos:prefLabel xml:lang="el">Κολάζ</skos:prefLabel>
    <skos:prefLabel xml:lang="it">collage</skos:prefLabel>
    <skos:altLabel xml:lang="de">Materialbild</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Colagem</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10168">
    <skos:altLabel xml:lang="de">Clip</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211662\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211662"/>
    <skos:prefLabel xml:lang="pt">Pregadeira (joalharia)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spännen</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Decorative items, similar in appearance to a brooch, but attached to a garment with a spring fastening. For jewelry made in a variety of forms fastened by a pin, uses ""brooches."" (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Fermagli</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Klips</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Klämmor</skos:altLabel>
    <skos:prefLabel xml:lang="en">clip (jewelry)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Spange (Schmuck)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Clip (sieraad)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Clip (joya)</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κλιπ</skos:prefLabel>
    <skos:altLabel xml:lang="fr">clips</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sv">Clips</skos:altLabel>
    <skos:prefLabel xml:lang="fr">attache</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10167"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10109">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10108"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300375755\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300375755"/>
    <skos:altLabel xml:lang="sv">Sorgkläder</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Abiti da lutto</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Rouwkleding</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Vêtements de deuil</skos:altLabel>
    <skos:altLabel xml:lang="nl">Rouwkledij</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Crnina</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">vêtement de deuil</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ένδυμα πένθους</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Trauerkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestuário de luto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Clothes or draperies customarily indicative of bereavement, often donned during a period designated for the conventional or ceremonial manifestation of sorrow. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Ropa de luto</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sorgdräkt</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">Mourning clothing</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10314">
    <skos:prefLabel xml:lang="de">Webseite</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Webpage</skos:altLabel>
    <skos:altLabel xml:lang="nl">Webpage</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Internetseite</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ιστοσελίδα</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">page Internet</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Página web</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Webbsida</skos:prefLabel>
    <skos:altLabel xml:lang="de">Webdokument</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Página da internet</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Hypertext documents of text or images that are accessible via the World Wide Web, hosted on a web site. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="fr">page web</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Web stranica</skos:prefLabel>
    <skos:prefLabel xml:lang="it">pagina web</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Webpagina</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300264578\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300264578"/>
    <skos:prefLabel xml:lang="en">webpage</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10640">
    <skos:prefLabel xml:lang="es">Broche</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10167"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">brooch</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Brosch</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Broš</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Brosche</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">broche</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10268">
    <skos:prefLabel xml:lang="fr">boots (enfant)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">scarpine (da bebé)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Babyskor</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">chaussons</skos:altLabel>
    <skos:prefLabel xml:lang="el">Παπούτσια πλεκτά παιδικά</skos:prefLabel>
    <skos:altLabel xml:lang="el">Τερλίκια</skos:altLabel>
    <skos:prefLabel xml:lang="de">gestrickte Babyschuhe</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Soft, socklike footwear, usually knitted or crocheted. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Botitas de bebé</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210021\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210021"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">bootees (infant footwear)</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10267"/>
    <skos:prefLabel xml:lang="pt">Botinhas de bébé (calçado infantil)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Patike za bebe</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Babyschoenen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/Subject">
    <skos:prefLabel xml:lang="en">Subject</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10021">
    <skos:prefLabel xml:lang="pt">Cardigan</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πλεκτή ζακέτα</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cardigan</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cardigan</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:prefLabel xml:lang="en">Cardigan</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kardigan</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Cardigan</skos:altLabel>
    <skos:altLabel xml:lang="nl">Cardigan</skos:altLabel>
    <skos:altLabel xml:lang="fr">Cardigan</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Vest</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Strickjacke</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Sweaters that open the full length of the center front, closing with buttons, snaps, or a zipper, and having a round or V-shaped, usually collarless, neckline. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Cárdigan</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Casaco de malha sem gola com botões</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Kofta</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209880\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209880"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10372">
    <skos:prefLabel xml:lang="el">Ταφτάς</skos:prefLabel>
    <skos:prefLabel xml:lang="en">taffeta</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Platbinding</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">taffetas</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10371"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Tafetá</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Crisp textile, typically in plain weave, sometimes with a fine crosswise rib and a smooth lustrous surface on both sides, originally of silk, now of various fibers. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Taf</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Taft</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Taft</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Taft</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300249434\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300249434"/>
    <skos:prefLabel xml:lang="es">Tafetán</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10304">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Pintura</skos:prefLabel>
    <skos:prefLabel xml:lang="it">dipinto</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300033618\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300033618"/>
    <skos:prefLabel xml:lang="el">Πίνακας ζωγραφικής</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Slika</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Gemälde</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="en">painting</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Unique works in which images are formed primarily by the direct application of pigments suspended in oil, water, egg yolk, molten wax, or other liquid, arranged in masses of color, onto a generally two-dimensional surface. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">peinture</skos:prefLabel>
    <skos:altLabel xml:lang="fr">tableau</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Schilderij</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Målning</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pintura</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10233">
    <skos:prefLabel xml:lang="nl">Cocktailjurk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10005"/>
    <skos:prefLabel xml:lang="en">Cocktail dress</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10391">
    <skos:prefLabel xml:lang="es">Tapicería</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Bildväv</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Tapiserija</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to the process used to create tapestries, which are heavy, woven textiles characterized by ornamental or pictorial designs and used as wall hangings, curtains, upholstery, or to hang from windows or balconies. The process is performed on a tapestry loom and differs from cloth-weaving in that the weft travels only to the warp at the edge of a particular color or pattern in the design, rather traveling from edge to edge of the entire piece of fabric. Various techniques are used in mixing and overlaying colors to create shading and patterns. Details of the design are often painted or embroidered. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">tapisserie</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ταπισερί</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10385"/>
    <skos:prefLabel xml:lang="de">Tapisserie</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Tapisserie</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Tapijtwerk</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Gobeläng</skos:prefLabel>
    <skos:prefLabel xml:lang="en">tapestry</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Tapeçaria</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300061981\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300061981"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10252">
    <skos:altLabel xml:lang="pt">Sapatos sem calcanhar</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Muiltjes</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216741\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216741"/>
    <skos:altLabel xml:lang="de">Mule</skos:altLabel>
    <skos:altLabel xml:lang="it">ciabattine</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Chinelas</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Mules</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Sandaletter</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:altLabel xml:lang="el">Μούλες</skos:altLabel>
    <skos:prefLabel xml:lang="de">Pantolette</skos:prefLabel>
    <skos:prefLabel xml:lang="en">mules</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mules</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Papuče</skos:prefLabel>
    <skos:altLabel xml:lang="es">Mulés</skos:altLabel>
    <skos:prefLabel xml:lang="el">Πασούμια</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">Pantoffel</skos:altLabel>
    <skos:prefLabel xml:lang="it">pianelle</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Heeled shoes or slippers with only a vamp, or front portion, and no quarters or back portion. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="it">sabot</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10354">
    <skos:prefLabel xml:lang="fr">coton</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Katoen</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pamuk</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Baumwolle</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Algodão</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βαμβάκι</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"White-to-yellowish seed-hair fibers of several species of the genus Gossypium, native to most subtropical areas of the world; used especially for making textile, cord, padding, rag paper, and for cellulose used for plastic and rayon. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="en">cotton</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
    <skos:prefLabel xml:lang="es">Algodón</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bomull</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300183670\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300183670"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10467">
    <skos:prefLabel xml:lang="sv">Konfektion</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Konfektion</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Ready-to-wear</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Έτοιμο ένδυμα</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Prêt à porter</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Prêt à porter</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Prêt à porter</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pronto-a-vestir</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Konfekcija</skos:prefLabel>
    <skos:altLabel xml:lang="de">Prêt-à-porter</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10334"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10653">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10221"/>
    <skos:prefLabel xml:lang="en">Band (collar)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10128">
    <skos:prefLabel xml:lang="de">Reitkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ridkläder</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κοστούμι ιππασίας</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Odeća za jahanje</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Coordinated costume for horseback riding, usually consisting of a jacket or coat, skirt or breeches or jodhpurs, and vest. Varies in degree of formality. Usually worn with stock-tied shirt, cap or hat, and riding boots. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Ropa de montar</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tenue d'équitation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Paardrijkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento da equitazione</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fato de equitação</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300215869\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300215869"/>
    <skos:altLabel xml:lang="nl">Ruiterkleding</skos:altLabel>
    <skos:prefLabel xml:lang="en">Horseriding wear</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10121"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10344">
    <skos:prefLabel xml:lang="el">Εβδομάδα μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mässa</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">fair</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Feira</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Fachmesse</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300054776\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300054776"/>
    <skos:prefLabel xml:lang="es">Feria</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="sr">Sajam</skos:prefLabel>
    <skos:prefLabel xml:lang="it">fiera</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">foire</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Exhibitions in which different exhibitors participate to publicize and promote their products, services, or activities, sometimes though not necessarily involving buying and selling, and often combined with entertainment. For meetings of buyers and sellers at stated times and places for the purpose of trade, use ""markets (events)."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Beurs</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10040">
    <skos:prefLabel xml:lang="el">Κάπα</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Capa</skos:prefLabel>
    <skos:altLabel xml:lang="de">Cape</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Cape</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Cape</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Cape</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Pelerine</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Ogrtač</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046140\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046140"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Umhang</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Capa corta</skos:prefLabel>
    <skos:altLabel xml:lang="fr">pèlerine</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Sleeveless outer garments of varying lengths, fastening at the neck and falling loosely from the shoulders that function as either separate garments or attach to longer coats or cloaks. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Cappa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoudermantel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10667">
    <skos:scopeNote xml:lang="en">"After reading the script the costume designer may develop ideas digitally or on paper. If the designer is too busy on the production or the sketch requires a slick finish (such as a super-hero) they may work with a costume illustrator. Many designers choose to use mood boards, collages of inspiration as communication tools. Whether or not sketches are created is ultimately irrelevant. The characters on screen define the “art of costume” (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10307"/>
    <skos:prefLabel xml:lang="en">Costume sketch (Costume design)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10457">
    <skos:prefLabel xml:lang="de">Schuhmacherwerkzeuge</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Εργαλεία υποδηματοποιίας</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoenmakersgereedschap</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10274"/>
    <skos:prefLabel xml:lang="pt">Ferramentas para produção de calçado</skos:prefLabel>
    <skos:altLabel xml:lang="de">Werkzeuge der Schuhmacherei</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Obućarski alat</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Gereedschap voor schoenmaken</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Tools for shoemaking</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Herramientas de zapatero</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">outil de cordonnier</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Skomakarverktyg</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10247">
    <skos:prefLabel xml:lang="nl">Accessoires gedragen aan benen of voeten</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi na nogama i stopalima</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorios de pierna y pie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">am Bein oder Fuß zu tragen</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Accessoirer för ben och fötter</skos:prefLabel>
    <skos:prefLabel xml:lang="en">accessories worn on the legs or feet</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Acessórios usados nas pernas ou pés</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211599\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211599"/>
    <skos:prefLabel xml:lang="it">Accessori indossati alle gambe o ai piedi</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ κάτω άκρων</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Accessoires</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires portés sur les jambes ou sur les pieds</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10149">
    <skos:prefLabel xml:lang="pt">Guarda-chuva</skos:prefLabel>
    <skos:altLabel xml:lang="de">Regenschirm</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Portable, usually waterproof canopies consisting of a frame with hinged ribs radiating from a center pole with handle, carried for protection against the weather. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Paraplu's</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">parapluie</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Paraplyer</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Kišobran</skos:prefLabel>
    <skos:prefLabel xml:lang="en">umbrella</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Paraguas</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046227\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046227"/>
    <skos:prefLabel xml:lang="el">Ομπρέλα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Ombrello</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Schirm</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10315">
    <skos:prefLabel xml:lang="pt">Blogpost</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Blogpost</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Blogginlägg</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Post na blogu</skos:prefLabel>
    <skos:prefLabel xml:lang="en">blogpost</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Blogpost</skos:altLabel>
    <skos:altLabel xml:lang="nl">Blogpost</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Blogbericht</skos:prefLabel>
    <skos:prefLabel xml:lang="it">post</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Blogbeitrag</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">article de blog</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Blog post</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10169">
    <skos:altLabel xml:lang="es">collera</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10167"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">boutons de manchette</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209299\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209299"/>
    <skos:prefLabel xml:lang="nl">Manchetknopen</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botões de punho</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Manschettenknöpfe</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gemelli</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Manschettknappar</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Dugmad za manžetne</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gemelos</skos:prefLabel>
    <skos:altLabel xml:lang="es">Botón doble</skos:altLabel>
    <skos:altLabel xml:lang="sv">manchett</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">Linked ornamental buttons or buttonlike devices for fastening a shirt cuff. (AAT)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">cuff links</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μανικετόκουμπα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10267">
    <skos:prefLabel xml:lang="el">Μποτάκια</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botines</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Botins (calçado de adulto)</skos:prefLabel>
    <skos:altLabel xml:lang="it">tronchetti</skos:altLabel>
    <skos:altLabel xml:lang="sr">Duboke cipele</skos:altLabel>
    <skos:prefLabel xml:lang="fr">boots (adulte)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Enkellaarzen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">bottines</skos:altLabel>
    <skos:scopeNote xml:lang="en">Ankle-length boots worn by women. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Stiefelette</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">bottillons</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300262817\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300262817"/>
    <skos:prefLabel xml:lang="sv">Stövletter</skos:prefLabel>
    <skos:prefLabel xml:lang="en">booties (adult footwear)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">stivaletti</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Polučizme</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10020">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Caraco: giacca storica</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209879\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209879"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10017"/>
    <skos:scopeNote xml:lang="en">Women's short jackets extending to cover the spread of the panniers. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Caracó</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Casaquinha ou corpete cintado com aba</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Karako</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Caraco</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Caraco</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10371">
    <skos:prefLabel xml:lang="sr">Tkanje</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Väva</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">tissé</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Gewebebindung</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10370"/>
    <skos:prefLabel xml:lang="en">weave</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Estrutura de tecelagem</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Binding</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Ύφανση</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Weefsel</skos:altLabel>
    <skos:prefLabel xml:lang="es">Ligamento</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10353">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Plantaardige vezels</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vegetabiliska fibrer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Vezels van plantaardige oorsprong</skos:altLabel>
    <skos:prefLabel xml:lang="en">vegetable origin fibres</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras de origen vegetal</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Vlakna biljnog porekla</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υλες φυτικής προέλευσης</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pflanzenfasern</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Fibres d'origine végétale</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:scopeNote xml:lang="en">"Natural fiber made from plant material, often used in papermaking. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Fibras de origem vegetal</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10347"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014031\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014031"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10108">
    <skos:prefLabel xml:lang="fr">vêtement fonctionnel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Functionele kleding</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Indumentaria funcional</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Roupa funcional</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">Vêtements fonctionnels</skos:altLabel>
    <skos:altLabel xml:lang="nl">Functiekledij</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Kläder för speciella tillfällen</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Χρηστικά ενδύματα</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abbigliamento funzionale</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Functional wear</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Funktionale Kleidung</skos:prefLabel>
    <skos:altLabel xml:lang="de">zweckgebundene Kleidung</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10001"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Odeća za posebne prilike</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10305">
    <skos:prefLabel xml:lang="fr">estampe</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Druck</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Impressão</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Χαρακτικό</skos:prefLabel>
    <skos:prefLabel xml:lang="en">print</skos:prefLabel>
    <skos:prefLabel xml:lang="it">stampa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Prent</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Tryck</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300041273\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300041273"/>
    <skos:altLabel xml:lang="fr">impression</skos:altLabel>
    <skos:prefLabel xml:lang="es">Estampa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Afdruk</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Štampa</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Pictorial works produced by transferring images by means of a matrix such as a plate, block, or screen, using any of various printing processes. When emphasizing the individual printed image, use ""impressions."" Avoid the controversial expression ""original prints,"" except in reference to discussions of the expression's use. If prints are neither ""reproductive prints"" nor ""popular prints,"" use the simple term ""prints."" With regard to photographs, see ""photographic prints""; for types of reproductions of technical drawings and documents, see terms found under ""reprographic copies."" (AAT)"</skos:scopeNote>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10232">
    <skos:prefLabel xml:lang="nl">Avondjurk</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10005"/>
    <skos:prefLabel xml:lang="en">Evening gown</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10043">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Staubmantel</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dammrock</skos:prefLabel>
    <skos:altLabel xml:lang="it">Scialle</skos:altLabel>
    <skos:prefLabel xml:lang="en">Duster</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Spolverino</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Daster kaput</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Cache-poussière</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Πανωφόρι οδηγού</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Long, lightweight coatlike overgarments worn in the early 20th century to protect the wearer from dust, especially while riding in automobiles. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216842\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216842"/>
    <skos:altLabel xml:lang="en">Wrap</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="pt">Guarda pó</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Stofjas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Guardapolvo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10146">
    <skos:prefLabel xml:lang="sv">Ridspön</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Bič za jahanje</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Frustino</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="es">Rebenque</skos:altLabel>
    <skos:prefLabel xml:lang="en">riding whip</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Short whips, usually made of braided leather, used by equestrians. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Μαστίγιο ιππασίας</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Reitgerte</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300243779\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300243779"/>
    <skos:prefLabel xml:lang="nl">Rijzwepen</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Pingalins</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">cravache</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Látigo</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10197">
    <skos:prefLabel xml:lang="nl">Toques</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τόκα</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Tocco</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Small caps or hats worn by men or women in various different eras and countries. Also, small brimless hats. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Tokica</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">toque</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mössor</skos:prefLabel>
    <skos:prefLabel xml:lang="en">toque (cap)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Toque</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Toque</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="es">Casquete</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210812\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210812"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10384">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300227779\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300227779"/>
    <skos:prefLabel xml:lang="en">brocade</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Lavrado</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Textile produced by brocading, typically richly figured and incorporating metal thread. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Μπροκάρ</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Brokat</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="de">broschiertes Gewebe</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Brocado</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Brocado</skos:prefLabel>
    <skos:altLabel xml:lang="de">Brokat</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Brokaat</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Brokad</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10371"/>
    <skos:prefLabel xml:lang="fr">brocart</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10662">
    <skos:scopeNote xml:lang="en">"A squib is a small explosive (pyrotechnic) used to simulate a bullet hit. These can be placed in costumes, walls or props (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:prefLabel xml:lang="en">Squib</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10156">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">bijoux portés sur la tête</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Joalharia usada na cabeça</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κοσμήματα κεφαλιού</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bijoux de tête</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Hoofdsieraad</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Gioielli da indossare sulla testa</skos:prefLabel>
    <skos:prefLabel xml:lang="de">auf dem Kopf getragener Schmuck</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209301\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209301"/>
    <skos:prefLabel xml:lang="es">Joyería para la cabeza</skos:prefLabel>
    <skos:prefLabel xml:lang="en">jewelry worn on the head</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Nakit za glavu</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Smycken burna på huvudet</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sieraden gedragen op het hoofd</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10652">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10217"/>
    <skos:prefLabel xml:lang="en">suspenders</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10394">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10392"/>
    <skos:prefLabel xml:lang="es">Nudo turco</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ponto turco</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="en">Turkish knot</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">nœud turc</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Turkse knoop</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Turkisk knut</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τουρκικός κόμπος</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Türkischer Knoten</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="sr">Simetričan čvor</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Turski čvor</skos:prefLabel>
    <skos:altLabel xml:lang="fr">nœud symétrique</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10033">
    <skos:prefLabel xml:lang="pt">Páreo</skos:prefLabel>
    <skos:altLabel xml:lang="it">Gonna pareo</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:prefLabel xml:lang="sr">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Sarong</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209928\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209928"/>
    <skos:prefLabel xml:lang="es">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Sarong</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Gonna Sarong</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Main garments formed by wrapping a strip of cloth around the lower part of the body. Worn chiefly by men and women of the Malay Archipelago and the Pacific Islands. Also, similar often preformed garments worn by Western women. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Σαρόνγκ</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10183">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">mask</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μάσκα</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to coverings for all or part of the face, usually with openings for the eyes and sometimes the mouth. They are worn to hide or alter the identity of the wearer or for protection. Masks as cultural objects have been used throughout the world in all periods since the Stone Age. Masks are extremely varied in appearance, function, and fundamental meaning. They may be associated with ceremonies that have religious and social significance or are concerned with funerary customs, fertility rites, or curing sickness. They may be used on festive occasions or to portray characters in a dramatic performance and in re-enactments of mythological events. They may be used for warfare and as protective devices in certain sports. They are also employed as architectural ornaments. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">masque</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Maske</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300138758\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300138758"/>
    <skos:altLabel xml:lang="es">Careta</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Mask</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Maskers</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:prefLabel xml:lang="pt">Máscara</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Máscara</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Maska</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Maschera</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10231">
    <skos:scopeNote xml:lang="en">Small neckties tied in a bow knot. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Pajarita</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Flugor</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παπιγιόν</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Farfallino</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Leptir mašna</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bow tie</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Querbinder</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">nœud papillon</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="nl">Vlinderdas</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210057\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210057"/>
    <skos:prefLabel xml:lang="nl">Vlinderstrik</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="de">Mascherl</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Laço</skos:prefLabel>
    <skos:altLabel xml:lang="de">Fliege</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10229"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10632">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="en">Knapsack</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10333">
    <skos:prefLabel xml:lang="es">Colección</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Collectie</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10000"/>
    <skos:scopeNote xml:lang="en">"Refers to accumulated groups of objects or materials having a focal characteristic and that have been brought together by an individual or organization. Examples include a selected set of art works in a museum or archive, or separate literary works that do not form a treatise or monograph on a subject but have been combined and issued together as a whole. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Kolekcija</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Samling</skos:altLabel>
    <skos:prefLabel xml:lang="it">Collezione</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="sr">Zbirka</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Colecção</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Collection</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Collection</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300025976\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300025976"/>
    <skos:altLabel xml:lang="de">Sammlung</skos:altLabel>
    <skos:prefLabel xml:lang="el">Συλλογή</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Kollektion</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kollektion</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10107">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Kragstöd</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">support de col</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Collar support</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kraagversteviging</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Supporto per colletto</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Suporte de gola</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Στήριγμα κολάρου</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kragenstütze</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="es">Cuello</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Držač za okovratnik</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10352">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">soie</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Fine continuous protein fiber consisting primarily of fibroin filaments, secreted by silk caterpillars. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Zijde</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μετάξι</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10350"/>
    <skos:prefLabel xml:lang="en">silk</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Seide</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Siden</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Seda</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Svila</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014072\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014072"/>
    <skos:prefLabel xml:lang="es">Seda</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10155">
    <skos:scopeNote xml:lang="en">"Hanging articles of jewelry, usually suspended from a necklace, brooch, or earrings, but also includes Renaissance examples fastened to the sleeve often worn as decorative ornaments; can also be an article of devotional, magical, or mourning jewelry which then may sometimes be concealed under clothing. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="sr">Ogrlica sa pandantivom</skos:altLabel>
    <skos:prefLabel xml:lang="el">Μενταγιόν</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pendants d'oreille</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Hanger</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ciondolo</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pandantiv</skos:prefLabel>
    <skos:altLabel xml:lang="es">pinjón</skos:altLabel>
    <skos:prefLabel xml:lang="de">Anhänger</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10152"/>
    <skos:prefLabel xml:lang="sv">Hängsmycken</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Pendente (joalharia)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">pendentifs (bijoux)</skos:altLabel>
    <skos:prefLabel xml:lang="es">Colgante</skos:prefLabel>
    <skos:altLabel xml:lang="it">Collana con ciondolo</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046002\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046002"/>
    <skos:altLabel xml:lang="es">Pinjante</skos:altLabel>
    <skos:prefLabel xml:lang="en">pendant (jewelry)</skos:prefLabel>
    <skos:altLabel xml:lang="de">Kettenanhänger</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10312">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Ταινία μόδας</skos:prefLabel>
    <skos:prefLabel xml:lang="it">fashion film</skos:prefLabel>
    <skos:prefLabel xml:lang="en">fashion film</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">film sur la mode</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Modefilm</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modefilm</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Modefilm</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Filme de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Película de moda</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Modni film</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="it">fashion video</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10311"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10661">
    <skos:scopeNote xml:lang="en">"Something that an actor uses during a scene, a cigarette, a coffee cup, a briefcase, or a newspaper. Historically, props provide wedding bands, watches and glasses and the costume designer chooses the appropriate one for each character (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:prefLabel xml:lang="en">Props (film)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10642">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10182"/>
    <skos:prefLabel xml:lang="en">Sunglasses</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10117">
    <skos:prefLabel xml:lang="fr">costume de cérémonie</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Ceremoniële kleding</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Zeremonialkleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Ceremoniell dräkt</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Ceremoniële kledij</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210387\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210387"/>
    <skos:prefLabel xml:lang="sr">Ceremonijalna odeća</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Clothing or other apparel having a primarily ceremonial or ritual purpose. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Traje de cerimónia</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Ceremonial costume</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Taufkleidung</skos:altLabel>
    <skos:prefLabel xml:lang="el">Τελετουργικά ενδύματα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10108"/>
    <skos:prefLabel xml:lang="it">Abbigliamento da cerimonia</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vestuario ceremonial</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10241">
    <skos:prefLabel xml:lang="pt">Braçadeiras</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Brazalete (banda)</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Traka za ruke</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">brassard</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cinturino</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Armbanden</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Bands encircling the arm, sometimes worn for identification or in mourning; also similar component parts of a sleeve. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">armband</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Armband</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10240"/>
    <skos:prefLabel xml:lang="de">Armbinde</skos:prefLabel>
    <skos:altLabel xml:lang="it">Bracciale</skos:altLabel>
    <skos:prefLabel xml:lang="el">Περιβραχιόνια</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300247490\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300247490"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10459">
    <skos:prefLabel xml:lang="el">Αφίσα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Affisch</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Póster</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Poster</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Poster</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Poster</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Poster</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Poster</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Poster</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="fr">Affiche</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300027221\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300027221"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10042">
    <skos:prefLabel xml:lang="sr">Kaput</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Main garments usually fitted to the upper body, extending below the hip line, open at the front or side and generally having sleeves. Also, similar outer garments worn for warmth or protection from the weather. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Mantel</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046143\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046143"/>
    <skos:prefLabel xml:lang="sv">Kappa/rock</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Coat</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παλτό</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Casaco</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Manteau</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Abrigo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">Cappotto</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10383">
    <skos:prefLabel xml:lang="en">damassé (cotton or linen)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Adamascado (algodão ou linho)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Adamascado</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300231734\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300231734"/>
    <skos:prefLabel xml:lang="sr">Damastno tkanje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Damassé</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bomulls- / linnedammast</skos:prefLabel>
    <skos:altLabel xml:lang="fr">damassé (coton ou lin)</skos:altLabel>
    <skos:prefLabel xml:lang="fr">damassé</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10381"/>
    <skos:scopeNote xml:lang="en">"Figured woven textile having a damasklike appearance, with contrasting luster in the pattern and ground."</skos:scopeNote>
    <skos:prefLabel xml:lang="de">Baumwoll- oder Leinendamast</skos:prefLabel>
    <skos:altLabel xml:lang="de">Baumwoll-oder Leinendamast</skos:altLabel>
    <skos:prefLabel xml:lang="el">Δαμασκηνό (βαμβακερό ή λινό)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10198">
    <skos:prefLabel xml:lang="es">Orejeras (accesorios para la cabeza)</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Oorwarmers</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Öronmuffar</skos:prefLabel>
    <skos:prefLabel xml:lang="en">earmuff</skos:prefLabel>
    <skos:altLabel xml:lang="de">Ohrenschützer</skos:altLabel>
    <skos:prefLabel xml:lang="it">Paraorecchie</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10188"/>
    <skos:prefLabel xml:lang="fr">cache-oreille</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Muf za uši</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Protector de orelhas</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Ear coverings worn as protection against the cold. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Προστατευτικό αυτιών</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Ohrschützer</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046104\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046104"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10147">
    <skos:prefLabel xml:lang="sr">Štap</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Bengala</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Bastón</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="sv">Promenadkäppar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Bastone da passeggio</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μπαστούνι</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">canne</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="de">Wanderstock</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Sticks held in the hand and used for support in walking, especially as a fashionable and often ornamental accessory when taking a walk. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Wandelstokken</skos:prefLabel>
    <skos:prefLabel xml:lang="en">walking stick</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221268\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221268"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10651">
    <skos:prefLabel xml:lang="de">Strohhut</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Slamnati šešir</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chapeau de paille</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Straw hat</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Halmhattar</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Sombrero de paja</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ψαθάκι</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Stråhattar</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10157">
    <skos:prefLabel xml:lang="fr">bijoux d'oreille</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">ornements d'oreille</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Smycken för öron</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Ukras za uho</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211279\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211279"/>
    <skos:prefLabel xml:lang="nl">Oorsieraden</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Ohrschmuck</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:prefLabel xml:lang="pt">Ornamentos de orelha</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Decorative items worn in, on, or about the ear. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Στολίδια αυτιών</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Adorno para las orejas</skos:prefLabel>
    <skos:prefLabel xml:lang="en">ear ornament</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Ornamenti per le orecchie</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10184">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:prefLabel xml:lang="es">Lunar</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">patch</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schönheitspflaster</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Schoonheidsvlekjes</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Καλύπτρα ματιού</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210515\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210515"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Mouche</skos:prefLabel>
    <skos:altLabel xml:lang="de">Mouche</skos:altLabel>
    <skos:prefLabel xml:lang="fr">mouche</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mouche</skos:altLabel>
    <skos:altLabel xml:lang="pt">Remendo</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Beauty spots, sometimes in decorative shapes, applied to the face for ornament. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="it">Toppa</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Emblema</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Aplikacija</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10393">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="fr">nœud asymétrique</skos:altLabel>
    <skos:prefLabel xml:lang="fr">nœud persan</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Nudo persa</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Persijski čvor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10392"/>
    <skos:prefLabel xml:lang="de">Persischer Knoten</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Persian knot</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ponto persa</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Persisk knut</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Perzische knoop</skos:prefLabel>
    <skos:altLabel xml:lang="sr">Asimetričan čvor</skos:altLabel>
    <skos:prefLabel xml:lang="el">Περσικός κόμπος</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10032">
    <skos:scopeNote xml:lang="en">"Close-fitting trouserlike garments, often with a strap that goes under the foot or shoe at the instep; includes those made to match a coat or jacket and worn outdoors by children. Also, leg coverings, usually extending from the ankle to the knee but sometimes higher, worn for protection. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="sv">Leggings</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Tights</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Leggings</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Helanke</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Leggings</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Leggings</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Leggings</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Leggings</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Περικνημίδες</skos:prefLabel>
    <skos:altLabel xml:lang="fr">leggings</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046173\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046173"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Legging</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Guêtres</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Caleçon long</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10331">
    <skos:prefLabel xml:lang="sr">Izlog</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schaufenster</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vitrina</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10301"/>
    <skos:prefLabel xml:lang="en">display window</skos:prefLabel>
    <skos:prefLabel xml:lang="it">vetrina</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Skyltfönster</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">vitrine</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Etalage</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Uitstalraam</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300002970\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300002970"/>
    <skos:scopeNote xml:lang="en">"Any windows used or designed for the display of goods or advertising material, whether fully or partly enclosed or entirely open at the rear; may have a platform raised above street level. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Montra</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Προθήκη</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10230">
    <skos:prefLabel xml:lang="sv">Bolo slips</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Gravata bolo  (cordão de cabedal com aplicação)</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Schnürsenkelkrawatte</skos:prefLabel>
    <skos:altLabel xml:lang="es">Corbata de cordón</skos:altLabel>
    <skos:prefLabel xml:lang="fr">bolo tie</skos:prefLabel>
    <skos:prefLabel xml:lang="en">bolo tie</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Bola</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">Neckties of thin cord fastened in front with an ornamental clasp or other device. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="sr">Vestern kravata</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Bolo kravata</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Gravata atacador</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221454\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221454"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10229"/>
    <skos:altLabel xml:lang="it">Cravatta di cuoio</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Corbata de bolo</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Veterdassen</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Bolo tie</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10332">
    <skos:altLabel xml:lang="pt">Boneca de Moda</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Modedocka</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300211317\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300211317"/>
    <skos:prefLabel xml:lang="el">Κούκλα βιτρίνας</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Mannequin (pop)</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Fashion doll</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fashion doll</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Fashion doll</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Modepuppe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">fashion doll</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">poupée mannequin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10301"/>
    <skos:scopeNote xml:lang="en">"Human figures or dolls created to illustrate or promote specific fashions in clothing, textiles, and hair styling; especially popular from the 16th to the 18th century in Europe. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Modna lutka</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10259">
    <skos:scopeNote xml:lang="en">"Shoes consisting essentially of a sole fastened to the foot by straps, strips, or cords. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Sandale</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sandaler</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σανδάλια</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">sandales</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046077\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046077"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">sandals</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sandálias</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sandalias</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Sandalen</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Sandalen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">sandali</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10631">
    <skos:prefLabel xml:lang="el">Minaudiere</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Minaudiere</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Večernja tašnica</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">Minaudiere</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="es">Bolso de boquilla</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">minaudière</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Minaudière</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Aftonväskor</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10106">
    <skos:prefLabel xml:lang="nl">Kuitkussentjes</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">faux mollets</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Panturrilha</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lösvader</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">"""Lažni listovi"""</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="de">Wadenpolster</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Pantorrilha</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">amplificateurs de mollet</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Relleno de pantorrilla</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Calf improvers</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Calf improvers</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10351">
    <skos:prefLabel xml:lang="de">Wolle</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Lana</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300014074\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300014074"/>
    <skos:altLabel xml:lang="de">Wollhaar</skos:altLabel>
    <skos:prefLabel xml:lang="fr">laine</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Wol</skos:prefLabel>
    <skos:prefLabel xml:lang="en">wool</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="sr">Vuna</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Textile made from the animal hair wool. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Lã</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μαλλί</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10350"/>
    <skos:prefLabel xml:lang="sv">Ull</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10650">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Tropikhjälmar</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Tropenhelm</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κάσκα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10200"/>
    <skos:prefLabel xml:lang="es">Salacot</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Casque colonial</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Pith helmet</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Safari šešir</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10059">
    <skos:prefLabel xml:lang="fr">ceinture</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10051"/>
    <skos:prefLabel xml:lang="es">Cinturón</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210002\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210002"/>
    <skos:altLabel xml:lang="sv">skärp</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Pojas</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Flexible straps or bands generally encircling the waist or hips or passing over the shoulder and usually having some type of fastener, such as a buckle; worn for decoration, support, or to carry such items as weapons, tools, or money. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Riem</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Gürtel</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Belt</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Cinto</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Cintura</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bälte</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ζώνη</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10660">
    <skos:prefLabel xml:lang="sr">Desert čizme</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Botas de desierto</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="es">Desert boots</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Ökenkängor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10261"/>
    <skos:prefLabel xml:lang="de">Desert Boots</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Μποτάκια</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">desert boots</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Desert boots</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10479">
    <skos:prefLabel xml:lang="pt">Juta</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Juta</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Γιούτα</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Juta</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Jute</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Yute</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Jute</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Jute</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jute</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">jute</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10353"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10313">
    <skos:altLabel xml:lang="de">Internetplattform</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Sítio da internet</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ιστότοπος</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sitio web</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Website</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Internetportal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">site Internet</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"A group of World Wide Web pages usually associated with a particular subject and connected via hyperlinks, made available online by an institution, company, government, or other organization. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="de">Internetauftritt</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Webbplats</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300265431\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300265431"/>
    <skos:altLabel xml:lang="pt">Site</skos:altLabel>
    <skos:prefLabel xml:lang="en">website</skos:prefLabel>
    <skos:altLabel xml:lang="fr">site web</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Web sajt</skos:prefLabel>
    <skos:altLabel xml:lang="de">Website</skos:altLabel>
    <skos:prefLabel xml:lang="it">sito web</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10279"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10269">
    <skos:prefLabel xml:lang="nl">Overschoenen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10249"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="el">Προστατευτικά παπουτσιών</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Galochas</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Sobre-sapatos</skos:altLabel>
    <skos:altLabel xml:lang="fr">Couvre-chaussures</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Bottiner</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210039\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210039"/>
    <skos:prefLabel xml:lang="sr">Kaljače</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Überschuhe</skos:prefLabel>
    <skos:prefLabel xml:lang="en">overshoes</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Footwear worn over shoes for warmth or protection. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="es">Cubrezapatos</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">soprascarpe</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">galoche</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10641">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10167"/>
    <skos:prefLabel xml:lang="en">Corsage</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10240">
    <skos:prefLabel xml:lang="sr">Aksesoar koji se nosi na rukama i šakama</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accessoires portées  sur les bras et sur les mains</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Accessoires an Arm oder Hand zu tragen</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Accesorios de mano y brazo</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Acessórios usados nos braços ou mãos</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10150"/>
    <skos:prefLabel xml:lang="it">Accessori indossati sulle braccia o alle mani</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209282\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209282"/>
    <skos:prefLabel xml:lang="el">Αξεσουάρ άνω άκρων</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Accessoirer för armar och händer</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Accessoires gedragen aan armen of handen</skos:prefLabel>
    <skos:prefLabel xml:lang="en">accessories worn on arms or hands</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10116">
    <skos:prefLabel xml:lang="it">Divisa</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Werkuniform</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Uniform (professionele kledij)</skos:altLabel>
    <skos:prefLabel xml:lang="fr">uniforme</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Uniforma</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Identifying garments or styles of dress worn by the members of a given profession, organization, or rank. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Uniforme ( roupa de trabalho)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Uniformes (indumentaria profesional)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212393\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212393"/>
    <skos:prefLabel xml:lang="sv">Uniform</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="fr">tenue de travail</skos:altLabel>
    <skos:prefLabel xml:lang="de">Berufsuniform</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10110"/>
    <skos:prefLabel xml:lang="el">Στολή</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Uniform (professional wear)</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10664">
    <skos:scopeNote xml:lang="en">"The cutter-fitter will create a toile out of inexpensive fabric. This is the first three-dimentsional interpretation of the design, whether from a sketch or a conversation. The designer will correct the toile in the dress form. Then, a final flat pattern will be made (UCLA)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10276"/>
    <skos:prefLabel xml:lang="en">Toile (muslin)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10382">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10381"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300163295\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300163295"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Damasco (seda)</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">damassé</skos:prefLabel>
    <skos:prefLabel xml:lang="en">damask (silk)</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Damasco</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Woven figured textile with one warp and one weft in which the pattern is formed by a contrast of binding systems, and appears on the face and the back in reverse positions. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Damast (zijde)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="el">Δαμασκηνό (μεταξωτό)</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Sidendammast</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Damast (Seide)</skos:prefLabel>
    <skos:altLabel xml:lang="fr">damasserie (soie)</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Damast</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10320">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Literary compositions prepared for publication as an independent portion of a magazine, newspaper, encyclopedia, or other work. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Članak</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Άρθρο</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Artículo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300048715\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300048715"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">articolo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Artikel</skos:prefLabel>
    <skos:altLabel xml:lang="de">Zeitungsartikel</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Artikel</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Artigo</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Beitrag</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">article</skos:prefLabel>
    <skos:prefLabel xml:lang="en">article</skos:prefLabel>
    <skos:altLabel xml:lang="de">Aufsatz</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10244">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210012\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210012"/>
    <skos:prefLabel xml:lang="de">Fausthandschuhe</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Rukavice</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Vantar</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Coverings for the hand enclosing four fingers in one section and the thumb in another section. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Wanten</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10242"/>
    <skos:prefLabel xml:lang="pt">Mitenes (luvas com um só dedo / tipo luva de boxe ou de criança)</skos:prefLabel>
    <skos:altLabel xml:lang="de">Fäustlinge</skos:altLabel>
    <skos:prefLabel xml:lang="el">Γάντια με ένα δάχτυλο</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Guanti a manopola</skos:prefLabel>
    <skos:altLabel xml:lang="it">Guanti senza le singole dita</skos:altLabel>
    <skos:prefLabel xml:lang="fr">mitaines</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">mittens (handwear)</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Manoplas</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10195">
    <skos:prefLabel xml:lang="el">Φρυγικός σκούφος</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"Term applied to conical caps or bonnets with the peak bent or turned over in front. It originated in the ancient country of Phrygia in Asia Minor and is represented in ancient Greek art as the type of headdress worn by Orientals. In Rome the Phrygian cap was worn by emancipated slaves as a symbol of their freedom. During the 11th and 12th centuries, it was again extensively used. For similar caps worn in the French Revolution and identified with the cap of liberty, use ""liberty caps."" (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">bonnet phrygien</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gorro frigio</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Frygiska mössor</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Frigijska kapa</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210776\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210776"/>
    <skos:prefLabel xml:lang="pt">Barretes frígios</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Phrygian cap</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Berretto frigio</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Phrygische Mütze</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:prefLabel xml:lang="nl">Phrygische mutsen</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10045">
    <skos:prefLabel xml:lang="pt">Jaqueta</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Jacket</skos:prefLabel>
    <skos:altLabel xml:lang="pt">Casaco</skos:altLabel>
    <skos:altLabel xml:lang="sr">Sako</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Jasje</skos:prefLabel>
    <skos:altLabel xml:lang="fr">veston</skos:altLabel>
    <skos:prefLabel xml:lang="el">Ζακέτα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Jacka</skos:prefLabel>
    <skos:altLabel xml:lang="fr">blouson</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:scopeNote xml:lang="en">"A short coat for the upper body made in various forms and lengths but usually no lower than the waist; worn separately or as part of a suit. Also, similar outer garments worn for warmth or protection from the weather. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Jakna</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Jacke</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Chaqueta</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Veste</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="it">Giacca</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046167\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046167"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10330">
    <skos:prefLabel xml:lang="es">Dossier de prensa</skos:prefLabel>
    <skos:prefLabel xml:lang="it">cartella stampa</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Pressemappe</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="nl">Persmap</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="pt">Dossier de imprensa</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10329"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">dépliant publicitaire</skos:prefLabel>
    <skos:altLabel xml:lang="fr">dossier de presse</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Press materijal</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φάκελος</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Pressmapp</skos:prefLabel>
    <skos:prefLabel xml:lang="en">press folder</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Dossiê de imprensa</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10031">
    <skos:altLabel xml:lang="de">Culottes</skos:altLabel>
    <skos:scopeNote xml:lang="en">Wide trousers that form the exterior silhouette of a skirt. May have a panel to cover the joint between the legs. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="pt">Ceroulas de senhora (calções interiores pelo joelho)</skos:altLabel>
    <skos:prefLabel xml:lang="it">Culotte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10030"/>
    <skos:prefLabel xml:lang="nl">Broekrok</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Culottes</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Culottes</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kniebundhosen</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Suknja-pantalone</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046158\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046158"/>
    <skos:altLabel xml:lang="fr">Jupe-culotte</skos:altLabel>
    <skos:prefLabel xml:lang="fr">Jupe-culotte</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ζυπ-κυλότ</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Falda pantalón</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Knäbyxor</skos:prefLabel>
    <skos:altLabel xml:lang="es">Culotte</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10396">
    <skos:prefLabel xml:lang="es">Textiles no tejidos</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10366"/>
    <skos:prefLabel xml:lang="sr">Netkane tehnike</skos:prefLabel>
    <skos:prefLabel xml:lang="en">non-weaving techniques</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="nl">Niet-weeftechnieken</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">non tissé</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Niet-gewoven technieken</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Faserfließstoffe</skos:altLabel>
    <skos:altLabel xml:lang="de">Verbundstoffe</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Técnicas de não tecelagem</skos:prefLabel>
    <skos:altLabel xml:lang="fr">techniques sans tissage</skos:altLabel>
    <skos:prefLabel xml:lang="de">Techniken nicht gewebter Stoffe</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μη υφασμένα</skos:prefLabel>
    <skos:altLabel xml:lang="de">Vliesstoffe</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Icke-vävda tekniker</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10185">
    <skos:prefLabel xml:lang="pt">Acessórios de cabelo</skos:prefLabel>
    <skos:altLabel xml:lang="de">Accessoires für die Haare</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Accessoirer för håret</skos:prefLabel>
    <skos:altLabel xml:lang="de">Frisurzubehör</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Ukrasi za kosu</skos:prefLabel>
    <skos:prefLabel xml:lang="en">hair accessory</skos:prefLabel>
    <skos:altLabel xml:lang="fr">accessoires pour cheveux</skos:altLabel>
    <skos:prefLabel xml:lang="it">Accessori per capelli</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">accesoires de cheveux</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10181"/>
    <skos:prefLabel xml:lang="es">Accesorios para el cabello</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Αξεσουάρ μαλλιών</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Haaraccessoires</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Haaraccessoires</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300225910\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300225910"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10158">
    <skos:prefLabel xml:lang="en">earring (jewelry)</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Ear ornaments worn suspended from a bent wire or a thin loop passed through a hole pierced in the lobe of the ear or clipped or screwed to the lobe. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="fr">boucles d'oreille</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10157"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Naušnica</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Orecchini</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σκουλαρίκια</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300045998\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300045998"/>
    <skos:prefLabel xml:lang="es">Pendiente</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Oorbellen</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Ohrring (Schmuck)</skos:prefLabel>
    <skos:altLabel xml:lang="es">zarcillo</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Örhängen</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Brincos (joalharia)</skos:prefLabel>
    <skos:altLabel xml:lang="sv">örhängar</skos:altLabel>
    <skos:altLabel xml:lang="es">Aro</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10258">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Snörskor</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Sapatos de atacadores</skos:prefLabel>
    <skos:prefLabel xml:lang="en">tied shoes</skos:prefLabel>
    <skos:prefLabel xml:lang="it">scarpe allacciate</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Zapatos de cordones</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Schnürschuh</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">chaussures à lacets</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Παπούτσια δετά</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Cipele na šniranje</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Veterschoenen</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10300">
    <skos:prefLabel xml:lang="nl">Modepop</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Manequim pequeno</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Maniquí en miniatura</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10299"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="sv">Provdocka</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Mala krojačka lutka</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Kleine Schaufensterpuppe</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mannequin</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Small mannequin</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Liten skyltdocka</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Manichino in miniatura</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Κούκλα ραπτικής μικρή</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Mannequin</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10350">
    <skos:prefLabel xml:lang="en">animal origin fibres</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Fibras de origen animal</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Vezels van dierlijke oorsprong</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Animaliska fibrer</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Υλες ζωικής προέλευσης</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10347"/>
    <skos:prefLabel xml:lang="nl">Dierlijke vezels</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Fasern tierischen Ursprungs</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Materials"/>
    <skos:prefLabel xml:lang="pt">Fibras de origem animal</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">fibres d'origine animale</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300386673\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300386673"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Vlakna životinjskog porekla</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10105">
    <skos:prefLabel xml:lang="es">Calzoncillos</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Κυλότα/Σώβρακο</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Mutande</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Drawers</skos:prefLabel>
    <skos:altLabel xml:lang="en">briefs</skos:altLabel>
    <skos:altLabel xml:lang="nl">Lange onderbroeken</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210555\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210555"/>
    <skos:prefLabel xml:lang="sr">Ženske gaće</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Unterhosen</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">culotte</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Directoire</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="de">Schlüpfer</skos:altLabel>
    <skos:prefLabel xml:lang="sv">Trosor</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:altLabel xml:lang="sv">Kalsonger</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Bifurcated undergarments extending from the waist to the top of the knees or below. For similar garments with elastic or a band near the knees or below, use ""bloomers."""</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="en">knickers</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Cuecas</skos:prefLabel>
    <skos:altLabel xml:lang="fr">caleçon</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10630">
    <skos:prefLabel xml:lang="el">Τσάντα φάκελος</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">pochette</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Envelope Bag</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10134"/>
    <skos:prefLabel xml:lang="es">Clutch</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Pismo tašna</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kuvertväskor</skos:prefLabel>
    <skos:altLabel xml:lang="de">Handtasche in Form eines Briefumschlags</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">Envelope bag</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10243">
    <skos:prefLabel xml:lang="de">Handschuhe</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300148821\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300148821"/>
    <skos:prefLabel xml:lang="fr">gants</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10242"/>
    <skos:prefLabel xml:lang="sr">Rukavice</skos:prefLabel>
    <skos:prefLabel xml:lang="en">glove</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Guanti</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:scopeNote xml:lang="en">"Coverings for the hand enclosing each finger separately, sometimes extending over the wrist and arm. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Γάντια</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Luvas</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Guante</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Handschoenen</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Handskar</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10046">
    <skos:scopeNote xml:lang="en">General term for a form of outerwear of differing lengths and having some shaping. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="de">Umhang</skos:altLabel>
    <skos:prefLabel xml:lang="es">Manto</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="fr">Cape</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="sr">"Ogrtač, kaput"</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Capa (com ou sem mangas)</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Mantella</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Mantil</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mantel</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Mantel</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212298\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212298"/>
    <skos:prefLabel xml:lang="el">Μανδύας</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="de">Überwurf</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:prefLabel xml:lang="fr">Mante</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Mantle</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10119">
    <skos:prefLabel xml:lang="sr">Svadbena odeća</skos:prefLabel>
    <skos:altLabel xml:lang="fr">vêtements de mariage</skos:altLabel>
    <skos:prefLabel xml:lang="el">Νυφικό</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300255177\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300255177"/>
    <skos:prefLabel xml:lang="de">Hochzeitskleidung</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10117"/>
    <skos:prefLabel xml:lang="sv">Bröllopskläder</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Vestidos de novia</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Weddingclothes</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">tenue de mariage</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Abiti da matrimonio</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="nl">Trouwkledij</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Fatos de casamento</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Trouwkleding</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10321">
    <skos:prefLabel xml:lang="fr">livre</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Livro</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300028051\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300028051"/>
    <skos:prefLabel xml:lang="de">Buch</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Book</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Boek</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:scopeNote xml:lang="en">"Items comprising a collection of leaves of paper, parchment, wood, stiffened textile, ivory, metal tablets, or other flat material, that are blank, written on, or printed, and are strung or bound together in a volume. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Knjiga</skos:prefLabel>
    <skos:prefLabel xml:lang="it">libro</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Libro</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βιβλίο</skos:prefLabel>
    <skos:prefLabel xml:lang="en">book</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10143">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300216195\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300216195"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="nl">Zakdoek</skos:prefLabel>
    <skos:altLabel xml:lang="de">Schnupftuch</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Maramica</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mouchoir</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Μαντίλι χειρός</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pañuelo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Näsdukar</skos:prefLabel>
    <skos:altLabel xml:lang="de">Facelet</skos:altLabel>
    <skos:prefLabel xml:lang="de">Taschentuch</skos:prefLabel>
    <skos:altLabel xml:lang="de">Fazzoletto</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="it">Fazzoletto da mano</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Pieces of cloth, usually square, varying in size and material, carried for usefulness or as a costume accessory. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="pt">Lenço de mão</skos:prefLabel>
    <skos:prefLabel xml:lang="en">handkerchief</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10644">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Stickad mössa</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Σκουφάκι</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Gorro</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Strickmütze</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Knit cap</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Pletena kapa</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">bonnet</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10340">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300054789\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300054789"/>
    <skos:prefLabel xml:lang="el">web cast</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Conferentie</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">conférence</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Konferenz</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Conferência</skos:prefLabel>
    <skos:prefLabel xml:lang="en">conference</skos:prefLabel>
    <skos:prefLabel xml:lang="it">conferenza</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Conferencia</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Meetings of two or more people for discussing matters of common concern, usually a formal or public exchange of views. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sv">Konferens</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10336"/>
    <skos:prefLabel xml:lang="sr">Konferencija</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10310">
    <skos:altLabel xml:lang="fr">animation</skos:altLabel>
    <skos:prefLabel xml:lang="it">animazione</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">film d'animation</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300053160\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300053160"/>
    <skos:prefLabel xml:lang="pt">Animação</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Κινούμενα σχέδια</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Animacija</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Refers to the process of making still images appear to move, particularly by the technique of photographing drawings or objects in progressive stages of performing an action, so that movement is simulated when the images are projected as a series in quick succession. (AAT)"</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Animatie</skos:prefLabel>
    <skos:prefLabel xml:lang="en">animation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="es">Animación</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Animation</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Animation</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10663">
    <skos:scopeNote xml:lang="en">Invisible two-sided tape used on delicate skin or fabric (UCLA)</skos:scopeNote>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10297"/>
    <skos:prefLabel xml:lang="en">Toupee tape</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10145">
    <skos:prefLabel xml:lang="en">parasol</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sombrinha</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Parasol</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Ombrellino parasole</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Suncobran</skos:prefLabel>
    <skos:altLabel xml:lang="fr">parasols</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046218\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046218"/>
    <skos:prefLabel xml:lang="el">Παρασόλι</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Sombrilla</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Sonnenschirm</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Small umbrellas used as a sunshade or simply carried as a fashionable accessory. (AAT)</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:altLabel xml:lang="es">Quitasol</skos:altLabel>
    <skos:prefLabel xml:lang="fr">ombrelle</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Parasoll</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10196">
    <skos:prefLabel xml:lang="sr">Kape koje se nose na temenu</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Kalotter</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046125\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046125"/>
    <skos:prefLabel xml:lang="it">Zucchetto</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Close-fitting caps, often made of mesh, wool, silk, or velvet, covering only the crown of the head. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Καπελάκι</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Casquete</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Barrete</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Kalotten</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">calotte</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Kalotje</skos:altLabel>
    <skos:prefLabel xml:lang="de">Scheitelkäppchen</skos:prefLabel>
    <skos:prefLabel xml:lang="en">skullcap (cap)</skos:prefLabel>
    <skos:altLabel xml:lang="de">Pileolus</skos:altLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10189"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10044">
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10038"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sr">Frak</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Levita</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">Redingote</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ρετινγκότα</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Sobrecasaca</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Geklede jas</skos:altLabel>
    <skos:prefLabel xml:lang="en">Frock coat</skos:prefLabel>
    <skos:altLabel xml:lang="sv">Smoking</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Redingote (jassen)</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046146\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046146"/>
    <skos:prefLabel xml:lang="de">Gehrock</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Bonjour</skos:prefLabel>
    <skos:scopeNote xml:lang="en">Men's and boy's coats with a close-fitting body and a rather full knee-length skirt. (AAT)</skos:scopeNote>
    <skos:altLabel xml:lang="es">Redingote</skos:altLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10381">
    <skos:altLabel xml:lang="nl">Satijn</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="en">satin</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">satin</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10371"/>
    <skos:prefLabel xml:lang="pt">Cetim</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Woven textile characterized by a basic binding system or weave with smooth, shiny surface formed by long warp floats. Generally, each warp end passes over four or more adjacent weft picks and under the next one. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="sr">Saten</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Raso</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300132902\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300132902"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="sv">Satin</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Satin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="nl">Satijnbinding</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Σατέν</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10159">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sr">Tijara</skos:prefLabel>
    <skos:prefLabel xml:lang="en">tiara</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10156"/>
    <skos:prefLabel xml:lang="fr">tiare</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046046\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046046"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="pt">Tiara</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="sv">Tiaror</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Tiara</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Tiara</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Tiara</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Type of head ornament worn by women, usually royalty or members of the nobility, on state or formal occasions; usually in the form of a curved (less than semicircular) vertical band with a central peak, encrusted with diamonds or other gems. The term originally applied to the headdresses worn by the ancient Persians; it is now also applied to the triple crown of the pope. (AAT)"</skos:scopeNote>
    <skos:prefLabel xml:lang="nl">Tiara's</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Τιάρα</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10395">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Techniques"/>
    <skos:prefLabel xml:lang="fr">nœud espagnol</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10392"/>
    <skos:prefLabel xml:lang="sr">Španski čvor</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Spansk knut</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ισπανικός κόμπος</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Spanish knot</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Spaanse knoop</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Nudo español</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Ponto espanhol</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Spanischer Knoten</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10030">
    <skos:prefLabel xml:lang="fr">Bas du corps</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Βασικά ενδύματα κάτω κορμού</skos:prefLabel>
    <skos:prefLabel xml:lang="de">wchtigste Kleidungsstücke unterhalb der Taille getragen</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Principali capi di abbigliamento per la parte bassa del corpo</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Dräktdelar underkropp</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Glavni odevni predmeti - donji deo tela</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10002"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="nl">Bovenkleding voor het onderlichaam</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="pt">Vestuário principal parte inferior corpo</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Prendas principales parte inferior del cuerpo</skos:prefLabel>
    <skos:altLabel xml:lang="fr">Vêtement principaux partie inférieure du corps</skos:altLabel>
    <skos:prefLabel xml:lang="en">Main garments lower body</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:altLabel xml:lang="de">Hauptkleidungsstücke unterhalb der Taille getragen</skos:altLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300209279\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300209279"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10186">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="en">wig</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Parrucche</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Peluca</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Peruca</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10185"/>
    <skos:prefLabel xml:lang="de">Perücke</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Pruiken</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Perika</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">perruque</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300046049\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300046049"/>
    <skos:scopeNote xml:lang="en">Head coverings of artificial hair knotted into a shaped net foundation. (AAT)</skos:scopeNote>
    <skos:prefLabel xml:lang="el">Περούκα</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Peruker</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10104">
    <skos:altLabel xml:lang="de">Einlagen zur Brustvergrößerung</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="sv">Bröstinlägg</skos:prefLabel>
    <skos:altLabel xml:lang="en">Bust forms</skos:altLabel>
    <skos:prefLabel xml:lang="sr">Umeci za grudi</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Busto stringivita</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Almofadas de busto</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="el">Ενισχυτικά στήθους</skos:prefLabel>
    <skos:altLabel xml:lang="nl">beha-vulling</skos:altLabel>
    <skos:prefLabel xml:lang="de">Brustverbesserer</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10079"/>
    <skos:prefLabel xml:lang="nl">BH-vulling</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Relleno de pecho</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">amplificateurs de poitrine</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Bust improvers</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10301">
    <skos:scopeNote xml:lang="en">"The Visual and Verbal Communication section encompasses artifacts, including images and written documents, whose primary and original function is to communicate ideas, concepts, or aesthetic experience through visual or verbal media. Though almost any artifact can be considered to communicate visually something about its maker, its origin, or its use, this section concerns those items originally produced with the purpose of transmitting an informational, symbolic, or aesthetic message. (AAT)"</skos:scopeNote>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Comunicação visual e verbal</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Visuelle und verbale Kommunikation</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Comunicación verbal y visual</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Lemmario</skos:prefLabel>
    <skos:prefLabel xml:lang="en">Visual and verbal communication</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Οπτική και προφορική επικονωνία</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Visuell och verbal kommunikation</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Visuele en verbale communicatie</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">communication verbale et non verbale</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300264552\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300264552"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10000"/>
    <skos:prefLabel xml:lang="sr">Vizuelna i verbalna komunikacija</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10257">
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300210037\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300210037"/>
    <skos:prefLabel xml:lang="de">Mokassin</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Mokasine</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Μοκασίνια</skos:prefLabel>
    <skos:prefLabel xml:lang="en">moccasins</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">mocassins</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Mocassins</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Heelless footwear made entirely of soft leather, as deerskin, with the sole brought up to form part or all of the upper portion covering the foot, and with a back seam; worn originally by indigenous peoples of North America. Also, shoes of similar construction, with hard sole and heel attached, made of soft or hard leather or leatherlike material. (AAT)"</skos:scopeNote>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10250"/>
    <skos:prefLabel xml:lang="es">Mocasines</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="it">mocassini</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Mockasiner</skos:prefLabel>
    <skos:prefLabel xml:lang="nl">Moccasins</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/Type">
    <skos:prefLabel xml:lang="en">Type</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#ConceptScheme"/>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10118">
    <skos:altLabel xml:lang="pt">Fato de baptizado</skos:altLabel>
    <skos:altLabel xml:lang="nl">Doopjurk</skos:altLabel>
    <skos:prefLabel xml:lang="en">Christening gown</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Taufkleid</skos:prefLabel>
    <skos:altLabel xml:lang="en">Baptismal clothing</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Doopkleding</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">costume de baptême</skos:prefLabel>
    <skos:altLabel xml:lang="es">Traje de cristianar</skos:altLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="el">Βαφτιστικό ένδυμα</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10117"/>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:scopeNote xml:lang="en">"Long dresses, typically elaborately trimmed with lace, beading, or embroidery, and worn by infants for christening. (AAT)"</skos:scopeNote>
    <skos:altLabel xml:lang="fr">robes de baptême</skos:altLabel>
    <skos:prefLabel xml:lang="es">Traje de bautizo</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Veste battesimale</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Vestido de baptismo</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300265752\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300265752"/>
    <skos:prefLabel xml:lang="sr">Haljine za krštenje/odeća za krštenje</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sv">Dopkläder</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10242">
    <skos:prefLabel xml:lang="nl">Handbedekking</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Αξεσουάρ χεριών</skos:prefLabel>
    <skos:prefLabel xml:lang="en">handwear</skos:prefLabel>
    <skos:prefLabel xml:lang="pt">Peças de mão</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10240"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="sr">Rukavica</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="es">Accesorios de mano</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">ganterie</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Plagg för händer</skos:prefLabel>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300212570\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300212570"/>
    <skos:prefLabel xml:lang="de">Handbekleidung</skos:prefLabel>
    <skos:prefLabel xml:lang="it">Accessori per mani</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10643">
    <skos:prefLabel xml:lang="sr">Perje</skos:prefLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="fr">Plume</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10185"/>
    <skos:prefLabel xml:lang="sv">Fjäder</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">Feather</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="de">Feder</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φτερό</skos:prefLabel>
    <skos:prefLabel xml:lang="es">Pluma</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10311">
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="es">Película</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Ταινία</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Film</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Film</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Film</skos:prefLabel>
    <skos:altLabel xml:lang="it">video</skos:altLabel>
    <skos:prefLabel xml:lang="nl">Film</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10277"/>
    <skos:prefLabel xml:lang="it">film</skos:prefLabel>
    <skos:prefLabel xml:lang="fr">film</skos:prefLabel>
    <skos:prefLabel xml:lang="en">film</skos:prefLabel>
    <skos:scopeNote xml:lang="en">"Works presented in the form of a series of pictures carried on photographic film or video tape, presented to the eye in such rapid succession as to give the illusion of natural movement. For the study and practice of filmmaking and motion pictures as an art and form of expression, use ""film (performing arts)."" (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300136900\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300136900"/>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:altLabel xml:lang="nl">Speelfilm</skos:altLabel>
    <skos:prefLabel xml:lang="pt">Filme</skos:prefLabel>
  </skos:Concept>
  <skos:Concept rdf:about="http://thesaurus.europeanafashion.eu/thesaurus/10144">
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Type"/>
    <skos:prefLabel xml:lang="en">lorgnettes</skos:prefLabel>
    <skos:altLabel xml:lang="it">occhialini con una impugnatura a stanghetta</skos:altLabel>
    <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
    <skos:prefLabel xml:lang="pt">Lornhões</skos:prefLabel>
    <skos:prefLabel xml:lang="sr">Lornjon</skos:prefLabel>
    <skos:prefLabel xml:lang="de">Lorgnon</skos:prefLabel>
    <skos:broader rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/10133"/>
    <skos:prefLabel xml:lang="es">Impertinentes</skos:prefLabel>
    <skos:inScheme rdf:resource="http://thesaurus.europeanafashion.eu/thesaurus/Subject"/>
    <skos:prefLabel xml:lang="fr">longue vue</skos:prefLabel>
    <skos:altLabel xml:lang="fr">lorgnettes</skos:altLabel>
    <skos:prefLabel xml:lang="it">Lorgnette</skos:prefLabel>
    <skos:prefLabel xml:lang="sv">Lornjett</skos:prefLabel>
    <skos:prefLabel xml:lang="el">Φασαμέν</skos:prefLabel>
    <skos:altLabel xml:lang="nl">Knijpbril</skos:altLabel>
    <skos:scopeNote xml:lang="en">"Type of eyeglasses, often more decorative than functional, hand-held to the eye and usually mounted on a long ornamental handle. (AAT)"</skos:scopeNote>
    <skos:exactMatch rdf:resource="http://www.getty.edu/vow/AATFullDisplay?find=300221440\&amp;logic=AND\&amp;note=\&amp;english=N\&amp;prev_page=1\&amp;subjectid=300221440"/>
    <skos:prefLabel xml:lang="nl">Lorgnet</skos:prefLabel>
  </skos:Concept>
  </xsl:variable>
</xsl:stylesheet>    
