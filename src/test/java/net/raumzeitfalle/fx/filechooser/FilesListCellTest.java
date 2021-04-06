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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;

class FilesListCellTest extends FxTestTemplate {

	private FilesListCell controlUnderTest; 
	
	@Test
	void creationWithoutUpdate() {
		
		controlUnderTest = new FilesListCell();
		
		assertNotNull(controlUnderTest);
		assertNull(controlUnderTest.getText());
		assertNull(controlUnderTest.getGraphic());
		assertTrue(controlUnderTest.isVisible());
		
	}
	
	@Test
	void updateWithValidItem() {
		
		IndexedPath path = IndexedPath.valueOf(Paths.get("TestData/SomeFiles/HorrbibleSpreadSheet.xls"));
		
		controlUnderTest = new FilesListCell();
		controlUnderTest.updateItem(path, false);
		
		assertNull(controlUnderTest.getText());
		assertTrue(controlUnderTest.isVisible());
		
		Pane subView = (Pane) controlUnderTest.getGraphic();
		assertNotNull(subView);
		
		Map<String,Node> expectedNodes = new HashMap<>();
		for (Node n : subView.getChildrenUnmodifiable()) {
			if (n.getId().startsWith("fileListCell-")) {
				expectedNodes.put(n.getId(), n);
			}
		}

		assertNotNull(expectedNodes.get("fileListCell-fileTypeIcon"));
		
		assertNotNull(expectedNodes.get("fileListCell-fileName"));
		assertTrue(expectedNodes.get("fileListCell-fileName") instanceof Labeled);
		assertEquals("HorrbibleSpreadSheet.xls", ((Labeled) expectedNodes.get("fileListCell-fileName")).getText());
		
		assertNotNull(expectedNodes.get("fileListCell-fileDate"));
		assertTrue(expectedNodes.get("fileListCell-fileDate") instanceof Labeled);
		
		String expectedPattern = "\\d{4}-\\d{2}-\\d{2}  -  \\d{2}:\\d{2}:\\d{2}";
		String labelText = ((Labeled) expectedNodes.get("fileListCell-fileDate")).getText();
		
		assertTrue(labelText.matches(expectedPattern));
	}
	
	@Test
	void updateWithNullItem() {
		
		controlUnderTest = new FilesListCell();
		controlUnderTest.updateItem(null, false);
		
		assertNotNull(controlUnderTest);
		assertNull(controlUnderTest.getText());
		assertNull(controlUnderTest.getGraphic());
		assertTrue(controlUnderTest.isVisible());
		
		
	}

}
