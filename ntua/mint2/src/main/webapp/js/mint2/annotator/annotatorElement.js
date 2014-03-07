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
	
	var methods = {
			init: function(options) {
				options = $.extend({}, defaults, options);
				if(options.editor != undefined) {
					options.ajaxUrl = options.editor.ajaxUrl;
				}
				
				this.data(widget, {
					settings: options,
					container: this,
					editor: options.editor
				});

				render(this);
				
				return this;
			},
			/*
			addCallData: function(editor, fname, xpath, arguments) {
				var data = {apply: fname, arguments: arguments};
				var xpathData = [{xpath: xpath, data: [data]}];
				var itemsQuery = editor.items.itemBrowser("getQuery");
				var itemsToOmmit = editor.items.itemBrowser("getItemsToOmmit");
				var queryIndex = getQueryIndex(editor, itemsQuery, itemsToOmmit);
				if (queryIndex == -1) {
					editor.queryXpathData.push({query: itemsQuery, itemsToOmmit: itemsToOmmit, 
						xpathData: xpathData});
				}
				else {
					var xpathIndex = getXpathIndex(editor.queryXpathData[queryIndex].xpathData, xpath);
					if (xpathIndex == -1) {	
						editor.queryXpathData[queryIndex]["xpathData"] = xpathData;					
					}
					else 
						editor.queryXpathData[queryIndex].xpathData[xpathIndex].data.push(data);
				}
		    }
			*/
			addCallData: function(fname, xpath, arguments) {
				var xpathData = {type: fname, xpath: xpath, arguments: arguments};
				groupActs.push(xpathData);
		    }
	};
	
	$.fn.annotatorElement = function(method) {
	    if ( methods[method] ) {
	        return methods[ method ].apply( this, Array.prototype.slice.call(arguments, 1));
	      } else if ( typeof method === 'object' || ! method ) {
	        return methods.init.apply(this, arguments);
	      } else {
	        $.error('Method ' +  method + ' does not exist on ' + widget);
	      }   
	};
	
	function render(container) {
		var data = container.data(widget);
		var xpath = data.settings.xpath;
		var label = data.settings.label;
		var showDuplicateWithNewValue = data.settings.showDuplicateWithNewValue!=undefined ? data.settings.showDuplicateWithNewValue : true;
		var showRemoveWithValue = data.settings.showRemoveWithValue!=undefined ? data.settings.showRemoveWithValue : true; 
		var showRemoveAll =  data.settings.showRemoveAll!=undefined ? data.settings.showRemoveAll : true; 
		var showEditFunction = data.settings.showEditFunction!=undefined ? data.settings.showEditFunction : true; 		
		var fpopupOn = false;
		
		container.empty().addClass("mapping-element");
		
		//[{query:query, itemsToOmmit: itemsToOmmit, xpathData:[{xpath:xpath, data: [{apply: apply, arguments: [a1, a2, ..]}]}]}]
		//var queryXpathData = data.editor.queryXpathData;
		
		var header = $("<div>").addClass("group-annotator-header");
		var cssLabel = $("<div>").addClass("group-annotator-label").appendTo(header);
		var name = $("<div>").addClass("group-annotator-name").appendTo(cssLabel);
		if (xpath != undefined && label != undefined) {
			name.append(label);
			name.attr("title", xpath);
			var right_actions = $("<div>").addClass("group-annotator-actions").appendTo(header);
			if (showDuplicateWithNewValue) {
				var editValue = $("<div>")
				.addClass("group-annotator-duplicate")
				.attr("title", "Duplicate element")
				.appendTo(right_actions);
				editValue.click(function() {
					var value = prompt("New element", "Type value of new element here");
					if (value != null)
					   container.annotatorElement("addCallData", "duplicate", xpath, [value]);
				})
			}
			if (showRemoveWithValue) {
				var editValue = $("<div>")
				.addClass("group-annotator-remove")
				.attr("title", "Remove element with certain value")
				.appendTo(right_actions);
				editValue.click(function() {
					var value = prompt("Remove element with value", "Type value here");
					if (value != null)
					   container.annotatorElement("addCallData", "remove", xpath, [value]);
					//xpathsToRemove.push({"xpath":xpath, "value":value});
				})
			}
			if (showRemoveAll) {
				var editValue = $("<href>")
				.addClass("group-annotator-removeAll")
				.attr("title", "Remove element")
				.attr("id", "a")
				.appendTo(right_actions);
				editValue.click(function() {
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
			}
			//Allows the user to open only one function popup
			if (showEditFunction) {
				var editFunction = $("<div>")
				.addClass("group-annotator-function")
				.attr("title", "Select function to be applied on value")
				.appendTo(right_actions);
				editFunction.click(function() {
					if (fpopupOn == false) {
						var fpopup =  $("<div>")
						.attr("title", "Apply function to value of "+label)
						.attr("id", "function-popup");
						fpopup.annotatorFunction({xpath: xpath});
						fpopup.dialog({width: 400,height:200});
						fpopup.bind('dialogclose', function(event) {
						     fpopupOn = false;
						 });
						fpopupOn = true;
					}
				});
			}
			//var hasSchema = data.editor.configuration.schemaId != undefined;
		}
		
		container.append(header);
		
	}

	/*
	function groupAnnotate(query, xpathValues, xpathsToRemove) {
		var self = this;
		//this.container.annotations.empty();
		this.showLoading();
		$.ajax({
			url: this.ajaxUrl,
			context: this,
			dataType: "json",
			data: {
				command: "groupAnnotate",
				query: JSON.stringify(query),
				xpathValues: JSON.stringify(xpathValues),
				xpathsToRemove: JSON.stringify(xpathsToRemove)
			},
			success: function(response) {
				//$(self).trigger("afterItemsChange", response);
				//$(self).trigger("documentChanged", response);
				this.hideLoading();
			},
			
			failure: function(response) {
				this.hideLoading();
				//$(self).trigger("afterLoadItem", response);
			}
		});
	}
	*/
	function itemsChange() {
		var itemsQueryRecent = data.editor.items.itemBrowser("getQuery");
		if (JSON.stringify(this.itemsQuery) != JSON.stringify(itemsQueryRecent)) {
			this.itemsQuery = itemsQueryRecent;
			return true;
		}
		else 
			return false;
	}
	
	//xpathData:[{xpath:xpath, data: [{apply: apply, arguments: [a1, a2, ..]}]}]
	function getXpathIndex(xpathData, xpath) {
		var index = 0;
		for (var x in xpathData) {
			if (x.xpath == xpath)
				return index;
			index++;
		}
		return -1;
	}
	
	function getQueryIndex(editor, itemsQuery, itemsToOmmit) {
		var index = 0;
		for (var q in editor.queryXpathData) {
			if (q.query.query == itemsQuery && q.itemsToOmmit == itemsToOmmit)
				return index;
			index++;
		}
		return -1;
	}
})(jQuery);


