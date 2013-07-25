This is the Geoparser Text Verification tool.

Install local maven artifact
============================
For the moment, you still need to install the following artifacts by issuing from the project root directory:

mvn install:install-file \
  -Dfile=lib/openlayers-gwt-binding-1.0.jar \
  -DgroupId=at.ac.ait.dme.gwt \
  -DartifactId=openlayers-gwt-binding \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -DgeneratePom=true
  
Launch configuration for eclipse
================================

name:			gtv-hosted-mode
main class:		com.google.gwt.dev.DevMode
program args:	-startupUrl Application.html at.ac.ait.dme.gtv.Application -port 8888
JVM args:		-Djava.library.path=lib -Xmx256M -Dfile.encoding=UTF-8
classpath:		folder src/main/java (User entries -> Advanced -> Add folder)

Launching from the shell with maven
===================================

Alternatively, you can also run the application in hosted mode like this:

	mvn gwt:run

Don't forget to install the GWT plugin for your browser!