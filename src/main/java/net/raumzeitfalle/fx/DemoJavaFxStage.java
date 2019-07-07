package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoJavaFxStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
    	Logger logger = Logger.getLogger(DemoJavaFxStage.class.getSimpleName());
		
    	PathFilter all = PathFilter.acceptAllFiles("all files");
    	
        PathFilter exe = PathFilter.forFileExtension("Program", "exe");
        PathFilter xml = PathFilter.forFileExtension("XML", "xml");
        PathFilter txt = PathFilter.forFileExtension("Text", "txt");
        PathFilter xlsx = PathFilter.forFileExtension("Excel 2007", "xlsx");
        
        PathFilter combined = xlsx.combine(txt).combine(xml).combine(exe); 
        
        PathFilter na0 = PathFilter.forFileExtension(".na0 (LMS binary files)", "n[a-z]\\d");
        
        Path local = Paths.get("./");
        FXFileChooserStage fc = FXFileChooserStage.create(Skin.DARK, local,all,xml, xlsx, na0, txt, exe,combined);
        
        Button button = new Button("Show customized Stage: FXFileChooserImpl.class");
        button.setOnAction(e -> {
            Optional<Path> selection = fc.showOpenDialog(primaryStage);
            logger.log(Level.INFO, selection.map(String::valueOf).orElse("Nothing selected"));
        });
        
        FileChooser standardFileChooser = new FileChooser();
        Button standardFileChooserButton = new Button("JavaFX standard file chooser");
        standardFileChooserButton.setOnAction(event->standardFileChooser.showOpenDialog(primaryStage));
        
        FileChooser standardDirectoryChooser = new FileChooser();
        Button standardDirectoryChooserButton = new Button("JavaFX standard directory chooser");
        standardDirectoryChooserButton.setOnAction(event->standardDirectoryChooser.showOpenDialog(primaryStage));
        
        VBox vbox = new VBox(button, standardDirectoryChooserButton, standardFileChooserButton);
        Scene mainScene = new Scene(vbox);
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(400);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(new String[0]);
    }
    
}
