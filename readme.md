This utility configures Pentaho shim. Currently works with CDH and HDP shims.

Latest binary can be downloaded here: https://sourceforge.net/projects/shimconfig/files/ShimConfig-1.0.4-jar-with-dependencies.jar/download


The utility have GUI and console running modes. It works on both Windows and Linux.

For GUI – execute “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar”

For console run, there are two methods:

1. “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar somefile.properties” (an example for .properties file is included)

2. “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar [pathToShim] [host] [sshUser] [sshPassword] [restUser] [restPassword] [optional:pathToTestProperties] ”

It does:
-   Modifies all necessary settings in plugin.properties
-	Modifies all necessary settings in config.properties, including Kerberos settings before/after impersonation 
-	Copies all necessary *.-site.xml files from cluster
-	Adds “cross-platform” to mapred-site.xml
-	Copy MySQL and Impala Simba drivers to appropriate places (if they are in the same folder with utility)
-	Downloads “krb5.conf” from cluster to shim folder, but it need to be placed in appropriate folder manually.
-	It detects all needed settings and modifies test.properties file with properties, like HDFS hostname/port, Job Tracker/Resource Manager host and port, Hive/Impala host/port with Kerberos credentials, Zookeeper hosts/port, Sqoop settings, Spark settings and others.

It does NOT configure:
-	File “hosts”
-	Java and java cryptography extension (JCE) 
-	SHIMS_DATA property for automation (can be added later)
-	Licenses have to copied manually to data-integration folder for YARN step
-   Path to spark_submit_utility is not defined

Changelog:
- 1.0.1:
Added detection of separator at the end of the path to the shim.
- 1.0.2:
Impala and MySql Drivers copy fixed.
Property "pentaho.authentication.default.mapping.server.credentials.kerberos.principal" is changed to “hive@PENTAHOQA.COM” instead of devuser@PENTAHOQA.COM.
Now button "Start" is disabled after starting.
- 1.0.3:
RestHost property added for Microsift Azure shim. (config for this shim does NOT work for now).
SqoopSecureLibjarPath disabled for unsecure cluster.
kinit disabled for unsecure cluster.
- 1.0.4:
dfsInstallDir property added, can be used to modify /opt/pentaho/mapreduce in plugin.properties file.
RestClient was modified to ignore ssl sertificate for basic auth.
- 1.0.5:
Small issue with missing "/" in "/opt/pentaho/mapreduce" was fixed

