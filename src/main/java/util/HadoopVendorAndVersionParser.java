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
            return (Character.toString(tempStringArr[0].charAt(16)) +
                    Character.toString(tempStringArr[0].charAt(18)));
        }
        else {
            return (Character.toString(tempStringArr[0].charAt(13)) +
                    Character.toString(tempStringArr[0].charAt(15)));
        }
    }
}
