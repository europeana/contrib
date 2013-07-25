<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<c:choose>
	<c:when test="${scheduledTask.frequency == 'ONCE'}">
		<fmt:message key="common.once" /> 
	</c:when>
	<c:when test="${scheduledTask.frequency == 'DAILY'}">
		<fmt:message key="common.daily" /> 
	</c:when>
	<c:when test="${scheduledTask.frequency == 'WEEKLY'}">
		<fmt:message key="common.every" /> ${scheduledTask.weekdayAsString} 
	</c:when>
	<c:when test="${scheduledTask.frequency == 'XMONTHLY'}">
		<fmt:message key="common.every" /> ${scheduledTask.xmonths} <fmt:message key="common.months" />
		<fmt:message key="common.day" /> <fmt:formatNumber minIntegerDigits="2" value="${scheduledTask.day}" />
	</c:when>
</c:choose>
	
		
<fmt:message key="common.starting" />
${scheduledTask.firstRunStringDate}

<fmt:message key="common.at" />
${scheduledTask.firstRunStringHour}

<c:if test="${scheduledTask.taskClass.name == 'pt.utl.ist.repox.task.IngestDataSource'
				&& scheduledTask.parameters[2] != null
				&& scheduledTask.parameters[2] == 'true'}">
	<fmt:message key="dataSource.fullIngest.short" />
</c:if>
