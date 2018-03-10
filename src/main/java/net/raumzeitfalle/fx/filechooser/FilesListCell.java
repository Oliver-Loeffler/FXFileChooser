package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;


import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

class FilesListCell extends ListCell<Path> {

	@Override
	protected void updateItem(Path item, boolean empty) {
		super.updateItem(item, empty);
		updateView();
	}
	
	private void updateView() {
        if (getItem() != null) {
            setText(String.valueOf(getItem().getFileName()));
            
            if (getItem().getFileName().toString().toLowerCase().endsWith(".xls")) {
                String img = FilesListCell.class.getResource("005-technology.png").toExternalForm();
                ImageView icon = new ImageView(img);
                icon.setFitHeight(24);
                icon.setFitWidth(24);
                setGraphic(icon);
            }
            
            if (getItem().getFileName().toString().toLowerCase().endsWith(".xlsx")) {
                String image = FilesListCell.class.getResource("011-excel.png").toExternalForm();
                ImageView img = new ImageView(image);
                img.setFitHeight(24);
                img.setFitWidth(24);
                setGraphic(img);
            }
            
            if (getItem().getFileName().toString().toLowerCase().endsWith(".xml")) {
                
                String image = FilesListCell.class.getResource("010-xml.png").toExternalForm();
                ImageView img = new ImageView(image);
                img.setFitHeight(24);
                img.setFitWidth(24);
                setGraphic(img);
            }
            
        } else {
            setText("");
            setGraphic(null);
        }
        
        
    }
}
