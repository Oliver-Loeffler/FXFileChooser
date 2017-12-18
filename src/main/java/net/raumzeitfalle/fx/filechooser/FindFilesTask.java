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
        boolean oneUpdateOnly = false;
        if (oneUpdateOnly) {
            List<Path> candidates = Files.list(directory)
                    .parallel()
                    .filter(p->!p.toFile().isDirectory())
                    .collect(Collectors.toList());
                clearAndUpdate(candidates);    
        } else {
            invokeAndWait(()-> pathsToUpdate.clear());
            Files.list(directory)
                .parallel()
                .filter(p->!p.toFile().isDirectory())
                .forEach(this::addPathToList);
        }
        return null;
    }

    
    private void clearAndUpdate(List<Path> paths) {
      Platform.runLater(
              ()->{ pathsToUpdate.clear(); 
                    pathsToUpdate.addAll(paths);});
    }
        
    private void invokeAndWait(Runnable r) throws InterruptedException, ExecutionException {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }
    
    private void addPathToList(Path p) {
      Platform.runLater(()->pathsToUpdate.add(p));
    }
}
