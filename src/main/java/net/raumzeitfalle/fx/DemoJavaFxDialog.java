package net.raumzeitfalle.fx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserDialog;

public class DemoJavaFxDialog extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

    FXFileChooserDialog fc = FXFileChooserDialog.create();
    
    Button showDialog = new Button("Show Dialog");
    showDialog.setOnAction(a -> {
        try {
            Optional<Path> path = fc.showOpenDialog(primaryStage);
            System.out.println(path.map(String::valueOf).orElse("Nothing selected"));
            
        } catch (IOException e) {
            // don't mind 
        }
    });
        
        Scene scene = new Scene(showDialog);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
