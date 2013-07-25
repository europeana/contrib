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

var services;
var registry = new Array();
var servicesNS = new Array();
var headers = new Array();
var pageDoc;

// Statics
var divName = "services";
var URL_TO_REGISTRY = "regsearch?callback=processRegistry";


	/**##################
	# Include functions #
	###################*/

/**
 * Initializes the Javascript code.
 * @param doc the DOM document.
 * @param url the URL to the services JSON object.
 */
function loadJson(doc, url) {
    pageDoc = doc;
	includeJS("json2.js");
	readServices(url);
}

/**
 * Adds a Javascript link to the HEAD of the DOM.
 * @param fileName the URL to the Javascript that is to be included.
 */
function includeJS(filename) {
	var jsFile = document.createElement('script');
	jsFile.setAttribute("src", filename);
	jsFile.setAttribute("type", "text/javascript");
	document.getElementsByTagName("head")[0].appendChild(jsFile);
}

/**
 * Reads the services from the JSON object. The callback should be setServices.
 * @param url the URL to the Javascript that is to be included.
 */
function readServices(url) {
	includeJS(url);
}


	/**###########################
	# Service Specific Functions #
	############################*/

/**
 * Function to be called after requesting the JSON object. It process the services object and displays it to the user.
 * @param serv the services object in JSON format.
 */
function setServices(serv) {
	services = eval(serv);

	// Save the namespaces in the servicesNS array
	for (key in services) {
		if (key != "sd:service") {
			servicesNS.push("\"" + key + "\":\"" + services[key] + "\",");
		}
	}

	if (services["sd:service"] != null) {
		services = services["sd:service"];
		processServices();
	} else if (services["srw:records"] != null) {
		// It is SRU output, we can't handle that directly
		alert("It is not possible to directly read records from the Registry!");
	} else {
	    alert("The JSON object is invalid!");
	}
}

/**
 * Resets the services object.
 */
function resetServices() {
	window.location = "services?reset";
}

/**
 * Processes the JSON services object and builds an editable table.
 */
