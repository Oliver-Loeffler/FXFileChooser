package net.raumzeitfalle.demos.fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;

abstract class ApplicationTemplate extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        DemoController controller = new DemoController();
        loader.setController(controller);

        String appName = getClass().getSimpleName();
        URL fxmlUrl = getClass().getResource(appName+".fxml");
        loader.setLocation(fxmlUrl);

        Pane root = loader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle(appName+" Application");
        stage.show();

    }
}
