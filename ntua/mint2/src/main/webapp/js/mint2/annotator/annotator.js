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

XMLAnnotator.MODE_SINGLE = 0;
XMLAnnotator.MODE_GROUP = 1;
XMLAnnotator.MODE_ANNOTATOR_DEFAULT = XMLAnnotator.MODE_SINGLE;

this.tree = [];

var groupActions = function() {
		var self=this;
		var actions=[];
    	actions.push = function(){
			Array.prototype.push.apply(this, arguments);
			XMLAnnotator.prototype.loadActionsPreview($(".editor-preview"));
			return true;	
		}
		
 	    actions.pop = function(){
		   return Array.prototype.pop.apply(this,arguments);		
	    }
	return actions;
}

this.groupActs = new groupActions;

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

XMLAnnotator.prototype.init = function(dataUploadId, hideGroupAnnotator) {
	var self = this;
	this.dataUploadId = dataUploadId;
	this.hideGroupAnnotator = hideGroupAnnotator;
	this.mode = XMLAnnotator.MODE_DEFAULT;
	this.annotatorMode = XMLAnnotator.MODE_ANNOTATOR_DEFAULT;
    this.panel.kpanel("setTitle", "Loading...");
    
	this.showLoading();
	
	//this.initItemsContainer();
	this.initAnnotationsContainer();
	//groupActs.length=0;
}

XMLAnnotator.prototype.initToolbar = function(views, template) {
	var toolbar =  this.container.toolbar;
	var self = this;
	

	this.showAnnotatorMode(views, template);
	
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
	
	//this.modeToolbar();
	this.container.items.empty();
	this.items = $("<div>").appendTo(this.container.items).itemBrowser({
		datasetId: this.dataUploadId,
		click: function(item) {
			self.loadItem(item);
		},
		annotatorMode: self.annotatorMode
	});
}

XMLAnnotator.prototype.getSchemaTree = function(views, template) {
	var self = this;
	$.ajax({
		url: "Tree",
		context: this,
		data: {
			dataUploadId: this.dataUploadId,
			groupAnnotator: this.annotatorMode
		},
		success: function(response) {
			if(response != undefined) {
				if(response.error != undefined) {
					alert(response.error);
				} else if(response) {
					tree = response.tree;
					this.initToolbar(views, template);
				}				
			} else {
				alert("Could not retrieve tree data");
			}
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
			dataUploadId: this.dataUploadId
		},
		success: function(response) {
			this.configuration = response.configuration;
			this.metadata = response.metadata;
			var template = response.template;
			this.panel.kpanel("setTitle", "Annotator demo");

			this.views = response.views;
			
			this.getSchemaTree(this.views, template);
			
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
			$K.kaiten('maximize', this.panel);
			
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
	this.showLoading();
	
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: {
			command: "loadItem",
			itemId: item.id
		},
		success: function(response) {
			this.showMapping(response.template);
			//$(self).trigger("documentChanged", response);
			//$(self).trigger("afterLoadItem", response);
			this.hideLoading();
		},
		failure: function(response) {
			this.hideLoading();
			//$(self).trigger("afterItemsChange", response);
		}
	});
}

XMLAnnotator.prototype.showAnnotatorMode = function(views, template) {
	var self=this;
	this.container.annotations.empty();
	if (this.annotatorMode == XMLAnnotator.MODE_SINGLE) {
		this.initItemsContainer();
		this.showSingleAnnotator(views, template);
		this.closeSubpanel();
	} else if (this.annotatorMode == XMLAnnotator.MODE_GROUP) {
		this.initItemsContainer();
		this.showGroupAnnotator(views, template);
		this.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var content = $("<div>").appendTo(details).addClass("editor-preview");
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			panel.find(".panel-body").before(details);
			self.loadActionsPreview(content);
		}, "Group Actions Preview", {width: "30%"});
		
	}
}

