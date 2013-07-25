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
			function submitOaiCheck(form) {
				var processing = document.getElementById('processing').style.display = '';
				
				var lastHidden = document.getElementById('lastHidden');
				var newitem = "<input type=\"hidden\" name=\"testOaiSourceURL\" />";
				var newnode = document.createElement("span");
				newnode.innerHTML = newitem;
				form.insertBefore(newnode, lastHidden);
				  
				/*form.testOaiSourceURL.value = 'testOaiSourceURL'; */
				form.submit();
			}

			function submitOaiFull(form) {
				var processing = document.getElementById('processing').style.display = '';
				
				var lastHidden = document.getElementById('lastHidden');
				var newitem = "<input type=\"hidden\" name=\"getAllOaiSources\" />";
				var newnode = document.createElement("span");
				newnode.innerHTML = newitem;
				form.insertBefore(newnode, lastHidden);
				  
				/*form.testOaiSourceURL.value = 'testOaiSourceURL'; */
				form.submit();
			}

			function changeFormat() {
				var select_list = document.getElementById('selectFormat');
				var selected_index = select_list.selected_index;

				var otherFormatVar = document.getElementById('otherFormat');
				otherFormatVar.value = select_list.value;
				
				if(select_list.value != null && select_list.value == '') {
					otherFormatVar.readOnly = false;
				}
				else {
					otherFormatVar.readOnly = true;
				}
			}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />
			
		<stripes:form action="/dataProvider/CreateEditDataSourceOai.action" acceptcharset="UTF-8">
			<stripes:hidden name="dataProviderId" />
			<stripes:hidden id="lastHidden" name="dataSourceId" />
			<c:if test="${!empty actionBean.sets}">
				<c:forEach var="currentSet" items="${actionBean.sets}" varStatus="status">
					<stripes:hidden name="sets[${status.index}]" value="${currentSet}" /> 
				</c:forEach>
			</c:if>
			<c:if test="${actionBean.editing}"><stripes:hidden name="editing" value="true" /></c:if>
			
			<table class="selectTable">
				<tr>
					<td class="selectCell">
						<c:import url="/jsp/dataProvider/dataSourceMenu.include.jsp" />
					</td>
					<td>
						<div class="headerDiv"><fmt:message key="dataSource" /></div>
						<p>
			    			<label for="dataSource.oaiSourceURL"><fmt:message key="dataSource.oai.sourceURL" /><em>*</em> </label>
				    		<stripes:text name="dataSource.oaiSourceURL" />
			    			
				    		<!-- <stripes:submit name="testOaiSourceURL"><fmt:message key="common.check" /></stripes:submit> -->
				    		<input type="button" onclick="javascript:submitOaiCheck(this.form)" name="check" value="check"/>
				    		<input type="button" onclick="javascript:submitOaiFull(this.form)" name="Add All" value="Add All"/>
				    		<span id="processing" style="display: none;"><p><label>&nbsp;</label>
				    		<fmt:message key="dataSource.oai.processingCheck" /></p></span>
				    	</p>
						<p>
			    			<label for="dataSource.oaiSet"><fmt:message key="dataSource.oai.set" /> </label>
			    			<c:if test="${empty actionBean.sets}"><stripes:text name="dataSource.oaiSet" /></c:if>
										    			
				    		<c:if test="${!empty actionBean.sets}">
			    				<stripes:select name="dataSource.oaiSet">
			    					<optgroup label="${fn:length(actionBean.sets)} sets">
				    					<stripes:option disabled="" label="<ALL>" value="" />
				    					
						    			<c:forEach var="currentSet" items="${actionBean.sets}" varStatus="status">
						    				<c:choose>
							    				<c:when test="${!empty actionBean.setsRecords[status.index]}">
							    					<c:set var="recordsString"> (${actionBean.setsRecords[status.index]} <fmt:message key="common.records" />)</c:set>
							    				</c:when>
							    				<c:otherwise>
							    					<c:set var="recordsString" value="" />
							    				</c:otherwise>
						    				</c:choose>
							    			<stripes:option label="${currentSet} ${recordsString}" value="${currentSet}" />
						    			</c:forEach>
					    			</optgroup>
				    			</stripes:select>
			    			</c:if>
				    	</p>
				    	
				    	<p>
			    			<label for="dataSource.metadataFormat"><fmt:message key="dataSource.metadataFormat" /><em>*</em> </label>
			    			<select id="selectFormat" name="dummy" onchange="javascript:changeFormat()">
			    				<c:if test="${not empty actionBean.dataSource.metadataFormat}">
			    					<option selected="selected" value="${actionBean.dataSource.metadataFormat}">* Current</option>
			    				</c:if>
								<option value="oai_dc">oai_dc</option>
								<option value="tel">tel</option>
								<option value="MarcXchange">MarcXchange</option>
								<option value="">Other</option>
							</select>
							<c:if test="${not empty actionBean.dataSource.metadataFormat}">
								<stripes:text id="otherFormat" readonly="true" name="dataSource.metadataFormat" size="150px" />
							</c:if>
							<c:if test="${empty actionBean.dataSource.metadataFormat}">
								<stripes:text id="otherFormat" value="oai_dc" readonly="true" name="dataSource.metadataFormat" size="150px" />
							</c:if>
		    			</p>
						
						<c:import url="/jsp/dataProvider/createDataSourceCommon.include.jsp" />
					</td>
				</tr>
			</table>
		</stripes:form>
	</body>
</html>
