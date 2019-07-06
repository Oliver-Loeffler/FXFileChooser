package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Provides various {@link Comparator} variants to compare {@link Path} objects.
 */
class PathComparator {
	
	enum Option {
		ASCENDING,
		DESCENDING;
	}
	
	static Comparator<IndexedPath> ascendingByName() {
		return lexical(Option.ASCENDING);
	}

	static Comparator<IndexedPath> descendingByName() {
		return lexical(Option.DESCENDING);
	}
	
	static Comparator<IndexedPath> lexical(Option option) {
		int order = option.equals(Option.ASCENDING) ? 1 : -1;
		return (IndexedPath a, IndexedPath b)-> order * a.asPath().compareTo(b.asPath());
	}
	
	static Comparator<IndexedPath> descendingLastModified() {
		return byLastModified(Option.DESCENDING);
	}
	
	static Comparator<IndexedPath> ascendingLastModified() {
		return byLastModified(Option.ASCENDING);
	}
	
	static Comparator<IndexedPath> byLastModified(Option option) {
		int order = option.equals(Option.ASCENDING) ? 1 : -1;
		return (IndexedPath a, IndexedPath b)-> order * a.getTimestamp().compareTo(b.getTimestamp());
	}
					
	private PathComparator() {
		// provides short cuts for commonly used comparators
	}

}
