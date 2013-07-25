<#ftl encoding="UTF-8"/>
<#include "taglib.ftl"/>

<script type="text/javascript">
    //<![CDATA[

	function sourceTransfer(url) {
		if(confirm('Do you wish to transfer the data from the data source into the database')) return window.location=url;
	}

	function sourceClear(url) {
		if(confirm('Do you wish to remove all data from the data source')) return window.location=url;
	}

	function dbClear(url) {
		if(confirm('Do you wish to remove all data from the master database')) return window.location=url;
	}

	function dbScript(url) {
		if(confirm('Do you wish to run the correction script over the master database')) return window.location=url;
	}

	//]]>
</script>

<div style="backgound-image: url(../../imgs/sources.jpg);">
<table cellpadding="0" cellspacing="8"><tr><td width="100%" valign="top" class="menudot">

<table width="100%" cellpadding="0" cellspacing="0"><tr><td>

<table class="list" width="100%">
	<tr><th colspan="6">Data Sources</th></tr>
	<tr><th>ID</th><th>schema</th><th>size</th><th colspan="3"></th></tr>
<#list action.sources as source>
	<tr><td>${source.identifier}</td><td>${source.defaultSchema.name}</td><td>${source.recordCount}</td>
	<td style="text-align:center;">
		<@url _var="action" _url="source.clear.action" source="${source.identifier}"/>
		<a href="javascript:sourceClear('${action}')">clear</a>
	</td>
	<td style="text-align:center;">
		<@url _var="action" _url="source.upload.action" source="${source.identifier}"/>
		<a href="${action}">import</a>
	</td>
	<td style="text-align:center;">
		<@url _var="action" _url="source.transfer.action" source="${source.identifier}"/>
		<a href="javascript:sourceTransfer('${action}')">transfer</a>
	</td>
	</tr>
</#list>
</table>

</td></tr><tr><td>
<br>
<table class="list" width="100%">
	<tr><th colspan="6">Task Manager</th></tr>
	<tr><th>Task</th><th>Scheduled</th><th>Started</th><th></th></tr>
<#list pool.tasks as task>
	<tr>
	<td>${task.name}</td><td>${task.scheduled?datetime}</td>
	<td><#if task.start??>${task.start?datetime}<#else>waiting...</#if></td>
	<td style="text-align:center"><a href="#">remove</a></td>
	</tr>
</#list>
</table>

</td></tr></table>

</td><td valign="top">

<table cellspacing="2" cellpadding="4" class="record" width="100%" style="backgound: url(imgs/database.jpg) bottom right;">
	<tr><th colspan="2">Database (<a href="javascript:dbClear('database.clear.action')">clear</a>) (<a href="javascript:dbScript('database.script.action')">script</a>)</th></tr>
	<tr><th align="right">total</th><td align="right">${metadataSpace.recordCount}</td>
	<tr><td colspan="2"><img src="imgs/database.jpg"/></td></tr>
	<#list metadataSpace.spaces as ms>
	<tr><th align="right">${ms.defaultSchema.prefix}</th><td align="right">${ms.recordCount}</td></tr>
	</#list>
</table>


</td></tr></table>

</div>