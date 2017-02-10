package util;

import java.io.File;

/**
 * Created by Ihar_Chekan on 10/14/2016.
 */
public final class ShimValues {

    private static String sshUser;
    private static String sshHost;
    private static String sshPassword;
    private static String pathToShim;
    private static String pathToTestProperties; //optional

    private static String restHost;
    private static String restUser;
    private static String restPassword;

    //This is gained by parsing the response of the "hadoop version" command
    private static String hadoopVendor;
    private static String hadoopVendorVersion;

    // Values, that is determined from xml files
    private static boolean shimSecured;

    // Hardcoded list of files, which will be retrieved from cluster
    private static String[] filesToRetrieve = new String[] {
        "/etc/hadoop/conf/core-site.xml",
        "/etc/hadoop/conf/hdfs-site.xml",
        "/etc/hadoop/conf/mapred-site.xml",
        "/etc/hadoop/conf/yarn-site.xml",
        "/etc/hbase/conf/hbase-site.xml",
        "/etc/hive/conf/hive-site.xml"
    };

    public static void populateValues( String pathToConfigFile ) {
        sshUser = PropertyHandler.getPropertyFromFile( pathToConfigFile , "sshUser");
        sshHost = PropertyHandler.getPropertyFromFile( pathToConfigFile, "sshHost" );
        sshPassword = PropertyHandler.getPropertyFromFile( pathToConfigFile, "password" );
        String pathToShimTemp = PropertyHandler.getPropertyFromFile( pathToConfigFile, "pathToShim" );
        if ( pathToShimTemp.substring(pathToShimTemp.length() - 1).equalsIgnoreCase(File.separator) ) {
            pathToShim = pathToShimTemp;
        } else {
            pathToShim = pathToShimTemp + File.separator;
        }
        String restHostTemp = PropertyHandler.getPropertyFromFile( pathToConfigFile, "restHost");
        if ( restHostTemp == null || restHostTemp.equals("")) {
            restHost = sshHost;
        } else {
            restHost = restHostTemp;
        }
        restUser = PropertyHandler.getPropertyFromFile( pathToConfigFile, "restUser" );
        restPassword = PropertyHandler.getPropertyFromFile( pathToConfigFile, "restPassword" );

        String tempPathToTestProperties = PropertyHandler.getPropertyFromFile( pathToConfigFile, "pathToTestProperties");
        if ( tempPathToTestProperties == null || tempPathToTestProperties.equals("")) {
            pathToTestProperties = null;
        } else pathToTestProperties = tempPathToTestProperties;
    }

    public static void populateValues(String[] configs ) {
        if ( configs[0].substring(configs[0].length() - 1).equalsIgnoreCase(File.separator) ) {
            pathToShim = configs[0];
        } else {
            pathToShim = configs[0] + File.separator;
        }
        sshHost = configs[1];
        sshUser = configs[2];
        sshPassword = configs[3];
        restUser = configs[4];
        restPassword = configs[5];
        if ( configs[6] == null || configs[6].equals("")) {
            restHost = sshHost;
        } else {
            restHost = configs[6];
        }
        pathToTestProperties = configs[7];
    }

    public static void populateValuesAfterDownloading(){
        isSecured();
        readHadoopVendorAndVersion();
    }

    //determine if shim shimSecured and set appropriate value
    private static void isSecured() {
        String secured = XmlPropertyHandler.readXmlPropertyValue(pathToShim + "core-site.xml",
            "hadoop.security.authorization" );
        if (secured == null ) {
        System.out.println("Unable to read 'hadoop.security.authorization' property!!!");
        }
        else if (secured.equalsIgnoreCase("true")) {
            ShimValues.shimSecured = true;
        }
        else { ShimValues.shimSecured = false; }
    }

    //determine hadoop vendor and it`s version
    private static void readHadoopVendorAndVersion() {
        hadoopVendor = HadoopVendorAndVersionParser.hadoopVendorParser(sshUser, sshHost, sshPassword);
        hadoopVendorVersion = HadoopVendorAndVersionParser.hadoopVendorVersionParser(sshUser, sshHost, sshPassword);
    }

    public static String getSshUser() {
        return sshUser;
    }

    public static String getSshHost() {
        return sshHost;
    }

    public static String getSshPassword() {
        return sshPassword;
    }

    public static String getPathToShim() {
        return pathToShim;
    }

    public static String[] getFilesToRetrieve() {
        return filesToRetrieve;
    }

    public static String getPathToTestProperties() {
        return pathToTestProperties;
    }

    public static boolean isShimSecured() {
        return shimSecured;
    }

    public static String getHadoopVendor() {
        return hadoopVendor;
    }

    public static String getHadoopVendorVersion() {
        return hadoopVendorVersion;
    }

    public static String getRestUser() {
        return restUser;
    }

    public static String getRestPassword() {
        return restPassword;
    }

    public static String getRestHost() {
        return restHost;
    }
}
