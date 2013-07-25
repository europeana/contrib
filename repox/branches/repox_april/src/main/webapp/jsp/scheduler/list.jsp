<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<html>
	<head>
		<title><fmt:message key="tasks" /></title>
				
		<script type="text/javascript">
			function changeView(newView) {
				var views = new Array('runningView', 'calendarView', 'listView');
				for (i in views) {
					var viewElement = document.getElementById(views[i]);
					
					
					if(views[i] == newView) {
						viewElement.style.display = '';
					}
					else {
						viewElement.style.display = 'none';
					}
				}
			}

		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />

		<c:import url="/jsp/scheduler/scheduleTaskPopup.include.jsp" />
		
		<span id="calendarView" style="display: block;">
			<div style="text-align: center;">
				<fmt:message key="scheduledTask.calendarView" /> |
				<a href="javascript:changeView('listView')"><fmt:message key="scheduledTask.listView" /></a> |
				<a href="javascript:changeView('runningView')"><fmt:message key="scheduledTask.runningView" /></a>
			</div>
	
			<br />
			<table class="calendar">
				<thead>
					<tr>
						<th colspan="7">
							<stripes:link href="/scheduler/Scheduler.action?calendarMonth=${actionBean.taskCalendarMonth.month.monthNumber - 1}&calendarYear=${actionBean.calendarYear}">&lt;&lt;</stripes:link>
							${actionBean.taskCalendarMonth.month} ${actionBean.taskCalendarMonth.year}
							<stripes:link href="/scheduler/Scheduler.action?calendarMonth=${actionBean.taskCalendarMonth.month.monthNumber + 1}&calendarYear=${actionBean.calendarYear}">&gt;&gt;</stripes:link>
						</th>
					</tr>
					<tr>
						<c:forEach var="weekday" items="${actionBean.taskCalendarMonth.weekdays}">
							<th class="weekday">${weekday}</th>
						</c:forEach>
					</tr>
				</thead>
				
				<c:forEach var="currentWeek" items="${actionBean.taskCalendarMonth.monthdays}" varStatus="currentWeekIndex">
					<tr>
						<c:forEach var="currentDay" items="${currentWeek}" varStatus="currentDayIndex">
	
							<c:choose>
						        <c:when test="${currentWeek[currentDayIndex.index].currentMonth}">
						        	<c:set var="backgroundcolor" value="#FFFFFF" />
						        </c:when>
						        <c:otherwise><c:set var="backgroundcolor" value="#AAAAAA" /></c:otherwise>
							</c:choose>
							
							<td style="background: ${backgroundcolor}">
								<div class="day">${currentWeek[currentDayIndex.index].numericDay}</div>
								<c:forEach var="task" items="${currentWeek[currentDayIndex.index].tasks}">
									<c:choose>
								        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
											<c:set var="dataSourceId" value="${task.parameters[1]}" />
											<c:set var="fullIngest" value="${task.parameters[2]}" />
								        </c:when>
								        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
											<c:set var="dataSourceId" value="${task.parameters[1]}" />
											<c:set var="exportDirectory" value="${task.parameters[2]}" />
											<c:set var="recordsPerFile" value="${task.parameters[3]}" />
								        </c:when>
								        <c:otherwise>
								        	<!-- <fmt:message key="error"></fmt:message> --> 
								        </c:otherwise>
									</c:choose>
									
									<c:choose>
								        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
							        		<a href="javascript:ingestDialogLoad('/scheduler/Scheduler.action', '${repox:dataProviderName(dataSourceId)}', '${dataSourceId}', '${fullIngest}', '${task.id}', '${task.frequency}', '${task.xmonths}', '${task.firstRunString}')">
								        </c:when>
								        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
											<a href="javascript:exportDirDialogLoad('/scheduler/Scheduler.action', '${repox:dataProviderName(dataSourceId)}', '${dataSourceId}', '${repox:sanitizeJavascript(exportDirectory)}', '${recordsPerFile}', '${task.id}', '${task.frequency}', '${task.xmonths}', '${task.firstRunString}')">
								        </c:when>
								        <c:otherwise>
								        	<a href=""> 
								        </c:otherwise>
									</c:choose>

									<div class="task">
										<c:set var="dailyKey"><fmt:message key="common.daily" /></c:set>
										<c:set var="weeklyKey"><fmt:message key="common.weekly" /></c:set>
										<c:set var="monthlyKey"><fmt:message key="common.every" /> ${task.xmonths} <fmt:message key="common.months" /></c:set>
										<c:choose>
									        <c:when test="${task.frequency == 'ONCE'}"></c:when>
									        <c:when test="${task.frequency == 'DAILY'}">
									        	<img alt="${dailyKey}" title="(${dailyKey}) ${repox:dataProviderName(dataSourceId)} - ${dataSourceId}" border="none" height="12px" width="12px"
													src="${pageContext.request.contextPath}/jsp/images/daily.png" />
											</c:when>
									        <c:when test="${task.frequency == 'WEEKLY'}">
									        	<img alt="${weeklyKey}" title="(${weeklyKey}) ${repox:dataProviderName(dataSourceId)} - ${dataSourceId}" border="none" height="12px" width="12px" 
									        		src="${pageContext.request.contextPath}/jsp/images/weekly.png" />
									        </c:when>
									        <c:when test="${task.frequency == 'XMONTHLY'}">
									        	<img alt="${monthlyKey}" title="(${monthlyKey}) ${repox:dataProviderName(dataSourceId)} - ${dataSourceId}" border="none" height="12px" width="12px"
									        		src="${pageContext.request.contextPath}/jsp/images/monthly.png" />
									        </c:when>
										</c:choose>
										<fmt:formatNumber type="number" minIntegerDigits="2" value="${task.hour}" />:<fmt:formatNumber type="number" minIntegerDigits="2" value="${task.minute}" />
										${fn:substring(dataSourceId, 0, 8)}
										<c:if test="${fullIngest == 'true'}"><fmt:message key="common.full" /></c:if>
									</div>
									</a>
								</c:forEach>
							</td>
				
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</span>

		<span id="listView" style="display: none;">
			<div style="text-align: center;">
				<a href="javascript:changeView('calendarView')"><fmt:message key="scheduledTask.calendarView" /></a> |
				<fmt:message key="scheduledTask.listView" /> |
				<a href="javascript:changeView('runningView')"><fmt:message key="scheduledTask.runningView" /></a>
			</div>
			
			<table class="mainTable uncenteredCells">
				<thead>
					<tr>
						<th class="mainHeader"><fmt:message key="common.type" /></th>
			      		<th class="mainHeader"><fmt:message key="common.parameters" /></th>
			      		<th class="mainHeader"><fmt:message key="scheduledTask.firstRun" /></th>
			      		<th class="mainHeader"></th>
					</tr>
				</thead>
				
				<c:forEach var="scheduledTask" items="${actionBean.scheduledTasks}" varStatus="currentIndex">
					<c:choose>
				        <c:when test="${currentIndex.index % 2 == 0}"><c:set var="cellClass" value="even" /></c:when>
				        <c:otherwise><c:set var="cellClass" value="odd" /></c:otherwise>
					</c:choose>
					<c:choose>
				        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
							<c:set var="dataSourceId" value="${scheduledTask.parameters[1]}" />
							<c:set var="fullIngest" value="${scheduledTask.parameters[2]}" />
				        </c:when>
				        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
							<c:set var="dataSourceId" value="${scheduledTask.parameters[1]}" />
							<c:set var="exportDirectory" value="${scheduledTask.parameters[2]}" />
				        </c:when>
				        <c:otherwise>
				        	<!-- <fmt:message key="error"></fmt:message> --> 
				        </c:otherwise>
					</c:choose>
					
					<tr>
						<td class="${cellClass}">
							<c:choose>
						        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
					        		<fmt:message key="dataSource.importer" />
						        </c:when>
						        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
					        		<fmt:message key="export.exportTofileSystem" />
						        </c:when>
						        <c:otherwise>
						        	<!-- <fmt:message key="error"></fmt:message> --> 
						        </c:otherwise>
							</c:choose>
						</td>
						<td class="${cellClass}">
							<c:choose>
						        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
					        		<fmt:message key="dataSource" />: ${dataSourceId}
					        		<br /><fmt:message key="dataSource.fullIngest" />: ${fullIngest}
						        </c:when>
						        <c:when test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
						        	<fmt:message key="dataSource" />: ${dataSourceId}
					            	<br /><fmt:message key="common.directory" />: ${exportDirectory}
						        </c:when>
						        <c:otherwise>
						        	<!-- <fmt:message key="error"></fmt:message> --> 
						        </c:otherwise>
							</c:choose>
						</td>
						
						<c:set var="allKey"> <fmt:message key="common.all" /></c:set>
						
						<td class="${cellClass}">
							<c:set var="scheduledTask" value="${scheduledTask}" scope="request"/>
							<c:import url="/jsp/scheduledTask.include.jsp" />
						</td>
						<td class="${cellClass}">
							<stripes:form action="/scheduler/Scheduler.action" acceptcharset="UTF-8">
								<c:set var="confirmationMessageScheduledTask"><fmt:message key="scheduledTask.delete.confirmationMessage" /></c:set>
						        <stripes:button onclick="javascript:deleteScheduledTask('${confirmationMessageScheduledTask}', '${scheduledTask.id}');"
						        				name="delete"><fmt:message key="common.delete" /></stripes:button>
					        </stripes:form>
						</td>
					</tr>
				</c:forEach>
			</table>
		</span>
		
		<span id="runningView" style="display: none;">
			<div style="text-align: center;">
				<a href="javascript:changeView('calendarView')"><fmt:message key="scheduledTask.calendarView" /></a> |
				<a href="javascript:changeView('listView')"><fmt:message key="scheduledTask.listView" /></a> |
				<fmt:message key="scheduledTask.runningView" />
			</div>
	
			<br />
			
			<table class="mainTable uncenteredCells">
				<thead>
					<tr>
						<th class="mainHeader"></th>
						<th class="mainHeader"><fmt:message key="common.type" /></th>
			      		<th class="mainHeader"><fmt:message key="common.parameters" /></th>
					</tr>
				</thead>
				
				<c:if test="${fn:length(actionBean.runningTasks) == 0}">
					<tr><td colspan="3"><fmt:message key="task.noRunningTasks" /></td></tr>
				</c:if>
				
				<c:forEach var="task" items="${actionBean.runningTasks}" varStatus="currentIndex">
					<c:choose>
				        <c:when test="${currentIndex.index % 2 == 0}"><c:set var="cellClass" value="even" /></c:when>
				        <c:otherwise><c:set var="cellClass" value="odd" /></c:otherwise>
					</c:choose>
					<c:choose>
				        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
							<c:set var="dataSourceId" value="${task.parameters[1]}" />
							<c:set var="fullIngest" value="${task.parameters[2]}" />
				        </c:when>
				        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
							<c:set var="dataSourceId" value="${task.parameters[0]}" />
							<c:set var="exportFolder" value="${task.parameters[1]}" />
				        </c:when>
				        <c:otherwise>
				        	<!-- <fmt:message key="error"></fmt:message> --> 
				        </c:otherwise>
					</c:choose>
					
					<tr>
						<td>
							<c:choose>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
						        	<stripes:form action="/scheduler/Scheduler.action" acceptcharset="UTF-8">
										<input type="hidden" name="taskClass" value="pt.utl.ist.repox.task.IngestDataSource" />
										<input type="hidden" name="dataSourceId" value="${dataSourceId}" />
										<input type="hidden" name="fullIngest" value="${fullIngest}" />
										
										<stripes:image name="cancelTask" alt="cancel" title="cancel" src="/jsp/images/cancel.png" />
									</stripes:form>
						        </c:when>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
						        	<stripes:form action="/scheduler/Scheduler.action" acceptcharset="UTF-8">
										<input type="hidden" name="taskClass" value="pt.utl.ist.repox.task.ExportToFilesystem" />			
										<input type="hidden" name="dataSourceId" value="${dataSourceId}" />
										<input type="hidden" name="exportFolder" value="${exportFolder}" />
										
										<stripes:image name="cancelTask" alt="cancel" title="cancel" src="/jsp/images/cancel.png" />
									</stripes:form>
						        </c:when>
						        <c:otherwise>
						        	<!-- <fmt:message key="error"></fmt:message> --> 
						        </c:otherwise>
							</c:choose>
						</td>
						<td class="${cellClass}">
							<c:choose>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
					        		<fmt:message key="dataSource.importer" />
						        </c:when>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
					        		<fmt:message key="export.exportTofileSystem" />
						        </c:when>
						        <c:otherwise>
						        	<!-- <fmt:message key="error"></fmt:message> --> 
						        </c:otherwise>
							</c:choose>
						</td>
						<td class="${cellClass}">
							<c:choose>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'}">
					        		<fmt:message key="dataSource" />: ${dataSourceId}
					        		<br /><fmt:message key="dataSource.fullIngest" />: ${fullIngest}
						        </c:when>
						        <c:when test="${task.taskClass.name == 'pt.utl.ist.repox.task.ExportToFilesystem'}">
						        	<fmt:message key="dataSource" />: ${dataSourceId}
					            	<br /><fmt:message key="common.directory" />: ${exportDirectory}
						        </c:when>
						        <c:otherwise>
						        	<!-- <fmt:message key="error"></fmt:message> --> 
						        </c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</table>
		</span>
	</body>
</html>
