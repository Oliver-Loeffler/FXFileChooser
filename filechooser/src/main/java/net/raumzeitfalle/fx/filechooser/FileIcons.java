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
package net.raumzeitfalle.fx.filechooser;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

enum FileIcons {
    
    CSV(".csv", "icons/file-csv.png"),
    DOC(".doc", "icons/file-doc.png"),
    DOCX(".docx", "icons/file-docx4.png"),
    UNKNOWN(null, "icons/file-unknown.png"),
    XLS(".xls", "icons/file-xls.png"),
    XLSX(".xlsx", "icons/file-xlsx4.png"),
    TXT(".txt", "icons/file-txt.png"),
    XML(".xml", "icons/file-xml.png");
        
    private final String suffix;
    private final String iconResource;

    private FileIcons(String suffix, String iconFileName) {
        this.suffix = suffix;
        this.iconResource = FileIcons.class
                                     .getResource(iconFileName)
                                     .toExternalForm();
    }
    
    ImageView create() {
        ImageView image = new ImageView(iconResource);
        image.preserveRatioProperty().set(true);
        image.setFitHeight(FileChooserProperties.getIconSize());
        image.getStyleClass().add("file-icon");
        return image;
    }
    
    private static ImageView create(String file) {
        if (null == file) {
            return FileIcons.UNKNOWN.create();
        }
        String fileName = file.toLowerCase();
        for (FileIcons icon : FileIcons.values()) {
            String suffix = icon.suffix;
            if (null != icon.suffix && fileName.endsWith(suffix)) {
                return icon.create();
            }
        }
        return FileIcons.UNKNOWN.create();
    }

    static StackPane fromFile(IndexedPath path) {
        return fromFile(path.toString());
    }

    static StackPane fromFile(String file) {
       StackPane pane = new StackPane();
       ImageView image = create(file);
       pane.getChildren().add(image);
       pane.setMinWidth(FileChooserProperties.getIconSize() * 1.5);
       pane.getStyleClass().add("file-icon-pane");
       return pane;
    }
}
