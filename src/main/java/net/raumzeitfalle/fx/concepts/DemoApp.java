package net.raumzeitfalle.fx.concepts;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DemoApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent view = new DemoView();
        Scene scene = new Scene(view);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
