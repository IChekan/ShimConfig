package modifiers;

import util.PropertyHandler;
import util.SSHUtils;
import util.ShimValues;

import java.io.File;

/**
 * Created by Ihar_Chekan on 10/14/2016.
 */
public class ModifyPluginConfigProperties {

    public void modifyPluginProperties () {
        if (ShimValues.isShimSecured()) {
            SSHUtils.copyFileBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
                    "/etc/krb5.conf", ShimValues.getPathToShim() );
            System.out.println("krb5.conf copied from cluster to shim location. Please move it to appropriate place.");
        }

        // Determine shim folder
        File f = new File(ShimValues.getPathToShim());
        String shimFolder = f.getName();
        File hadoopConfigurationsFolder = new File(f.getParent());
        String pluginPropertiesFile = hadoopConfigurationsFolder.getParent() + File.separator + "plugin.properties";
        String configPropertiesFile = ShimValues.getPathToShim() + "config.properties";

        PropertyHandler.setProperty(pluginPropertiesFile, "active.hadoop.configuration", shimFolder);

        PropertyHandler.setProperty(configPropertiesFile, "pentaho.oozie.proxy.user", "devuser");

        if (ShimValues.isShimSecured()) {
            //determine if shim is using impersonation and modify it accordingly
            if (PropertyHandler.getPropertyFromFile(configPropertiesFile,
                    "pentaho.authentication.default.mapping.impersonation.type") == null) {
                PropertyHandler.setProperty(configPropertiesFile, "authentication.superuser.provider", "kerberos");
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.id", "kerberos");
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.principal", "devuser@PENTAHOQA.COM");
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.password", "password");
            }
            else {
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.principal", "devuser@PENTAHOQA.COM");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.password", "password");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.impersonation.type", "simple");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.principal", "hive@PENTAHOQA.COM");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.password", "password");
            }
        }

        // modifying /opt/pentaho/mapreduce in plugin.properties file
        PropertyHandler.setProperty(pluginPropertiesFile, "pmr.kettle.dfs.install.dir",
                "opt/pentaho/mapreduce_" + ShimValues.getDfsInstallDir() );
    }

}