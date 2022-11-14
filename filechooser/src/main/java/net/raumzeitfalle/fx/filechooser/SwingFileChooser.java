/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import net.raumzeitfalle.fx.dirchooser.DirectoryChooser;
import net.raumzeitfalle.fx.filechooser.locations.Location;

/*
 * TODO: Review usage schema  and assign proper mouse and keyboard actions
 * TODO: Rework demos and show new way to select directories
 * TODO: Add a transition for the scene change between file chooser and dir chooser.
 */
public class SwingFileChooser extends JFXPanel implements HideableView {

    public static void setUseJavaFxDirectoryChooser(boolean toggle) {
        SwingFileChooserProperties.setUseJavaFXDirectoryChooser(toggle);
    }

    private static final long serialVersionUID = -5879082370711306802L;

    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;

    /**
     * Creates a new {@link FileChooser} within a Swing {@link JDialog} window. File browsing will start
     * in current working directory.
     * 
     * @param skin   {@link Skin} Defines the visual appearance of FileChooser. Defaults to dark skin.
     * @param filter {@link PathFilter} argument of variable length, accepts all path filters as needed.
     *               In case no filter is provided, a filter accepting all files will be used.
     * @return A configured {@link SwingFileChooser}.
     */
    public static SwingFileChooser create(Skin skin, PathFilter... filter) {
        return create(skin, "Choose file:", ".", filter);
    }

    /**
     * Creates a new {@link FileChooser} within a Swing {@link JDialog} window. File browsing will start
     * in current working directory.
     * 
     * @param skin   {@link Skin} Defines the visual appearance of FileChooser. Defaults to dark skin.
     * @param title  {@link String} title of the {@link FileChooser} view. In case of having the JavaFX
     *               based {@link DirectoryChooser} configured, the window titles for the directory and
     *               file choosers will be identical.
     * @param filter {@link PathFilter} argument of variable length, accepts all path filters as needed.
     *               In case no filter is provided, a filter accepting all files will be used.
     * @return A configured {@link SwingFileChooser}.
     */
    public static SwingFileChooser create(Skin skin, String title, PathFilter... filter) {
        return create(skin, title, ".", filter);
    }

    /**
     * Creates a new {@link FileChooser} within a Swing {@link JDialog} window.
     * 
     * @param skin         {@link Skin} Defines the visual appearance of FileChooser. Defaults to dark
     *                     skin.
     * @param title        {@link String} title of the {@link FileChooser} view. In case of having the
     *                     JavaFX based {@link DirectoryChooser} configured, the window titles for the
     *                     directory and file choosers will be identical.
     * @param pathToBrowse {@link Path} Location to start file browsing in. If the argument is null or
     *                     the denoted location does not exist, the control will default to current
     *                     working directory.
     * @param filter       {@link PathFilter} argument of variable length, accepts all path filters as
     *                     needed. In case no filter is provided, a filter accepting all files will be
     *                     used.
     * @return A configured {@link SwingFileChooser}.
     */
    public static SwingFileChooser create(Skin skin, String title, String pathToBrowse, PathFilter... filter) {
        return create(skin, title, title, pathToBrowse, filter);
    }

