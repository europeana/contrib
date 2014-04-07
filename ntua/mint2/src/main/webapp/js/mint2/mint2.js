/**
 * See (http://jquery.com/).
 * @name $
 * @class 
 * See the jQuery Library  (http://jquery.com/) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 * @ignore
 */
 
/**
 * See (http://jquery.com/)
 * @name fn
 * @class 
 * See the jQuery Library  (http://jquery.com/) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 * @memberOf $
 * @ignore
 */


/**
 * @namespace Generic Mint2 namespace
 */
Mint2 = {};

Mint2.languages = {}
/**
 * Hash map that relates language codes to language labels
 */
Mint2.languages.list = {
	"aa": "Afar",
	"ab": "Abkhazian",
	"af": "Afrikaans",
	"am": "Amharic",
	"ar": "Arabic",
	"as": "Assamese",
	"ay": "Aymara",
	"az": "Azerbaijani",
	
	"ba": "Bashkir",
	"be": "Byelorussian",
	"bg": "Bulgarian",
	"bh": "Bihari",
	"bi": "Bislama",
	"bn": "Bengali; Bangla",
	"bo": "Tibetan",
	"br": "Breton",
	
	"ca": "Catalan",
	"co": "Corsican",
	"cs": "Czech",
	"cy": "Welsh",
	
	"da": "Danish",
	"de": "German",
	"dz": "Bhutani",
	
	"el": "Greek",
	"en": "English",
	"eo": "Esperanto",
	"es": "Spanish",
	"et": "Estonian",
	"eu": "Basque",
	
	"fa": "Persian",
	"fi": "Finnish",
	"fj": "Fiji",
	"fo": "Faroese",
	"fr": "French",
	"fy": "Frisian",
	
	"ga": "Irish",
	"gd": "Scots Gaelic",
	"gl": "Galician",
	"gn": "Guarani",
	"gu": "Gujarati",
	
	"ha": "Hausa",
	"he": "Hebrew (formerly iw)",
	"hi": "Hindi",
	"hr": "Croatian",
	"hu": "Hungarian",
	"hy": "Armenian",
	
	"ia": "Interlingua",
	"id": "Indonesian (formerly in)",
	"ie": "Interlingue",
	"ik": "Inupiak",
	"is": "Icelandic",
	"it": "Italian",
	"iu": "Inuktitut",
	
	"ja": "Japanese",
	"jw": "Javanese",
	
	"ka": "Georgian",
	"kk": "Kazakh",
	"kl": "Greenlandic",
	"km": "Cambodian",
	"kn": "Kannada",
	"ko": "Korean",
	"ks": "Kashmiri",
	"ku": "Kurdish",
	"ky": "Kirghiz",
	
	"la": "Latin",
	"ln": "Lingala",
	"lo": "Laothian",
	"lt": "Lithuanian",
	"lv": "Latvian, Lettish",
	
	"mg": "Malagasy",
	"mi": "Maori",
	"mk": "Macedonian",
	"ml": "Malayalam",
	"mn": "Mongolian",
	"mo": "Moldavian",
	"mr": "Marathi",
	"ms": "Malay",
	"mt": "Maltese",
	"my": "Burmese",
	
	"na": "Nauru",
	"ne": "Nepali",
	"nl": "Dutch",
	"no": "Norwegian",
	
	"oc": "Occitan",
	"om": "(Afan) Oromo",
	"or": "Oriya",
	
	"pa": "Punjabi",
	"pl": "Polish",
	"ps": "Pashto, Pushto",
	"pt": "Portuguese",
	
	"qu": "Quechua",
	
	"rm": "Rhaeto-Romance",
	"rn": "Kirundi",
	"ro": "Romanian",
	"ru": "Russian",
	"rw": "Kinyarwanda",
	
	"sa": "Sanskrit",
	"sd": "Sindhi",
	"sg": "Sangho",
	"sh": "Serbo-Croatian",
	"si": "Sinhalese",
	"sk": "Slovak",
	"sl": "Slovenian",
	"sm": "Samoan",
	"sn": "Shona",
	"so": "Somali",
	"sq": "Albanian",
	"sr": "Serbian",
	"ss": "Siswati",
	"st": "Sesotho",
	"su": "Sundanese",
	"sv": "Swedish",
	"sw": "Swahili",
	
	"ta": "Tamil",
	"te": "Telugu",
	"tg": "Tajik",
	"th": "Thai",
	"ti": "Tigrinya",
	"tk": "Turkmen",
	"tl": "Tagalog",
	"tn": "Setswana",
	"to": "Tonga",
	"tr": "Turkish",
	"ts": "Tsonga",
	"tt": "Tatar",
	"tw": "Twi",
	
	"ug": "Uighur",
	"uk": "Ukrainian",
	"ur": "Urdu",
	"uz": "Uzbek",
	
	"vi": "Vietnamese",
	"vo": "Volapuk",
	
	"wo": "Wolof",
	
	"xh": "Xhosa",
	
	"yi": "Yiddish (formerly ji)",
	"yo": "Yoruba",
	
	"za": "Zhuang",
	"zh": "Chinese",
	"zu": "Zulu"
};

