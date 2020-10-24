package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;

class FilesListCellTest extends ApplicationTest {

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
		assertEquals("2019-08-31  -  09:18:52", ((Labeled) expectedNodes.get("fileListCell-fileDate")).getText());
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
