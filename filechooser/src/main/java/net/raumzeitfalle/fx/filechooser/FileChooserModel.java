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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import net.raumzeitfalle.fx.filechooser.locations.Location;

final class FileChooserModel {

    private final ObservableList<IndexedPath> allPaths;

    private final FilteredList<IndexedPath> filteredPaths;

    private final UpdateService fileUpdateService;

    private final ListProperty<IndexedPath> allPathsProperty;

    private final ListProperty<IndexedPath> filteredPathsProperty;

    private final ObjectProperty<Path> fileSelection = new SimpleObjectProperty<>();

    private final StringProperty selectedFileName = new SimpleStringProperty("");

    private final BooleanProperty invalidSelection = new SimpleBooleanProperty(true);

    private final ObservableList<PathFilter> observablePathFilter = FXCollections
            .observableArrayList(new ArrayList<>(30));

    private final ObservableSet<Location> locations = FXCollections.observableSet(new LinkedHashSet<>());

    private PathFilter effectiveFilter = PathFilter.acceptAllFiles("all files");

    private final ObjectProperty<Path> pastedPath = new SimpleObjectProperty<>();

    public static FileChooserModel startingInUsersHome(PathFilter... filter) {
        return startingIn(getUsersHome(), filter);
    }

    public static FileChooserModel startingIn(Path startFolder, PathFilter... filter) {
        ObservableList<IndexedPath> paths = FXCollections.observableArrayList(new ArrayList<>(300_000));
        Supplier<UpdateService> serviceProvider = () -> new FileUpdateService(startFolder, paths);
        FileChooserModel model = new FileChooserModel(paths, serviceProvider);
        model.observablePathFilter.addAll(filter);
        return model;
    }

    public FileChooserModel(ObservableList<IndexedPath> paths, Supplier<UpdateService> serviceProvider) {
        this.allPaths = paths;
        this.filteredPaths = new FilteredList<>(allPaths);
        this.allPathsProperty = new SimpleListProperty<>(this.allPaths);
        this.filteredPathsProperty = new SimpleListProperty<>(this.filteredPaths);

        // TODO: Make FileUpdateService part of the controller and rewire model and
        // service inside the controller
        this.fileUpdateService = serviceProvider.get();
        this.fileUpdateService.startUpdate();
        this.selectedFileName.bind(createStringBindingTo(fileSelection));
        this.initializeFilter("");
    }

    private StringBinding createStringBindingTo(ObservableValue<?> observable) {
        Callable<String> callable = () -> (null != observable.getValue()) ? String.valueOf(observable.getValue()) : "";

        return Bindings.createStringBinding(callable, observable);
    }

    private static Path getUsersHome() {
        return Paths.get(System.getProperty("user.home"));
    }

    public UpdateService getUpdateService() {
        return this.fileUpdateService;
    }

    public ObjectProperty<Path> currentSearchPath() {
        return this.fileUpdateService.searchPathProperty();
    }

    public ObservableList<IndexedPath> getFilteredPaths() {
        return filteredPaths;
    }

    ReadOnlyIntegerProperty filteredPathsSizeProperty() {
        return this.filteredPathsProperty.sizeProperty();
    }

    ReadOnlyIntegerProperty allPathsSizeProperty() {
        return this.allPathsProperty.sizeProperty();
    }

    ReadOnlyBooleanProperty invalidSelectionProperty() {
        return this.invalidSelection;
    }

    public void setSelectedFile(IndexedPath file) {
        if (null == file) {
            this.fileSelection.setValue(null);
        } else {
            Path rootDir = fileUpdateService.searchPathProperty().get();
            this.fileSelection.setValue(file.asPath(rootDir).toAbsolutePath().normalize());
        }
        this.invalidSelection.setValue(null == file);
    }

    public Path getSelectedFile() {
        return this.fileSelection.getValue();
    }

    public ReadOnlyObjectProperty<Path> selectedFileProperty() {
        return this.fileSelection;
    }

    public ReadOnlyStringProperty selectedFileNameProperty() {
        return this.selectedFileName;
    }

    /**
     * Updates only the live filter criterion, not the effective path filter.
     * 
     * @param criterion {@link String} A search text such as &quot;index&quot; for
     *                  &quot;index.html&quot; or &quot;index.txt&quot;.
     */
    public void updateFilterCriterion(String criterion) {
        Predicate<IndexedPath> combined = createManualListFilter(criterion)
                                            .and(indexedPath -> this.effectiveFilter
                                                                    .getPredicate()
                                                                    .test(indexedPath.toString()));
        this.filteredPaths.setPredicate(combined);
    }

