package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.net.URL;

import net.raumzeitfalle.fx.filechooser.Skin;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DirectoryChooserView extends VBox {

	public DirectoryChooserView(Stage parent, DirectoryChooserModel model, Skin skin) throws IOException {

        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(this);

        DirectoryChooserController controller = new DirectoryChooserController(model);
        loader.setController(controller);
        loader.load();
        Skin.applyTo(this,skin);

    }
	
}
