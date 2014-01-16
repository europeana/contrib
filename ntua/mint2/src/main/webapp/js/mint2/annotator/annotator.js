function XMLAnnotator(container) {
	var self = this;
	this.ajaxUrl = "Annotator_ajax";
	
	this.ajax = new MappingAjax({ editor: this });

	if(container != undefined) this.layout(container);
}

XMLAnnotator.prototype = new XSDMappingEditor();
XMLAnnotator.prototype.constructor = XMLAnnotator;

XMLAnnotator.MODE_COMPLEX = 0;
XMLAnnotator.MODE_SIMPLE = 1;
XMLAnnotator.MODE_DEFAULT = XMLAnnotator.MODE_SIMPLE;

XMLAnnotator.prototype.layout = function(container) {
	this.container = $("#" + container);
	this.container.items = $("<div>").attr("id", "editor-items");
	this.container.annotations = $("<div>").attr("id", "editor-annotations");
	this.container.loading = $("<div>").attr("id", "editor-loading").hide();
	this.container.toolbar = $("<div>");

	this.layout = {};
	this.layout.north = $("<div>").addClass("ui-layout-north");
	this.layout.center = $("<div>").addClass("ui-layout-center");
	this.layout.west = $("<div>").addClass("ui-layout-west");
	this.layout.south = $("<div>").addClass("ui-layout-south");
	
	this.layout.north.append(this.container.toolbar);
	this.layout.west.append(this.container.items);
	this.layout.center.append(this.container.annotations);
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
			size: 400
		},
		center: {
			paneClass: "editor-pane-center",
		}
	});

	var cp = $(this.container.closest('div[id^=kp]'));
	this.panel = cp;
}

XMLAnnotator.prototype.init = function(dataUploadId) {
	var self = this;
	this.dataUploadId = dataUploadId;

	this.mode = XMLAnnotator.MODE_DEFAULT;
    this.panel.kpanel("setTitle", "Loading...");
	$K.kaiten('maximize', this.panel);
	
	this.showLoading();
	
	this.initItemsContainer();
	this.initAnnotationsContainer();
}

XMLAnnotator.prototype.initToolbar = function() {
	var toolbar = this.container.toolbar;
	var self = this;
	
	toolbar.empty();
	var buttons = $("<div>").addClass("editor-toolbar");

	this.btnNew = $("<button>")
	.addClass("editor-toolbar-button")
	.text("Create new item")
	.button({ icons: { primary: 'editor-toolbar-new' }})
	.attr("title", "Create a new item in this dataset")
	.click(function () {
		self.newItem();
	}).appendTo(buttons);
	
	this.btnDelete = $("<button>")
	.addClass("editor-toolbar-button")
	.text("Delete current item")
	.button({ icons: { primary: 'editor-toolbar-delete' }})
	.attr("title", "Delete current item")
	.click(function () {
		self.deleteItem();
	}).appendTo(buttons);
	
	$("<span>").addClass("editor-toolbar-separator").appendTo(buttons);

	var btnMode = $("<button>")
	.addClass("editor-toolbar-button")
	.text("Switch to complex")
	.attr("title", "Switch mode from complex to simple")
	.button({ icons: { primary: 'editor-toolbar-preview' }})
	.click(function () {
		if(self.mode == XMLAnnotator.MODE_SIMPLE) {
			self.mode = XMLAnnotator.MODE_COMPLEX;
			$(this).find(".ui-button-text").text("Switch to simple");
		} else {
			self.mode = XMLAnnotator.MODE_SIMPLE;
			$(this).find(".ui-button-text").text("Switch to complex");			
		}
		
		self.showMapping();
	}).appendTo(buttons);
	
	if(this.views.buttons != undefined) {
		for(var i in this.views.buttons) {
			var button = this.views.buttons[i];
			var label = button.label;
			var btnButton = this.viewButton(button).appendTo(buttons);
		}
	}

	var btnPreview = $("<button>").addClass("editor-toolbar-button").text("Preview").attr("title", "Preview").button({ icons: { primary: 'editor-toolbar-preview' }}).click(function () {
		self.lastButtonClicked = btnPreview;
		self.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var content = $("<div>").appendTo(details).addClass("editor-preview");
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			panel.find(".panel-body").before(details);
			
			self.loadPreview(content);
		}, "Preview");
	}).appendTo(buttons);
	
	
