<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
	<head>
		<title>
			<fmt:message key="dataProvider" />: ${actionBean.dataProvider.name} - 
			<c:choose>
		        <c:when test="${not actionBean.editing}"><fmt:message key="dataSource.create" /></c:when>
		        <c:otherwise><fmt:message key="dataSource.edit" /></c:otherwise>
		    </c:choose>
		</title>
		<script type="text/javascript">
			$(function() { $("input.numeric").numeric(); });

			function changeHarvestMethod() {
				var select_list = document.getElementById('selectMehtod');
				
				if(select_list.value != null && select_list.value == 'TimestampHarvester') {
					document.getElementById('timestampHarvester').style.display = '';
					document.getElementById('idListHarvester').style.display = 'none';
					document.getElementById('idSequenceHarvester').style.display = 'none';
				}
				else if(select_list.value != null && select_list.value == 'IdListHarvester') {
					document.getElementById('timestampHarvester').style.display = 'none';
					document.getElementById('idListHarvester').style.display = '';
					document.getElementById('idSequenceHarvester').style.display = 'none';
				}
				else if(select_list.value != null && select_list.value == 'IdSequenceHarvester') {
					document.getElementById('timestampHarvester').style.display = 'none';
					document.getElementById('idListHarvester').style.display = 'none';
					document.getElementById('idSequenceHarvester').style.display = '';
				}
				
			}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/dataProvider/CreateEditDataSourceZ3950.action" acceptcharset="UTF-8">
			<stripes:hidden name="dataProviderId" />
			<stripes:hidden name="dataSourceId" />
			<c:if test="${actionBean.editing}"><stripes:hidden name="editing" value="true" /></c:if>

			<table class="selectTable">
				<tr>
					<td class="selectCell">
				 		<c:import url="/jsp/dataProvider/dataSourceMenu.include.jsp" />
			       	</td>
					<td>
						<div class="headerDiv"><fmt:message key="dataSource" /></div>
						<p>
			    			<label for="target.address"><fmt:message key="dataSource.z3950.address" /><em>*</em> </label>
				    		<stripes:text name="target.address" />
				    	</p>
				    	<p>
			    			<label for="target.port"><fmt:message key="dataSource.z3950.port" /><em>*</em> </label>
				    		<stripes:text class="numeric" name="target.port" />
				    	</p>
				    	<p>
			    			<label for="target.database"><fmt:message key="dataSource.z3950.database" /><em>*</em> </label>
				    		<stripes:text name="target.database" />
				    	</p>
				    	<p>
			    			<label for="target.user"><fmt:message key="dataSource.z3950.user" /> </label>
				    		<stripes:text name="target.user" />
				    	</p>
				    	<p>
			    			<label for="target.password"><fmt:message key="dataSource.z3950.password" /> </label>
				    		<stripes:text name="target.password" />
				    	</p>
				    	<p>
			    			<label for="target.charset"><fmt:message key="dataSource.z3950.charset" /><em>*</em> </label>
				    		<stripes:text name="target.charset" />
				    	</p>
				    	<p>
			    			<label for="target.recordSyntax"><fmt:message key="dataSource.z3950.recordSyntax" /><em>*</em> </label>
			    			<select name="target.recordSyntax">
								<option value="unimarc">unimarc</option>
								<option value="usmarc">usmarc</option>
							</select>
				    	</p>
				    	
				    	<p>
			    			<label for="harvestMethodString"><fmt:message key="dataSource.z3950.harvestMethod" /><em>*</em> </label>
			    			<select id="selectMehtod" name="harvestMethodString" onchange="javascript:changeHarvestMethod()">
								<option value="TimestampHarvester"><fmt:message key="dataSource.z3950.timestamp" /></option>
								<option value="IdListHarvester"><fmt:message key="dataSource.z3950.idList" /></option>
								<option value="IdSequenceHarvester"><fmt:message key="dataSource.z3950.idSequence" /></option>
							</select>
		    			</p>
		    			
		    			<c:choose>
			    			<c:when test="${empty actionBean.harvestMethodString || actionBean.harvestMethodString == 'TimestampHarvester'}">
								<c:set var="timestampHarvesterDisplay" value="block" />
			    			</c:when>
			    			<c:otherwise><c:set var="timestampHarvesterDisplay" value="none" /></c:otherwise>
		    			</c:choose>
		    			<c:choose>
			    			<c:when test="${actionBean.harvestMethodString == 'IdListHarvester'}">
								<c:set var="idListHarvesterDisplay" value="block" />
			    			</c:when>
			    			<c:otherwise><c:set var="idListHarvesterDisplay" value="none" /></c:otherwise>
		    			</c:choose>
		    			<c:choose>
			    			<c:when test="${actionBean.harvestMethodString == 'IdSequenceHarvester'}">
								<c:set var="idSequenceHarvesterDisplay" value="block" />
			    			</c:when>
			    			<c:otherwise><c:set var="idSequenceHarvesterDisplay" value="none" /></c:otherwise>
		    			</c:choose>

						<span id="timestampHarvester" style="display: ${timestampHarvesterDisplay}">		
				    		<p>
				    			<label for="earliestTimestampString"><fmt:message key="dataSource.z3950.earliestTimestamp" /><em>*</em> </label>
					    		<stripes:text class="numeric" name="earliestTimestampString" />
					    	</p>
				    	</span>
						<span id="idListHarvester" style="display: ${idListHarvesterDisplay}">		
							<p>
				    			<label for="idListFile"><fmt:message key="dataSource.z3950.idListFile" /><em>*</em> </label>
				    			<stripes:file name="idListFile"/>
					    	</p>
				    	</span>
						<span id="idSequenceHarvester" style="display: ${idSequenceHarvesterDisplay}">		
							<p>
				    			<label for="maximumId"><fmt:message key="dataSource.z3950.maximumId" /><em>*</em> </label>
					    		<stripes:text class="numeric" name="maximumId" />
					    	</p>
				    	</span>
				    	
				    	<c:import url="/jsp/dataProvider/createDataSource.include.jsp" />
					</td>
				</tr>
			</table>
		</stripes:form>
	</body>
</html>
