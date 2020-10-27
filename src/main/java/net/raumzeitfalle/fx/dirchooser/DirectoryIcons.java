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
package net.raumzeitfalle.fx.dirchooser;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

enum DirectoryIcons {
    
    OPEN("icons/folder-open-32.png"),
    CLOSED("icons/folder-closed-32.png"),
    OPEN_WITH_FILE("icons/folder-file-open-32.png"),
    CLOSED_PLUS("icons/folder-closed-plus-32.png"),
    HOST("icons/host-computer-32.png");
  
    private final String iconFileName;
    
    private DirectoryIcons(String iconFileName) {
        this.iconFileName = iconFileName;
    }
    
    private static ImageView create(String iconFileName, double fitSize) {
        String img = DirectoryIcons.class.getResource(iconFileName).toExternalForm();
        ImageView image = new ImageView(img);
        image.preserveRatioProperty().set(true);
        image.setFitHeight(fitSize);
        return image;
    }

    protected StackPane get(double fitSize) {
       StackPane pane = new StackPane();
       ImageView image = create(iconFileName,fitSize);
       
       pane.getChildren().add(image);
       pane.setMinWidth(fitSize*1.5);
       image.getStyleClass().add("directory-icon");
       pane.getStyleClass().add("directory-icon-pane");
       
       return pane;
    }
    
}
