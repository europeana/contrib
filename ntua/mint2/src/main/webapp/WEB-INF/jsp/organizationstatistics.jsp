<%@ include file="_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%>

<s:set var="orgId" value="filterOrg" />
<s:if test="orgid==-1">
	<s:set var="orgId" value="user.dbID" />
</s:if>

<div class="panel-body">
	<div class="block-nav">
		<div class="summary">
			<div class="label"></div>
			<% if( request.getAttribute( "actionmessage" ) != null ) {  %>
			<div class="info">
				<div class="errorMessage"></div>
				<%=(String) request.getAttribute( "actionmessage" )%></div>
			<%}else{%>
			<div class="info">Organization Statistics:</div>
			<%} %>
		</div>

		<div
			style="display: block; padding: 5px 0 0 5px; background: #F2F2F2; border-bottom: 1px solid #CCCCCC;">
			<span style="width: 150px; display: inline-block"></span>
			<s:select theme="simple" cssStyle="width:200px" name="filterorg"
				id="filterorg"  list="allOrgs"
				listKey="dbID" listValue="name" value="orgId" onChange="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'OrgItemStats.action?organizationId='+$('#filterorg').val(), kTitle:'Organization Statistics' });"></s:select>
		</div>
	 
	 <div title="Organization Statistics"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'OrgItemStats.action?organizationId='+$('#filterorg').val(), kTitle:'Organization Statistics' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<b>Organization Statistics</b>
		</div>
	 
	 
	
	 
</div>
<script type="text/javascript">
jQuery(document).ready(function(){
	$("#filterorg").chosen();
});




</script>
