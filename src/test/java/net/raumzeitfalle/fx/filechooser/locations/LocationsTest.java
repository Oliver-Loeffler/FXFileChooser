package net.raumzeitfalle.fx.filechooser.locations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class LocationsTest {

	@Test
	void at_with_null_argument() {
		
		Throwable t = assertThrows(NullPointerException.class,
				()->Locations.at(null));
		
		assertEquals("path must not be null", t.getMessage());
		
	}
	
	@Test
	void at_with_file_having_a_parent() {
		
		Path path = Paths.get("TestData/SomeFiles/TestFile1.txt");
		Location location = Locations.at(path);
		
		assertAll(
			()->assertEquals("TestData/SomeFiles", location.getName().replace("\\", "/"),"name"),
			()->assertTrue(location.exists(), "exist"),
			()->assertEquals(path.getParent(), location.getPath(), "path")
		);
		
	}
	
	@Test
	void at_with_directory_having_a_parent() {
		
		Path path = Paths.get("TestData/SomeFiles/");
		Location location = Locations.at(path);
		
		assertAll(
			()->assertEquals("TestData/SomeFiles", location.getName().replace("\\", "/"),"name"),
			()->assertTrue(location.exists(), "exist"),
			()->assertEquals(path, location.getPath(), "path")
		);
		
	}
	
	@Test
	void at_with_directory_having_no_parent() {
		
		Path path = Paths.get("\\\\root\\share");
		Location location = Locations.at(path);
		
		assertAll(
			()->assertEquals("//root/share/", location.getName().replace("\\", "/"),"name"),
			()->assertEquals("//root/share/", location.getPath().toString().replace("\\", "/"), "path")
		);
		
	}
	
}
