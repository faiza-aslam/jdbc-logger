# jdbc-logger
JDBC Logger is a utility app that logs events to database. For this, a custom Handler "EventLoggingHandler" is defined which extends java.util.logging.Handler.
This utility is cross container (i.e. it can be run on different containers). SQL script to setup database and table is also added. The sample to test this functionality is available [here](https://github.com/faiza-aslam/logging-tester). 

Clone this repo and execute following command to build this project:
```
mvn clean install
```

**Tomcat** & **WildFly** configuration is as follows:
------
### Tomcat
1. Place following jars in %TOMCAT_HOME%/bin
	* jdbc-logger-0.0.1-SNAPSHOT.jar [this utility]
	* c3p0-0.9.5.1.jar
	* commons-dbutils-1.6.jar
	* mchange-commons-java-0.2.10.jar
	* mysql-connector-java-5.1.39-bin.jar

2. Modify catalina.bat file and add following lines just after :noJuliManager label:
```
set LOGGER="%CATALINA_HOME%\bin\jdbc-logger-0.0.1-SNAPSHOT.jar"
set MCHANGE="%CATALINA_HOME%\bin\mchange-commons-java-0.2.10.jar"
set C3P0="%CATALINA_HOME%\bin\c3p0-0.9.5.1.jar"
set DBUTILS="%CATALINA_HOME%\bin\commons-dbutils-1.6.jar"
set MYSQL="%CATALINA_HOME%\bin\mysql-connector-java-5.1.39-bin.jar"

set "CLASSPATH=%CLASSPATH%;%LOGGER%;%MCHANGE%;%C3P0%;%DBUTILS%;%MYSQL%"
```
This is because these jars need to be loaded before tomcat loads logging configuration.

3. Add following lines in logging.properties file placed at %TOMCAT_HOME%/conf
```
handlers= ...., com.examples.logging.EventLoggingHandler

com.examples.logging.EventLoggingHandler.level = ALL
com.examples.logging.EventLoggingHandler.driverClassName = com.mysql.jdbc.Driver
com.examples.logging.EventLoggingHandler.jdbcUrl = jdbc:mysql://localhost:3306/test?useSSL=false&amp;autoReconnect=true
com.examples.logging.EventLoggingHandler.username = root
com.examples.logging.EventLoggingHandler.password = root
com.examples.logging.EventLoggingHandler.insertStatement = insert into user_event_log values (?,?,?,?,?,?,?)

CustomEventLogger.handlers = com.examples.logging.EventLoggingHandler
CustomEventLogger.level = ALL
```
------

### WildFly
1. Create global module for this logger jar. For this, create directory structure as:
	%WILDFLY_HOME%/modules/com/examples/logging/main
	
	- Place following jars in this directory. The jars can be found from %USER_PROFILE%\.m2\repository\com\mchange\
		* jdbc-logger-0.0.1-SNAPSHOT.jar [this utility]
		* c3p0-0.9.5.1.jar
		* mchange-commons-java-0.2.10.jar
	
	- Place module.xml (included in this repo) in above location. Add following lines in standalone.xml in domain:ee subsystem before <spec-descriptor-property-replacement> element:
		```
		<global-modules>
			<module name="com.examples.logging" slot="main"/>
		</global-modules>
		```

2. Add 2 handlers ["ASYNC", "DB"] and logger in standalone.xml in logging subsystem:
	```
	<async-handler name="ASYNC">
		<level name="ALL"/>
		<queue-length value="1024"/>
		<overflow-action value="block"/>
		<subhandlers>
			<handler name="DB"/>
		</subhandlers>
	</async-handler>
	.....
	<custom-handler name="DB" class="com.examples.logging.EventLoggingHandler" module="com.examples.logging">
		<level name="ALL"/>
		<formatter>
			<pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
		</formatter>
		<properties>
			<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
			<property name="jdbcUrl" value="jdbc:mysql://localhost:3306/test?useSSL=false&amp;autoReconnect=true"/>
			<property name="username" value="{username}"/>
			<property name="password" value="{password}"/>
			<property name="insertStatement" value="insert into user_event_log values (?,?,?,?,?,?,?)"/>
		</properties>
	</custom-handler>
	.....
	<logger category="CustomEventLogger">
		<level name="ALL"/>
		<handlers>
			<handler name="ASYNC"/>
		</handlers>
	</logger>
	```
	
3. Add module for commons-dbutils as follows:
	* create directory structure -> %WILDFLY_HOME%\modules\system\layers\base\org\apache\commons\dbutils\main
	* place jar from %USER_PROFILE%\.m2\repository\commons-dbutils\commons-dbutils\1.6 to this directory
	* add module.xml file with following content:
		```
		<module xmlns="urn:jboss:module:1.3" name="org.apache.commons.dbutils">
			<resources>
				<resource-root path="commons-dbutils-1.6.jar" />
			</resources> 
			<dependencies>
				<module name="javax.sql.api"/>
			</dependencies>
		</module>
		```

3. Add module for mysql as follows:
	* create directory structure -> %WILDFLY_HOME%\modules\system\layers\base\com\mysql\driver\main
	* place jar from %USER_PROFILE%\.m2\repository\mysql\mysql-connector-java to this directory
	* add module.xml file with following content:
		```
		<module xmlns="urn:jboss:module:1.3" name="com.mysql.driver">
			<resources>
				<resource-root path="mysql-connector-java-5.1.39-bin.jar" />
			</resources>
			<dependencies>
				<module name="javax.api"/>
				<module name="javax.transaction.api"/>
			</dependencies>
		</module>
		```
	
------

That's it.. Your logger is ready to log into your database.
