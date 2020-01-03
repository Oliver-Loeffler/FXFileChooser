package net.raumzeitfalle.fx.concepts;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class DemoController implements Initializable {

    @FXML
    private Button action;

    @FXML
    private Label label;

    private final Random random = new Random();

    private final String welcomeText;

    public DemoController(String data) {
        this.welcomeText = data;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        action.setOnAction(this::performAction);
        label.setText(this.welcomeText);
    }

    private void performAction(ActionEvent actionEvent) {
        int number = random.nextInt();
        Platform.runLater(()->label.setText("RandomInt: " + number));
    }
}