/**
 * Returns a label that corresponds to the specified language code.
 * @return Language label
 */
Mint2.languages.labelForCode = function(code, showCode) {
	var label = Mint2.languages.list[code];
	if(showCode) label = code + " - " + label;
	return label;
};

/**
 * Utility method to make a cross domain page request.
 * Uses the CrossDomainPage action.
 * 
 * @see <a href="http://mint.image.ece.ntua.gr/mint2-javadoc/index.html?gr/ntua/ivml/mint/actions/CrossDomainPage.html" target="_blank">CrossDomainPage</a>
 * 
 * @param page URL of the cross domain request.
 * @param callback function that will handle the request response.
 */
Mint2.crossDomainPage = function (page, callback) {
	$.get("CrossDomainPage.action", {
		url: page,
	}, callback);
};

// Mint2 Messages
Mint2.INFO = "info";
Mint2.OK = "ok";
Mint2.WARNING = "warning";
Mint2.ERROR = "error";

/**
 * Creates an Mint2 message.
 *
 * @function
 * @param {String} message The message that will be displayed. Can be either a string or a jQuery object.
 * @param {String} [type=Mint2.INFO] Type of the message. Controls how the message will be displayed. Possible values: Mint2.INFO, Mint2.OK, Mint2.WARNING, Mint2.ERROR
 * 
 * @example
 * <pre>
 * var message = Mint2.message("This is an information message", Mint2.OK);
 * </pre>
 * <img src="http://mint.image.ece.ntua.gr/mint2/wp-content/uploads/2013/04/Mint2.message-example.png"/>
 */
Mint2.message = function(message, type) {
	if(type == undefined) type = Mint2.INFO;
	
	var state = "highlight";
	var icon = "info";
	
	if(type == Mint2.WARNING) {
		state = "warning";
		icon = "warning";
	} else if(type == Mint2.ERROR) {
		state = "error";
		icon = "alert";
	} else if(type == Mint2.OK) {
		state = "ok";
		icon = "ok";
	}
	
	var div = $("<div>").addClass("ui-state-" + state + " mint-message").addClass("ui-corner-all");
	$("<span>").addClass("ui-icon").addClass("ui-icon-" + icon).appendTo(div);
	$("<span>").addClass("mint-message-content").append(message).appendTo(div);
	
	return div;
};

/**
 * Creates an Mint2 loading message.
 *
 * @function
 * @param {String} [message="Loading" The message that will be displayed. Can be ommited.
 * 
 * @example
 * <pre>
 * var loading = Mint2.loading();
 * var loadingCustom = Mint2.loading("Loading with custom message");
 * </pre>
 */
Mint2.loading = function(message) {
	if(message == undefined) message = "Loading";
	
	var div = $("<div>").addClass("mint-loading");
	$("<span>").addClass("mint-spinner").appendTo(div);
	$("<span>").addClass("mint-message-content").append(message).appendTo(div);
	
	return div;
};

