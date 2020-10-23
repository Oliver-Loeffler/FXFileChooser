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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class FindFilesTaskTest extends ApplicationTest {

	private final Path searchLocation = Paths.get("TestData/SomeFiles");

	private ObservableList<IndexedPath> consumerCollection;

	private FindFilesTask classUnderTest;
	
	@BeforeEach
	void prepareConsumer() {
		consumerCollection = FXCollections.observableArrayList();
		consumerCollection.add(IndexedPath.valueOf(Paths.get("/notexisting")));
	}

	@Test
	void runningTheTask_inPopulatedFolder() throws Exception {

		classUnderTest = new FindFilesTask(searchLocation, consumerCollection);
		
		
		
		Awaitility.await()
				  .atMost(Duration.ofSeconds(30))
				  .until(classUnderTest::call, allFilesHaveBeenProcessed());
	
		Set<String> fileNames = consumerCollection.stream()
												  .map(IndexedPath::asPath)
												  .map(Path::getFileName)
												  .map(String::valueOf)
												  .collect(Collectors.toSet());

		assertAll(
				() -> assertEquals(11, consumerCollection.size(), "files found"),
				() -> assertTrue(fileNames.contains("HorrbibleSpreadSheet.xls")),
				() -> assertTrue(fileNames.contains("JustNumbers.csv")),
				() -> assertTrue(fileNames.contains("NewerDocument.docx")),
				() -> assertTrue(fileNames.contains("OldDocument.doc")),
				() -> assertTrue(fileNames.contains("SupposedToBeXtensible.xml")),
				() -> assertTrue(fileNames.contains("TestFile1.txt")),
				() -> assertTrue(fileNames.contains("TestFile2.txt")),
				() -> assertTrue(fileNames.contains("TestFile3.txt")),
				() -> assertTrue(fileNames.contains("TestFile4.txt")),
				() -> assertTrue(fileNames.contains("TestFile5.txt")),
				() -> assertTrue(fileNames.contains("XtremeHorrbibleSpreadSheet.xlsx")));
	}


	@Test
	void runningTheTask_inEmptyFolder(@TempDir Path emptyDirectory) throws Exception {

		ObservableList<IndexedPath> listOfPaths = FXCollections.observableArrayList();
		classUnderTest = new FindFilesTask(emptyDirectory, listOfPaths);

		Awaitility.await()
				  .atMost(Duration.ofSeconds(30))
				  .until(classUnderTest::call, allFilesHaveBeenProcessed());
		  
		assertEquals(0, listOfPaths.size());
	}
	
	private Predicate<Integer> allFilesHaveBeenProcessed() {
		return v->v>=0;
	}

}
