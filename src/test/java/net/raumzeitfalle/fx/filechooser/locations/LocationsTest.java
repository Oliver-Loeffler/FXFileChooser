/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
	
	
}
