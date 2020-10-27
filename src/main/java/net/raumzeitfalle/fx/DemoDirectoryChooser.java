package net.raumzeitfalle.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import net.raumzeitfalle.fx.dirchooser.DirectoryChooserView;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoDirectoryChooser extends Application  {

	public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	DirectoryChooserView view = new DirectoryChooserView(Skin.DARK);
    	Scene scene = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

   
}
