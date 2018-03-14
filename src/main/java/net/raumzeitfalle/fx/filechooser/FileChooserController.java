package net.raumzeitfalle.fx.filechooser;

import java.net.URL;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

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
    private ListView<Path> listOfFiles;

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
    
    private final UsePattern usagePattern;
    
    private final PathSupplier pathSupplier;
        
    public static FileChooserController withDialog(final FileChooserModel fileChooserModel, final PathSupplier pathSupplier, final Dialog<Path> dialogWindow) {
        return new FileChooserController(fileChooserModel, pathSupplier, ()->dialogWindow.close(), UsePattern.DIALOG);       
    }
    
    public static FileChooserController withStage(final FileChooserModel fileChooserModel, final PathSupplier pathSupplier, final HideableWindow window) {
        return new FileChooserController(fileChooserModel, pathSupplier, ()->window.hide(), UsePattern.NORMAL_STAGE);       
    }
    
    
    private FileChooserController(final FileChooserModel fileChooserModel, final PathSupplier pathSupplier, final HideableWindow window, UsePattern useCase) {
       this.model = fileChooserModel;
       this.stage = window;
       this.usagePattern = useCase;
       this.pathSupplier = pathSupplier;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.listOfFiles.setItems(this.model.getFilteredPaths());  // .getSortedPaths()  => consider sorting in a table model 
        
        fileNameFilter.textProperty().addListener( l -> {
            this.listOfFiles.getSelectionModel().clearSelection();
            this.model.updateFilterCriterion(fileNameFilter.getText());
        });
        
        listOfFiles.setOnMouseClicked(event->{
            if (event.getClickCount() == 2) {
                model.setSelectedFile(listOfFiles.getSelectionModel().getSelectedItem());
                event.consume();
                this.stage.hide();
            }
        });

        listOfFiles.setCellFactory(c -> new FilesListCell());
        listOfFiles.getSelectionModel().selectedItemProperty().addListener(l -> {
            model.setSelectedFile(selectedItem());
         });
        
        selectedFile.textProperty().bind(model.selectedFileProperty());
        usersHomeCommand.setOnAction(e -> model.changeToUsersHome());
        showAllFilesFilter.setVisible(false);
        
        this.model.initializeFilter(fileNameFilter.getText());
        this.model.getPathFilter().forEach(p -> {
        		MenuItem item = new MenuItem(p.getName());
        		this.fileExtensionFilter.getItems().add(item);
        		item.setOnAction(e->{
        			this.model.updateFilterCriterion(p,fileNameFilter.getText());
        		});
        });
        
        chooser.setOnAction(e -> {
            Platform.runLater(()->{
                fileChooserForm.setDisable(true);
                pathSupplier.getUpdate(model::updateFilesIn);
                fileChooserForm.setDisable(false);
            });
        });    
        
        refreshButton.setOnAction(e -> model.refreshFiles());
        stopButton.setOnAction(e -> model.getFileUpdateService().cancel());
 
        assignSortAction(buttonSortAz, PathComparator.ascendingByName());
        assignSortAction(buttonSortZa, PathComparator.descendingByName());
        assignSortAction(buttonSortOldestFirst, PathComparator.ascendingLastModified());
        assignSortAction(buttonSortRecentFirst, PathComparator.descendingByName());
                
        buttonSortRecentFirst.setVisible(true);
        buttonSortOldestFirst.setVisible(true);
        
        ReadOnlyBooleanProperty updateIsRunning = model.getFileUpdateService().runningProperty();
       
        /*
         *  TODO: replace progress indicator by progress bar which is updated in intervals only
         *  OR use indicator for small sets and bar for large data sets 
         */
        progressBar.progressProperty().bind(model.getFileUpdateService().progressProperty());
        
        //counterPane.visibleProperty().bind(updateIsRunning);
        counterPane.setVisible(true);
        stopButton.visibleProperty().bind(updateIsRunning);
        
        // TODO: update counts after refresh
        filteredPathsCount.textProperty().bind(model.filteredPathsSizeProperty().asString());
        allPathsCount.textProperty().bind(model.allPathsSizeProperty().asString());
                
        okButton.setOnAction(e -> {
            this.stage.hide();
        });
        
        cancelButton.setOnAction(e -> {
            this.model.setSelectedFile(null);
            this.stage.hide();
        });
        
        switch (this.usagePattern) {
            case DIALOG :{ 
                okButton.visibleProperty().setValue(false);
                cancelButton.visibleProperty().setValue(false);
                break;             
            }
            
            case NORMAL_STAGE : {
                okButton.disableProperty().bind(model.invalidSelectionProperty());
                okButton.visibleProperty().setValue(true);
                cancelButton.visibleProperty().setValue(true);
                break;
            }
            default : {
                throw new UnsupportedOperationException("Unknown use case.");
            }
        }
    }

    private void assignSortAction(MenuItem menuItem, Comparator<Path> comparator) {
        menuItem.setOnAction(e -> {
            Invoke.later(()->{
                model.sort(comparator);
                
                SVGPath svgPath = new SVGPath();
                svgPath.getStyleClass().add("tool-bar-icon");
                svgPath.setContent(((SVGPath)menuItem.getGraphic()).getContent());
                sortMenu.setGraphic(svgPath);
                sortMenu.getGraphic().getStyleClass().add("tool-bar-icon");
            });
            
        });
    }

    private Path selectedItem() {
        return listOfFiles.getSelectionModel().selectedItemProperty().getValue();
    }
    
    private static enum UsePattern {
        NORMAL_STAGE, DIALOG;
    }
}
