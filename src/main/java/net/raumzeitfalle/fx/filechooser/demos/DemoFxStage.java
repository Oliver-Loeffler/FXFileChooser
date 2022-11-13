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
package net.raumzeitfalle.fx.filechooser.demos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.Skin;

import java.nio.file.Path;

public class DemoFxStage extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Show File Chooser");
        FXFileChooserStage fc = FXFileChooserStage.create(Skin.DARK);
        button.setOnAction(evt -> fc.showOpenDialog(primaryStage)
                                    .ifPresent(this::showSelection));

        Scene scene = new Scene(button);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    private void showSelection(Path selectedPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("File Selection");
        alert.setContentText(selectedPath.toString());
        alert.show();
    }
}
