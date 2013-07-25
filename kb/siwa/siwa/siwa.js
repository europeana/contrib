<!--

/**
 * SIWA Software, version 0.9
 * Last update: April 16th, 2010
 *
 * Developer: Thomas Beekman, The National Library of the Netherlands
 * E-mail: tsbeekman@gmail.com
 * Date: December 2009 - April 2010
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
 *   it to a JSON Object which can be used by the SIWA Software and can be edited by the user. 
 */

    /**####################
	#	Global Variables  #
	#####################*/

// Main config
//var siwaUrl = "http://81.204.248.129/siwa/"; 												// The location of the .JS files	
var siwaUrl = ""; 												// The location of the .JS files	
//var ssUrl = "http://81.204.248.129/servicesserver/services.php"; 							// PHP Version of the ServicesServer
var ssUrl = "http://siwa.kbresearch.nl/servicesserver/services"; 							// JAVA Version of the ServicesServer
var searchUrl = "http://www.europeana.eu/portal/brief-doc.html?start=1&view=table&query="	// URL for direct searching when a search phrase has been provided
	
// Services & document holders
var services; 				// Will hold the JSON object containing all services descriptions
var pageDoc=document; 				// Pointer to the DOM DOC of the page holding the metadata fields

// Session holder
var sessionArray = []; 		// Container for Session Variables such as _query

// Json Request holders
var jsonRequests = []; 		// Queue for pending JSON queries
var jsonReqBusy = false; 	// Check whether the JSON queue is being processed

// Proxy Config
var proxyEnabled = false; 	// Check whether the Proxy functionality is enabled

// Text brackets for replaceText
var leftBracket = "|{| "; 	// Indicator for recognizing transformed text when performing a httpReq
var rightBracket = " |}|"; 	// Indicator for recognizing transformed text when performing a httpReq


	/**##################
	# Include functions #
	###################*/
	
/**
 * Adds a Javascript link to the HEAD of the DOM.
 * @param fileName the URL to the Javascript that is to be included.
 */
function includeJS(filename) {		
	var jsFile = pageDoc.createElement('script');
	jsFile.setAttribute("src", filename);
	jsFile.setAttribute("type", "text/javascript");
	pageDoc.getElementsByTagName("head")[0].appendChild(jsFile);	
}

/**
 * Adds a CSS/Style link to the HEAD of the DOM.
 * @param fileName the URL to the CSS/Style file that is to be included.
 */
function includeCSS(filename) {		
	var cssFile = pageDoc.createElement('link');
	cssFile.setAttribute("rel", "stylesheet");
    cssFile.setAttribute("type", "text/css");
    cssFile.setAttribute("href", filename);
	pageDoc.getElementsByTagName("head")[0].appendChild(cssFile);
}

/**
 * Adds a DIV block to the DOM to put embed/thumbnail objects in.
 * @param divID of the DIV to be created.
 */
function createDiv(divID, contents) {
	var i = 0;
	
	while (pageDoc.getElementById(divID + i) != null) {
		i++;		
	}
	divID = divID + i;
	var newDiv = pageDoc.createElement("div");
	newDiv.setAttribute("id", divID);	
	newDiv.setAttribute("class", "siwadiv");	
	newDiv.innerHTML = contents;	
		
	if (pageDoc.getElementById("cmenu") != null) {		
		newDiv.style.top = pageDoc.getElementById("cmenu").style.top;	
		newDiv.style.left = pageDoc.getElementById("cmenu").style.left;			
	} 		
	
	newDiv.onclick = function() { pageDoc.body.removeChild(newDiv); }
	pageDoc.body.appendChild(newDiv);	
}


	/**###########################
	# Service Specific Functions # 
	############################*/

/**
 * Get the Services Descriptions object
 * @return services object containing the services descriptions
 */
function getServices() { return services; }

/**
 * Get a specific Service Description of a single Service.
 * @param serviceLabel The label of the Service Description to be returned.
 * @return if found, the complete Service Description of the requested Service, else an empty String.
 */
function getServiceByLabel(serviceLabel) {
	for (var i=0; i<services.length;i++) {
		var service = services[i];		
		//if (service["sd:serviceLabel"].indexOf(serviceLabel) != -1)	{				
		if (service["sd:serviceLabel"] == serviceLabel)	{				
			return service;				
		}
	}
	return "";
}

/**
 * Initialize the software. To be invoked by the page containing the metadata fields.
 * @param doc a pointer to the DOM Document of the page the metadata fields.
 * @param servicesUrl the URL or version of the Services.xml which the software should use.
 * @param styleUrl the URL of the CSS/Style file to be used.
 * @param proxyUrl the URL of the Javascript Proxy implementation code.
 * @param sessionVars an object container for session variables like _query or language.
 */
