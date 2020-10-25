/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2020 Oliver Loeffler, Raumzeitfalle.net
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
import org.testfx.framework.junit5.ApplicationTest;

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

class FileChooserControllerSelectionAndDirchangeTest extends ApplicationTest {

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
	
	@Override
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

	// TODO: Figure out, why it runs differntly in Linux.
	@Test
	@EnabledOnOs({OS.WINDOWS})
	void that_selection_is_accepted_with_okay_after_dirchange_in_textbox() {
		
		Button okay   = lookup("#okButton").query();
		assertTrue(okay.isDisabled());
		
		clickOn("#fileNameFilter");
		write("./TestData/SomeFiles/");
		
		ListView<?> list = lookup("#listOfFiles").query();
				
		assertTrue(list.getItems().isEmpty());
		
		clickOn("#fileNameFilter");
		press(KeyCode.ENTER);
				
		assertEquals(11, list.getItems().size());
		
		clickOn("#listOfFiles");
		scroll(2, VerticalDirection.DOWN);
		sleep(300); // must not appear like a double click
		clickOn("#listOfFiles");	
		
		
		assertTrue(primaryStage.isShowing());
		
		
		clickOn(okay);
		
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
