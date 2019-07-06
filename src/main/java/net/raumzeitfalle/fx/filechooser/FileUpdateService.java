package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FileUpdateService extends javafx.concurrent.Service<Void> implements UpdateService {
     
    private ObjectProperty<Path> rootFolder = new SimpleObjectProperty<>();
    
    private ObservableList<IndexedPath> pathsToUpdate;
   
            
    public FileUpdateService(Path folderToStart, ObservableList<IndexedPath> paths) {
        setSearchLocation(folderToStart);        
        this.pathsToUpdate = paths;
        registerShutdownHook();
    }

    private void setSearchLocation(Path folderToStart) {
        if (folderToStart.toFile().isDirectory()) {
            this.rootFolder.setValue(folderToStart);
        } else {
            this.rootFolder.setValue(folderToStart.getParent());
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new FindFilesTask(rootFolder.getValue(), pathsToUpdate);
    }
    
    public void restartIn(Path location) {
        setSearchLocation(location);
        this.refresh();
    }
    
    public ObjectProperty<Path> searchPathProperty() {
        return this.rootFolder;
    }

	@Override
	public void refresh() {
		this.restart();
	}
	
	@Override
	public void cancelUpdate() {
		this.cancel();
	}
	
	@Override
	public void startUpdate() {
		this.start();
	}
    
	private void registerShutdownHook() {
		Runnable shutDownAction = ()->Platform.runLater(()->this.cancelUpdate());
        Runtime.getRuntime().addShutdownHook(new Thread(shutDownAction));
	}

}
