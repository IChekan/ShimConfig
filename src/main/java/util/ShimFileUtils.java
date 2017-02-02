package util;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Ihar_Chekan on 1/5/2017.
 */
public class ShimFileUtils {

    //
    public static File getFileFromZipAndSaveAsTempFile (byte[] inputByteArr, String fileName ) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(inputByteArr));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().contains(fileName)) {
                byte[] buffer = new byte[15000];
                int len;
                while ((len = zipInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                File temp = File.createTempFile( "tempFile", ".tmp" );
                temp.deleteOnExit();
                FileUtils.writeStringToFile(temp,out.toString());
                out.close();
                zipInputStream.close();
                return temp;
            }
        }
        out.close();
        zipInputStream.close();
        return null;
    }
}
