package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.Test;

class IndexedPathTest {
	
	private IndexedPath classUnderTest;

	@Test
	void asPath() {
		
		Path source = Paths.get("TestData/SomeFiles/HorrbibleSpreadSheet.xls").toAbsolutePath();
		classUnderTest = IndexedPath.valueOf(source);
		
		assertEquals(source, classUnderTest.asPath());
		
	}
	
	@Test
	void stringRepresentation() {
		
		Path source = Paths.get("TestData/SomeFiles/HorrbibleSpreadSheet.xls").toAbsolutePath();
		classUnderTest = IndexedPath.valueOf(source);
		
		assertEquals(source.toString(), classUnderTest.toString());
		
	}
	
	@Test
	void getTimestamp() {
		
		Path source = Paths.get("TestData/SomeFiles/HorrbibleSpreadSheet.xls").toAbsolutePath();
		classUnderTest = IndexedPath.valueOf(source);
		
		
		FileTime timestamp = classUnderTest.getTimestamp();
		
		assertNotNull(timestamp);
		
	}

}