function initServices(doc, servicesUrl, styleUrl, proxyUrl, sessionVars) { 		
	pageDoc = doc;
		
	// Load Session Variables, create the object if no sessionVars are provided
	if (sessionVars == null) {
		var sessionVars = [];
	}	
	loadSessionArray(sessionVars);
	
	if (sessionVars["ssurl"] != null) {
		ssUrl = sessionVars["ssurl"];
	}
	
	// Load CSS/Style file
	if (styleUrl == "" || styleUrl == null)	{	
		includeCSS(siwaUrl + "siwa.css");
	} else {
		includeCSS(styleUrl);	
	}
	
	// Load Context Menu code	
	includeJS(siwaUrl + "cmenu.js");		
	
	// Load Proxy code
	if (proxyUrl != null && proxyUrl != "") {
		// Todo: A thorough check whether the proxy file has implemented all functions (typeof yourFunctionName == 'function')
		proxyEnabled = true;
		includeJS(proxyUrl);
	}
	
	// Load the Services.xml
	if (servicesUrl == "" || servicesUrl == null) {
		readServices(ssUrl + "?format=json&callback=setServices");			
	} else {		
		if (servicesUrl.indexOf("http://") != -1) {			
			readServices(ssUrl + "?format=json&callback=setServices&src=" + servicesUrl);		
		} else {
			readServices(ssUrl + "?format=json&callback=setServices&version=" + servicesUrl);		
		}
	}
	
}

/**
 * Loads the Session Variables Array and sets some defaults if the were not already given.
 * @param sessionVars the Session Variables Array.
 */
function loadSessionArray(sessionVars) {
	sessionArray = sessionVars;	
	
	// Set a few defaults if these sessionVars are not already provided
	sessionArray["srcLang639_1"] = sessionArray["srcLang639_1"] ? sessionArray["srcLang639_1"] : "nl";
	sessionArray["userLang639_1"] = sessionArray["userLang639_1"] ? sessionArray["userLang639_1"] : "en";
	sessionArray["srcLangFull"] = sessionArray["srcLangFull"] ? sessionArray["srcLangFull"] : "Dutch";
	sessionArray["userLangFull"] = sessionArray["userLangFull"] ? sessionArray["userLangFull"] : "English";	
}

/**
 * Wrapper for loading the context menu. To be invoked on onclick/onmouseover events.
 * @param e the event object of the trigger.
 */
function servRequest(e) {			
	e = e ? e : window.event;		
	showContextMenu(e);		
}

/**
 * Wrapper for including the Services Descriptions object.
 * @param url the URL of the JSON object containing the Services Descriptions. The callback should be set to setServices.
 */
function readServices(url) { includeJS(url); }

/**
 * Function invoked by the callback when retrieving the Services Descriptions as a JSON object.
 * @param servs the Services Descriptions in JSON format.
 */
function setServices(servs) {
	services = eval(servs);		
	initializeSpans();	
	
	if (services["sd:service"] != null) {
		services = services["sd:service"];
	}
		
	// Invoke Automatic services		
	for (var i=0; i<services.length; i++) {
		var service = services[i];		
		
		if (service["sd:invocation"] != null) {
			var invocation = service["sd:invocation"].toLowerCase();
			
			if (invocation.indexOf("option") == -1) {					
				var typeOfUse = service["sd:typeOfUse"] ? service["sd:typeOfUse"].toLowerCase() : "";
				var accessType = service["sd:accessType"] ? service["sd:accessType"].toLowerCase() : "";
				
				if ((typeOfUse != "linkonly") && (typeOfUse != "thumbnail") && (proxyEnabled != true) && (accessType != "json")) {
					service["sd:invocation"] = "option";												
				} else {				
					var triggers = service["sd:triggers"]["sd:trigger"];
									
					// Loop through Metadata Fields
					spanArray = pageDoc.getElementsByTagName("span");	
					for (var j=0; j<spanArray.length; j++) {
						var currSpan = spanArray[j];
						var theTrigger = currSpan.getAttribute("name") ? currSpan.getAttribute("name") : "";						
						var strippedTrigger = theTrigger;
							
						strippedTrigger = strippedTrigger.replace("europeana:", "");
						strippedTrigger = strippedTrigger.replace("dcterms:", "");
						strippedTrigger = strippedTrigger.replace("dc:", "");	
											
						if ((spanArray[j].className == "siwa") && (strippedTrigger != null)) {
							if ((in_array(strippedTrigger, triggers)) || (in_array("*", triggers))) {
								invkService = true;
															
								if ((service["sd:extraConditions"] != null) && (service["sd:extraConditions"]["sd:extraCondition"] != null)) {
									var extraConditions = service["sd:extraConditions"]["sd:extraCondition"];
									if (checkExtraConditions(extraConditions, theTrigger) == false) {										
										invkService = false;
									}
								}
								if (invkService) {
// add button or link
									var servLabel = service["sd:serviceLabel"];
									var id = currSpan.getAttribute("id");
									var value = currSpan.innerHTML;	
									if (invocation.indexOf("automatic") != -1) {					
						
										invokeService(servLabel, id, value);
									}
								else {
if (invocation.indexOf("button") != -1) 
	txt='<a onClick="invokeService(\''+ servLabel+'\',\'' + id + '\',\'' + value + '\')"><img src="' + service["sd:logo"] + '" border="0"></a><br />'
else txt='<a onClick="invokeService(\''+ servLabel+'\',\'' + id + '\',\'' + value + '\')">' + servLabel + '</a><br />'

document.getElementById('siwaLinks').innerHTML=txt
								}
								}
							}
						}
					}
				}				
			}			
		}		
	}	
}

