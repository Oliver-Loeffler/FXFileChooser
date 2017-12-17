package net.raumzeitfalle.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooser;
import net.raumzeitfalle.fx.filechooser.PathFilter;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        PathFilter xmlOnly = PathFilter.create(".xml", p->p.getFileName().endsWith(".xml"));
        FXFileChooser fc = FXFileChooser.create(xmlOnly);
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
