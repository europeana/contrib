/**
 * Creates an XSD Mapping Editor.
 *
 * @constructor
 * @this {XSDMappingEditor}
 * @param {string} container The editor's container.
 * @param {object} options The widget's configuration options.
 */

function XSDMappingEditor(container) {
	var self = this;
	this.ajaxUrl = "mappingAjax";

	if(container != undefined) this.layout(container);
}

XSDMappingEditor.prototype.layout = function(container) {
	this.container = $("#" + container);
	this.container.tree = $("<div>").attr("id", "editor-tree");
	this.container.mappings = $("<div>").attr("id", "editor-mappings");
	this.container.loading = $("<div>").attr("id", "editor-loading").hide();
	this.container.toolbar = $("<div>");

	this.layout = {};
	this.layout.north = $("<div>").addClass("ui-layout-north");
	this.layout.center = $("<div>").addClass("ui-layout-center");
	this.layout.west = $("<div>").addClass("ui-layout-west");
	this.layout.south = $("<div>").addClass("ui-layout-south");
	
	this.layout.north.append(this.container.toolbar);
	this.layout.west.append(this.container.tree);
	this.layout.center.append(this.container.mappings);
	this.layout.center.append(this.container.loading);
	this.container.loading.dialog({
		draggable: false,
		resizable: false,
		autoOpen: false
	});
	this.container.loading.parent().find(".ui-dialog-titlebar").css("display", "none");
	this.container.loading.append($("<div>").addClass("editor-loading").append($("<span>").addClass("editor-loading-spinner")).append($("<span>").text("Loading...")));
	
	this.container.append(this.layout.north);
	this.container.append(this.layout.east);
	this.container.append(this.layout.center);
	this.container.append(this.layout.west);
	
	this.container.layout({
		scrollToBookmarkOnLoad: false,
		applyDefaultStyles: true,
		north: {
			closable: false,
			resizable: false,
			paneClass: "editor-pane-north",
			spacing_open: 0
		},	
		west: {
			paneClass: "editor-pane-west",
		},
		center: {
			paneClass: "editor-pane-center",
		}
	});

	var cp = $(this.container.closest('div[id^=kp]'));
	this.panel = cp;
}

XSDMappingEditor.prototype.init = function(dataUploadId, mappingId, lockId) {
	var self = this;
	this.dataUploadId = dataUploadId;
	this.mappingId = mappingId;
	this.lockId = lockId;

	console.log("options");
	var options = this.panel.data("kpanel").options.beforedestroy = function () {
    	$.ajax({
    		url: "LockSummary",
    		data: { lockDeletes: self.lockId }
    	});
    };
    
    this.panel.kpanel("setTitle", "Loading...");	
	this.showLoading();
	
	console.log("init tree");
	this.initTreeContainer(this.dataUploadId);
	console.log("init mappings");
	this.initMappingsContainer(this.mappingId);
}

XSDMappingEditor.prototype.showLoading = function() {
	this.container.loading.dialog("open");
}

XSDMappingEditor.prototype.hideLoading = function() {
	this.container.loading.dialog("close");
}

