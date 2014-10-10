import org.apache.commons.io.IOUtils

// Show the last 1000 log lines
//  of catalina.home/logs/project.log
project = "euscreenxl"
pr = Runtime.getRuntime().exec( "tail -1000 " + System.getProperty( "catalina.home" ) + "/logs/${project}.log" )
lines = IOUtils.readLines( pr.getInputStream() )
pr.waitFor()
lines.each{ println it }
""

