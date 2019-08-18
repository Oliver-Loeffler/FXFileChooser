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
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import net.raumzeitfalle.fx.util.Location;

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
    private VBox fileChooserForm;
      
    private final FileChooserModel model;
    
    private final HideableWindow stage;
    
    private final BooleanProperty showOkayCancelButtons;
    
    private final PathSupplier pathSupplier;
        
    public static FileChooserController withDialog(
    	
    		final FileChooserModel fileChooserModel, 
    		final PathSupplier pathSupplier, 
    		final Dialog<Path> dialogWindow) {
    	
        return new FileChooserController(fileChooserModel, pathSupplier, dialogWindow::close, OkayCancelButtons.HIDE);       
    }
    
    public static FileChooserController withStage(
    		
    		final FileChooserModel fileChooserModel, 
    		final PathSupplier pathSupplier, 
    		final HideableWindow window) {
    	
        return new FileChooserController(fileChooserModel, pathSupplier, window::hide, OkayCancelButtons.SHOW);       
    }
    
    
    private FileChooserController(final FileChooserModel fileChooserModel, final PathSupplier pathSupplier, final HideableWindow window, OkayCancelButtons useCase) {
       this.model = fileChooserModel;
       this.stage = window;
       this.showOkayCancelButtons = new SimpleBooleanProperty(OkayCancelButtons.SHOW.equals(useCase));
       this.pathSupplier = pathSupplier;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    	this.listOfFiles.setItems(this.model.getFilteredPaths()); 
        
        fileNameFilter.textProperty().addListener( l -> handleFileNameFilterChanges());
        
        listOfFiles.setOnMouseClicked(this::handleDoubleClickInFilesList);

        listOfFiles.setCellFactory(c -> new FilesListCell());
        listOfFiles.getSelectionModel()
        	.selectedItemProperty()
        	.addListener(l -> model.setSelectedFile(selectedItem()));
        
        selectedFile.textProperty().bind(model.selectedFileProperty());
        usersHomeCommand.setOnAction(e -> model.changeToUsersHome());
        showAllFilesFilter.setVisible(false);
        
        this.model.initializeFilter(fileNameFilter.getText());
        
        // initialize PathFilter menu
		this.model.getPathFilter()
		          .forEach(this::addNewPathFilterMenuItem);
        
        // permit to dynamically add or remove PathFilter menu items
        this.model.getPathFilter()
        	      .addListener(this::handlePathFilterModelChange);
        
        chooser.setOnAction(e -> changeDirectory());
        
        // FIXME: Rework and redesign the update cycle for Locations
        model.locationsProperty().addListener(new SetChangeListener<Location>() {

			@Override
			public void onChanged(Change<? extends Location> change) {
				updateLocationsMenu();
			}
        	
        });
        
        refreshButton.setOnAction(e -> model.refreshFiles());
        stopButton.setOnAction(e -> model.getUpdateService().cancelUpdate());
 
        assignSortAction(buttonSortAz, PathComparator.ascendingByName());
        assignSortAction(buttonSortZa, PathComparator.descendingByName());
        assignSortAction(buttonSortOldestFirst, PathComparator.ascendingLastModified());
        assignSortAction(buttonSortRecentFirst, PathComparator.descendingByName());
                
        buttonSortRecentFirst.setVisible(true);
        buttonSortOldestFirst.setVisible(true);
        
        ReadOnlyBooleanProperty updateIsRunning = model.getUpdateService().runningProperty();
       
        /*
         *  TODO: replace progress indicator by progress bar which is updated in intervals only
         *  OR use indicator for small sets and bar for large data sets 
         */
        progressBar.progressProperty().bind(model.getUpdateService().progressProperty());
        
        //counterPane.visibleProperty().bind(updateIsRunning);
        counterPane.setVisible(true);
        stopButton.visibleProperty().bind(updateIsRunning);
        
        // TODO: update counts after refresh
        filteredPathsCount.textProperty().bind(model.filteredPathsSizeProperty().asString());
        allPathsCount.textProperty().bind(model.allPathsSizeProperty().asString());
                
        okButton.setOnAction(e -> okayAction());
        cancelButton.setOnAction(e -> cancelAction());
        
        okButton.disableProperty().bind(model.invalidSelectionProperty());
        okButton.visibleProperty().bind(showOkayCancelButtons);
        cancelButton.visibleProperty().bind(showOkayCancelButtons);
    }

	private void updateLocationsMenu() {
		ObservableList<MenuItem> locationMenu = this.chooser.getItems();
		
		for (Location l : this.model.locationsProperty()) {
			MenuItem locationItem = new MenuItem(l.getName());
			locationItem.setOnAction(e->this.model.updateFilesIn(l.getPath()));
			
			// TODO: Update model, so that the model holds the menu items
			if (!locationMenu.contains(locationItem)) {
				this.chooser.getItems().add(locationItem);
			}
		}
		
	}

	private void handleFileNameFilterChanges() {
		this.listOfFiles.getSelectionModel().clearSelection();
		this.model.updateFilterCriterion(fileNameFilter.getText());
	}

	private void handleDoubleClickInFilesList(MouseEvent event) {
		if (event.getClickCount() == 2) {
		    model.setSelectedFile(listOfFiles.getSelectionModel().getSelectedItem());
		    event.consume();
		    okayAction();
		}
	}
    
    private void okayAction() {
    	this.stage.hide();
    }
    
    private void cancelAction() {
    	this.model.setSelectedFile(null);
        this.stage.hide();
    }
    
    private void changeDirectory() {
    	Invoke.later(()->{
    		fileChooserForm.setDisable(true);
            pathSupplier.getUpdate(model::updateFilesIn);
            fileChooserForm.setDisable(false);
    	});
    }
    
    private void handlePathFilterModelChange(Change<? extends PathFilter> change) {
    	if (change.next()) {
    		change.getAddedSubList().forEach(this::addNewPathFilterMenuItem);
    		change.getRemoved().forEach(this::removePathFilterMenuItem);
    	}
    }

	private void addNewPathFilterMenuItem(PathFilter p) {
		Invoke.later(()->{
			MenuItem item = new MenuItem(p.getName());
			item.setOnAction(e -> this.model.updateFilterCriterion(p, fileNameFilter.getText()));
			this.fileExtensionFilter.getItems().add(item);
		});
	}
	
	private void removePathFilterMenuItem(PathFilter filterToRemove) {
		Invoke.later(()->this.fileExtensionFilter
			.getItems()
			.removeIf(mi->mi.getText().equalsIgnoreCase(filterToRemove.getName())));
	}

    private void assignSortAction(MenuItem menuItem, Comparator<IndexedPath> comparator) {
        menuItem.setOnAction(e -> 
            Invoke.later(()->{
                model.sort(comparator);
                SVGPath svgPath = new SVGPath();
                svgPath.getStyleClass().add("tool-bar-icon");
                svgPath.setContent(((SVGPath)menuItem.getGraphic()).getContent());
                sortMenu.setGraphic(svgPath);
                sortMenu.getGraphic().getStyleClass().add("tool-bar-icon");
            }));
    }

    private IndexedPath selectedItem() {
        return listOfFiles.getSelectionModel().selectedItemProperty().getValue();
    }
    
    private enum OkayCancelButtons {
        SHOW, 
        HIDE;
    }
}
