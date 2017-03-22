package util;

/**
 * Created by Ihar_Chekan on 11/16/2016.
 */
public class HadoopVendorAndVersionParser {

    //currently supports only cdh and hdp
    public static String hadoopVendorParser(String user,String host,String password) {
        String[] tempStringArr = SSHUtils.getCommandResponseBySSH(user,host,password, "hadoop version").split("\\r?\\n");
        if (tempStringArr[0].contains("cdh")) {
            return "cdh";
        }
        else {
            return "hdp";
        }
    }

    //currently supports only cdh and hdp
    public static String hadoopVendorVersionParser(String user,String host,String password) {
        String[] tempStringArr = SSHUtils.getCommandResponseBySSH(user,host,password, "hadoop version").split("\\r?\\n");
        if (tempStringArr[0].contains("cdh")) {
            String[] versionsArr = tempStringArr[0].split( "cdh" );
            String shimVer = versionsArr[1];
            String[] shimVerArr = shimVer.split( "\\." );

            return ( shimVerArr[0] + shimVerArr[1] );
        }
        else {
            String[] versionsArr = tempStringArr[0].split( "\\." );
            return ( versionsArr[3] + versionsArr[4] );
        }
    }
}
