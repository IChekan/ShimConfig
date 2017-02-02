package main;

import modifiers.AddCrossPlatform;
import modifiers.ModifyPluginConfigProperties;
import modifiers.ModifyTestProperties;
import util.SSHUtils;
import util.ShimValues;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Ihar_Chekan on 10/19/2016.
 */
class ShimConfigRun {

    public void shimConfigRun () {

        SSHUtils.getCommandResponseBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
                "echo password | kinit");

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
            System.out.println( "IOexception: " + e );
        }

        //Copy impala simba driver to appropriate place
        if (ShimValues.getHadoopVendor().equalsIgnoreCase("cdh")) {
            try {
                Path impalaSimbaDriverPath = Files.find(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString()), 1, (p, bfa) -> bfa.isRegularFile()
                        && p.getFileName().toString().matches("ImpalaJDBC41.jar")).findFirst().get();
                if (Files.exists(impalaSimbaDriverPath)) {
                    Files.copy(impalaSimbaDriverPath, Paths.get(ShimValues.getPathToShim() + File.separator + "lib"));
                    System.out.println("ImpalaSimbaDriver copy successful");
                }
            } catch (FileAlreadyExistsException ee) {
                System.out.println("Impala Simba driver already exists in shim folder");
            } catch (IOException e) {
                System.out.println("IOexception while copying impala simba driver" + e);
            } catch (Exception e) {
                //
            }
        }

        //Copy mysql driver to appropriate place
        try {
            Path mysqlDriverPath = Files.find( Paths.get( Paths.get(".").toAbsolutePath().normalize().toString() ) , 1 , (p, bfa ) -> bfa.isRegularFile()
                    && p.getFileName().toString().matches( "mysql-connector-java-.+?-bin.jar" ) ).findFirst().get();
            if (Files.exists(mysqlDriverPath)) {
                Path pathToShim = Paths.get(ShimValues.getPathToShim());
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("data-integration")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent()
                            + File.separator + "lib"));
                }
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("kettle")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent().getParent()
                            .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"));
                }
            }
        } catch (FileAlreadyExistsException ee) {
            System.out.println("MySQL driver already exists destination folder");
        }
        catch (IOException e ) {
            System.out.println("IOexception while copying MySQL driver" + e);
        }
        catch (Exception e ) {
            //
        }

        System.out.println("Finished. Please check log to be sure all is ok.");
        System.exit(0);
    }
}
