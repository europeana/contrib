* If you want to enable server side zooming make sure to add your native JMagick library to the
java.library.path and have the corresponding ImageMagick binaries in the execution path.

* launch configuration:
	main class: com.google.gwt.dev.DevMode
	vm: -Djava.library.path=lib -Xmx256M -Dfile.encoding=UTF-8
	program args: -startupUrl test.html at.researchstudio.dme.imageannotation.Application -port 8888