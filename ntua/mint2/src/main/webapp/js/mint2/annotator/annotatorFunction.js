(function($) {
	var widget = "annotatorFunction";
	var defaults = {
		'ajaxUrl': 'Annotator_ajax'
	};

	var functions = [
    	 /*{
    	 	name: "",
    	 	description: "No function",
    	 	arguments:[]
 	     },*/
	     {
	    	 name: "editValue",
	    	 description: "Set constant value",
	    	 	arguments: [
	    	 	{ label: "New value"}
	    	 	]
	     },
	     {
	    	 name: "editValueIf",
	    	 description: "Replace value",
	    	 	arguments: [
	    	 	{label: "Current value"},
	    	 	{label: "New value"}
	    	 	] 
	     },
	     //TODO: dates transformation functions
	     {
	    	 	name: "extensionToULCase",
	    	 	description: "File extension to lower case",
	    	 	//message: "Substring after '.' will be set to lower/upper case",
	    	 	arguments: []
	     },
	     /*
	     {
	    	 	name: "extensionToUpperCase",
	    	 	description: "File extension to upper case",
	    	    message: "Substring after '.' will be set to lower case"",
	    	 	arguments: []
	     },
	     */
	     {
	    	 	name: "substringToULCase",
	    	 	description: "Substring to lower/upper case",
	    	 	arguments: [
	    	 	            { label: "String to be converted"}
	    	 	            ]
	     },
	     /*
	     {
	    	 	name: "substringToLowerCase",
	    	 	description: "Substring to lower case",
	    	 	arguments: [
	    	 	            { label: "String to be converted" }
	    	 	            ]
	     }
	     */
    ];
    
	function render(container) {
		var fheader = $("<div>").addClass("annotator-function-header").appendTo(container);
		var fbody = $("<div>").addClass("annotator-function-body").appendTo(container);
		//var ffooter = $("<div>").addClass("annotator-function-footer").appendTo(container);
		// header
		$("<span>").text("Apply Function:").addClass("annotator-function-text").appendTo(fheader);
		fheader.append("<br/>");
		var select = $("<select>").attr("id", "annotator-function-call")
			.css("border", "1px solid")
			.css("background", "#f1f1f1")
			.css("padding", "0px")
			.css("margin-left", "6px")
			//.width('auto')
			.width(250)
			.appendTo(fheader);	
		for(var f in functions) {
			var name = functions[f].name;
			var description = functions[f].description;
			var option = $("<option>").attr("value", name).text(description);
			if (name == "editValue") {
				option.attr("selected", 'selected');
			}
			option.appendTo(select);
		}
		
		renderArguments(container);
		
		select.change(function() {
			renderArguments(container);
		});
			
	}

	function renderArguments(container) {
		var data = container.data(widget);
		var xpath = data.settings.xpath;
		var select = container.find('#annotator-function-call').val();
		
		var fbody = container.find('.annotator-function-body');
		fbody.empty();
		
		var farguments = $("<div>").addClass("annotator-function-arguments").appendTo(fbody);

		for(var f in functions) {
			var funct = functions[f];
			var name = funct.name;
			
			if (name == select) {
				//container.find('#annotator-function-call').width(funct.description.length*7.6+10);
				var fcount = 0;
				if (funct.message != undefined) {
					farguments.append($("<div>").text(funct.message).addClass("mapping-function-message"));
				}															
				for (var fa in funct.arguments) {
					var fargument = funct.arguments[fa];

					if (fargument.type == undefined) {
						fargument.type = "text";
					}
					var row = $("<div>").addClass("annotator-function-argument").appendTo(farguments);
					var maxWidth = 15;
					if (fargument.label.length > maxWidth) {
						var label = $("<span>").text(fargument.label).appendTo(row);
						row.append("<br\>");
					}
					else
						var label = $("<span>").addClass("annotator-function-argument-label").text(fargument.label).appendTo(row);
					var value = $("<input>", { type: fargument.type }).addClass("annotator-function-argument-value").appendTo(row);
					fcount += 1;   
				}
				/*
				var maxWidth = 100;
				farguments.find(".annotator-function-argument-label").each(function(k, v) {
					var width = $(v).outerWidth();
					if (width > maxWidth) maxWidth = width;
				});
				
				farguments.find(".annotator-function-argument-label").each(function(k, v) {
					$(v).css("width", maxWidth + "px");
				});
				*/
				if (name.indexOf("ULCase") > 0) {
					var upper = $("<span>").css("margin-left", "5px").text("To Upper")
					.button().appendTo(fbody);
					var lower = $("<span>").css("margin-left", "10px").text("To Lower")
						.button().appendTo(fbody);
					upper.click(function() {
						var calldata = container.annotatorFunction("calldata");
						$("<div>").annotatorElement("addCallData", name.replace('UL', 'Upper'), 
								xpath, calldata.arguments);
					});
					lower.click(function() {
						var calldata = container.annotatorFunction("calldata");
						$("<div>").annotatorElement("addCallData", name.replace('UL', 'Lower'), 
								xpath, calldata.arguments);
					});
				}
				else {
					var apply = $("<span>").text("Apply")
					.css("margin-left", "5px").button().appendTo(fbody);//{disabled: true}
					apply.click(function() {
						//renderArguments(container);
						var calldata = container.annotatorFunction("calldata");
						$("<div>").annotatorElement("addCallData", calldata.apply, 
								xpath, calldata.arguments);
					});
				}
			}
		}
	}

	var methods = {
		init: function(options) {
			options = $.extend({}, defaults, options);
			this.data(widget, {
				settings: options,
				container: this
			});
			
			render(this);

			return this;
		},

		calldata: function() {
			var data = this.data(widget);
			var call = data.container.find('#annotator-function-call').val();
			var args = [];
			data.container.find(".annotator-function-argument-value").each(function(k, v) {
				args.push(encodeURIComponent($(v).val()));
			});
			
			var result = {
				apply: call,
				arguments: args
			};
			
			return result;
		}
    };
	
	$.fn.annotatorFunction = function(method) {
	    if ( methods[method] ) {
	        return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
	      } else if ( typeof method === 'object' || ! method ) {
	        return methods.init.apply( this, arguments );
	      } else {
	        $.error( 'Method ' +  method + ' does not exist on ' + widget );
	      }  
	};
	
})(jQuery);