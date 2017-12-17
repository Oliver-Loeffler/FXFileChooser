package net.raumzeitfalle.fx.filechooser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        FXFileChooser fc = FXFileChooser.create();
        Button button = new Button("Show Dialog");
        button.setOnAction(e -> System.out.println(fc.getSelectedPath().map(String::valueOf).orElse("Nothing selected")));
        Scene mainScene = new Scene(button);
        arg0.setScene(mainScene);
        arg0.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
