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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import net.raumzeitfalle.fx.filechooser.locations.Location;

final class FileChooserController implements Initializable {

    @FXML
    private SplitMenuButton chooser;

    @FXML
    private MenuItem usersHomeCommand;

    @FXML
    private MenuButton fileExtensionFilter;

    @FXML
    private MenuItem showAllFilesFilter;

    @FXML
    private FlowPane counterPane;

    @FXML
    private Label filteredPathsCount;

    @FXML
    private Label allPathsCount;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField fileNameFilter;

    @FXML
    private ListView<IndexedPath> listOfFiles;

    @FXML
    private TextField selectedFile;

    @FXML
    private Button refreshButton;

    @FXML
    private Button stopButton;

    @FXML
    private MenuButton sortMenu;

    @FXML
    private MenuItem buttonSortAz;

    @FXML
    private MenuItem buttonSortZa;

    @FXML
    private MenuItem buttonSortOldestFirst;

    @FXML
    private MenuItem buttonSortRecentFirst;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private VBox fileChooserView;

    private final FileChooserModel model;

    private final HideableView stage;

    private final BooleanProperty showOkayCancelButtons;

    private final PathSupplier pathSupplier;

    private final LocationMenuItemFactory menuItemFactory;

    private final FileChooserViewOption fileChooserViewOption;

    private final Dialog<Path> dialog;

    /**
     * Creates a new {@link FileChooserController} which provides all logic and
     * functionality for the {@link FXFileChooserStage}, {@link FXFileChooserDialog}
     * and {@link SwingFileChooser} components.
     *
     * @param fileChooserModel      The data model.
     * @param pathSupplier          Provides a path on demand, e.g. can be File
     *                              Chooser or Directory Chooser component. This
     *                              component is called when clicked on Choose
     *                              Directory button.
     * @param window                The parent window which shall be closable.
     * @param fileChooserViewOption The {@link FileChooserViewOption} decides if the
     *                              view will have its own OKAY/CANCEL buttons or if
     *                              OKAY/CANCEL buttons are provided e.g. by the
     *                              parent container (e.g. Dialog).
     */
    public FileChooserController(final FileChooserModel fileChooserModel, final PathSupplier pathSupplier,
            final HideableView window, FileChooserViewOption fileChooserViewOption, final Dialog<Path> dialog) {
        this.model = fileChooserModel;
        this.stage = window;
        this.fileChooserViewOption = fileChooserViewOption;
        this.showOkayCancelButtons = new SimpleBooleanProperty(
                FileChooserViewOption.STAGE.equals(fileChooserViewOption));
        this.pathSupplier = pathSupplier;
        this.menuItemFactory = new LocationMenuItemFactory(model::updateFilesIn);
        this.dialog = dialog;
    }
    
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.listOfFiles.setItems(this.model.getFilteredPaths());

        this.fileNameFilter.textProperty().addListener(l -> handleFileNameFilterChanges());

        StringBinding binding = Bindings.createStringBinding(
                () -> this.model.currentSearchPath().get().toAbsolutePath().toString(), this.model.currentSearchPath());

        this.selectedFile.promptTextProperty().bind(binding);

        /*
         * TODO: Add Key listener to accept selected file when pressing ENTER TODO: Add
         * Key listener to perform CANCEL when pressing ESC
         */
        this.listOfFiles.setOnMouseClicked(this::handleDoubleClickInFilesList);
        this.listOfFiles.setCellFactory(e -> new FilesListCell());
        this.listOfFiles.getSelectionModel().selectedItemProperty()
                .addListener(l -> model.setSelectedFile(selectedItem()));

        this.listOfFiles.setOnKeyPressed(this::handleEnterKeyOnSelection);

        this.selectedFile.textProperty().bind(model.selectedFileNameProperty());
        this.usersHomeCommand.setOnAction(e -> model.changeToUsersHome());

        this.showAllFilesFilter.setVisible(false);

        this.model.initializeFilter(fileNameFilter.getText());
        this.fileNameFilter.setOnKeyPressed(this::handleKeysForFileNameFilterField);

        // initialize PathFilter menu and permit to dynamically
        // add or remove PathFilter menu items
        this.model.getPathFilter().forEach(this::addNewPathFilterMenuItem);
        this.model.getPathFilter().addListener(this::handlePathFilterModelChange);
        this.model.getLocations().forEach(l -> chooser.getItems().add(menuItemFactory.apply(l)));
        this.model.getLocations().addListener(this::handleAddedLocation);

        this.chooser.setOnAction(e -> changeDirectory());

        refreshButton.setOnAction(e -> model.refreshFiles());
        stopButton.setOnAction(e -> model.getUpdateService().cancelUpdate());