Mint2.languageSelection = function(options) {
	var settings = $.extend({}, {
		languages: Mint2.languages.list,
		showCode: false
	}, options);
	
	var select = $("<select>").change(function() {
		var lang = select.val();
		if(settings.onChange != undefined) {
			settings.onChange(lang);
		} else {
			console.log(lang);
		}
	}).addClass("mint2-language-select");

	var notSet = $("<option>").text("- not set -").attr("value", "").appendTo(select);
	for(var i in settings.languages) {
		var lang = i;
		var label = Mint2.languages.labelForCode(lang, settings.showCode);
		var option = $("<option>").text(label).attr("value", lang);
		if(settings.selected != undefined && settings.selected == lang) {
			option.attr("selected", "selected");
			console.log("SELECTED " + lang);
		}
		select.append(option);
	}

	return select;
}

// Mint2 JSON Utilities
Mint2.jsonToList = function(data) {
	var ul = $("<ul>");
	
	for(var property in data){
		var li = $("<li>").appendTo(ul);
		var label = property;
		var content = data[property];
		
		if($.isArray(content)) {
				content = "[" + content.length + "]";
		} else if(typeof(content) === 'object') {
			var c = "";
			if(content.id != undefined) c += " id: " + content.id;
			if(content.name != undefined) {
				c += " name: " + content.name;
				if(content.prefix != undefined) c += " prefix: " + content.prefix;
			}
			if(c.length == 0) c = " ...";
			content = "{" + c + " }";
		}
		
		var a = $("<a>")
		.appendTo(li)
		.append($("<span>").css("font-weight", "bold").text(label))
		.append($("<span>").text(": " + content));
		
		if(typeof(data[property]) === 'object') li.append(Mint2.jsonToList(data[property]));
	}
	
	return ul;
};

Mint2.jsonViewer = function(data) {
	var viewer = $("<div>").css("line-height", "20px");
	viewer.append(Mint2.jsonToList(data));
	viewer.jstree({
		plugins : ["themes", "html_data", "ui","dnd"],
		core: {
			animation: 100
		},
		ui: {
			select_limit: 1,
		},
		themes: {
			theme: "classic",
			dots: false,
			icons: false
		}
	});
	
	return viewer;
};

// Mint2 jQuery 
Mint2.jQueryPlugin = function(name, object) {
	$.fn[name] = function(options) {
		if ( this.length ) {
			return this.each(function() {
				$(this).data(name, new object(this, options));
			});
		}
	}
}; 

// Mint2 Widgets
Mint2.dialog = function(content, title) {
	if(title == undefined) title = "Mint2";
	var $dialog = $('<div>')
	.html(content)
	.dialog({
		autoOpen: false,
		title: title,
		buttons: {
			Ok: function() {
				$( this ).dialog( "close" );
			}
		}
	});
	$dialog.dialog('open');
}

/**
 * Creates a table widget with that displays the contents of a javascript object.
 * @param {Object} options 
 * @param {Object} options.data Javascript object that contains tha table data. Object keys will be used as entry labels. If options.data is an array, they will be displayed as a list.
 * @param {String} [options.title] Add table header with specified title.  
 * @param {boolean} [options.collapsed=false] Set to true to render table collapsed by default.
 * @param {function} [options.label] Function that gets an entry label and returns a modified label.
 *   
 * @example
 * <pre>
 * 	var table = Mint2.dataTable({
 *		title: "dc:title information",
 *		data: {
 *		  "XPath":          "/metadata/record/title",
 *		  "Namespace URI":  "http://purl.org/dc/elements/1.1/",
 *		  "Count":          "560",
 *		  "Distinct Count": "457",
 *		  "Average Length": "35.95"
 *		}
 *	});
 * </pre>
 * 	<img src="http://mint.image.ece.ntua.gr/mint2/wp-content/uploads/2013/04/Mint2.dataTable-example.png"/>
 */
