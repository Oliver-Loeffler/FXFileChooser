/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.locations.Location;

public class FXFileChooserDialog extends Dialog<Path> implements HideableView {

    public static FXFileChooserDialog create(Skin skin, PathFilter... filter) {
        FileChooserModel model = FileChooserModel.startingInUsersHome(filter);
        return new FXFileChooserDialog(skin, model);
    }

    public void addFilter(PathFilter filter) {
        model.addOrRemoveFilter(filter);
    }

    static FXFileChooserDialog create(Skin skin, FileChooserModel model) {
        return new FXFileChooserDialog(skin, model);
    }

    private final FileChooserModel model;

    private static final double minWidth = 700;

    private static final double minHeight = 550;

    // TODO: Enable File Chooser to use new (2nd scene) directory chooser as well
    // but keep old API alive for JavaFX
    // TODO: Make CSS file externally configurable
    private FXFileChooserDialog(Skin skin, FileChooserModel fileChooserModel) {
        this.model = fileChooserModel;
        Skin.applyToDialog(this, skin);

        setTitle("File Selection");
        setHeaderText("Select File from for processing:");

        StringBinding sb = Bindings.createStringBinding(() -> {
            Path current = model.currentSearchPath().get();
            if (current != null) {
                return current.normalize().toAbsolutePath().toString();
            }
            return "";
        }, model.currentSearchPath());

        headerTextProperty().bind(sb);
        initModality(Modality.APPLICATION_MODAL);

        Supplier<Window> ownerProvider = () -> getDialogPane().getScene().getWindow();
        PathUpdateHandler pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), ownerProvider);
        FileChooser view = new FileChooser(pathSupplier, null, model, skin, FileChooserViewOption.DIALOG, this);
        getDialogPane().setContent(view);
        ButtonType okay = ButtonType.OK;
        getDialogPane().getButtonTypes().addAll(okay, ButtonType.CANCEL);

        Node okayButton = getDialogPane().lookupButton(okay);
        okayButton.disableProperty().bind(model.invalidSelectionProperty());

        setResultConverter(dialogButton -> {
            if (dialogButton == okay) {
                this.hide();
                return model.getSelectedFile();
            }
            return null;
        });

        resizableProperty().set(true);
        setOnShowing(this::configureMinWindowSize);
    }

    private void configureMinWindowSize(Event evt) {
        Platform.runLater(this::setMinWinSize);
    }

    private void setMinWinSize() {
        Window window = getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
    }

    public Optional<Path> showOpenDialog(Window ownerWindow) {
        if (null == this.getOwner()) {
            this.initOwner(ownerWindow);
        }
        return this.showAndWait();
    }

    @Override
    public void closeView() {
        this.getDialogPane().getScene().getWindow().hide();
    }

    public void addLocations(List<Location> locations) {
        locations.forEach(model::addLocation);
    }
}
