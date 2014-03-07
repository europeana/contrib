(function($) {
	var widget = "mappingElement";
	var defaults = {
		'ajaxUrl': 'mappingAjax',
		'schemaMapping': true,
		'showBookmarks': true,
		'showChildrenToggle': true,
		'showAttributesToggle': true,
		'showValidation': true,
		'showDuplicate': true,
		'showRemove': "duplicate",
		'preferedInput': undefined,
		'onDuplicate': function(response, settings) {
			var original = response.original;
			var newItem = response.duplicate;
			if(original != undefined && newItem != undefined) {
				var node = $("<div>").mappingElement($.extend({}, settings, {target: newItem}));
				node.insertBefore($("#" + original));
				node.hide().fadeIn();
				data.editor.validate();
			}
		},
		'onRemove': function(response) {
			var id = response.id;
			if(id != undefined) {
				$("#" + id).slideUp(250, function() {
					$("#" + id).remove();
				});
			}
			
			removeDuplicate.toggleClass("mapping-element-loading");
			data.editor.validate();
		},
		'label': null
	};
	
	var xpathsInMappings = [];
	/**
	 * @class mappingElement
	 * @name mappingElement
	 * @memberOf XSDMappingEditor
	 * @description
	 * jQuery plugin that handles a single mapping element.
	 * Mapping element's consists of three subelements:
	 * <ul>
	 * 	<li>Header element that contains element's name, controls and mapping area.</li>
	 *  <li>Children element that contains element's children.</li>
	 *  <li>Attributes element that contains element's attributes.</li>
	 * </ul>
	 */
	$.fn.mappingElement = function(method) {
	    if ( methods[method] ) {
	        return methods[ method ].apply(this, Array.prototype.slice.call(arguments, 1));
	      } else if ( typeof method == 'object' || ! method ) {
	        return methods.init.apply(this, arguments);
	      } else {
	        $.error( 'Method ' +  method + ' does not exist on ' + widget );
	      }   
	};
	
	function existsInArray(jsonArray, value) {
	    for (var i = 0; jsonArray.length > i; i += 1) {
	        if (jsonArray[i] == value) 
	            return true;
	    }
	    return false;
	}
	
	function render(container) {
		var data = container.data(widget);
		var target = data.target;
		/** Belongs to an XSD Mapping editor - enables schema mappings on top of value edits */
		var isSchemaMapping = (data.settings.schemaMapping == true);
		/** Structural elements must have children and cannot receive constant mappings. */
		var isStructural = (target.children != undefined && target.children.length > 0);
		
		container
		.empty()
		.attr("id", target.id)
		.addClass("mapping-element");
		
		if(target == undefined) {
			container.text("Mapping target not defined");
		} else {
//			console.log(data.editor);
			var hasSchema = data.editor.configuration.schemaId != undefined; 
			var header = $("<div>").addClass("mapping-element-header");
			// label
			var label = $("<div>").addClass("mapping-element-label").appendTo(header);
			// children toggle
			if(data.settings.showChildrenToggle) {
				var childrenToggle = $("<div>").appendTo(label);
				if(target.children == undefined || target.children.length == 0) {
					childrenToggle.addClass("mapping-element-button-empty");
				} else {
					childrenToggle
					.addClass("mapping-element-children-toggle")
					.attr("title", "Toggle children")
					.click(function() {
						container.mappingElement("toggleChildren");
					});
				}
			}
			// attributes toggle
			if(data.settings.showAttributesToggle) {
				var attributesToggle = $("<div>").appendTo(label);
				if(target.attributes == undefined || target.attributes.length == 0) {
					attributesToggle.addClass("mapping-element-button-empty");
				} else {
					attributesToggle
					.addClass("mapping-element-attributes-toggle")
					.attr("title", "Toggle attributes")
					.click(function() {
						container.mappingElement("toggleAttributes");
					});
				}
			}
			if(data.settings.showValidation) {
				var validation = $("<div>").addClass("mapping-element-validation").appendTo(label);
			}
			
			// bookmark toggle
			if(data.settings.showBookmarks) {
				var bookmarkToggle = $("<div>").appendTo(label);
				bookmarkToggle.addClass("mapping-element-bookmark").attr("title", "Add bookmark");
				bookmarkToggle.click(function() {
					var title = (target.prefix == undefined)?"":target.prefix+":";
					if(target.name.indexOf("@") == 0) {
						title = "@" + title + target.name.substr(1, target.name.length - 1); 
					} else {
						title = title + target.name;
					}
									
					if($(this).attr("class").indexOf("mapping-element-bookmark-off") > -1) {
						data.editor.addBookmark(target.id, title);
					} else {
						data.editor.removeBookmark(target.id);
					}
				});
			}

			// prefix:name
			var name = $("<div>").addClass("mapping-element-element").appendTo(label);
			
			if(data.settings.label) {
				name.append(data.settings.label);
			} else {
				var title = (target.label || target.name);
				
				if(title.indexOf("@") == 0 ) {
					name.append($("<span>").addClass("mapping-element-name").text("@"));
					title = title.substring(1, title.length);
				}
				
				if(target.prefix != undefined) {
					name.append($("<span>").addClass("mapping-element-prefix").html(target.prefix + "<span>:</span>"))
				}

				name.append($("<span>").addClass("mapping-element-name").text(title));
			}
			
			name.click(function() {
				console.log("Clicked on target " + target.name + ": ", target);
				if(!isStructural && mappingsStructural != undefined) mappingsStructural.slideToggle();
			});

			// right side actions
			var right_actions = $("<div>").addClass("mapping-element-right-actions").appendTo(header);
			if(target.fixed == undefined) {
				if(data.settings.showDuplicate) {
					//alert("maxOccurs " +  target.maxOccurs);
					//if (target.maxOccurs != undefined && target.maxOccurs != 1) {	
					if(data.settings.showRemove == "always" ||
						(data.settings.showRemove == "duplicate" && target.duplicate != undefined)) {
							var removeDuplicate = $("<div>").attr("title", "Remove duplicate element").addClass("mapping-action-remove-duplicate").appendTo(right_actions);
							removeDuplicate.click(function() {
								removeDuplicate.toggleClass("mapping-element-loading");
								data.editor.ajax.removeNode(target.id, data.settings.onRemove);
							});
					}
					if ((target.maxOccurs == undefined || target.maxOccurs != 1) && 
							target.name.indexOf("@") < 0) {
						var duplicate = $("<div>").
							attr("title", "Duplicate element").
							addClass("mapping-action-duplicate").
							appendTo(right_actions);
							duplicate.click(function() {
								duplicate.toggleClass("mapping-element-loading");
								data.editor.ajax.duplicateNode(target.id, data.settings, data.settings.onDuplicate);
								duplicate.toggleClass("mapping-element-loading");
						});
					}
				}
			}
			if(hasSchema) {
				// documentation
				var documentation = $("<div>").attr("title", "Element documentation").addClass("mapping-action-info").appendTo(right_actions);
				documentation.click(function() {
					var self_data = data;
					var element = data.target.name;
					if(data.target.prefix != undefined) {
						if(element.indexOf("@") == 0) {
							element = "@" + data.target.prefix + ":" + element.substring(1, element.length);												
						} else {
							element = data.target.prefix + ":" + element;						
						}
					}
					
					$.get('SchemaDocumentation', {
						schemaId: self_data.editor.configuration.schemaId,
						element: element
					}, function(response) {
						var doc = $("<div>").css({ padding: "10px", "font-size":"150%" });
						resp = response;
						
						name.clone().css({
							width: "100%",
							"padding-bottom": "10px",
							"padding-top": "10px",
							"font-size": "150%",
							"font-weight": "bold",
							"text-align": "center"
						}).appendTo(doc);
						$("<hr>").appendTo(doc);
	
						if(response.documentation != undefined) {
							var lines = response.documentation.split("\n");
							for(i in lines) {
								$("<div>").css({ "padding-bottom": "10px" }).text(lines[i]).appendTo(doc);
							}
						} else {
							if(response.error != undefined) {
								$("<div>").text(response.error).appendTo(doc);
							}
						}
						
						// check if a panel is already open
						self_data.editor.loadSubpanel({
							kConnector:'html.string',
							kTitle:'Documentation',
							html: doc
						}, "Documentation", { reference: header });					
					});					
				});
			}
			
			// mapping areas
			var mappingAreas = $("<div>")
			.addClass("mapping-element-mapping-areas")
			.appendTo(header)
			
			if(isSchemaMapping) {
				var s = data.target["structural"];
				if (s!= undefined) {
					var m = s.mappings;
					for (var j in m) {
						if (m[j].type == "xpath" && !existsInArray(xpathsInMappings, m[j].value))
							xpathsInMappings.push(m[j].value);
					}
				}
				var mappingsStructural = $("<div>")
				.mappingArea($.extend({}, data.settings, {
					element: container,
					structural: true
				}))
				.appendTo(mappingAreas);
			}
			
			if(!isStructural) {
				if(mappingsStructural != undefined) mappingsStructural.hide();

				var casesLength = data.target["mapping-cases"].length;
				
				if(casesLength == 0) {
					var mappings = $("<div>")
					.mappingArea($.extend({}, data.settings, {
						element: container,
						structural: false
					}))
					.appendTo(mappingAreas);					
				} else {
					for(var i in data.target["mapping-cases"]) {
						var aCase = data.target["mapping-cases"][i];
						for (var j in aCase.mappings) {
							var m = aCase.mappings[j];
							if (m.type == "xpath" && !existsInArray(xpathsInMappings, m.value))
								xpathsInMappings.push(m.value);
						}
						var mappings = $("<div>")
						.mappingArea($.extend({}, data.settings, {
							element: container,
							showRemoveMappingCase: (casesLength > 1),
							structural: false,
							case: i,
						}))
						.appendTo(mappingAreas);						
					}
				}
			}
			// children
			var children = $("<div>").addClass("mapping-element-children");
			children.hide();
			
			// attributes
			var attributes = $("<div>").addClass("mapping-element-attributes");
			attributes.hide();

			container.append(header);
			container.append(attributes);
			container.append(children);
			
			data.header = header;
			data.children = children;
			data.attributes = attributes;
		}
	}
	
	var methods = {
		init: function(options) {
			options = $.extend({}, defaults, options);
			if(options.editor != undefined) {
				options.ajaxUrl = options.editor.ajaxUrl;
			}
			
			this.data(widget, {
				settings: options,
				container: this,

				target: options.target,
				editor: options.editor
			});

			render(this);
			
			return this;
		},
		
		destroy: function() {
			this.removeData(widget);
		},
		
		settings: function() {
			return this.data(widget);
		},

		refresh: function(target) {
			var data = this.data(widget);
			
			if(target != undefined) {
				data.target = data.settings.target = target;
				this.mappingElement(data.settings);
				this.trigger("afterRefresh");
			}			
		},
		
		/**
		 * toggles visibility of children element.
		 */
		toggleChildren: function() {
			var data = this.data(widget);
			if(data.children.is(":visible")) {
				data.children.slideUp();
				//data.attributes.slideUp();
				var toggle = this.children(".mapping-element-header").find(".mapping-element-children-toggle-open");
				toggle.removeClass("mapping-element-children-toggle-open").addClass("mapping-element-children-toggle");
			} else {
				var toggle = this.children(".mapping-element-header").find(".mapping-element-children-toggle");
				toggle.removeClass("mapping-element-children-toggle").addClass("mapping-element-children-toggle-open");

				if(data.children.is(":empty")) {
					var self = this;
					toggle.toggleClass("mapping-element-loading");
					
					setTimeout(function () {
						self.mappingElement("populateChildren");
						data.children.slideDown();	
						toggle.toggleClass("mapping-element-loading");
					}, 2);
				} else {
					data.children.slideDown();					
				}

			}
		},
		
		populateChildren: function() {
			var data = this.data(widget);
			var target = data.target;
			var include = data.settings.include;
			var hide = data.settings.hide;
			
			for(i in target.children) {
				var childTarget = target.children[i];
				var showChild = (childTarget.hidden == undefined);
				
				//console.log(include, childTarget);

				if(include != undefined && include != null) {
					showChild = false;
					for(j in include) {
						var np = stringToNameAndPrefix(include[j]);
						if(elementNameAndPrefixEqual(childTarget, np)) {
							showChild = true;
							break;
						}
					}
				}
				
				if(hide != undefined && hide != null) {
					for(j in hide) {
						var np = stringToNameAndPrefix(hide[j]);
						if(elementNameAndPrefixEqual(childTarget, np)) {
							showChild = false;
							break;
						}
					}
				}
				
				if(showChild) {
					var child = $("<div>").mappingElement($.extend({}, data.settings, {
						target: target.children[i],
						include: null,
						hide: null
					}));
					data.children.append(child);
				}
			}
			
			data.editor.validate();
		},
		
		/**
		 * toggles visibility of attributes element.
		 * @memberOf toggleAttributes
		 */
		toggleAttributes: function() {
			var data = this.data(widget);
			if(data.attributes.is(":visible")) {
				data.attributes.slideUp();
			} else {
				if(data.attributes.is(":empty")) {
					this.mappingElement("populateAttributes");
				}

				data.attributes.slideDown();
			}
		},
		
		populateAttributes: function() {
			var data = this.data(widget);
			var target = data.target;

			for(i in target.attributes) {
				var attribute = $("<div>").mappingElement($.extend({}, data.settings, {
					target: target.attributes[i]
				}));
				data.attributes.append(attribute);
			}
			
			data.editor.validate();
		},
		
		validate: function(value, forAttributes) {
			var data = this.data(widget);
			var validation = data.header.find(".mapping-element-validation");
			var attributes = data.header.find(".mapping-element-attributes-toggle");
			
			if(value != undefined) {
				if(forAttributes == true) {
					attributes.attr("class", "mapping-element-attributes-toggle");
					attributes.addClass("mapping-element-attributes-validation-" + value);
				} else {
					validation.attr("class", "mapping-element-validation");
					validation.addClass("mapping-element-validation-" + value);
				}
			} else {
				validation.attr("class", "mapping-element-validation");
				attributes.attr("class", "mapping-element-attributes-toggle");
			}
		},
		
		bookmark: function(state) {
			var data = this.data(widget);
			var bookmark = data.header.find(".mapping-element-bookmark");

			bookmark.attr("class", "mapping-element-bookmark");
			bookmark.addClass("mapping-element-bookmark-" + state);
		},
		
		hasMappings: function() {
			var data = this.data(widget);
			//console.log(data, data.header, data.header.find(".mapping-element-mapping:not(.unmapped-mapping, .structural-mapping)"));
			if(data.header.find(".mapping-element-mapping:not(.unmapped-mapping, .structural-mapping)").length > 0) return true;
			
			if(!data.children.is(":empty") && data.target.children != undefined) {
				var children = data.children.children();
				for(i in children) {
					if($.isNumeric(i)) {
						var child = children[i];
						//console.log("looking mappingElement: ", i, child);
						if($(child).mappingElement("hasMappings")) return true;
					}
				}				
			} else {
				if(data.target.children != undefined) {
					for(i in data.target.children) {
						if($.isNumeric(i)) {
							var child = data.target.children[i];
							if(targetHasMappings(child)) return true;
						}
					}
				}
			}

			if(!data.attributes.is(":empty") && data.target.attributes != undefined) {
				var attributes = data.attributes.children();
				for(i in attributes) {
					if($.isNumeric(i)) {
						var child = attributes[i];
						//console.log("looking mappingElement: ", i, child);
						if($(child).mappingElement("hasMappings")) return true;
					}
				}				
			} else {
				if(data.target.attributes != undefined) {
					for(i in data.target.attributes) {
						if($.isNumeric(i)) {
							var child = data.target.attributes[i];
							if(targetHasMappings(child)) return true;
						}
					}
				}
			}
				
			return false;
		},
		
		hasMissing: function() {
			var data = this.data(widget);
			//console.log(data, data.header, data.header.find(".mapping-element-mapping:not(.unmapped-mapping, .structural-mapping)"));
			//console.log(data.target.name, data.target.minOccurs);
			if(!data.children.is(":empty") && data.target.children != undefined) {
				var children = data.children.children();
				for(i in children) {
					if($.isNumeric(i)) {
						var child = children[i];
						//console.log("looking mappingElement: ", i, child);
						if($(child).mappingElement("hasMissing")) return true;
					}
				}				
			} else {
				if(data.target.children != undefined) {
					for(i in data.target.children) {
						if($.isNumeric(i)) {
							var child = data.target.children[i];
							if(targetHasMissing(child)) return true;
						}
					}
				}
			}

			if(!data.attributes.is(":empty") && data.target.attributes != undefined) {
				var attributes = data.attributes.children();
				for(i in attributes) {
					if($.isNumeric(i)) {
						var child = attributes[i];
						//console.log("looking mappingElement: ", i, child);
						if($(child).mappingElement("hasMissing")) return true;
					}
				}				
			} else {
				if(data.target.attributes != undefined) {
					for(i in data.target.attributes) {
						if($.isNumeric(i)) {
							var child = data.target.attributes[i];
							if(targetHasMissing(child)) return true;
						}
					}
				}
			}
				
			if((data.target.minOccurs > 0) && !data.container.mappingElement("hasMappings")) 
				return true;
			return false;
		},
		getXpathsInMappings: function() {
			return xpathsInMappings;
		}
	};
})(jQuery);

