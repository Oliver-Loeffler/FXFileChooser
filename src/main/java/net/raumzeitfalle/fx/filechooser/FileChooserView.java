package net.raumzeitfalle.fx.filechooser;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

final class FileChooserView extends VBox {

    public FileChooserView(PathSupplier pathSupplier, final HideableWindow window, FileChooserModel model, FileChooserViewOption fileChooserViewOption) throws IOException {

        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(this);

        FileChooserController controller = FileChooserController.withStage(model, pathSupplier, window, fileChooserViewOption);
        loader.setController(controller);
        Parent view = loader.load();

    }

}
