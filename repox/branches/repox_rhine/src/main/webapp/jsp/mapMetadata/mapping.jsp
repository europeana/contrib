<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="repox" uri="http://repox.ist.utl.pt/tlds/repox.tld"%>


<html>
	<head>
		<title>
			<fmt:message key="mapMetadata.mapFields" />
		</title>
		
		<script type="text/javascript" src="../jsp/WireIt/lib/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
		<script type="text/javascript" src="../jsp/WireIt/lib/yui/dragdrop/dragdrop-min.js"></script>
		
		<!-- Excanvas -->
		<!--[if IE]><script type="text/javascript" src="../jsp/WireIt/lib/excanvas.js"></script><![endif]-->
		
		<!-- WireIt -->
		<script type="text/javascript" src="../jsp/WireIt/js/WireIt.js"></script>
		<script type="text/javascript" src="../jsp/WireIt/js/Wire.js"></script>
		<script type="text/javascript" src="../jsp/WireIt/js/Terminal.js"></script>
		<link rel="stylesheet" type="text/css" href="../jsp/WireIt/css/WireIt.css" />
		
		<style>
			div.blockBox { /* WireIt */
				position: absolute;
				z-index: 5;
				opacity: 0.8;
				/* Others */
				width: 50px;
				height: 380px;
				background-color: rgb(255, 200, 200);
			}
			
			div.WireIt-Terminal {
				margin: 10px 0px;
			}
			
			#blockTop div.WireIt-Terminal,#blockBottom div.WireIt-Terminal {
				margin: 8px;
				float: left;
			}
		</style>
		
		<script>
		var side1List = [];
		var side2List = [];
			
		window.onload = function() {
			var bl = YAHOO.util.Dom.get('blockLeft');
			var br = YAHOO.util.Dom.get('blockRight');
						 
			for( var i = 0 ; i < ${fn:length(actionBean.parsedTags)} ; i++) {
				side2List.push(new WireIt.Terminal(bl, {direction: [-1,0], offsetPosition:[0,i*25 - 10], ddConfig: {type: 'side1', allowedTypes: 'side2'}}));
			}
			for( var i = 0 ; i < ${fn:length(actionBean.dcTags)} ; i++) {
				side1List.push(new WireIt.Terminal(br, {direction: [1,0], offsetPosition:[0,i*25 - 10], ddConfig: {type: 'side2', allowedTypes: 'side1'}}));
			}
			
			<c:if test="${fn:length(actionBean.mappedTags) > 0}">
				<c:set var="splitTags" value="${fn:split(actionBean.mappedTags, ',')}" />
				<c:forEach var="currentSplitTags" items="${splitTags}" varStatus="currentIndex">
					<c:set var="currentTag" value="${fn:split(currentSplitTags, '-')}" />
				
					var newWire = new WireIt.Wire(side1List[${currentTag[0]}], side2List[${currentTag[1]}], br);
					newWire.redraw();
				</c:forEach>
			</c:if>
			
			/*
				var wire1 = new WireIt.Wire(side1List[0], side2List[1], br);
				wire1.redraw();
			*/
		};

		function compareNumbers(a, b)
		{
			return a - b;
		}
		
		function deleteDc(indexDcTag, confirmationMessage) {
			var form = document.getElementById('mapForm');
			var answer = confirm (confirmationMessage);
			if (answer) {
				form.action= 'MapMetadata.action?deleteDcTag=deleteDcTag';
				form.dcTagIndex.value = indexDcTag;
				return sendMappings(form);
			}
			else {
				return false;
			}
		}
		
		function deleteParsed(indexParsedTag, confirmationMessage) {
			var form = document.getElementById('mapForm');
			var answer = confirm (confirmationMessage);
			if (answer) {
				form.action= 'MapMetadata.action?deleteParsedTag=deleteParsedTag';
				form.parsedTagIndex.value = indexParsedTag;
				return sendMappings(form);
			}
			else {
				return false;
			}
		}

		function createDcTagFunc(form) {
			currentElement = document.createElement("input");
			currentElement.setAttribute("type", "hidden");
			currentElement.setAttribute("name", "createDcTag");
			form.appendChild(currentElement);

			sendMappings(form);
		}
		
		function createParsedTagFunc(form) {
			currentElement = document.createElement("input");
			currentElement.setAttribute("type", "hidden");
			currentElement.setAttribute("name", "createParsedTag");
			form.appendChild(currentElement);

			sendMappings(form);
		}
		
		function createGroupFunc(form) {
			currentElement = document.createElement("input");
			currentElement.setAttribute("type", "hidden");
			currentElement.setAttribute("name", "createGroup");
			form.appendChild(currentElement);

			sendMappings(form);
		}
		
		function sendMappings(form) {
			var resultConnections = new Array(side1List.length);
						
			for(var i = 0 ; i < side1List.length; i++) {
				var currentResultConnections = [];
				
				var connectedTerminals = side1List[i].getConnectedTerminals();
				for(var j = 0;  j < connectedTerminals.length; j++) {
					for(var k = 0; k < side2List.length; k++) {
						if(connectedTerminals[j] == side2List[k]) {
							currentResultConnections.push(k);
						}
					}
				}
				
				for(var j = 0 ; j < side2List.length; j++) {
					if(side2List[j].getConnectedTerminals().indexOf(side1List[i]) != -1) {
						currentResultConnections.push(j);
					}
				}
				
				resultConnections[i] = currentResultConnections.sort(compareNumbers);
			}
			
			var resultString = 'RESULTING CONNECTIONS: ';
			form.mappedTags.value = '';
			for(var i = 0 ; i < resultConnections.length; i++) {
				resultString += '\nNode [' + i + ']: ';
				form.mappedTags[i] = new Array(resultConnections[i].length); 
				for(var j = 0;  j < resultConnections[i].length; j++) {
					resultString += resultConnections[i][j] + ';';
					form.mappedTags.value = form.mappedTags.value + i + '-' + resultConnections[i][j] + ',';
				}
			}
			
			form.submit();
		}
		
		function viewToggleMarcXchange()
		{
			var marcXchangeTag = document.getElementById('marcXchangeTag');
			var marcXchangeName = document.getElementById('marcXchangeName');
		
			marcXchangeTag.style.display = (marcXchangeTag.style.display != 'none' ? 'none' : '');
			marcXchangeName.style.display = (marcXchangeTag.style.display != 'none' ? 'none' : '');
		}
		
		function cancelMapping(confirmationMessage) {
			var Location = '${pageContext.request.contextPath}' + '/mapMetadata/MapMetadata.action?cancel=cancel';
			var answer = confirm (confirmationMessage);
			if (answer) {
				location.href = Location;
			}
			else {
				
			}
		}
		</script>
	</head>
	<body>
		<stripes:errors />
		<stripes:messages />

		<stripes:form id="mapForm" action="/mapMetadata/MapMetadata.action" acceptcharset="UTF-8">
			<input type="hidden" name="mappedTags" />
			<input type="hidden" name="dcTagIndex" />
			<input type="hidden" name="parsedTagIndex" />
			
			<c:set var="totalHeight">
				<c:choose>
					<c:when test="${fn:length(actionBean.parsedTags) > fn:length(actionBean.dcTags)}">${fn:length(actionBean.parsedTags) * 25}</c:when>
					<c:otherwise>${fn:length(actionBean.dcTags) * 25}</c:otherwise>
				</c:choose>
			</c:set>
										
			<table class="metadataMap">
				<thead>
					<tr>
						<td><fmt:message key="mapMetadata.oaiDctel" /> 
							<stripes:button name="createDcTag" onclick="javascript:createDcTagFunc(this.form)"><fmt:message key="mapMetadata.newTag" /></stripes:button>
						</td>
						<td></td>
						<td>
						<stripes:button name="createParsedTag" onclick="javascript:createParsedTagFunc(this.form)"><fmt:message key="mapMetadata.newTag" /></stripes:button>
							(<a href="javascript:viewToggleMarcXchange()">Show XPath/Tag names</a>) ${actionBean.sampleNamespace}
						</td>
					</tr>
				</thead>
				<tr>
					<td align="right" style="white-space: nowrap;">
						<div style="position: relative; height: ${totalHeight}px;">
							<c:forEach var="dcTag" items="${actionBean.dcTags}" varStatus="currentIndex">
								<span style="position:absolute; top:${1 + currentIndex.index * 25}px; right: 2px;">
									${dcTag.xpath}
									<c:set var="confirmationMessageDcTag"><fmt:message key="mapMetadata.dcTagDlete.confirmationMessage" /></c:set>
									<img class="handOver" alt="trash" src="../jsp/images/delete.png" onclick="javascript:deleteDc('${currentIndex.index}', '${confirmationMessageDcTag}');" />
									<span class="info" title="${dcTag.description}">&nbsp;i&nbsp;</span>
								</span>
							</c:forEach>
						</div>
					</td>
					<td>
						<div style="position: relative; height: ${totalHeight}px; width:210px;">
							<div id='blockRight' class="blockBox" style="position: absolute; width: 20px; height: ${fn:length(actionBean.dcTags) * 25 - 5}px;"></div>
							<div id='blockLeft'  class="blockBox"  style="position: absolute; width: 20px; height: ${fn:length(actionBean.parsedTags) * 25 - 5}px; left: 190px;"></div>
						</div>
					</td>
					<td align="left" style="white-space: nowrap;">
						<div style="position: relative; height: ${totalHeight}px; ">
							
							<c:forEach var="parsedTag" items="${actionBean.parsedTags}" varStatus="tagIndex">
								<span style="position: absolute; top:${1 + tagIndex.index * 25}px; left: 0px;">
									<input type="checkbox" name="selectedTags[${tagIndex.index}]" value="${parsedTag.xpath}" />
								</span>
							</c:forEach>
							
							<c:set var="numberDigitsOccurs">${repox:digitsMaxNumberOccurences(actionBean.parsedTags)}</c:set>
							<span id="marcXchangeTag">
								<c:forEach var="parsedTag" items="${actionBean.parsedTags}" varStatus="tagIndex">
									<span style="position: absolute; top:${1 + tagIndex.index * 25}px; left: 25px;">
										<span class="info" title="${parsedTag.description}">&nbsp;i&nbsp;</span>
										<span class="occurrence" title="${parsedTag.examples}">&nbsp;${repox:getFixedSizeNumber(parsedTag.occurrences, numberDigitsOccurs)}&nbsp;</span>
										<c:set var="confirmationMessageParsedTag"><fmt:message key="mapMetadata.parsedTagDlete.confirmationMessage" /></c:set>
										<img class="handOver" alt="trash" src="../jsp/images/delete.png" onclick="javascript:deleteParsed('${tagIndex.index}', '${confirmationMessageParsedTag}');" />
										${parsedTag.xpath}
									</span>
								</c:forEach>
							</span>
							<span id="marcXchangeName" style="display: none;">
								<c:forEach var="parsedTag" items="${actionBean.parsedTags}" varStatus="tagIndex">
									<span style="position: absolute; top:${1 + tagIndex.index * 25}px; left: 25px;">
										<span class="info" title="${parsedTag.xpath}">&nbsp;i&nbsp;</span>
										<span class="occurrence" title="${parsedTag.examples}">&nbsp;${repox:getFixedSizeNumber(parsedTag.occurrences, numberDigitsOccurs)}&nbsp;</span>
										<c:set var="confirmationMessageParsedTag"><fmt:message key="mapMetadata.parsedTagDlete.confirmationMessage" /></c:set>
										<img class="handOver" alt="trash" src="../jsp/images/delete.png" onclick="javascript:deleteParsed('${tagIndex.index}', '${confirmationMessageParsedTag}');" />
										${parsedTag.description}
									</span>
								</c:forEach>
							</span>
						</div>

						<stripes:button name="createGroup" onclick="javascript:createGroupFunc(this.form)"><fmt:message key="mapMetadata.createGroup" /></stripes:button>
					</td>
				</tr>
				<tr>
					<td colspan="3" align="center">
						<c:set var="confirmationMessage"><fmt:message key="mapMetadata.cancel.confirmationMessage" /></c:set>
				    	<stripes:button onclick="javascript:cancelMapping('${confirmationMessage}');" name="cancel"><fmt:message key="common.cancel" /></stripes:button>
						<stripes:submit name="submitMappings" onclick="javascript:sendMappings(this.form)"><fmt:message key="common.submit" /></stripes:submit>
					</td>
				</tr>
			</table>
		</stripes:form>
	</body>
</html>
