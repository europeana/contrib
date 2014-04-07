<%@ include file="_include.jsp"%>

<script type="text/javascript"><!--
	$(document).ready(function() {
	    var annotatorPanel = $('div[id^="kp"]:last');
		var hideGroupAnnotator =
		<%= Config.getBoolean("ui.hide.groupAnnotator")%>;//?"true":"false";%>
		_annotator = new XMLAnnotator("annotator");
		_annotator.init(
			"<%= request.getAttribute("uploadId") %>",
			hideGroupAnnotator
		);	
	});

</script>
		
<div class="panel-body"  style="height: 100%; width: 100%">
	<div class="block-nav"  style="height: 100%; width: 100%">
	
	<div id="annotator" style="position: absolute; top: 0; bottom: 0; left: 0; right: 0; min-height: 500px">
	</div>

	</div>
</div>