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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.locations.Location;

public class FXFileChooserStage extends Stage implements HideableView {

    public static FXFileChooserStage create(Skin skin) throws IOException {
        return create(skin, Paths.get("."), new PathFilter[0]);
    }

    public static FXFileChooserStage create(Skin skin, PathFilter... filter) throws IOException {
        return new FXFileChooserStage(FileChooserModel.startingInUsersHome(filter), skin);
    }

    public static FXFileChooserStage create(Skin skin, Path inLocation, PathFilter... filter) throws IOException {
        return new FXFileChooserStage(FileChooserModel.startingIn(inLocation, filter), skin);
    }

    private final FileChooserModel model;

    private FXFileChooserStage(FileChooserModel model, Skin skin) throws IOException {
        this.model = model;
        FXDirectoryChooser dirChooser = FXDirectoryChooser.createIn(model.currentSearchPath(), () -> this);
        FileChooser view = new FileChooser(dirChooser, this, model, skin, FileChooserViewOption.STAGE);
        Scene scene = new Scene(view);
        this.setScene(scene);
        StringBinding sb = Bindings.createStringBinding(() -> {
            Path current = model.currentSearchPath().get();
            if (current != null) {
                String path = current.normalize().toAbsolutePath().toString();
                if (path.length() > 100) {
                    return path.substring(0, 20) + " ... " + path.substring(path.length() - 75, path.length());
                } else {
                    return path;
                }
            }
            return "";
        }, model.currentSearchPath());

        this.titleProperty().bind(sb);
        initModality(Modality.APPLICATION_MODAL);
    }

    public Optional<Path> showOpenDialog(Window ownerWindow) {
        if (null == this.getOwner()) {
            this.initOwner(ownerWindow);
        }

        this.showAndWait();
        return this.getSelectedPath();
    }

    private Optional<Path> getSelectedPath() {
        return Optional.ofNullable(this.model.getSelectedFile());
    }

    @Override
    public void closeView() {
        this.hide();
    }

    public void addLocations(List<Location> locations) {
        locations.forEach(model::addLocation);
    }
}
