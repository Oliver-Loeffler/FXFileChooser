/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2022 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.dirchooser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.raumzeitfalle.fx.filechooser.FileChooser;
import net.raumzeitfalle.fx.filechooser.PathSupplier;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DirectoryChooser extends AnchorPane {

    private final DirectoryChooserController controller;

    public DirectoryChooser() {
        this(Skin.DARK);
    }
    
    public DirectoryChooser(Skin skin) {
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        controller = new DirectoryChooserController();
        loader.setController(controller);
        Parent view;
        try {
            view = loader.load();
        } catch (Exception e) {
            view = handleErrorOnLoad(fileName, resource, e);
        }
        this.getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        Skin.applyTo(this, skin);
    }

    private VBox handleErrorOnLoad(String fileName, URL resource, Exception e) {
        StringWriter errors = new StringWriter();
        PrintWriter writer = new PrintWriter(errors);
        writer.println("FXML: "+ String.valueOf(fileName));
        writer.println("Controller: " + controller.getClass().getName());
        e.printStackTrace(writer);
        TextArea text = new TextArea();
        text.setText(errors.toString());        
        VBox.setVgrow(text, Priority.ALWAYS);
        VBox box = new VBox();
        box.getChildren().add(text);
        return box;
    }

    public ReadOnlyObjectProperty<Path> selectedDirectoryProperty() {
        return controller.selectedDirectoryProperty();
    }

    public void onSelect(Runnable action) {
        controller.setOnSelect(action);
    }

    public void onCancel(Runnable action) {
        controller.setOnCancel(action);
    }
    
    public void shutdown() {
        controller.shutdown();
    }
    
    public void setEnabled(boolean toggle) {
        setManaged(toggle);
        setVisible(toggle);
    }
    
    public static class DirChooserPathSupplier implements PathSupplier {

        private final DirectoryChooser dirChooser;
        private final FileChooser fileChooser;
        public DirChooserPathSupplier(FileChooser fileChooser) {
            this.fileChooser = Objects.requireNonNull(fileChooser);
            this.dirChooser = new DirectoryChooser();
            this.fileChooser.getChildren().add(dirChooser);
            this.dirChooser.setEnabled(false);
        }
        
        @Override
        public void getUpdate(Consumer<Path> update) {
            Platform.runLater(()->{
                dirChooser.setEnabled(true);
                fileChooser.setEnabled(false);
            });
            
            dirChooser.onSelect(() -> {
                Path selectedDir = dirChooser.selectedDirectoryProperty().get();
                if (null != selectedDir) {
                    update.accept(selectedDir);
                }
                dirChooser.setEnabled(false);
                fileChooser.setEnabled(true);
            });

            dirChooser.onCancel(() -> {
                dirChooser.setEnabled(false);
                fileChooser.setEnabled(true);
            });
        }
    }
}
