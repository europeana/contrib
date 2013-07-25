<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
	<title>Europeana Geoparsing Service - v1.0 beta</title>
    <meta http-equiv="content-type" content="text/html;charset=utf-8"/>
	<link href="<c:url value="/styles/screen.css" />" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/styles/geoparser.css" />" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/styles/styles.css" />" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/styles/econnect.css" />" rel="stylesheet" type="text/css"/>
	
	<script language="javascript" type="text/javascript">

	var record1='<record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:europeana="http://www.europeana.eu">       <dc:title>Herrbacka skogsmark och areal</dc:title>        <dcterms:issued>2008-03-11</dcterms:issued>        <dcterms:spatial>Finland</dcterms:spatial>        <dcterms:spatial>Östra Nyland</dcterms:spatial>        <dcterms:spatial>Borgå</dcterms:spatial>       <dc:description>Herrbacka skogsmark och areal. Odaterad.</dc:description>        <dcterms:isPartOf>Stensböle gårds arkiv</dcterms:isPartOf>        <dcterms:isPartOf>SLSA 1070</dcterms:isPartOf>        <dc:language>swe</dc:language>        <dc:publisher>Svenska litteratursällskapet i Finland, HLA</dc:publisher>        <dc:source>SLSA 1070</dc:source>        <dc:subject>ägande</dc:subject>        <dc:subject>jordinnehav</dc:subject>        <dc:subject>byar</dc:subject>        <dc:type>kartor</dc:type>        <dc:type>map</dc:type>        <dc:format>image/jpeg</dc:format>       <dcterms:extent>355 x 440 mm</dcterms:extent>        <dc:identifier>HLA:slsa1070_k14</dc:identifier>        <europeana:uri>http://www.europeana.eu/resolve/record/01201/3C2F001170A54C7A789861F17CD554F1E7359EFC</europeana:uri>        <dc:rights>fri</dc:rights>        <dcterms:isFormatOf>slsa1070_k14.tif</dcterms:isFormatOf>       <europeana:object>http://www.sls.fi/databasen/slsa1070/slsa1070_k14.jpg</europeana:object>        <europeana:provider>Svenska litteratursällskapet i Finland</europeana:provider>        <europeana:isShownBy>http://www.sls.fi/databasen/slsa1070/slsa1070_k14.jpg</europeana:isShownBy>        <europeana:hasObject>true</europeana:hasObject>        <europeana:country>finland</europeana:country>       <europeana:type>IMAGE</europeana:type>        <europeana:language>fi</europeana:language> </record>';	
	var record2='<record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:europeana="http://www.europeana.eu">      <dc:title>Facciata dell\'Istituto Professionale per l\'Industria e l\'Artigianato "A. Pacinotti",Pistoia.</dc:title>        <dc:creator>Istituto e Museo di storia della Scienze, Firenze, Italia</dc:creator>        <dc:publisher>Istituto e Museo di storia della Scienze, Firenze, Italia</dc:publisher>       <dc:type>Immagine</dc:type>        <europeana:type>IMAGE</europeana:type>        <dc:format>xhtml</dc:format>        <europeana:isShownAt>http://brunelleschi.imss.fi.it/isd/iisd.asp?c=924</europeana:isShownAt>        <europeana:uri>http://www.europeana.eu/resolve/record/02305/3AF195221B3A9B7F8FE5B2FE59F69DC7C6C1E967</europeana:uri>        <dc:language>it</dc:language>        <dcterms:spatial>Pistoia,Pistoia</dcterms:spatial>        <dc:rights>Istituto e Museo di storia della Scienze, Firenze, Italia</dc:rights>        <europeana:hasObject>false</europeana:hasObject>        <europeana:country>italy</europeana:country>        <europeana:provider>Istituto e Museo di Storia della Scienza</europeana:provider>        <europeana:language>it</europeana:language>     </record>';	
	var record3='<record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:europeana="http://www.europeana.eu">    <dc:identifier >000B5F71-3325-4223-8433-A8017BEF382F</dc:identifier>       <europeana:uri>http://www.europeana.eu/resolve/record/05811/3C4DD780DD79FBA738AA7CDC7A7D023548D5D5BA</europeana:uri>        <europeana:isShownBy>http://r.culturalia.ro/m/do.aspx?guid=000B5F71-3325-4223-8433-A8017BEF382F</europeana:isShownBy>        <dc:type >StillImage</dc:type>       <dc:source>Muzeul Etnografic - REGHIN</dc:source>        <dc:title >Catrinta cu trup vânat</dc:title>        <dc:publisher >CIMEC - Institutul de Memorie Culturala,Bucharest, Romania</dc:publisher>        <dc:format >image/jpeg</dc:format>        <europeana:isShownAt>http://www.cimec.ro/scripts/PCN/Clasate/detaliu.asp?k=000B5F71-3325-4223-8433-A8017BEF382F</europeana:isShownAt>        <dc:creator >Frandes,Nastasia</dc:creator>        <dc:contributor >Români</dc:contributor>        <dc:subject >Catrinta</dc:subject>        <dc:subject >Port popular</dc:subject>        <dc:subject >amplasat la poale, vargi, decor geometric</dc:subject>        <dcterms:created >prim.jum.sec.XX</dcterms:created>       <europeana:year >0000</europeana:year>        <dcterms:created >1942</dcterms:created>        <europeana:year >1942</europeana:year>        <dcterms:provenance>România</dcterms:provenance>        <dcterms:provenance >Valea Gurghiu</dcterms:provenance>        <dcterms:provenance >Hodac</dcterms:provenance>        <dcterms:provenance >Frandes, Livia</dcterms:provenance>        <dcterms:medium >lâna, lânica, tesut 4 ite, ales printre fire, ales pestefire</dcterms:medium>        <dcterms:extent >L.ornament 27 cm.</dcterms:extent>        <dcterms:extent >97 cm.</dcterms:extent>        <dcterms:extent >57cm.</dcterms:extent>        <dc:description >Catrinta de forma dreptunghiulara tesuta în 4 ite din lâna albastru închis ("trup vînat"). La brâu, tivita manual. Pemarginile laterale câte 2 vrâste din fire de urzeala rosii, galbene, la poale câmp ornamental realizat din vargi alese printre si peste f</dc:description>       <dc:description >componenta a portului popular femeiesc</dc:description>        <europeana:object>http://r.culturalia.ro/FisiereResurse/000B5F71-3325-4223-8433-A8017BEF382F/000B5F71-3325-4223-8433-A8017BEF382F.jpg</europeana:object>        <dc:subject>Romania</dc:subject>        <dc:subject >România</dc:subject>        <dc:subject >Roumanie</dc:subject>        <dc:subject >Rumänien</dc:subject>       <dc:subject >Rumania</dc:subject>        <europeana:hasObject >true</europeana:hasObject>        <europeana:country >romania</europeana:country>        <europeana:provider>cIMeC - Institutul de Memorie Culturala</europeana:provider>        <europeana:type >IMAGE</europeana:type>        <europeana:language>ro</europeana:language> </record>';	
	var record4='<record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:europeana="http://www.europeana.eu">       <europeana:uri >http://www.europeana.eu/resolve/record/90101/3CB9B3F4452E8EE9D4486654CEB8EB4652D9682B</europeana:uri>        <dc:identifier >Cota (Call-Number): BN-PURL 13232/0</dc:identifier>        <dc:identifier >Cota (Call-Number): BN-PURL 13232/1</dc:identifier>        <dc:identifier >Cota (Call-Number): BN-PURL13232/2</dc:identifier>        <dc:identifier >Cota (Call-Number): BN-E. 4639 P.</dc:identifier>        <europeana:isShownAt>http://purl.pt/13232</europeana:isShownAt>        <dc:title >Belem Castle, Lisbon</dc:title>        <dc:creator >Stanfield, William Clarkson,1793-1867</dc:creator>        <dc:subject >725.94(469.411)"15"(084.1), por</dc:subject>        <dc:subject >623.1/.3(469.411)"15"(084.1), por</dc:subject>       <dc:subject >639.2-051"18"(084.1), por</dc:subject>        <dc:subject >762(=1:410)"18"(084.1), por</dc:subject>        <dc:description >Monografia</dc:description>       <dc:description >Vista da Torre de Belém rodeada de barcos de pesca e em 1º plano pescadores a puxar as redes</dc:description>        <dc:publisher >J.Murray</dc:publisher>        <dc:publisher >London</dc:publisher>        <dc:contributor >Finden, Edward, 1791-1857</dc:contributor>        <dc:date >1832</dc:date>        <europeana:year >1832</europeana:year>        <dc:type >material gráfico a duas dimensões</dc:type>        <europeana:type >IMAGE</europeana:type>        <dc:type >Visual gráfico</dc:type>        <dc:format >1 gravura, buril sobre aço, aguarelada, 17x24 cm</dc:format>        <dc:language>en</dc:language>        <europeana:isShownBy >http://purl.pt/homepage/13232/13232_13356.jpg</europeana:isShownBy>        <europeana:object>http://purl.pt/homepage/13232/13232_13356.jpg</europeana:object>        <europeana:hasObject >true</europeana:hasObject>        <europeana:country>portugal</europeana:country>        <europeana:provider >Biblioteca Nacional de Portugal</europeana:provider>        <europeana:language>pt</europeana:language> </record>';	

	</script>
	
