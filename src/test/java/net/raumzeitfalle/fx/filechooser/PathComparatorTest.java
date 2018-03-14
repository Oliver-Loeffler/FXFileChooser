package net.raumzeitfalle.fx.filechooser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import net.raumzeitfalle.fx.filechooser.PathComparator.Option;

public class PathComparatorTest {
	
	private final Path b = Paths.get("byFarOldestFile.txt");
	
	private final Path a = Paths.get("aNewFile.txt");
	
	private final Path c = Paths.get("crazyLargeFile.cfg");
	
	private List<Path> files = Arrays.asList(b,a,c);
	
	private Comparator<Path> comparatorUnderTest;

	@Test
	public void byName_ascending() {
		
		comparatorUnderTest = PathComparator.ascendingByName();
	
		List<Path> sorted = sort(comparatorUnderTest);
		
		assertEquals(a, sorted.get(0));
		assertEquals(b, sorted.get(1));
		assertEquals(c, sorted.get(2));
		
	}
	
	@Test
	public void byName_descending() {
		
		comparatorUnderTest = PathComparator.descendingByName();
	
		List<Path> sorted = sort(comparatorUnderTest);
		
		assertEquals(a, sorted.get(2));
		assertEquals(b, sorted.get(1));
		assertEquals(c, sorted.get(0));
		
	}
	
	
	@Test
	public void byLastModified_ascending() throws IOException {
		
		Function<Path, Instant> testMapping = p -> {
			if (p.getFileName().toString().toLowerCase().startsWith("A")) {
				return LocalDateTime.MIN.atZone(ZoneId.systemDefault()).toInstant();
			} else {
				return LocalDateTime.MAX.atZone(ZoneId.systemDefault()).toInstant();
			}
		};
		
		comparatorUnderTest = PathComparator.byTime(testMapping, Option.DESCENDING);
		
		Path fileA = Paths.get("./TestData/A-oldest.txt");		
		Path fileB = Paths.get("./TestData/B-latest.txt");
		
		List<Path> paths = sortByInstant(testMapping,fileA,fileB);
		
		assertEquals(fileA, paths.get(0));
		assertEquals(fileB, paths.get(1));
		
	}



	private List<Path> sortByInstant(Function<Path, Instant> testMapping, Path... file) {
		return Arrays.stream(file)
				.sorted(comparatorUnderTest)
				.collect(Collectors.toList());
	}

	private List<Path> sort(Comparator<Path> byName) {
		return files.stream().sorted(byName).collect(Collectors.toList());
	}

}
