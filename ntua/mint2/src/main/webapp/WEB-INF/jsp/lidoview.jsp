<%@ page import="gr.ntua.ivml.mint.util.Config" %>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id="theContent" class="java.lang.String" scope="request" ></jsp:useBean>

<%
%>

<div style="display: none">
	<form id="iframe-preview-form" method="post" action="http://lido.ubitech.eu:8080/lidoviewer/jsfs/lidoviewer.jsf" target="lidoview">
		<input type="hidden" name="xmlData" id="xmlData" value='<% out.println(java.net.URLEncoder.encode(theContent.trim(), "UTF-8")); %>'>
	</form>
</div>

<iframe name="lidoview" src="" style="width: 100%; height: 100%">
</iframe>