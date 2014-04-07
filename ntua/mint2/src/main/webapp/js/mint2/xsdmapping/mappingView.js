/**
 * Creates an MappingView widget.
 *
 * @constructor
 * @this {MappingView}
 * @param container The widget's container. Can be either a string selector or a jQuery object.
 * @param {object} options The widget's configuration options.
 */
function MappingView(container, options) {
	this.defaults = {
			root: null,				// MappingView that is the root of this view. If root has a target mapping element, its id will be used as root in find queries.
			view: null,				// json object that describes this view
			target: null,			// a mapping element used as the root of this view. If defined, xpath will be ignored.
			metadata: undefined,	// metadata associated with this view's contents. Usually loaded after ajax call that retrieves a target.
			lazy: false				// do not call render() on creation. Use this in order to avoid making ajax calls immediately.
	}
	
	this.settings = $.extend({}, this.defaults, options);
	this.editor = this.settings.editor;
	this.isLoaded = false;
	if(container != undefined) {
		this.container = $(container);
		if(!this.settings.lazy) this.render();
	}
}

MappingView.prototype.lazyLoad = function() {
	if(this.isLoaded) return;
	else this.render();
}

MappingView.prototype.render = function() {
	var self = this;
	var xpath = this.getView().xpath;
	var target = this.getTarget();
	var root = this.getRoot();
	
	var rootId = undefined;
	if(root != null) {
		if(root.getTarget() != null) {
			rootId = root.getTarget().id;
		}
	}
	
	this.container.empty().addClass("editor-mapping-view");
	var groupAnn = this.getView().groupAnn;
	var type = this.getView().type;
	var place = this.getView().place;
	
	if (this.editor.annotatorMode == XMLAnnotator.MODE_GROUP) {
		/*if (groupAnn == undefined) {
			self.container.append(Mint2.message($("<span>No group annotator support</span>"), Mint2.WARNING));
		}*/
		if (groupAnn != undefined) {// && itemsQuery != undefined) {
			var tabs = $("<div>");
			var ul = $("<ul>");
			for (var i in groupAnn) {
				var element = groupAnn[i];	
				xpath = element.xpath;
				var label = element.label;
				if (xpath != undefined && label != undefined) {
					var viewElement = $("<div>").annotatorElement({
						xpath: xpath,
						editor: this.editor,
						label: label
					});
					this.container.append(viewElement);
				}	
			}
		}
	}
	else if (this.editor.annotatorMode == XMLAnnotator.MODE_SINGLE) {	
		if(target == null && xpath != undefined) {
			this.container.append(Mint2.loading());
	
			this.editor.ajax.find({
				xpath: xpath,
				root: rootId,
				metadata: this.getMetadataXPaths()
			}, function(response) {
				var id = response.id;
				self.container.empty();

				if(response.results != undefined && response.results.length > 0) {
					for(var i in response.results) {
						var target = response.results[i].target;
						var element = $("<div>").mappingView({
							editor: self.settings.editor,
							root: self,
							view: self.getView(),
							metadata: response.results[i].metadata,
							target: target
						});
						self.container.append(element);
					}
				} else {
					self.container.append(Mint2.message($("<span>No results found for xpath '<b>" + xpath + "</b>'</span>"), Mint2.WARNING));
				}
			})
		} else {
			if(type == undefined) {
				if(target != null) {
					var element = this.getMappingElement(target);
					this.container.append(element);				
				} else {
					this.container.append(Mint2.message($("<span>No results found for xpath '<b>" + xpath + "</b>'</span>"), Mint2.WARNING));
				}
			} else if (type == "tabs") {
				if (this.getView().contents != undefined) {
					var tabs = $("<div>");
					var ul = $("<ul>").appendTo(tabs);
	
					for (var c in this.getView().contents) {
						var item = this.getView().contents[c];
						var view = $("<div>").mappingView({
							editor: self.settings.editor,
							root: self,
							view: item,
							lazy: true
						});
						
						tabs.append($("<div>").attr("id", "view-" + c).append(view));
						ul.append($("<li>").append($("<a>").attr("href", "#view-" + c).append(view.data("mappingView").getLabel())));
					}
	
					this.container.append(tabs);
					tabs.tabs({
						// tab views are lazy loaded to avoid all ajax calls issued at once.
						show: function(event, ui) {
							$(ui.panel).find("div").each(function(k, v) {
								var tabView = $(v).data("mappingView");
								if(tabView != undefined) {
									tabView.lazyLoad();
								}
							});
						},
					});
				} else {
					this.container.append(Mint2.message("View with label '<b>" + label + "</b>' does not have contents", Mint2.ERROR));			
				}
			} else if (type == "collection") {
				var label = this.getLabel();
				if(this.getView().contents != undefined) {
					for(var c in this.getView().contents) {
						var item = this.getView().contents[c];
						var view = $("<div>").mappingView({
							editor: self.settings.editor,
							root: self,
							view: item
						});
						this.container.append(view);
					}
				} else {
					this.container.append(Mint2.message("View with label '<b>" + label + "</b>' does not have contents", Mint2.ERROR));			
				}
			} else if (type == "image") {
				var label = this.getLabel();
				if (this.getView().xpath != undefined) {
					if(target["mapping-cases"] != undefined) {
						console.log("image item:", target);
						for(var i in target["mapping-cases"]) {
							var value = target["mapping-cases"][i].mappings[0].value;
							var view = $("<div>").addClass("editor-mapping-view-image");
							view.append($("<img>").attr("src", value));
							this.container.append(view);					
						}
					}
				} else {
					this.container.append(Mint2.message("View with label '<b>" + label + "</b>' does not have xpath", Mint2.ERROR));			
				}			
			} else if (type == "table") {
				var label = this.getLabel();		
				if(this.getView().contents != undefined) {
					var array = [];
					for(var c in this.getView().contents) {
						var item = this.getView().contents[c];
						var view = $("<div>").mappingView({
							editor: this.settings.editor,
							root: this,
							view: item,
							lazy: true
						});
						array.push(view);
					}
	
					var dataTable = Mint2.dataTable({
						title: label,
						data: array,
						collapsed: true,
						expand: function() {
							console.log(self);
							dataTable.find("div").each(function(k, v) {
								var tableView = $(v).data("mappingView");
								if(tableView != undefined) {
									tableView.lazyLoad();
								}
							});
						}
					});
	
					this.container.append(dataTable);
				} else {
					this.container.append(Mint2.message("View with label '<b>" + label + "</b>' does not have contents", Mint2.ERROR));			
				}	
			} else {
				this.container.append(Mint2.message("No view defined for type '<b>" + type + "</b>'", Mint2.ERROR));
			}		
		}
	}
	this.isLoaded = true;
}
Mint2.jQueryPlugin("mappingView", MappingView);

