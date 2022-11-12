/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import net.raumzeitfalle.fx.filechooser.Skin;

public class DirectoryChooserView extends AnchorPane {

    private final DirectoryChooserController controller;

    public DirectoryChooserView(Skin skin) throws IOException {

        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);

        controller = new DirectoryChooserController();
        loader.setController(controller);
        Parent view = loader.load();
        this.getChildren().add(view);

        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);

        Skin.applyTo(this, skin);

    }

    public ReadOnlyObjectProperty<Path> selectedDirectoryProperty() {
        return controller.selectedDirectoryProperty();
    }

    public void onSelect(Runnable action) {
        controller.setOnSelect(action);
    }

    public void onCancel(Runnable action) {
        controller.setOnCancel(action);

    }

}
