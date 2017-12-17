package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserImpl;
import net.raumzeitfalle.fx.filechooser.PathFilter;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        
        PathFilter xmlOnly = PathFilter.create(".xml", p->p.getFileName().endsWith(".xml"));
        FXFileChooserImpl fc = FXFileChooserImpl.create(); //Paths.get("E:\\Test")
        
        Button button = new Button("Show Dialog");
        button.setOnAction(e -> {
            Optional<Path> selection = fc.showOpenDialog(arg0);
            selection.map(String::valueOf).ifPresent(System.out::println);
            System.out.println("Result is present: " + selection.isPresent());
        });
        
        Scene mainScene = new Scene(button);
        arg0.setScene(mainScene);
        arg0.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
