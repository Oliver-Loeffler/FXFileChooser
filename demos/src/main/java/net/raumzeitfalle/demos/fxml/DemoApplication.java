package net.raumzeitfalle.demos.fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class DemoApplication extends Application {

    public static void main(String[] args) {
        launch(DemoApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        DemoController controller = new DemoController();
        loader.setController(controller);

        URL fxmlUrl = getClass().getResource("/FXMLView.fxml");
        loader.setLocation(fxmlUrl);

        Pane root = loader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Demo Application");
        stage.show();

        ImageView view = null;
    }
}
