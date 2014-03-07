/**
 * An object that handles ajax calls to a mapping service.
 * 
 * @constructor
 * @this {MappingAjax}
 * @param {object} options The widget's configuration options.
 * @param {object} options
 */
function MappingAjax(options) {
	var defaults = {
	}
	
	this.settings = $.extend({}, defaults, options);
	if(this.settings.editor == undefined) console.error("No editor is defined for this MappingAjax handler: ", this);
	else {
		this.ajaxUrl = this.settings.editor.ajaxUrl;
	}
}

MappingAjax.prototype.call = function(command, arguments, callback) {
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: $.extend({}, { command: command }, arguments),
		success: function(response) {
			if(callback != undefined) callback(response);
		}
	});
}

MappingAjax.prototype.find = function(arguments, callback) {
	this.call("find", arguments, callback);
}

/**
 * Remove a model's node with specified id, If callback exists, call on success with response.
 * @param {String} id id of node that should be removed.
 * @param {function} [callback] Callback that should be called on success.
 */
MappingAjax.prototype.removeNode = function(id, callback) {
	this.call("removeNode", { id: id }, callback);
}

/**
 * Duplicate a model's node with specified id, If callback exists, call on success with response.
 * @param {String} [id] id of node that should be duplicated.
 * @param {function} [callback] Callback that should be called on success.
 */
MappingAjax.prototype.duplicateNode = function(id, settings, callback) {
	$.ajax({
		url: this.ajaxUrl,
		context: this,
		dataType: "json",
		data: $.extend({}, { command: "duplicateNode" }, {id: id}),
		success: function(response) {
			if(callback != undefined) 
				callback(response, settings);
		}
	});
}

MappingAjax.prototype.setConstantValueMapping = function(arguments, callback) {
	this.call("setConstantValueMapping", $.extend({}, arguments, {
		value: encodeURIComponent(arguments.value),
		annotation: (arguments.annotation != undefined) ? encodeURIComponent(arguments.annotation) : undefined,
	}), callback);
}

MappingAjax.prototype.removeMapping = function(arguments, callback) {
	this.call("removeMapping", arguments, callback);
}

MappingAjax.prototype.removeStructuralMapping = function(arguments, callback) {
	this.call("removeStructuralMapping", arguments, callback);
}