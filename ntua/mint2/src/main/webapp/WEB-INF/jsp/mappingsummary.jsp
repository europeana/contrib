<%@ include file="_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%> 

<div class="panel-body">
 <div class="block-nav">
	
	<div class="summary">
	<div class="label">
    Mappings</div>  
    
    <div class="info">
		<span style=";position:relative;"><img src="images/locked.png" title="locked mappings" style="width:12px;"/> <font>Locked mappings ,&nbsp;</font></span>
		<span style="position:absolute;height:30px;"><img src="images/shared.png" title="shared mappings" style="width:15px;"/> <font>Shared mappings</font></span>
		
		<s:if test="hasActionErrors()">
		<s:iterator value="errorMessages">
			
             <div class="info"><font color="red"><s:property escape="false" /> </font> </div>
        
		</s:iterator>
	    </s:if>	
	</div>
	
	</div>
	<s:if test="!hasActionErrors()">
		<div title="Create new mapping" data-load='{"kConnector":"html.page", "url":"NewMapping_input.action?selaction=createschemanew&uploadId=<s:property value="uploadId"/>&userId=<s:property value="user.dbID"/>", "kTitle":"New mapping" }' class="items navigable">
			<div class="label">Create new mapping</div>
			<div class="tail"></div>
		</div>
		
		<div title="Upload mapping" data-load='{"kConnector":"html.page", "url":"NewMapping_input.action?selaction=uploadmapping&uploadId=<s:property value="uploadId"/>&userId=<s:property value="user.dbID"/>", "kTitle":"Upload mapping"  }' class="items navigable">
			<div class="label">Upload mapping</div> 
			<div class="tail"></div>
		</div>

		<div title="Upload XSL" data-load='{"kConnector":"html.page", "url":"NewMapping_input.action?selaction=uploadxsl&uploadId=<s:property value="uploadId"/>&userId=<s:property value="user.dbID"/>", "kTitle":"Upload XSL"  }' class="items navigable">
			<div class="label">Upload XSL</div> 
			<div class="tail"></div>
		</div>

		<div class="summary">      
		</div>      
		<s:if test="organizations.size()>0">
			<div style="display:block;padding:5px 0 0 5px;background:#F2F2F2;border-bottom:1px solid #CCCCCC;">
				<span style="width:150px;display:inline-block"><b>Filter by Organization: </b></span><s:select theme="simple"  cssStyle="width:200px"  name="filtermaporg"  id="filtermaporg" vaue="${orgId}" list="organizations" listKey="dbID" listValue="name" headerKey="-1" headerValue="-- All mappings --" onChange="javascript:ajaxMappingPanel(0,$('#filtermaporg').val(),${uploadId},${user.dbID});"></s:select>
			</div>
		</s:if>
		
		
      
     
      
     <s:if test="recentMappings.size>0">
        <div id="mappings-panel-recent-mappings"  style="padding: 0">
		<div class="summary"><div class="label">Relevant Mappings</div></div>
        <s:set var="lastOrg" value=""/>
         <s:iterator id="smap" value="recentMappings">
	         <s:set var="current" value="organization.dbID"/>
	         <s:if test="#current!=#lastOrg">
	                <div class="items separator">
	               
	                
					
					<div class="head">
					  <img src="images/museum-icon.png" width="25" height="25" style="left:1px;top:4px;position:absolute;max-width:25px;max-height:25px;"/>
					</div>
					
					<div class="label">Organization: <s:property value="organization.name"/></div>
					
					<div class="info"></div>
					
					</div>
					<s:set var="lastOrg" value="#current"/>
	         
	         </s:if>
	         
			<div title="<s:property value="name"/>" 
			onclick=" javascript:
			
			if(mapurl.indexOf('Transform.action')==-1){
			    var cp=$($(this).closest('div[id^=kp]'));$(cp).find('div.k-active').removeClass('k-active'); $(this).toggleClass('k-active');
				var loaddata={kConnector:'html.page', url:mapurl+'uploadId=<s:property value="uploadId"/>&selectedMapping=<s:property value="dbID"/>', kTitle:'Mapping options' };
           		$K.kaiten('removeChildren',cp, false);$K.kaiten('load', loaddata);}else{
           		importTransform(<s:property value="uploadId"/>,<s:property value="dbID"/>,<%=request.getParameter("orgId")%>);
           		}" 
			 class="items navigable">
			          	
			 				<div class="label" style="width:80%">						
							<s:property value="name"/> <font style="font-size:0.9em;margin-left:5px;color:grey;">(<s:property value="targetSchema"/>)</font></div>
							
							<s:if test="xsl==true"><span style="color:#a00">XSL</span></s:if>
							<div class="info">
							<s:if test="isLocked(user, sessionId)">
							<img src="images/locked.png" title="locked mappings" style="top:4px;position:relative;max-width:18px;max-height:18px;padding-right:4px;">
							</s:if>
							<s:if test="isShared()">
							<img src="images/shared.png" title="shared mappings" style="top:4px;position:relative;max-width:18px;max-height:18px;">
							</s:if>
							</div>
							<div class="tail"></div>
						</div>		
		</s:iterator>
		
       </div>
       </s:if>
     
     <div class="summary">
	</div>

		<div id="mappings-panel-mappings" style="margin-top: 10px">

        
       
       <br/>
       
       <div class="summary"><div class="label">All Mappings</div></div>
       
        <div class="mappings_pagination"></div>
        <div id="mappingPanel">
       <script>ajaxMappingPanel(0,${orgId},${uploadId},${user.dbID});</script>
       </div>
        <div class="mappings_pagination"></div>
        
       
	 
    
    </div>
     </s:if>
     
     </div>
</div>


<script type="text/javascript">
$(document).ready(function() {
	/* use org id of import to show appropriate mappings*/
  <%if(request.getParameter("orgId")!=null){%>
    mapping_oid=<%=request.getParameter("orgId")%>;
    $('#filtermaporg').val(mapping_oid);
  <%}%>
});
</script>

