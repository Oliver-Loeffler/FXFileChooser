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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.locations.Location;

/**
 * A {@link FileChooser} placed into its own {@link Stage}.
 * 
 * @see FileChooser
 */
public class FXFileChooserStage extends Stage implements HideableView {

    /**
     * Creates a new FileChooser placed into its own stage. The new stage will be application modal by
     * default. To change modality call the {@code initModality(...)} method.
     * <p>
     * This instance will start browsing in current working directory accepting the provided
     * {@link PathFilter}.
     * 
     * @param skin    {@link Skin} Defines the visual appearance of FileChooser. In case no Skin is
     *                provided, MODENA will be used.
     * @param filters {@link PathFilter} argument of variable length, accepts all path filters as
     *                needed. In case no filter is provided, a filter accepting all files will be used.
     * 
     * @return {@link FXFileChooserStage} A file chooser in its own window.
     */
    public static FXFileChooserStage create(Skin skin, PathFilter... filters) {
        PathFilter[] filter2use = filters;
        if (filters.length == 0) {
            filter2use = new PathFilter[] {PathFilter.acceptAllFiles()};
        }
        Skin skinToUse = skin == null ? Skin.MODENA : skin;
        return create(skinToUse, Paths.get("."), filter2use);
    }

    /**
     * Creates a new FileChooser placed into its own stage. The new stage will be application modal by
     * default. To change modality call the {@code initModality(...)} method.
     * <p>
     * This instance will start browsing in current working directory accepting the provided
     * {@link PathFilter}.
     * 
     * @param skin             {@link Skin} Defines the visual appearance of FileChooser. In case no
     *                         Skin is provided, MODENA will be used.
     * @param dirChooserOption {@link DirectoryChooserOption} Defines if an integrated (same stage)
     *                         directory chooser is used or the standard JavaFX platform specific
     *                         directory chooser.
     * @param filters          {@link PathFilter} argument of variable length, accepts all path filters
     *                         as needed. In case no filter is provided, a filter accepting all files
     *                         will be used.
     * 
     * @return {@link FXFileChooserStage} A file chooser in its own window.
     */
    public static FXFileChooserStage create(Skin skin, DirectoryChooserOption dirChooserOption, PathFilter... filters) {
        PathFilter[] filter2use = filters;
        if (filters.length == 0) {
            filter2use = new PathFilter[] {PathFilter.acceptAllFiles()};
        }
        Skin skinToUse = skin == null ? Skin.MODENA : skin;
        return create(skinToUse, Paths.get("."), dirChooserOption, filter2use);
    }

    /**
     * Creates a new FileChooser placed into its own stage. The new stage will be application modal by
     * default. To change modality call the {@code initModality(...)} method.
     * <p>
     * This instance will start browsing in the provided directory accepting files matching the provided
     * {@link PathFilter}.
     * 
     * @param skin     {@link Skin} Defines the visual appearance of FileChooser. In case no Skin is
     *                 provided, MODENA will be used.
     * @param startsIn {@link Path} Location where file browsing starts. If not provided, the file
     *                 chooser will start browsing in the current working directory.
     * @param filters  {@link PathFilter} argument of variable length, accepts all path filters as
     *                 needed. In case no filter is provided, a filter accepting all files will be used.
     * 
     * @return {@link FXFileChooserStage} A file chooser in its own window.
     */
    public static FXFileChooserStage create(Skin skin, Path startsIn, PathFilter... filters) {
        PathFilter[] filter2use = filters;
        if (filters.length == 0) {
            filter2use = new PathFilter[]{PathFilter.acceptAllFiles()};
        }
        Skin skinToUse = skin == null ? Skin.MODENA : skin;
        Path location = startsIn == null ? Paths.get(".") : startsIn;
        return new FXFileChooserStage(FileChooserModel.startingIn(location, filter2use), skinToUse);
    }

    /**
     * Creates a new FileChooser placed into its own stage. The new stage will be application modal by
     * default. To change modality call the {@code initModality(...)} method.
     * <p>
     * This instance will start browsing in the provided directory accepting files matching the provided
     * {@link PathFilter}.
     * 
     * @param skin             {@link Skin} Defines the visual appearance of FileChooser. In case no
     *                         Skin is provided, MODENA will be used.
     * @param startsIn         {@link Path} Location where file browsing starts. If not provided, the
     *                         file chooser will start browsing in the current working directory.
     * @param dirChooserOption {@link DirectoryChooserOption} Defines if an integrated (same stage)
     *                         directory chooser is used or the standard JavaFX platform specific
     *                         directory chooser.
     * @param filters          {@link PathFilter} argument of variable length, accepts all path filters
     *                         as needed. In case no filter is provided, a filter accepting all files
     *                         will be used.
     * 
     * @return {@link FXFileChooserStage} A file chooser in its own window.
     */
    public static FXFileChooserStage create(Skin skin, Path startsIn, DirectoryChooserOption dirChooserOption, PathFilter... filters) {
        PathFilter[] filter2use = filters;
        if (filters.length == 0) {
            filter2use = new PathFilter[]{PathFilter.acceptAllFiles()};
        }
        Skin skinToUse = skin == null ? Skin.MODENA : skin;
        Path location = startsIn == null ? Paths.get(".") : startsIn;
        return new FXFileChooserStage(FileChooserModel.startingIn(location, filter2use), skinToUse, dirChooserOption);
    }

    private final FileChooserModel model;

    private FXFileChooserStage(FileChooserModel model, Skin skin) {
        this(model, skin, DirectoryChooserOption.JAVAFX_PLATFORM);
    }
    
    FXFileChooserStage(FileChooserModel model, Skin skin, DirectoryChooserOption dirChooserOption) {
        this.model = Objects.requireNonNull(model);
        FileChooser view = new FileChooser(this.model, skin, dirChooserOption, FileChooserViewOption.STAGE);
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
    
    FileChooserModel getModel() {
        return this.model;
    }
}
