<?php

/**
 * SIWA Software, version 0.5
 * Last update: January 19th, 2010
 *
 * Developer: Thomas Beekman, The National Library of the Netherlands
 * E-mail: tsbeekman@gmail.com
 * Date: December 2009 - February 2010
 *
 * Simple PHP Proxy for SIWA Implementations. 
 *
 */
	// Global variables
	$currDomain = "81.204.248.129";
 
	// Set defaults
	$method = "GET";
	$parameters = "";
 
	// Get the url
	if (isset($_REQUEST['url']))
		$url = $_REQUEST['url'];
	else
		die("No url given!<br>Usage: proxy.php?url=[the-url]");
	
	// Get Parameters (if any)
	if (isset($_REQUEST['pars']))
		$parameters = $_REQUEST['pars'];
	
	// Get the desired method	
	if (isset($_REQUEST['method']))
		$method = $_REQUEST['method'];
	
	// Rewrite URL if it is the same as the portal's
	if (strstr($url, $currDomain) == true)
		$url = str_replace($currDomain, "localhost", $url);
	
	// Check the method and execute! (Defaulting again to GET)
	if ($method == "POST") {		
		// Open the Curl session
		$session = curl_init($url);

		// Don't return HTTP headers. Do return the contents of the call
		curl_setopt($session, CURLOPT_HEADER, false);
		curl_setopt($session, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($session, CURLOPT_POST, true);
		curl_setopt($session, CURLOPT_POSTFIELDS, $parameters);    

		// Make the call
		$html = curl_exec($session);

		// The web service returns HTML. Set the Content-Type appropriately
		header("Content-Type: text/html");

		echo $html;
		curl_close($session);	
	} else {
		// Open the Curl session
		$session = curl_init($url);

		// Don't return HTTP headers. Do return the contents of the call
		curl_setopt($session, CURLOPT_HEADER, false);
		curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

		// Make the call
		$html = curl_exec($session);

		// The web service returns HTML. Set the Content-Type appropriately
		/* Get the content type from CURL */
		$content_type = curl_getinfo($session, CURLINFO_CONTENT_TYPE );
 
		/* Get the MIME type and character set */
		$mime = "text/html";
		/*preg_match( '@([\w/+]+)(;\s+charset=(\S+))?@i', $content_type, $matches );
		if ( isset( $matches[1] ) )
			$mime = $matches[1];
		if ( isset( $matches[3] ) )
			$charset = $matches[3];*/

		//echo $mime;
		header("Content-Type: ".$mime); //text/html");

		echo $html;
		curl_close($session);
	}

	
	
	
?>
