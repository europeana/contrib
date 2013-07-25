<!--

/**
 * SIWA Software, version 0.9
 * Last update: February 24th, 2010
 *
 * Developer: Thomas Beekman, The National Library of the Netherlands
 * E-mail: tsbeekman@gmail.com
 * Date: December 2009 - February 2010
 *
 * This code is used to initialize the context menu when requested and invoke the service chosen.
 * Also, this code recognizes multiple value in a Metadata field and is able to present the user
 * a list of options after a specific service is chosen. 
 *
 */

    /**####################
	#	Global Variables  #
	#####################*/
	
var cmenu = "cmenu";
var cmHide;


    /**#########################
	#	Context Menu Functions #
	##########################*/
	
/**
 * Creates, initializes and positions the context menu.
 * @param e the event object of the Metadata field.
 */
function showContextMenu(e) {		
	if (!document.getElementById(cmenu))
		createContextMenu(cmenu);
	
	createServicesList(e, cmenu);	
	positionContextMenu(e, cmenu);
}

/** 
 * Checks the input value of the Metadata field for multiple objects by looking at separation characters. If
 * multiple objects are found, present the user a list of objects of which he can choose one.
 * @param servLabel the label of the service chosen.
 * @param targetId the id of the Metadata field span which was triggered.
 * @param inputValue the value of the Metadata field.
 */
function checkInputValue(servLabel, targetId, inputValue) {
	servLabel = unescape(servLabel);
	inputValue = unescape(inputValue);
	
	if (inputValue.indexOf(';&nbsp;') != -1) 	
		showOptionsList(servLabel, targetId, inputValue, ';&nbsp;');		
	else if (inputValue.indexOf(';') != -1) 	
		showOptionsList(servLabel, targetId, inputValue, ';');			
	else 				
		invokeService(servLabel, targetId, inputValue);	
}

/**
 * Rebuilds the current context menu for showing the user a list of objects of which he can choose one.
 * @param servLabel the label of the service chosen.
 * @param targetId the id of the Metadata field span which was triggered.
 * @param value the value of the Metadata field.
 * @param splitChar the character which is used as a separation character.
 */
function showOptionsList(servLabel, targetId, value, splitChar) {		
	var optionList = "<b><span align=\"center\">Select the input for the service '" + servLabel + "':</span></b>";
	
	optionList = optionList + "<li class=\"citem\" onclick=\"setDivHidden(cmenu);invokeService('" + servLabel + "', '" + targetId + "', '" + escape(value.replace(splitChar, " ")) + "');\">" + value;
	
	var options = value.split(splitChar);	
	for(var i=0; i<options.length; i++) 		
		optionList = optionList + "<li class=\"citem\" onclick=\"setDivHidden(cmenu);invokeService('" + servLabel + "', '" + targetId + "', '" + escape(options[i]) + "');\">" + options[i];
	
	injectCode(cmenu, optionList);		
	setDivVisible(cmenu);
}

/**
 * Creates a new div which is used as a context menu.
 * @param cmenu the id of the div.
 */ 
function createContextMenu(cmenu) {
	var divTag = document.createElement("div");       	
    divTag.id = cmenu;       
    
	divTag.onmouseover = cmCancelTO;
	divTag.onmouseout = cmHideTO;
	divTag.innerHTML = "Services loading...";
	
    document.body.appendChild(divTag);
}

/** 
 * Fills the cmenu div with services that can be used with the Metadata field which triggered the context menu.
 * @param e the event object of the Metadata field.
 * @param cmenu the id of the cmenu div.
 */
function createServicesList(e, cmenu) {
	var services = "";

	// Get Metadata name
	var target = e.target ? e.target : e.srcElement;	
	var theTrigger = getName(target.id);	
	var strippedTrigger = theTrigger;
		
	strippedTrigger = strippedTrigger.replace("europeana:", "");
	strippedTrigger = strippedTrigger.replace("dcterms:", "");
	strippedTrigger = strippedTrigger.replace("dc:", "");
	
	// Get Value of Metadata field
	var value = target.innerHTML;		
	var serviceNames = "<b><span align=\"center\">Available services for '" + strippedTrigger + "':</span></b>";
		
	// Get Service for the specific metadata field	
	services = getServices();	
		
	// TODO: Implement "triggerField:???"
	// TODO: use in_array!
	// TODO: CASE Insensitive
	
	for (var i=0; i<services.length; i++) {
		var service = services[i];		
		var invocation = service["sd:invocation"];
		if (invocation == null || (invocation.indexOf("automatic") == -1 || invocation == ""))  { 
			var triggers = service["sd:triggers"]["sd:trigger"];			
			var addService = false;			
						
			if ((in_array(strippedTrigger, triggers)) || (in_array("*", triggers)))
				addService = true;
			
			if ((service["sd:extraConditions"] != null) && (service["sd:extraConditions"]["sd:extraCondition"] != null) && (addService == true)) {				
				var extraConditions = service["sd:extraConditions"]["sd:extraCondition"];				
				addService = checkExtraConditions(extraConditions, theTrigger);					
			}
											
			if (addService) {				
				var servIcon = "- ";							
				var servLabel = service["sd:serviceLabel"];
				var servTitle = service["dc:title"];
				var servIcon = service["sd:logo"]; 
				
				if (servIcon != null)				
					servIcon = "<img height=\"16\" width=\"16\" alt=\"- \" src=" + servIcon + "> ";
				else
					servIcon = "- ";
							
				serviceNames = serviceNames + "<li class=\"citem\" onclick=\"setDivHidden(cmenu);checkInputValue('" + escape(servLabel) + "', '" + target.id + "', '" + escape(value) + "');\">" + servIcon + servLabel;				
			}
		}
	}
	
	injectCode(cmenu, serviceNames);
}

