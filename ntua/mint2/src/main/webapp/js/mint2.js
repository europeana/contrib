Mint2 = {};

Mint2.languages = {
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
Mint2.languages.labelForCode = function(code) {
	return Mint2.languages[code];
};
Mint2.crossDomainPage = function (page, callback) {
	$.get("CrossDomainPage.action", {
		url: page,
	}, callback);
};

Mint2.INFO = "info";
Mint2.OK = "ok";
Mint2.WARNING = "warning";
Mint2.ERROR = "error";
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
	$("<span>").addClass("mint-message").append(message).appendTo(div);
	
	return div;
};

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
		plugins : ["themes", "html_data", "ui"],
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

Mint2.jQueryPlugin = function(name, object) {
	$.fn[name] = function(options) {
		if ( this.length ) {
			return this.each(function() {
				$(this).data(name, new object(this, options));
			});
		}
	}
}; 

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