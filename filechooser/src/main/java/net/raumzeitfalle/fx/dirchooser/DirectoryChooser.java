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

import javafx.beans.NamedArg;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.raumzeitfalle.fx.filechooser.DirectoryChooserOption;
import net.raumzeitfalle.fx.filechooser.FileChooser;
import net.raumzeitfalle.fx.filechooser.PathUpdateHandler;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DirectoryChooser extends AnchorPane {

    private final DirectoryChooserController controller;

    private final BooleanProperty useChooseFileButtonProperty;
    
    private final BooleanProperty useCancelButtonProperty;

    /**
     * Creates a new JavaFX based directory chooser which. This is not the JavaFX platform specific
     * directory chooser. This directory chooser can be used as a control within a scene or even within
     * a FXML document. Its skin defaults to dark mode.
     */
    public DirectoryChooser() {
        this(Skin.DARK);
    }

    /**
     * Creates a new JavaFX based directory chooser which. This is not the JavaFX platform specific
     * directory chooser. This directory chooser can be used as a control within a scene or even within
     * a FXML document.
     * 
     * @param skin {@link Skin} defines the visual appearance of the directory chooser control
     */
    public DirectoryChooser(@NamedArg("skin") Skin skin) {
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
            view = handleErrorOnLoad(fileName, e);
        }
        this.getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        Skin.applyTo(this, skin);

        this.controller.hideChooseFilesButton();
        this.useChooseFileButtonProperty = new SimpleBooleanProperty(false);
        this.useChooseFileButtonProperty.addListener((obs,prev,next)->{
            if (Boolean.compare(next, prev) == 0) {
                return;
            }
            this.controller.setUseChooseFilesButton(next);
        });
        
        this.controller.hideCancelButton();
        this.useCancelButtonProperty = new SimpleBooleanProperty(false);
        this.useCancelButtonProperty.addListener((obs,prev,next)->{
            if (Boolean.compare(next, prev) == 0) {
                return;
            }
            this.controller.setUseCancelButton(next);
        });
    }

    private VBox handleErrorOnLoad(String fileName, Exception e) {
        StringWriter errors = new StringWriter();
        PrintWriter writer = new PrintWriter(errors);
        writer.println("FXML: " + fileName);
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

    public BooleanProperty useChooseFileButtonProperty() {
        return this.useChooseFileButtonProperty;
    }

    public BooleanProperty useCancelButtonProperty() {
        return this.useCancelButtonProperty;
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

    /**
     * This enabled combined use of {@link FileChooser} and {@link DirectoryChooser} as one control. The
     * {@link FileChooser} only allows file selection and provides no functions for directory selection.
     * Hence either the JavaFX platform specific directory chooser must be configured or the
     * {@link DirectoryChooser} can be used.
     * 
     * See {@link DirectoryChooserOption} for details.
     */
    public static class DirChooserPathUpdateHandler implements PathUpdateHandler {

        private final DirectoryChooser dirChooser;
        private final FileChooser fileChooser;

        /**
         * Connects a new {@link DirectoryChooser} with a given {@link FileChooser}.
         * 
         * @param fileChooser {@link FileChooser} to make use of the {@link DirectoryChooser}.
         */
        public DirChooserPathUpdateHandler(FileChooser fileChooser) {
            this.fileChooser = Objects.requireNonNull(fileChooser);
            this.dirChooser = new DirectoryChooser();
            this.fileChooser.getChildren().add(dirChooser);
            this.dirChooser.setEnabled(false);
            this.dirChooser.useChooseFileButtonProperty.setValue(true);
            this.dirChooser.useCancelButtonProperty.setValue(true);
        }

        /**
         * Disables the file chooser view and allows a directory selection by user. After user decides to
         * accept or reject the selection, the directory chooser view is hidden and the file chooser view is
         * shown. This is internally managed by updating the visible and managed properties
         * ({@code setVisible(...)} and {@code setManaged(...)}).
         * 
         * @param update {@link Consumer} of {@link Path} defines how to process the path which was selected
         *               by the user.
         */
        @Override
        public void getUpdate(Consumer<Path> update) {
            Platform.runLater(() -> {
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