/**
 * Positions the context menu depending on the position of the user's clicking device.
 * @param e the event object of the Metadata field.
 * @param cmenu the id of the cmenu div.
 */
function positionContextMenu(e, cmenu) {		
	var x = mouseX(e)-5;
	var y = mouseY(e)-5;
			
	setDivLeft(cmenu, x);	
	setDivTop(cmenu, y);	
	setDivVisible(cmenu);
}


	/**################
	# Style Functions #
	#################*/

/**
 *  Alters a div's style.left.
 * @param div the id of the div which style is to be altered. 
 * @param left the new value for this property.
 */
function setDivLeft(div, left) {
	var the_style = document.getElementById(div).style;
	var the_left = parseInt(left);
	if (document.layers) 
		the_style.left = the_left;			
	else 
		the_style.left = the_left + "px";
}

/**
 *  Alters a div's style.top.
 * @param div the id of the div which style is to be altered. 
 * @param top the new value for this property.
 */
function setDivTop(div, top) {
	var the_style = document.getElementById(div).style;
	var the_top = parseInt(top);
	if (document.layers) 
		the_style.top = the_top;			
	else 
		the_style.top = the_top + "px";
}

/**
 * Gets the name of an object by using its id.
 * @param id the id of the object which name is requested.
 * @return the name of the object.
 */
function getName(id) {
	var object = document.getElementById(id);	
	return object.getAttribute("name"); 
}

/**
 * Gets the value of an object by using its id.
 * @param id the id of the object which name is requested.
 * @return the value of the object.
 */
function getValue(id) {
	var object = document.getElementById(id);
	return object.innerHTML;
}

/** 
 * Changes the inner HTML of an object.
 * @param id the id of the object which contents is to be changed.
 * @htmlCode the new value of this object.
 */
function injectCode(id, htmlCode) { 
	document.getElementById(id).innerHTML = htmlCode; 
}

/**
 * Sets the style property of an object to visible.
 * @param id the id of the object which should be set to visible.
 */
function setDivVisible(id) { 
	var style = document.getElementById(id).style; style.visibility = 'visible'; 
}	

/**
 * Sets the style property of an object to hidden.
 * @param id the id of the object which should be set to hidden.
 */
function setDivHidden(id) { 
	var style = document.getElementById(id).style; 
	style.visibility = 'hidden'; 
}

/** 
 * Sets the time out for the cmenu div after which it has to be set to hidden.
 */
function cmHideTO(to) { 
	cmHide = setTimeout("setDivHidden('" + cmenu + "');", 1000); 
}

/**
 * Cancels an already ongoing time out to keep the context menu visible.
 */
function cmCancelTO() { 
	clearTimeout(cmHide); 
}


	/**###################### 
	# Positioning Functions # 
	#######################*/
	
/** 
 * Gets the horizontal position of the user's clicking device from the event object.
 * @param evt the event object.
 * @return the X position of the mouse. Null on error.
 */
function mouseX(evt) {
	if (evt.pageX) 
		return evt.pageX;
	else if (evt.clientX)
	   return evt.clientX + (document.documentElement.scrollLeft ?
	   document.documentElement.scrollLeft :
	   document.body.scrollLeft);
	   
	else return null;
}

/** 
 * Gets the vertical position of the user's clicking device from the event object.
 * @param evt the event object.
 * @return the Y position of the mouse. Null on error.
 */
function mouseY(evt) {
	if (evt.pageY) 
		return evt.pageY;
	else if (evt.clientY)
	   return evt.clientY + (document.documentElement.scrollTop ?
	   document.documentElement.scrollTop :
	   document.body.scrollTop);
	   
	else return null;
}

/** 
 * Gets the horizontal scroll position of the page. 
 * @return the horizontal scroll position of the page. 
 */
function getScrollPosX() { 
	var x = 0;	

	if (typeof(window.pageYOffset) == 'number') 
		x = window.pageXOffset;		
	else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) 
		x = document.documentElement.scrollLeft;		
	else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) 
		x = document.body.scrollLeft;			

	return x; 
} 

/** 
 * Gets the vertical scroll position of the page. 
 * @return the vertical scroll position of the page. 
 */
function getScrollPosY() { 	
	var y = 0;

	if (typeof(window.pageYOffset) == 'number') 	
		y = window.pageYOffset;
	else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) 	
		y = document.documentElement.scrollTop;
	else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) 	
		y = document.body.scrollTop;	

	return y; 
} 


	/**##################
	# Toolbox Functions #
	###################*/

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

-->