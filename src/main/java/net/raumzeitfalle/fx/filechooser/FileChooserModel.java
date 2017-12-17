package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

final class FileChooserModel {
    
    private final ObservableList<Path> allPaths;
    
    private final FilteredList<Path> filteredPaths;
    
    private final FileUpdateService fileUpdateService;
    
    private final ListProperty<Path> allPathsProperty;
    
    private final List<Predicate<? super Path>> baseFilters = new ArrayList<>();
    
  //  private final ListProperty<Path> filteredPathsProperty;
    
    private final StringProperty fileSelection = new SimpleStringProperty();
    
    private final BooleanProperty invalidSelection = new SimpleBooleanProperty(true);
    
    private final Set<PathFilter> pathFilter = new HashSet<>(10);
    
    private Path selectedFile;
    
    public FileChooserModel() {
        this(getUsersHome(),1_000);
    }
    
    public FileChooserModel(Path startFolder, int initialCapacity) {
        if (null == startFolder) {
            startFolder = getUsersHome();
        }
        this.allPaths = FXCollections.observableArrayList(new ArrayList<Path>(initialCapacity));
        this.filteredPaths = new FilteredList<>(allPaths);
        this.fileUpdateService = new FileUpdateService(startFolder, this.allPaths);
        this.allPathsProperty = new SimpleListProperty<>(this.allPaths);
     //   this.filteredPathsProperty = new SimpleListProperty<>(filteredPaths);
        updateFilterCriterion("");
        this.fileUpdateService.start();
    }

    private static Path getUsersHome() {
        return Paths.get(System.getProperty("user.home"));
    }
    
    public FileUpdateService getFileUpdateService() {
        return this.fileUpdateService;
    }
    
    public ObjectProperty<Path> currentSearchPath() {
        return this.fileUpdateService.searchPathProperty();
    }

    public ObservableList<Path> getFilteredPaths() {
        return filteredPaths;
    }
    
//    ReadOnlyIntegerProperty filteredPathsSizeProperty() {
//        return this.filteredPathsProperty.sizeProperty();
//    }
    
    ReadOnlyIntegerProperty allPathsSizeProperty() {
        return this.allPathsProperty.sizeProperty();
    }
    
    ReadOnlyBooleanProperty invalidSelectionProperty() {
        return this.invalidSelection;
    }
    
    public void setSelectedFile(Path path) {
        this.selectedFile = path;
        if (null == path) {
            this.fileSelection.setValue("");    
        } else {
            this.fileSelection.setValue(path.toString());
        }       
        this.invalidSelection.setValue(path == null);
    }
    
    public Path getSelectedFile() {
        return this.selectedFile;
    }
    
    public ReadOnlyStringProperty selectedFileProperty() {
        return this.fileSelection;
    }
    
    public void updateFilterCriterion(String criterion) {
        Predicate<? super Path> customFilter = createManualListFilter(criterion);                           
        this.filteredPaths.setPredicate(combineFilterPredicates(customFilter));
    }

    private Predicate<? super Path> createManualListFilter(String criterion) {
        String corrected = removeInvalidChars(criterion);
        Predicate<? super Path> customFilter = p -> {
            return null == corrected || corrected.isEmpty() ||
                    p.toString().toLowerCase().contains(corrected.toLowerCase());};
        return customFilter;
    }

    private Predicate<? super Path> combineFilterPredicates(Predicate<? super Path> customFilter) {
        List<Predicate<? super Path>> effectiveFilter = this.baseFilters.parallelStream().collect(Collectors.toList());
        effectiveFilter.add(customFilter);
        Predicate<? super Path> effectivePredicate = effectiveFilter.parallelStream().reduce(x -> true, Predicate::and);
        return effectivePredicate;
    }

    private String removeInvalidChars(String criterion) {
        char[] invalidChars = new char[] {'"','?','<','>','|',':','*'};   
        String corrected = criterion;
        for (char invalid : invalidChars) {
            corrected = corrected.replace(String.valueOf(invalid), "");
        }
        return corrected;
    }
    
    public void refreshFiles() {
        this.fileUpdateService.restart();
    }
    
    public void updateFilesIn(File directory) {
        if (null != directory) {
            updateFilesIn(directory.toPath());    
        }
    }
    
    public void updateFilesIn(Path directory) {
        if (Files.isDirectory(directory)) {            
            this.fileUpdateService.restartIn(directory);

        } else if (Files.isRegularFile(directory)) {
            this.fileUpdateService.restartIn(directory.getParent());
        }
    }
    
    public void changeToUsersHome() {
        updateFilesIn(getUsersHome());
    }

    public void addFilter(PathFilter filter) {
        this.pathFilter.add(filter);
    }
}
