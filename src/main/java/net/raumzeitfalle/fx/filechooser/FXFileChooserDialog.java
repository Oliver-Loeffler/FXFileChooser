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
package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.locations.Location;

public class FXFileChooserDialog extends Dialog<Path> implements HideableView {
    
    public static FXFileChooserDialog create(Skin skin, PathFilter ...filter) throws IOException {
    	
    	FileChooserModel model = FileChooserModel.startingInUsersHome(filter);
    	return new FXFileChooserDialog(skin,model);
    	
    }
    
    public void addFilter(PathFilter filter) {
    	model.addOrRemoveFilter(filter);
	}

	public static FXFileChooserDialog create(Skin skin,FileChooserModel model) throws IOException {
        return new FXFileChooserDialog(skin,model);
    }
    
    private final FileChooserModel model;
    
    // TODO: Enable File Chooser to use new (2nd scene) directory chooser as well but keep old API alive for JavaFX
    // TODO: Make CSS file externally configurable
    private FXFileChooserDialog(Skin skin,FileChooserModel fileChooserModel) throws IOException {
        this.model = fileChooserModel;
        Skin.applyTo(getDialogPane(),skin);

        setTitle("File Selection");
        setHeaderText("Select File from for processing:");
        headerTextProperty().bind(model.currentSearchPath().asString());
        initModality(Modality.APPLICATION_MODAL);

        Supplier<Window> ownerProvider = ()->getDialogPane().getScene().getWindow();
        PathSupplier pathSupplier = FXDirectoryChooser.createIn(model.currentSearchPath(), ownerProvider);
        FileChooserView view = new FileChooserView(pathSupplier,this,model, skin,FileChooserViewOption.DIALOG, this);
     
        getDialogPane().setContent(view);

        getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        getDialogPane().minHeightProperty().set(500);
        getDialogPane().minWidthProperty().set(700);
        this.resizableProperty().set(true);
        
        
        ButtonType okay = ButtonType.OK;
        getDialogPane().getButtonTypes().addAll(okay, ButtonType.CANCEL);
        
        Node okayButton = getDialogPane().lookupButton(okay);
        okayButton.disableProperty().bind(model.invalidSelectionProperty());
        
        setResultConverter(dialogButton -> {
            if (dialogButton == okay) {
                this.hide();
                return model.getSelectedFile();
            }
            return null;
        });
        
    }
    
    public Optional<Path> showOpenDialog(Window ownerWindow) {
        if (null == this.getOwner()) {
            this.initOwner(ownerWindow);    
        }
        return this.showAndWait();
    }

    @Override
    public void closeView() {
        this.getDialogPane().getScene().getWindow().hide();
    }

    public void addLocations(List<Location> locations) {
        locations.forEach(model::addLocation);
    }
}
