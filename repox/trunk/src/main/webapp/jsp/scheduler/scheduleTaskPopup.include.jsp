<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<!-- Jquery Stuff -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/javascript/taskPopup.js"></script>

<c:set var="scheduleKey"> <fmt:message key="common.schedule" /></c:set>
<div id="dialog" title="${scheduleKey}">
	
	<stripes:form action="/dataProvider/ViewDataProvider.action" acceptcharset="UTF-8">
   		<input type="hidden" id="dataSourceId" name="dataSourceId" value="" />
   		<input type="hidden" id="editing" name="editing" value="" />
   		<input type="hidden" id="taskId" name="taskId" value="" />
   		    	
   		<div>
			<div type="text" id="datepicker"></div>
			<br /><fmt:message key="scheduledTask.firstRun" />
				  <stripes:text readonly="readonly" id="actualDate" name="scheduledTask.date"style="width:110px;" />
			
			<fmt:message key="common.at" />

			<stripes:select id="hourProperty" name="scheduledTask.hour">
	      		<c:forEach var="currentHour" items="${actionBean.scheduledTask.hoursList}">
		            <stripes:option label="${currentHour}" value="${currentHour}"/>
				</c:forEach>
			</stripes:select>
			<fmt:message key="common.hour.short" />
		
      		<stripes:select id="minuteProperty" name="scheduledTask.minute">
	      		<c:forEach var="currentMinute" items="${actionBean.scheduledTask.minutesList}">
		            <stripes:option label="${currentMinute}" value="${currentMinute}"/>
				</c:forEach>
			</stripes:select>
      		<fmt:message key="common.minute.short" />
   		</div>

   		<div id="fullIngest" style="display: none;">
			<fmt:message key="dataSource.incrementalIngest" /> <stripes:radio id="incrementalIngestRadio" value="false" name="fullIngest" />
			<fmt:message key="dataSource.fullIngest" /> <stripes:radio id="fullIngestRadio" value="true" name="fullIngest" />
    	</div>
   		
   		<div id="export" style="display: none;">
   			<fmt:message key="export.recordsPerFile" />:
			<stripes:select id="recordsPerFileValue" name="recordsPerFile">
				<stripes:option label="1" value="1"/>
				<stripes:option label="10" value="10"/>
				<stripes:option label="100" value="100"/>
				<stripes:option label="250" value="250"/>
				<stripes:option label="1000" value="1000"/>
				<stripes:option value="-1"><fmt:message key="common.all" /></stripes:option>
			</stripes:select>
			<%--
			<br />
				
			<fmt:message key="common.directory" /> (<fmt:message key="common.fullPath" />):
			<stripes:text id="exportDirectoryValue" name="exportDirectory" />
            --%>
		</div>
    	
   		<table class="scheduleTable">
    		<tr>
    			<td class="frequency"><fmt:message key="common.frequency" />:</td>
     			<td class="radio">
     				<stripes:radio value="ONCE" id="ONCE" name="scheduledTask.frequency" />
     				<br /><stripes:radio value="DAILY" id="DAILY" name="scheduledTask.frequency" />
		      		<br /><stripes:radio value="WEEKLY" id="WEEKLY" name="scheduledTask.frequency" />
		      		<br /><stripes:radio value="XMONTHLY" id="XMONTHLY" name="scheduledTask.frequency" />
     			</td>
     			<td class="radioLabel">
	      			<fmt:message key="common.once" />
	      			<br /><fmt:message key="common.daily" />
	      			<br /><fmt:message key="common.weekly" />
	      			<br /><fmt:message key="common.every" />
	      					<stripes:select id="xmonths" name="scheduledTask.xmonths">
					      		<c:forEach var="monthFrequency" begin="1" end="12">
						            <stripes:option label="${monthFrequency}" value="${monthFrequency}"/>
								</c:forEach>
							</stripes:select>
	      					<fmt:message key="common.months" />	      				
     			</td>
     		</tr>
     		<tr>
     			<td colspan="3">
					<c:set var="confirmationMessageScheduledTask"><fmt:message key="scheduledTask.delete.confirmationMessage" /></c:set>
			        <stripes:button id="deleteTaskButton" onclick="javascript:deleteCalendarScheduledTask('${confirmationMessageScheduledTask}');"
			        				name="delete"><fmt:message key="common.delete" /></stripes:button>
     				<stripes:submit id="popupSubmitName" name=""><fmt:message key="common.schedule" /></stripes:submit>
     			</td>
     		</tr>
    	</table>	
	</stripes:form>
</div>