        assignSortAction(buttonSortAz, PathComparator.byName());
        assignSortAction(buttonSortZa, PathComparator.byName().reversed());
        assignSortAction(buttonSortOldestFirst, PathComparator.byTimestamp());
        assignSortAction(buttonSortRecentFirst, PathComparator.byTimestamp().reversed());

        buttonSortRecentFirst.setVisible(true);
        buttonSortOldestFirst.setVisible(true);

        ReadOnlyBooleanProperty updateIsRunning = model.getUpdateService().runningProperty();

        /*
         * TODO: replace progress indicator by progress bar which is updated in
         * intervals only OR use indicator for small sets and bar for large data sets
         */
        progressBar.progressProperty().bind(model.getUpdateService().progressProperty());

        // counterPane.visibleProperty().bind(updateIsRunning);
        counterPane.setVisible(true);
        stopButton.visibleProperty().bind(updateIsRunning);

        // TODO: update counts after refresh
        filteredPathsCount.textProperty().bind(model.filteredPathsSizeProperty().asString());
        allPathsCount.textProperty().bind(model.allPathsSizeProperty().asString());

        okButton.setOnAction(e -> okayAction());
        okButton.setOnKeyPressed(this::handleOkayButtonKeyEvents);
        cancelButton.setOnAction(e -> cancelAction());

        okButton.disableProperty().bind(model.invalidSelectionProperty());
        okButton.visibleProperty().bind(showOkayCancelButtons);
        cancelButton.visibleProperty().bind(showOkayCancelButtons);

        StringBinding sb = Bindings.createStringBinding(() -> {
            Path current = model.currentSearchPath().get();
            if (current != null) {
                return current.normalize().toAbsolutePath().toString();
            }
            return "";
        }, model.currentSearchPath());

