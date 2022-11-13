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
package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;

/**
 * A configurable file chooser view.
 * 
 * @deprecated As this class is not compatible with JavaFX Scene Builder, this is going to be
 *             removed in later versions of FXFileChhooser. Please use {@link FileChooser} instead.
 */
@Deprecated
final class FileChooserView extends AnchorPane {

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * 
     * @deprecated As this class is not compatible with JavaFX Scene Builder, it is going to be removed
     *             in later versions of FXFileChhooser. Please use {@link FileChooser} instead.
     * 
     * @param pathSupplier          {@link PathSupplier} In case the user wants to change the directory,
     *                              this supplier is called to provide the directory where the user
     *                              wants to lookup files. Here one can implement a customized approach
     *                              for directory selection.
     * 
     * @param window                {@link HideableView} Reference to the parent window of the
     *                              {@link FileChooserView}. This can be a JavaFX stage, a Swing JFrame
     *                              or a Dialog. When the {@link FileChooserView} is operated as a
     *                              dialog window but placed inside a Stage (or Swing JFrame), then when
     *                              user decides to continue with okay or cancel, this window will be
     *                              closed.
     * 
     * @param model                 {@link FileChooserModel} The internal data model holding the
     *                              contents of a folder or directory being searched. This model takes
     *                              care on indexing of a directory. The main purpose here is to prevent
     *                              GUI and main application blocking which usually occurs with built-in
     *                              file choosers when directory contents exceeds a certain size.
     * 
     * @param skin                  {@link Skin} Defines the appearance of this view.
     * 
     * @param fileChooserViewOption {@link FileChooserViewOption} Use this switch to adjust
     *                              {@link FileChooserView} behavior to operation inside a JavaFX dialog
     *                              (which brings its own OKAY and CANCEL buttons) or to operation
     *                              inside a JavaFX stage / Swing JFrame where OKAY and CANCEL buttons
     *                              will be provided by the {@link FileChooserView}.
     * 
     * @throws IOException The control is defined as FXML file and in any case of error during FXML
     *                     reading an exception will be thrown.
     */
    @Deprecated
    public FileChooserView(PathSupplier pathSupplier, final HideableView window, FileChooserModel model, Skin skin,
            FileChooserViewOption fileChooserViewOption) throws IOException {
        this(pathSupplier, window, model, skin, fileChooserViewOption, null);
    }

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * 
     * @deprecated As this class is not compatible with JavaFX Scene Builder, it is going to be removed
     *             in later versions of FXFileChhooser. Please use {@link FileChooser} instead.
     * 
     * @param pathSupplier          {@link PathSupplier} In case the user wants to change the directory,
     *                              this supplier is called to provide the directory where the user
     *                              wants to lookup files. Here one can implement a customized approach
     *                              for directory selection.
     * 
     * @param window                {@link HideableView} Reference to the parent window of the
     *                              {@link FileChooserView}. This can be a JavaFX stage, a Swing JFrame
     *                              or a Dialog. When the {@link FileChooserView} is operated as a
     *                              dialog window but placed inside a Stage (or Swing JFrame), then when
     *                              user decides to continue with okay or cancel, this window will be
     *                              closed.
     * 
     * @param model                 {@link FileChooserModel} The internal data model holding the
     *                              contents of a folder or directory being searched. This model takes
     *                              care on indexing of a directory. The main purpose here is to prevent
     *                              GUI and main application blocking which usually occurs with built-in
     *                              file choosers when directory contents exceeds a certain size.
     * 
     * @param skin                  {@link Skin} Defines the appearance of this view.
     * 
     * @param fileChooserViewOption {@link FileChooserViewOption} Use this switch to adjust
     *                              {@link FileChooserView} behavior to operation inside a JavaFX dialog
     *                              (which brings its own OKAY and CANCEL buttons) or to operation
     *                              inside a JavaFX stage / Swing JFrame where OKAY and CANCEL buttons
     *                              will be provided by the {@link FileChooserView}.
     * 
     * @param dialog                {@link Dialog} When operated inside a JavaFX dialog, the controller
     *                              class will provide the user selection to the dialog. Hence the
     *                              dialog where the {@link FileChooserView} is used within must be
     *                              known up front.
     * 
     * @throws IOException The control is defined as FXML file and in any case of error during FXML
     *                     reading an exception will be thrown.
     */
    @Deprecated
    public FileChooserView(PathSupplier pathSupplier, final HideableView window, FileChooserModel model, Skin skin,
            FileChooserViewOption fileChooserViewOption, Dialog<Path> dialog) throws IOException {
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        FileChooserController controller = new FileChooserController(model, pathSupplier, window, fileChooserViewOption, dialog);
        loader.setController(controller);
        Parent view = loader.load();
        this.getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        Skin.applyTo(this, skin);
    }

}
