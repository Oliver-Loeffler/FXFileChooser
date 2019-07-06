package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Window;

public class FXFileChooserDialog extends Dialog<Path> {
    
    public static FXFileChooserDialog create(Skin skin, PathFilter ...filter) throws IOException {
    	FileChooserModel model = FileChooserModel.startingInUsersHome();
    	for (PathFilter f : filter) {
    		model.addFilter(f);
    	}
        return new FXFileChooserDialog(skin,model);
    }
    
    public static FXFileChooserDialog create(Skin skin,FileChooserModel model) throws IOException {
        return new FXFileChooserDialog(skin,model);
    }
    
    private final FileChooserModel model;
    
    // TODO: Make CSS file externally configurable
    private FXFileChooserDialog(Skin skin,FileChooserModel fileChooserModel) throws IOException {
        this.model = fileChooserModel;

        Skin.applyTo(getDialogPane(),skin);

        setTitle("File Selection");
        setHeaderText("Select File from for processing:");
        headerTextProperty().bind(model.currentSearchPath().asString());
        initModality(Modality.APPLICATION_MODAL);
        
       
        getDialogPane().setContent(FileChooserView.create(model,this, skin));
        getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        getDialogPane().minHeightProperty().set(500);
        getDialogPane().minWidthProperty().set(700);
        this.resizableProperty().set(true);
        
        
        ButtonType okay = ButtonType.OK;
        getDialogPane().getButtonTypes().addAll(okay, ButtonType.CANCEL);
        
        Node okayButton = getDialogPane().lookupButton(okay);
        okayButton.disableProperty().bind(model.invalidSelectionProperty());
        
        setResultConverter(dialogButton -> {
            if (dialogButton  == okay) {
                this.hide();
                return model.getSelectedFile();
            }
            return null;
        });
        
    }
    
    public Optional<Path> showOpenDialog(Window ownerWindow) {
        if (null == this.getOwner()) {
            this.initOwner(ownerWindow);    
        }
        return this.showAndWait();
    }
    
    
}
