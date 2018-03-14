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
	boolean matches(Path path);
	
	/**
	 * @param file {@link File}
	 * @return true in case the given {@link File} matches with the {@link Predicate}.
	 */
	default boolean matches(File file) {
		return this.matches(file.toPath());
	}
	
	/**
	 * In some cases it is beneficial to combine {@link PathFilter} objects.
	 * 
	 * @param other {@link PathFilter} to be combined with this one
	 * @return {@link PathFilter} where this ones {@link Predicate} and the others {@link PathFilter} {@link Predicate} are combined using a logical OR.
	 */
	PathFilter combine(SimplePathFilter other);
}
