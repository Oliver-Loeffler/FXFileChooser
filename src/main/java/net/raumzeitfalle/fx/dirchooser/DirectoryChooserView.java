package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import net.raumzeitfalle.fx.filechooser.Skin;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class DirectoryChooserView extends VBox {

	private final DirectoryChooserController controller;
	
	public DirectoryChooserView(Skin skin) throws IOException {

        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(this);

        controller = new DirectoryChooserController();
        loader.setController(controller);
        loader.load();
        Skin.applyTo(this,skin);
        
        
        controller.initDirTree();

    }

	public ReadOnlyObjectProperty<Path> selectedDirectoryProperty() {
		return controller.selectedDirectoryProperty();
	}

	public void onSelect(Runnable action) {
		controller.setOnSelect(action);
	}

	public void onCancel(Runnable action) {
		controller.setOnCancel(action);
		
	}
	
}
