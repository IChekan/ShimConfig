package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logger.TextAreaAppender;
import org.apache.log4j.Logger;
import util.ShimValues;

import java.io.File;

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
    ChoiceBox shimChoice;
    @FXML
    TextArea output;

    @FXML
    void buttonInit (ActionEvent event) {
        if (event.getTarget() instanceof Button) {
            if (event.getTarget() == buttonStart)
                buttonStartAction();
            else if (event.getTarget() == buttonOpenShim)
                buttonOpenShimAction();
            else if (event.getTarget() == buttonOpenTestProperties)
                buttonOpenTestPropertiesAction();
            else if (event.getTarget() == buttonSetDefaults )
                buttonSetDefaultsAction();
        }
    }

    @FXML
    void initialize() {
        output.setText( "Welcome to Shim Setup!" );
    }

    public void buttonStartAction() {
        output.setText( "" );
        TextAreaAppender.setTextArea(output);

        if (pathToShim.getText().isEmpty() || cluster_node_FQDN.getText().isEmpty() ) {
            logger.info("One or more required field(s) is empty! Only test.properties field is not required!");
        } else if (!dfsInstallDir.getText().matches("^[a-zA-Z0-9]*$") ) {
            logger.info("Only english chars and numbers allowed to be added to /opt/pentaho/mapreduce in plugin.properties file.");
        }
        else {
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
}

