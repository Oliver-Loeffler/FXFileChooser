/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class FindFilesTaskTest extends FxTestTemplate {

    private final Path searchLocation = Paths.get("TestData/SomeFiles");

    private ObservableList<IndexedPath> consumerCollection;

    private FindFilesTask classUnderTest;

    @BeforeEach
    void prepareConsumer() {
        sleep(300);
        consumerCollection = FXCollections.observableArrayList();
        consumerCollection.add(IndexedPath.valueOf(Paths.get("/notexisting")));
    }

    @Test
    void runningTheTask_inPopulatedFolder() throws Exception {
        classUnderTest = new FindFilesTask(searchLocation, consumerCollection);

        Awaitility.await()
                  .atMost(Duration.ofSeconds(60))
                  .until(classUnderTest::call, allFilesHaveBeenProcessed());

        Set<String> fileNames = consumerCollection.stream()
                                                  .map(IndexedPath::toString)
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
        classUnderTest = new FindFilesTask(emptyDirectory, consumerCollection);
        Awaitility.await()
                  .atMost(Duration.ofSeconds(30))
                  .until(classUnderTest::call, allFilesHaveBeenProcessed());
        assertEquals(0, consumerCollection.size());
    }

    @Test
    void that_null_for_listOfPaths_causes_exception() {
        Path path = Paths.get("./");
        Throwable t = assertThrows(NullPointerException.class, () -> new FindFilesTask(path, null));
        assertEquals("listOfPaths must not be null", t.getMessage());
    }

    @Test
    void that_null_for_searchDirectory_works() {
        classUnderTest = new FindFilesTask(null, consumerCollection);
        assertEquals(1, consumerCollection.size());

        Awaitility.await()
          .atMost(Duration.ofSeconds(30))
          .until(classUnderTest::call, allFilesHaveBeenProcessed());
        assertEquals(0, consumerCollection.size());
    }

    @Test
    void that_files_are_not_processed_as_directory() {
        /*
         * 
         * The test if a file is given or a directory is not performed in FindFilesTask.
         * If a file is provided instead of a directory, then task will not search 
         * the parent directory yet.
         * 
         */
        Path givenFile = Paths.get("TestData/SomeFiles/TestFile5.txt");

        classUnderTest = new FindFilesTask(givenFile, consumerCollection);

        Awaitility.await()
                  .atMost(Duration.ofSeconds(30))
                  .until(classUnderTest::call, allFilesHaveBeenProcessed());

        assertEquals(0, consumerCollection.size());
        
    }

    @ParameterizedTest
    @CsvSource({
        "       0,       1",
        "       6,       1",
        "     999,       4",
        "    1233,       6",
        " 1234233,    6171",
        "87366113,  436830"
    })
    void that_progress_intervall_is_reasonable(int pathsInDirectory, int interval) {
        Path givenFile = Paths.get("TestData/SomeFiles/TestFile5.txt");
        classUnderTest = new FindFilesTask(givenFile, consumerCollection);
        assertEquals(interval, classUnderTest.getProgressInterval(pathsInDirectory));
    }

    private Predicate<Integer> allFilesHaveBeenProcessed() {
        return v -> v >= 0;
    }
}
