package net.raumzeitfalle.demos.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class DemoController implements Initializable {

    @FXML
    public net.raumzeitfalle.fx.filechooser.FileChooser fileChooser;

    @FXML
    public Button actionButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