    /**
     * Creates a new {@link FileChooser} within a Swing {@link JDialog} window.
     * 
     * @param skin            {@link Skin} Defines the visual appearance of FileChooser. Defaults to
     *                        dark skin.
     * @param title           {@link String} title of the {@link FileChooser} view
     * @param dirChooserTitle {@link String} title of the {@link DirectoryChooser} view when enabled. By
     *                        default, the same title is used as for the {@link FileChooser} view.
     * @param pathToBrowse    {@link Path} Location to start file browsing in. If the argument is null
     *                        or the denoted location does not exist, the control will default to
     *                        current working directory.
     * @param filter          {@link PathFilter} argument of variable length, accepts all path filters
     *                        as needed. In case no filter is provided, a filter accepting all files
     *                        will be used.
     * @return A configured {@link SwingFileChooser}.
     */
    public static SwingFileChooser create(Skin skin, String title, String dirChooserTitle, String pathToBrowse, PathFilter... filter) {

        String fcTitle = (title == null) ? "Choose file:" : title;
        String dcTitle = (dirChooserTitle == null) ? fcTitle : dirChooserTitle;
        Skin skinToUse = (skin == null) ? Skin.DARK : skin;
        String location = (pathToBrowse == null) ? "." : pathToBrowse;
        Path startHere = startPath(location);

        PathFilter[] filters = filter;
        if (filters.length == 0) {
            filters = new PathFilter[] {PathFilter.acceptAllFiles()};
        }

        SwingFileChooser fc = new SwingFileChooser(fcTitle, dcTitle);
        fc.model = FileChooserModel.startingIn(startHere);
        for (PathFilter f : filters) {
            fc.model.addOrRemoveFilter(f);
        }
        // do all JavaFX work
        Platform.runLater(() -> {
            PathUpdateHandler pathSupplier = configureDirectoryChooser(skin, startHere, fc);
            FileChooser view = new FileChooser(pathSupplier, fc, fc.model, skinToUse, FileChooserViewOption.STAGE);
                               
            Scene fileChooserScene = new Scene(view);
            fc.setScene(fileChooserScene);
        });

        return fc;
    }

    private static PathUpdateHandler configureDirectoryChooser(Skin skin, Path startHere, SwingFileChooser fc) {
        PathUpdateHandler onPathUpdate = null;
        if (SwingFileChooserProperties.usesJavaFXDirectoryChooser()) {
            onPathUpdate = FXDirectoryChooser.createIn(startHere, () -> fc.getScene().getWindow());
        } else {
            DirectoryChooser dirChooser = new DirectoryChooser(skin);
            dirChooser.useChooseFileButtonProperty().setValue(true);
            dirChooser.useCancelButtonProperty().setValue(true);
            Scene dirChooserScene = new Scene(dirChooser);
            onPathUpdate = update->{
                    Scene previousScene = fc.getScene();
                    String previousTitle = fc.title;
                    fc.setTitle(fc.dirChooserTitle);
                    fc.setScene(dirChooserScene);

                    dirChooser.onSelect(() -> {
                        Path selectedDir = dirChooser.selectedDirectoryProperty().get();
                        if (null != selectedDir) {
                            fc.setTitle(selectedDir.toString());
                            update.accept(selectedDir);
                        }
                        fc.setTitle(previousTitle);
                        fc.setScene(previousScene);
                    });

                    dirChooser.onCancel(() -> {
                        fc.setTitle(previousTitle);
                        fc.setScene(previousScene);
                    });
                };
        }
        return onPathUpdate;
    }

    private static Path startPath(String pathToBrowse) {
        Path startHere = Paths.get(pathToBrowse);
        if (pathToBrowse.equals("")) {
            startHere = Paths.get("./");
        }
        if (Files.notExists(startHere)) {
            return Paths.get("./");
        }
        return startHere;
    }

    private transient FileChooserModel model;

    private JDialog dialog;

    private String title;

    private String dirChooserTitle;

    private SwingFileChooser(String title, String dirChooserTitle) {
        this.title = (null != title) ? title : "Choose file:";
        this.dirChooserTitle = (null != dirChooserTitle) ? dirChooserTitle : "Choose directory:";
    }

    public int showOpenDialog(Component parent) {
        if (null == this.dialog) {
            Frame frame = JOptionPane.getFrameForComponent(parent);
            dialog = new JDialog(frame, this.title, true);
            dialog.setContentPane(this);
            Dimension size = new Dimension(750, 550);
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            dialog.pack();
            dialog.setResizable(true);
        }

        this.dialog.setVisible(true);
        if (this.model.invalidSelectionProperty().getValue()) {
            return CANCEL_OPTION;
        } else {
            return APPROVE_OPTION;
        }
    }

    public File getSelectedFile() {
        if (null != this.model.getSelectedFile()) {
            return this.model.getSelectedFile().toFile();
        } else {
            return null;
        }
    }

    @Override
    public void closeView() {
        this.dialog.setVisible(false);
    }

    public void addLocations(List<Location> locations) {
        locations.forEach(model::addLocation);
    }

    protected void setTitle(String newTitle) {
        this.title = newTitle;
        this.dialog.setTitle(title);
    }
}