//	var navigation = $("<button>").addClass("editor-toolbar-button").text("Navigation").attr("title", "Navigation").button({ icons: { primary: 'editor-toolbar-navigation' }}).click(function () {
//		self.loadSubpanel(function(panel) {
//			var details = $("<div>").css("padding", "10px");
//			var browser = $("<div>").appendTo(details);
//			var body = panel.find(".panel-body");
//			details.css("height", body.height() - 20);
//			details.css("overflow", "auto");
//			
//			panel.find(".panel-body").before(details);
//			browser.text("Navigation");
//		}, "Navigation");
//	}).appendTo(buttons);
	
//	var btnPreferences = $("<button>").addClass("editor-toolbar-button").text("Preferences").attr("title", "Preferences").button({ icons: { primary: 'editor-toolbar-preferences' }}).click(function () {
//		self.openPreferences("Annotator preferences");
//	}).appendTo(buttons);

//	var help = $("<button>").addClass("editor-toolbar-button").text("Help").attr("title", "Documentation").button({ icons: { primary: 'editor-toolbar-help' }}).click(function () {
//		self.loadSubpanel(function(panel) {
//			var iframe = Mint2.documentation.getDocumentationIFrame("/documentation/editor");
//			pppp =panel;
//			panel.find(".panel-body").before(iframe);
//			panel.find(".newtab").css("display", "inline");			
//			panel.find(".newtab").click(function(event) {
//				e.preventDefault();
//				e.stopPropagation();
//			});			
//		}, "Anotator documentation");
//	}).appendTo(buttons);
	
	toolbar.append(buttons);

	this.metadata = {};
	$("<div>").addClass("editor-toolbar-section").append(
		$("<span>").addClass("editor-toolbar-name").text(this.metadata.name)
	).append(
		$("<div>").addClass("editor-toolbar-detail")
		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.schema))
	).appendTo(toolbar);

//	$("<div>").addClass("editor-toolbar-section").append(
//		$("<div>").addClass("editor-toolbar-detail")
//		.append($("<span>").addClass("editor-toolbar-label").text("Organization:"))
//		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.organization))
//	).append(
//		$("<div>").addClass("editor-toolbar-detail")
//		.append($("<span>").addClass("editor-toolbar-label").text("Created:"))
//		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.created))
//	).appendTo(toolbar);	
}

XMLAnnotator.prototype.viewButton = function(buttonView) {
	var self = this;
	var label = "Button";
	if(buttonView.label != undefined) label = buttonView.label;
	
	var button = $("<button>")
	.attr("title", label)
	.addClass("editor-toolbar-button")
	.text(label)
	.button({ icons: { primary: 'editor-toolbar-preview' }}).click(function () {
		self.lastButtonClicked = button;
		self.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var content = $("<div>").appendTo(details).addClass("editor-preview");
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			panel.find(".panel-body").before(details);
			
			self.loadViewButton(content, buttonView.contents);
		}, label);
	});
	
	return button;
}

XMLAnnotator.prototype.initItemsContainer = function() {
	var self = this;
	
	this.items = $("<div>").appendTo(this.container.items).itemBrowser({
		datasetId: this.dataUploadId,
		click: function(item) {
			self.loadItem(item);
		}
	});
}

XMLAnnotator.prototype.initAnnotationsContainer = function() {
	var self = this;
	
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "init",
			dataUploadId: this.dataUploadId,
		},
		success: function(response) {
			this.configuration = response.configuration;
			this.metadata = response.metadata;
			this.configuration = response.configuration;
			
			this.panel.kpanel("setTitle", "Annotator demo");

			this.views = response.views;

			this.initToolbar();
			
			this.preferences = new EditorPreferences({
				element: "mapping-editor-preferences",
				preferences: response.preferences,
				onValueChange: function(source, value) {
					self.savePreferences();
				}
			});

			// bad layout practice ..
			var self = this;
			window.setInterval(function () {
				self.container.layout().resizeAll();
			}, 500);

			this.hideLoading();
		},
		failure: function(response) {
			this.hideLoading();
		}
	});
}

XMLAnnotator.prototype.loadItem = function(item) {
	if(item == undefined) {
		if(this.loadItem == undefined) return;
		item = this.loadedItem;
	} else {
		this.loadedItem = item;
	} 
	
	var self = this;
	this.container.annotations.empty();
	this.showLoading();
	
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "loadItem",
			itemId: item.id,
		},
		success: function(response) {
			this.showMapping(response.template);

			$(self).trigger("documentChanged", response);
			$(self).trigger("afterLoadItem", response);
			this.hideLoading();
		},
		failure: function(response) {
			this.hideLoading();
			$(self).trigger("afterLoadItem", response);
		}
	});
}

