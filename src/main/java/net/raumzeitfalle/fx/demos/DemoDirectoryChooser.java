/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.demos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.dirchooser.DirectoryChooser;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DemoDirectoryChooser extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    private DirectoryChooser view; 
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        view = new DirectoryChooser(Skin.DARK);
        Scene scene = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    @Override
    public void stop() {
        view.shutdown();
    }
}
