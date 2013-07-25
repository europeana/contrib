<#ftl encoding="ISO-8859-1"/>
<#include "taglib.ftl"/>

<table width="100%" height="100%" cellspacing="10" cellpadding="0"><tr><td valign="top">

	<table cellspacing="0" cellpadding="0" width="100%"><tr><td colspan="3">
		<@folder title="Sobre" class="folder_simple">
			<img src="imgs/home.jpg" class="text" width="320" height="240">
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s</p>
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s</p>
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj sj</p>
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s</p>
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s</p>
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s</p>
		</@folder>
	</td></tr><tr><td style="width: 50%">
		<br/>
		<@folder title="QQ coisa 1" class="folder_simple" style="width: 100%; height: 100%">
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s
			</p>
		</@folder>
	</td><td>
	<pre style="width:10"/></td><td style="width: 50%">
		<br/>
		<@folder title="QQ coisa 2" class="folder_simple" style="width: 100%; height: 100%">
			<p class="text">aa bb aa bbr asd sdf 2wr f af ad afa sa gw rerg erg eg eg s sg eg e ge g g sg sd gs dg er ge rg eg er ger g erg eg er ge g erg e g eg r ge g ldj sldj sljsdlgjslj s
			</p>
		</@folder>
	</td></tr></table>

</td><td>
<@folder title="Anuncios" class="folder_simple" style="width: 110">
	<#list action.adds as p>
		<@button_product class="button_WhiteBlue"
				id="${p.@id}" pic="${p.@id}_1.small.jpg" product="${p.@brand}" price="${p.@price}"/>
	</#list>
</@folder>
</td></tr></table>
