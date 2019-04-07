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
	
	private FileChooserView() {
		throw new UnsupportedOperationException(
				"This is a utility class and not intended  for instantiation.");
	}
    
    static Parent create(Stage stage, Skin skin) throws IOException {
        FileChooserModel model = FileChooserModel.get();
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(Paths.get(""), stage.getOwner());
        
        return create(model, pathSupplier , stage, skin);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, JFXPanel panel, Skin skin) throws IOException {
        return create(FileChooserController.withStage(model, pathSupplier, ()->panel.setVisible(false)),skin);
    }
    
    static Parent create(FileChooserModel model, Dialog<Path> dialog, Skin skin) throws IOException {
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), dialog.getOwner());
        return create(model, pathSupplier, dialog, skin);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, Dialog<Path> dialog, Skin skin) throws IOException {
        return create(FileChooserController.withDialog(model, pathSupplier, dialog),skin);
    }
        
    static Parent create(FileChooserModel model, Stage stage, Skin skin) throws IOException {
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), stage.getOwner());
        return create(model, pathSupplier, stage, skin);
    }
    
    static Parent create(FileChooserModel model, PathSupplier pathSupplier, Stage stage, Skin skin) throws IOException {
        return create(FileChooserController.withStage(model,pathSupplier, stage::hide),skin);
    }
    
    private static Parent create(FileChooserController controller, Skin skin) throws IOException {
        FXMLLoader loader = new FXMLLoader(FileChooserView.class.getResource("FileChooserView.fxml"));
        loader.setController(controller);
        Parent parent = loader.load();

        Skin.applyTo(parent,skin);

        return parent;
    }
}
