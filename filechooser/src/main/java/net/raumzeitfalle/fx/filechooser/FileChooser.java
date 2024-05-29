/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2024 Oliver Loeffler, Raumzeitfalle.net
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
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
@DefaultProperty("path")
public class FileChooser extends StackPane {

    private static final Logger LOGGER = Logger.getLogger(FileChooser.class.getName()); 

    private FileChooserModel model = FileChooserModel.startingInUsersHome(PathFilter.acceptAllFiles("all files"));
    
    private FileChooserViewOption viewOption = FileChooserViewOption.STAGE;
    
    private Skin skin = Skin.MODENA;
    
    private Dialog<Path> dialog = null;
    
    private AnchorPane fileChooserView;
    
    private final FileChooserController controller;
    
    private String pathName = null;
    
    private final StringProperty pathNameProperty = new SimpleStringProperty(this.pathName);
    
    public StringProperty pathNameProperty() {
        return this.pathNameProperty;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String newPathName) {
        this.pathName = newPathName;
        if (this.pathNameProperty != null) {
            this.pathNameProperty.set(newPathName);
        }
    }
    
    private String skinName = null;
    
    private final StringProperty skinNameProperty = new SimpleStringProperty(this.skinName);
    
    public StringProperty skinNameProperty() {
        return this.skinNameProperty;
    }

    public String getskinName() {
        return skinName;
    }

    public void setskinName(String newskinName) {
        this.skinName = newskinName;
        if (this.skinNameProperty != null) {
            this.skinNameProperty.set(newskinName);
        }
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
        PathUpdateHandler updateHandler = getPathUpdateHandler(directoryChooserOption);
        controller = new FileChooserController(model, updateHandler, viewOption, dialog);
        loadController(controller);
        Skin.applyTo(this, this.skin);
    }

    FileChooser(@NamedArg("model") FileChooserModel model,
                @NamedArg("skin") Skin skin, 
                @NamedArg("directoryChooserOption") DirectoryChooserOption directoryChooserOption,
                @NamedArg("viewOption") FileChooserViewOption viewOption,
                @NamedArg("hideableWindow") HideableView hideableWindow) {

        HideableView window = Objects.requireNonNull(hideableWindow);
        this.model = Objects.requireNonNull(model);
        
        if (skin != null) {
            this.skin = skin;
        }

        PathUpdateHandler updateHandler = getPathUpdateHandler(directoryChooserOption);
        controller = new FileChooserController(this.model, updateHandler, window, viewOption, dialog);
        loadController(controller);
        Skin.applyTo(this, this.skin);
    }

    private PathUpdateHandler getPathUpdateHandler(DirectoryChooserOption directoryChooserOption) {
        PathUpdateHandler updateHandler = null;
        if (directoryChooserOption != null) {
            updateHandler = directoryChooserOption.apply(this);
        } else {
            updateHandler = DirectoryChooserOption.JAVAFX_PLATFORM.apply(this);
        }
        return updateHandler;
    }
    

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * <p>
     * In case of error during FXML loading, the view is replaced by a {@link TextArea} showing the
     * cause and stack trace of the error.
     * 
     * @param handlePath            {@link PathUpdateHandler} In case the user wants to change the
     *                              directory, this supplier is called to provide the directory where
     *                              the user wants to lookup files. Here one can implement a customized
     *                              approach for directory selection.
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
    FileChooser(PathUpdateHandler handlePath, HideableView window, FileChooserModel model, 
                Skin skin, FileChooserViewOption fileChooserViewOption) {
        this(handlePath, window, model, skin, fileChooserViewOption, null);
    }

    /**
     * Creates a file chooser view. This view only shows contents of a single directory where it allows
     * live filtering searching by typing text into a file search bar.
     * <p>
     * In case of error during FXML loading, the view is replaced by a {@link TextArea} showing the
     * cause and stack trace of the error.
     * 
     * @param handlePath            {@link PathUpdateHandler} In case the user wants to change the
     *                              directory, this supplier is called to provide the directory where
     *                              the user wants to lookup files. Here one can implement a customized
     *                              approach for directory selection.
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
    FileChooser(PathUpdateHandler handlePath, HideableView window, FileChooserModel model, 
                Skin skin, FileChooserViewOption fileChooserViewOption, Dialog<Path> dialog) {

        this.model = model;
        this.skin = skin;
        this.viewOption = fileChooserViewOption;
        controller = new FileChooserController(this.model,
                                                    handlePath,
                                                    window,
                                                    this.viewOption,
                                                    this.dialog);
        loadController(controller);
        Skin.applyTo(this, this.skin);
    }
    
    private void updatePath() {
        if (this.pathName != null) {
            Path updated = this.getPathFromString();
            if (updated == null) {
                return;
            }
            
            if (Files.notExists(updated)) {
                LOGGER.log(Level.WARNING, "Not existing path defined in FXML property \"pathName\": {0}.", this.pathName);
                return;
            }

            Path existing = model.getCurrentSearchPath();
            if (!updated.equals(existing)) {
                model.getUpdateService().restartIn(updated);
            }
        }
    }

    private Path getPathFromString() {
        if (null == this.pathName) {
            return null;
        }
        
        try {
            return Path.of(this.pathName).toAbsolutePath().normalize();
        } catch (InvalidPathException ipe) {
            LOGGER.log(Level.WARNING, "The value for FXML property \"pathName\" is not a valid file system path ({0}).", this.pathName);
            return null;
        }
    }
    
    private void loadController(FileChooserController controller) {
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(controller);
        Parent view;
        try {
            view = loader.load();
        } catch (Exception e) {
            view = handleErrorOnLoad(fileName, controller, e);
        }
        
        this.pathNameProperty.addListener(l -> updatePath());
        this.skinNameProperty.addListener(l -> updateSkin());
        this.fileChooserView = new AnchorPane();
        this.fileChooserView.getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        this.getChildren().add(fileChooserView);
    }

    private void updateSkin() {
        if (this.skinName != null) {
            Set<String> x = Arrays.stream(Skin.values()).map(Skin::name).collect(Collectors.toSet());
            if (!x.contains(this.skinName)) {
                String values = Arrays.stream(Skin.values()).map(Skin::name).collect(Collectors.joining(", "));
                LOGGER.log(Level.WARNING, "Invalid Skin [{0}] detected. Valid values for skins are: {1}",
                        new Object[] { this.skinName, values });
            } else {
                Skin newSkin = Skin.valueOf(this.skinName);
                if (newSkin != this.skin) {
                    Skin.removeFrom(this, this.skin);
                    this.skin = newSkin;
                    Skin.applyTo(this, newSkin);
                }
            }
        }
    }

    private VBox handleErrorOnLoad(String fileName, Object controller, Exception e) {
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
        this.controller.stopServices();
    }
}