/**
 * Checks the extra conditions of a service.
 * @param extraConditions the extra conditions of a service, can be an array or a single condition.
 * @param theTrigger the Metadata field that triggered the service.
 * @return true if extra conditions are met, false if the service should not be invoked.
 */
function checkExtraConditions(extraConditions, theTrigger) {	
	var invkService = true;
	if (isArray(extraConditions)) {		
		for (var i=0; i<extraConditions.length; i++) {
			var extraCondition = extraConditions[i];
			if ((extraCondition["@field"] != null) && (extraCondition["#text"] != null)){
				var ecHaystack = getValueOfMDF(extraCondition["@field"]);
				var ecNeedle = extraCondition["#text"];
// tvv 28-5-2010 replace by match(regexp)
				if (ecNeedle.indexOf('/') != 0) { if (ecHaystack.indexOf(ecNeedle) == -1) invkService = false;}
				else if (eval('ecHaystack.match('+ecNeedle+')') == null) invkService = false;				
			} else {
				var ecHaystack = getValueOfMDF(theTrigger);
				var ecNeedle = extraCondition;
				if (ecNeedle == "_noHits") {
					// TODO: _noHits implementation
				} else {
				if (ecNeedle.indexOf('/') != 0) { if (ecHaystack.indexOf(ecNeedle) == -1) invkService = false;}
				else if (eval('ecHaystack.match('+ecNeedle+')') == null) invkService = false;				
				}
			}			
		}			
	} else {		
		if ((extraConditions["@field"] != null) && (extraConditions["#text"] != null)) {
			var ecHaystack = getValueOfMDF(extraConditions["@field"]);
			var ecNeedle = extraConditions["#text"];
			if (ecNeedle.indexOf('/') != 0) { if (ecHaystack.indexOf(ecNeedle) == -1) invkService = false;}
			else if (eval('ecHaystack.match('+ecNeedle+')') == null) invkService = false;				
		} else {			
			var ecHaystack = getValueOfMDF(theTrigger);						
			var ecNeedle = extraConditions;
			if (ecNeedle == "_noHits") {
				// TODO: _noHits implementation
			} else {	
				if (ecNeedle.indexOf('/') != 0) { if (ecHaystack.indexOf(ecNeedle) == -1) invkService = false;}
				else if (eval('ecHaystack.match('+ecNeedle+')') == null) invkService = false;				
			}
		}
	}	
	return invkService;
}

/**
 * Adds the onclick functionality to spans with the class name "siwa".
 */
function initializeSpans() {
	spanArray = pageDoc.getElementsByTagName("span");	
	for (var i=0; i<spanArray.length; i++) {
		if (spanArray[i].className == "siwa") {
			var currSpan = spanArray[i];			
			currSpan.onclick = servRequest;			
		}
	}	
}

/**
 * Invokes a service by going thru its description.
 * @param servLabel the unique service label of the service to be invoked (Soon to be replaced by the SID, Service Identifier).
 * @param id the ID of the Metadata field which triggered the service. 
 * @param value the contents of the Metadata field which triggered the service.
 */
function invokeService(servLabel, id, value) {			
	var service = getServiceByLabel(servLabel);
	value = unescape(value);
	
	if (service == null) {
		alert("Error: The services.xml is corrupt.");
	} else {
		var typeOfUse = service["sd:typeOfUse"];		// linkOnly, replaceText, createSearchList etc.						
		var format = service["dc:format"];				// Response format
		var accessType = service["sd:accessType"];		// GET, POST, JSON etc.		
						
		accessType = accessType ? accessType.toUpperCase() : "";
		
		// Select the right method for the specific required access type. Default to GET
		switch (accessType) {
			case "GET":				
				runGetService(service, id, value);
				break;
			case "POST":				
				runPostService(service, id, value);
				break;
			case "JSON":				
				runJsonService(service, id, value);
				break;
			case "OPENSEARCH":
				alert("The service \"" + service["dc:title"] + "\" was requested for field \"" + id + "\".\nAccess-type OpenSearch is not implemented yet. ");
				break;
			case "SOAP":
				alert("The service \"" + service["dc:title"] + "\" was requested for field \"" + id + "\".\nAccess-type SOAP is not implemented yet. ");
				break;
			case "OAI":
				alert("The service \"" + service["dc:title"] + "\" was requested for field \"" + id + "\".\nAccess-type OAI is not implemented yet. ");
				break;
			case "SPARQL":
				alert("The service \"" + service["dc:title"] + "\" was requested for field \"" + id + "\".\nAccess-type SPARQL is not implemented yet. ");
				break;
			default: 
				runGetService(service, id, value);
				break;
		}		
	}	
}

/**
 * Process the several parameter types such as field, fixed, lookup, replace and parts.
 * @param parameter the parameter to process.
 * @param triggerValue the value of the Metadata field which triggered the service.
 * @return the parsed parameter value.
 */
