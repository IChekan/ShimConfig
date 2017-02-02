package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {

    /** Holder of a switchable scene. */
    @FXML
    private StackPane sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }


}