Mint2.dataTable = function(options) {
	var box = $("<div>").addClass("mint2-box");
	var defaults = {
		collapsed: false,
		expand: undefined,		// function callback to be called after expansion.
		collapse: undefined,		// function callback to be called after collapsing.
	}
	var settings = $.extend({}, defaults, options);

	var rows = $("<div>").addClass("mint2-dataTable").appendTo(box);

	// add optional header
	if(settings.title != undefined) {
		var header = $("<div>")
		.addClass("mint2-header")
		.prependTo(box)
		.css("cursor", "pointer")
		.click(function(event, ui) {
			var theRows = $(this).parent().find(".mint2-dataTable");
			var isCollapsing = theRows.is(":visible");

			$(this).find("span.mint2-icon")
			.removeClass("ui-icon-triangle-1-e ui-icon-triangle-1-s")
			.addClass("ui-icon ui-icon-triangle-1-" + (isCollapsing?"e":"s"))
			theRows.slideToggle();
			
			if(isCollapsing) {
				if(settings.collapse != undefined) settings.collapse();
			} else {
				if(settings.expand != undefined) settings.expand();				
			}
		});
		
		var icon = $("<span>").addClass("mint2-icon").addClass("ui-icon ui-icon-triangle-1-" + ((settings.collapsed)?"e":"s")).css({
			"display": "inline-block",
			"vertical-align": "text-bottom"
		}).css("margin-right", "5px").prependTo(header);
		
		$("<span>").addClass("mint2-label").append(settings.title).appendTo(header);
	}
	
	if(settings.collapsed) rows.hide();
	
	// add data rows
	var width = 0;
	var body = $("body");
	var data = settings.data;
	for(var key in data) {
		var value = data[key];
		
		var label = undefined;
		if(settings.label != undefined) {
			label = settings.label(key);
		}
		if(label == undefined || label == null) label = $("<span>").addClass("mint2-label").text(key);
		
		label.appendTo(body);
		var vWidth = Number(label.width());
		if(vWidth > width) width = vWidth;
		label.remove();
		
		var content = undefined;
		if(settings.content != undefined) {
			content = settings.content(value);
		}
		if(content == undefined || content == null) {
			content = $("<span>").addClass("mint2-content").append(value);
		}
		
		var row = $("<div>")
		.addClass("mint2-dataTable-row")
		.appendTo(rows);
		if(!$.isArray(data)) row.append(label);
		row.append(content);
	}
	rows.find(".mint2-label").width(width);
	
	return box;
}

/**
 * Creates a generic search box widget to handle searches.
 * 
 * @param {Object} options 
 * @param {function} options.callback Function that performs the search based on requested term.
 * @param {String} [options.prompt] Default prompt text inside search box (Not yet implemented, but set to show in future update :).
 *   
 * @example
 * <pre>
 * 	var table = Mint2.searchBox({
 *		prompt: "Search anything!",
 *		callback: function(term) {
 *			console.log("You searched for: " + term);
 *		}
 *	});
 * </pre>
 * 
 * 	<img src="http://mint.image.ece.ntua.gr/mint2/wp-content/uploads/2013/04/Mint2.searchBox-example.png"/>
 */
Mint2.searchBox = function(options) {
	var container = $("<div>");
	var box = $("<div>").addClass("mint2-search-box").appendTo(container);
	var select = undefined;
	var icon = $("<span>").addClass("mapping-action-search").css("position", "absolute").appendTo(box);
	var self = this;
	
	var input = $("<input>").appendTo(box).keyup(function(event) {
		if(event.keyCode == 13){
			var term = input.val();
			console.log(term, options);
			if(options.callback != undefined) {
				if( options.selections != undefined && select != undefined) {
					options.callback(term, select.val());
				} else {
					options.callback(term );
				}
			}
		}
	});
	
	if( options.selections != undefined ) {
		select = $("<select>").addClass( "mint2-search-box-select");
		for( var selVal in options.selections ) {
			$("<option>").attr("value", selVal).text( options.selections[selVal] ).appendTo( select );
		}
		select.appendTo( box );
		input.css( "width","initial");
	}
	return container;
}
