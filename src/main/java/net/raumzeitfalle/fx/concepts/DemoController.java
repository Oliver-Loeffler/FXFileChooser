/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2020 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