XSDMappingEditor.prototype.initToolbar = function() {
	var toolbar = this.container.toolbar;
	var self = this;
	
	toolbar.empty();
	var buttons = $("<div>").addClass("editor-toolbar");
	var preview = $("<button>").addClass("editor-toolbar-button").text("Preview").attr("title", "Preview").button({ icons: { primary: 'editor-toolbar-preview' }}).click(function () {
		self.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var content = $("<div>").appendTo(details);
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			
			panel.find(".panel-body").before(details);
			content.itemPreview({
				datasetId: self.dataUploadId,
				mappingId: self.mappingId,
				showMappings: self.preferences.get("previewShowMappings")
			}).bind("beforePreviewItem", function() {
			    var panel = content.closest('div[id^="kp"]');
			    $K.kaiten("maximize", panel);
			});
		}, "Preview");
	}).appendTo(buttons);	
	var navigation = $("<button>").addClass("editor-toolbar-button").text("Navigation").attr("title", "Navigation").button({ icons: { primary: 'editor-toolbar-navigation' }}).click(function () {
		self.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var browser = $("<div>").appendTo(details);
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			
			panel.find(".panel-body").before(details);
			browser.xsdmappingNavigation({
				editor: self,
				datasetId: self.dataUploadId,
				mappingId: self.mappingId
			});
		}, "Navigation");
	}).appendTo(buttons);
	
	$("<span>").addClass("editor-toolbar-separator").appendTo(buttons);

	var btnPreferences = $("<button>").addClass("editor-toolbar-button").text("Preferences").attr("title", "Preferences").button({ icons: { primary: 'editor-toolbar-preferences' }}).click(function () {
		self.openPreferences("Mapping editor preferences");
	}).appendTo(buttons);

	var help = $("<button>").addClass("editor-toolbar-button").text("Help").attr("title", "Documentation").button({ icons: { primary: 'editor-toolbar-help' }}).click(function () {
		self.loadSubpanel(function(panel) {
			var iframe = new MintDocumentation().getDocumentationIFrame("/documentation/mapping-editor");
			pppp =panel;
			panel.find(".panel-body").before(iframe);
			panel.find(".newtab").css("display", "inline");			
			panel.find(".newtab").click(function(event) {
				e.preventDefault();
				e.stopPropagation();
			});			
		}, "Mapping editor documentation");
	}).appendTo(buttons);
	
	toolbar.append(buttons);

	$("<div>").addClass("editor-toolbar-section").append(
		$("<span>").addClass("editor-toolbar-name").text(this.metadata.name)
	).append(
		$("<div>").addClass("editor-toolbar-detail")
		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.schema))
	).appendTo(toolbar);

	$("<div>").addClass("editor-toolbar-section").append(
		$("<div>").addClass("editor-toolbar-detail")
		.append($("<span>").addClass("editor-toolbar-label").text("Organization:"))
		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.organization))
	).append(
		$("<div>").addClass("editor-toolbar-detail")
		.append($("<span>").addClass("editor-toolbar-label").text("Created:"))
		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.created))
	).appendTo(toolbar);	
}

XSDMappingEditor.prototype.initTreeContainer = function(dataUploadId) {
	var editor = this;
	this.tree = new SchemaTree("editor-tree");
	this.tree.loadFromDataUpload(dataUploadId, function() {
		editor.validate();
	});
	
	this.tree.selectNodeCallback = function(data) {
		console.log(data[0].data());
		var xpath = data[0].data("xpath");
		var xpathHolderId = data[0].data("xpathHolderId");
		var count = data[0].data("count");
		//console.log(data[0].data());
		var parts = xpath.split("/");
		var title = parts[parts.length - 1];
		
		editor.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var browser = $("<div>").appendTo(details);
			
			panel.find(".panel-body").before(details);
			browser.valueBrowser({
				xpathHolderId: xpathHolderId
			});
		}, title);
	};
	
	this.tree.dropCallback = function(source, target) {
		if($(target).is(".mapping-element-mapping")) {
			var container = $(target).parent().parent();
			var element = container.data("mappingArea").target;
			
			var id = element.id;
			var idx = $(target).find(".mapping-element-mapping-area").data("mapping").index;
			var xpath = source.data("xpath");
	
			$.ajax({
				url: editor.ajaxUrl,
				context: this,
				dataType: "json",
				data: {
					command: "setXPathMapping",
					xpath: xpath,
					target: id,
					index: idx
				},
				
				success: function(response) {
					container.data("mappingArea").target = response;
					container.mappingArea("refresh");
				}
			});
		} else if($(target).is(".mapping-condition-xpath")) {
			ttt = target;
			var container = $(target).parent().parent().parent();
			var clause = $(target).parent();
			var path = clause.mappingConditionClause("path");
			
			var element = container.data("mappingCondition").target;
			
			var id = element.id;
			var xpath = source.data("xpath");

			$.ajax({
				url: editor.ajaxUrl,
				context: this,
				dataType: "json",
				data: {
					command: "setConditionClauseKey",
					key: "xpath",
					value: encodeURIComponent(xpath),
					id: id,
					path: path
				},
				
				success: function(response) {
					var element = container.data("mappingCondition").element;
					element.mappingArea("setCondition", response);
					element.mappingArea("refresh");
				}
			});
		}
	};
}

