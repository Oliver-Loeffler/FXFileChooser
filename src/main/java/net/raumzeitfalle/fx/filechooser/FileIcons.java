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

import java.nio.file.Path;

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
    
    private final String iconFileName;
    
    private FileIcons(String suffix, String iconFileName) {
        this.suffix = suffix;
        this.iconFileName = iconFileName;
    }
    
    
    private static ImageView create(String iconFileName, double fitSize) {
        String img = FileIcons.class.getResource(iconFileName).toExternalForm();
        ImageView image = new ImageView(img);
        image.preserveRatioProperty().set(true);
        image.setFitHeight(fitSize);
        return image;
    }

    static StackPane fromFile(Path path, double fitSize) {
       StackPane pane = new StackPane();
       Path file = path.getFileName();
       ImageView image = null;
       if (null != file) {
           String fileName = file.toString().toLowerCase();
           for (FileIcons icon : FileIcons.values()) {
               String suffix = icon.suffix;
               if (null != suffix && fileName.endsWith(suffix)) {
                       image = create(icon.iconFileName,fitSize);
               }
           }    
       }
       if (null == image) {
           image = create(FileIcons.UNKNOWN.iconFileName,fitSize);
       }
       
       pane.getChildren().add(image);
       pane.setMinWidth(fitSize*1.5);
       image.getStyleClass().add("file-icon");
       pane.getStyleClass().add("file-icon-pane");
       
       return pane;
    }
}
