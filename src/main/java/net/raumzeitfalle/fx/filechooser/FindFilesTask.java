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
            // slowest (!) on Windows with SSD and 240000 files
//            try {
//            Files.list(directory)
//                .peek(p->{
//                    if (isCancelled()) throw new Break();
//                })
//                .filter(Files::isRegularFile)
//                .forEach(p->Invoke.later(()->pathsToUpdate.add(p.toFile())));
//            } catch (Break b) {
//                // aborted
//            }
            
            for (int f = 0; f < files.length; f++) {
                if (isCancelled()) {
                    break;
                }
                updateProgress(f+1, files.length);
                if (!files[f].isDirectory() && files[f].exists()) {
                    // File file = files[f];
                    //Invoke.later(()->pathsToUpdate.add(file));
                    buffer.update(files[f]);
                }
            }
            buffer.flush();
            updateProgress(files.length, files.length);
          
          
           // alternatives: still slow as JavaFX UI updates take most of the time
//            Arrays.stream(files)
//                  .parallel()
//                  .peek(f -> {if (isCancelled()) throw new Break();})
//                  .filter(File::exists)
//                  .filter(f->!f.isDirectory())
//                  .forEach(f -> buffer.update(f));
//            buffer.flush();
           
//            try {
//                Files.list(directory)
//                //.parallel()
//                .peek(f -> {if (isCancelled()) throw new Break();})
//                .filter(p->!p.toFile().isDirectory())
//                .forEach(p -> Invoke.later(()->pathsToUpdate.add(p)));
//                //.forEach(p -> {
//                //    buffer.update(p);
//                //});    
//            } catch (Break b) {
//                // this was an intended abort by task cancellation
//                System.out.println("FindFilesTask aborted.");
//            }
            //buffer.flush();
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
