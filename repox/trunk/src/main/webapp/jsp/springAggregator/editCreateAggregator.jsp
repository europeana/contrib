<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${!empty error}">
    <font color=red><c:out value="${error}" /></font>
</c:if>

<c:choose>
    <c:when test="${editing}">
        <c:import url="editAggregator.jsp" />
    </c:when>
    <c:otherwise>
        <c:import url="createAggregator.jsp" />
    </c:otherwise>
</c:choose>