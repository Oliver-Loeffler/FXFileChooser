package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DemoInDialog extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Button showDialog = new Button("Show Dialog");
        Scene scene = new Scene(showDialog);
        
        FXFileChooserDialog fc = FXFileChooserDialog.create();
        
        showDialog.setOnAction(a -> {
            try {
                System.out.println(fc.showOpenDialog(primaryStage).map(String::valueOf).orElse("Nothing selected"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