</head>

<body>
	<!-- Header -->
	<div id="header" align="center">
	  <table align="center" border="1" cellpadding="0" cellspacing="0" width="976">
		<tbody><tr>
		  <td align="right" height="470" valign="bottom"></td>
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
            <h3>Europeana Geoparsing Service - v1.0 beta</h3>
			<br/>
			Unstructured text and semi-structured text (metadata records) may contain mentions to places and historical periods that are not directly usable by software applications. Geoparsing consists in automatically extracting structured information about places and historical periods from these textual resources. The Geoparser is a web service where users can provide textual sentences or metadata records, and it will reply with an XML document containing the geoparsing results.
        </td>
    </tr>
</table>
<br/>

   
                            <table class="tablemain">
                                <tr>
                                    <td id="news">
<form name="geoparsingForm" method="post" action="<c:url value="/geoparsing/freeText" />">
										<h3>Geoparse free text</h3>
                                    
									<br/>
                                    <center>
					<textarea name="freeText"  style="width:100%;height:150px;padding:5px;">Example: History of Architecture in Europe: the cases of Lisbon, Madrid and Paris of the 19th century.</textarea>			
									</center>  
									Display result in: 
									<select name="stylesheet">
									<option value="">XML</option>
									<option value="../styles/geoparse_result.xsl">HTML</option>
									</select>
									<input type="hidden" name="language" value="" />									
									<br />
					<input name="submitGeoparseFreeText" type="submit" value="Geoparse"/>                                      
