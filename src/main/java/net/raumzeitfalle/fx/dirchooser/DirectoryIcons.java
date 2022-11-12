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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

enum DirectoryIcons {
    
    OPEN("icons/folder-open-32.png"),
    CLOSED("icons/folder-closed-32.png"),
    OPEN_WITH_FILE("icons/folder-file-open-32.png"),
    CLOSED_PLUS("icons/folder-closed-plus-32.png"),
    CLOSED_XL("icons/folder-closed-xl-32.png"),
    HOST("icons/host-computer-32.png"),
    EMPTY("icons/folder-empty-32.png"),
    NO_SUBDIRS_XL("icons/folder-closed-unknown-xl-32.png"),
    DRIVE("icons/windows-drive-32.png"),
    DRIVE_PLUS("icons/windows-drive-plus-32.png"),
    DRIVE_XL("icons/windows-drive-xl-32.png"),
    DRIVE_EMPTY("icons/windows-drive-empty-32.png");
  
    private final String iconResource;
    private static int iconSize = 24;
    private static double paneSize = iconSize*1.5;
    
    static final String PROPERTIES_FILE = "directorychooser.properties";
    static final String PROPERTY_ICON_SIZE = "directory.chooser.icon.size";
    
    static {
        URL resource = DirectoryIcons.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (resource != null) {           
            try (FileInputStream fis = new FileInputStream(new File(resource.toURI()))) {
                Properties props = new Properties();
                props.load(fis);
                String value = props.getProperty(PROPERTY_ICON_SIZE, "24");
                iconSize = Integer.parseInt(value);
                paneSize = iconSize * 1.5; 
            } catch (Exception error) {
                String message = String.format("Failed to read icon size from %s (via resource: %s)",
                                        new Object[] {PROPERTIES_FILE, resource});
                Logger.getLogger(DirectoryIcons.class.getName())
                      .log(Level.WARNING, message, error);
            }
        }
    }
    
    private DirectoryIcons(String iconFileName) {
        this.iconResource = DirectoryIcons.class
                                          .getResource(iconFileName)
                                          .toExternalForm();
    }
    
    ImageView create() {
        ImageView image = new ImageView(iconResource);
        image.preserveRatioProperty().set(true);
        image.setFitHeight(iconSize);
        image.getStyleClass().add("directory-icon");
        return image;
    }
    
    protected StackPane get() {
       StackPane pane = new StackPane();
       pane.getChildren().add(create());
       pane.setMinWidth(paneSize);
       pane.getStyleClass().add("directory-icon-pane");       
       return pane;
    }

    
}