XMLAnnotator.prototype.showMapping = function(mapping) {
	if(mapping == undefined) {
		if(this.shownMapping == undefined) return;
		mapping = this.shownMapping;
	}
	this.shownMapping = mapping;
	
	this.container.annotations.empty();
		
	if(this.mode == XMLAnnotator.MODE_COMPLEX) {
		this.showMappingComplex(mapping);
	} else if(this.mode == XMLAnnotator.MODE_SIMPLE) {
		this.showMappingSimple(mapping);
	}
}

XMLAnnotator.prototype.showMappingComplex = function(mapping) {
	var m = $("<div>").appendTo(this.container.annotations).mappingElement({
		target: mapping,
		editor: this,
		schemaMapping: false
	});
	m.mappingElement("toggleChildren");
}

XMLAnnotator.prototype.showMappingSimple = function(mapping) {
//	console.log(this.views);
	if(this.views == undefined || this.views.views == undefined)  this.container.annotations.append(Mint2.message("no views defined for this schema", Mint2.WARNING));
	
	for(var i in this.views.views) {
		var view = $("<div>").mappingView({
			view: this.views.views[i],
			editor: this
		}).appendTo(this.container.annotations);
	}
}

XMLAnnotator.prototype.newItem = function(item) {
	var self = this;
	this.container.annotations.empty();
	this.showLoading();
	
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "newItem"
		},
		success: function(response) {
			this.loadedItem = { id: response.itemId };
			this.showMapping(response.item.template);

			$(self).trigger("afterItemsChange", response);
			$(self).trigger("documentChanged", response);
			$(self).trigger("afterLoadItem", response);
			this.hideLoading();
		},
		
		failure: function(response) {
			this.hideLoading();
			$(self).trigger("afterLoadItem", response);
		}
	});
}

XMLAnnotator.prototype.deleteItem = function(item) {
	var self = this;
	this.showLoading();
	
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "deleteItem"
		},
		success: function(response) {
			this.container.annotations.empty();
			this.loadedItem = undefined;
			console.log("delete", response);
			$(self).trigger("afterItemsChange", response);
			$(self).trigger("documentChanged", response);
			this.hideLoading();
		},
		failure: function(response) {
			this.hideLoading();
			$(self).trigger("afterLoadItem", response);
		}
	});
}

XMLAnnotator.prototype.loadViewButton = function(container, views) {
	var self = this;
	if(container == undefined || container.size() == 0) return;
	
	container.empty();
	
	if(this.loadedItem != undefined) {
		if(views != undefined) {
			for(var i in views) {
				var view = $("<div>").mappingView({
					view: views[i],
					editor: this
				}).appendTo(container);
			}		
		}
	} else {
		container.append(Mint2.message("No item loaded", Mint2.WARNING));
	}
}

XMLAnnotator.prototype.loadPreview = function(previewContainer) {
	var self = this;
	if(previewContainer == undefined || previewContainer.size() == 0) return;
	
	previewContainer.empty();
	
	if(this.loadedItem != undefined) {
		var itemPreview = $("<div>").appendTo(previewContainer);
		itemPreview.itemPreview({
			datasetId: this.dataUploadId,
			showOptions: false,
			compact: true,
			itemId: this.loadedItem.id
		});			
	} else {
		previewContainer.append(Mint2.message("No item loaded", Mint2.WARNING));
	}
}

XMLAnnotator.prototype.documentChanged = function(element) {
	if(this.lastButtonClicked != undefined) this.lastButtonClicked.click();
	this.items.itemBrowser("refresh");
//	this.loadPreview($(".editor-preview"));
}

XMLAnnotator.prototype.afterItemsChange = function(element) {
	this.items.itemBrowser("refresh");
}

XMLAnnotator.prototype.validate = function() {
	$.ajax({
		url: this.ajaxUrl,
		dataType: "json",
		data: {
			command: "getValidationReport"
		},
		success: function(response) {	
//			$(".editor-navigation").xsdmappingNavigation("warnings", response.warnings);
			$(".mapping-element").each(function (k, v) { $(v).mappingElement("validate") })

			for(var i in response.mapped) {
				var e = $("#" + response.mapped[i]);
				if(e.length > 0) e.mappingElement("validate", "ok");
			}

			for(var i in response.missing) {
				var e = $("#" + response.missing[i]);
				if(e.length > 0) e.mappingElement("validate", "error");
			}
			
			for(var i in response.mapped_attributes) {
				var e = $("#" + response.mapped_attributes[i]);
				if(e.length > 0) e.mappingElement("validate", "ok", true);
			}

			for(var i in response.missing_attributes) {
				var e = $("#" + response.missing_attributes[i]);
				if(e.length > 0) e.mappingElement("validate", "error", true);
			}
		}
	});
	
	this.bookmarks();
}