</form>	
                                    </td>
                                </tr>
                            </table>
   
   <br/>
                           <table class="tablemain">
                                <tr>
                                    <td id="news">
<form name="geoparsingMetadataForm" method="post" action="<c:url value="/geoparsing/metadata" />">
										<h3>Geoparse ESE xml metadata record</h3>
									<br/>
									Note: only descriptive metadata elements are geoparsed (ex: title, description, coverage, etc.).
									<br/>
									<a onclick="javascript:document.geoparsingMetadataForm.metadata.value=record1; return false;" href="">Example A</a> 
									<a onclick="javascript:document.geoparsingMetadataForm.metadata.value=record2; return false;" href="">Example B</a>
									 <a onclick="javascript:document.geoparsingMetadataForm.metadata.value=record3; return false;" href="">Example C</a>
									  <a onclick="javascript:document.geoparsingMetadataForm.metadata.value=record4; return false;" href="">Example D</a>
                                    <center>
					<textarea name="metadata" style="width:100%;height:200px;padding:5px;">
&lt;record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:europeana="http://www.europeana.eu" &gt; 
      &lt;dc:title &gt;Herrbacka skogsmark och areal&lt;/dc:title&gt;  
      &lt;dcterms:issued &gt;2008-03-11&lt;/dcterms:issued&gt;  
      &lt;dcterms:spatial &gt;Finland&lt;/dcterms:spatial&gt;  
      &lt;dcterms:spatial &gt;Östra Nyland&lt;/dcterms:spatial&gt;  
      &lt;dcterms:spatial &gt;Borgå&lt;/dcterms:spatial&gt;  
      &lt;dc:description &gt;Herrbacka skogsmark och areal. Odaterad.&lt;/dc:description&gt;  
      &lt;dcterms:isPartOf &gt;Stensböle gårds arkiv&lt;/dcterms:isPartOf&gt;  
      &lt;dcterms:isPartOf &gt;SLSA 1070&lt;/dcterms:isPartOf&gt;  
      &lt;dc:language &gt;swe&lt;/dc:language&gt;  
      &lt;dc:publisher &gt;Svenska litteratursällskapet i Finland, HLA&lt;/dc:publisher&gt;  
      &lt;dc:source &gt;SLSA 1070&lt;/dc:source&gt;  
      &lt;dc:subject &gt;ägande&lt;/dc:subject&gt;  
      &lt;dc:subject &gt;jordinnehav&lt;/dc:subject&gt;  
      &lt;dc:subject &gt;byar&lt;/dc:subject&gt;  
      &lt;dc:type &gt;kartor&lt;/dc:type&gt;  
      &lt;dc:type &gt;map&lt;/dc:type&gt;  
      &lt;dc:format &gt;image/jpeg&lt;/dc:format&gt;  
      &lt;dcterms:extent &gt;355 x 440 mm&lt;/dcterms:extent&gt;  
      &lt;dc:identifier &gt;HLA:slsa1070_k14&lt;/dc:identifier&gt;  
      &lt;europeana:uri &gt;http://www.europeana.eu/resolve/record/01201/3C2F001170A54C7A789861F17CD554F1E7359EFC&lt;/europeana:uri&gt;  
      &lt;dc:rights &gt;fri&lt;/dc:rights&gt;  
      &lt;dcterms:isFormatOf &gt;slsa1070_k14.tif&lt;/dcterms:isFormatOf&gt;  
      &lt;europeana:object &gt;http://www.sls.fi/databasen/slsa1070/slsa1070_k14.jpg&lt;/europeana:object&gt;  
      &lt;europeana:provider &gt;Svenska litteratursällskapet i Finland&lt;/europeana:provider&gt;  
      &lt;europeana:isShownBy &gt;http://www.sls.fi/databasen/slsa1070/slsa1070_k14.jpg&lt;/europeana:isShownBy&gt;  
      &lt;europeana:hasObject &gt;true&lt;/europeana:hasObject&gt;  
      &lt;europeana:country &gt;finland&lt;/europeana:country&gt;  
      &lt;europeana:type &gt;IMAGE&lt;/europeana:type&gt;  
      &lt;europeana:language &gt;fi&lt;/europeana:language&gt; 
&lt;/record&gt;				
					</textarea> 		
									</center>  
									                                      									Display result in: 
									<select name="stylesheet">
									<option value="">XML</option>
									<option value="../styles/geoparse_result.xsl">HTML</option>
									</select>
									<br />
					<input name="submitGeoparseMetadata" type="submit" value="Geoparse"/>
</form>	
                                    </td>
                                </tr>
                            </table>

</center>
	</div>
   <br/>

	<!-- Foooter -->
	<div class="gradient" id="footer">
   <br/>
<p align="center"><img src="<c:url value="/images/eu-flag.gif" />" align="absmiddle" height="13"/> Co-funded by the European Community Programme <em>e</em>Content<em>plus</em>
		</p>  
	  <p align="center">EuropeanaConnect is solely responsible for it and that it does not represent the opinion of the Community <br/>
	  and that the Community is not responsible for any use that might be made of information contained on this site.</p>
	</div>
</body>
</html>