XSDMappingEditor.prototype.initMappingsContainer = function(mappingId) {
	var self = this;
//	console.time("init mappings");
//	console.time("init mappings network");
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "init",
			mappingId: mappingId
		},
		success: function(response) {
//			console.timeEnd("init mappings network");
			if(response != undefined) {
				if(response.error != undefined) {
					alert(response.error);
				} else if(response) {
					this.target = response.targetDefinition;
					this.configuration = response.configuration;
					this.configuration.groups = this.target.groups;
					this.metadata = response.metadata;

					this.preferences = new EditorPreferences({
						element: "mapping-editor-preferences",
						preferences: response.preferences,
						onValueChange: function(source, value) {
							self.savePreferences();
						}
					});

					var m = $("<div>").appendTo(this.container.mappings).mappingElement({
						target: this.target.template,
						groups: this.target.groups,
						editor: this
					});
					m.mappingElement("toggleChildren");
					this.panel.kpanel("setTitle", "Mapping: " + this.metadata.name + " (" + this.metadata.schema + ")");
					
					this.initToolbar();
					
					this.validate();
					
					// bad layout practice ..
					var self = this;
					window.setInterval(function () {
						self.container.layout().resizeAll();
					}, 500);
					
//					console.timeEnd("init mappings");

					$K.kaiten('maximize', this.panel);	
					this.hideLoading();
				}			
			} else {
				this.hideLoading();
			}
		}
	});
}

XSDMappingEditor.prototype.showMapping = function (id, navigation) {
	if(this.container.loading.is(":visible")) return;

	var self = this;
	this.container.mappings.empty();
	this.showLoading();
	
	if(navigation == undefined) {
		navigation = {};
	}
	
	$.ajax({
		url: self.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "getElement",
			id: id
		},
		success: function(response) {
			if(response.parent != undefined) {
				var p = $("<div>")
				.addClass("mapping-element-parent")
				.appendTo(self.container.mappings);
				
				p.append($("<span>").text("Up to "));
				
				if(response.parent.prefix != undefined) {
					p.append($("<span>").addClass("mapping-element-prefix").text(response.parent.prefix + ":"));
				}	
	
				p.append($("<span>").addClass("mapping-element-name").text(response.parent.name));
				
				p.click(function() {
					self.showMapping(response.parent.id);
				});
			}
			
			var m = $("<div>").appendTo(self.container.mappings).mappingElement({
				target: response,
				editor: self,
				include: navigation.include,
				hide: navigation.hide
			});
			m.mappingElement("toggleChildren");
			this.hideLoading();
		}
	});
}

XSDMappingEditor.prototype.initPreviewContainer = function() {
	var navigation = this.configuration.preview;
	var fixed = $("<div>");
	var self = this;
	
	$("<div>").appendTo(fixed).itemBrowser({
		datasetId: this.dataUploadId
	});
		
	this.container.preview.empty();
	this.container.preview.append(fixed);
}


XSDMappingEditor.prototype.parseResponse = function(r) {
	var response = r;
	if(response.error != undefined) {
		alert(response.error);
		
		return null;
	}
	
	return response;
}

XSDMappingEditor.prototype.subscribeElement = function(element) {
	$(element).bind("afterRefresh", this.validate());
}