function processServices() {
	var divContents;

	divContents = "<h3>Number of services: " + services.length + "</h3>";

	// The Form Header
	divContents += "<form name=\"servform\">";

	// The Table Header
	divContents += "<table><tr>";
	divContents += "<th>#</th>";
	divContents += "<th><input type=\"checkbox\" name=\"mastercheck\" onclick=\"toggleCheck();\" checked></th>";
	divContents += "<th>X</th>";

	if (headers == "") {
		headers.push("dc:title");
		headers.push("sd:serviceLabel");
		headers.push("sd:serviceType");
		headers.push("sd:invocation");
		headers.push("sd:triggers");
	}

	/* Adv user
	headersList.add("sd:sidId");
            headersList.add("dc:identifier");
            headersList.add("sd:parameters");
            headersList.add("dc:type");
            headersList.add("sd:accessType");	*/

	/*for (key in services) {
		for (skey in services[key]) {
			if (!in_array(skey, headers)) {
				headers.push(skey);
			}
		}
	}*/

	for (i=0; i<headers.length; i++) {
		divContents += "<th>" + headers[i] + "</th>";
	}

	// The Table contents
	for (i=0; i<services.length; i++) {
		var trClass = "tr2";

		if (i % 2 == 0) {
			trClass = "tr1";
		}

		divContents += "<tr class=\"" + trClass + "\">";
		divContents += "<td style=\"font-weight:bold;text-align:center;\">" + (i+1) + "</td>";
		divContents += "<td><input type=\"checkbox\" name=\"servcb\" checked></td>";
		divContents += "<td><div id=\"del" + divName + i + "\" class=\"delservice\"><img id=\"del" + divName + i + "\" src=\"icon_cross.gif\" alt=\"Delete this service\" /></div></td>";

		for (j=0; j<headers.length; j++) {
			var currServ = services[i];

			divContents += "<td>";
			if (currServ[headers[j]] != null) {
				currDivName = divName + i + "-" + j;

				divContents += "<div id=\"" + currDivName + "\" class=\"serventry\">";
				divContents += getValueOfField(currServ, headers[j]);
				divContents += "</div>";

			}
			divContents += "</td>";
		}

		divContents += "</tr>";
	}
	divContents += "</table></form><br>";
	divContents += "<input type=\"submit\" onclick=\"storeJson();return false;\" value=\"Store JSON\">";
	//divContents += "<input type=\"submit\" onclick=\"printXML();return false;\" value=\"Print XML\">"; 
	//divContents += "<input type=\"submit\" onclick=\"resetServices();return false;\" value=\"Reset Services\">";
	divContents += "<input type=\"submit\" onclick=\"saveJsonToFile();return false;\" value=\"Save as file...\"><br>";
	divContents += "<input type=\"submit\" onclick=\"delSelectedItems();return false;\" value=\"Delete Selected Services\"><br>";

	// Load the regsearch
	var regSearchCont = "<br><br><input type=\"submit\" onclick=\"loadRegistry();return false;\" value=\"(Re)load Registry\"><br><br>";

	// Load the Services Loader page
	// //divContents += "<input type=\"file\" name=\"jsonfile\" onclick=\"loadJsonFromFile();return false;\" value=\"Load services from file\">";
	//var loadTabCont = "<br><br><form name=\"loadform\">Select services.json file: <input type=\"file\" size=\"50\" name=\"jsonfile\" /><br><br>" +
	//	"<input type=\"submit\" onclick=\"loadJsonFromFile();return false;\" value=\"Load services from file\" /></form><br><br>";

	var loadTabCont = "<br><br><form action=\"xmlloadermp\" method=\"post\" enctype=\"multipart/form-data\" name=\"loadxmlform\" id=\"loadxmlform\">" +
		"Selecteer services.xml file: <input type=\"file\" size=\"50\" name=\"xmlstring\" id=\"xmlstring\" />" +
		"<input type=\"submit\" value=\"Load services from file\" id=\"loadxmlsub\" name=\"loadxmlsub\" /></form>" +
		"<form action=\"jsonloadermp\" method=\"post\" enctype=\"multipart/form-data\" name=\"loadform\" id=\"loadform\">" +
		"Selecteer services.json file: <input type=\"file\" size=\"50\" name=\"jsonstring\" id=\"jsonstring\" />" +
		"<input type=\"submit\" value=\"Load services from file\" id=\"loadsub\" name=\"loadsub\" /></form>" +
		"<form name=\"servurlform\" id=\"servurlform\">URL naar services.xml: <input type=\"text\" size=\"50\" name=\"servurl\" id=\"servurl\" />" +
		"<input type=\"submit\" value=\"Gebruik deze services.xml\" onclick=\"loadServFromUrl();return false;\"></form><br><br>";

	pageDoc.getElementById("servicestab").innerHTML = divContents;
	pageDoc.getElementById("regsearch").innerHTML = regSearchCont;
	pageDoc.getElementById("loadtab").innerHTML = loadTabCont;

	initializeDivs();
}

/**
 * Loads the registry services object in JSON format from the servlet.
 */
function loadRegistry() {
	readServices(URL_TO_REGISTRY);
}

/**
 * Processes the registry services object in JSON format and displays it as a table from which the user can copy
 *  services to his own set of services.
 * @param regServs the registry services object in JSON format provided by the servlet.
 */
function processRegistry(regServs) {
	var tmpReg = eval(regServs);
	var result = "";
	tmpReg = tmpReg["srw:records"]["srw:record"];

	for (var i=0; i<tmpReg.length; i++) {
		registry[registry.length] = tmpReg[i]["srw:recordData"]["sd:service"];
	}

	// The Form Header
	result += "<form name=\"regform\">";

	// The Table Header
	result += "<table><tr>";
	result += "<th><input type=\"checkbox\" name=\"mastercheck\" onclick=\"toggleRegCheck();\"></th>";

	for (i=0; i<headers.length; i++) {
		result += "<th>" + headers[i] + "</th>";
	}

	// The Table contents
	for (i=0; i<registry.length; i++) {
		var trClass = "tr2";

		if (i % 2 == 0) {
			trClass = "tr1";
		}

		result += "<tr class=\"" + trClass + "\">";
		result += "<td><input type=\"checkbox\" name=\"regcb\"></td>";

		for (j=0; j<headers.length; j++) {
			var currServ = registry[i];

			result += "<td>";
			if (currServ[headers[j]] != null) {
				currDivName = divName + i + "-" + j;

				result += "<div id=\"" + currDivName + "\" class=\"regentry\">";
				result += getValueOfField(currServ, headers[j]);
				result += "</div>";

			}
			result += "</td>";
		}

		result += "</tr>";
	}
	result += "</table>";
	result += "<br><input type=\"submit\" onclick=\"addServFromReg();return false;\" value=\"Add to my services!\"></form>";

	pageDoc.getElementById("regbox").innerHTML = result;
}

