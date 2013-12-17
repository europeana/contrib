<%@ include file="_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="gr.ntua.ivml.mint.persistent.Organization"%>
<%@page import="gr.ntua.ivml.mint.persistent.XmlSchema"%>
<%@page import="java.util.List"%>


 
<script type="text/javascript">



$(function() {

	$("#submit_import").click(function() {
			var p=$('div[id^="kp"]:last');
			
			var querystring =$('form[name=createDatasetForm]').serialize();
			$.ajax({
			   	 url: "CreateDataset.action",
			   	 type: "POST",
			   	 data: querystring,
			     error: function(){
			   		alert("An error occured. Please try again.");
			   		},
			   	 success: function(response){
			   		var cp=p.find('div.panel-body');
			   		 cp.html(response);
				   	  }
			   	});
		})
});


</script>

<div class="panel-body">

	<div class="block-nav">
	<div class="summary">
	<div class="label">Create empty dataset</div>
	<div id="info">
	<s:if test="hasActionErrors()">
	<s:iterator value="actionErrors">
				<font style="color:red;"><s:property escape="false" /> </font>
	</s:iterator>
	</s:if>
    </div>
	</div>


<s:form name="createDatasetForm" action="CreateDataset" cssClass="athform" theme="mytheme"
	enctype="multipart/form-data" method="POST" onSubmit="return false">

		<%List<XmlSchema> l= DB.getXmlSchemaDAO().findAll();
		%>
	     <div class="fitem">
	     <label>Name the dataset </label><s:textfield name="nameDataset" cssStyle="width: 100px;" />
		</div>

		<div class="fitem">	
			<label>Define the schema for this dataset:</label>
			 <select name="schemaId">
			 <option value="0">--Select schema--</option>
			 <%for(XmlSchema i:l){ %>
			 <option value="<%=i.getDbID() %>"><%=i.getName() %></option>
			 <%} %>
			 </select> 
		</div>

	    <s:if test="%{user.accessibleOrganizations.size>1}">
			<div class="fitem"><label>Create for Organization</label><s:select name="orgId"
				headerKey="0" headerValue="-- Which Organization --"
				list="user.accessibleOrganizations" listKey="dbID" listValue="name" value="user.organization.dbID"
				required="true"/> </div>
		</s:if>
	    
	    <p align="left">
				<a class="navigable focus k-focus" id="submit_import">
					 <span>Submit</span></a>  
	     </p>


</s:form>
<script type="text/javascript">

<%if(request.getParameter("mth")!=null){%>
    var mthr=document.getElementsByName('mth');
	for (var i=0; i<mthr.length; i++)  {
		if (mthr[i].checked)  {
		
		mthr[i].disabled=false;
		mthr[i].click();
		}
	} 
<%}%>
</script>
</div>
</div>


