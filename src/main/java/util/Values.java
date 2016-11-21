package util;

/**
 * Created by Ihar_Chekan on 10/14/2016.
 */
public final class Values {

    private static String user;
    private static String host;
    private static String password;
    private static String pathToShim;
    private static String pathToTestProperties; //optional

    //This is gained by parsing the response of the "hadoop version" command
    private static String hadoopVendor;
    private static String hadoopVendorVersion;

    // Values, that is determined from xml files
    private static boolean secured;

    // Hardcoded list of files, which will be retrieved from cluster
    private static String[] filesToRetrieve = new String[] {
        "/etc/hadoop/conf/core-site.xml",
        "/etc/hadoop/conf/hdfs-site.xml",
        "/etc/hadoop/conf/mapred-site.xml",
        "/etc/hadoop/conf/yarn-site.xml",
        "/etc/hbase/conf/hbase-site.xml",
        "/etc/hive/conf/hive-site.xml"
    };

    public static void populateValues ( String pathToConfigFile ) {
        PropertyHandler propertyHandler = new PropertyHandler();
        user = propertyHandler.getPropertyFromFile( pathToConfigFile , "user");
        host = propertyHandler.getPropertyFromFile( pathToConfigFile, "host" );
        password = propertyHandler.getPropertyFromFile( pathToConfigFile, "password" );
        pathToShim = propertyHandler.getPropertyFromFile( pathToConfigFile, "pathToShim" );

        String tempPathToTestProperties = propertyHandler.getPropertyFromFile( pathToConfigFile, "pathToTestProperties");
        if ( tempPathToTestProperties == null || tempPathToTestProperties.equals("")) {
            pathToTestProperties = null;
        } else pathToTestProperties = tempPathToTestProperties;
    }

    public static void populateValuesAfterDownloading(){
        isSecured();
        readHadoopVendorAndVersion();
    }

    //determine if shim secured and set appropriate value
    private static void isSecured() {
        XmlPropertyHandler xmlPropertyHandler = new XmlPropertyHandler();
        String secured = xmlPropertyHandler.readXmlPropertyValue(pathToShim + "core-site.xml",
            "hadoop.security.authorization" );
        if (secured == null ) {
        System.out.println("Unable to read 'hadoop.security.authorization' property!!!");
        }
        else if (secured.equalsIgnoreCase("true")) {
            Values.secured = true;
        }
        else { Values.secured = false; }
    }

    //determine hadoop vendor and it`s version
    private static void readHadoopVendorAndVersion() {
        hadoopVendor = HadoopVendorAndVersionParser.hadoopVendorParser(user,host,password);
        hadoopVendorVersion = HadoopVendorAndVersionParser.hadoopVendorVersionParser(user,host,password);
    }

    public static String getUser() {
        return user;
    }

    public static String getHost() {
        return host;
    }

    public static String getPassword() {
        return password;
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

    public static boolean getSecured() {
        return secured;
    }

    public static String getHadoopVendor() {
        return hadoopVendor;
    }

    public static String getHadoopVendorVersion() {
        return hadoopVendorVersion;
    }

}