MappingView.prototype.getMappingElement = function(target) {
	var self = this;
	var label = $("<div>").append(this.getLabel());
	var element = $("<div>").mappingElement({
		target: target,
		editor: this.editor,
		preferedInput: this.getView().input,
		schemaMapping: false,
		showBookmarks: false,
		showChildrenToggle: false,
		showAttributesToggle: false,
		showValidation: false,
		//showRemove: "always",
		onDuplicate: function(response) {
			if(self.getRoot() != undefined) self.getRoot().render();
			//$(self.editor).trigger("documentChanged", response);
		},
		onRemove: function(response) {
			if(self.getRoot() != undefined) self.getRoot().render();
			$(self.editor).trigger("documentChanged", response);
		},
		label: label
	});
	
	this.editor.ajax.find({
		xpath: "@xml:lang",
		root: target.id,
	}, function(response) {
		if(response.results != undefined && response.results.length > 0) {
			var header = element.find(".mapping-element-header");
			header.find(".mapping-element-label").append($("<div>").mappingLanguageView({
				editor: self.editor,
				target: response.results[0].target
			}));
			header.css("min-height", "50px");
		}
	})
	
	return element;
}

MappingView.prototype.getView = function() {
	return this.settings.view;
}

MappingView.prototype.getTarget = function() {
	return this.settings.target;
}

MappingView.prototype.setTarget = function(target) {
	this.settings.target = target;
}

MappingView.prototype.getRoot = function() {
	return this.settings.root;
}

MappingView.prototype.getLabel = function() {
	var result = $("<span>");
	
	var label = "View";
	if(this.settings.view != undefined) {
		label = this.getView().label;
	}
	result.append($("<span>").text(label));
	
	var metadata = this.getMetadata();
	if(metadata != undefined) {
		for(var i in metadata) {
			var metadataItem = $("<span>").css({
				"margin-left": "5px"
			}).addClass("mapping-element-label-small").text(metadata[i]);
			result.append(metadataItem);			
		}
	}
	
	return result;
}

MappingView.prototype.getMetadata = function() {
	return this.settings.metadata;
}

MappingView.prototype.getMetadataXPaths = function() {
	var result = undefined;
	
	if(this.getView() != undefined && this.getView().metadata != undefined) {
		result = {};
		var metadata = this.getView().metadata;
		for(var i in metadata) {
			var item = metadata[i];
			result[i] = item.xpath;
		}
	}
	
	return result;
}

/**
 * Creates a special MappingLanguageView widget that handles mappings on @xml:lang attributes.
 *
 * @constructor
 * @this {MappingLanguageView}
 * @param container The widget's container. Can be either a string selector or a jQuery object.
 * @param {object} options The widget's configuration options.
 */
function MappingLanguageView(container, options) {
	this.defaults = {
		root: null,			// MappingView that is the root of this view. If root has a target mapping element, it's id will be used as root in find queries.
		target: null		// a mapping element used as the root of this view. If defined, xpath will be ignored.
	}
	
	this.settings = $.extend({}, this.defaults, options);
	this.editor = this.settings.editor;
	
	if(container != undefined) {
		this.container = $(container);
		this.render();
	}	
}

// inherit from MappingView
MappingLanguageView.prototype = new MappingView();
MappingLanguageView.prototype.constructor = MappingLanguageView;

MappingLanguageView.prototype.render = function() {
	var self = this;
	var target = this.getTarget();
	var root = this.getRoot();
	
	var rootId = undefined;
	if(root != null) {
		if(root.getTarget() != null) {
			rootId = root.getTarget().id;
		}
	}
	
	this.container.empty().addClass("editor-mapping-view");
	
	this.container.append($("<span>").addClass("mapping-element-label-small").text("Language: "));
	
	var selected = undefined;
	try {
		selected = target["mapping-cases"][0].mappings[0].value;
	} catch(e) {
	}

	var select = Mint2.languageSelection({
		selected: selected,
		onChange: function(lang) {
			if(lang == "") {
				self.editor.ajax.removeMapping({
					id: target.id,
					index: 0,
					case: 0
				}, function(response) {
					self.setTarget(response);
					self.render();
					$(self.editor).trigger("documentChanged", response);
				});				
			} else {
				self.editor.ajax.setConstantValueMapping({
					id: target.id,
					index: 0,
					case: 0,
					value: lang
				}, function(response) {
					self.setTarget(response);
					self.render();
					$(self.editor).trigger("documentChanged", response);
				});				
			}
		}
	});
	this.container.append($("<span>").append(select));
	select.chosen({ width: "100px" });
	
}

Mint2.jQueryPlugin("mappingLanguageView", MappingLanguageView);