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
        function changeFormat() {
            var select_list = document.getElementById('selectFormat');
            var selected_index = select_list.selected_index;
            //var text = select_list.options[selected_index].text;
            //var value = select_list.value;

            var otherFormatVar = document.getElementById('otherFormat');
            otherFormatVar.value = select_list.value;

            if(select_list.value != null && select_list.value == 'ISO2709') {
                otherFormatVar.readOnly = true;
                document.getElementById('iso2709').style.display = '';
            }
            else if(select_list.value != null && select_list.value == '') {
                otherFormatVar.readOnly = false;
                document.getElementById('iso2709').style.display = 'none';
            }
            else {
                otherFormatVar.readOnly = true;
                document.getElementById('iso2709').style.display = 'none';
            }
        }
    </script>
</head>
<body>
<stripes:errors />
<stripes:messages />

<stripes:form action="/dataProvider/CreateEditDataSourceDirectoryImporter.action" acceptcharset="UTF-8">
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
                    <label for="dataSource.metadataFormat"><fmt:message key="dataSource.metadataFormat" /><em>*</em> </label>
                    <select id="selectFormat" name="dummy" onchange="javascript:changeFormat()">
                        <c:if test="${not empty actionBean.dataSource.metadataFormat}">
                            <option selected="selected" value="${actionBean.dataSource.metadataFormat}">* Current</option>
                        </c:if>
                        <option value="ISO2709">ISO2709</option>
                        <option value="MarcXchange">MarcXchange</option>
                        <option value="tel">tel</option>
                        <option value="oai_dc">oai_dc</option>
                        <option value="">Other</option>
                    </select>
                    <c:if test="${not empty actionBean.dataSource.metadataFormat}">
                        <stripes:text id="otherFormat" readonly="true" name="dataSource.metadataFormat" size="150px" />
                    </c:if>
                    <c:if test="${empty actionBean.dataSource.metadataFormat}">
                        <stripes:text id="otherFormat" value="ISO2709" readonly="true" name="dataSource.metadataFormat" size="150px" />
                    </c:if>
                </p>

                <c:choose>
                    <c:when test="${actionBean.dataSource.metadataFormat == 'ISO2709'}"><c:set var="iso2709display" value="block"/></c:when>
                    <c:otherwise><c:set var="iso2709display" value="none"/></c:otherwise>
                </c:choose>
				    	
                <span id="iso2709" style="display: ${iso2709display}">
                    <p>
                        <label for="isoImplementationClass"><fmt:message key="dataSource.isoImplementationClass" /><em>*</em> </label>
                        <stripes:select name="isoImplementationClass">

                            <c:set var="msgIteratorIso2709"><fmt:message key="dataSource.iteratorIso2709" /></c:set>
                            <stripes:option label="${msgIteratorIso2709}" value="pt.utl.ist.marc.iso2709.IteratorIso2709" />
                            <c:set var="msgIteratorIso2709Albania"><fmt:message key="dataSource.iteratorIso2709Albania" /></c:set>
                            <stripes:option label="${msgIteratorIso2709Albania}" value="pt.utl.ist.marc.iso2709.IteratorIso2709Albania" />
                            <c:set var="msgIteratorIso2709Ukrania"><fmt:message key="dataSource.iteratorIso2709Ukraine" /></c:set>
                            <stripes:option label="${msgIteratorIso2709Ukrania}" value="pt.utl.ist.marc.iso2709.IteratorIso2709Ukraine" />
                        </stripes:select>
                    </p>
                    <p>
                        <label for="dataSource.characterEncoding"><fmt:message key="dataSource.characterEncoding" /><em>*</em> </label>
                        <stripes:select name="dataSource.characterEncoding" size="${fn:length(actionBean.characterEncodings)}">
                            <c:forEach var="currentEncoding" items="${actionBean.characterEncodings}">
                                <stripes:option label="${fn:replace(currentEncoding, '_', '-')}" value="${currentEncoding}"/>
                            </c:forEach>
                        </stripes:select>
                    </p>
                </span>

                <p>
                    <label for="dataSource.sourcesDirPath"><fmt:message key="dataSource.sourcesDirPath" /><em>*</em> </label>
                    <stripes:text name="dataSource.sourcesDirPath" />
                </p>

                <p>
                    <label for="dataSource.recordXPath"><fmt:message key="dataSource.recordXPath" /></label>
                    <stripes:text name="dataSource.recordXPath" />
                </p>
                <p>
                    <label for="dataSource.namespaces"><fmt:message key="dataSource.namespaces" /></label>
                    <stripes:submit name="addNamespace4Record"><fmt:message key="common.add" /></stripes:submit>
                </p>
                <c:set var="namespaceItems4Record">
                    <c:choose>
                        <c:when test="${fn:length(actionBean.namespacePrefixes4Record) >= fn:length(actionBean.namespaceUris4Record)}">${actionBean.namespacePrefixes4Record}</c:when>
                        <c:otherwise>${actionBean.namespaceUris4Record}</c:otherwise>
                    </c:choose>
                </c:set>
                <c:forEach items="${namespaceItems4Record}" var="namespacePrefix4Record" varStatus="namespaceIndex4Record">
                    <p>
                        <label for="namespacePrefixes4Record[${namespaceIndex4Record.index}]"><fmt:message key="dataSource.namespacePrefix" /> ${namespaceIndex4Record.index + 1}</label>
                        <stripes:text name="namespacePrefixes4Record[${namespaceIndex4Record.index}]" />
                    </p>
                    <p>
                        <label for="namespaceUris4Record[${namespaceIndex4Record.index}]"><fmt:message key="dataSource.namespaceUri" /> ${namespaceIndex4Record.index + 1}</label>
                        <stripes:text name="namespaceUris4Record[${namespaceIndex4Record.index}]" />
                    </p>
                </c:forEach>

                <c:import url="/jsp/dataProvider/createDataSource.include.jsp" />
            </td>
        </tr>
    </table>
</stripes:form>
</body>
</html>
