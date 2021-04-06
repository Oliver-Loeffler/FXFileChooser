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
