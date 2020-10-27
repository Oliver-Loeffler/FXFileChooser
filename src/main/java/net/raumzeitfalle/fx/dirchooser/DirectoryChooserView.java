package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DirectoryChooserView extends AnchorPane {

	private final DirectoryChooserController controller;
	
	public DirectoryChooserView(Skin skin) throws IOException {

        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        
        controller = new DirectoryChooserController();
        loader.setController(controller);
        Parent view = loader.load();
        this.getChildren().add(view);
        
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        
        Skin.applyTo(this,skin);
        
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