    /**
     * Updates the effective {@link PathFilter} and the the live filter criterion.
     * 
     * @param pathFilter {@link PathFilter} Usually a specific file filter such as
     *                   *.txt or *.html.
     * @param criterion  {@link String} A search text such as &quot;index&quot; for
     *                   &quot;index.html&quot; or &quot;index.txt&quot;.
     */
    public void updateFilterCriterion(PathFilter pathFilter, String criterion) {
        this.effectiveFilter = pathFilter;
        updateFilterCriterion(criterion);
    }

    private Predicate<IndexedPath> createManualListFilter(String criterion) {
        String trimmed = criterion.trim();
        String withAsterisks = removeInvalidCharsExceptAsterisk(trimmed);
        String corrected = withAsterisks.replace("*", "");
        int firstAsterisk = withAsterisks.indexOf('*');
        int lastAsterisk = withAsterisks.lastIndexOf('*');
        if (withAsterisks.endsWith("*") && !withAsterisks.startsWith("*")) {
            // starts with
            if (corrected.length() > 0) {
                return p -> null == corrected || corrected.isEmpty()
                        || p.toString().toLowerCase().startsWith(corrected.toLowerCase());
            }
        } else if (withAsterisks.startsWith("*") && !withAsterisks.endsWith("*")) {
            // ends with
            if (corrected.length() > 0) {
                return p -> null == corrected || corrected.isEmpty()
                        || p.toString().toLowerCase().endsWith(corrected.toLowerCase());
            }
        } else if (firstAsterisk > 0 
                   && lastAsterisk > 0 
                   && firstAsterisk == lastAsterisk
                   && withAsterisks.length() >= 3) {
            // starts with & end with
            String left = withAsterisks.substring(0, firstAsterisk);
            String right = withAsterisks.substring(lastAsterisk + 1);
            return p -> null == corrected || corrected.isEmpty()
                    || (p.toString().toLowerCase().startsWith(left.toLowerCase())
                        &&  p.toString().toLowerCase().endsWith(right.toLowerCase()));
        }

        // contains
        return p -> null == corrected || corrected.isEmpty()
                || p.toString().toLowerCase().contains(corrected.toLowerCase());
    }

    public void initializeFilter(String text) {
        if (!this.observablePathFilter.isEmpty()) {
            PathFilter combined = this.observablePathFilter.get(0);
            for (PathFilter filter : this.observablePathFilter) {
                combined = combined.combine(filter);
            }
            this.effectiveFilter = combined;
        }
        updateFilterCriterion(text);
    }

    private String removeInvalidCharsExceptAsterisk(String criterion) {
        char[] invalidChars = new char[] {'"', '?', '<', '>', '|', ':'};
        String corrected = criterion;
        for (char invalid : invalidChars) {
            corrected = corrected.replace(String.valueOf(invalid), "");
        }
        return removeDoubles(corrected, "*");
    }

    private String removeDoubles(String text, String single) {
        String dbl = single + single;
        String corrected = text;
        while (corrected.contains(dbl)) {
            corrected = corrected.replace(dbl, single);
        }
        return corrected;
    }

    public void refreshFiles() {
        this.fileUpdateService.refresh();
    }

    public void updateFilesIn(File directory) {
        if (null != directory) {
            fileUpdateService.restartIn(directory.toPath());
        }
    }

    public void updateFilesIn(Location location) {
        if (null != location) {
            fileUpdateService.restartIn(location.getPath());
        }
    }

    public void changeToUsersHome() {
        fileUpdateService.restartIn(getUsersHome());
    }

    public ObservableList<PathFilter> getPathFilter() {
        return this.observablePathFilter;
    }

    public void addLocation(Location location) {
        this.locations.add(location);
    }

    public ObservableSet<Location> getLocations() {
        return this.locations;
    }

    public void sort(Comparator<IndexedPath> comparator) {
        this.allPaths.sort(comparator);
    }

    public void addOrRemoveFilter(PathFilter newFilter) {
        boolean wasRemoved = this.observablePathFilter
                                 .removeIf(pf -> pf.getName().equalsIgnoreCase(newFilter.getName()));
        if (!wasRemoved) {
            this.observablePathFilter.add(newFilter);
        }
    }

    public Path getPastedPath() {
        return pastedPath.get();
    }

    public ObjectProperty<Path> pastedPathProperty() {
        return pastedPath;
    }
}
