<html>
	<head>
		<title>REST Record Operations</title>
		<script type="text/javascript">
			function URLencode(sStr) {
				return escape(sStr).replace(/\+/g, '%2B').replace(/\"/g,'%22')
				.replace(/\'/g, '%27').replace(/\//g, '%2F');
			}
			
			function setLabelUrlLocaltion(Location)
			{
				document.getElementById('URLLocation').innerHTML = Location;
			}
			
			function viewOperationsList() {
				var Location = '${pageContext.request.contextPath}/rest/records/'
				parent.frames['resultFrame'].location.href=Location;
				setLabelUrlLocaltion(Location);
			}
			
			function viewDataSources() {
		        var Location = '${pageContext.request.contextPath}/rest/records?operation=listDataSources';
		        parent.frames['resultFrame'].location.href=Location;
		        setLabelUrlLocaltion(Location);
			}
			
			function viewRecord() {
				if(document.getForm.recordId.value == "") {
				 	alert('Fill recordId');
				 	return;
				}
				
				var Location = '${pageContext.request.contextPath}/rest/records/' + URLencode(document.getForm.recordId.value);
				parent.frames['resultFrame'].location.href = Location
				setLabelUrlLocaltion(Location);
			}
		</script>
	</head>

	<body onload="javascript:setLabelUrlLocaltion()">
		<div class="testREST">
			<table width="100%"><tr>
				<td style="vertical-align:top">
					<fieldset>
						<legend>Retrieval Operations</legend>
						<form name="getForm" action="${pageContext.request.contextPath}/rest/records/" method="GET">
							<input type="hidden" name="operation" />
							
							<!-- DataSource Id: <input type="text" name="dataSourceId" value="" />  -->
							Record URN: <input type="text" name="recordId" />
				
							<br /><a href="javascript:viewOperationsList();">[View]</a> Operations List
							<br /><a href="javascript:viewDataSources();">[View]</a> Data Sources List
							<br /><a href="javascript:viewRecord();">[View]</a> Record [Fill Record URN]
						</form>
					</fieldset>
					
					<br /><br />
					
					<fieldset>
						<legend>Modification Operations</legend>
						<form action="${pageContext.request.contextPath}/rest/records/" target="resultFrame" method="POST">
							Operation:
							<select name="operation">
								<option value="">[Select Operation]</option>
								<option value="save">Save Record</option>
								<option value="delete">Delete Record (mark as deleted)</option>
								<option value="erase">Erase Record (remove permanently)</option>
							</select>
		
							<br />DataSourceId: <input type="text" name="dataSourceId" value="" />
							<br />RecordId: <input type="text" name="recordId" />
							<br />Record XML: <br /> <textarea rows="8" style="width: 450px;" name="record"></textarea>
							
							<br /><br /><input type="submit" name="submit" value="Submit" />
						</form>
					</fieldset>
				</td>
			
				<td style="vertical-align:top">
					<p ><span style="font-weight: bold;">URL [Retrieval Operations only]:</span> <span id="URLLocation"></span></p>
					<b>RESULT:</b>
					<div class="iframe">
						<iframe src="${pageContext.request.contextPath}/rest/records/" name="resultFrame" height="500px" width="700px"></iframe>
					</div>
				</td>
			</tr></table>
		</div>
	</body>
</html>