function parseParameter(parameter, triggerValue) {	
	var currParValue = "";
	
	if (parameter["#text"] == null) {
		parameter["#text"] = "";
	}
	// If no parameter type was given; default to field
	if (parameter["@type"] == null) {
		parameter["@type"] = "field";
	} 
			
	switch (parameter["@type"].toLowerCase()) {		
		case "field":
			currParValue = parseParValue(parameter["#text"], triggerValue);			
			break;
		case "prompt":			
			//var reply = prompt("");
			alert("Prompt not implemented in parseParameter!");				
			break;
		case "fixed":
			currParValue = parameter["#text"];
			break;
		case "lookup":			
			var keys = parameter["sd:keys"]["sd:key"];		
			var entries = parameter["sd:entries"]["sd:entry"];
			
			if (isArray(keys)) {			
				for (var i=0; i<keys.length; i++) {
					keys[i] = parseParValue(keys[i], triggerValue);
				}
			} else {
				keys = parseParValue(keys, triggerValue);				
			}
			currParValue = lookupEntryInMap(keys, entries);			
			break;	
		case "replace":				
			alert("Unsupported use of the replace parameter. The replace parameter should only occur in a parts parameter.");
			break;
		case "replaceby":
			alert("Unsupported use of the replace parameter. The replace parameter should only occur in a parts parameter.");
			break;
		case "parts":
			var replaceParmPart = false;
			var replacePart = "";
			var fieldPart = "";
			
			for (var i=0; i<parameter["sd:parmPart"].length; i++) {
				var currParmPart = parameter["sd:parmPart"][i];
				if (currParmPart["@type"] == "replace") {
					replaceParmPart = true;
					replacePart = currParmPart["#text"];						
				}
				else if (currParmPart["@type"] == "field") {
					if (replaceParmPart) {
						fieldPart = parseParValue(currParmPart["#text"], triggerValue);							
					} else {
						//currParValue += parseParValue(parseParameter(currParmPart, triggerValue));					
						currParValue += parseParameter(currParmPart, triggerValue);					
					}
				}
				else if (currParmPart["@type"] == "replaceBy") {
					// Possible Security Issue TODO						
					fieldPart = eval('fieldPart.replace('+replacePart+',"'+currParmPart["#text"]+'");');								
					
					currParValue = fieldPart;
					replaceParmPart = false;
					replacePart = "";
					fieldPart = "";
				}				
				else {
					//currParValue += parseParValue(parseParameter(currParmPart, triggerValue));		
					currParValue += parseParameter(currParmPart, triggerValue);		
				}
			}							
			break;
		default:
			break;
	}
	
	//	Process the operation attribute
	if (parameter["@operation"] != null) {
		switch (parameter["@operation"].toLowerCase()) {
			case "urlencode":
				currParValue = encodeURIComponent(currParValue);
				break;
			case "doubleencode":
				currParValue = encodeURIComponent(encodeURIComponent(currParValue));			
				break;
			default: 
				break;
		}
	}
	
	return currParValue;
}

/**
 * Processes a service with access type GET.
 * @param service the service object of the service to be invoked.
 * @param id the ID of the Metadata field which triggered the service. 
 * @param value the contents of the Metadata field which triggered the service.
 */
