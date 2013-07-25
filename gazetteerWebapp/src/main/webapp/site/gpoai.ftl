<#ftl encoding="ISO-8859-1"/>
<#include "taglib.ftl"/>

	<h1 class="text">Alexandria Digital Library - General Protocol (ADL-GP) with OAI Response</h1>

	<p>The gazetteer server is successfully deployed.</p>
	<p>Use the forms on this page to test some queries against the gazetteer server.</p>

<#macro adlForm query>
<form method="post" action="gpoai">
<textarea rows="10" cols="69" name="request">
	<@adlQuery key=query/>
</textarea>
<table class="tablemain"><tr><td>
	<input type="submit" name="button" value="Run Query"/>
	<input type="reset" name="reset" value="Reset"/>
</td></tr></table>
</form>
</#macro>

<center>

	<div class="h2">XML Request - Query by place name</div>

<@adlForm query="name"/>

	<div class="h2">XML Request - Query by place name and feature type</div>

<@adlForm query="name&class"/>

	<div class="h2">XML Request - Query by place name and geographic region</div>

<@adlForm query="name&footprint_overlaps"/>

	<div class="h2">XML Request - Query by place name and place status</div>

<@adlForm query="name&status"/>

	<div class="h2">XML Request - Query by feature type and geographic region</div>

<@adlForm query="class&footprint"/>

	<div class="h2">XML Request - Query by feature identifier</div>

<@adlForm query="id"/>

	<div class="h2">XML Request - Query by feature type and part-of reltionship</div>

<@adlForm query="rel&class"/>

	<div class="h2">XML Request - Query by place name and containement relationship</div>

<@adlForm query="name&footprint_within"/>

	<div class="h2">XML Request - Query by debug feature</div>

<@adlForm query="debug"/>

</center>