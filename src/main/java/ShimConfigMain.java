import util.ShimValues;

/**
 * Created by Ihar_Chekan on 10/13/2016.
 */
public class ShimConfigMain {

    public static void main(String[] arg) {
        if (arg.length != 1) {
            System.err.println("usage: java thisJar [SomeConfigFile]] ");
            System.exit(-1);
        }

        // Get values from provided file and set properties. Note, that some values are populated after *-site.xml files are downloaded!
        ShimValues.populateValues( arg[0]);

        // executes shim config
        ShimConfigRun shimConfigRun = new ShimConfigRun();
        shimConfigRun.shimConfigRun();



    }

}