function runGetService(service, id, value) {
	var identifier = parseParValue(getValueOfField(service, "dc:identifier"), value);		
	var parArray = service["sd:parameters"];
	var parValue;
	var typeOfUse = getValueOfField(service, "sd:typeOfUse");
	var	parameters = "";
	
	identifier = (identifier.indexOf("?") == -1) ? identifier + "?" : identifier;
	
	if (parArray != null) {
		parArray = service["sd:parameters"]["sd:parameter"];		
	} else {
		parArray = service["sd:parameter"];
	}
		
	value = (proxyEnabled && (typeOfUse == "replaceText")) ? (leftBracket + trim(value) + rightBracket) : (trim(value));	
	
	// Parse the Parameters (if any)
	if (parArray != null) {			
		if (isArray(parArray)) {
			for (var i=0; i<parArray.length;i++) {
				var currPar = parArray[i];
				parValue = parseParameter(currPar, value);			
				
				// Check for parameter["@substitute"] (for identifier)
				if (currPar["@substitute"] != null && currPar["@substitute"]) {
					identifier = identifier.replace(currPar["@name"], parValue);						
				} else {
					parameters += "&" + currPar["@name"] + "=" + parseParameter(currPar, value);				
				}
			}
		} else {
			parValue = parseParameter(parArray, value);			
			if (parArray["@substitute"] != null && parArray["@substitute"]) {
				identifier = identifier.replace(parArray["@name"], parValue);													
			} else {
			    parameters += "&" + parArray["@name"] + "=" + parValue;
			}
		}
	}
	
	// Build up the URL
	var url = identifier + parameters;	
	
	// Check if the Proxy functionality is enabled
	if (proxyEnabled) {
		var handler;
		switch (typeOfUse) {			
			case "debug":			
				handler = function(result) { 
					alert(result); 
				}
				htmlGetReq(url, handler);				
				break;
			case "alertOccurrences": 
				// Get fieldSpec
				var fieldSpec = service["sd:fieldSpec"];
								
				if ((fieldSpec != null) && (fieldSpec != "")) {				
					handler = function(result) {
						var numOfOccurrences = 0;
						
						String.prototype.count = function(char) {
							return this.split(char).length-1;
						}
						
						alert(result.count(fieldSpec) + " occurrences of \"" + fieldSpec + "\" were found.");	
						// TODO: Invoke nextService if > 0
					}
					htmlGetReq(url, handler);	
				}
				break;
			case "replaceField":
				handler = function(result) { 
					// TODO: fieldSpec
					pageDoc.getElementById(id).innerHTML = result;	
				} 
				htmlGetReq(url, handler);				
				break;
			case "replaceText":
				handler = function(result) { 					
					var begin = 0; var length = 0; var endResult = ""; var tmpResult = ""; value = decodeURIComponent(value);
						
					while (result.substr(begin).indexOf(leftBracket) != -1) {						
						begin += result.substr(begin).indexOf(leftBracket) + leftBracket.length;						
						length = result.substr(begin).indexOf(rightBracket);						
						tmpResult = result.substr(begin, length);						
						if ((endResult.indexOf(tmpResult) == -1) && (tmpResult.indexOf(value.substr(leftBracket.length, value.length-(leftBracket.length+rightBracket.length))) == -1)) {
							endResult = (endResult == "") ? tmpResult : endResult + "<br>" + tmpResult;													
						}
					}					
					pageDoc.getElementById(id).innerHTML = endResult;						
				} 							
				htmlGetReq(url, handler);				
				break;
			case "thumbnail":
				var contents = "<img src=\"" + url + "\" alt=\"" + url + "\" />";
				createDiv("siwathumbdiv", contents);				
				break;
			case "createSearchList":				
				var fieldSpec = service["sd:fieldSpec"];
				
				if ((fieldSpec != null) && (fieldSpec != "")) {								
					handler = function(result) { 
						var fSpecs = fieldSpec.split("|");
						var contents = "<b>Search list:</b><br>";
																						
						for (var i=0; i<fSpecs.length; i++) {
							if (result.indexOf(fSpecs[i]) != -1) {
								var tmpStr = result;
								var response = "";
								
								while (tmpStr.indexOf("<" + fSpecs[i] + ">") != -1) {									
									var firstOcc = tmpStr.indexOf("<" + fSpecs[i] + ">") + fSpecs[i].length + 2;								
									var tmpStr = tmpStr.substring(firstOcc);
									var endPos = tmpStr.indexOf("</" + fSpecs[i] + ">");
									var currValue = tmpStr.substring(0, endPos);
									
									response += "<b><li class=\"citem\" onclick=\"searchItem('" + currValue + "')\">" + fSpecs[i] + ": </b>" + currValue + "<br>";								
								}								
																
								if (response != "") {									
									contents += response;
								} else {
									contents = "<b>No results</b>";
								}							
							}							
						}
						createDiv("csDiv", contents);								
					} 							
					htmlGetReq(url, handler);										
				}
				break;
			default:
				window.open(url, "", "");	
				break;
		}
	} else {		
		switch (typeOfUse) {
			case "thumbnail":
				var contents = "<img src=\"" + url + "\" alt=\"" + url + "\" />";
				createDiv("siwathumbdiv", contents);				
				break;				
			default:
				window.open(url, "", "");
				break;
		}
	}
	
	/* Check for typeOfUse
	 
	 * - alertOccurrences ->
	 * - alertIfResultCount ->
	 * - present ->
	 * - replaceField -> httpRequest with callback value placed in mdf.innerHTML
	 * - replaceText ->
	 * - displayWhenText ->
	 * - createSearchList ->
	 * - embed ->
	 * - thumbnail ->
	 * - expandQuery ->
	 * - addToField ->
	 */
	
	//window.open(url, "servWindow", "");		
}

/**
 * Processes a service with access type POST.
 * @param service the service object of the service to be invoked.
 * @param id the ID of the Metadata field which triggered the service. 
 * @param value the contents of the Metadata field which triggered the service.
 */
