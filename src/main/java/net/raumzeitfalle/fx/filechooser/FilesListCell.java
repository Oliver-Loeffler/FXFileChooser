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

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

class FilesListCell extends ListCell<IndexedPath> {

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd  -  HH:mm:ss";

    private static final String FILE_ICON_LABEL = "file-icon-label";

    @Override
    protected void updateItem(IndexedPath item, boolean empty) {
        super.updateItem(item, empty);
        updateView();
    }

    private void updateView() {
        if (getItem() != null && getItem() != null) {

            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().add(FILE_ICON_LABEL);

            Pane icon = FileIcons.fromFile(getItem());
            icon.setId("fileListCell-fileTypeIcon");
            
            gridPane.addColumn(0, icon);
            GridPane.setHgrow(icon, Priority.SOMETIMES);
            
            
            Label fileName = new Label(String.valueOf(getItem()));
            fileName.getStyleClass().add(FILE_ICON_LABEL);
            fileName.setId("fileListCell-fileName");
            
            gridPane.addColumn(1, fileName);
            GridPane.setHgrow(fileName, Priority.ALWAYS);
            
            Label date = new Label("");
            
            FileTime time = getItem().getTimestamp();
            LocalDateTime timestamp = LocalDateTime.from(time.toInstant().atZone(ZoneId.systemDefault()));
            date.setText(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN).format(timestamp));
            date.setId("fileListCell-fileDate");
            
            date.getStyleClass().add(FILE_ICON_LABEL);
            gridPane.addColumn(2, date);
            GridPane.setHgrow(date, Priority.NEVER);
            
            setGraphic(gridPane);
            
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}
