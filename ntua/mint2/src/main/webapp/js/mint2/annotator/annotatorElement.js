(function($) {
	var widget = "annotatorElement";
	var defaults = {
		'xpath': undefined,
		'ajaxUrl': 'Annotator_ajax',
		//'showEditValue': true,
		//'showDuplicateWithNewValue': true,
		//'showRemoveWithValue': true,
		'label': null
	};
    var clicked = [];
    
	$.fn.annotatorElement = function(method) {
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
		var tree = data.target;
		
		var self = this;
		
		container 
		.empty()
		.addClass("schema-tree-ann-container");
		
		//$("<div>").text("Schema").appendTo(container);
		
		if (tree == undefined) {
			container.text("Transformed item not defined");
		} else {
			 var treeContainer = $("<div>")
				.addClass("schema-tree-ann")
				//.css("text-align", "center")
				//.css('font-weight', 'bold')
				//.css('font-size', 15)
				.attr("id", "treeContainer");
			 
			this.searchContainer = Mint2.searchBox({
				prompt: "Search in schema",
				callback: function(term) {
					searchInTree(treeContainer, term);
				}
		 	}).addClass("schema-tree-search").appendTo(container);
			

			treeContainer.appendTo(container);

			treeContainer.jstree({
				core: {
					animation: 100
				},
				plugins : ["themes", "search", "json_data", "ui"],
				json_data: {
					data: tree
				},
				ui: {
					select_limit: 1
				},
				themes: {
					theme: "classic",
					dots: false
				}
			}); 
			/*treeContainer.find("a").css({
				"text-decoration": "none"
			});*/
			treeContainer.bind("reopen.jstree", function (e, data) {
				iterateOverNodes(tree);	
				treeContainer.jstree('open_node', $("#schema-tree-"+tree[0].metadata.xpathHolderId));
			});
			treeContainer.jstree('reopen');

			treeContainer.bind("select_node.jstree", function(event, data) {
				var metadata = data.rslt.obj.data();
				var minOccurs = metadata.minOccurs;
				var maxOccurs = metadata.maxOccurs;
				var title = data.rslt.obj.text().substring(2);
				var id = metadata.xpathHolderId;
				var children = data.inst.get_json(data.rslt.obj)[0].children;
				if (clicked.indexOf(id) < 0) {
					if	((children == undefined || children.length==0)) {
						var fpopup =  $("<div>")
						.attr("id", "editFunctions")
						.css("marginLeft", "20px")
						.appendTo($("#schema-tree-" + id));
						fpopup.annotatorFunction({xpath: metadata.xpath});
						/*
						if (!(maxOccurs == 1 && minOccurs == 1) && !(title.indexOf("@")==0)) {
							$("<div>").addClass("mapping-function-message").text("").appendTo(fpopup);
							var ffooter = $("<div>").addClass("annotator-function-body").appendTo(fpopup);
							var deleteEl = $("<div>").text("Delete").css("margin-left", "130px")
								//.css("color", "red")
								.button().appendTo(ffooter);
							deleteEl.click(function() {
								$("<div>").annotatorElement("addCallData", "remove", metadata.xpath, ["*"]);
							});
						}
						*/
					}
					/*
					else {
						//var parent = data.inst.get_parent();
						var parent = data.inst._get_parent(data.rslt.obj);
						if (parent != -1) {
							var fpopup =  $("<div>")
							.attr("id", "editFunctions")
							.css("marginLeft", "20px")
							.css("width", "200px");
							var fheader = $("<div>").addClass("annotator-function-header").appendTo(fpopup);
							*/
							/*
							if (maxOccurs > 1 || maxOccurs == -1) {
								fpopup.appendTo($("#schema-tree-" + id));
								//allow duplication
								var editValue = $("<span>")
								.text("Duplicate")
								.button().appendTo(fbody);
								editValue.click(function() {
									var value = prompt("New element", "Type value of new element here");
									if (value != null)
										$("<div>").annotatorElement("addCallData", "duplicate", metadata.xpath, [value]);
								});
							}
							*/
							/*
							if (!(maxOccurs == 1 && minOccurs == 1)) {
								//allow deletion
								fpopup.appendTo($("#schema-tree-" + id));
								var deleteEl = $("<span>")
								.text("Delete")
								.css("margin-left", "60px")
								.button().appendTo(fheader);
								deleteEl.click(function() {
									$("<div>").annotatorElement("addCallData", "remove", metadata.xpath, ["*"]);
								});
							}
						}
					}*/
					clicked.push(id);
				}
				else {
					$('#editFunctions').remove();
					clicked.splice($.inArray(id, clicked), 1);
				}
			});

			/*
			// right side actions
			var right_actions = $("<div>").addClass("mapping-element-right-actions").appendTo(header);
			if (!isStructural) {
				var mappingAreas = $("<div>")
				.addClass("mapping-element-mapping-areas")
				.appendTo(header);
				var editValue = $("<div>")
				.addClass("group-annotator-duplicate")
				.attr("title", "Duplicate element")
				.appendTo(right_actions);
				editValue.click(function() {
					var value = prompt("New element", "Type value of new element here");
					if (value != null)
					   container.annotatorElement("addCallData", "duplicate", xpath, [value]);
				});
				
				var removeWithValue = $("<div>")
				.addClass("group-annotator-remove")
				.attr("title", "Remove element with certain value")
				.appendTo(right_actions);
				removeWithValue.click(function() {
					var value = prompt("Remove element with value", "Type value here");
					if (value != null)
					   container.annotatorElement("addCallData", "remove", xpath, [value]);
					//xpathsToRemove.push({"xpath":xpath, "value":value});
				});
			}
			
			
			var remove = $("<href>")
			.addClass("group-annotator-removeAll")
			.attr("title", "Remove element")
			.attr("id", "a")
			.appendTo(right_actions);
			remove.click(function() {
				var rpopup =  $("<div>")
				.attr("title", "Remove element")
				.text("Xpath: " + xpath + " will be removed");
				rpopup.dialog({
					buttons : {
			        "Confirm" : function() {
					  $(this).dialog("close");
					  container.annotatorElement("addCallData", "remove", xpath, ["*"]);
			        },
			        "Cancel" : function() {
			          $(this).dialog("close");
			        }
			      }
				 });
			});

			*/
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
		
		addCallData: function(fname, xpath, arguments) {
			var xpathData = {type: fname, xpath: xpath, arguments: arguments};
			groupActs.push(xpathData);
	    }
	};
})(jQuery);

function searchInTree(treeContainer, term) {
	treeContainer.find(".jstree-search").removeClass("jstree-search");
	treeContainer.jstree("search", term);
}

function iterateOverNodes(tree) {
	$.each(tree, function(k, node) {
		var children = node.children;
		if (children.length == 0 || children == undefined) {
			var id = node.metadata.xpathHolderId;
			//alert(node.data.title + "  " + jQuery("#treeContainer").jstree("select_node", "#schema-tree-" + id).html());
			$("#schema-tree-" + id + " a")
			//.find("a")
			.css({
				"text-decoration": "underline"
			});
			return;
		}
		else {
			iterateOverNodes(children);
			return;
		}
	});
}

