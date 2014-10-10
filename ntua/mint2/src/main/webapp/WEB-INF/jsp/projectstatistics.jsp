
<head>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="@gr.ntua.ivml.mint.util.Config@has('orgtargets')">
<script type="text/javascript">
    
 
     $(function(){
    	 var self = this;
    	 var content = $('#chart_1');
    	 var panel = content.closest('div[id^="kp"]');
    	 $K.kaiten('maximize', panel);
		drawBarChart()  
     
 function drawBarChart()
    {
    $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
    $('#chart_2').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

    $('#chart_3').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  <%-- 	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>, --%>
     success:function(data) {
    	console.log(data);
   
    var sets = [];
    
    var headers = ["Organization","Country","Imported","Transformed","Published","Published to Euscreenxl-EDM","Published to ItemClip v2","Published to Series v2","Aggregtaion Goal","Core Goal"];
    sets.push(headers);
    for(var i=0; i<data.result.length; i++)
    {
    var name = data.result[i].Name;
    var country = data.result[i].Country;
    var imported = data.result[i].Imported;
    var transformed = data.result[i].Transformed;
    var published = data.result[i].Published;
    var publishedEDM = data.result[i].PublishedtoEDM;
    var publishedItemClip = data.result[i].PublishedtoItemClipv2;
    var publishedSeries = data.result[i].PublishedtoSeriesv2;
    var aggregationtarget = data.result[i].AggregationTarget;
    var coretarget = data.result[i].CoreTarget;
    sets.push([name, country,imported,transformed,published,publishedEDM,publishedItemClip,publishedSeries,aggregationtarget,coretarget]);
    console.log(sets);
    }   

    var data = google.visualization.arrayToDataTable(sets);
	
	var options = {'title': 'Project <%=request.getAttribute("getName()")%>'};
    var table = new google.visualization.Table(document.getElementById('chart_1'));
    table.draw(data, options);
   
    var dataView2 = new google.visualization.DataView(data);
    var dataView1 = new google.visualization.DataView(data);
    dataView1.setColumns([0, 2]);
    options = {'title': 'Imported Items','width' : 500,'height' : 250};
    var chart = new google.visualization.PieChart(document.getElementById('chart_2'));
    chart.draw(dataView1, options);

    var chart2 = new google.visualization.PieChart(document.getElementById('chart_3'));
    dataView2.setColumns([0, 4]);
    options = {'title': 'Published  Items','width' : 500,'height' : 250};
    chart2.draw(dataView2, options);
    
    

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
    	 var content = $('#chart_1');
    	 var panel = content.closest('div[id^="kp"]');
    	 $K.kaiten('maximize', panel);
		drawBarChart()  
     
 function drawBarChart()
    {
    $('#chart_1').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
    $('#chart_2').html('<span><img src="images/buttons/loading.gif" alt=""></span>');

    $('#chart_3').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
    $.ajax({
    type:'GET',
    url:"OrganizationStat.action",
  <%-- 	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>, --%>
     success:function(data) {
    	console.log(data);
   
    var sets = [];
    
    var headers = ["Organization","Country","Imported","Transformed","Published"];
    sets.push(headers);
    for(var i=0; i<data.result.length; i++)
    {
    var name = data.result[i].Name;
    var country = data.result[i].Country;
    var imported = data.result[i].Imported;
    var transformed = data.result[i].Transformed;
    var published = data.result[i].Published;
   
    sets.push([name, country,imported,transformed,published]);
    console.log(sets);
    }   

    var data = google.visualization.arrayToDataTable(sets);
	
	var options = {'title': 'Project <%=request.getAttribute("getName()")%>'};
    var table = new google.visualization.Table(document.getElementById('chart_1'));
    table.draw(data, options);
   
    var dataView2 = new google.visualization.DataView(data);
    var dataView1 = new google.visualization.DataView(data);
    dataView1.setColumns([0, 2]);
    options = {'title': 'Imported Items','width' : 500,'height' : 250};
    var chart = new google.visualization.PieChart(document.getElementById('chart_2'));
    chart.draw(dataView1, options);

    var chart2 = new google.visualization.PieChart(document.getElementById('chart_3'));
    dataView2.setColumns([0, 4]);
    options = {'title': 'Published  Items','width' : 500,'height' : 250};
    chart2.draw(dataView2, options);
    
    

    }								   
    });
    }
     
     })

   
    
    </script>

</s:else>

</head>



<div class="panel-body">



	<div class="block-nav">
		<div class="summary">
			<div class="label"><%=request.getAttribute("getName()")%></div>
			<div class="info"><br/></div>
		 </div>
		 
		 <div style="width: 100%; overflow: hidden;">
   		 <div id="chart_3" style="width: 600px; float: left;"> Left </div>
  		  <div id="chart_2" style="margin-left: 620px;"> Right </div>
		</div>
		<!--  <h3>Published</h3>
		<div id="chart_3"></div>
		 <h3>Imported</h3>
		<div id="chart_2"></div> -->
		<h3>Overall</h3>	
		<div id="chart_1"style="height: 100%; width: 100%"></div>
		
		

	</div>
</div>