/**
 * Makes certain table entries clickable according to their class names.
 */
function initializeDivs() {
	spanArray = pageDoc.getElementsByTagName("div");
	for (var i=0; i<spanArray.length; i++) {
		if (spanArray[i].className == "serventry") {
			var currSpan = spanArray[i];
			currSpan.onclick = divClick;
		} else if (spanArray[i].className == "delservice") {
			var currSpan = spanArray[i];
			currSpan.onclick = delService;
		}
	}

	spanArray = pageDoc.getElementsByTagName("span");
	for (var i=0; i<spanArray.length; i++) {
		if (spanArray[i].className.indexOf("menuitem") > -1) {
			var currSpan = spanArray[i];
			currSpan.onclick = handleMenuReq;
		} else if (spanArray[i].className.indexOf("resetservs") > -1) {
		    var currSpan = spanArray[i];
		    currSpan.onclick = resetServices;
		}
	}

}

/**
 * Helper function: toggles all checkboxes in the services table on/off according to the master checkbox.
 */
function toggleCheck() {
	var newValue = pageDoc.servform.mastercheck.checked;

	for (i=0; i<pageDoc.servform.servcb.length; i++) {
		pageDoc.servform.servcb[i].checked = newValue;
	}
}

/**
 * Helper function: toggles all checkboxes in the registry table on/off according to the master checkbox.
 */
function toggleRegCheck() {
	var newValue = pageDoc.regform.mastercheck.checked;

	for (i=0; i<pageDoc.regform.regcb.length; i++) {
		pageDoc.regform.regcb[i].checked = newValue;
	}
}

/**
 * Transforms the selected services into a JSON Object and returns it as a JSON String.
 * @return a String containing the selected services.
 */
function toJson() {
	var myServ = new Array();

	var json = "{";

	// Declare the namespaces
	/*for (i=0; i<servicesNS.length; i++) {
		json += servicesNS[i];
	}*/

	json += "\"sd:service\":";

	// Fill the services
	if (pageDoc.servform.servcb.length == null) {
		if (pageDoc.servform.servcb.checked) {
			myServ = services;
		}
	}
	else {
		for (i=0; i<pageDoc.servform.servcb.length; i++) {
			if (pageDoc.servform.servcb[i].checked) {
				myServ.push(services[i]);
			}
		}
	}

	json += JSON.stringify(myServ);

	json += "}";

	return json;
}

/**
 * Adds the selected services in the registry table to the user's services object.
 */
function addServFromReg() {
	var sids = new Array();
	var res = "Sel Items: ";

	// Make a list of sidId already in the current services object
	for (i=0; i<services.length; i++) {
		var currSid = services[i]["sd:sidId"];
		if (currSid != null)
			sids.push(currSid);
	}

	for (i=0; i<pageDoc.regform.regcb.length; i++) {
		if (pageDoc.regform.regcb[i].checked) {
			var currSidId = registry[i]["sd:sidId"];

			if (!in_array(currSidId, sids)) {
				services.push(registry[i]);
			}
		}
	}

	// Reload table
	processServices();
	showTab("servview");
}

/**
 * Loads a services object from a file containing JSON.
 */
function loadJsonFromFile() {
	//alert(pageDoc.loadform.jsonfile.value);
	//window.open("services?format=json");

}

/**
 * Loads a services.xml from an URL
 */
function loadServFromUrl() {    
    window.location = "services?lock&src=" + pageDoc.servurlform.servurl.value;
}

/**
 * Saves the JSON output as a file.
 */