function runPostService(service, id, value) { 
	var identifier = parseParValue(getValueOfField(service, "dc:identifier"), value);		
	var parArray = service["sd:parameters"];
	var typeOfUse = getValueOfField(service, "sd:typeOfUse");
	var	parameters = "";

	identifier = (identifier.indexOf("?") == -1) ? identifier + "?" : identifier;
	
	if (parArray != null) {
		parArray = service["sd:parameters"]["sd:parameter"];		
	} else {
		parArray = service["sd:parameter"];
	}
	
	value = (proxyEnabled && (typeOfUse == "replaceText")) ? "|{| " + trim(value) + " |}|" : value = trim(value);	
	
	// Create a DOM Document Form
	var form = pageDoc.createElement("form");
	form.action = identifier;
	form.method = "POST";
	form.target = "_blank";		
		
	if (parArray != null) {			
		var parValue;
		if (isArray(parArray)) {
			for (var i=0; i<parArray.length;i++) {
				var currPar = parArray[i];
				parValue = parseParameter(currPar, value);
				
				// Check for parameter["@substitute"] (for identifier)
				if (currPar["@substitute"] != null && currPar["@substitute"]) {
					identifier = identifier.replace(currPar["@name"], parValue);						
				} else {					
					createInputElement(form, parArray[i]["@name"], parValue);
					parameters += "&" + currPar["@name"] + "=" + parValue;
				}
			}
		} else {			
			parValue = parseParameter(parArray, value);			
			
			if (parArray["@substitute"] != null && parArray["@substitute"]) {
				identifier = identifier.replace(parArray["@name"], parValue);													
			} else {								
			    createInputElement(form, parArray["@name"], parValue);	
				parameters += "&" + parArray["@name"] + "=" + parValue;
			}
		}
	}		
	
	// Check if the Proxy functionality is enabled
	if (proxyEnabled) {		
		var handler;
		switch (typeOfUse) {			
			case "debug":			
				handler = function(result) { alert(result); }
				htmlPostReq(identifier, parameters, handler);				
				break;
			case "alertOccurrences": 		
				// Get fieldSpec
				var fieldSpec = service["sd:fieldSpec"];
								
				if ((fieldSpec != null) && (fieldSpec != "")) {				
					handler = function(result) {
						var numOfOccurrences = 0;
						
						String.prototype.count = function(char) {
							return this.split(char).length-1;
						}
						
						alert("Number of occurrences of \"" + fieldSpec + "\": " + result.count(fieldSpec));	
						// TODO: Invoke nextService if > 0
					}
					htmlGetReq(url, handler);	
				}
				break;
			case "replaceField":
				handler = function(result) { pageDoc.getElementById(id).innerHTML = result;	} // TODO: fieldSpec				
				htmlPostReq(identifier, parameters, handler);				
				break;
			case "replaceText":
				handler = function(result) { 					
					var begin = 0; var length = 0; var endResult = ""; var tmpResult = "";
										
					while (result.substr(begin).indexOf("|{| ") != -1) {						
						begin += result.substr(begin).indexOf("|{| ") + 4;						
						length = result.substr(begin).indexOf(" |}|");						
						tmpResult = result.substr(begin, length);
						if ((endResult.indexOf(tmpResult) == -1) && (tmpResult.indexOf(value.substr(4, value.length-8)) == -1)) {
							endResult = (endResult == "") ? tmpResult : endResult + "<br>" + tmpResult;							
						}
					}					
					pageDoc.getElementById(id).innerHTML = endResult;						
				} 				
				htmlPostReq(identifier, parameters, handler);				
				break;
			case "createSearchList":				
				var fieldSpec = service["sd:fieldSpec"];
				
				if ((fieldSpec != null) && (fieldSpec != "")) {								
					handler = function(result) { 
						var fSpecs = fieldSpec.split("|");
						var contents = "<b>Search list:</b><br>";
																						
						for (var i=0; i<fSpecs.length; i++) {
							if (result.indexOf(fSpecs[i]) != -1) {
								var tmpStr = result;
								var response = "";
								
								while (tmpStr.indexOf("<" + fSpecs[i] + ">") != -1) {									
									var firstOcc = tmpStr.indexOf("<" + fSpecs[i] + ">") + fSpecs[i].length + 2;								
									var tmpStr = tmpStr.substring(firstOcc);
									var endPos = tmpStr.indexOf("</" + fSpecs[i] + ">");
									var currValue = tmpStr.substring(0, endPos);
									
									response += "<b><li class=\"citem\" onclick=\"searchItem('" + currValue + "')\">" + fSpecs[i] + ": </b>" + currValue + "<br>";								
								}								
																
								if (response != "") {									
									contents += response;
								} else {
									contents = "<b>No results</b>";
								}							
							}							
						}
						createDiv("csDiv", contents);								
					} 							
					htmlGetReq(url, handler);										
				}
				break;
			default:
				pageDoc.body.appendChild(form);
				form.submit();
				pageDoc.body.removeChild(form);		
				break;
		}
	} else {		
		pageDoc.body.appendChild(form);
		form.submit();
		pageDoc.body.removeChild(form);
	}
}

/**
 * Creates an input element for form (used in runPostService())
 * @param form the form in which the input element is to be placed.
 * @param name the name of the input element.
 * @param value the value of the input element.
 */
function createInputElement(form, name, value) {
	var input = document.createElement("input");
	input.type = "hidden";
	input.name = name;
	input.value = value;
	form.appendChild(input);
}

/**
 * Processes a service with access type JSON.
 * @param service the service object of the service to be invoked.
 * @param id the ID of the Metadata field which triggered the service. 
 * @param value the contents of the Metadata field which triggered the service.
 */
