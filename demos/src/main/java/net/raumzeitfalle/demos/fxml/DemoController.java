package net.raumzeitfalle.demos.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class DemoController implements Initializable {

    @FXML
    public net.raumzeitfalle.fx.filechooser.FileChooser fileChooser;

    @FXML
    public Button actionButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionButton.setOnAction(evt-> {
            fileChooser.currentSearchPath().setValue(Path.of(fileChooser.pathProperty().getValue()));
        });
    }
}
