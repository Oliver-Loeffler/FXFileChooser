package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        PathFilter exe = PathFilter.create(".exe", p->p.getName().toString().toLowerCase().endsWith(".exe"));
        PathFilter xml = PathFilter.create(".xml", p->p.getName().toString().toLowerCase().endsWith(".xml"));
        PathFilter txt = PathFilter.create(".txt", p->p.getName().toString().toLowerCase().endsWith(".txt"));
        
        PathFilter xlsx = PathFilter.create(".xls or .xlsx", p-> p.getName().toString().toLowerCase().endsWith(".xls") 
        		|| p.getName().toString().toLowerCase().endsWith(".xlsx"));
        
        PathFilter na0 = PathFilter.forFileExtension(".na0 (LMS binary files)", "n[a-z]\\d");
        
        Path local = Paths.get("C:\\Users\\Oliver\\Downloads");
        FXFileChooserStage fc = FXFileChooserStage.create(local,xml, xlsx, na0, txt, exe);
        
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