function runJsonService(service, id, value) { 
	var identifier = getValueOfField(service, "dc:identifier");		
	var parArray = service["sd:parameters"];
	var	parameters = "";
	
	identifier = (identifier.indexOf("?") == -1) ? identifier + "?" : identifier;
	
	if (parArray != null) {
		parArray = service["sd:parameters"]["sd:parameter"];		
	} else {
		parArray = service["sd:parameter"];
	}
		
	value = trim(value);
	
	if (parArray != null) {			
		if (isArray(parArray)) {			
			for (var i=0; i<parArray.length;i++) {
				var currPar = parArray[i];
				
				// Check for parameter["@substitute"] (for identifier)
				if (currPar["@substitute"] != null && currPar["@substitute"]) {					
					identifier = identifier.replace(currPar["@name"], parseParameter(currPar, value));									
				} else {
					parameters += "&" + currPar["@name"] + "=" + parseParameter(currPar, value);				
				}
			}
		} else {
			if (parArray["@substitute"] != null && parArray["@substitute"]) {
				identifier = identifier.replace(parArray["@name"], parseParameter(parArray, value));									
			} else {
			    parameters += "&" + parArray["@name"] + "=" + parseParameter(parArray, value);
			}
		}
	}
	
	
	var url = identifier + parameters; // + "&callback=getJsonResponse";		
	// TODO: Replace ?& -> ? and && -> &, also for JSON reqs
	
	// Build the JSON Request and add it to the queue
	var jsonReq = [];	
	jsonReq["service"] = service;
	jsonReq["id"] = id;
	jsonReq["value"] = value;
	jsonReq["url"] = url;	
	jsonRequests.push(jsonReq);	
	
	// Fire the queue, if it isn't processed already
	if (!jsonReqBusy) {
		executeJsonReq();		
	}
}

/**
 * If no JSON request is processed, but if there is a request pending, process the request.
 */
function executeJsonReq() {
	if (!jsonReqBusy && jsonRequests.length > 0) {	
		jsonReqBusy = true;
		jsonReq = jsonRequests[0];
		includeJS(jsonReq["url"]);			
	}
}

/**
 * Fetches the response of a JSON request.
 * @param respons the response object.
 */
function getJsonResponse(response) {		
	var currJsonReq = jsonRequests.shift();

	var currService = currJsonReq["service"];
	var currId = currJsonReq["id"];
	var currValue = currJsonReq["value"];
	
	var triggerName = getName(currId);
	var typeOfUse = getValueOfField(currService, "sd:typeOfUse");
	var fieldSpec = getValueOfField(currService, "sd:fieldSpec");
	var respObject = eval("response");
	var respText = print_r(respObject); // Just in case no fieldSpec was provided
	var unitName = "items";
	
	
	// Check if fieldSpec is given
	if ((fieldSpec != null) && (fieldSpec != "")) {
		var fsObject = currService["sd:fieldSpec"];
		
		respText = respObject;
		var tree = fieldSpec.split(".");		
		
		for (var i=0; i<tree.length; i++) {			
			respText = respText[tree[i]];				
		}	
		
		// Check for unitName
		if ((fsObject["@unitName"] != null) && (fsObject["@unitName"] != "")) {			
			var unitLabel = fsObject["@unitName"];
			var tree = unitLabel.split(".");		
			var unitName = respObject;
			
			for (var i=0; i<tree.length; i++) {				
				unitName = unitName[tree[i]];			
			}			
		}	
	}
	
	switch (typeOfUse) {		
		case "replaceField":						
			pageDoc.getElementById(currId).innerHTML = " " + respText;			
			break;					
		case "addToField":						
			pageDoc.getElementById(currId).innerHTML += " - " + respText;			
			break;	
		case "alertOccurrences": 
			if ((fieldSpec != null) && (fieldSpec != "")) {				
				var numOfOccurrences = 0;
				
				String.prototype.count = function(char) {
					return this.split(char).length-1;
				}
				respText = print_r(respObject);
				alert(respText.count(fieldSpec) + " occurrences of \"" + fieldSpec + "\" were found. " + respText);	
					
				// TODO: Invoke nextService if > 0				
			}
			break;			
		case "alertIfResultCount":
			if (parseInt(respText) > 0) {
				alert(respText + " occurrences of the type " + unitName + " were found of the object in the field " + triggerName + ".");
			}
			break;		
		default:
			// There should be a check for content-type TODO			
			if ((respText != null) && (respText != "")) {
				alert(respText);						
			} else {
				alert(print_r(respObject));
			}
			//alert((respObject));								
			break;
	}	
	
	// Check for other JSON requests in the queue
	jsonReqBusy = false;
	if (jsonRequests.length > 0) {
		executeJsonReq();
	}
}

// Placeholders for access types which are not implemented yet (OpenSearch, SOAP, OAI and SPARQL)
function runOpenSearchService(service, id, value) { }
function runSoapService(service, id, value) { }
function runOaiService(service, id, value) { }
function runSparqlService(service, id, value) { }

/**
 * Helper method for the lookup table functionality of the parameters.
 * @param entry the entry to be found in the map, the "needle".
 * @param map the map containing all values, the "haystack".
 * @return if found, the value of the entry. Else an empty String.
 */