        Tooltip toolTip = new Tooltip();
        toolTip.textProperty().bind(sb);
        chooser.setTooltip(toolTip);
        Platform.runLater(() -> fileNameFilter.requestFocus());
    }

    private void handleKeysForFileNameFilterField(KeyEvent keyEvent) {
        if (this.fileNameFilter.getText().equalsIgnoreCase("") && KeyCode.ESCAPE.equals(keyEvent.getCode())) {
            cancelAction();
        }

        if (KeyCode.ESCAPE.equals(keyEvent.getCode())) {
            this.fileNameFilter.setText("");
        }

        if (KeyCode.ENTER.equals(keyEvent.getCode())) {
            handlePossiblePastedPath();
        }
    }

    private void handlePossiblePastedPath() {
        Path pastedPath = this.model.pastedPathProperty().get();
        Path updatedPath = pastedPath;
        if ("..".equals(String.valueOf(pastedPath))) {
            Path current = model.currentSearchPath().getValue();
            if (null != current) {
                Path parent = current.getParent();
                if (null != parent) {
                    updatedPath = parent;
                }
            }
        }

        if (null != updatedPath) {
            acceptPathAndSelectFileIfValid(updatedPath);
        } else {
            tryManualInputPathSelection();
        }
    }

    private void tryManualInputPathSelection() {
        try {
            selectParentPathFromInput();
        } catch (InvalidPathException anyError) {
            /*
             * Failed to convert text to Path but as of now this is no problem.
             */
        }
    }

    private void selectParentPathFromInput() {
        String pastedText = fileNameFilter.getText();
        if (pastedText.isEmpty())
            return;

        Path pasted = Paths.get(pastedText);
        Path parent = pasted;
        if (!Files.exists(pasted) || !Files.isDirectory(pasted)) {
            parent = pasted.getParent();
        }

        /*
         * Rebuild this to a while loop * while "parent" not exists & parent is not file
         * get parent
         * 
         * Ensure that already here is determined, if parent exists or not! Then call
         * the update service.
         * 
         */

        selectFirstExistingParentPath(pasted, parent);
    }

    private void selectFirstExistingParentPath(Path pasted, Path parent) {
        boolean parentExists = false;
        while (parent != null && !parentExists) {
            parentExists = Files.exists(parent);
            if (parentExists) {
                model.getUpdateService().restartIn(parent);
                this.fileNameFilter.setText(parent.toString());
                fileNameFilter.positionCaret(parent.toString().length() + 1);
            } else {
                parent = pasted.getParent();
            }
            /*
             * This is required on Windows as in some cases a share or drive letter is
             * listed but actually not available. In these cases File().list() will provide
             * a null value without throwing an exception.
             * 
             * Interestingly Files.notExists() blocks. But it works fine when previously
             * (parent.toFile().list()) is called.
             */
            if (parent.toFile().list() == null) {
                Logger.getLogger(FileChooserController.class.getName()).log(Level.SEVERE, "path is not accessible: {0}",
                        parent);
                break;
            }
            model.pastedPathProperty().set(parent);
        }
    }

    private void acceptPathAndSelectFileIfValid(Path pastedPath) {
        Path normalized = pastedPath.normalize()
                                    .toAbsolutePath()
                                    .normalize();
        model.getUpdateService().restartIn(normalized);
        this.fileNameFilter.setText("");
        if (Files.exists(normalized)) {
            if (Files.isRegularFile(normalized)) {
                selectEnteredFileAndRequestOkayFocus(normalized);
            }
        }
    }

    private void selectEnteredFileAndRequestOkayFocus(Path pastedPath) {
        String fileName = pastedPath.getFileName().toString();
        fileNameFilter.setText(fileName);
        fileNameFilter.positionCaret(fileName.length() + 1);
        IndexedPath ip = IndexedPath.valueOf(pastedPath);
        model.setSelectedFile(ip);
        model.pastedPathProperty().set(pastedPath.getParent());
        Platform.runLater(() -> this.okButton.requestFocus());
    }

    private void handleFileNameFilterChanges() {
        this.listOfFiles.getSelectionModel().clearSelection();
        this.model.updateFilterCriterion(fileNameFilter.getText());
        evaluateIfPathWasEntered();
    }

    private void evaluateIfPathWasEntered() {
        try {
            Path possiblePath = Paths.get(fileNameFilter.getText());
            if (null != possiblePath && possiblePath.toFile().exists()) {
                this.model.pastedPathProperty().set(possiblePath);
            } else {
                this.model.pastedPathProperty().set(null);
            }
        } catch (InvalidPathException ipe) {
            this.model.pastedPathProperty().set(null);
        }
    }

    private void handleDoubleClickInFilesList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            model.setSelectedFile(listOfFiles.getSelectionModel().getSelectedItem());
            event.consume();
            okayAction();
            if (FileChooserViewOption.DIALOG.equals(fileChooserViewOption)) {
                dialog.setResult(model.getSelectedFile());
            }
        }
    }

    private void okayAction() {
        this.stage.closeView();
    }

    private void cancelAction() {
        this.model.setSelectedFile(null);
        this.stage.closeView();
    }

    private void changeDirectory() {
        Platform.runLater(() -> {
            fileChooserView.setDisable(true);
            pathSupplier.getUpdate(value -> model.getUpdateService().restartIn(value));
            fileChooserView.setDisable(false);
        });
    }

    private void handlePathFilterModelChange(Change<? extends PathFilter> change) {
        if (change.next()) {
            change.getAddedSubList().forEach(this::addNewPathFilterMenuItem);
            change.getRemoved().forEach(this::removePathFilterMenuItem);
        }
    }

    private void handleAddedLocation(SetChangeListener.Change<? extends Location> change) {
        if (change.wasAdded()) {
            Location added = change.getElementAdded();
            chooser.getItems().add(menuItemFactory.apply(added));
        }
    }

    private void addNewPathFilterMenuItem(PathFilter p) {
        Platform.runLater(() -> {
            MenuItem item = new MenuItem(p.getName());
            item.setOnAction(e -> this.model.updateFilterCriterion(p, fileNameFilter.getText()));
            this.fileExtensionFilter.getItems().add(item);
        });
    }

    private void removePathFilterMenuItem(PathFilter filterToRemove) {
        Platform.runLater(() -> this.fileExtensionFilter.getItems()
                .removeIf(mi -> mi.getText().equalsIgnoreCase(filterToRemove.getName())));
    }

    private void assignSortAction(MenuItem menuItem, Comparator<IndexedPath> comparator) {
        menuItem.setOnAction(e -> Platform.runLater(() -> {
            model.sort(comparator);
            SVGPath svgPath = new SVGPath();
            svgPath.getStyleClass().add("tool-bar-icon");
            svgPath.setContent(((SVGPath) menuItem.getGraphic()).getContent());
            sortMenu.setGraphic(svgPath);
            sortMenu.getGraphic().getStyleClass().add("tool-bar-icon");
        }));
    }

    private IndexedPath selectedItem() {
        return listOfFiles.getSelectionModel().selectedItemProperty().getValue();
    }

    private void handleOkayButtonKeyEvents(KeyEvent keyEvent) {
        if (KeyCode.ENTER.equals(keyEvent.getCode())) {
            okButton.fire();
        }
    }

    private void handleEnterKeyOnSelection(KeyEvent keyevent) {
        if (KeyCode.ENTER.equals(keyevent.getCode()) && !listOfFiles.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> this.okButton.requestFocus());
        }
    }
    
    void stopServices() {
        this.model.getUpdateService().cancelUpdate();
    }
}
