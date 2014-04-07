<%@ include file="_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%> 

   <s:set var="orgId" value="filterOrg"/>
    <s:if test="orgid==-1">
    <s:set var="orgId" value="user.dbID"/>
    </s:if>   

<div class="panel-body">
 <div class="block-nav">
 	<div class="summary">
	<div class="label">
   </div>  
    <% if( request.getAttribute( "actionmessage" ) != null ) {  %>
		<div class="info"><div class="errorMessage"></div>
		<%=(String) request.getAttribute( "actionmessage" )%></div>
      <%}else{%>
    <div class="info">
		View organization and project statistics:
	</div><%} %>
	</div>
	
    <div
			style="display: block; padding: 5px 0 0 5px; background: #F2F2F2; border-bottom: 1px solid #CCCCCC;">
			<span style="width: 150px; display: inline-block"></span>
			<s:select theme="simple" cssStyle="width:200px" name="filterorg"
				id="filterorg"  list="allOrgs"
				listKey="dbID" listValue="name" value="orgId"></s:select>
		</div>
	 
	 
	<div title="Organization overall"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'OrgItemStats.action?organizationId='+$('#filterorg').val(), kTitle:'Organization statistics' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Organization overall</div>
			<div class="tail"></div>
		</div>
	 
	 
	 <!-- <div title="Organization overall"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'OrgItemStats.action?organizationId='+$('#filterorg').val(), kTitle:'Organization Statistics' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Organization overall</div>
			<div class="tail"></div>
		</div> -->
		
		 <div title="Imports history"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'ImportedHistory.action?organizationId='+$('#filterorg').val(), kTitle:'Imports history' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Imports history</div>
			<div class="tail"></div>
		</div>

 	<div title="Transformations history"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'TransformedHistory.action?organizationId='+$('#filterorg').val(), kTitle:'Transformations history' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Transformations history</div>
			<div class="tail"></div>
		</div>
		
		<div title="Publications history"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'PublishedHistory.action?organizationId='+$('#filterorg').val(), kTitle:'Publications history' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Publications history </div>
			<div class="tail"></div>
		</div>
	<!-- 	<div title="OAI Status"
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'OaiStatus.action?organizationId='+$('#filterorg').val(), kTitle:'OAI Status' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">OAI Status </div>
			<div class="tail"></div>
		</div> -->		
	
		
	<div title="Project overall statistics "
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'ProjectStatistics.action', kTitle:'Project overall statistics' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Project overall statistics </div>
			<div class="tail"></div>
		</div>	

 <!-- 
	<div title="Project publication statistics "
			onclick="var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active');$(this).toggleClass('k-active');$K.kaiten('removeChildren',cp, false);$K.kaiten('load',{kConnector:'html.page', url:'ProjectPublications.action', kTitle:'Project publications statistics' });"
			class="items navigable" style="min-height:30px;height:auto;">
			<div class="label">Project publications statistics </div>
			<div class="tail"></div>
	</div>	
  -->
	
	
<%-- 
     <%if(Config.getBoolean("mint.enableGoalReports") ||user.hasRight(User.SUPER_USER)){%>
    <div title="Organization Statistics" data-load='{"kConnector":"html.page", "url":"OrganizationStatistics", "kTitle":"Organization Statistics" }' class="items navigable">
 	<div class="label">Organization Statistics</div>
	<div class="tail"></div>
 -	</div>	
 	<%} %> --%>
 	
 <%-- <%if(user.hasRight(User.SUPER_USER)){%>
	<div title="Project Statistics" data-load='{"kConnector":"html.page", "url":"ProjectStatistics", "kTitle":"Project Statistics" }' class="items navigable">
	<!-- <div class="label">Project Statistics</div> -->
	<b>Project Statistics</b>
<!-- 	<div class="tail"></div> -->
    </div> 

<%}%>	 --%>
	
 
 
