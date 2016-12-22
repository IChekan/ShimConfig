import modifiers.AddCrossPlatform;
import modifiers.ModifyPluginConfigProperties;
import modifiers.ModifyTestProperties;
import util.SSHUtils;
import util.ShimValues;

import java.io.IOException;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
class ShimConfigRun {

    public void shimConfigRun () {

        for (int i = 0; i < ShimValues.getFilesToRetrieve().length; i++) {
            SSHUtils.copyFileBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
                    ShimValues.getFilesToRetrieve()[i], ShimValues.getPathToShim());
        }

        //set Values after copying the *-site.xml files
        ShimValues.populateValuesAfterDownloading();

        // Adding cross-platform to mapred-site.xml
        AddCrossPlatform addCrossPlatform = new AddCrossPlatform();
        addCrossPlatform.addCrossPlatform(ShimValues.getPathToShim() + "mapred-site.xml");

        // Modify plugin.properties
        ModifyPluginConfigProperties modifyPluginConfigProperties = new ModifyPluginConfigProperties();
        modifyPluginConfigProperties.modifyPluginProperties();

        // Optional: Modify test.properties
        try {
            if (ShimValues.getPathToTestProperties() != null || !"".equals(ShimValues.getPathToTestProperties()) ) {
                ModifyTestProperties.modifyAllTestProperties(ShimValues.getPathToTestProperties());
            }
        } catch ( ArrayIndexOutOfBoundsException e) {
            // do nothing
        } catch ( IOException e ) {
            System.out.println( "I see IOexception..." );
        }

    }

}
