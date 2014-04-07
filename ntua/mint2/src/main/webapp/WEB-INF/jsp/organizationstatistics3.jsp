<head>

<%@ taglib prefix="s" uri="/struts-tags" %>


	
<script type="text/javascript">
    

      
     $(function(){
    	 var self = this;
    	 var content = $('#chart_div');
    	 var panel = content.closest('div[id^="kp"]');
    	 $K.kaiten('maximize', panel);
 
			drawBarChart()
		
	  

 	function drawBarChart()
    {

     $('#chart_div').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
     $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

     
     
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
    success:function(data) {

			  
			/*   var cdata = google.visualization.arrayToDataTable([
			 ['Organization', 'Imported', 'Transformed', 'Published', 'Published to EUscreenXL-EDM','Published to ItemClip v2','Published to Series v2','Aggregation Goal','Core Goal'],
			 [data.result.Name, data.result.Imported , data.result.Transformed , data.result.Published , data.result.PublishedtoEDM ,data.result.PublishedtoItemClipv2,data.result.PublishedtoSeriesv2,data.result.AggregationTarget,data.result.CoreTarget]
			                                                    
			 ]);
			 */
			 result = data.result
			 keys = Object.keys(result);
			 
			/*  var cdata = {};
			 
			 var cols = []];
			 
			 var rows = []; */
			 
			 var cdata = new google.visualization.DataTable();
			 
			 for (var key in keys){
				 console.log(keys[key]);
				 cdata.addColumn('string',keys[key]);
			 }
			 var row = [];
			 for (var key in keys){
				 x = keys[key];
				 console.log(x);
				 row.push(result[x]);
			 }
			 console.log(row);
			 /* cdata.addRow(row); */


    		  var options = {'title':'Items counted : <%=request.getAttribute("getName()")%>'}

    		  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
			  chart.draw(cdata, options);
			  var chart = new google.visualization.Table(document.getElementById('chart_1'));
			  chart.draw(cdata, options);
 			 } 
    								
       
    
    });
    }
     })
    </script>

  
</head>

<div class="panel-body"  style="height: 100%; width: 100%">
	<div class="block-nav"  style="height: 100%; width: 100%">
		<div class="summary">
			<div class="label">
			<%=request.getAttribute("getName()")%></div>
			<div class="info"><br/></div>
		</div>
		
		<h3>Overall</h3>
		<div id="chart_div"  style="height: 50%; width: 100%"></div>
			<div id="chart_1"  style="height: 50%; width: 100%"></div>
       		
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>