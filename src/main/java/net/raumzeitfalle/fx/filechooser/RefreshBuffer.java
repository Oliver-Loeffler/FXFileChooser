/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;


import javafx.collections.ObservableList;

class RefreshBuffer {
    
        static RefreshBuffer get(FindFilesTask task, int cachSize, ObservableList<IndexedPath> target) {
            return new RefreshBuffer(task, cachSize, target);
        }
        
        private final List<IndexedPath> cache;
        
        private final AtomicReference<List<IndexedPath>> atomicCache;
        
        private final ReentrantLock lock = new ReentrantLock();
        
        private final ObservableList<IndexedPath> target;
                
        private final int desiredCacheSize;
        
        private final FindFilesTask task;
                                        
        private RefreshBuffer(FindFilesTask task, int cacheSize, ObservableList<IndexedPath> target) {
            this.cache = new ArrayList<>(2*cacheSize);
            this.target = target;
            this.desiredCacheSize = cacheSize;
            this.atomicCache = new AtomicReference<>(cache);
            this.task = task;
        }
        
        void update(Path file) {
            cache.add(IndexedPath.valueOf(file));
            if (!task.isCancelled() && currentCacheSize() > desiredCacheSize) {
            		flush();    
            }    
        }

        private int currentCacheSize() {
            return cache.size();
        }
        
        void flush()  {
            this.lock.lock();
            
            try {
            	
            	IndexedPath[] update = this.atomicCache.get().toArray(new IndexedPath[0]);
                Invoke.later(()->target.addAll(update));
                this.atomicCache.get().clear();
                
            } finally {
            		this.lock.unlock();	
            }
        }
        
    }