function saveJsonToFile() {
	//pageDoc.getElementById("jsonbox").innerHTML = toJson();
	storeJson();
	window.open("services?format=json");
}

/**
 * Posts the current services object to the servlet which stores it in the session.
 */
function storeJson() {
	var json = toJson();

	var pars = new Array();
	pars['jsonstring'] = json;

	doPost('jsonloader', pars);
}

/**
 * Posts the current services object to the servlet which transforms it to XML.
 */
function printXML() {
	var json = toJson();

	// POST the JSON Object to the server

	var pars = new Array();
	pars['jsonstring'] = json;

	doPost('services.xml', pars);
}

/**
 * Event catcher for mouseclicks on fields in the services table; provides the ability to edit fields.
 * @param e the event object.
 */
function divClick(e) {
	e = e ? e : window.event;
	var target = e.target ? e.target : e.srcElement;
	editEntry(target.id);
}

/**
 * Deletes all selected services after confirmation and reloads the services table.
 */
function delSelectedItems() {
	var newServices = new Array();

	if (confirm("Delete all selected services?")) {
		for (i=0; i<pageDoc.servform.servcb.length; i++) {
			if (!pageDoc.servform.servcb[i].checked) {
				newServices.push(services[i]);
			}
		}
		services = newServices;
		processServices();
	}
}

/**
 * Deletes a single service after selection and confirmation by the user and reloads the services table.
 */
function delService(e) {
	e = e ? e : window.event;
	var target = e.target ? e.target : e.srcElement;
	var targetId = target.id;
	var currServIndex = parseInt(targetId.substring(targetId.indexOf(divName)+divName.length));

	if (confirm("Do you really want to delete service " + (currServIndex + 1) + "?")) {
		services.splice(currServIndex, 1);
		processServices();
	}
}

/**
 * Shows the edit tab to provide the user an interface to edit the field of his choice.
 */
function editEntry(entryId) {
	var sep = entryId.indexOf("-");
	var servEntry = entryId.substring(divName.length, sep);
	var fieldEntry = entryId.substring(sep+1);

	var servicesTab = pageDoc.getElementById("servicestab");
	var editTab = pageDoc.getElementById("edittab");

	editTab.innerHTML = printEditTab(servEntry, fieldEntry);
	showTab("editview");
}

/**
 * Sets up the edit tab to provide the user an interface to edit the field of his choice.
 */
function printEditTab(servEntry, fieldEntry) {
	var result = "";

	result += "<h3>Editing field \"" + headers[fieldEntry] + "\" of service \"" + getValueOfField(services[servEntry], "dc:title") +  "\"</h3>";

	var value = getValueOfField(services[servEntry], headers[fieldEntry]);

	result += "<b>" + headers[fieldEntry] + ":</b> ";

	result += "<form name=\"editform\">";
	result += "<span id=\"currentry\" style=\"display:none;visibility:hidden\">" + servEntry + "-" + fieldEntry + "</span><input type=\"text\" size=\"50\" name=\"entryvalue\" value=\"" + value + "\" />";

	// + getValueOfField(services[servEntry], headers[fieldEntry]);
	result += "<br><br>";
	result += "<input type=\"submit\" onclick=\"submitNewValue();return false;\" value=\"Save\">";
	result += "<input type=\"submit\" onclick=\"hideEditTab();return false;\" value=\"Cancel\"></form><br>";

	return result;
}

/**
 * Saves the new value of a field after editing it and reloads the services table.
 */
function submitNewValue() {
	var theEntry = pageDoc.getElementById("currentry").innerHTML;
	var servEntry = theEntry.substring(0, theEntry.indexOf("-"));
	var fieldEntry = theEntry.substring(theEntry.indexOf("-")+1);
	var newValue = pageDoc.editform.entryvalue.value;

	if (isObject(services[servEntry][headers[fieldEntry]])) {
		var subHeader = newValue.substring(0, newValue.indexOf(": "));
		if (subHeader != -1) {
			var values = newValue.substring(newValue.indexOf(": ") + 2).split(",");
			services[servEntry][headers[fieldEntry]][subHeader] = values;
		} else {
			alert("Error in parsing the field value!");
		}
	} else {
		services[servEntry][headers[fieldEntry]] = newValue;
	}

	processServices();
	hideEditTab();
}