function lookupEntryInMap(entry, map) {
	var value = "";
	var found = false;
	var i=0;

	while (i<map.length && !found) {
		var gotHim = false;
		var currEntry = map[i]["sd:val"];		
		
		if (isArray(entry)) {
			for (var j=0; j<entry.length; j++) {						
				gotHim = (currEntry[j] == entry[j]);									
			}
		} else {
			gotHim = (currEntry[0] == entry);
		}
		
		if (gotHim) {
			found = true;
			value = currEntry[currEntry.length-1];
			alert(value);
		}		
		i++;
	}	
	
	return value;
}

/**
 * Gets the value of a specific Services Description field.
 * @param service the Services Description Object.
 * @param field the requested field.
 * @return if found, the value of the field. Else an empty String.
 */
function getValueOfField(service, field) {				
	if (service[field]) {
		var value = service[field]["#text"] ? service[field]["#text"] : service[field];
		return value;			
	} else {
		return "";
	}
}

/**
 * Get the value of a Metadata field by its name.
 * @param input the name of the requested Metadata field.
 * @return if found, the value of the Metadata field. Else an empty String.
 */
function getValueOfMDF(input) {
	var value = input;		
	var results;
	
	// Make an array of spans
	if (pageDoc.getElementsByTagName("span") != null) {
		results = pageDoc.getElementsByTagName("span");
	}
		
	var i=0;	
	// Loop through the array trying to find the span with input as its name and siwa as its class
	while (i<results.length) {		
		if ((results[i].getAttribute("name") == input) && results[i].className == "siwa") {
			value = results[i].innerHTML;					
			break;
		}
		else {
			i++;
		}
	}	
	
	return value;	
}

/**
 * Parses the contents of a parameter for substituting purposes.
 * @param parValue the value of the parameter.
 * @param triggerValue the value of the field which triggered the service.
 * @return the parsed parameter value.
 */
function parseParValue(parValue, triggerValue) {				
	// Replace triggerValue by the value of the MDF which triggered the service
	if (parValue.indexOf("_triggerValue") != -1) 		
		return parValue.replace("_triggerValue", triggerValue);	
	
	// Strip the leading "_"
	if (parValue.charAt(0) == "_") {
		parValue = parValue.substring(1, parValue.length);
	}	
	
	// Substitute where appropriate
	if (sessionArray[parValue] != null)	{
		// If the requested parValue is in the session array, return the value
		return parValue.replace(parValue, sessionArray[parValue]);
	} else if (parValue.indexOf("triggerName") != -1) {
		// Replace triggerName by the name of the MDF which triggered the service
		return getTriggerByValue(triggerValue);
	} else {	
		parValue = getValueOfMDF(parValue);	
	}
	
	return parValue;
}

/**
 * Returns the Metadata field name by providing its contents. 
 * @param triggerValue the value of the Metadata field.
 * @return the name of the Metadata field with that contents.
 */
function getTriggerByValue(triggerValue) {	
	var spanName = "";
	var spanArray = pageDoc.getElementsByTagName("span");	
		
	for (var i=0; i<spanArray.length; i++) {
		if (spanArray[i].className == "siwa") {
			var currSpan = spanArray[i];			
						
			if (currSpan.innerHTML == triggerValue) {				
				return currSpan.getAttribute("name");
			} else if (currSpan.innerHTML.indexOf(triggerValue) != -1) {
				spanName = currSpan.getAttribute("name");
			}
		}
	}	
	return spanName;
}

/**
 * Helper function to be able to use createSearchList functionality to continue the search on the website.
 * @param searchPhrase a String containing the phrase to be searched.
 */
function searchItem(searchPhrase) {
	var url = searchUrl + searchPhrase;	
	window.open(url, "", "");	
}


	/**##################
	# Toolbox Functions #
	###################*/

/**
 * Checks if the object given is an array by checking its constructor. 
 * @obj the object to be tested.
 * @return true if the object is an array.
 */ 
function isArray(obj) {
	if (obj.constructor.toString().indexOf("Array") == -1) {
		return false;
	} else {
		return true;
	}
}

/**
 * A Javascript implementation of the PHP method print_r. Shows the structure of an object.
 * @param theObj the object to be printed.
 * @return a String containing the structure of the object.
 */
function print_r(theObj){
	var result = "";
	
	if (theObj.constructor == Array || theObj.constructor == Object) {		
		for (var p in theObj) {
			if (theObj[p] != null) {
				if (theObj[p].constructor == Array || theObj[p].constructor == Object) {
					result += "["+p+"] => "+typeof(theObj);				
					result += print_r(theObj[p]);				
				} else {
					result += "["+p+"] => "+theObj[p];
					//result += theObj[p];
				}
			} 
		}		
	} else {
		result = theObj;
	}
	return result;
}

/**
 * Trims a String. 
 * @param value the String to be trimmed.
 * @return the trimmed String.
 */
function trim(value) {
	value = value.replace(/^\s+/,'');
	value = value.replace(/\s+$/,'');
	return value;
}

-->