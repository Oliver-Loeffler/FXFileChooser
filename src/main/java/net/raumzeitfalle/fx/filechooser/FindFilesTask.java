package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
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
            Invoke.andWait(()-> pathsToUpdate.clear());
            AtomicReference<RefreshBuffer> buffer = new AtomicReference<RefreshBuffer>(RefreshBuffer.get(pathsToUpdate));
            
            try {
                Files.list(directory)
                .peek(f -> {if (isCancelled()) throw new Break();})
                .parallel()
                .filter(p->!p.toFile().isDirectory())
                .forEach(p -> {
                    buffer.get().update(p);
                });    
            } catch (Break b) {
                // this was an intended abort by task cancellation
                System.out.println("FindFilesTask aborted.");
            }
            buffer.get().flush();
        return null;
    }
       

    
    private static class Break extends RuntimeException {

        private static final long serialVersionUID = 5799667198172681610L;

        private Break() { }
    }
}