/**
 * Hides the edit tab.
 */
function hideEditTab() {
	pageDoc.getElementById("edittab").innerHTML = "<h3>No field selected for editing!</h3>";
	showTab("servview");
}

/*
 * Manages the menu.
 */
function selectMenuItem(currItem) {
	var menuItemArray = new Array();
	spanArray = pageDoc.getElementsByTagName("span");

	for (var i=0; i<spanArray.length; i++) {
		if (spanArray[i].className.indexOf("menuitem") > -1) {
			spanArray[i].className = "menuitem";
		}
	}

	pageDoc.getElementById(currItem).className = "menuitemsel";
}

/*
 * Handles a click on a menu item.
 */
function handleMenuReq(e) {
	e = e ? e : window.event;
	var target = e.target ? e.target : e.srcElement;
	showTab(target.id);
}

/**
 * Shows a tab according to requested menu item.
 */
function showTab(targetId) {
	var servicesTab = pageDoc.getElementById("servicestab");
	var editTab = pageDoc.getElementById("edittab");
	var regTab = pageDoc.getElementById("regtab");
	var loadTab = pageDoc.getElementById("loadtab");

	servicesTab.style.visibility = "hidden";
	servicesTab.style.display = "none";
	editTab.style.visibility = "hidden";
	editTab.style.display = "none";
	regTab.style.visibility = "hidden";
	regTab.style.display = "none";
	loadTab.style.visibility = "hidden";
	loadTab.style.display = "none";

	// Check class
	switch (targetId) {
		case "servview":
			servicesTab.style.visibility = "visible";
			servicesTab.style.display = "block";
			break;
		case "editview":
			editTab.style.visibility = "visible";
			editTab.style.display = "block";
			break;
		case "regview":
			regTab.style.visibility = "visible";
			regTab.style.display = "block";
			break;
		case "loadview":
			loadTab.style.visibility = "visible";
			loadTab.style.display = "block";
			break;
	}

	selectMenuItem(targetId);
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
		if (isArray(value)) {
			var result = "";

			for (var i=0; i<value.length; i++) {
				if (isObject(value[i]) || isArray(value[i])) {
					result = getValueOfField(value, i);
				} else {
					result += value[i];
				}
				if (i<value.length-1) {
					result += ", ";
				}
			}

			// Do something
			return result;
		} else if (isObject(value)) {
			var result = "";

			for (var key in value) {
				result += key + ": " + getValueOfField(value, key);
			}
			return result;

		} else {
			return value;
		}
	} else {
		return "";
	}
}

/**
 * Sends out a POST requested.
 * @param url the target URL to POST to.
 * @param p a key/value map of objects to POST.
 */
function doPost(url, p) {
	var myForm = document.createElement("form");
	myForm.method = "post";
	myForm.action = url;

	for (var k in p) {
		var myInput = document.createElement("input");
		myInput.setAttribute("name", k);
		myInput.setAttribute("value", p[k]);
		myForm.appendChild(myInput);
	}

	pageDoc.body.appendChild(myForm);
	myForm.submit();
	pageDoc.body.removeChild(myForm);
}


	/**##################
	# Toolbox Functions #
	###################*/

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
					result += "[" + p + "] => " + typeof(theObj);
					result += print_r(theObj[p]);
				} else {
					result += "[" + p + "] => " + theObj[p];
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
 * Checks if a specific value is in a given array.
 * @param needle the value to be looked for.
 * @param haystack the array in which the needle is sought.
 * @return true if the needle is in the haystack, false if not.
 */
function in_array(needle, haystack) {
	var inArray = false;

	if (isArray(haystack)) {
		for (var i=0; i<haystack.length; i++) {
			if (haystack[i].indexOf(needle) != -1) {
				inArray = true;
				break;
			}
		}
	} else
		if (haystack.indexOf(needle) != -1)
			inArray = true;

	return inArray;
}

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
 * Checks if the object given is an object by checking its constructor.
 * @obj the object to be tested.
 * @return true if the object is an array.
 */
function isObject(obj) {
	if (obj.constructor.toString().indexOf("Object") == -1) {
		return false;
	} else {
		return true;
	}
}

-->