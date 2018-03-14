package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * SimplePathfilter is a default implementation of the {@link PathFilter} interface. {@link SimplePathFilter} provides convenient static factory methods to create simple filters for {@link java.nio.file.Path} objects. 
 */
public class SimplePathFilter implements PathFilter {
    public static SimplePathFilter create(String label, Predicate<Path> p) {
        return new SimplePathFilter(label, p);
    }
    
    protected static SimplePathFilter acceptAll() {
    		return new SimplePathFilter("", p->true);
    }
    
    public static SimplePathFilter forFileExtension(String label, String extension) {
    		return new SimplePathFilter(label, p->{
    			Path filename = p.getFileName();
        		if (null != filename) {
        			String name = filename.toString().toLowerCase();
        			int lastDot = name.lastIndexOf('.');
        			if (lastDot > 0) {
        				return name.substring(lastDot).matches("[.]"+extension+"$"); 
        			}
        		} 
        		return false;
        });
    }
    
    private final String name;
    
    private final Predicate<Path> criterion;
    
    private SimplePathFilter(String label, Predicate<Path> criterion) {
        this.name = label;
        this.criterion = criterion;
    }
    
    public String getName() {
    		return this.name;
    }
    
    public Predicate<Path> getPredicate() {
    		return this.criterion;
    }
    
    /**
     * Creates a new PathFilter as a combination of the this one and any other.
     * @param other {@link SimplePathFilter}
     * @return {@link SimplePathFilter} new PathFilter with both predicates combined.
     */
    public SimplePathFilter combine(SimplePathFilter other) {
    		String label = this.name + ", " + other.name;
    		return new SimplePathFilter(label, this.criterion.or(other.criterion));
    }
    
    public boolean matches(Path path) {
    		return this.getPredicate().test(path);
    }
}
