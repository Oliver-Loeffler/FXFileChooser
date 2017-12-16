package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FileUpdateService extends javafx.concurrent.Service<Void>{
     
    private ObjectProperty<Path> root = new SimpleObjectProperty<>();
    
    private ObservableList<Path> pathsToUpdate;
   
            
    public FileUpdateService(Path folderToStart, ObservableList<Path> paths) {
        setSearchLocation(folderToStart);        
        this.pathsToUpdate = paths;
    }

    private void setSearchLocation(Path folderToStart) {
        if (Files.isDirectory(folderToStart)) {
            this.root.setValue(folderToStart);
        } else {
            this.root.setValue(folderToStart.getParent());
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new FindFilesTask(root.getValue(), pathsToUpdate);
    }
    
    public void restartIn(Path location) {
        setSearchLocation(location);
        this.restart();
    }
    
    ObjectProperty<Path> searchPathProperty() {
        return this.root;
    }
    
    
}
