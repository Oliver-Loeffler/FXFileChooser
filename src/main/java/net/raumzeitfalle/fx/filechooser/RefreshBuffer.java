package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;


import javafx.collections.ObservableList;

class RefreshBuffer {
    
        static RefreshBuffer get(FindFilesTask task, int cachSize, ObservableList<File> target) {
            return new RefreshBuffer(task, cachSize, target);
        }
        
        private final List<File> cache;
        
        private final AtomicReference<List<File>> atomicCache;
        
        private final ReentrantLock lock = new ReentrantLock();
        
        private final ObservableList<File> target;
                
        private final int cacheSize;
        
        private final FindFilesTask task;
                                        
        private RefreshBuffer(FindFilesTask task, int cacheSize, ObservableList<File> target) {
            this.cache = new ArrayList<>(2*cacheSize);
            this.target = target;
            this.cacheSize = cacheSize;
            this.atomicCache = new AtomicReference<List<File>>(cache);
            this.task = task;
            
        }
        
        void update(File file) {
            cache.add(file);
            if (!task.isCancelled() && cache.size() > cacheSize) {
                try {
                    flush();    
                } catch (InterruptedException e) {
                    System.out.println("Cannot update queue");
                }
            }    
        }
        
        void flush() throws InterruptedException {
            this.lock.lock();
            File[] update = this.atomicCache.get().toArray(new File[0]);
            Invoke.later(()->{
                target.addAll(update);
            });
            this.atomicCache.get().clear();
            this.lock.unlock();
        }
        
    }
