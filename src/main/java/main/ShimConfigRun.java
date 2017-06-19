package main;

import modifiers.AddCrossPlatform;
import modifiers.CopyFilesFromCluster;
import modifiers.CopyDriversAndFiles;
import modifiers.ModifyPluginConfigProperties;
import modifiers.ModifyTestProperties;
import org.apache.log4j.Logger;
import util.SSHUtils;
import util.ShimValues;

import java.io.IOException;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
class ShimConfigRun {

    final static Logger logger = Logger.getLogger(ShimConfigRun.class);

    public void shimConfigRun () {

        CopyFilesFromCluster copyFilesFromCluster = new CopyFilesFromCluster();
        copyFilesFromCluster.copySiteXmlFilesFromCluster();

        //set Values after copying the *-site.xml files
        ShimValues.populateValuesAfterDownloading();

        if (ShimValues.isShimSecured()) {
            SSHUtils.getCommandResponseBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
                "echo password | kinit");
        }

        // Adding cross-platform to mapred-site.xml
        AddCrossPlatform addCrossPlatform = new AddCrossPlatform();
        addCrossPlatform.addCrossPlatform(ShimValues.getPathToShim() + "mapred-site.xml");

        // Copying krb5.conf
        copyFilesFromCluster.copyKrb5conf();

        // Modify plugin.properties
        ModifyPluginConfigProperties modifyPluginConfigProperties = new ModifyPluginConfigProperties();
        modifyPluginConfigProperties.modifyPluginProperties();

        // Optional: Modify test.properties
        try {
            if (ShimValues.getPathToTestProperties() != null && !"".equals(ShimValues.getPathToTestProperties()) ) {
                ModifyTestProperties.modifyAllTestProperties(ShimValues.getPathToTestProperties());
            }
        } catch ( ArrayIndexOutOfBoundsException e) {
            logger.error( "ArrayIndexOutOfBoundsException: " + e );
        } catch ( IOException e ) {
            logger.error( "IOexception: " + e );
        }

        //Copy impala simba and MySQL drivers and license file to appropriate place
        CopyDriversAndFiles.copyLicensesForSpoon();
        CopyDriversAndFiles.copyImpalaSimbaDriver();
        CopyDriversAndFiles.copyMySqlDriver();
        CopyDriversAndFiles.copySparkSqlDriver();

        logger.info("Finished. Please check the log above to be sure all is ok.");
    }
}
