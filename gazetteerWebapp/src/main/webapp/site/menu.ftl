<#ftl encoding="UTF-8"/>
<table cellpadding="4" cellspacing="0">
    <!-- DIGMAP Menu -->
    <tbody>
	<tr>
	<td align="center" valign="top">
		<img src="imgs/subtitulo.jpg">
	</td>
	</tr>
	<tr>
        <!-- DIGMAP Public Services Title -->
        <td class="menuBar">
            <strong>Search</strong>
        </td>
    </tr>
    <tr>
        <td>
            <div class="search">
                <form name="form1" action="search.action" method="get">
                    <input type="text" style="width: 100px;" name="name">
                    <input type="submit" alt="Search" value="search">
                </form>
            </div>
        </td>
    </tr>
    <!-- Support Services Title -->
    <tr>
        <td class="menuBar">
            <i>Service Interfaces</i>
        </td>
    </tr>
    <!-- Support Services -->
    <tr>
        <td>
			<div class="item">
                <img src="style/imgs/seta.png" alt="" height="5" hspace="3" width="3">
                <a href="urn.action" class="menu1" title="URN">URN</a>
            </div>
            <div class="item">
                <img src="style/imgs/seta.png" alt="" height="5" hspace="3" width="3">
                <a href="gp.action" class="menu1" title="ADL General Protocol">ADL General Protocol (ADL-GP)</a>
            </div>
			<!--
            <div class="item">
                <img src="style/imgs/seta.png" alt="" height="5" hspace="3" width="3">
                <a href="oai.action" class="menu1" title="Open Archives Initiative - Protocol for Metadata Harvesting">OAI-PMH</a>
            </div>
			-->
            <div class="item">
                <img src="style/imgs/seta.png" alt="" height="5" hspace="3" width="3">
                <a href="gpoai.action" class="menu1" title="ADL-GP with OAI response">ADL-GP with OAI response</a>
            </div>
			<br>
        </td>
    </tr>
</tbody></table>