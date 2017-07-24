package profile;

/**
 * Created by Ihar_Chekan on 7/19/2017.
 */
public class ProfileProperties {

  private String sshUser;
  private String sshHost;
  private String sshPassword;
  private String pathToShim;
  private String pathToTestProperties;

  private String restHost;
  private String restUser;
  private String restPassword;

  private String dfsInstallDir;

  private String profileName;

    public String getSshUser() {
      return sshUser;
    }

    public String getSshHost() {
      return sshHost;
    }

    public String getSshPassword() {
      return sshPassword;
    }

    public String getPathToShim() {
      return pathToShim;
    }

    public String getPathToTestProperties() {
      return pathToTestProperties;
    }

    public String getRestHost() {
      return restHost;
    }

    public String getRestUser() {
      return restUser;
    }

    public String getRestPassword() {
      return restPassword;
    }

    public String getDfsInstallDir() {
      return dfsInstallDir;
    }

    public String getProfileName() {
      return profileName;
    }

  private ProfileProperties ( ProfilePropertiesBuilder profilePropertiesBuilder ) {
    this.sshUser=profilePropertiesBuilder.sshUser;
    this.sshHost=profilePropertiesBuilder.sshHost;
    this.sshPassword=profilePropertiesBuilder.sshPassword;
    this.pathToShim=profilePropertiesBuilder.pathToShim;
    this.pathToTestProperties=profilePropertiesBuilder.pathToTestProperties; //optional
    this.restHost=profilePropertiesBuilder.restHost;
    this.restUser=profilePropertiesBuilder.restUser;
    this.restPassword=profilePropertiesBuilder.restPassword;
    this.dfsInstallDir=profilePropertiesBuilder.dfsInstallDir;
    this.profileName=profilePropertiesBuilder.profileName;

  }

  public static class ProfilePropertiesBuilder {

    private String sshUser;
    private String sshHost;
    private String sshPassword;
    private String pathToShim;
    private String pathToTestProperties;

    private String restHost;
    private String restUser;
    private String restPassword;

    private String dfsInstallDir;

    private String profileName;

    public ProfilePropertiesBuilder ( String profileName ) {
      this.profileName = profileName;
    }

    public ProfilePropertiesBuilder setSshUser( String sshUser ) {
      this.sshUser = sshUser;
      return this;
    }

    public ProfilePropertiesBuilder setSshHost( String sshHost ) {
      this.sshHost = sshHost;
      return this;
    }

    public ProfilePropertiesBuilder setSshPassword( String sshPassword ) {
      this.sshPassword = sshPassword;
      return this;
    }

    public ProfilePropertiesBuilder setPathToShim( String pathToShim ) {
      this.pathToShim = pathToShim;
      return this;
    }

    public ProfilePropertiesBuilder setPathToTestProperties( String pathToTestProperties ) {
      this.pathToTestProperties = pathToTestProperties;
      return this;
    }

    public ProfilePropertiesBuilder setRestHost( String restHost ) {
      this.restHost = restHost;
      return this;
    }

    public ProfilePropertiesBuilder setRestUser( String restUser ) {
      this.restUser = restUser;
      return this;
    }

    public ProfilePropertiesBuilder setRestPassword( String restPassword ) {
      this.restPassword = restPassword;
      return this;
    }

    public ProfilePropertiesBuilder setDfsInstallDir( String dfsInstallDir ) {
      this.dfsInstallDir = dfsInstallDir;
      return this;
    }

    public ProfileProperties build() {
      return new ProfileProperties( this );
    }
  }
}
