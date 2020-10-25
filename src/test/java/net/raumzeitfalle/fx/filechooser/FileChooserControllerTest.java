package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.locations.Locations;

class FileChooserControllerTest extends FxTestTemplate {
	
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
		
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void clickOnCancelClosesWindow() {
		
		clickOn("#cancelButton", MouseButton.PRIMARY);
		
		assertFalse(primaryStage.isShowing());
	}
	
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_dialog_is_initialized_properly() {
		
		ListView<?> list = lookup("#listOfFiles").query();
		Button okay   = lookup("#okButton").query();
		Button cancel = lookup("#cancelButton").query();
		
		assertTrue(okay.isDisabled());
		assertFalse(cancel.isDisabled());
		assertFalse(list.getItems().isEmpty());
		
		assertNull(model.getSelectedFile());
		
	}
		
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_parent_of_filepath_is_used_for_dirchange_in_textbox() {
				
		clickOn("#fileNameFilter");
		write(Paths.get("TestData/SomeFiles/TestFile1.txt").toString());
		press(KeyCode.ENTER);
		
		ListView<?> list = lookup("#listOfFiles").query();
		
		assertEquals(11, list.getItems().size());

	}
	
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_list_is_updated_after_clicking_refresh(@TempDir Path directory) throws IOException {
		
		ListView<?> list = lookup("#listOfFiles").query();
		ObservableList<?> items = list.getItems();
	
		dirChooser.setDirectory(directory);
		clickOn("#chooser");
		clickOn("#refreshButton");

		sleep(200);
		assertTrue(items.isEmpty());
		
		Path source = Paths.get("TestData/SomeFiles/TestFile1.txt");
		Path target = directory.resolve(source.getFileName());
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		
		clickOn("#refreshButton");
		
		sleep(200);
		assertEquals(1, items.size());

	}
	
	@Disabled("Sort functionality")
	@Test
	void that_list_is_sorted_after_clicking_on_sortmenu() {
		
	}
		
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_selection_is_accepted_with_doubleclick() {
		
		dirChooser.setDirectory(Paths.get("TestData/SomeFiles"));
		clickOn("#chooser");
		
		ListView<?> list = lookup("#listOfFiles").query();
		
		assertEquals(11, list.getItems().size());
		
		doubleClickOn("#listOfFiles");
		
		assertFalse(primaryStage.isShowing());
			
		Path selection = model.getSelectedFile();
		assertNotNull(selection);

	}
	
	@Test
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	void that_list_content_is_reduced_by_entering_filtertext() {
		
		dirChooser.setDirectory(Paths.get("TestData/SomeFiles"));
		clickOn("#chooser");
		
		ListView<?> list = lookup("#listOfFiles").query();
		
		assertEquals(11, list.getItems().size());
		
		clickOn("#fileNameFilter");
		write("doc");
				
		assertEquals(2, list.getItems().size(), "there are only 2 files which match the filter 'doc'");
		
		write("xml");
		
		assertEquals(0, list.getItems().size(), "there is NO file which matches the filter 'docxml'");
		
		eraseText("docxml".length());
		write("xml");
		
		assertEquals(1, list.getItems().size(), "there is 1 file which matches the filter 'xml'");
		
	}
	
	@Test
	@EnabledOnOs({OS.WINDOWS})
	void that_pathfilters_from_file_type_menu_are_applied() {
		
		dirChooser.setDirectory(Paths.get("./TestData/SomeFiles"));
		clickOn("#chooser");
				
		ListView<?> list = lookup("#listOfFiles").query();
		
		MenuButton filterMenu = lookup("#fileExtensionFilter").query();
		clickOn(filterMenu);
		clickOn("XML");
		
		sleep(300);
		assertEquals(1, list.getItems().size(), "there is 1 file which matches the filter 'xml'");
		
		clickOn(filterMenu);
		clickOn("all files");
		sleep(300);
		
		assertEquals(11, list.getItems().size(), "for filter 'all files' 11 files are expected.");
	}
	
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
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
	
	protected void captureImage(Parent put, String filename) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(capture(put).getImage(), null);
        try {
			ImageIO.write(bImage, "png", new File(filename));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		
		public void setDirectory(Path dir) {
			this.directory = dir;
		}
		
	}
}
