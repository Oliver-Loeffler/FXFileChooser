package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javafx.collections.transformation.SortedList;

final class FileChooserModel {
    
    private final ObservableList<Path> allPaths;
    
    private final FilteredList<Path> filteredPaths;
    
    private final SortedList<Path> sortedPaths;
    
    private final FileUpdateService fileUpdateService;
    
    private final ListProperty<Path> allPathsProperty;
        
    private final ListProperty<Path> filteredPathsProperty;
    
    private final StringProperty fileSelection = new SimpleStringProperty();
    
    private final BooleanProperty invalidSelection = new SimpleBooleanProperty(true);
    
    private final Set<PathFilter> pathFilter = new HashSet<>(10);
    
    private Path selectedFile;
    
    /*
     * TODO: add possibility to construct model with list or array of filters
     */
    public FileChooserModel() {
        this(getUsersHome());
    }
    
    public FileChooserModel(Path startFolder) {
        if (null == startFolder) {
            startFolder = getUsersHome();
        }
        this.allPaths = FXCollections.observableArrayList();
        this.filteredPaths = new FilteredList<>(allPaths);
        this.sortedPaths = new SortedList<>(this.filteredPaths);
        this.fileUpdateService = new FileUpdateService(startFolder, this.allPaths);
        this.allPathsProperty = new SimpleListProperty<>(this.allPaths);
        this.filteredPathsProperty = new SimpleListProperty<>(filteredPaths);
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
    
    public ObservableList<Path> getSortedPaths() {
        return sortedPaths;
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
        Predicate<Path> customFilter = createManualListFilter(criterion);                           
        this.filteredPaths.setPredicate(combineFilterPredicates(customFilter));
    }

    private Predicate<Path> createManualListFilter(String criterion) {
        String corrected = removeInvalidChars(criterion);
        Predicate<Path> customFilter = p -> {
            return null == corrected || corrected.isEmpty() ||
                    p.toString().toLowerCase().contains(corrected.toLowerCase());};
        return customFilter;
    }

    private Predicate<Path> combineFilterPredicates(Predicate<Path> customFilter) {
        List<Predicate<Path>> effectiveFilter = this.pathFilter.parallelStream().map(PathFilter::getCriterion).collect(Collectors.toList());
        effectiveFilter.add(customFilter);
     
        Predicate<Path> effectivePredicate = effectiveFilter.parallelStream().reduce(x -> true, Predicate::and);
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
        		Path parent = directory.getParent();
        		if (parent != null) {
        			this.fileUpdateService.restartIn(parent);	
        		}
        }
    }
    
    public void changeToUsersHome() {
        updateFilesIn(getUsersHome());
    }

    public void addFilter(PathFilter filter) {
        this.pathFilter.add(filter);
    }
    
    public Set<PathFilter> getPathFilter() {
    		return this.pathFilter;
    }

    public void replacePathFilter(PathFilter filter) {
    		this.pathFilter.clear();
        this.pathFilter.add(filter);
    }
    
	public void clearBaseFilter() {
		this.pathFilter.clear();
	}
}
