package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;


import javafx.collections.ObservableList;

class RefreshBuffer {
    
        static RefreshBuffer get(FindFilesTask task, int cachSize, ObservableList<Path> target) {
            return new RefreshBuffer(task, cachSize, target);
        }
        
        private final List<Path> cache;
        
        private final AtomicReference<List<Path>> atomicCache;
        
        private final ReentrantLock lock = new ReentrantLock();
        
        private final ObservableList<Path> target;
                
        private final int cacheSize;
        
        private final FindFilesTask task;
                                        
        private RefreshBuffer(FindFilesTask task, int cacheSize, ObservableList<Path> target) {
            this.cache = new ArrayList<>(2*cacheSize);
            this.target = target;
            this.cacheSize = cacheSize;
            this.atomicCache = new AtomicReference<List<Path>>(cache);
            this.task = task;
            
        }
        
        void update(Path file) {
            cache.add(file);
            if (!task.isCancelled() && cache.size() > cacheSize) {
                try {
                    flush();    
                } catch (InterruptedException e) {
                    System.out.println("Cannot update Paths cache!");
                }
            }    
        }
        
        void flush() throws InterruptedException {
            this.lock.lock();
            Path[] update = this.atomicCache.get().toArray(new Path[0]);
            Invoke.later(()->{
                target.addAll(update);
            });
            this.atomicCache.get().clear();
            this.lock.unlock();
        }
        
    }
