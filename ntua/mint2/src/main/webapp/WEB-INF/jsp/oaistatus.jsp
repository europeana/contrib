<head>
<!--Load the AJAX API-->

<script type="text/javascript">
     $(function(){	 
    	 drawChart()
         function drawChart()
         {
         $('#chart').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
         $.ajax({
         type:'GET',
         url:"UrlApi?isApi=true&action=list&type=Publication",data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
         success:function(data) {
         	console.log(data);
         var sets = [];
         var headers = ["Parent Dataset Name","Date","Total Items","Valid Items","Invalid Items","Target Schema","Mapping"];
         sets.push(headers);
         for(var i=0; i<data.result.length; i++)
         {
        	 	var parent = data.result[i].parentDataset;
        	    var date = new Date(data.result[i].lastModified);
        	    var items = data.result[i].itemCount;
        	    var valid = data.result[i].validItems;
        	    var invalid = data.result[i].invalidItems;
        	    var target = data.result[i].targetSchema;
        	    var mapping = data.result[i].mappingUsed;
        	    sets.push([parent, date,items,valid,invalid,target,mapping]);
        	    console.log(sets);
         }   
        var data = google.visualization.arrayToDataTable(sets);
     	var options = {'title': 'Publications for organization <%=request.getAttribute("getName()")%>','width' : 700,'height' : 300};
         
        var table = new google.visualization.Table(document.getElementById('chart'));
        table.draw(data, options);
        
        var grouped_dt = google.visualization.data.group(
        	      data, [5],[{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'}]);
        
        var grouped_table = new google.visualization.ColumnChart(document.getElementById('grouped_table'));
        grouped_table.draw(grouped_dt, options);
        
        }
        });   
        }     
     })
    </script>
</head>


<div class="panel-body">

	<div class="block-nav">
		<div class="summary">
			<div class="label">
			
		</div>
		<div class="info"><br/></div>
		 </div>
		<h3>Publications History</h3>	
		<div id="chart"></div>
		<h3>Group by schema</h3>	
       <div id="grouped_table"></div>
	</div>
</div>
