How to Build:
-------------
Build the project with Maven and install the module .jar to your local Maven repository by
issuing from the project root directory:

mvn install:install-file \
  -Dfile=target/gwt-openlayers-wrapper-0.9.0.jar \
  -DgroupId=at.ait \
  -DartifactId=gwt-openlayers-wrapper \
  -Dversion=0.9.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

How to Use:
-----------
To use the gwt-openlayers-wrapper module in your GWT project, perform the following steps:
* Include the .jar in your project build path
* Declare the module dependency in your applications .gwt.xml module descriptor. 
  This is done by adding the following line:

<inherits name='at.ait.dme.gwt.openlayers.OpenLayersWrapper'/>

* Since the module only provides a binding to OpenLayers, not a replacement, you also need
  to include the OpenLayers script in your application's HTML file, e.g.:

<script src="http://openlayers.org/api/OpenLayers.js"></script>

That's it. The following sample code will add a basic OpenLayers map with an OpenStreetMap layer:

MapWidget mapWidget = new MapWidget(500, 300);
Map map = mapWidget.getMap();
map.addLayer(OSM.create());
map.zoomTo(5);
RootPanel.get().add(mapWidget);
  