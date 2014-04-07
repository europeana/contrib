<head>

<%@ taglib prefix="s" uri="/struts-tags" %>


	<s:if test="@gr.ntua.ivml.mint.util.Config@has('orgtargets')">
	
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

			  res = data.result;
			  var cdata = google.visualization.arrayToDataTable([
			 ['Organization', 'Imported', 'Transformed', 'Published', 'Published to EUscreenXL-EDM','Published to ItemClip v2','Published to Series v2','Aggregation Goal','Core Goal'],
			 [res.Name,res.Imported , res.Transformed , res.Published ,res["Published to EUscreenXL-EDM"],res["Published to EUscreenXL ITEM/CLIP v2"],res["Published to EUscreenXL Series v2"],res.AggregationTarget,res.CoreTarget]
			                                                    
			 ]);
			

    		  var options = {'title':'Items counted : <%=request.getAttribute("getName()")%>'}

    		  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
			  chart.draw(cdata, options);
			  var chart = new google.visualization.Table(document.getElementById('chart_1'));
			  chart.draw(cdata, options);
 			 } 
    								
       
    
    });
    }
 	
 	function drawBarChartNoTargets()
    {

     $('#chart_div').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
     $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

     
     
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
    success:function(data) {

			  
			  var cdata = google.visualization.arrayToDataTable([
			 ['Organization', 'Imported', 'Transformed', 'Published'],
			 ['<%=request.getAttribute("getName()")%>', data.result.Imported , data.result.Transformed , data.result.Published , ]
			                                                    
			 ]);
			

    		  var options = {'title':'Items for  Organization <%=request.getAttribute("getName()")%>'}

    		  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
			  chart.draw(cdata, options);
			  var chart = new google.visualization.Table(document.getElementById('chart_1'));
			  chart.draw(cdata, options);
 			 } 
    								
       
    
    });
    }
 	
 	
 	
     })
    </script>
    </s:if>
   	<s:else>
   		
<script type="text/javascript">
    

      
     $(function(){
    	 var self = this;
    	 var content = $('#chart_div');
    	 var panel = content.closest('div[id^="kp"]');
    	 $K.kaiten('maximize', panel);
 
			drawBarChartNoTargets()
		
	  

 	function drawBarChart()
    {

     $('#chart_div').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
     $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

     
     
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
    success:function(data) {
		  res = data.result;

			  
			  var cdata = google.visualization.arrayToDataTable([
			 ['Organization', 'Imported', 'Transformed', 'Published', 'Published to EUscreenXL-EDM','Published to ItemClip v2','Published to Series v2','Aggregation Goal','Core Goal'],
			 [res.Name,res.Imported , res.Transformed , res.Published ,res["Published to EUscreenXL-EDM"],res["Published to EUscreenXL ITEM/CLIP v2"],res["Published to EUscreenXL Series v2"],res.AggregationTarget,res.CoreTarget]
			                                                    
			 ]);
			

    		  var options = {'title':'Items for  Organization <%=request.getAttribute("getName()")%>'}

    		  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
			  chart.draw(cdata, options);
			  var chart = new google.visualization.Table(document.getElementById('chart_1'));
			  chart.draw(cdata, options);
 			 } 
    								
       
    
    });
    }
 	
 	function drawBarChartNoTargets()
    {

     $('#chart_div').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
     $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

     
     
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
    success:function(data) {

			  
			  var cdata = google.visualization.arrayToDataTable([
			 ['Organization', 'Imported', 'Transformed', 'Published'],
			 ['<%=request.getAttribute("getName()")%>', data.result.Imported , data.result.Transformed , data.result.Published , ]
			                                                    
			 ]);
			

    		  var options = {'title':'Items for  Organization <%=request.getAttribute("getName()")%>'}

    		  var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
			  chart.draw(cdata, options);
			  var chart = new google.visualization.Table(document.getElementById('chart_1'));
			  chart.draw(cdata, options);
 			 } 
    								
       
    
    });
    }
 	
 	
 	
     })
    </script>
	</s:else>

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
