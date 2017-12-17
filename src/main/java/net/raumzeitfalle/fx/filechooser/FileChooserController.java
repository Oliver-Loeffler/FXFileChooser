package net.raumzeitfalle.fx.filechooser;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;

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
    private ProgressIndicator progress;

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
    private Button okButton;
    
    @FXML
    private Button cancelButton;
      
    private final FileChooserModel model;
    
    private final DirectoryChooser dirChooser;
    
    private final ClosableStage stage;
    
    private final UsePattern usagePattern;
    
    public static FileChooserController withDialog(final FileChooserModel fileChooserModel, final Dialog<Path> dialog) {
        return new FileChooserController(fileChooserModel, ()->dialog.close(), UsePattern.DIALOG);       
    }
    
    public static FileChooserController withStage(final FileChooserModel fileChooserModel, final ClosableStage stage) {
        return new FileChooserController(fileChooserModel, ()->stage.hide(), UsePattern.NORMAL_STAGE);       
    }
    
    
    private FileChooserController(final FileChooserModel fileChooserModel, final ClosableStage stage, UsePattern useCase) {
       this.model = fileChooserModel;
       this.dirChooser = new DirectoryChooser();
       this.stage = stage;
       this.usagePattern = useCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.listOfFiles.setItems(this.model.getFilteredPaths()); 
        
        fileNameFilter.textProperty().addListener( l -> {
            this.listOfFiles.getSelectionModel().clearSelection();
            this.model.updateFilterCriterion(fileNameFilter.getText());
        });
        
        
        listOfFiles.getSelectionModel().selectedItemProperty().addListener(l -> {
            model.setSelectedFile(selectedItem());
         });
        
        selectedFile.textProperty().bind(model.selectedFileProperty());
        usersHomeCommand.setOnAction(e -> model.changeToUsersHome());
        showAllFilesFilter.setOnAction(e -> fileNameFilter.setText(""));
        
        chooser.setOnAction(e -> model.updateFilesIn(dirChooser.showDialog(null)));    
        
        refreshButton.setOnAction(e -> model.refreshFiles());
        stopButton.setOnAction(e -> model.getFileUpdateService().cancel());
        
        ReadOnlyBooleanProperty updateIsRunning = model.getFileUpdateService().runningProperty();
        progress.visibleProperty().bind(updateIsRunning);
        counterPane.visibleProperty().bind(updateIsRunning);
        stopButton.visibleProperty().bind(updateIsRunning);
        
      //  filteredPathsCount.textProperty().bind(model.filteredPathsSizeProperty().asString());
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

    private Path selectedItem() {
        return listOfFiles.getSelectionModel().selectedItemProperty().getValue();
    }
    
    private static enum UsePattern {
        NORMAL_STAGE, DIALOG;
    }
}
