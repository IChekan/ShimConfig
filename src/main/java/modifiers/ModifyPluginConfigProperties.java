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
        if (ShimValues.getShimSecured()) {
            SSHUtils.copyFileBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
                    "/etc/krb5.conf", ShimValues.getPathToShim() );
            System.out.println("krb5.conf copied from cluster to shim location. Please move it to appropriate place.");
        }

        // Determine shim folder
        File f = new File(ShimValues.getPathToShim());
        String shimFolder = f.getName();
        File hadoopConfigurationsFolder = new File(f.getParent());
        String pluginPropertiesFile = hadoopConfigurationsFolder.getParent() + File.separator + "plugin.properties";
        String configPropertiesFile = ShimValues.getPathToShim() + File.separator + "config.properties";

        PropertyHandler propertyHandler = new PropertyHandler();
        propertyHandler.setProperty(pluginPropertiesFile, "active.hadoop.configuration", shimFolder);

        propertyHandler.setProperty(configPropertiesFile, "pentaho.oozie.proxy.user", "devuser");

        if (ShimValues.getShimSecured()) {
            //determine if shim is using impersonation and modify it accordingly
            if (propertyHandler.getPropertyFromFile(configPropertiesFile,
                    "pentaho.authentication.default.mapping.impersonation.type") == null) {
                propertyHandler.setProperty(configPropertiesFile, "authentication.superuser.provider", "kerberos");
                propertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.id", "kerberos");
                propertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.principal", "devuser@PENTAHOQA.COM");
                propertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.password", "password");
            }
            else {
                propertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.principal", "devuser@PENTAHOQA.COM");
                propertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.password", "password");
                propertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.impersonation.type", "simple");
                propertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.principal", "devuser@PENTAHOQA.COM");
                propertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.password", "password");
            }
        }
    }

}