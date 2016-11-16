import modifiers.AddCrossPlatform;
import modifiers.ModifyPluginConfigProperties;
import modifiers.ModifyTestProperties;
import util.*;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
class ShimConfigRun {

    public void shimConfigRun () {

        for (int i = 0; i < Values.getFilesToRetrieve().length; i++) {
            CopyFileSSH.copyFileBySSH(Values.getUser(), Values.getHost(), Values.getPassword(),
                    Values.getFilesToRetrieve()[i], Values.getPathToShim());
        }

        //set Values after copying the *-site.xml files
        Values.populateValuesAfterDownloading();

        // Adding cross-platform to mapred-site.xml
        AddCrossPlatform addCrossPlatform = new AddCrossPlatform();
        addCrossPlatform.addCrossPlatform(Values.getPathToShim() + "mapred-site.xml");

        // Modify plugin.properties
        ModifyPluginConfigProperties modifyPluginConfigProperties = new ModifyPluginConfigProperties();
        modifyPluginConfigProperties.modifyPluginProperties();

        // Optional: Modify test.properties
        try {
            if (Values.getPathToTestProperties() != null) {
                ModifyTestProperties.modifyTestProperties(Values.getPathToTestProperties());
            }
        } catch ( ArrayIndexOutOfBoundsException e) {
            // do nothing
        }

    }

}
