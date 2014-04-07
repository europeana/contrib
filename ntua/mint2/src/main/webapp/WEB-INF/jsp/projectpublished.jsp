<head>
<!--Load the AJAX API-->

<script type="text/javascript">
     $(function(){
    	 var self = this;
    	 var content = $('#chart');
    	 var panel = content.closest('div[id^="kp"]');
    	 $K.kaiten('maximize', panel);
    	 drawChart()
         function drawChart()
         {
      //   $('#chart').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
          $.ajax({
         type:'GET',
         url:"UrlApi?isApi=true&action=list&type=Transformation",
         success:function(data) { 
         var sets = [];
         //var headers = ["Organization","Parent Dataset Name","Publication Date","Published Items","Invalid Items","Target Schema","Mapping"];
         var headers = ["Organization","Publication Date","Items ","Mapping"];
         sets.push(headers);
         for(var i=0; i<data.result.length; i++)
         {
        	    var organization = data.result[i].organization.name;
        	// 	var parent = data.result[i].parentDataset;
        	//    var date = new Date(data.result[i].publicationEndDate);
        	    var date = new Date(data.result[i].lastModified);
        	//    var items = data.result[i].validItems;
        	    var items =  data.result[i].itemCount;
        //	    var target = data.result[i].targetSchema;
        	    var mapping = data.result[i].mappingUsed;
        	    sets.push([organization, date,items,mapping,]);
        
   
         }   
  
         var data = google.visualization.arrayToDataTable(sets);
   
            var categoryPicker = new google.visualization.ControlWrapper({
              'controlType': 'CategoryFilter',
              'containerId': 'control1',
              'options': {
                'filterColumnLabel': 'Organization',
                'ui': {
                'labelStacking': 'vertical',
                  'allowTyping': false,
                  'allowMultiple': false
                }
              }
            });
            
            var categoryPicker2 = new google.visualization.ControlWrapper({
              'controlType': 'CategoryFilter',
              'containerId': 'control2',
              'options': {
                'filterColumnLabel': 'Mapping',
                'ui': {
                'labelStacking': 'vertical',
                  'allowTyping': false,
                  'allowMultiple': false
                }
              }
            });
          
            // Define a Pie chart
            var pie = new google.visualization.ChartWrapper({
              'chartType': 'ColumnChart',
              'containerId': 'chart1',
              'options': {
                'width': 300,
                'height': 300,
                'legend': 'none',
                'title': 'Invalid items',
                'chartArea': {'left': 15, 'top': 15, 'right': 0, 'bottom': 0},
                'pieSliceText': 'Mapping'
              },
              // Instruct the piechart to use colums 0 (Name) and 3 (Donuts Eaten)
              // from the 'data' DataTable.
              'view': {'columns': [0,2]}
            });
          
            // Define a table
            var table = new google.visualization.ChartWrapper({
              'chartType': 'Table',
              'containerId': 'chart2',
              'options': {
                'width': '300px'
              }
            });
            
            
            var grouped_dt = google.visualization.data.group(
          	      data, [0],[{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'}]);
            
            var grouped_table = new google.visualization.ColumnChart(document.getElementById('grouped_table'));
            grouped_table.draw(grouped_dt);
          
            // Create a dashboard
            new google.visualization.Dashboard(document.getElementById('dashboard')).
                // Establish bindings, declaring the both the slider and the category
                // picker will drive both charts.
                bind([categoryPicker,categoryPicker2], [pie, grouped_table]).
                // Draw the entire dashboard.
                draw(grouped_dt);
     
     //	var piechart = new google.visualization.PieChart(document.getElementById('chart1'));
         

     	
        /* var table = new google.visualization.Table(document.getElementById('chart'));
        table.draw(data, options);
        
        var grouped_dt = google.visualization.data.group(
        	      data, [0],[{'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'}]);
        
        var grouped_table = new google.visualization.ColumnChart(document.getElementById('grouped_table'));
        grouped_table.draw(grouped_dt, options);
         */
        }
        });   
        }     
     })
    </script>
</head>


<div class="panel-body" style="height: 100%; width: 100%">

	<div class="block-nav"  style="height: 100%; width: 100%" >
		<div class="summary">
			<div class="label">
			
			<%=request.getAttribute("getName()")%>
		</div>
		<div class="info"><br/></div>
		 </div>
		<h3>Publications</h3>	
		<div id="dashboard">
      <table>
        <tr style='vertical-align: top'>
          <td style='width: 400px; font-size: 0.9em;'>
            <div id="control1"></div>
            <div id="control2"></div>
            <div id="control3"></div>
          </td>
           <td style='width: 800px'>
             <div style="float: left;" id="chart1"></div>
                <div style="float: left;" id="chart3"></div>
				<div style="float: left;" id="chart2"></div>
				<div style="float: left;" id="grouped_table"></div>
				
          </td>
          
        </tr>
      </table>
    </div>
        <!--     <div style="float: left;" id="chart2" style="height: 100%; width: 100%"></div> -->
		
		
		<h3>Published items per schema</h3>	
	</div>
</div>
