@echo off
rem Root OSGEO4W home dir to the same directory this script exists in
set OSGEO4W_ROOT=%~dp0
rem Remove trailing backslash
set OSGEO4W_ROOT=%OSGEO4W_ROOT:~0,-1%
echo OSGEO4W home is %OSGEO4W_ROOT% & echo.

PATH=%OSGEO4W_ROOT%\bin;%PATH%
for %%f in (%OSGEO4W_ROOT%\etc\ini\*.bat) do call %%f

rem @cmd.exe 
