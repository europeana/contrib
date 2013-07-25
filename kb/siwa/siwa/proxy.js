<!--

/**
 * SIWA Software, version 0.9
 * Last update: February 24th, 2010
 *
 * Developer: Thomas Beekman, The National Library of the Netherlands
 * E-mail: tsbeekman@gmail.com
 * Date: December 2009 - February 2010
 *
 * This code is used to initialize and use the proxy properly. Since every proxy implementation can 
 * differ, this code is excluded from the siwa.js to make clear which code should be altered in 
 * order to make the proxy work. 
 *
 */

    /**####################
	#	Global Variables  #
	#####################*/

var proxyUrl = "http://81.204.248.129/proxy/proxy.php";		// The absolute URL to the real proxy


    /**####################
	#	Proxy functions   #
	#####################*/

/**
 * Creates a HTTP GET request and links a handler to the output.
 * @param url the url to fetch. 
 * @param handler a method which should be invoked after the request taking the output as its parameter.
 */
function htmlGetReq(url, handler) {
	var xmlHttp = createXMLHttpRequest();
		
	url = encodeURIComponent(url);
	url = proxyUrl + "?url=" + url + "&method=GET";
	
	// Invoke the handler function when the http response is OK and Ready
	xmlHttp.onreadystatechange = function () {
		if ((xmlHttp.readyState == 4) && (xmlHttp.status == 200)) 		
			handler(xmlHttp.responseText);        		
	}	
	
    xmlHttp.open("GET", url, true);
    xmlHttp.send(null);
}

/**
 * Creates a HTTP Post request and links a handler to the output.
 * @param url the url to fetch. 
 * @param parameters parameters for the POST action.
 * @param handler a method which should be invoked after the request taking the output as its parameter.
 */
function htmlPostReq(url, parameters, handler) {
	var xmlHttp = createXMLHttpRequest();
	
	url = encodeURIComponent(url);
	parameters = encodeURIComponent(parameters);
	
	url = proxyUrl + "?url=" + url + "&pars=" + parameters + "&method=POST";
	
	// Invoke the handler function when the http response is OK and Ready
	xmlHttp.onreadystatechange = function () {
		if ((xmlHttp.readyState == 4) && (xmlHttp.status == 200)) 		
			handler(xmlHttp.responseText);        		
	}	
	
    xmlHttp.open("GET", url, true);
    xmlHttp.send(null);	
}


	/**############### 
	# AJAX Functions #
	################*/

// Mainly legacy code. Will be removed in future releases.	
function createXMLHttpRequest() {
    if (window.ActiveXObject)
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");    
    else if (window.XMLHttpRequest) 
        xmlHttp = new XMLHttpRequest(); 
	return xmlHttp;
}
 
function getRequest(query) {
    createXMLHttpRequest();
          
    var queryString = "whatever.php?" + query;
    
    xmlHttp.onreadystatechange = handleStateChange;
    xmlHttp.open("GET", queryString, true);
    xmlHttp.send(null);
}

function postRequest(query) {
    createXMLHttpRequest();
    
    var url = "whatever.php";
   
    xmlHttp.open("POST", url, true);
    xmlHttp.onreadystatechange = handleStateChange;
    xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");    
    xmlHttp.send(query);
}
    
function handleStateChange() {
    if (xmlHttp.readyState == 4) {
        if (xmlHttp.status == 200) 
            alert("test");
			//handler();        
    }
}

function parseResults() {
     alert(xmlHttp.responseText);
    // document.getElementbyId('whatever').innerHTML = xmlHttp.responseText;
}


-->