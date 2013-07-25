$(function() {
	$("#search-term").keyup(function(event){
	  if(event.keyCode == 13){
		  europeana_search();
	  }
	});
	
});

function europeana_search(page) {
	var thePage = page;
	if(thePage == undefined) {
		thePage = 1;
	}
	
	var term = $("#search-term").val();
	var type = $("#search-type").val();
	europeana_search_term(thePage,type, term);
}

function europeana_search_term(page,type, term)
{
	if(term == undefined) term = current_term;
	if(type == undefined) type = current_type;
	current_page = page;
	current_term = term;
	current_type=type;
	$("#results").empty();
	$("#results").text("Loading...");
	$.ajax({
		contentType : "application/json",
        dataType : 'json',
        data: '{"pageNumber":'+page+',"term":"'+term+'","type":"'+type+'"}',
		type : "POST",
		url: "../EuropeanaSearch/search",
		success: function(results) {
			console.log(results);
			var items = $("#results");

			if(results != undefined && results.totalResults != undefined && results.totalResults > 0) {
				var list=$("<div>");
				var preview = $("<div>").attr("id", "preview").appendTo(list);
				var listitems = $("<div>").attr("id", "items").appendTo(list);
				
				preview.append($("<div>").addClass("europeana-preview-title"));
                preview.hide();
				items.empty();
			
				items.append(pagination(results));
				items.append(list);
			
				renderList(listitems, results.items);
			} else {
				items.empty();
				items.append($("<div>").attr("id", "not-found").html("Search term '<span id=\"not-found-term\">" + term + "</span>' was not found in any document of that type"));
			}
		},
		error: function(result) {
			$("#results").text('Status:'+result.status+', Error: ' +result.responseText);
             
     }
	});
}

function renderList(container, list) {
	for(var i in list) {
		container.append(resultElement(list[i]));
	}			
	
	imagePreview();
}


function pagination(results) {
	var element = $("<div>").addClass("europeana-pagination");
	
	element.append($("<span>").text("Pages: "));
	
	if(current_page > 1) {
		element.append($("<a>").attr("href", "javascript:europeana_search(" + (current_page-1) + ")").text("Previous"));
	}
	
	var maxPages = results.totalResults / 6;
	console.log("maxPages:"+maxPages);
	maxPages=Math.round(maxPages);
	
	var lower = (current_page <= 6)?1:current_page-6;
	var higher = (current_page+6>=maxPages)?maxPages:current_page+6;
	for(var p = lower; p <= higher; p++) {
		if(p!=current_page)
		element.append($("<a>").attr("href", "javascript:europeana_search(" + (p) + ")").text(p));
		else{element.append($("<span>").css("padding-left","+=10").text(p));}
	}
	
	if(current_page < maxPages) {
		element.append($("<a>").attr("href", "javascript:europeana_search(" + (current_page+	1) + ")").text("Next"));
	}
		
	return element;
}

function resultElement(item) {
	var wrapper = $("<div>").addClass("li");

	var thumbnail = $("<div class='thumb-frame'>").addClass("europeana-small-thumbnail").append($("<img>").attr("src", item.url)).appendTo(wrapper);
	
	var it = item;
	var link = $("<div>").text(item.title).appendTo(wrapper);
	link.addClass("preview");
	link.data("item", item);

	return wrapper;
}

function imagePreview() {
	$(".preview").click(
		// on hover
			function(e) {
			var data = $(this).data("item");
			var preview = $("#preview").empty();
				
			$("#preview").hide();
			var title = $("<div>")
			.addClass("europeana-preview-title")
			.append($("<a>").text(data.title).attr("href", data.source).attr("target", "_blank"))
			.appendTo(preview);
			
			var url = $("<div>")
			.addClass("europeana-preview-thumbnail")
			.append($("<img>").attr("src", data.url))
			.appendTo(preview);
						
			
			
			var datatype = $("<div>")
			.addClass("europeana-preview-description")
			.text("Type: "+data.type)
			.appendTo(preview);
			
			if(data.provider!=null)
			var provider = $("<div>")
			.addClass("europeana-preview-description")
			.text("Provider: "+data.provider)
			.appendTo(preview);
			
			if(data.dataProvider!=null)
			var dataprovider = $("<div>")
			.addClass("europeana-preview-description")
			.text("Data Provider: "+data.dataProvider)
			.appendTo(preview);
	
			if(data.dcCreator!=null)
			var dcCreator = $("<div>")
			.addClass("europeana-preview-description")
			.text("creator: "+data.dcCreator)
			.appendTo(preview);
	
			
			if(data.language!=null)
			var language = $("<div>")
			.addClass("europeana-preview-description")
			.text("Language: "+data.language)
			.appendTo(preview);
			
			if(data.rights!=null)
			var rights = $("<div>")
			.addClass("europeana-preview-description")
			.text("Rights: "+data.license)
			.appendTo(preview);
			
			var source = $("<div>")
			.addClass("europeana-preview-description")
			.text("View item at (source): "+data.source)
			.appendTo(preview);
			
			$("#preview").show();		

				
			
						
			}	
				
	);
}