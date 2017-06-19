This utility configures Pentaho shim. Currently works with CDH and HDP shims.

Latest binary can be downloaded here: https://sourceforge.net/projects/shimconfig/files/ShimConfig-1.0.10-jar-with-dependencies.jar/download


The utility have GUI and console running modes. It works on both Windows and Linux.

For GUI – execute “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar”

For console run, there are two methods:  
1. “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar somefile.properties” (an example for .properties file is included)  
2. “java -jar ShimConfig-1.0-SNAPSHOT-jar-with-dependencies.jar [pathToShim] [host] [sshUser] [sshPassword] [restUser] [restPassword] [optional:pathToTestProperties] ”  

It does:
- Modifies all necessary settings in plugin.properties
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
-	Licenses is to data-integration folder for YARN step only if they are in the same folder with utility (can add more places to look for them)
- Path to spark_submit_utility is not defined

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
Small issue with missing "/" in "/opt/pentaho/mapreduce" was fixed.  
Temp fix for hdp25sec cluster added.  
- 1.0.6:  
Now if dfsInstallDir is not set "pmr.kettle.dfs.install.dir" is not changed.  
Minor fixes.  
- 1.0.7:  
Cdh hadoop version like x.xx is now parsed correctly.  
Added support of changing secure configuration to unsecure configuration.  
- 1.0.8:  
"allow_text_splitter" is now correctly set for hdp26 clusters.  
"sqoop_secure_libjar_path" is now set with "file:///" prefix.  
- 1.0.10:
Added log into UI.  
Added copying SparkSQL driver to CDH shim folder.  
Now drivers for copying can be in sub-folder.  
Now it uses default values for "ssh user/password" and "rest user/password" if no values was entered.  
Now it can be used multiple times in one run.  
sqoop_secure_libjar_path finally correctly set on windows.  
If .installedLicenses.xml is in the same folder with utility - copied into data-integration folder.  
Another mechanism for root folder detection - No longer need to launch this utility from its folder.  
- 1.0.10
Fixed: SparkSql driver copied to HDP, not CDH shim folder.

