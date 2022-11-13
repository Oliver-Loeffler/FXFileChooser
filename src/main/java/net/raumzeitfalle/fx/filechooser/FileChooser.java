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
package net.raumzeitfalle.fx.filechooser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * A configurable file chooser for browsing directories with thousands of files. Some platform file
 * choosers and also file choosers provided by JavaFX and Java Swing will block when attempting to
 * browse directories with significant numbers of files within (&gt; 10k ... 500k files). In such
 * cases the GUI appears to freeze and often the operating system offers to kill the task.
 * <p>
 * To avoid this behavior, this {@link FileChooser} indexes a given directory in the background and
 * keeps the list of found files in memory. The background task continues to run until all files
 * have been indexed. The file chooser will not index the folder automatically, instead the user
 * needs to request a refresh.
 * <p>
 * To make finding the desired files easy, one can type a search string into the search box above
 * the file list. All files containing the provided text will be listed. One can also start sorting
 * (which will also run in a separate task) and apply filtering at same time. Sorting and filtering
 * operations are available even during directory indexing.
 */
public class FileChooser extends StackPane {

    private HideableView window = () -> this.getScene().getWindow();
    private FileChooserModel model = FileChooserModel.startingInUsersHome(PathFilter.acceptAllFiles("all files"));
    private FileChooserViewOption viewOption = FileChooserViewOption.STAGE;
    private Skin skin = Skin.MODENA;
    private Dialog<Path> dialog = null;
    private AnchorPane fileChooserView;
    
    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar. Creates a new file chooser with
     * following default configuration:
     * <ul>
     * <li>the view assumes that it is operated from a JavaFX stage</li>
     * <li>the model starts in user home directory accepting all files</li>
     * <li>dark skin is used</li>
     * <li>the default JavaFX directory chooser is used</li>
     * </ul>
     * 
     * @param skin {@link Skin} defines the visual appearance of the file chooser control
     * 
     */
    public FileChooser() {
        this(Skin.DARK, DirectoryChooserOption.JAVAFX_PLATFORM);
    }
    
    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar. Creates a new file chooser with
     * following default configuration:
     * <ul>
     * <li>the view assumes that it is operated from a JavaFX stage</li>
     * <li>the model starts in user home directory accepting all files</li>
     * <li>dark skin is used</li>
     * <li>the default JavaFX directory chooser is used</li>
     * </ul>
     * 
     * @param skin {@link Skin} defines the visual appearance of the file chooser control
     * @param directoryChooserOption {@link DirectoryChooserOption} defines which directory chooser will be used.
     * 
     */
    public FileChooser(@NamedArg("skin") Skin skin, 
                       @NamedArg("directoryChooserOption") DirectoryChooserOption directoryChooserOption) {

        if (skin != null) {
            this.skin = skin;
        }
        
        PathSupplier pathSupplier = null;
        if (directoryChooserOption != null) {
            pathSupplier = directoryChooserOption.apply(this);
        } else {
            pathSupplier = DirectoryChooserOption.JAVAFX_PLATFORM.apply(this);
        }
        
        FileChooserController controller = new FileChooserController(model, pathSupplier, window, viewOption, dialog);
        loadControl(controller);
        
        Skin.applyTo(this, this.skin);
    }

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * <p>
     * In case of error during FXML loading, the view is replaced by a {@link TextArea} showing the
     * cause and stack trace of the error.
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
     */
    public FileChooser(PathSupplier pathSupplier, final HideableView window, FileChooserModel model, Skin skin,
            FileChooserViewOption fileChooserViewOption) {
        this(pathSupplier, window, model, skin, fileChooserViewOption, null);
    }

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * <p>
     * In case of error during FXML loading, the view is replaced by a {@link TextArea} showing the
     * cause and stack trace of the error.
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
     */
    public FileChooser(PathSupplier pathSupplier, final HideableView window, FileChooserModel model, Skin skin,
            FileChooserViewOption fileChooserViewOption, Dialog<Path> dialog) {
        this.window = window;
        this.model = model;
        this.skin = skin;
        this.viewOption = fileChooserViewOption;
        FileChooserController controller = new FileChooserController(this.model,
                                                                     pathSupplier,
                                                                     this.window,
                                                                     this.viewOption,
                                                                     this.dialog);
        loadControl(controller);
        Skin.applyTo(this, this.skin);
    }

    private void loadControl(FileChooserController controller) {
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(controller);
        Parent view;
        try {
            view = loader.load();
        } catch (Exception e) {
            view = handleErrorOnLoad(fileName, resource, controller, e);
        }

        this.fileChooserView = new AnchorPane();
        this.fileChooserView.getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        this.getChildren().add(fileChooserView);
    }

    private VBox handleErrorOnLoad(String fileName, URL resource, Object controller, Exception e) {
        StringWriter errors = new StringWriter();
        PrintWriter writer = new PrintWriter(errors);
        writer.println("FXML: " + String.valueOf(fileName));
        writer.println("Controller: " + controller.getClass().getName());
        e.printStackTrace(writer);
        TextArea text = new TextArea();
        text.setText(errors.toString());
        VBox.setVgrow(text, Priority.ALWAYS);
        VBox box = new VBox();
        box.getChildren().add(text);
        return box;
    }
    
    public ObjectProperty<Path> currentSearchPath() {
        return this.model.currentSearchPath();
    }
    
    public Window getWindow() {
        return getScene().getWindow();
    }
    
    public void setEnabled(boolean toggle) {
        this.fileChooserView.setManaged(toggle);
        this.fileChooserView.setVisible(toggle);
    }

    public void shutdown() {
           
    }
}
