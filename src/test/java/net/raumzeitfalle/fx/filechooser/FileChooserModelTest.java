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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class FileChooserModelTest {
	
	private static final Path TEST_ROOT = Paths.get("./TestData");
	
	private final FileChooserModel classUnderTest = createTestModel(TEST_ROOT, new ArrayList<>());

	
	@Test
	void listFileContents() {
	
		// update from a directory which only contains directories
		classUnderTest.getUpdateService().restartIn(TEST_ROOT);
		assertTrue(classUnderTest.getFilteredPaths().isEmpty());
		assertEquals(0, classUnderTest.allPathsSizeProperty().get());
		
		// take a path object
		classUnderTest.getUpdateService().restartIn(TEST_ROOT.resolve("SomeFiles/"));
		classUnderTest.updateFilesIn((File) null);
		
		assertEquals(11, classUnderTest.getFilteredPaths().size());
		assertEquals(11, classUnderTest.filteredPathsSizeProperty().get());
		assertEquals(11, classUnderTest.allPathsSizeProperty().get());
		
		// just take a file object pointing to a path
		classUnderTest.updateFilesIn(TEST_ROOT.resolve("SomeFiles/").toFile());
		
		assertEquals(11, classUnderTest.getFilteredPaths().size());
		assertEquals(11, classUnderTest.filteredPathsSizeProperty().get());
		assertEquals(11, classUnderTest.allPathsSizeProperty().get());
		
		// take a Path pointing to a filename
		classUnderTest.getUpdateService().restartIn(TEST_ROOT.resolve("SomeFiles").resolve("XtremeHorrbibleSpreadSheet.xlsx"));
		
		assertEquals(11, classUnderTest.getFilteredPaths().size());
		assertEquals(11, classUnderTest.filteredPathsSizeProperty().get());
		assertEquals(11, classUnderTest.allPathsSizeProperty().get());
		
		// take a Path pointing to a share only
		classUnderTest.getUpdateService().restartIn(Paths.get("//server/share"));
		
		assertEquals(11, classUnderTest.getFilteredPaths().size());
		assertEquals(11, classUnderTest.filteredPathsSizeProperty().get());
		assertEquals(11, classUnderTest.allPathsSizeProperty().get());
	}
	
	@Test
	void applySimpleStringFilter() {
		
		classUnderTest.getUpdateService().restartIn(TEST_ROOT.resolve("SomeFiles"));
		classUnderTest.updateFilterCriterion(".csv");
		assertEquals(1, classUnderTest.getFilteredPaths().size());

	}
	
	@Test
	void usingPathFilter() {
		classUnderTest.getUpdateService().restartIn(TEST_ROOT.resolve("SomeFiles"));
		PathFilter filter = PathFilter.create(p->p.toString().startsWith("Test"));
		
		classUnderTest.updateFilterCriterion(filter, "5");
		assertEquals(1, classUnderTest.getFilteredPaths().size());
		
	}
	
	@Test
	void lookIntoUsersHome(@TempDir Path emptyTemporaryDir) {
				
		
		PathFilter filter = PathFilter.acceptAllFiles("all files");

		classUnderTest.updateFilterCriterion(filter, "");
		classUnderTest.updateFilesIn(emptyTemporaryDir.toFile());
		
		classUnderTest.changeToUsersHome();
		
		assertTrue(classUnderTest.getFilteredPaths().size() > 1);
		
	}

	private FileChooserModel createTestModel(Path testRoot, List<IndexedPath> paths) {
		ObservableList<IndexedPath> observableList = FXCollections.observableArrayList(paths);

		UpdateService service = getUpdateService(testRoot, observableList);

		Supplier<UpdateService> serviceProvider = ()->service;
		
		return new FileChooserModel(observableList, serviceProvider);
	}

	private UpdateService getUpdateService(Path testRoot, ObservableList<IndexedPath> observableList) {
		return new UpdateService() {

				private Path location = testRoot;

				@Override
				public void startUpdate() { /* nothing to do here */ }

				@Override
				public ObjectProperty<Path> searchPathProperty() { return new SimpleObjectProperty<Path>(testRoot); }

				@Override
				public ReadOnlyBooleanProperty runningProperty() { return new SimpleBooleanProperty(false); }

				@Override
				public void restartIn(Path location) {				
					if (null != location && Files.isRegularFile(location)) {
						Path parent = location.getParent();
						if (null != parent && parent.toFile().exists()) {
							this.location = parent;
							this.refresh();
						}
					} else if (null != location && location.toFile().exists()){
						this.location = location;
						this.refresh();
					}
					 
				}

				@Override
				public void refresh() { observableList.clear(); try {

						Files.list(location)
							.filter(Files::isRegularFile)
							.map(IndexedPath::valueOf)
							.forEach(observableList::add);

					} catch (IOException e) { 
						throw new RuntimeException(e);
						/* its good to see the error but there is no need to handle it here */
					}
				}

				@Override
				public ReadOnlyDoubleProperty progressProperty() { return new SimpleDoubleProperty(0.0); }

				@Override
				public void cancelUpdate() { /* nothing to do here */ }
			};
	}

}
