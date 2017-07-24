package profile;

import modifiers.CopyDriversAndFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static modifiers.CopyDriversAndFiles.getRootUtilityFolder;

/**
 * Created by Ihar_Chekan on 7/24/2017.
 */
public class Profile {

  public static ArrayList<String> loadAllProfileNamesFromDisk() {
    ArrayList<Path> allProfilePaths;
    ArrayList<String> allProfileNamesProperties = new ArrayList<>();
    try {
      allProfilePaths = (ArrayList<Path>) getAllProfilePaths();
      for ( Path profilePath : allProfilePaths ) {
        allProfileNamesProperties.add( profilePath.getFileName().toString().split( "\\." )[0] );
      }
    } catch ( Exception e ) {
      // do nothing
    }
    return allProfileNamesProperties;
  }

  public static ArrayList<ProfileProperties> loadAllProfilesFromDisk () {
    ArrayList<Path> allProfilePaths;
    ArrayList<ProfileProperties> allProfileProperties = new ArrayList<>(  );
    try {
      allProfilePaths = (ArrayList<Path>) getAllProfilePaths();
      for ( Path profilePath : allProfilePaths ) {
        allProfileProperties.add( createProfileFromPath ( profilePath ) );
      }

    } catch ( Exception e ) {
      // do nothing
    }
    return allProfileProperties;
  }

  private static List<Path> getAllProfilePaths () throws IOException {
    return ( Files.find( Paths.get( getRootUtilityFolder() + File.separator + "profiles" ), 1, ( p, bfa ) -> bfa.isRegularFile()
      && p.getFileName().toString().matches( ".*\\.profile" ) ).collect( Collectors.toList() ) );
  }

  private static ProfileProperties createProfileFromPath ( Path path ) {
    Properties props = new Properties();
    try {
      InputStream in = Files.newInputStream( path );
      props.load( in );
      in.close();
    } catch ( IOException ioe ) {
      //
    }
    return new ProfileProperties.ProfilePropertiesBuilder( path.getFileName().toString().split( "\\." )[0] )
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
  }

  public static ProfileProperties loadProfileFromDisk ( String profileName ) {
    Path path = Paths.get( CopyDriversAndFiles.getRootUtilityFolder() + File.separator + "profiles"
      + File.separator + profileName + ".profile" );
    return createProfileFromPath( path );
  }

}
