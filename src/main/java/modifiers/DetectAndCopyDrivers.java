package modifiers;

import util.ShimValues;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Ihar_Chekan on 2/6/2017.
 */
public class DetectAndCopyDrivers {

    //Copy impala simba driver to appropriate place
    public static void copyImpalaSimbaDriver() {
        if (ShimValues.getHadoopVendor().equalsIgnoreCase("cdh")) {
            try {
                Path impalaSimbaDriverPath = Files.find(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString()), 1, (p, bfa) -> bfa.isRegularFile()
                        && p.getFileName().toString().matches("ImpalaJDBC41.jar")).findFirst().get();
                if (Files.exists(impalaSimbaDriverPath)) {
                    Files.copy(impalaSimbaDriverPath, Paths.get(ShimValues.getPathToShim() + File.separator + "lib" +
                    File.separator + impalaSimbaDriverPath.getFileName()));
                    System.out.println("ImpalaSimbaDriver copy successful from " + impalaSimbaDriverPath + " to " +
                            Paths.get(ShimValues.getPathToShim() + File.separator + "lib" +
                                    File.separator + impalaSimbaDriverPath.getFileName()));
                }
            } catch (FileAlreadyExistsException ee) {
                System.out.println("Impala Simba driver already exists in the shim folder");
            } catch (IOException e) {
                System.out.println("IOexception while copying impala simba driver" + e);
            } catch (Exception e) {
                //
            }
        }
    }

    //Copy mysql driver to appropriate place
    public static void copyMySqlDriver() {
        try {
            Path mysqlDriverPath = Files.find(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString()), 1, (p, bfa) -> bfa.isRegularFile()
                    && p.getFileName().toString().matches("mysql-connector-java-.+?-bin.jar")).findFirst().get();
            if (Files.exists(mysqlDriverPath)) {
                Path pathToShim = Paths.get(ShimValues.getPathToShim());
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("data-integration")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent()
                            + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName()));
                    System.out.println("MySQL Driver copy successful from " + mysqlDriverPath + " to " + Paths.get(pathToShim.
                            getParent().getParent().getParent().getParent()
                            + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName()));
                }
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("kettle")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent().getParent()
                            .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
                            + File.separator + mysqlDriverPath.getFileName()));
                    System.out.println("MySQL Driver copy successful from " + mysqlDriverPath + " to " +
                            Paths.get(pathToShim.getParent().getParent().getParent().getParent().getParent()
                                    .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
                                    + File.separator + mysqlDriverPath.getFileName()));
                }
            }
        } catch (FileAlreadyExistsException ee) {
            System.out.println("MySQL driver already exists in the destination folder");
        } catch (IOException e) {
            System.out.println("IOexception while copying MySQL driver" + e);
        } catch (Exception e) {
            //
        }
    }
}
