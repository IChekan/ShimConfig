package modifiers;

import util.PropertyHandler;
import util.SSHUtils;
import util.Values;
import util.XmlPropertyHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ihar_Chekan on 10/14/2016.
 */
public class ModifyTestProperties {

    public static void modifyTestProperties (String pathToTestProperties) {

        PropertyHandler propertyHandler = new PropertyHandler();
        XmlPropertyHandler xmlPropertyHandler = new XmlPropertyHandler();

        // set secured value
        if (Values.getSecured()) {
            propertyHandler.setProperty(pathToTestProperties, "secure_cluster", "true" );
        }
        else {
            propertyHandler.setProperty(pathToTestProperties, "secure_cluster", "false" );
        }

        //TODO: kinit user and password
        //TODO: make kinit on clusters


        //TODO: verify shim_active is for actual shim, not folder...

        //set shim_active
        propertyHandler.setProperty(pathToTestProperties, "shim_active", Values.getHadoopVendor()
                + Values.getHadoopVendorVersion() );

        // set sshServer, sshUser, sshPassword
        propertyHandler.setProperty(pathToTestProperties, "sshServer", Values.getHost() );
        propertyHandler.setProperty(pathToTestProperties, "sshUser", Values.getUser() );
        propertyHandler.setProperty(pathToTestProperties, "sshPassword", Values.getPassword() );


        // set hdfsServer, hdfsProto, hdfsPort, hdfsUrl values
        String defaultFS = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "core-site.xml",
                "fs.defaultFS" );
        String[] defaultFsSplitForHdfsProto = defaultFS.split("://");
        String[] defaultFsSplitForHdfsPort = defaultFsSplitForHdfsProto[1].split(":");
        propertyHandler.setProperty(pathToTestProperties, "hdfsProto", defaultFsSplitForHdfsProto[0] );
        propertyHandler.setProperty(pathToTestProperties, "hdfsServer", defaultFsSplitForHdfsPort[0] );
        if (defaultFsSplitForHdfsPort.length == 1) {
            propertyHandler.setProperty(pathToTestProperties, "hdfsPort", "" );
            propertyHandler.setProperty(pathToTestProperties, "hdfsUrl", "${hdfsProto}://${hdfsServer}" );
        }
        else {
            propertyHandler.setProperty(pathToTestProperties, "hdfsPort", defaultFsSplitForHdfsPort[1] );
            propertyHandler.setProperty(pathToTestProperties, "hdfsUrl", "${hdfsProto}://${hdfsServer}:${hdfsPort}" );
        }

