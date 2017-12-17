package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
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
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        
        clearList();
        //addPathsToList(paths);
        uploadInChunks(candidates);
        
        return null;
    }

    private void uploadInChunks(List<Path> candidates) {
        int chunk = 1;
        if (candidates.size() > 10_000) {
            chunk = 100;
        }
        int start = 0;
        int end = start+chunk;
        while (start < candidates.size()) {            
            addPathsToList(candidates.subList(start, end));
            start = end;
            end = (end + chunk) > candidates.size() ? candidates.size() : (end+chunk);                    
        }
    }

    private void clearList() throws InterruptedException, ExecutionException {
        invokeAndWait(()->pathsToUpdate.clear());
    }
    
    private void invokeAndWait(Runnable r) throws InterruptedException, ExecutionException {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }
    
    private void addPathsToList(List<Path> paths) {
      Platform.runLater(()->pathsToUpdate.addAll(paths));
    }
    
    private void addPathToList(Path path) {
        Platform.runLater(()->pathsToUpdate.add(path));
      }
    


}
