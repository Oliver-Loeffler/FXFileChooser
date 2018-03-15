package net.raumzeitfalle.fx.filechooser;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

final class FileChooserView {
    
    static Parent create(Stage stage) throws IOException {
        FileChooserModel model = FileChooserModel.get();
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(Paths.get(""), stage.getOwner());
        
        return create(model, pathSupplier , stage);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, JFXPanel panel) throws IOException {
        return create(FileChooserController.withStage(model, pathSupplier, ()->panel.setVisible(false)));
    }
    
    static Parent create(FileChooserModel model, Dialog<Path> dialog) throws IOException {
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), dialog.getOwner());
        return create(model, pathSupplier, dialog);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, Dialog<Path> dialog) throws IOException {
        return create(FileChooserController.withDialog(model, pathSupplier, dialog));
    }
        
    static Parent create(FileChooserModel model, Stage stage) throws IOException {
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), stage.getOwner());
        return create(model, pathSupplier, stage);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, Stage stage) throws IOException {
        return create(FileChooserController.withStage(model,pathSupplier, ()->stage.hide()));
    }
    
    private static Parent create(FileChooserController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(FileChooserView.class.getResource("FileChooserView.fxml"));
        loader.setController(controller);
        Parent parent = loader.load();
        String css = FileChooserView.class.getResource("FileChooserView.css").toExternalForm();
        parent.getStylesheets().add(css);
        return parent;
    }
}
