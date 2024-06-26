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

import static org.junit.jupiter.api.Assertions.assertAll;
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

import org.junit.jupiter.api.AfterEach;
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
import javafx.scene.control.TextInputControl;
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
        return new PathFilter[] {PathFilter.create("XML", p -> p.toString().toLowerCase().endsWith(".xml")),
                PathFilter.acceptAllFiles("all files")};
    }

    protected Skin getSkin() {
        return Skin.DARK;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        model = FileChooserModel.startingIn(Paths.get("./"), getPathFilter());
        model.addLocation(Locations.withName("TEST", Paths.get("TestData/SomeFiles")));

        dirChooser = new TestDirChooser(model.currentSearchPath());
        controller = new FileChooserController(model, dirChooser, () -> stage.close(), FileChooserViewOption.STAGE, null);

        Class<?> thisClass = getClass();
        String fileName = "FileChooser.fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(controller);

        Parent view = null;
        try {
            view = loader.load();
        } catch (IOException e) {
            Label errorLabel = new Label("Could not load FileChooser.");
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
        sleep(30);
    }

    @AfterEach
    void closeStage() throws Exception {
        Invoke.andWait(() -> primaryStage.close());
        sleep(30);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void clickOnCancelClosesWindow() {
        clickOn("#cancelButton", MouseButton.PRIMARY);

        assertFalse(primaryStage.isShowing());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_dialog_is_initialized_properly() {
        ListView<?> list = lookup("#listOfFiles").query();
        Button okay = lookup("#okButton").query();
        Button cancel = lookup("#cancelButton").query();

        sleep(400);

        assertTrue(okay.isDisabled());
        assertFalse(cancel.isDisabled());
        assertFalse(list.getItems().isEmpty());

        assertNull(model.getSelectedFile());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_parent_of_filepath_is_used_for_dirchange_in_textbox_while_filename_is_used_for_filter() {
        clickOn("#fileNameFilter");
        write("TestData/SomeFiles/TestFile1.txt");
        hitKey(KeyCode.ENTER);
        ListView<?> list = lookup("#listOfFiles").query();

        Path expectedDirectory = Paths.get("TestData/SomeFiles/").toAbsolutePath();
        Path currentDirectory = model.currentSearchPath().get();
        TextInputControl text = lookup("#fileNameFilter").queryTextInputControl();

        assertAll(
            () -> assertEquals(1, list.getItems().size(), "There should only be one file listed"),
            () -> assertEquals("TestFile1.txt", text.getText(), "Filename filter text"),
            () -> assertEquals("TestFile1.txt", list.getItems().get(0).toString(), "Expected File"),
            () -> assertEquals(expectedDirectory, currentDirectory, "New working directory")
        );
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_parent_of_filepath_is_used_for_dirchange_in_textbox() {
        clickOn("#fileNameFilter");
        write("TestData/SomeFiles/");
        hitKey(KeyCode.ENTER);

        ListView<?> list = lookup("#listOfFiles").query();

        Path expectedDirectory = Paths.get("TestData/SomeFiles/").toAbsolutePath();
        Path currentDirectory = model.currentSearchPath().get();
        // there is 1 subdir
        int trueFileCountd = expectedDirectory.toFile().list().length - 1;

        assertAll(
            () -> assertEquals(expectedDirectory, currentDirectory, "New working directory"),
            () -> assertEquals(11, list.getItems().size(), "Expeted size"),
            () -> assertEquals(trueFileCountd, list.getItems().size(), "Actual directory size")
        );
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_list_is_updated_after_clicking_refresh(@TempDir Path directory) throws IOException {
        ListView<?> list = lookup("#listOfFiles").query();
        ObservableList<?> items = list.getItems();

        dirChooser.setDirectory(directory);
        clickOn("#chooser");
        clickOn("#refreshButton");
        sleep(200);

        assertTrue(items.isEmpty());

        Path source = Paths.get("TestData/SomeFiles/TestFile1.txt");
        Path target = directory.resolve("CopyOfTestFile1.txt");
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        clickOn("#refreshButton");
        model.getUpdateService().waitUntilFinished();

        Path expectedDirectory = directory.toAbsolutePath();
        Path currentDirectory = model.currentSearchPath().get().toAbsolutePath();

        assertAll(
            () -> assertEquals(1, items.size()),
            () -> assertEquals(expectedDirectory, currentDirectory));
    }

    /*
     * TODO: Implement test for sorting
     */
    @Disabled("Sort functionality")
    @Test
    void that_list_is_sorted_after_clicking_on_sortmenu() {

    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_one_can_navigate_the_tree_upwards() {
        dirChooser.setDirectory(Paths.get("TestData/SomeFiles"));
        clickOn("#chooser");
        sleep(200);

        assertNotNull(model.currentSearchPath().get());
        assertEquals(Paths.get("TestData/SomeFiles"), model.currentSearchPath().get());

        clickOn("#fileNameFilter");
        write("..");
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();

        assertNotNull(model.currentSearchPath().get());
        assertEquals(Paths.get("TestData").toAbsolutePath(), model.currentSearchPath().get());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_directory_can_be_changed_by_entering_a_valid_path() {
        dirChooser.setDirectory(Paths.get(".").toAbsolutePath());
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();
        assertEquals(Paths.get(".").toAbsolutePath(), model.currentSearchPath().get());

        clickOn("#fileNameFilter");
        write("TestData/SomeFiles/");
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();

        assertNotNull(model.currentSearchPath().get());
        assertEquals(Paths.get("TestData/SomeFiles").toAbsolutePath(), model.currentSearchPath().get());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void that_directory_can_be_changed_when_entered_path_has_existing_parent_windows() {
        dirChooser.setDirectory(Paths.get(".").toAbsolutePath());
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();
        assertEquals(Paths.get(".").toAbsolutePath(), model.currentSearchPath().get());

        clickOn("#fileNameFilter");
        write("C:\\ThisFolderShouldNotExist");
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();

        assertNotNull(model.currentSearchPath().get());
        assertEquals(Paths.get("C:\\").toAbsolutePath(), model.currentSearchPath().get());
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void that_directory_can_be_changed_when_entered_path_has_existing_parent_other() {
        dirChooser.setDirectory(Paths.get(".").toAbsolutePath());
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();
        assertEquals(Paths.get(".").toAbsolutePath(), model.currentSearchPath().get());

        clickOn("#fileNameFilter");
        write("/etc/someNotExistingDir/");
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();

        assertNotNull(model.currentSearchPath().get());
        assertEquals(Paths.get("/etc/").toAbsolutePath(), model.currentSearchPath().get());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_directory_and_selection_do_not_change_when_empty_text_is_entered() {
        var start = Paths.get("TestData/SomeFiles").toAbsolutePath();
        dirChooser.setDirectory(start);

        clickOn("#chooser");
        sleep(200);
        clickOn("#fileNameFilter");
        write("TestData/SomeFiles/TestFile1.txt", 1);
        press(KeyCode.ENTER);
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();
        
        Path expected = start.toAbsolutePath();
        Path currentDir = model.currentSearchPath().get();
        assertEquals(expected, currentDir);

        clickOn("#fileNameFilter");
        press(KeyCode.COMMAND, KeyCode.A);
        release(KeyCode.COMMAND, KeyCode.A);
        press(KeyCode.BACK_SPACE);
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();
        
        Path selection = model.getSelectedFile().toAbsolutePath();
        Path expectedSelection = start.resolve("TestFile1.txt").toAbsolutePath();
        assertEquals(expectedSelection, selection);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_file_can_selected_by_entering_a_valid_path() {
        dirChooser.setDirectory(Paths.get(".").toAbsolutePath());
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();
        assertEquals(Paths.get(".").toAbsolutePath(), model.currentSearchPath().get());

        clickOn("#fileNameFilter");
        write("TestData/SomeFiles/TestFile1.txt");
        hitKey(KeyCode.ENTER);

        model.getUpdateService().waitUntilFinished();

        Path selection = model.getSelectedFile();
        assertEquals(Paths.get("TestData/SomeFiles/TestFile1.txt").toAbsolutePath(), selection);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_selection_is_accepted_with_doubleclick() {
        dirChooser.setDirectory(Paths.get("TestData/SomeFiles"));
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();

        ListView<?> list = lookup("#listOfFiles").query();
        assertEquals(11, list.getItems().size());

        doubleClickOn("#listOfFiles");
        model.getUpdateService().waitUntilFinished();;

        assertFalse(primaryStage.isShowing());

        Path selection = model.getSelectedFile();
        assertNotNull(selection);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_list_content_is_reduced_by_entering_filtertext() {
        dirChooser.setDirectory(Paths.get("TestData/SomeFiles"));
        clickOn("#chooser");
        model.getUpdateService().waitUntilFinished();

        ListView<?> list = lookup("#listOfFiles").query();
        assertEquals(11, list.getItems().size());

        clickOn("#fileNameFilter");
        write("doc");
        model.getUpdateService().waitUntilFinished();
        
        assertEquals(2, list.getItems().size(), "there are only 2 files which match the filter 'doc'");

        write("xml");
        model.getUpdateService().waitUntilFinished();

        assertEquals(0, list.getItems().size(), "there is NO file which matches the filter 'docxml'");

        eraseText("docxml".length());
        write("xml");
        model.getUpdateService().waitUntilFinished();
        assertEquals(1, list.getItems().size(), "there is 1 file which matches the filter 'xml'");
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    void that_pathfilter_for_XML_from_menu_is_applied() {
        sleep(200);
        write("./TestData/SomeFiles/");
        hitKey(KeyCode.ENTER);
        MenuButton filterMenu = lookup("#fileExtensionFilter").query();
        clickOn(filterMenu);
        clickOn("XML");
        model.getUpdateService().waitUntilFinished();
        ListView<?> list = lookup("#listOfFiles").query();
        assertEquals(1, list.getItems().size(), "there is only 1 file which matches the filter 'xml'");
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX, OS.MAC})
    void that_selection_is_accepted_with_okay_after_dirchange_in_textbox() {
        Button okay = lookup("#okButton").query();
        assertTrue(okay.isDisabled());

        clickOn("#fileNameFilter");
        write("./TestData/SomeFiles/");
        model.getUpdateService().waitUntilFinished();

        ListView<?> list = lookup("#listOfFiles").query();
        assertTrue(list.getItems().isEmpty());

        clickOn("#fileNameFilter");
        hitKey(KeyCode.ENTER);
        model.getUpdateService().waitUntilFinished();
        
        assertEquals(11, list.getItems().size());

        clickOn("#listOfFiles");
        scroll(2, VerticalDirection.DOWN);
        sleep(400); // must not appear like a double click
        clickOn("#listOfFiles");

        assertTrue(primaryStage.isShowing());

        clickOn(okay);
        sleep(200);

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

    private static class TestDirChooser implements PathUpdateHandler {

        private Path directory = null;

        private TestDirChooser(ObjectProperty<Path> startLocation) {
            directory = startLocation.get();
        }

        public void getUpdate(Consumer<Path> update) {
            update.accept(directory);
        }

        public void setDirectory(Path dir) {
            this.directory = dir;
        }
    }
}