XMLAnnotator.prototype.showSingleAnnotator = function(views, template) {
	var toolbar = this.container.toolbar;
	toolbar.empty();
	if (!this.hideGroupAnnotator) {
	   toolbar.append(this.annotatorModeButton(views, template)); 
	}
	toolbar.append(this.singleAnnotatorButtons(views));
	this.metadata = {};
	$("<div>").addClass("editor-toolbar-section").append(
		$("<span>").addClass("editor-toolbar-name").text(this.metadata.name)
	).append(
		$("<div>").addClass("editor-toolbar-detail")
		.append($("<span>").addClass("editor-toolbar-info").text(this.metadata.schema))
	).appendTo(toolbar);
}
	

XMLAnnotator.prototype.showGroupAnnotator = function(views, template) {
	var toolbar = this.container.toolbar;
	toolbar.empty();
	toolbar.append(this.annotatorModeButton(views, template));
	toolbar.append(this.groupAnnotatorButtons(views));
	//this.groupAnnotatorButtons(views);
}

XMLAnnotator.prototype.singleAnnotatorButtons = function(views) {
	var self = this;
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
	
	if (views != undefined) {
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
		if (views.buttons != undefined) {
			for(var i in views.buttons) {
				var button = this.views.buttons[i];
				var label = button.label;
				var btnButton = this.viewButton(button).appendTo(buttons);
			}
		}
	}
	else {
		self.mode = XMLAnnotator.MODE_COMPLEX;
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

	return buttons;
}

XMLAnnotator.prototype.annotatorModeButton = function(views, template) {
	var self = this;
	var text = "";
	if(self.annotatorMode == XMLAnnotator.MODE_SINGLE) {
       text = "Switch to Group Item Annotator";
       groupActs.length=0;
	}
    else 
    	text = "Switch to Single Annotator";		
	var btnAnnot = $("<button>")
	.addClass("editor-toolbar-button")
	.text(text)
	.attr("title", "Switch mode between single and group annotator")
	.button({ icons: { primary: 'editor-toolbar-preview' }})
	.click(function () {
		if(self.annotatorMode == XMLAnnotator.MODE_SINGLE) {
			self.annotatorMode = XMLAnnotator.MODE_GROUP;
		} else {
			self.annotatorMode = XMLAnnotator.MODE_SINGLE;		
		}
		self.showAnnotatorMode(views, template);
	});
	return btnAnnot;
}

XMLAnnotator.prototype.groupAnnotatorButtons = function(views) {
	var self = this;
	var buttons = $("<div>").addClass("editor-toolbar");
	var itemsQuery = self.items.itemBrowser("getQuery");
	/*if (views != undefined) {
		for(var i in this.views.views) {
			var view = $("<div>").mappingView({
				view: this.views.views[i],
				editor: this
			}).appendTo(this.container.annotations);
		}
	}
	else {
		this.container.annotations.append(Mint2.message("no views defined for this schema", Mint2.WARNING));
	*/
	var m = $("<div>").appendTo(this.container.annotations).annotatorElement({
		target: tree,
		editor: this,
		schemaMapping: false
	});
	var btnActionsPreview = $("<button>").addClass("editor-toolbar-button").text("Group Actions Preview").attr("title", "Group Actions Preview").button({ icons: { primary: 'editor-toolbar-preview' }}).click(function () {
				self.lastButtonClicked = btnActionsPreview;
				self.loadSubpanel(function(panel) {
					var details = $("<div>").css("padding", "10px");
					var content = $("<div>").appendTo(details).addClass("editor-preview");
					var body = panel.find(".panel-body");
					details.css("height", body.height() - 20);
					details.css("overflow", "auto");
					panel.find(".panel-body").before(details);
					
					self.loadActionsPreview(content);
				}, "Group Actions Preview");
			}).appendTo(buttons);
	return buttons;		
}

XMLAnnotator.prototype.showMapping = function(mapping) {
	var self = this;
	if(mapping == undefined) {
		if(this.shownMapping == undefined) return;
		mapping = this.shownMapping;
	}
	this.shownMapping = mapping;
	
	if (this.annotatorMode == XMLAnnotator.MODE_SINGLE) {
		this.container.annotations.empty();
		if (this.mode == XMLAnnotator.MODE_COMPLEX) {
			self.showMappingComplex(mapping);
		} else if(this.mode == XMLAnnotator.MODE_SIMPLE) {
			self.showMappingSimple(mapping);
		}
	}
	else {
		this.loadSubpanel(function(panel) {
			var details = $("<div>").css("padding", "10px");
			var content = $("<div>").appendTo(details).addClass("editor-preview");
			var body = panel.find(".panel-body");
			details.css("height", body.height() - 20);
			details.css("overflow", "auto");
			panel.find(".panel-body").before(details);
			self.loadPreview(content);
		}, "Preview");
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
	if(this.views == undefined || this.views.views == undefined)  
		this.container.annotations.append(Mint2.message("no views defined for this schema", Mint2.WARNING));
	
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
			//$(self).trigger("documentChanged", response);
			//$(self).trigger("afterLoadItem", response);
			this.hideLoading();
		},
		
		failure: function(response) {
			this.hideLoading();
			//$(self).trigger("afterLoadItem", response);
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
			$(self).trigger("afterItemsChange", response);
			//$(self).trigger("documentChanged", response);
			this.hideLoading();
		},
		failure: function(response) {
			this.hideLoading();
			//$(self).trigger("afterLoadItem", response);
		}
	});
}

