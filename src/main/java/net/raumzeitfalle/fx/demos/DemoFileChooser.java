package net.raumzeitfalle.fx.demos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.DirectoryChooserOption;
import net.raumzeitfalle.fx.filechooser.FileChooser;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoFileChooser extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    private FileChooser fileChooser; 
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        fileChooser = new FileChooser(Skin.DARK, DirectoryChooserOption.CUSTOM);
        Scene scene = new Scene(fileChooser);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    @Override
    public void stop() {
        fileChooser.shutdown();
    }
}