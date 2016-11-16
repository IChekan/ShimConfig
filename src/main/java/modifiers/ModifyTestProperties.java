package modifiers;

import util.PropertyHandler;
import util.Values;
import util.XmlPropertyHandler;

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

        //TODO: determine if it is hdp or cdh shim. With version preferably

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
        String hiveUris = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "hive-site.xml",
                "hive.metastore.uris" );
        String[] hiveHostWithoutThrift = hiveUris.split("://");
        String[] hiveHostWithoutPort = hiveHostWithoutThrift[1].split(":");
        propertyHandler.setProperty(pathToTestProperties, "sshServer", hiveHostWithoutPort[0] );
        propertyHandler.setProperty(pathToTestProperties, "hive2_hostname", hiveHostWithoutPort[0] );
        //if secured - add hive principal
        if (Values.getSecured()) {
            String hivePrincipalTemp1[] = xmlPropertyHandler.readXmlPropertyValue(Values.getPathToShim() + "hive-site.xml",
                    "hive.metastore.kerberos.principal" ).split("/");;
            String hivePrincipalTemp2[] = hivePrincipalTemp1[1].split("@");
            String hivePrincipal = hivePrincipalTemp1[0] + "/" + hiveHostWithoutPort[0] + "@" + hivePrincipalTemp2[1];

            propertyHandler.setProperty(pathToTestProperties, "hive2_option", "principal");
            propertyHandler.setProperty(pathToTestProperties, "hive2_principal", hivePrincipal);
        }

        //TODO: add impala

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

        // TODO:Oozie
        // TODO:sqoop_secure_libjar_path
        // TODO:spark



    }

}
