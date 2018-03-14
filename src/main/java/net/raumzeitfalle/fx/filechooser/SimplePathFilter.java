package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * SimplePathfilter is a default implementation of the {@link PathFilter} interface. {@link SimplePathFilter} provides convenient static factory methods to create simple filters for {@link java.nio.file.Path} objects. 
 */
public final class SimplePathFilter implements PathFilter {
    
	/**
	 * Creates a new {@link PathFilter} with a label text and a {@link Path} {@link Predicate}.
	 * @param label GUI label text
	 * @param p {@link Predicate}
	 * @return {@link PathFilter}
	 */
	public static SimplePathFilter create(String label, Predicate<Path> p) {
        return new SimplePathFilter(label, p);
    }
    
	/**
	 * Creates a new {@link PathFilter} which generally matches with all files.
	 * @param name {@link String} GUI label text
	 * @return {@link PathFilter}
	 */
    public static SimplePathFilter acceptAll(String name) {
    		return new SimplePathFilter(name, p->true);
    }
    
    /**
     * Creates a new {@link PathFilter} for file name extensions such as (.html, .xls, .xml or .pdf). 
     * @param label GUI label text
     * @param extension {@link String} the file name extension
     * @return new {@link PathFilter}
     */
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
    public PathFilter combine(PathFilter other) {
    		String label = this.name + ", " + other.getName();
    		return new SimplePathFilter(label, this.criterion.or(other.getPredicate()));
    }
    
    public boolean matches(Path path) {
    		return this.getPredicate().test(path);
    }
}
