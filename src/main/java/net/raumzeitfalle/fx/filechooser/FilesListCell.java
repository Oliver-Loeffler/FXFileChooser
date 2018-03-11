package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

class FilesListCell extends ListCell<File> {

	@Override
	protected void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);
		updateView();
	}
	
	private void updateView() {
        if (getItem() != null) {
            
            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().add("file-icon-label");      
                        
            Pane icon = FileIcons.fromFile(getItem(), 32);
            gridPane.addColumn(0, icon);
            GridPane.setHgrow(icon, Priority.SOMETIMES);
            
            Label fileName = new Label(String.valueOf(getItem().getName()));
            fileName.getStyleClass().add("file-icon-label");
            gridPane.addColumn(1, fileName);
            GridPane.setHgrow(fileName, Priority.ALWAYS);
            
            Label date = new Label("");
            
            try {
                File item = getItem();
                FileTime time = Files.getLastModifiedTime(item.toPath());
                LocalDateTime timestamp = LocalDateTime.from(time.toInstant().atZone(ZoneId.systemDefault()));
                date.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd  -  HH:mm:ss").format(timestamp));
            } catch (IOException e) {
                date.setText("...");
            }
            date.getStyleClass().add("file-icon-label");
            gridPane.addColumn(2, date);
            GridPane.setHgrow(date, Priority.NEVER);
            
            setGraphic(gridPane);
            
        } else {
            setText(null);
            setGraphic(null);
        }
        
        
    }
}
