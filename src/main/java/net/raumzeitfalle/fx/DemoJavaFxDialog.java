package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserDialog;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoJavaFxDialog extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		PathFilter all = PathFilter.acceptAllFiles("all files");
    	
        PathFilter exe = PathFilter.forFileExtension("Program", "exe");
        PathFilter xml = PathFilter.forFileExtension("XML", "xml");
        PathFilter txt = PathFilter.forFileExtension("Text", "txt");
        
        PathFilter xls = PathFilter.forFileExtension("Excel 2003", "xls");
        PathFilter xlsx = PathFilter.forFileExtension("Excel 2007+", "xlsx").combine(xls);
        
		FXFileChooserDialog darkfc = FXFileChooserDialog.create(Skin.DARK,all,exe,xml,txt,xlsx);
		

		Logger logger = Logger.getLogger(DemoJavaFxDialog.class.getSimpleName());
		
		Button showDarkDialog = new Button("Show dark JavaFX Dialog (FXFileChooserDialog.class)");
		showDarkDialog.setOnAction(a -> {

			Optional<Path> path = darkfc.showOpenDialog(primaryStage);
			logger.log(Level.INFO, path.map(String::valueOf).orElse("Nothing selected"));

		});
		
		FXFileChooserDialog fc = FXFileChooserDialog.create(Skin.DEFAULT,all,exe,xml,txt,xlsx);
		Button showDialog = new Button("Show default JavaFX Dialog (FXFileChooserDialog.class)");
		showDialog.setOnAction(a -> {

			Optional<Path> path = fc.showOpenDialog(primaryStage);
			logger.log(Level.INFO, path.map(String::valueOf).orElse("Nothing selected"));

		});

		VBox vbox = new VBox(showDarkDialog, showDialog);
		Scene scene = new Scene(vbox);
		primaryStage.setScene(scene);
		primaryStage.setWidth(400);
		primaryStage.setHeight(200);
		primaryStage.show();
	}

	public static void main(String[] args) {
	     launch(new String[0]);
	}

}
