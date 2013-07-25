<#ftl encoding="UTF-8"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign tiles=JspTaglibs["http://tiles.apache.org/tags-tiles"]>
<@tiles.importAttribute name="title" toName="title" scope="page"/>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>EuropeanaConnect Gazetteer</title>
	<link rel="shortcut icon" href="imgs/icon.ico" type="image/x-icon"/>
	<link href="style/css/gaz.css" rel="stylesheet" type="text/css"/>
	<link href="style/css/econnect.css" rel="stylesheet" type="text/css"/>
	<link href="style/css/styles.css" rel="stylesheet" type="text/css"/>
</head>


<body <@tiles.insertAttribute name="map"/>>

	<@tiles.insertAttribute name="header"/>

	<!-- Separator Bar -->
	<div class="separatorBar" style="height: 3px; padding: 0px; border: 0px; margin: 0px"></div>

	<!-- Content -->
	<div style="width:100%;margin-top:15px;">
		<@tiles.insertAttribute name="page"/>
	</div>

	<@tiles.insertAttribute name="footer"/>
</body>
</html>