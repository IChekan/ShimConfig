package util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;

/**
 * Created by Ihar_Chekan on 10/13/2016.
 */
public class SSHUtils {

    public static void copyFileBySSH ( String user, String host, String password, String retrieveFile, String whereToSaveFile ) {
    FileOutputStream fileOutputStream=null;

        try{
            String prefix=null;
            if(new File(whereToSaveFile).isDirectory()){
            prefix=whereToSaveFile;
            }

            JSch jsch=new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            // exec 'scp -f rfile' remotely
            String command="scp -f "+retrieveFile;
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out=channel.getOutputStream();
            InputStream in=channel.getInputStream();

            channel.connect();

            byte[] buf=new byte[1024];

            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();

            while(true){
                int c=checkAck(in);
                if(c!='C'){
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize=0L;
                while(true){
                    if(in.read(buf, 0, 1)<0){
                        // error
                        break;
                    }
                    if(buf[0]==' ')break;
                    filesize=filesize*10L+(long)(buf[0]-'0');
                }

                String file=null;
                for(int i=0;;i++){
                    in.read(buf, i, 1);
                    if(buf[i]==(byte)0x0a){
                        file=new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();

                // read a content of lfile
                fileOutputStream=new FileOutputStream(prefix==null ? whereToSaveFile : prefix+file);
                int foo;

                while(true){
                    if(buf.length<filesize) foo=buf.length;
                    else foo=(int)filesize;
                    foo=in.read(buf, 0, foo);
                    if(foo<0){
                        // error
                        break;
                    }
                    fileOutputStream.write(buf, 0, foo);
                    filesize-=foo;
                    if(filesize==0L) break;
                }
                fileOutputStream.close();
                fileOutputStream=null;

                if(checkAck(in)!=0){
                    System.exit(0);
                }

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();
            }

            session.disconnect();

            System.out.println( retrieveFile + " was copied from cluster to " + whereToSaveFile );
        }
        catch(Exception e){
            System.out.println(e);
            try{if(fileOutputStream!=null)fileOutputStream.close();}catch(Exception ee){}
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b=in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if(b==0) return b;
        if(b==-1) return b;

        if(b==1 || b==2){
            StringBuffer sb=new StringBuffer();
            int c;
            do {
                c=in.read();
                sb.append((char)c);
            }
            while(c!='\n');
            if(b==1){ // error
                System.out.print(sb.toString());
            }
            if(b==2){ // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    public static String getCommandResponseBySSH ( String user, String host, String password, String command ) {
        String outputStr = "";

        try {

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            // exec 'command' remotely
            //String command = "some command";
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // X Forwarding
            // channel.setXForwarding(true);
            //channel.setInputStream(System.in);
            channel.setInputStream(null);
            //channel.setOutputStream(System.out);
            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    outputStr = outputStr + (new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    if(in.available()>0) continue;
// Do I need exit status?
//                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){System.out.println(ee);}
            }
            channel.disconnect();
            session.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return outputStr;
    }
}