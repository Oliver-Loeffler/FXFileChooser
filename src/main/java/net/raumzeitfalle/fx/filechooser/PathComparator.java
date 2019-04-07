package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Provides various {@link Comparator} variants to compare {@link Path} objects.
 */
class PathComparator {
	
	static enum Option {
		ASCENDING,
		DESCENDING;
	}
	
	static Comparator<Path> ascendingByName() {
		return lexical(Option.ASCENDING);
	}

	static Comparator<Path> descendingByName() {
		return lexical(Option.DESCENDING);
	}
	
	static Comparator<Path> lexical(Option option) {
		int order = option.equals(Option.ASCENDING) ? 1 : -1;
		return (Path a, Path b)-> order * a.compareTo(b);
	}
	
	static Comparator<Path> descendingLastModified() {
		return byLastModified(Option.DESCENDING);
	}
	
	static Comparator<Path> ascendingLastModified() {
		return byLastModified(Option.ASCENDING);
	}
	
	static Comparator<Path> byLastModified(Option option) {
		Function<Path,Instant> mapping = p -> {
			try {
				return Files.getLastModifiedTime(p).toInstant();
			} catch (IOException e) {
				return LocalDateTime.MAX.atZone(ZoneId.systemDefault()).toInstant();
			}
		};
		return byTime(mapping, option);
	}
		
	static Comparator<Path> byTime(Function<Path,Instant> mapping, Option option) {
		int order = option.equals(Option.ASCENDING) ? 1 : -1;
		return (Path a, Path b)-> order * mapping.apply(a).compareTo(mapping.apply(b));
	}
					
	private PathComparator() {
		// provides short cuts for commonly used comparators
	}

}
