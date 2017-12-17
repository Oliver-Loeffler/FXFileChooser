package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FindFilesTask extends Task<Void>{
    
    private final ObservableList<Path> pathsToUpdate;
    
    private final Path directory;
    
    public FindFilesTask(Path searchFolder, ObservableList<Path> listOfPaths) {
        this.pathsToUpdate = listOfPaths;
        this.directory = searchFolder;
    }

    @Override
    protected Void call() throws Exception {
        
        List<Path> candidates = Files.list(directory)
            .parallel()
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        
        clearAndUpdate(candidates);
        
        return null;
    }

    
    private void clearAndUpdate(List<Path> paths) {
      Platform.runLater(
              ()->{ pathsToUpdate.clear(); 
                    pathsToUpdate.addAll(paths);});
    }
    
}
