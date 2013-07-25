call %OSGEO4W_ROOT%\OSGeo4W.bat

call %OSGEO4W_ROOT%\bin\gdal16.bat

call %OSGEO4W_ROOT%\apps\gdal-16\bin\gdal2tiles.bat %1 %2 %3 %4 %5
