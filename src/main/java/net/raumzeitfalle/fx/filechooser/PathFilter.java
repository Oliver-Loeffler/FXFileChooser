package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The PathFilter interface is used by {@link FileChooserModel} to provide filter predicates matching specific file types. Each PathFilter also provides a name to populate e.g. text fields in {@link javafx.scene.control.MenuItem} or plain {@link javafx.scene.control.Label} fields.<br>
 */
public interface PathFilter {
	
	/**
	 * @return A name to be used in user interface dialogs.
	 */
	String getName();
	
	/**
	 * @return The {@link Predicate} to match a specific file type or a group of file types. 
	 */
	Predicate<Path> getPredicate();
	
	/**
	 * @param path {@link Path} to test
	 * @return true in case the given {@link Path} matches with the {@link Predicate}.
	 */
	default boolean matches(Path path) {
		return getPredicate().test(path);
	}
	
	/**
	 * @param file {@link File}
	 * @return true in case the given {@link File} matches with the {@link Predicate}.
	 */
	default boolean matches(File file) {
		return this.matches(file.toPath());
	}
	

    /**
     * Creates a new PathFilter as a combination of the this one and any other.
 	 * @param other {@link PathFilter} to be combined with this one
	 * @return {@link PathFilter} where this ones {@link Predicate} and the others {@link PathFilter} {@link Predicate} are combined using a logical OR.
     */
    default PathFilter combine(PathFilter other) {
    		String label = getName() + ", " + other.getName();
    		Predicate<Path> thisOne = this.getPredicate();
    		return create(label, thisOne.or(other.getPredicate()));
    }
    
    /**
	 * Creates a new {@link PathFilter} which generally matches with all files.
	 * @param name String value intended to be used as GUI text.
	 * @return {@link PathFilter}
	 */
    static PathFilter acceptAllFiles(String name) {
    		return create(name, p->true);
    }
    
    static PathFilter create(Predicate<Path> p) {
    		return create(String.valueOf(p), p);
    }
    
    static PathFilter create(String label, Predicate<Path> p) {
	    	return new PathFilter() {
				
				@Override
				public Predicate<Path> getPredicate() { return p; }
				
				@Override
				public String getName() { return label; }
			};
    }
    
    /**
     * Creates a new {@link PathFilter} for file name extensions such as (.html, .xls, .xml or .pdf). The label text will be automatically the extension with a &quot;*.&quot; prefix so for extension txt the label will be *.txt.
     * @param extension {@link String} the file name extension
     * @return new {@link PathFilter}
     */
    static PathFilter forFileExtension(String extension) {
    		return forFileExtension("*."+extension, extension);
    }
    
    /**
     * Creates a new {@link PathFilter} for file name extensions such as (.html, .xls, .xml or .pdf). 
     * @param label GUI label text
     * @param extension {@link String} the file name extension
     * @return new {@link PathFilter}
     */
    static PathFilter forFileExtension(String label, String extension) {
    		return create(label, p->{
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
}
