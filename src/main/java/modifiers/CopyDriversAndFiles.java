package modifiers;

import org.apache.log4j.Logger;
import util.ShimValues;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

/**
 * Created by Ihar_Chekan on 2/6/2017.
 */
public class CopyDriversAndFiles {

    final static Logger logger = Logger.getLogger( CopyDriversAndFiles.class);

    //Copy impala simba driver to appropriate place
    public static void copyImpalaSimbaDriver() {
        if (ShimValues.getHadoopVendor().equalsIgnoreCase("cdh")) {
            try {
                copyDriverFileToShimLib( "ImpalaJDBC41.jar" );
                    logger.info("ImpalaSimbaDriver copy successful to " +
                            Paths.get(ShimValues.getPathToShim() + File.separator + "lib" +
                                    File.separator + "ImpalaJDBC41.jar" ));
            } catch ( NoSuchElementException nsee ) {
              logger.warn( "Impala Simba Driver was not found in ShimConfig folder" );
            } catch (FileAlreadyExistsException ee) {
                logger.info("Impala Simba driver already exists in the shim folder");
            } catch (IOException e) {
                logger.error("IOexception while copying impala simba driver" + e);
            } catch (Exception e) {
                logger.error( e );
            }
        }
    }

    //Copy mysql driver to appropriate place
    public static void copyMySqlDriver() {
        try {
            Path mysqlDriverPath = findFileInThisUtilityParent( "mysql-connector-java-.+?-bin.jar" );
            if (Files.exists(mysqlDriverPath)) {
                Path pathToShim = Paths.get(ShimValues.getPathToShim());
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("data-integration")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent()
                            + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName()));
                    logger.info("MySQL Driver copy successful to " + Paths.get(pathToShim.
                            getParent().getParent().getParent().getParent()
                            + File.separator + "lib" + File.separator + mysqlDriverPath.getFileName()));
                }
                if (pathToShim.getParent().getParent().getParent().getParent().getFileName().toString().
                        equalsIgnoreCase("kettle")) {
                    Files.copy(mysqlDriverPath, Paths.get(pathToShim.getParent().getParent().getParent().getParent().getParent()
                            .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
                            + File.separator + mysqlDriverPath.getFileName()));
                    logger.info("MySQL Driver copy successful to " +
                            Paths.get(pathToShim.getParent().getParent().getParent().getParent().getParent()
                                    .getParent().getParent() + File.separator + "tomcat" + File.separator + "lib"
                                    + File.separator + mysqlDriverPath.getFileName()));
                }
            }
        } catch ( NoSuchElementException nse ) {
            logger.warn( "MySQL Driver was not found in ShimConfig folder." );
        } catch (FileAlreadyExistsException ee) {
            logger.info("MySQL driver already exists in the destination folder");
        } catch (IOException e) {
            logger.error("IOexception while copying MySQL driver" + e);
        } catch (Exception e) {
            logger.error( e );
        }
    }

    // copy sparkSQL driver to appropriate place
    public static void copySparkSqlDriver() {
        if (ShimValues.getHadoopVendor().equalsIgnoreCase("cdh")) {

            String[] filesToCopy = {"ql.jar","SparkJDBC41.jar","TCLIServiceClient.jar"};

            try {
                for (String f : filesToCopy) {
                    copyDriverFileToShimLib( f );
                }
                logger.info( "SparkSQL Driver copy successful to " +
                  Paths.get(ShimValues.getPathToShim() + File.separator ) + "lib/*" );
            } catch ( NoSuchElementException nse ) {
                logger.warn( "SparkSQL Driver was not found in ShimConfig folder" );
            } catch (FileAlreadyExistsException ee) {
                logger.info("SparkSQL driver already exists in the shim folder");
            } catch (IOException e) {
                logger.error("IOexception while copying SparkSQL driver" + e);
            } catch (Exception e) {
                logger.error( e );
            }
        }
    }

    public static void copyLicensesForSpoon () {
        try {
            if ( Paths.get( ShimValues.getPathToShim() ).getParent().getParent().getParent().getParent().getFileName().toString().equals( "data-integration" ) ) {
                Path installedLicensesPath = findFileInThisUtilityParent( ".installedLicenses.xml" );
                if ( Files.exists( installedLicensesPath ) ) {
                     Files.copy( installedLicensesPath, Paths.get( ShimValues.getPathToShim() ).getParent().getParent().getParent().getParent() );
                }
            }
        } catch ( NoSuchElementException nse ) {
            logger.warn( ".installedLicenses.xml file was not found" );
        } catch ( FileAlreadyExistsException faee ) {
            logger.info( ".installedLicenses.xml already exists in the destination folder" );
        } catch ( URISyntaxException use ) {
            logger.error( use );
        } catch ( IOException io ) {
            logger.error( io );
        } catch ( Exception e ) {
            logger.error( e );
        }
    }

    private static void copyDriverFileToShimLib ( String driverFile ) throws NoSuchElementException, FileAlreadyExistsException, IOException, Exception {
        Path driverPath = findFileInThisUtilityParent( driverFile );
        if ( Files.exists( driverPath ) ) {
            Files.copy( driverPath, Paths.get( ShimValues.getPathToShim() + File.separator + "lib" +
              File.separator + driverPath.getFileName() ) );
        }
    }

    private static Path findFileInThisUtilityParent( String regex ) throws NoSuchElementException, FileAlreadyExistsException, IOException, Exception  {
        return Files.find(Paths.get(Paths.get( CopyDriversAndFiles.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
          .getParent().toAbsolutePath().normalize().toString()), 3, (p, bfa) -> bfa.isRegularFile()
          && p.getFileName().toString().matches( regex ) ).findFirst().get();
    }


}
