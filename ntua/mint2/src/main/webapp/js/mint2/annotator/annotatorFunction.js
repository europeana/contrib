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
	    	 description: "Edit value of element",
	    	 	arguments: [
	    	 	{ label: "Type new value"}
	    	 	]
	     },
	     {
	    	 name: "editValueIf",
	    	 description: "Edit elements that have certain value",
	    	 	arguments: [
	    	 	{label: "Edit elements with value: "},
	    	 	{label: "Type new value"}
	    	 	] 
	     },
	     //TODO: dates transformation functions?
	     {
	    	 	name: "extensionToLowerCase",
	    	 	description: "Convert extension to lower case",
	    	 	message: "Extension means characters after '.'",
	    	 	arguments: []
	     },
	     {
	    	 	name: "extensionToUpperCase",
	    	 	description: "Convert extension to upper case",
	    	    message: "Extension means characters after '.'",
	    	 	arguments: []
	     },
	     {
	    	 	name: "substringToUpperCase",
	    	 	description: "Substring to upper case",
	    	 	arguments: [
	    	 	            { label: "Type string to be converted to upper case"}
	    	 	            ]
	     },
	     {
	    	 	name: "substringToLowerCase",
	    	 	description: "Substring to lower case",
	    	 	arguments: [
	    	 	            { label: "Type string to be converted to lower case" }
	    	 	            ]
	     }
    ];
    
	function render(container) {
		var data = container.data(widget);
		var xpath = data.settings.xpath;
		var fheader = $("<div>").addClass("annotator-function-header").appendTo(container);
		var fbody = $("<div>").addClass("annotator-function-body").appendTo(container);
		var ffooter = $("<div>").addClass("annotator-function-footer").appendTo(container);
		// header
		$("<span>").text("Function: ").appendTo(fheader);
		var select = $("<select>").attr("id", "annotator-function-call").css("border", "1px solid").appendTo(fheader);	
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

		var apply = $("<span>").text("Apply function").button().appendTo(ffooter);//{disabled: true}
		apply.click(function() {
			//renderArguments(container);
			var calldata = container.annotatorFunction("calldata");
			$("<div>").annotatorElement("addCallData",  calldata.apply, xpath, calldata.arguments);
			$(this).closest('.ui-dialog-content').dialog('close');
		});
		
		select.change(function() {
			var selected = data.container.find('#annotator-function-call').val();
			//if (selected == "")
				//apply.button("disable");
			//else
			  // apply.button("enable");
			renderArguments(container);
		});
			
	}

	function renderArguments(container) {
		var select = container.find('#annotator-function-call').val();
		
		var fbody = container.find('.annotator-function-body');
		fbody.empty();
		
		var farguments = $("<div>").addClass("annotator-function-arguments").appendTo(fbody);

		for(var f in functions) {
			var funct = functions[f];
			var name = funct.name;
			
			if(name == select) {
				var fcount = 0;
				if(funct.message != undefined) {
					farguments.append($("<div>").text(funct.message).addClass("mapping-function-message"));
				}															
				for(var fa in funct.arguments) {
					var fargument = funct.arguments[fa];

					if(fargument.type == undefined) {
						fargument.type = "text";
					}
					var row = $("<div>").addClass("annotator-function-argument").appendTo(farguments);
					var label = $("<span>").addClass("annotator-function-argument-label")
						.text(fargument.label).appendTo(row);
					var value = $("<input>", {type: fargument.type})
						.addClass("annotator-function-argument-value").appendTo(row);
					fcount += 1;                             
				}
				
				var maxWidth = 100;
				farguments.find(".mapping-function-argument-label").each(function(k, v) {
					var width = $(v).outerWidth();
					if(width > maxWidth) maxWidth = width;
				});
				
				farguments.find(".mapping-function-argument-label").each(function(k, v) {
					$(v).css("width", maxWidth + "px");
				});
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