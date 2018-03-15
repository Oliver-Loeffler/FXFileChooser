package net.raumzeitfalle.fx.filechooser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class FileChooserModelTest {
	
	private static final Path TEST_ROOT = Paths.get("./TestData");
	
	private final FileChooserModel classUnderTest = createTestModel(TEST_ROOT, new ArrayList<>());
	
	@Test
	public void test() {
		
		classUnderTest.updateFilesIn(TEST_ROOT);
		System.out.println(classUnderTest.getFilteredPaths());
		assertTrue(classUnderTest.getFilteredPaths().isEmpty());
		
		classUnderTest.updateFilesIn(TEST_ROOT.resolve("SomeFiles"));
		classUnderTest.refreshFiles();
	
		assertEquals(11, classUnderTest.getFilteredPaths().size());
		
	}

	private FileChooserModel createTestModel(Path testRoot, List<Path> paths) {
		ObservableList<Path> observableList = FXCollections.observableArrayList(paths);
		
		UpdateService service = new UpdateService() {
			
			private Path location = testRoot;
			
			@Override
			public void startUpdate() { /* nothing to do here */ }
			
			@Override
			public ObjectProperty<Path> searchPathProperty() { return new SimpleObjectProperty<Path>(testRoot); }
			
			@Override
			public ReadOnlyBooleanProperty runningProperty() { return new SimpleBooleanProperty(false); }
			
			@Override
			public void restartIn(Path location) { this.location = location; this.refresh(); }
			
			@Override
			public void refresh() { observableList.clear(); try {
					Files.list(location)
						.filter(Files::isRegularFile)
						.forEach(observableList::add);
				} catch (IOException e) { /* the test will fail then */ } 
			}
			
			@Override
			public ReadOnlyDoubleProperty progressProperty() { return new SimpleDoubleProperty(0.0); } 
			
			@Override
			public void cancelUpdate() { /* nothing to do here */ }
		};
		
		Supplier<UpdateService> serviceProvider = ()->service;
		return new FileChooserModel(testRoot, observableList, serviceProvider);
	}
	
	private static class EmptyTask extends Task<Void> {

		@Override
		protected Void call() throws Exception { return null; }
		
	}

}
