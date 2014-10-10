
(function($) {
	var widget = "mappingArea";
	var defaults = {
		'showRemoveMappingCase': false,
		'preferedInput': undefined,
		'case': 0
	};
	
	/**
	 * @class mappingArea
	 * @name mappingArea
	 * @memberOf XSDMappingEditor
	 * @description
	 * jQuery plugin that handles a single mapping area within a mapping element.
	 */
	$.fn.mappingArea = function(method) {
	    if ( methods[method] ) {
	        return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
	      } else if ( typeof method === 'object' || ! method ) {
	        return methods.init.apply( this, arguments );
	      } else {
	        $.error( 'Method ' + method + ' does not exist on ' + widget );
	      }   
	};

	function render(container) {
		var data = container.data(widget);
		var target = data.target;
		var mappings = [];
		var maptemp = []; // holds mappings and unmapped fields
		
		// setup basic conditions
		/** Belongs to an XSD Mapping editor - enables schema mappings on top of value edits */
		var isSchemaMapping = (data.settings.schemaMapping == true);
		/** Element is fixed and should not be modified by editor */
		var isFixed = (target.fixed != undefined);
		/** Mapping area edits structural information of element */
		var isStructural = (data.settings.structural != undefined && data.settings.structural);
//		old structural definition - structural now is an option of mappingArea.		
//		var isStructural = (target.children != undefined && target.children.length > 0);
		/** Element takes values from enumerations */
		var isEnumerated = (target.enumerations != undefined && target.enumerations.length > 0);
		/** A Thesaurus is assigned to the element */
		var isThesaurus = (target.thesaurus != undefined && target.thesaurus.conceptScheme != undefined);

		// render container
		container.empty().addClass("mapping-element-mappings");
		var areaMappings = $("<div>").appendTo(container).css("margin-bottom", "2px");

		/** actions left of the mapping area (i.e. condition action) */
		var left_actions = $("<div>").addClass("mapping-element-mappings-actions top").appendTo(areaMappings);
		/** submappings area */
		var areas = $("<div>").addClass("mapping-element-mappings-area").appendTo(areaMappings);
		/** actions right of the mapping area (i.e. else condition action) */
		var right_actions = $("<div>").addClass("mapping-element-mappings-actions bottom").appendTo(areaMappings);
		/** next else part of the mapping area - based on options.else index */
		
		// initialise mapping areas
		/** Element area has no mappings */
		var isEmpty = false;
		
		if(isStructural) {
			areas.addClass("structural");
		}
		
		// setup mapping text and submappings
		if(isStructural) {
			if(target.structural == undefined || target.structural.mappings == undefined || target.structural.mappings.length == 0) {
				isEmpty = true;
				maptemp.push({ type: "unmapped" });
			} else {
				maptemp.push({ type: "xpath", value: target.structural.mappings[0].value });
			}
		} else {
			
			// normal mappings
//			console.log("Annotation", target);
			var mappingCase = target["mapping-cases"][data.settings.case];
			if(mappingCase != undefined && mappingCase.mappings != undefined) {
				maptemp.push.apply(maptemp, target["mapping-cases"][data.settings.case].mappings);
			}
		
			if(maptemp && maptemp.length == 0) {
				isEmpty = true;
				maptemp.push({ type: "unmapped" });
			}
		}
		
		// show only if schema mapping is enabled
		if(!isSchemaMapping) {
			left_actions.hide();
		}
		
		// condition button and area
		if(isSchemaMapping && !isEmpty && !isFixed) {
			var theCondition = undefined;
			if(isStructural) {
				theCondition = data.target["structural"].condition;
			} else {
				theCondition = data.target["mapping-cases"][data.settings.case].condition;
			}
			var hasCondition = theCondition != undefined && theCondition.clauses != undefined && theCondition.clauses.length > 0;
			
			var conditionArea = $("<div>").appendTo(areas).mappingCondition({
				element: data.container,
				target: data.target,
				editor: data.editor,
				condition: theCondition,
				index: {
					case: data.settings.case,
					key: (isStructural)?"structural":undefined
				}
			});

			var condition = $("<div>")
			.attr("title", "Set condition")
			.appendTo(left_actions)
			.click(function() {
				if(conditionArea.is(":visible")) {
					conditionArea.hide();
					if(hasCondition) {
						addCase.hide();
						removeCase.hide();
					}
				} else {
					conditionArea.show();
					if(hasCondition) {
						addCase.show();
						removeCase.show();
					}
				}
			});
					
			if(hasCondition) {
				condition.addClass("mapping-action-condition-on");
			} else {
				condition.addClass("mapping-action-condition-off");
				conditionArea.hide();
			}
			
			if(hasCondition && !isStructural) {
				var addCase = $("<div>")
				.addClass("mapping-action-add")
				.css("display", "block")
				.attr("title", "Add mapping case")
				.appendTo(left_actions)
				.click(function () {
					data.container.mappingArea("addMappingCase");
				});
			}
		}
		
		// remove mapping case buttons shows if more than one cases exist, even if no condition exists.
		if(data.settings.showRemoveMappingCase) {
			var removeCase = $("<div>")
			.addClass("mapping-action-delete")
			.css("display", "block")
			.attr("title", "Remove mapping case")
			.appendTo(left_actions)
			.click(function () {
				data.container.mappingArea("removeMappingCase");
			});
		}
		
		// For each of the submappings, or empty placeholder
		for(i in maptemp) {
			var input = maptemp[i];
			var mapping = $("<div>")
			.appendTo(areas)
			.addClass("mapping-element-mapping")
			.data("mappingElement", this);
			
			// mapping actions
			var actions = $("<div>").addClass("mapping-element-mapping-actions").appendTo(mapping);
			// mapping area for double click and/or drop
			var area = $("<div>").addClass("mapping-element-mapping-area").appendTo(mapping);	
			// toggle mapping actions - experimental
			if(false && data.editor.preferences.get("enableExperimental")) {
				actions.hide();
				var toggleArea = $("<div>")
				.addClass("mapping-element-mapping-actions")
				.css("width", "16px")
				.prependTo(mapping);
				
				var toggleAction = $("<span>")
				.css("display", "inline-block")
				.addClass("mapping-action-condition-on")
				.appendTo(toggleArea)
				.click(function() {
					actions.toggle();
				})
			}

						

			/** Mapping is empty - unmapped placeholder */
			var isUnmapped = (input.type == "empty");

			var isXPath = (input.type == "xpath");
			var isConstant = (input.type == "constant");
			var isParameter = (input.type == "parameter");
			
			// setup appropriate classes
			if(isFixed) {
				mapping.addClass("fixed-mapping");
			} else if(isSchemaMapping && !isFixed) {
				mapping.addClass("schema-tree-drop");
			}

			// set mapping index:
			// -1 for unmapped elements
			// n for nth mapping
			var index = i;
			if(input.type == "unmapped") {
				index = -1;
				if(isStructural) {
					area.text("structural");
					mapping.addClass("structural-mapping");
				} else {
					var text = "unmapped";
					if(isEnumerated) text = "unmapped (enumerated)";
					if(isThesaurus) text = "unmapped (thesaurus)";
					area.text(text);
					mapping.addClass("unmapped-mapping");

				}
			} else if(input.type == "empty") {
				area.text("unmapped");
				area.attr('contenteditable','true');
				mapping.addClass("unmapped-mapping");
			} else if(isConstant) {
				if(input.annotation != undefined) {
					area.text(input.annotation);
					area.addClass("annotation");
					area.attr('contenteditable','true');
				} else {
					area.text(input.value);
					area.attr('contenteditable','true');
				}
				defaultTooltip(area, input.value);
				mapping.addClass("constant-mapping");
			} else if(isParameter) {
				var text = (input.annotation != undefined) ? input.annotation : input.value;
				area.text(text);
				defaultTooltip(area, "Parameter: " + input.value);
				mapping.addClass("parameter-mapping");
			} else if(isXPath) {
				var parts = input.value.split("/");
				area.data("xpath", { xpath: input.value });
				area.text(parts[parts.length - 1]);
				
				var tooltip = $("<div>");
				tooltip.append($("<div>").addClass("xpath").text(input.value.replace(new RegExp("/", "g"), " / ")));
				var tooltipActions = $("<div>").addClass("actions").appendTo(tooltip);
				var showTree = $("<div>").addClass("action").appendTo(tooltipActions);
				$("<span>").addClass("mapping-action-search").appendTo(showTree);
				$("<span>").text("Show in tree").appendTo(showTree).click(function () {
					data.editor.tree.selectXPath(input.value);
				});
				var showValues = $("<div>").addClass("action").appendTo(tooltipActions);
				$("<span>").addClass("mapping-action-search").appendTo(showValues);
				$("<span>").text("Show values").appendTo(showValues).click(function () {
					var xpathHolderId = data.editor.tree.getNodeId(input.value);

					data.editor.loadSubpanel(function(panel) {
						var details = $("<div>").css("padding", "10px");
						var browser = $("<div>").appendTo(details);
						
						panel.find(".panel-body").before(details);
						browser.valueBrowser({
							xpathHolderId: xpathHolderId
						});
					}, "Values");
				});
				
				defaultTooltip(area, tooltip);
				mapping.addClass("xpath-mapping");
			}
			area.data("mapping", { index: index, target: target, case: data.settings.case });
			
			if(!isEmpty && !isFixed) {
				// add action
				if(isSchemaMapping && !isStructural && !isEnumerated && !isThesaurus) {
				var add = $("<span>").attr("title", "Concatenate mapping").addClass("mapping-action-add").css("left", "32px").appendTo(actions);
					add.data("mapping", { index: index, button: add });
					add.click(function() {
						var idx = $(this).data("mapping").index;
						var button = $(this).data("mapping").button;
						button.toggleClass("mapping-element-loading");
						data.editor.closeSubpanelIfReferencedBy(mapping);
						$.ajax({
							url: data.settings.ajaxUrl,
							context: this,
							dataType: "json",
							data: {
								command: "additionalMappings",
								id: data.target.id,
								index: idx,
								case: data.settings.case
							},
							
							success: function(response) {
								data.target = response;
								data.container.mappingArea("refresh");
								button.toggleClass("mapping-element-loading");
							}
						});
					});
				}

				// remove action
				var remove = $("<span>").attr("title", "Remove mapping").addClass("mapping-action-delete").css("left", "48px").appendTo(actions);
				remove.data("mapping", { index: index, button: remove });
				remove.click(function() {
					var idx = $(this).data("mapping").index;
					var button = $(this).data("mapping").button;
					button.toggleClass("mapping-element-loading");
					data.editor.closeSubpanelIfReferencedBy(mapping);
					
					data.container.mappingArea("removeMapping", idx);
				});

				if(isSchemaMapping && !isConstant && !isParameter && !isUnmapped && !isStructural) {
					var self_data = data;

					// functions action
					var functions = $("<span>").attr("title", "Apply functions").css("left", "0px").appendTo(actions);
					if(input.func != undefined && input.func.call != undefined && input.func.call.length > 0) {
						functions.addClass("mapping-action-function-on");
					} else {
						functions.addClass("mapping-action-function-off");
					}
					functions.data("mapping", { index: index, mapping: input, element: this, mapdiv: mapping });
					functions.click(function() {
						var idx = $(this).data("mapping").index;
						var map = $(this).data("mapping").mapping;
						var elm = $(this).data("mapping").element;
						var mapdiv = $(this).data("mapping").mapdiv;
						var target = self_data.target;
						self_data.editor.loadSubpanel(function(panel) {
							var functionContainer = $("<div>").addClass("mapping-function-container").append(node);
							var node = $("<div>").appendTo(functionContainer);
							panel.find('.panel-options').after(functionContainer);
							node.mappingFunction({
								element: container,
								mapping: map,
								index: idx,
								case: data.settings.case,
								editor: data.editor
							});
							
						}, data.target.name + " functions", { reference: mapdiv });
					});
					
					// value mappings action
					var values = $("<span>").attr("title", "Set value mappings").css("left", "16px").appendTo(actions);
					if(input.valuemap != undefined && input.valuemap.length > 0 ) {
						values.addClass("mapping-action-values-on");
					} else {
						values.addClass("mapping-action-values-off");
					}
					values.data("mapping", { index: index, mapping: input, element: this, mapdiv: mapping });
					values.click(function() {
						var idx = $(this).data("mapping").index;
						var map = $(this).data("mapping").mapping;
						var elm = $(this).data("mapping").element;
						var mapdiv = $(this).data("mapping").mapdiv;
						var target = self_data.target;
						self_data.editor.loadSubpanel(function(panel) {
							var valuesContainer = $("<div>").addClass("mapping-values-container").append(node);
							var node = $("<div>").appendTo(valuesContainer);
							panel.find('.panel-options').after(valuesContainer);
							node.mappingValues({
								element: container,
								mapping: map,
								index: idx,
								case: data.settings.case,
								editor: data.editor,
								target: target
							});
							
						}, data.target.name + " value mappings", { reference: mapdiv });
					});
				}							
			}
			
			// enable constant mapping
			if(!isFixed && !isStructural && (isConstant || isUnmapped || isEmpty)) {
				mapping.addClass("editable-mapping");
				var selected = input.value;
				if(isThesaurus) {
					area.data("mapping", { index: index, mapping: input, element: this, mapdiv: mapping });
					area.attr('contenteditable','false');
					
					
					area.on("touchstart",function(e) {
							var idx = $(this).data("mapping").index;
							var map = $(this).data("mapping").mapping;
							var elm = $(this).data("mapping").element;
							var mapdiv = $(this).data("mapping").mapdiv;
							var target = data.target;
							data.editor.loadSubpanel(function(panel) {
								var thesaurusContainer = $("<div>").addClass("mapping-thesaurus-container").append(node);
								var node = $("<div>").appendTo(thesaurusContainer);
								panel.find('.panel-options').after(thesaurusContainer);
								var thesaurus = new ThesaurusBrowser(node, {
									thesaurus: data.target.thesaurus,
									select: function(concept) {
										data.container.mappingArea("setConstantValueMapping", {
											index: idx,
											value: concept.concept,
											annotation: concept.label
										});
									}
								});
							}, data.target.name + " thesaurus", { reference: mapdiv });
						});
					
					area.on("dblclick",function() {
						var idx = $(this).data("mapping").index;
						var map = $(this).data("mapping").mapping;
						var elm = $(this).data("mapping").element;
						var mapdiv = $(this).data("mapping").mapdiv;
						var target = data.target;
						data.editor.loadSubpanel(function(panel) {
							var thesaurusContainer = $("<div>").addClass("mapping-thesaurus-container").append(node);
							var node = $("<div>").appendTo(thesaurusContainer);
							panel.find('.panel-options').after(thesaurusContainer);
							var thesaurus = new ThesaurusBrowser(node, {
								thesaurus: data.target.thesaurus,
								select: function(concept) {
									data.container.mappingArea("setConstantValueMapping", {
										index: idx,
										value: concept.concept,
										annotation: concept.label
									});
								}
							});
						}, data.target.name + " thesaurus", { reference: mapdiv });
					});
				} else {
					
					area.editable(function(value, settings) {
						var index = $(this).data("mapping").index;
						var selected = undefined;
						if($(this).data("mapping").target.enumerations != undefined) {
							var enumerations = $(this).data("mapping").target.enumerations;
							for(var i in enumerations) {
								var e = enumerations[i];
								if(e.value != undefined && e.label != undefined && e.value == value) selected = e.label;
							}
						}
						
						data.container.mappingArea("setConstantValueMapping", {
							index: index,
							value: value,
							annotation: selected
						});
				
						return value;
					},{
						event: "dblclick touchstart",
						style: "width: 100%",
						type:(isEnumerated)?"select":data.settings.preferedInput,
						data:(isEnumerated)?editable_enumerations(target.enumerations, selected):undefined,
						submit:(isEnumerated || data.settings.preferedInput != undefined)?"Apply":undefined
					});
				}
			}
		
			mappings.push(mapping);
		}

		data.mappings = mappings;
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
				editor: options.editor,
			});
			
			if(options.editor != undefined) $(options.editor).trigger("subscribeElement", this);
			
			render(this);
			
			return this;
		},
		
		destroy: function() {
			this.removeData(widget);
		},
		
		settings: function() {
			return this.data(widget);
		},
		
		setCondition: function(condition, index) {
			var data = this.data(widget);
			
			if(index != undefined && index.key == "structural") {
				data.settings.target["structural"].condition = condition;
			} else {
				data.settings.target["mapping-cases"][data.settings.case].condition = condition;
			}
			
			this.mappingArea(data.settings);			
		},
		
		setStructuralMapping: function(arguments) {
			this.mappingArea("setXPathMapping", $.extend({}, arguments, { structural: true }));
		},
		
		setXPathMapping: function(arguments) {
			var data = this.data(widget);
			var structural = (data.settings.structural != undefined) ? data.settings.structural : false;
			
//			console.log(arguments);
			
			var command = "setXPathMapping";
			if(structural) { command = "setStructuralMapping"; }
			$.ajax({
				url: data.settings.ajaxUrl,
				context: this,
				dataType: "json",
				data: {
					command: command,
					id: data.target.id,
					xpath: encodeURIComponent(arguments.xpath),
					annotation: (arguments.annotation != undefined) ? encodeURIComponent(arguments.annotation) : null,
					index: arguments.index,
					case: data.settings.case
				},
				
				success: function(response) {
					data.target = response;
					data.container.mappingArea("refresh");
					data.editor.validate();
					//$(data.editor).trigger("documentChanged", response);
				}
			});
		},
		
		setConstantValueMapping: function(arguments) {
			var data = this.data(widget);
			data.editor.ajax.setConstantValueMapping(
				$.extend({}, arguments, { id: data.settings.target.id, case: data.settings.case }),
				function(response) {
					data.target = response;
					data.container.mappingArea("refresh");
			
					//$(data.editor).trigger("documentChanged", response);
				});
		},
		
		removeMapping: function(index) {
			var data = this.data(widget);
			var structural = (data.settings.structural != undefined) ? data.settings.structural : false;

			var arguments = {
				id: data.target.id,
				index: index,
				case: data.settings.case
			}
			
			var callback = function(response) {
				data.target = response;
				data.container.mappingArea("refresh");
				data.editor.validate();
				//$(data.editor).trigger("documentChanged", response);
			}

			if(structural) {
				data.editor.ajax.removeStructuralMapping(arguments, callback);
			} else {
				data.editor.ajax.removeMapping(arguments, callback);		
			}	
		},

		addMappingCase: function() {
			var data = this.data(widget);

			$.ajax({
				url: data.settings.ajaxUrl,
				context: this,
				dataType: "json",
				data: {
					command: "addMappingCase",
					id: data.target.id,
					case: data.settings.case
				},
				
				success: function(response) {
					data.settings.element.mappingElement("refresh", response);
					
					//$(data.editor).trigger("documentChanged", response);
				}
			});
		},

		removeMappingCase: function() {
			var data = this.data(widget);

			$.ajax({
				url: data.settings.ajaxUrl,
				context: this,
				dataType: "json",
				data: {
					command: "removeMappingCase",
					id: data.target.id,
					case: data.settings.case
				},
				
				success: function(response) {
					data.settings.element.mappingElement("refresh", response);
					
					//$(data.editor).trigger("documentChanged", response);
				}
			});
		},

		refresh: function(closeSubpanel) {
			var data = this.data(widget);

			var parentClass = undefined;
			var activeIndex = undefined;
			var active = this.find(".mapping-active");
			
			if(closeSubpanel == undefined || closeSubpanel) data.editor.closeSubpanelIfReferencedBy(active);
			
			data.settings.target = data.target;
			this.mappingArea(data.settings);
			
			this.trigger("afterRefresh");
			//data.editor.validate();
		}		
	};
	
	function editable_enumerations(enumerations, selected) {
		var result = [];
		
		for(i in enumerations) {
			var enumeration = enumerations[i];
			var value = (enumeration.value != undefined) ? enumeration.value : enumeration;
			result[value] = (enumeration.label != undefined) ? enumeration.label + " - " + enumeration.value : value;
		}
		
		if(selected != undefined) result["selected"] = selected; 
		
		return result;
	}
	
	
})(jQuery);