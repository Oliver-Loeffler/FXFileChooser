package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Path;


import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FindFilesTask extends Task<Void>{
    
    private final ObservableList<File> pathsToUpdate;
    
    private final Path directory;
    
    public FindFilesTask(Path searchFolder, ObservableList<File> listOfPaths) {
        this.pathsToUpdate = listOfPaths;
        this.directory = searchFolder;
    }

    @Override
    protected Void call() throws Exception {
            Invoke.andWait(()-> pathsToUpdate.clear());
            
            
            
            File dir = new File(directory.toAbsolutePath().toString());
            File[] files = dir.listFiles();
            updateProgress(0, files.length);
            System.out.println("So many files: " + dir.list().length);
            
            int cacheSize = determineCacheSize(files);
            
            RefreshBuffer buffer = RefreshBuffer.get(this,cacheSize, pathsToUpdate);
            
            for (int f = 0; f < files.length; f++) {
                if (isCancelled()) {
                    break;
                }
                updateProgress(f+1, files.length);
                if (!files[f].isDirectory() && files[f].exists()) {
                    buffer.update(files[f]);
                }
            }
            buffer.flush();
            updateProgress(files.length, files.length);
          
        return null;
    }

    private int determineCacheSize(File[] files) {
        int items = files.length;
        if (items > 100_000) {
            return 500;
        }
        if (items > 50_000) {
            return 100;
        }
        if (items > 15_000) {
            return 100;
        }
        if (items > 5_000) {
            return 100;
        }
        if (items > 1_000) {
            return 50;
        }
        return 10;
    }
}
