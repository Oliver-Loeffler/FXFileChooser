package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
    	Logger logger = Logger.getLogger(DemoJavaFxStage.class.getSimpleName());
		
        PathFilter exe = PathFilter.forFileExtension("Program", "exe");
        PathFilter xml = PathFilter.forFileExtension("XML", "xml");
        PathFilter txt = PathFilter.forFileExtension("Text", "txt");
        PathFilter xlsx = PathFilter.forFileExtension("Excel 2007", "xlsx");
        
        PathFilter combined = xlsx.combine(txt).combine(xml).combine(exe); 
        
        PathFilter na0 = PathFilter.forFileExtension(".na0 (LMS binary files)", "n[a-z]\\d");
        
        Path local = Paths.get("./");
        FXFileChooserStage fc = FXFileChooserStage.create(Skin.DEFAULT, local,xml, xlsx, na0, txt, exe,combined);
        
        Button button = new Button("Show customized Stage: FXFileChooserImpl.class");
        button.setOnAction(e -> {
            Optional<Path> selection = fc.showOpenDialog(primaryStage);
            logger.log(Level.INFO, selection.map(String::valueOf).orElse("Nothing selected"));
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
