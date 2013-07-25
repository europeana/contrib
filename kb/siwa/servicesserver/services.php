<?php
	/**
	 * SIWA Software, version 0.5
	 * Last update: February 24th, 2010
	 *
	 * Developer: Thomas Beekman, The National Library of the Netherlands
	 * E-mail: tsbeekman@gmail.com
	 * Date: December 2009 - February 2010
	 *
	 * This software manages the Services Integration on a particular portal by reading a JSON version of the 
	 * SIWA (Scheme for Integration of Web Applications, http://dev.theeuropeanlibrary.org/siwa/) Services.xml,
	 * adding the onclick function to the siwa spans implementing a context menu and interpreting the descriptions
	 * in order to invoke the services requested. This software depends on the following files;
	 *
	 * SIWA Software
	 * - siwa.js: The main code which initializes the services and hooks itself onto spans of the class siwa;
	 * - cmenu.js: The code for generating the context menu, which is shown when the user clicks on a siwa span;
	 * - proxy.js: The code how to use the proxy, if any;
	 * - cmenu.css: The style code for the context menu and the siwa spans.
	 *
	 * ServicesServer
	 * - This code will serve the services.xml with the Services Descriptions in it, and is able to translate
	 *   it to a JSON Object which can be used by the SIWA Software. There is a PHP version and a Java version
	 *   available of this software.
	 *
	 */
	
	error_reporting(E_ALL);	
	ini_set("display_errors", 1);
	
	require_once("lib/inc_class_services.php");	
		
	$servicesXML = "services13.xml";	
	
	if (isset($_REQUEST['uncookie'])) {
		setcookie("siwasrc", "", time()-3600);
	}		
	
	if (isset($_COOKIE['siwasrc'])) {		
		$srcUrl = $_COOKIE['siwasrc'];
		$servicesXML = $srcUrl;
	}
	else if (isset($_REQUEST['src'])) {
		$srcUrl = $_REQUEST['src'];
		$servicesXML = $srcUrl;
		if (isset($_REQUEST['lock']))
			setcookie("siwasrc", $srcUrl, mktime (0, 0, 0, 12, 21, 2012));
	}
	else if (isset($_REQUEST['version'])) {
		$version = $_REQUEST['version'];
		if ($version == "1.2")
			$servicesXML = "services12.xml";	
		else if ($version == "1.3")
			$servicesXML = "services13.xml";	
		else if ($version == "1.3t")
			$servicesXML = "services13.xml";	
		else if ($version == "kb")
			$servicesXML = "services_kb.xml";	
		else if ($version == "tel")
			$servicesXML = "services_tel.xml";			
	}	
	
	$xml = new DOMDocument();		
	$xml->load($servicesXML);		
	
	$services = array();	
	
	$element = $xml->getElementsByTagName("service");	
	$services = xml2array($xml);
	
		
	if(isset($_REQUEST['format'])) 
		$format = $_REQUEST['format'];
	else
		$format = "html";
	
	if ($format == "json") {		
		if (isset($_REQUEST['callback']))			
			echo $_REQUEST['callback'] . "(" . json_encode($services) . ");";						
		else			
			echo json_encode($services);					
	}
	else if ($format == "jsonhr") {
		$jsonhr = json_encode($services);
		$jsonhr = str_replace("[", "[\n", $jsonhr);
		$jsonhr = str_replace("{", "{\n", $jsonhr);
		$jsonhr = str_replace("}", "\n}", $jsonhr);
		$jsonhr = str_replace("]", "\n]", $jsonhr);
		$jsonhr = str_replace(",", ",\n", $jsonhr);
		$ident = 0;
		$jsonhr = explode("\n", $jsonhr);
		
		for($i=0; $i<count($jsonhr); $i++) {
			$currLine = $jsonhr[$i];
			
			if (stristr($currLine, "]") || stristr($currLine, "}"))
				$ident--;
			for($j=0; $j<$ident;$j++)
				echo "&nbsp;&nbsp;";
					
			echo $currLine."<br>";
			if (stristr($currLine, "[") || stristr($currLine, "{"))
				$ident++;			
		}
	}
	else if ($format == "php") {
		print_r($services);
	}	
	else if ($format == "xml") {
		$fp = fopen($servicesXML, "r");
		$fr = fread($fp, filesize($servicesXML));
		echo $fr;
		fclose($fp); 
	}
	else {
		
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>Services.xml</title>

<style type="text/css">
	table { 
		border: 1px solid #C0C0C0; 
		background-color: white; 		
	}
	th { 				
		border: 1px solid black;
        text-align: center; 
		background-color: #FFFFFF; 
	}
	td { 				
		border: 1px solid black;
        text-align: left; 
		background-color: #FFFFCC; 
	}
</style>

</head>
<body>
<h1>Services overview</h1>

<?php	
	$servicesCount = count($services);
	echo "<h3>Number of services: " . $servicesCount . "</h3>";
?>
	
<table>
<tr>
<th>#</th>
<?php
	// Make table header (by looking through the array)
	$headerArray = array();

	for ($i=0; $i<$servicesCount; $i++ ) {
		$currService = $services[$i];
		$serviceCount = count($currService);
		
		$currServiceHeaders = array_keys($currService);
		
		for ($j=0; $j<count($currServiceHeaders); $j++) {
			$currHeader = $currServiceHeaders[$j];
			if (!in_array($currHeader, $headerArray)) {
				$headerArray[] = $currHeader;
				echo "<th>".$currHeader."</th>";
			}
		}	
	}	
	echo "</tr>";
	

	for($i=0; $i<count($services);$i++) {
		$service = $services[$i];		
			
		echo "<tr>";
		echo "<td>".($i+1)."</td>";	
		for ($j=0; $j<count($headerArray); $j++) {
			if (isset($service[$headerArray[$j]])) {
				$contents = $service[$headerArray[$j]];
				if (is_array($contents)) {
					echo "<td>".getArrayContents($contents)."</td>";
				} else {
					echo "<td>".$service[$headerArray[$j]]."</td>";
				}
			}
			else
				echo "<td></td>";
		
		}		
		
		echo "</tr>";	
	}	
?>

</table>
</body>
</html>
<?php
}
?>