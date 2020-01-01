package net.raumzeitfalle.fx.concepts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class DemoView extends AnchorPane {

    public DemoView() throws IOException {

        /*
         * TODO: #1
         *  Load FXML by convention, FXML should declare all GUI elements inside <fx:root/>.
         *
         */
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);

        /*
         * TODO: #2 identify and load controller by convention
         *
         * loader.setController(...);
         * loader.setControllerFactory(...);
         */

        loader.setRoot(this);

        /*
         * TODO: #3 decide if and how necessary models can be loaded by convention, if needed.
         *           in some cases the controller may require a separate model
         */

        Parent view = loader.load();

        /*
         * TODO: #4 load CSS sheets also by convention
         *  - define a convention (incl. naming convention for e.g. skin names)
         *  - define a loading strategy
         *
         *  getStylesheets().add(url.toExternalForm());
         *  applyCss();
         *
         */
    }
}
