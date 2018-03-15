package net.raumzeitfalle.fx.filechooser;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.Test;

public class PathFilterTest {
	
	private PathFilter classUnderTest;

	@Test
	public void criterion() {
		
		classUnderTest = PathFilter.create("HTML file", p->p.getFileName().toString().endsWith(".html"));
		Predicate<Path> criterion = classUnderTest.getPredicate();
		
		assertTrue(criterion.test(Paths.get("index.html")));
		assertFalse(criterion.test(Paths.get("index.php")));
		assertFalse(criterion.test(Paths.get("index")));
		assertTrue(criterion.test(Paths.get(".html")));
		
	}
	
	@Test
	public void combine() {
		
		classUnderTest = PathFilter
					.create("HTML file", p->String.valueOf(p.getFileName()).endsWith(".html"))
					 .combine(PathFilter.forFileExtension("HorribleSpreadSheet", "xlsx"));
		
		Predicate<Path> criterion = classUnderTest.getPredicate();
		
		assertFalse(criterion.test(Paths.get("textfile.")));
		
		assertTrue(criterion.test(Paths.get("index.html")));
		assertTrue(criterion.test(Paths.get("spreadSheet.XlSx")));
		assertFalse(criterion.test(Paths.get(".xlsx")));
		assertFalse(criterion.test(Paths.get("fileNameWithoutExtension")));
		assertFalse(criterion.test(Paths.get("textfile.txt")));
		assertFalse(criterion.test(Paths.get("./")));
		assertFalse(criterion.test(Paths.get("/test/share")));
		assertFalse(criterion.test(Paths.get("/")));
		assertTrue(criterion.test(Paths.get("//volume/share/spreadheet.xlsx")));
		
		assertEquals("HTML file, HorribleSpreadSheet", classUnderTest.getName());
	}

	@Test
	public void acceptAll() {
		classUnderTest = PathFilter.acceptAllFiles("all files");
		Predicate<Path> criterion = classUnderTest.getPredicate();

		assertTrue(criterion.test(Paths.get("")));
		assertTrue(criterion.test(Paths.get("ABC")));
		assertTrue(criterion.test(Paths.get("ABC.txt")));
	}
}
