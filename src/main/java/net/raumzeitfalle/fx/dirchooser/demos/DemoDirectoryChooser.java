/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.dirchooser.demos;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.dirchooser.DirectoryChooser;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoDirectoryChooser extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    private DirectoryChooser dirChooser;
    
    private Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        dirChooser = new DirectoryChooser(Skin.DARK);
        dirChooser.useCancelButtonProperty().setValue(true);
        dirChooser.onSelect(()->{
            Path selectedDir = dirChooser.selectedDirectoryProperty().get();
            showMessage("Selected:", selectedDir.normalize().toAbsolutePath().toString());
        });
        dirChooser.onCancel(()->{
            showMessage("Cancelled:", "One can hide the cancel button if not needed.");
        });
        scene = new Scene(dirChooser);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    @Override
    public void stop() {
        dirChooser.shutdown();
    }

    private void showMessage(String action, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(scene.getWindow());
        alert.setTitle("DirectoryChooser");
        alert.setHeaderText(action);
        alert.setContentText(message);
        alert.show();
    }
}
