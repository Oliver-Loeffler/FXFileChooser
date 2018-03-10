package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

class RefreshBuffer {
    
        static RefreshBuffer get(ObservableList<Path> target) {
            return new RefreshBuffer(10, target);
        }
        
        private final Set<Path> cache;
        
        private final ObservableList<Path> target;
        
        private final int cacheSize;
                        
        private RefreshBuffer(int cacheSize, ObservableList<Path> target) {
            this.cache = new HashSet<>(cacheSize);
            this.target = target;
            this.cacheSize = cacheSize;
        }
        
        void update(Path path) {
            cache.add(path);
            if (cache.size() > cacheSize) {
                flush();
            }    
        }
        
        void flush() {
            Set<Path> update = cache.stream().collect(Collectors.toSet());
            cache.clear(); 
            Invoke.later(()->target.addAll(update));
        }
        
    }
