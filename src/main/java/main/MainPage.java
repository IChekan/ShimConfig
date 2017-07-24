package main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logger.TextAreaAppender;
import modifiers.CopyDriversAndFiles;
import org.apache.log4j.Logger;
import profile.Profile;
import profile.ProfileProperties;
import util.ShimValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Ihar_Chekan on 2/1/2017.
 */
public class MainPage {

    final static Logger logger = Logger.getLogger(MainPage.class);

    @FXML
    Button buttonStart;
    @FXML
    Button buttonOpenShim;
    @FXML
    Button buttonOpenTestProperties;
    @FXML
    Button buttonSetDefaults;
    @FXML
    Button buttonSaveProfile;
    @FXML
    TextField pathToShim;
    @FXML
    TextField cluster_node_FQDN;
    @FXML
    TextField sshUser;
    @FXML
    TextField sshPassword;
    @FXML
    TextField restUser;
    @FXML
    TextField restPassword;
    @FXML
    TextField testPropertiesPath;
    @FXML
    TextField restHost;
    @FXML
    TextField dfsInstallDir;
    @FXML
    TextField profileName;
    @FXML
    ChoiceBox shimChoice;
    @FXML
    TextArea output;
    @FXML
    ComboBox<String> profileChooser;

    @FXML
    void buttonInit (ActionEvent event) {
        if (event.getTarget() instanceof Button) {
            if (event.getTarget() == buttonStart)
                buttonStartAction();
            else if (event.getTarget() == buttonOpenShim)
                buttonOpenShimAction();
            else if (event.getTarget() == buttonOpenTestProperties)
                buttonOpenTestPropertiesAction();
            else if ( event.getTarget() == buttonSaveProfile )
                buttonSaveProfileAction();
            else if (event.getTarget() == buttonSetDefaults )
                buttonSetDefaultsAction();
        }
    }
    @FXML
    void compoBoxOnShowing (Event event) {
        if ( event.getTarget() instanceof ComboBox ) {
            if ( event.getTarget() == profileChooser )
                comboBoxProfileChooser();
        }
    }

    @FXML
    void initialize() {
        TextAreaAppender.setTextArea(output);
        logger.info( "Welcome to Shim Configuration Utility!"
          + "\n"
          + "\nThis utility was created by Ihar Chekan"
          + "\nYou can find latest version and changelog here: https://github.com/IChekan/ShimConfig"
          + "\n"
          + "\nIf you have any questions/issues with this utility, please contact me:"
          + "\ne-mail: Igor_926@yahoo.com"
          + "\nSkype: Iliodor926"
          + "\n" );
        profileChooser.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed( ObservableValue ov, String t, String t1) {
                comboBoxSomethingChanged( ov );
            }
        });
    }

    public void buttonStartAction() {
        if (pathToShim.getText().isEmpty() || cluster_node_FQDN.getText().isEmpty() ) {
            logger.info("One or more required field(s) is empty! Only test.properties field is not required!");
        } else if (!dfsInstallDir.getText().matches("^[a-zA-Z0-9]*$") ) {
            logger.info("Only english chars and numbers allowed to be added to /opt/pentaho/mapreduce in plugin.properties file.");
        }
        else {
            //output.setText( "" );
            buttonStart.setDisable( true );

            String[] values = new String[9];
            values[0] = pathToShim.getText();
            values[1] = cluster_node_FQDN.getText();
            if ( sshUser.getText().isEmpty()) {
                values[2] = "devuser";
            } else { values[2] = sshUser.getText(); }
            if ( sshPassword.getText().isEmpty() ) {
                values[3] = "password";
            } else { values[3] = sshPassword.getText(); }
            if ( restUser.getText().isEmpty() ) {
                values[4] = "admin";
            } else { values[4] = restUser.getText(); }
            if ( restPassword.getText().isEmpty() ) {
                values[5] = "admin";
            } else { values[5] = restPassword.getText(); }
            values[6] = restHost.getText();
            values[7] = dfsInstallDir.getText();
            values[8] = testPropertiesPath.getText();

            Thread thread = new Thread() {
                public void run() {
                    ShimValues.populateValues(values);
                    ShimConfigRun shimConfigRun = new ShimConfigRun();
                    shimConfigRun.shimConfigRun();
                    buttonStart.setDisable( false );
                }
            };
            thread.start();

        }
    }

    public void buttonOpenShimAction() {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Shim directory");
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            pathToShim.setText(file.getAbsolutePath());
        }
    }

    public void buttonOpenTestPropertiesAction() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose test.properties file");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            testPropertiesPath.setText(file.getAbsolutePath());
        }
    }

    public void buttonSetDefaultsAction() {
        sshUser.setText("devuser");
        sshPassword.setText("password");
        restUser.setText("admin");
        restPassword.setText("admin");
    }

    public void buttonSaveProfileAction() {
        if (profileName.getText().isEmpty() ) {
            logger.info("Choose profile name!");
        } else if (!dfsInstallDir.getText().matches("^[a-zA-Z0-9]*$") ) {
            logger.info("Only english chars and numbers is allowed for profile name.");
        }
        else {

            try {
                Properties props = new Properties();
                props.setProperty( "sshUser", sshUser.getText() );
                props.setProperty( "password", sshPassword.getText() );
                props.setProperty( "sshHost", cluster_node_FQDN.getText() );
                props.setProperty( "restPassword", restPassword.getText() );
                props.setProperty( "pathToShim", pathToShim.getText() );
                props.setProperty( "pathToTestProperties", testPropertiesPath.getText() );
                props.setProperty( "restHost", restHost.getText() );
                props.setProperty( "restUser", restUser.getText() );
                props.setProperty( "restPassword", restPassword.getText() );
                props.setProperty( "dfsInstallDir", dfsInstallDir.getText() );
                Path path = Paths.get ( CopyDriversAndFiles.getRootUtilityFolder() + File.separator + "profiles"
                  + File.separator + profileName.getText() + ".profile" );
                if ( !Files.exists( path.getParent() ) ) {
                    Files.createDirectory( path.getParent() );
                }
                OutputStream out = Files.newOutputStream( path );
                props.store( out, "This is a profile for ShimConfig" );
                out.close();
                logger.info( "Profile with name " + profileName.getText() + " saved." );
            } catch ( Exception e ) {
                logger.error( "Was not able to save profile: " + e );
            }
        }
    }

    public void comboBoxProfileChooser () {
        ArrayList<String> allProfileNames = Profile.loadAllProfileNamesFromDisk();

        if (allProfileNames != null) {
            profileChooser.getItems().setAll( allProfileNames );
        }
    }

    public void comboBoxSomethingChanged ( ObservableValue ov ) {
        ProfileProperties profileProperties = Profile.loadProfileFromDisk ( ov.getValue().toString() );

        sshUser.setText( profileProperties.getSshUser() );
        sshPassword.setText( profileProperties.getSshPassword() );
        cluster_node_FQDN.setText( profileProperties.getSshHost() );
        restPassword.setText( profileProperties.getRestPassword() );
        pathToShim.setText( profileProperties.getPathToShim() );
        testPropertiesPath.setText( profileProperties.getPathToTestProperties() );
        restHost.setText( profileProperties.getRestHost() );
        restUser.setText( profileProperties.getRestUser() );
        restPassword.setText( profileProperties.getRestPassword() );
        dfsInstallDir.setText( profileProperties.getDfsInstallDir() );
    }
}