XMLAnnotator.prototype.loadViewButton = function(container, views) {
	var self = this;
	if (container == undefined || container.size() == 0) return;
	
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

XMLAnnotator.prototype.loadActionsPreview = function(previewActionsContainer) {
		var self = this;
		if(previewActionsContainer == undefined || previewActionsContainer.size() == 0) return;
		
		previewActionsContainer.empty();
		
		var actionsPreview = $("<div>").appendTo(previewActionsContainer);
		actionsPreview.html("<h1>Group Annotator: Actions log</h1>");
		var lastIsSearch = false;
		var wrapperdiv = "";
		for(var i=0;i<groupActs.length;i++){
			if (!(lastIsSearch && groupActs[i].type=="select")) {
				wrapperdiv=$("<div>").addClass("ui-state-highlight mint-message ui-corner-all");
				var icon = $("<div>").prop('title', 'Remove selected action').addClass("group-annotator-remove")
					.attr('style','cursor:pointer').attr('id','group-remove-'+i);
				wrapperdiv.append(icon);
				var buildhtml="<span><b>"+" "+groupActs[i].type+" </b>";
			}
			else {
				var buildhtml="<b>"+ " / select " +"</b>";
				lastIsSearch = false;
			}
			if(groupActs[i].type=="search"){
				buildhtml+=" for ";
				if(groupActs[i].arguments.query!=""){
					buildhtml+=	groupActs[i].arguments.query;
				}
				else{ 
					buildhtml+=" all in dataset ";}
				if(groupActs[i].arguments.queryType=="all"){
					buildhtml+=	" in everything";
				}
				else if(groupActs[i].arguments.queryType=="custom"){
					buildhtml+=	" using solr query";}
				else if(groupActs[i].arguments.queryType=="label"){
					buildhtml+=	" in labels";}
				lastIsSearch = true;
			}
			else if(groupActs[i].type=="select"){
				var val="no";
				if (groupActs[i].arguments.length>0)
					val=groupActs[i].arguments[0];
				buildhtml+=val+" items";	
    		}
			else if(groupActs[i].type=="editValue"){
				buildhtml+="xpath:"+groupActs[i].xpath+" <br/>set value to:"+groupActs[i].arguments[0];
			}
			//TODO: add other function types from annotatorFunction
			else if (groupActs[i].type=="duplicate") {
				buildhtml+="xpath:"+groupActs[i].xpath+" <br/>douplicate with value::"+groupActs[i].arguments[0];
			}
			else if(groupActs[i].type=="remove") {
				if (groupActs[i].arguments[0] != "*")
					buildhtml+="xpath:"+groupActs[i].xpath+
							" <br/>with value equal to: "+groupActs[i].arguments[0];
				else
					buildhtml+="xpath:"+groupActs[i].xpath;
			}
			else if (groupActs[i].type=="editValueIf") {
				buildhtml+="xpath:"+groupActs[i].xpath+" <br/> set value to: " + groupActs[i].arguments[1] +
				" if current value = " + groupActs[i].arguments[0];
			}
			else if (groupActs[i].type=="substringToUpperCase" || groupActs[i].type=="substringToLowerCase") {
				buildhtml+="xpath:"+groupActs[i].xpath+" <br/> substring: " + groupActs[i].arguments[0] +
				" will be set to " + groupActs[i].type.split("To")[1];
			}
			wrapperdiv.append(buildhtml);
			actionsPreview.append(wrapperdiv);
		}
		if(groupActs.length>0){
			var btnspan=$("<span>").attr("style","padding:20px;align:center");
			var btnClear = $("<button>")
			.attr("title", "Clear all")
			.text("Clear all")
			.click(function () {
				groupActs.length=0;
				XMLAnnotator.prototype.loadActionsPreview($(".editor-preview"));
			}).appendTo(btnspan);
			
			
			var btnCommit = $("<button>")
			.attr("title", "Commit all").attr("style","margin-left:10px;")
			.text("Commit all")
			.click(function () {
				//TODO: self.ajaxUrl is sometimes Annotator_ajax and sometimes mappingAjax
				//when called from push of groupActs, it's mappingAjax
				XMLAnnotator.prototype.groupAnnotate(groupActs);				
				
				//manage panels
				var panelcount=$('div[id^="kp"]:last');
		    	var panelid=panelcount.attr('id');
		    	var pnum=parseInt(panelid.substring(2,panelid.length));
		    	var startpanel=$("#kp1");
		    	$K.kaiten('slideTo',startpanel);
		    	if(pnum>3){
		    		var newpanel=$("#kp2");
		    		$K.kaiten('removeChildren',newpanel, false);
		    		
		    	   $K.kaiten('reload',newpanel,{kConnector:'html.page', url:'ImportSummary.action?orgId='+ooid+'&userId='+urid, kTitle:'My workspace' });

		    	}else{
		    		  $K.kaiten('reload',startpanel,{kConnector:'html.page', url:'ImportSummary.action?orgId='+window.opener.ooid+'&userId='+window.opener.urid, kTitle:'My workspace' });
		    		}
			}).appendTo(btnspan);
			previewActionsContainer.append(btnspan);
		}
}

$("div.group-annotator-remove").live('click', function(e) {
	if($(this).attr('id')!=undefined){
		gid= parseInt($(this).attr('id').replace('group-remove-',''));
		if (groupActs[gid].type == "search" && groupActs[gid+1].type == "select") {
			groupActs.splice(gid,2);
		}
		else 
			groupActs.splice(gid,1);
		XMLAnnotator.prototype.loadActionsPreview($(".editor-preview"));
	}
	
});

XMLAnnotator.prototype.groupAnnotate = function(groupActs) {
	$.ajax({
		url: "Annotator_ajax",
		context: this,
		dataType: "json",
		data: {
			command: "groupAnnotate",
			groupActs: JSON.stringify(groupActs)
		},
		success: function(response) {
			//this.editor.hideLoading();
		},
		
		failure: function(response) {
			//this.editor.hideLoading();
			alert(JSON.stringify(response));
		}
	});
}

XMLAnnotator.prototype.documentChanged = function(element) {
	if(this.lastButtonClicked != undefined) this.lastButtonClicked.click();
	this.items.itemBrowser("refresh");
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

