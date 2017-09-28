package profile;

import modifiers.CopyDriversAndFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static modifiers.CopyDriversAndFiles.getRootUtilityFolder;

/**
 * Created by Ihar_Chekan on 7/24/2017.
 */
public class Profile {

  private static final String PROFILE_DIRECTORY = "profiles";

  private static final String PROFILE_EXTENSION = "profile";

  private static final ProfileFileMatcher PROFILE_FILE_MATCHER = new ProfileFileMatcher();

  public static List<String> loadAllProfileNamesFromDisk() {
    try {
      List<String> profileNames = new ArrayList<>();
      List<Path> profilePaths = getAllProfilePaths();
      profilePaths.forEach(profilePath -> profileNames.add(profileNameFor(profilePath)));
      return profileNames;
    } catch ( Exception e ) {
      throw new RuntimeException("An error occurred while loading profile names", e);
    }
  }

  public static List<ProfileProperties> loadAllProfilesFromDisk () {
    try {
      List<ProfileProperties> allProfileProperties = new ArrayList<>();
      List<Path> allProfilePaths = getAllProfilePaths();
      allProfilePaths.forEach(profilePath -> allProfileProperties.add( loadProfileFromPath ( profilePath ) ));
      return allProfileProperties;
    } catch ( Exception e ) {
      throw new RuntimeException("An error occurred while loading profiles", e);
    }
  }

  private static List<Path> getAllProfilePaths () throws IOException {
    Path profilesDirectory = Paths.get( getRootUtilityFolder() + File.separator + PROFILE_DIRECTORY);
    return Files.find( profilesDirectory , 1, PROFILE_FILE_MATCHER ).collect( Collectors.toList() );
  }

  private static ProfileProperties loadProfileFromPath ( Path path ) {
    Properties props = new Properties();
    try (InputStream profileStream = Files.newInputStream( path )) {
      props.load( profileStream );
      return new ProfileProperties.ProfilePropertiesBuilder( profileNameFor(path) )
              .setSshUser( props.getProperty( "sshUser" ) )
              .setSshHost( props.getProperty( "sshHost" ) )
              .setSshPassword( props.getProperty( "password" ) )
              .setRestPassword( props.getProperty( "restPassword" ) )
              .setPathToShim( props.getProperty( "pathToShim" ) )
              .setPathToTestProperties( props.getProperty( "pathToTestProperties" ) )
              .setRestHost( props.getProperty( "restHost" ) )
              .setRestUser( props.getProperty( "restUser" ) )
              .setRestPassword( props.getProperty( "restPassword" ) )
              .setDfsInstallDir( props.getProperty( "dfsInstallDir" ) )
              .build();
    } catch ( IOException ioe ) {
      throw new RuntimeException("An error occurred while loading profile from " + path.toString(), ioe);
    }
  }

  public static ProfileProperties loadProfileFromDisk ( String profileName ) {
    Path path = Paths.get( CopyDriversAndFiles.getRootUtilityFolder() + File.separator + PROFILE_DIRECTORY
      + File.separator + profileName + "." + PROFILE_EXTENSION );
    return loadProfileFromPath( path );
  }

  static String profileNameFor(Path path) {
    return getBaseName(path.getFileName());
  }

  static String getBaseName(Path file) {
    return file.toString().split( "\\." )[0];
  }

  private static final class ProfileFileMatcher implements BiPredicate<Path, BasicFileAttributes> {

    public boolean test(Path filePath, BasicFileAttributes fileAttributes) {
      return fileAttributes.isRegularFile() && filePath.getFileName().toString().matches( ".*\\.profile" );
    }

  }

}
