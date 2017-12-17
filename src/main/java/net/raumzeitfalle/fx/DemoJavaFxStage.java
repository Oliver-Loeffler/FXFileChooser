package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserImpl;
import net.raumzeitfalle.fx.filechooser.PathFilter;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        PathFilter xmlOnly = PathFilter.create(".xml", p->p.getFileName().endsWith(".xml"));
        FXFileChooserImpl fc = FXFileChooserImpl.create(xmlOnly);
        
        Button button = new Button("Show customized Stage: FXFileChooserImpl.class");
        button.setOnAction(e -> {
            Optional<Path> selection = fc.showOpenDialog(primaryStage);
            selection.map(String::valueOf).ifPresent(System.out::println);
            System.out.println("Result is present: " + selection.isPresent());
        });
        
        Scene mainScene = new Scene(button);
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(400);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
