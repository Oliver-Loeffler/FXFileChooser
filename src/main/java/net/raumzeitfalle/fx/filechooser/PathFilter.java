package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.function.Predicate;

public class PathFilter {
    public static PathFilter create(String label, Predicate<Path> p) {
        return new PathFilter(label, p);
    }
    
    private PathFilter(String label, Predicate<Path> p) {
        
    }
}
