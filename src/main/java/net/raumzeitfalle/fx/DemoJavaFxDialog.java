/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.fx;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
import net.raumzeitfalle.fx.filechooser.locations.Location;
import net.raumzeitfalle.fx.filechooser.locations.Locations;

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

		List<Location> locations = new ArrayList<>();
		locations.add(Locations.withName("Configs: /etc", Paths.get("/etc")));
		locations.add(Locations.withName("User Homes: /Users",Paths.get("/Users")));
		locations.add(Locations.withName("C-Drive: C:\\",Paths.get("C:/")));

		darkfc.addLocations(locations);

		Logger logger = Logger.getLogger(DemoJavaFxDialog.class.getSimpleName());
		
		Button showDarkDialog = new Button("Show dark JavaFX Dialog (FXFileChooserDialog.class)");
		showDarkDialog.setOnAction(a -> {

			Optional<Path> path = darkfc.showOpenDialog(primaryStage);
			logger.log(Level.INFO, path.map(String::valueOf).orElse("Nothing selected"));

		});
		
		FXFileChooserDialog fc = FXFileChooserDialog.create(Skin.MODENA,all,exe,xml,txt,xlsx);
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
