
10-05-10 jacob.lundqvist@galdrion.com
07-06-10 cesare.concordia@isti.cnr.it

Temporary getting started guide

The current Repox release should be built from the code stored in to svn.

---- Download ----

Download code using an svn client from:

http://europeanalabs.eu/browser/contrib/repox/trunk

We call Repox-Home the dir where the code has been stored.

---- Install ----

On a new install you will first have to copy the file 

	/Repox_Home/src/main/resources/repox2sip-applicationRepox-context.xml.template 

to actual name

	/Repox_Home/src/main/resources/repox2sip-applicationRepox-context.xml

The integration between Repox and Sip Manager occurs via a shared data store, the data store is currently 
implemented using a RDBMS. 
You should have a DBMS installed and a database created on it where to store the shared data. During the
deployment phase Repox will create a set of tables, specifically those having the <<Repox>> stereotype in 
the db schema  description shown in the repoxSipIntegration.pdf document.

The file repox2sip-applicationRepox-context.xml contains the settings for database connections. 
In particular the elements of

	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">

should be modified as follows:

	<property name="driverClass" value="org.postgresql.Driver"/>
        
replace value with the correct driver. Warning: the Repox is currently tested with PostgresSQL 8.x,
we suggest to us the same DBMS for this early release.
        
   <property name="jdbcUrl" value="jdbc:url:db"/>

the jdbc URL linking the database in your DBMS

	<property name="user" value="user"/>
	<property name="password" value="pwd"/>

Login/password for accessing the database, make sure the user has permissions to create/drop tables.

----- compile ---

you need Maven to build the Repox. In the Repox home dir:

	mvn clean
	mvn install -Dmaven.test.skip=true

--- deployment ---

Install the file /Repox_Home/target/repox.war on apache Tomcat (release 6.0.x)


--- use ---

Connect to

http://server.address:port/repox/

the first connection is redirected to and administration GUI where the  administrator must set up the 
directories used by Repox to store data and configuration files. 
Directories are automatically created by repox so be sure to insert path where the user running Tomcat 
has rights permissions. For instance if you set:

	/Users/myuser/conf/repox_xml_config
	

as xmlConfig dir and  /Users/myuser/conf/ is a directory in the server the user running Tomcat must 
have write permissions on this directory in order to create the repox_xml_config and the files inside it.
When this administration form is correctly filled the actual Repox home page appears. 

