package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    private UpdateService fileUpdateService;
    
    private final ListProperty<Path> allPathsProperty;
        
    private final ListProperty<Path> filteredPathsProperty;
    
    private final StringProperty fileSelection = new SimpleStringProperty();
    
    private final BooleanProperty invalidSelection = new SimpleBooleanProperty(true);
    
    private final List<PathFilter> pathFilter = new ArrayList<>(10);
    
    private PathFilter effectiveFilter = PathFilter.acceptAllFiles("all files");
    
    /*
     * TODO: add possibility to construct model with list or array of filters
     */
    public static FileChooserModel get() {
        return startingIn(getUsersHome());
    }
    
    public static FileChooserModel startingIn(Path startFolder) {
    		ObservableList<Path> paths = FXCollections.observableArrayList(new ArrayList<>(300_000));
    		Supplier<UpdateService> serviceProvider = ()->new FileUpdateService(startFolder, paths);
    		return new FileChooserModel(paths, serviceProvider);
    }
    
    public FileChooserModel(ObservableList<Path> paths, Supplier<UpdateService> serviceProvider) {
        this.allPaths = paths;
        this.filteredPaths = new FilteredList<>(allPaths);
        this.allPathsProperty = new SimpleListProperty<>(this.allPaths);
        this.filteredPathsProperty = new SimpleListProperty<>(filteredPaths);
        this.fileUpdateService = serviceProvider.get();
        this.fileUpdateService.startUpdate();
             
        this.initializeFilter("");
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

    public ObservableList<Path> getFilteredPaths() {
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
    
    public void setSelectedFile(Path file) {
        if (null == file) {
            this.fileSelection.setValue("");
        } else {
            this.fileSelection.setValue(file.toAbsolutePath().normalize().toString());
        }       
        this.invalidSelection.setValue(null == file);
    }
    
    public Path getSelectedFile() {
        String selection = this.fileSelection.getValue();
        if (null != selection) {
            File selected = new File(selection);
            return selected.toPath();
        } else {
            return null;
        }
    }
    
    public ReadOnlyStringProperty selectedFileProperty() {
        return this.fileSelection;
    }
    
    /**
     * Updates only the live filter criterion, not the effective path filter.
     * @param criterion {@link String} A search text such as &quot;index&quot; for &quot;index.html&quot; or &quot;index.txt&quot;.
     */
    public void updateFilterCriterion(String criterion) {
        Predicate<Path> customFilter = createManualListFilter(criterion);                           
        this.filteredPaths.setPredicate(combineFilterPredicates(customFilter));
    }

    /**
     * Updates the effective {@link PathFilter} and the the live filter criterion.
     * @param pathFilter {@link PathFilter} Usually a specific file filter such as *.txt or *.html.
     * @param criterion {@link String} A search text such as &quot;index&quot; for &quot;index.html&quot; or &quot;index.txt&quot;.
     */
    public void updateFilterCriterion(PathFilter pathFilter, String criterion) {
    		this.effectiveFilter = pathFilter;
    		updateFilterCriterion(criterion);
    }
    
    private Predicate<Path> createManualListFilter(String criterion) {
        String corrected = removeInvalidChars(criterion);
        return p -> null == corrected 
        				|| corrected.isEmpty() 
        				|| p.toString().toLowerCase().contains(corrected.toLowerCase());
    }

    private Predicate<Path> combineFilterPredicates(Predicate<Path> customFilter) {
    		List<Predicate<Path>> predicates = new ArrayList<>();
    		predicates.add(this.effectiveFilter.getPredicate());
    		predicates.add(customFilter);

        return predicates
        				.stream()
        				.reduce(x -> true, Predicate::and);
        
    }
    
    public void initializeFilter(String text) {
    		if (!this.pathFilter.isEmpty()) {
    			PathFilter combined = this.pathFilter.get(0);
    			for (PathFilter filter : this.pathFilter) {
    				combined = combined.combine(filter);
    			}
    			 this.effectiveFilter = combined;
    		}
    		updateFilterCriterion(text);
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
        this.fileUpdateService.refresh();
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
    
    public List<PathFilter> getPathFilter() {
    		return this.pathFilter;
    }
	
	public void sort(Comparator<Path> comparator) {
	    this.allPaths.sort(comparator);
	}
}
