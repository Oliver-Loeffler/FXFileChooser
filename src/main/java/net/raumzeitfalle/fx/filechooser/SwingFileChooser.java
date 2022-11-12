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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

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
 *  
 */
public class SwingFileChooser extends JFXPanel implements HideableView {

    private static final long serialVersionUID = -5879082370711306802L;

    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;

    public static SwingFileChooser create(Skin skin, PathFilter... filter) {
        return create(skin, "Choose file:", "", filter);
    }

    public static SwingFileChooser create(Skin skin, String title, PathFilter... filter) {
        return create(skin, title, "", filter);
    }

    public static SwingFileChooser create(Skin skin, String title, String pathToBrowse, PathFilter... filter) {
        Path startHere = startPath(pathToBrowse);
        SwingFileChooser fc = new SwingFileChooser(title);

        fc.model = FileChooserModel.startingIn(startHere);
        for (PathFilter f : filter) {
            fc.model.addOrRemoveFilter(f);
        }

        // do all JavaFX work
        Platform.runLater(() -> {
             boolean useOldSchoolDirChooser = false;
            PathSupplier pathSupplier = null;
            if (useOldSchoolDirChooser) {
                pathSupplier = FXDirectoryChooser.createIn(startHere, () -> fc.getScene().getWindow());
            } else {
                DirectoryChooser dirChooser = new DirectoryChooser(skin);
                Scene dirChooserScene = new Scene(dirChooser);
                pathSupplier = new PathSupplier() {
                    @Override
                    public void getUpdate(Consumer<Path> update) {
                        fc.setTitle("Choose directory:");
                        Scene previousScene = fc.getScene();
                        String previousTitle = fc.title;
                        fc.setScene(dirChooserScene);

                        dirChooser.onSelect(() -> {
                            Path selectedDir = dirChooser.selectedDirectoryProperty().get();
                            if (null != selectedDir) {
                                fc.setTitle(selectedDir.toString());
                                update.accept(selectedDir);
                            } else {
                                fc.setTitle(previousTitle);
                            }
                            fc.setScene(previousScene);
                        });

                        dirChooser.onCancel(() -> fc.setScene(previousScene));

                    }
                };
            }

            FileChooser view = new FileChooser(pathSupplier, fc, fc.model, skin, FileChooserViewOption.STAGE);
            Scene fileChooserScene = new Scene(view);
            fc.setScene(fileChooserScene);
        });

        return fc;
    }

    private static Path startPath(String pathToBrowse) {
        Path startHere = Paths.get(pathToBrowse);
        if (pathToBrowse.equals("")) {
            startHere = Paths.get("./");
        }
        return startHere;
    }

    private transient FileChooserModel model;

    private JDialog dialog;

    private String title;

    private SwingFileChooser(String title) {
        this.title = (null != title) ? title : "Choose file:";
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
