<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>

<script type="text/javascript">
    function changeIdType() {
        var select_list = document.getElementById('selectId');

        if(select_list.value != null && select_list.value == 'IdExtracted') {
            document.getElementById('IdExtracted').style.display = '';
        }
        else {
            document.getElementById('IdExtracted').style.display = 'none';
        }
    }

</script>

<p>
    <label><fmt:message key="dataSource.idPolicy.short" /></label>
    <stripes:select id="selectId" name="idType" onchange="javascript:changeIdType()">
        <stripes:option value="IdGenerated"><fmt:message key="dataSource.idGenerated.short" /></stripes:option>
        <stripes:option value="IdExtracted"><fmt:message key="dataSource.idExtracted.short" /></stripes:option>
    </stripes:select>
</p>

<c:choose>
    <c:when test="${actionBean.idType == 'IdExtracted'}"><c:set var="IdExtractedDisplay" value="block"/></c:when>
    <c:otherwise><c:set var="IdExtractedDisplay" value="none"/></c:otherwise>
</c:choose>
  	
<span id="IdExtracted" style="display: ${IdExtractedDisplay}">
	<p>
        <label for="identifierXpath"><fmt:message key="dataSource.idXpath" /><em>*</em> </label>
        <stripes:text name="identifierXpath" />
    </p>
	<p>
        <label for="addNamespace4Id"><fmt:message key="dataSource.namespaces" /></label>
        <stripes:submit name="addNamespace4Id"><fmt:message key="common.add" /></stripes:submit>
    </p>
	<c:set var="namespaceItems4Id">
        <c:choose>
            <c:when test="${fn:length(actionBean.namespacePrefixes4Id) >= fn:length(actionBean.namespaceUris4Id)}">${actionBean.namespacePrefixes4Id}</c:when>
            <c:otherwise>${actionBean.namespaceUris4Id}</c:otherwise>
        </c:choose>
    </c:set>
	
	<c:forEach items="${namespaceItems4Id}" var="namespacePrefix4Id" varStatus="namespaceIndex4Id">
        <p>
            <label for="namespacePrefixes4Id[${namespaceIndex4Id.index}]"><fmt:message key="dataSource.namespacePrefix" /> ${namespaceIndex4Id.index + 1}</label>
            <stripes:text name="namespacePrefixes4Id[${namespaceIndex4Id.index}]" />
        </p>
        <p>
            <label for="namespaceUris4Id[${namespaceIndex4Id.index}]"><fmt:message key="dataSource.namespaceUri" /> ${namespaceIndex4Id.index + 1}</label>
            <stripes:text name="namespaceUris4Id[${namespaceIndex4Id.index}]" />
        </p>
    </c:forEach>
</span>

<c:import url="/jsp/dataProvider/createDataSourceCommon.include.jsp" />
