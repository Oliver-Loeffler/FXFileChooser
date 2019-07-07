package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

class FindFilesTaskTest extends ApplicationTest {

	private final Path searchLocation = Paths.get("TestData/SomeFiles");

	private static final Path NO_FILES_IN_HERE = Paths.get("TestData/NoFilesHere");

	private final ObservableList<IndexedPath> listOfPaths = FXCollections.observableArrayList();

	private FindFilesTask classUnderTest;

	@BeforeAll
	static void prepare() throws IOException {
		Files.createDirectories(NO_FILES_IN_HERE);
	}

	@AfterAll
	static void cleanup() throws IOException {
		Files.delete(NO_FILES_IN_HERE);
	}

	@Test
	void runningTheTask_inPopulatedFolder() throws Exception {

		classUnderTest = new FindFilesTask(searchLocation, listOfPaths);

		runTask(classUnderTest);

		Set<String> fileNames = listOfPaths.stream().map(IndexedPath::asPath).map(Path::getFileName)
				.map(String::valueOf).collect(Collectors.toSet());

		assertAll(
				() -> assertEquals(11, listOfPaths.size(), "files found"),
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

	private void runTask(Task<?> task) throws InterruptedException, ExecutionException {
		task.setOnFailed(e -> {
			throw new AssertionFailedError("Could not execute task - task failed");
		});
		task.setOnCancelled(e -> {
			throw new AssertionFailedError("Could not execute task - was cancelled");
		});

		task.run();

		Invoke.andWait(() -> {
			while (task.isRunning()) {
				/* wait */}
		});
	}

	@Test
	void runningTheTask_inEmptyFolder() throws Exception {

		ObservableList<IndexedPath> listOfPaths = FXCollections.observableArrayList();
		classUnderTest = new FindFilesTask(NO_FILES_IN_HERE, listOfPaths);

		runTask(classUnderTest);

		assertEquals(0, listOfPaths.size());
	}

}
