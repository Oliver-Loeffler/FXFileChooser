package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.locations.Locations;

@ExtendWith(ApplicationExtension.class)
class FileChooserControllerSelectionAndDirchangeTest {

	protected Stage primaryStage;
	
	private FileChooserModel model;
	
	private FileChooserController controller;
	
	private TestDirChooser dirChooser;
	
	protected PathFilter[] getPathFilter() { 
		return new PathFilter[] {
				PathFilter.create("XML", p->p.getFileName().toString().toLowerCase().endsWith(".xml")),
				PathFilter.acceptAllFiles("all files")
		};
	}
	
	protected Skin getSkin() { return Skin.DARK; }
	
	@Start
	public void start(Stage stage) {
		primaryStage = stage;
		
		model = FileChooserModel.startingIn(Paths.get("./"), getPathFilter());
		model.addLocation(Locations.withName("TEST", Paths.get("TestData/SomeFiles")));
		
		dirChooser = new TestDirChooser(model.currentSearchPath());
		controller = new FileChooserController(model, dirChooser, ()->stage.close(), FileChooserViewOption.STAGE);
		
		Class<?> thisClass = getClass();
        String fileName = "FileChooserView.fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(controller);
        
        
        Parent view = null;
        try {
        	view = loader.load();
 		} catch (IOException e) {
			Label errorLabel = new Label("Could not load FileChooserView.");
			errorLabel.setTextFill(Color.WHITE);
			StackPane pane = new StackPane();
			Rectangle rect = new Rectangle();
			rect.setFill(Color.RED);
			rect.widthProperty().bind(pane.widthProperty());
			rect.heightProperty().bind(pane.heightProperty());
			pane.getChildren().add(rect);
			pane.getChildren().add(errorLabel);
			view = pane;
		}
        
        Scene scene = new Scene(view, 700, 500);
        stage.setScene(scene);
        stage.show();
        
    }
			
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_selection_is_accepted_with_okay_after_dirchange_in_textbox(FxRobot robot) {
		
		Button okay   = robot.lookup("#okButton").queryButton();
		assertTrue(okay.isDisabled());
		
		robot.clickOn("#fileNameFilter");
		robot.write(Paths.get("TestData/SomeFiles").toString());
		
		ListView<?> list = robot.lookup("#listOfFiles").queryListView();
		assertTrue(list.getItems().isEmpty());
		
		robot.clickOn("#fileNameFilter");
		robot.press(KeyCode.ENTER);
				
		assertEquals(11, list.getItems().size());
		
		robot.clickOn("#listOfFiles");
		robot.scroll(2, VerticalDirection.DOWN);
		robot.sleep(300); // must not appear like a double click
		robot.clickOn("#listOfFiles");	
		
		
		assertTrue(primaryStage.isShowing());
		
		
		robot.clickOn(okay);
		
		assertFalse(primaryStage.isShowing());
		
		Path selection = model.getSelectedFile();
		assertNotNull(selection);

	}
	
	private static class TestDirChooser implements PathSupplier {
		
		private Path directory = null;
		
		private TestDirChooser(ObjectProperty<Path> startLocation) {
			directory = startLocation.get();
		}
		
		@Override
		public void getUpdate(Consumer<Path> update) {
			update.accept(directory);
		}		
	}
}
