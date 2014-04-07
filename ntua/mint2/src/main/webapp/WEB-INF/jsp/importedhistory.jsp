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
	    $('#chart').html('<span><img src="images/buttons/loading.gif" alt=""></span>');
	    $.ajax({
	    type:'GET',
	    url:"UrlApi?isApi=true&action=list&type=DataUpload",
	  	data:"organizationId="+<%=request.getAttribute("getOrganizationId()")%>,
	    success:function(data) {
	    	console.log(data);
	    var sets = [];
	    
	    var headers = ["Name","Date","Items","Invalid items"];    
	    sets.push(headers);	
	    for(var i=0; i<data.result.length; i++)
	    {
	    	var name = data.result[i].name;
	        var date = new Date(data.result[i].lastModified);
	        var items = 0;
	        if (data.result[i].itemCount != -1) {
	        	items = data.result[i].itemCount;
	        }
	        var invalid = 0;
	        if (data.result[i].invalidItems != -1) {
	        	invalid = data.result[i].itemCount;
	        }
	       
	        sets.push([name, date,items,invalid]);
	        console.log(sets);
	    }   
	
	    var data = google.visualization.arrayToDataTable(sets);
			
		var options = {'title': 'Imports : <%=request.getAttribute("getName()")%>','width' : 700,'height' : 300};
	    
	    var table = new google.visualization.Table(document.getElementById('chart'));
	    table.draw(data, options);
	    }
	    });	    
	    }
      
	 })
    </script>
</head>


<div class="panel-body">

<!-- 	<div class="block-nav">
 -->	 	<div class="block-nav" style="margin-top: 20px;padding:20px;display: none;">
	
		<div class="summary">
			<div class="label">
			<%=request.getAttribute("getName()")%></div>
		</div>
		<div class="info"><br/></div>
		 </div>
		<h3>Imports</h3>	
		<div id="chart"></div>
       		
	</div>
</div>