XSDMappingEditor.prototype.loadSubpanel = function(data, title, options) {
	if(this.isCreatingPanel) return false;
	this.isCreatingPanel = true;

	options = $.extend({}, {
		reference: undefined,
		replace: true
	}, options);
	
	var title = (title || data.kTitle);
	
	if(options.replace) {
		var cp = $(this.container.closest('div[id^=kp]'));
		$K.kaiten('removeChildren', cp, false);
	}
		
	var newpanel = undefined;
	if($.isFunction(data)) {
		newpanel = $K.kaiten('load', {
			kConnector:'html.string',
			kTitle:title,
			html: ""
		});
		
		data(newpanel);
	} else if(data instanceof jQuery) {
		newpanel = $K.kaiten('load', {
			kConnector:'html.string',
			kTitle:title,
			html: ""
		});
		
		newpanel.find(".panel-options").after(data);
	} else {
		newpanel = $K.kaiten('load', data);
	}
	
	if(this.panel != undefined) {
//		console.log(_editor.panel);
		newpanel.find(".titlebar .remove").click(function() {
			$K.kaiten('maximize', this.panel);
			$(".mapping-active").removeClass("mapping-active");
		});
	}
	
	newpanel.find(".newtab").css("display", "none");
	
	this.isCreatingPanel = false;
	
	if(options.reference != undefined) {
		$(".mapping-active").removeClass("mapping-active");
		options.reference.addClass("mapping-active");
	}
	this.panelReference = options.reference;
	
	return newpanel;
}

XSDMappingEditor.prototype.closeSubpanel = function() {
	$K.kaiten('removeChildren', this.panel, false);
	$K.kaiten('maximize', this.panel);
	this.panelReference = undefined;
}

XSDMappingEditor.prototype.closeSubpanelIfReferencedBy = function(reference) {
	if(reference != undefined && reference == this.panelReference) this.closeSubpanel();
}

XSDMappingEditor.prototype.attachPanelNode = function() {
	var lastPanel = $('div[id^="kp"]:last');
	var body = lastPanel.find('.panel-body');
//	console.log(body);
	body.append(this.panelNode);
}

XSDMappingEditor.prototype.getFirstByName = function(name, child) {
	var template = child;
	if(template == undefined) template = this.target.template;

	if(template.name == name) return template;

	var result = null;	

	if(template.children != undefined) {
		$.each(template.children, function(i, v) {
			result = XSDMappingEditor.prototype.getFirstByName(name, template.children[i]);
			if(result != null) return false;
		});
	}
	
	return result;
}

XSDMappingEditor.prototype.addBookmark = function(id, title) {
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "addBookmark",
			id: id,
			title: title
		},
		
		success: function(response) {
			this.bookmarks();
		}
	});
}

XSDMappingEditor.prototype.removeBookmark = function(id) {
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "removeBookmark",
			id: id
		},
		
		success: function(response) {
			this.bookmarks();
		}
	});
}

XSDMappingEditor.prototype.openPreferences = function(title) {
	var self = this;
	if(title == undefined) title = "Preferences";

	this.loadSubpanel(function(panel) {
		var contents = $("<div>");
		var prefs = self.preferences.editor().css("padding", "10px").appendTo(contents);			
	
		panel.find(".panel-body").before(contents);
		panel.find(".newtab").css("display", "inline");			
		panel.find(".newtab").click(function(event) {
			e.preventDefault();
			e.stopPropagation();
		});
	}, title);
}

XSDMappingEditor.prototype.savePreferences = function() {
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "setPreferences",
			preferences: JSON.stringify(this.preferences.get())
		},
		success: function(response) {
			if(response.error == undefined) {
				console.log("preferences saved");	
			} else {
				console.error(response);
			}
		}
	});
}

function ajaxTargetDefinition() {
	$.ajax({
		url: "mappingAjax",
		context: this,
		dataType: "json",
		data: {
			command: "getTargetDefinition"
		},
		success: function(response) {
			console.log(response);
		}
	});
}