/**
 * Utility function that analyses an element name and returns the name and prefix parts in the following structure:
 * <pre>
 * {
 *   prefix: "the prefix",
 *   name: "the name"
 * }
 * </pre>
 * @param str the element name.
 * @returns json object that contains prefix and name fields.
 */
function stringToNameAndPrefix(str) {
	var np = {
		name: str
	};
	
	if(str.indexOf(":") > -1) {
		var tokens = str.split(":");
		np.prefix = tokens[0];
		np.name = tokens[1];
	}
	
	return np;
}

/**
 * Compares two element name structures. Checks if their prefix and name fields contain the same information. 
 * @param np1
 * @param np2
 * @returns {Boolean}
 */
function elementNameAndPrefixEqual(np1, np2) {
	if(np1.name == np2.name) {
		if(np1.prefix == np2.prefix) {
			return true;
		}
	}
	
	return false;
}

function defaultTooltip(context, content) {
	context.qtip({
		content: { text: content },
		show: { when: "mouseover", solo: true },
		hide: { when: "mouseout", fixed: true, delay: 1000 },
		position: {
			corner: {
				target: "rightMiddle",
				tooltip: "leftMiddle"
			}
		},
		style: {
			name: "cream",
			width: { max : 500 },
			tip: { corner: 'leftMiddle' },
			classes: {
				tooltip: "mapping-tooltip"
			}
		}
	});
}