        //add jobTrackerServer / jobTrackerPort
        //for hdp we take it from yarn.resourcemanager.address property
        if (xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                "yarn.resourcemanager.address") != null) {
            String[] rmAddress = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                    "yarn.resourcemanager.address").split(":");
            propertyHandler.setProperty(pathToTestProperties, "jobTrackerServer", rmAddress[0] );
            propertyHandler.setProperty(pathToTestProperties, "jobTrackerPort", rmAddress[1] );
        }
        //for cdh we take it from yarn.resourcemanager.address.someAlias , aliases can be found in yarn.resourcemanager.ha.rm-ids
        else {
            String[] rmAlias = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                    "yarn.resourcemanager.ha.rm-ids").split("[,]");
            String[] rmAddress = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                    "yarn.resourcemanager.address" + "." + rmAlias[0]).split(":");
            propertyHandler.setProperty(pathToTestProperties, "jobTrackerServer", rmAddress[0] );
            propertyHandler.setProperty(pathToTestProperties, "jobTrackerPort", rmAddress[1] );
        }

        // determine hive host and set all values for it
        String allClusterNodesGrepHostname = SSHUtils.getCommandResponseBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                "hdfs dfsadmin -report | grep Hostname" );
        allClusterNodesGrepHostname = allClusterNodesGrepHostname.replaceAll("\\r|\\n", "");
        String[] allClusterNodes = allClusterNodesGrepHostname.replaceFirst("Hostname: ", "").split("Hostname: ");
        String hiveServerNode = "";
        for ( String node : allClusterNodes ) {
            if (SSHUtils.getCommandResponseBySSH(Values.getUser(), node, Values.getPassword(),
                    "ps aux | grep HiveServer2" ).contains("org.apache.hive.service.server.HiveServer2")) {
                hiveServerNode = node;
            }
        }
        if (!hiveServerNode.equals("")) {
            propertyHandler.setProperty(pathToTestProperties, "hive2_hostname", hiveServerNode);
            //If vendor is cdh - adding Impala properties, same as for hive
            if (Values.getHadoopVendor().equalsIgnoreCase("cdh")) {
                propertyHandler.setProperty(pathToTestProperties, "impala_hostname", hiveServerNode);
            }
        }
        else System.out.println ("Hive node was not determined!!!");
        //if secured - add hive principal
        if (Values.getSecured()) {
            String hivePrincipalTemp1[] = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "hive-site.xml",
                    "hive.metastore.kerberos.principal" ).split("/");;
            String hivePrincipalTemp2[] = hivePrincipalTemp1[1].split("@");
            String hivePrincipal = hivePrincipalTemp1[0] + "/" + hiveServerNode + "@" + hivePrincipalTemp2[1];

            propertyHandler.setProperty(pathToTestProperties, "hive2_option", "principal");
            propertyHandler.setProperty(pathToTestProperties, "hive2_principal", hivePrincipal);
            //If vendor is cdh - adding Impala secured properties, same as for hive
            if (Values.getHadoopVendor().equalsIgnoreCase("cdh")) {
                if (Values.getSecured()) {
                    propertyHandler.setProperty(pathToTestProperties, "impala_KrbRealm", hivePrincipalTemp2[1]);
                    propertyHandler.setProperty(pathToTestProperties, "impala_KrbHostFQDN", hiveServerNode);
                }
            }
        }

        // add zookeeper host and port
        //for hdp it can be taken from "hadoop.registry.zk.quorum" property
        String zkQuorum = "";
        if (xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                "hadoop.registry.zk.quorum") != null) {
            zkQuorum = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                    "hadoop.registry.zk.quorum" );
        }
        //for cdh it can be taken from "yarn.resourcemanager.zk-address" property
        else if (xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                "yarn.resourcemanager.zk-address") != null) {
            zkQuorum = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "yarn-site.xml",
                    "yarn.resourcemanager.zk-address" );
        }
        else {
            System.out.println( "Both \"hadoop.registry.zk.quorum\" or \"hadoop.registry.zk.quorum\" properties was not found in \"yarn-site.xml\" !!! " );
        }
        // parsing zookeeper address after we have it
        String[] zkQuorumTemp1 = zkQuorum.split("[,]");
        ArrayList<String> zkQuorumArrayRes = new ArrayList<>();
        String zkPort = "";
        for (int i = 0; i < zkQuorumTemp1.length; i++) {
            String[] zTemp = zkQuorumTemp1[i].split(":");
            zkQuorumArrayRes.add(zTemp[0]);
            zkPort = zTemp[1];
        }
        // actual adding zookeeper host and port
        String zkQuorumRes = String.join("," , zkQuorumArrayRes);
        propertyHandler.setProperty(pathToTestProperties, "zookeeper_host", zkQuorumRes);
        propertyHandler.setProperty(pathToTestProperties, "zookeeper_port", zkPort);

        // Adding Oozie oozie_server
        for ( String node : allClusterNodes ) {
            if (SSHUtils.getCommandResponseBySSH(Values.getUser(), node, Values.getPassword(),
                    "ps aux | grep oozie" ).contains(" -Doozie.http.hostname")) {
                propertyHandler.setProperty(pathToTestProperties, "oozie_server", node);
            }
        }

        //find spark-assembly jar
        String[] findSparkAssembly = SSHUtils.getCommandResponseBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                "find / -name 'spark-assembly*'").split("\\r|\\n");
        String localSparkAssemblyPath = "";
        loopForSpark:
        for (String a : findSparkAssembly) {
            if (a.contains("spark-assembly-")) {
                localSparkAssemblyPath = a;
                break loopForSpark;
            }
        }
        // copy spark-assembly jar to hdfs and set spark_yarn_jar property
        SSHUtils.getCommandResponseBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                ("hadoop fs -copyFromLocal " + localSparkAssemblyPath + " /opt/pentaho"));
        File f = new File(localSparkAssemblyPath);
        String sparkAssemblyName = f.getName();
        propertyHandler.setProperty(pathToTestProperties, "spark_yarn_jar", "${hdfsUrl}/opt/pentaho/" + sparkAssemblyName);
        // if it is hdp cluster - 2 more propertyes are needed
        if (Values.getHadoopVendor().equalsIgnoreCase("hdp")) {
            String hdpVersion = SSHUtils.getCommandResponseBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                    "hdp-select versions").replaceAll("\\r|\\n", "");
            propertyHandler.setProperty(pathToTestProperties, "spark_driver_extraJavaOptions", "-Dhdp.version=" + hdpVersion);
            propertyHandler.setProperty(pathToTestProperties, "spark_yarn_am_extraJavaOptions", "-Dhdp.version=" + hdpVersion);
        }

        // TODO: move -Dhdp.version= change in config.properties to another class (ModifyPluginConfigProperties)
        String configPropertiesFile = Values.getPathToShim() + File.separator + "config.properties";
        if (Values.getHadoopVendor().equalsIgnoreCase("hdp")) {
            String hdpVersion = SSHUtils.getCommandResponseBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                    "hdp-select versions").replaceAll("\\r|\\n", "");
            propertyHandler.setProperty(configPropertiesFile, "java.system.hdp.version", hdpVersion);
        }




        // TODO:sqoop_secure_libjar_path


    }

}
