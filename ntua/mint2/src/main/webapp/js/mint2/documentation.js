/*
 sample call:
 var doc = new MintDocumentation();
 doc.openDocumentation(); - opens default documentation page
 doc.openDocumentation("/mapping-editor-functions"); - opens documentation resource
 doc.openDocumentation({ resource: "/mapping-editor/functions" }); - open documentation for a specific resource
 doc.openDocumentation({ title: "Resource", resource: "/mapping-editor" }); - open documentation for a specific resource and define a title for the panel
 */

function MintDocumentation(options) {
	this.settings = $.extend({}, {
		url: "http://mint.image.ece.ntua.gr/mint2",
		resource: "/",
		suffix: "",
		title: "Mint2 Documentation"
	}, options);
}

MintDocumentation.prototype.getResourceURL = function (parameters) {
	var resource = this.settings.resource;
	
	if(!(parameters instanceof Object)) {
		resource = parameters;
	} else if(parameters.resource != undefined) resource = parameters.resource;

	return this.settings.url + resource + this.settings.suffix;
}

MintDocumentation.prototype.getDocumentationIFrame = function(parameters) {
	var iframe = $("<iframe>").css({
		width: "100%",
		height: "100%"
	}).attr("id", "mint2-documentation").attr("src", this.getResourceURL(parameters));
	
	return iframe;
}

MintDocumentation.prototype.openDocumentation = function(parameters) {
	if((parameters instanceof Object) && parameters.target != undefined) {
		window.open(this.getResourceURL(parameters), parameters.target);
	} else {
		var iframe = this.getDocumentationIFrame(parameters);
		var data = $.extend({}, this.settings, parameters);
		$K.kaiten('load', { kConnector:"html.string", kTitle: data.title, html: iframe });
	}
}

MintDocumentation.prototype.embed = function (element, content) {
	var icon = $("<span>").addClass("mint2-documentation mint2-topright");
	
	var html = content;
	if(MintDocumentation.resources != undefined && MintDocumentation.resources[content] != undefined) {
		html = MintDocumentation.resources[content];
	}
	
	$(element).append(icon);
	icon.qtip({
		content: { text: html },
		show: { when: "click", solo: true },
		hide: { when: "click" },
		position: {
			corner: {
				target: "middleLeft",
				tooltip: "topRight"
			}
		},
		style: {
			name: "cream",
			width: { max : 500 },
			tip: { corner: 'rightTop' },
			classes: {
				tooltip: "mapping-tooltip"
			}
		}
	}).bind('click', function(event){ event.preventDefault(); return false; });
}

Mint2.documentation = new MintDocumentation();