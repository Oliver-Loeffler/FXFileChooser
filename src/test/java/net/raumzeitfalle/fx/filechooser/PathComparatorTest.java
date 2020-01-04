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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class PathComparatorTest {
	
	private final Path b = Paths.get("byFarOldestFile.txt");
	
	private final Path a = Paths.get("aNewFile.txt");
	
	private final Path c = Paths.get("crazyLargeFile.cfg");
	
	private Comparator<IndexedPath> comparatorUnderTest;

	@Test
	void byName_ascending() {
		
		comparatorUnderTest = PathComparator.byName();
	
		List<Path> sorted = sortUsing(comparatorUnderTest, a,b,c);
		
		assertEquals(a, sorted.get(0));
		assertEquals(b, sorted.get(1));
		assertEquals(c, sorted.get(2));
		
	}
	
	@Test
	void byName_descending() {
		
		comparatorUnderTest = PathComparator.byName().reversed();
	
		List<Path> sorted = sortUsing(comparatorUnderTest, a,b,c);
		
		assertEquals(a, sorted.get(2));
		assertEquals(b, sorted.get(1));
		assertEquals(c, sorted.get(0));
		
	}
	
	
	@Test
	void byLastModified_ascending() throws IOException {
		
		
		comparatorUnderTest = PathComparator.byLastModified();
		
		Path fileA = Paths.get("./TestData/A-oldest.txt");		
		Path fileB = Paths.get("./TestData/B-latest.txt");
		
		List<Path> paths = sortUsing(comparatorUnderTest,fileA,fileB);
		
		assertEquals(fileA, paths.get(0));
		assertEquals(fileB, paths.get(1));
		
	}

	private List<Path> sortUsing(Comparator<IndexedPath> comparatorUnderTest, Path... file) {
		return Arrays.stream(file)
				.map(IndexedPath::valueOf)
				.sorted(comparatorUnderTest)
				.map(IndexedPath::asPath)
				.collect(Collectors.toList());
	}

}
