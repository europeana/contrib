<%@ include file="../../../jsp/_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%>
<script type="text/javascript">

$(function() {
	var title = '<s:property value="title"/>';
	if( title != "Error") {
		Mint2.dialog( Mint2.message( '<s:property value="message"/>', Mint2.OK), title );
	} else {
		Mint2.dialog( Mint2.message( '<s:property value="message"/>', Mint2.ERROR), title );
	}
var reloadPanel = $("#publishResponse").closest( ".k-panel").prev();
$K.kaiten( "reload", reloadPanel );
// $K.kaiten('load', { kConnector:"html.string", html : msghtml, title:title });
})

</script>
<div  id="publishResponse" class="panel-body">

	<div class="block-nav">
	<div class="summary">
	</div>
	</div>
	</div>
	
	