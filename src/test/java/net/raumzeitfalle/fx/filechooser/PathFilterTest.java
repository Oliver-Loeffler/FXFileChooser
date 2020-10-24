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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


class PathFilterTest {
	
	private PathFilter classUnderTest;

	@ParameterizedTest
	@CsvSource({
		"index.html,  true",
		"index.php,  false",
		"index,      false",
		".html,       true"
	})
	void criterion(String fileNameEntered, Boolean expectedPredicateResult) {
		
		classUnderTest = PathFilter.create("HTML file", p->p.getFileName()
															.toString()
															.endsWith(".html"));
		Predicate<Path> criterion = classUnderTest.getPredicate();
		
		assertEquals(criterion.test(Paths.get(fileNameEntered)), expectedPredicateResult);
				
	}

	@Test
	void matches_forExtension() {
		
		String extension = "xlsx";
		classUnderTest = PathFilter.forFileExtension(extension);

		assertEquals("*.xlsx", classUnderTest.getName());
		assertTrue(classUnderTest.matches(Paths.get("MyFile."+extension)));
		assertTrue(classUnderTest.matches(new File("MyFile."+extension)));
		assertFalse(classUnderTest.matches(Paths.get("MyFile.txt")));
		assertFalse(classUnderTest.matches(new File("MyFile.txt")));
	}
	
	@Test
	void matches_forExtensionWithLabel() {
		
		String extension = "xlsx";
		classUnderTest = PathFilter.forFileExtension("HorribleSpreadSheet", extension);

		assertEquals("HorribleSpreadSheet", classUnderTest.getName());
		assertTrue(classUnderTest.matches(Paths.get("MyFile."+extension)));
		assertTrue(classUnderTest.matches(new File("MyFile."+extension)));
		assertFalse(classUnderTest.matches(Paths.get("MyFile.txt")));
		assertFalse(classUnderTest.matches(new File("MyFile.txt")));
	}
	
	@Test
	void combinedName_forExtensionWithLabel() {
		
		classUnderTest = PathFilter
				.create("HTML file", p->String.valueOf(p.getFileName()).endsWith(".html"))
				 .combine(PathFilter.forFileExtension("HorribleSpreadSheet", "xlsx"));

		assertEquals("HTML file, HorribleSpreadSheet", classUnderTest.getName());
	}
	
	
	@ParameterizedTest
	@CsvSource({
		"textfile.,                      false",
		"index.html,                      true",
		"spreadSheet.XlSx,                true",
		".xlsx,                          false",
		"fileNameWithoutExtension,       false",
		"textfile.txt,                   false",
		"./,                             false",
		"/test/share,                    false",
		"/,                              false",
		"//volume/share/spreadheet.xlsx,  true"
	})
	void combine(String fileNameEntered, Boolean expectedPredicateResult) {
		
		classUnderTest = PathFilter
					.create("HTML file", p->String.valueOf(p.getFileName()).endsWith(".html"))
					 .combine(PathFilter.forFileExtension("HorribleSpreadSheet", "xlsx"));
		
		Predicate<Path> criterion = classUnderTest.getPredicate();
		
		assertEquals(criterion.test(Paths.get(fileNameEntered)), expectedPredicateResult);
	
	}

	@ParameterizedTest
	@ValueSource(strings= {
			"textfile.",
			"index.html",
			"spreadSheet.XlSx",
			".xlsx",
			"fileNameWithoutExtension",
			"textfile.txt",
			"./",
			"/test/share",
			"/",
			"",
			"//volume/share/spreadheet.xlsx",
			"ABC",
			"ABC.txt"
	})
	void acceptAll(String fileNameEntered) {
		classUnderTest = PathFilter.acceptAllFiles("all files");
		Predicate<Path> criterion = classUnderTest.getPredicate();
		Path path = Paths.get(fileNameEntered);
		assertTrue(criterion.test(path));
	}
}
