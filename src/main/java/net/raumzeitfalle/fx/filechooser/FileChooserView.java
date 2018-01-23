package net.raumzeitfalle.fx.filechooser;


import java.io.IOException;
import java.nio.file.Path;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

final class FileChooserView {
    
    static Parent create(Stage stage) throws IOException {
        FileChooserModel model = new FileChooserModel();
        return create(model, stage);
    }
    
    static Parent create(FileChooserModel model, JFXPanel panel) throws IOException {
        return create(FileChooserController.withStage(model, ()->panel.setVisible(false)));
    }
    
    static Parent create(FileChooserModel model, Dialog<Path> dialog) throws IOException {
        return create(FileChooserController.withDialog(model, dialog));
    }
    
    static Parent create(FileChooserModel model, Stage stage) throws IOException {
        return create(FileChooserController.withStage(model, ()->stage.hide()));
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
