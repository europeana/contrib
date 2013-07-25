<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<html>
	<head>
		<title>Create Group</title>
		<script>
			function sendOperation(form, position) {
				form.tagGroupPos.value = position;
				form.submit();
			} 
		</script>
	
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />

		<stripes:form action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
			<stripes:hidden name="tagGroupPos" />
			
			<div class="centerTable">
		    	<table>
		    		<tr>
		    			<td><fmt:message key="common.description" /><em>*</em></td>
		    			<td colspan="4"><stripes:text class="longField" name="tagGroup.description" /></td>
		    		</tr>
		    		<c:if test="${fn:length(actionBean.tagGroup.commonXpath) > 0}">
			    		<tr>
			    			<td><fmt:message key="mapMetadata.commonXpath" /></td>
			    			<td colspan="4">
				    			<stripes:select name="tagGroup.commonXpath">
									<stripes:option label="${actionBean.tagGroup.commonXpath}" value="${actionBean.tagGroup.commonXpath}" />
									<stripes:option label="-" value="" />
								</stripes:select>
			    			</td>
			    		</tr>
		    		</c:if>
		    		
					<c:forEach var="currentTag" items="${actionBean.tagGroupAllTags}" varStatus="tagIndex">
						<tr>
							<td>
								<c:if test="${tagIndex.index == 0}"><fmt:message key="mapMetadata.prefixInitial" /></c:if>
								<c:if test="${tagIndex.index != 0}"><fmt:message key="mapMetadata.prefix" /></c:if>
							</td>
							<td>
								<c:if test="${tagIndex.index == 0}"><stripes:text class="shortField" name="tagGroup.initialPrefix" /></c:if>
								<c:if test="${tagIndex.index != 0}">
									<input class="shortField" type="text" name="tagPrefixes[${tagIndex.index}]" value="${actionBean.tagPrefixes[tagIndex.index]}"/>
								</c:if>
							</td>
							<td>
								<c:if test="${tagIndex.index != 0}">
									<input type="image" src="${pageContext.request.contextPath}/jsp/images/up.jpg" onclick="javascript:sendOperation(this.form, ${tagIndex.index})" name="riseInGroup" value="Rise" />
									<br />
								</c:if>
								
								<c:if test="${tagIndex.index != fn:length(actionBean.tagGroupAllTags) - 1}">
									<input type="image" src="${pageContext.request.contextPath}/jsp/images/down.jpg" onclick="javascript:sendOperation(this.form, ${tagIndex.index})" name="lowerInGroup" value="Lower" />
								</c:if>
							</td>
							<td>${currentTag.xpath}</td>
							<td>
								<input type="submit" onclick="javascript:sendOperation(this.form, ${tagIndex.index})" name="removeFromGroup" value="Remove" />
							</td>
						</tr>
					</c:forEach>
					<tr>
						<td><fmt:message key="mapMetadata.suffixFinal" /></td>
						<td><stripes:text class="shortField"  name="tagGroup.finalSuffix" /></td>
						<td colspan="3"></td>
					</tr>
					<tr>
						<td align="center" colspan="5">
							<stripes:submit name="cancelToMappings" ><fmt:message key="common.cancel" /></stripes:submit>
							<stripes:submit name="submitNewGroup"><fmt:message key="common.save" />
						</stripes:submit></td>
					</tr>
				</table>
   			</div>
		</stripes:form>
	</body>
</html>
