package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.function.Predicate;

public class PathFilter {
    public static PathFilter create(String label, Predicate<Path> p) {
        return new PathFilter(label, p);
    }
    
    public static PathFilter forFileExtension(String label, String regex) {
    		return new PathFilter(label, p->{
        		String name = p.getFileName().toString();
        		if (null != name) {
        			int lastDot = name.lastIndexOf('.');
        			if (lastDot > 0) {
        				String ext = name.substring(lastDot);
        				if (ext != null && !ext.isEmpty()) {
        					return ext.matches("[.]"+regex+"$");
        				} 
        			}
        		} 
        		return false;
        });
    }
    
    private final String name;
    
    private final Predicate<Path> criterion;
    
    private PathFilter(String label, Predicate<Path> criterion) {
        this.name = label;
        this.criterion = criterion;
    }
    
    public String getName() {
    		return this.name;
    }
    
    public Predicate<Path> getCriterion() {
    		return this.criterion;
    }
}
