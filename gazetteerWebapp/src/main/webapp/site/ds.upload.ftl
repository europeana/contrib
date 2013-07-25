<#ftl encoding="UTF-8"/>

<@s.actionerror/>
<@s.fielderror/>
<h1>File Upload Example</h1>
<@s.form action="source.doUpload" method="POST" enctype="multipart/form-data">
	<@s.hidden name="source" value="${action.source!}"/>
	<@s.file name="upload" label="File"/>
	<@s.submit value="upload"/>
</@s.form>