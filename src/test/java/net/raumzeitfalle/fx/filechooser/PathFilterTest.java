/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;


class PathFilterTest {
	
	private PathFilter classUnderTest;

	@Test
	void criterion() {
		
		classUnderTest = PathFilter.create("HTML file", p->p.getFileName().toString().endsWith(".html"));
		Predicate<Path> criterion = classUnderTest.getPredicate();
		
		assertTrue(criterion.test(Paths.get("index.html")));
		assertFalse(criterion.test(Paths.get("index.php")));
		assertFalse(criterion.test(Paths.get("index")));
		assertTrue(criterion.test(Paths.get(".html")));
		
	}
	
	@Test
	void combine() {
		
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
	void acceptAll() {
		classUnderTest = PathFilter.acceptAllFiles("all files");
		Predicate<Path> criterion = classUnderTest.getPredicate();

		assertTrue(criterion.test(Paths.get("")));
		assertTrue(criterion.test(Paths.get("ABC")));
		assertTrue(criterion.test(Paths.get("ABC.txt")));
	}
}
