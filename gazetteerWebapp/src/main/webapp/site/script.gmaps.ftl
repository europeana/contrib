<!--<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAfFyRD4FCo2aSqqgi5tGMDBSp2bLliVIpYzSDPXNtEYWhruI3JBScDDlbzCB3nxHuV4dLUNTZ-tzK3A" type="text/javascript"></script>-->
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=true&amp;key=ABQIAAAAfFyRD4FCo2aSqqgi5tGMDBQC_Gt6LQGO31K3ivihSbLtDI7QGxQD2KC2efwVh-kSwA91nuwNgHFTKA" type="text/javascript"></script>


<script type="text/javascript">
    //<![CDATA[

	function initLocationMap(center) {
        var map = new GMap2(document.getElementById("map"));
        map.setCenter(center, 16);
//		map.addControl(new GOverviewMapControl());
		map.addControl(new GSmallMapControl());
		map.addControl(new GMapTypeControl());
		map.enableDoubleClickZoom();
		map.setMapType(G_HYBRID_MAP);
		var marker = new GMarker(center);
        map.addOverlay(marker);
	}

    function load() {
		if(GBrowserIsCompatible()) {
			initLocationMap(new GLatLng(38.580698,-8.678684));
		}
	}

    //]]>
</script>