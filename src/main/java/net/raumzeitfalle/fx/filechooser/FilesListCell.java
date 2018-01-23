package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import javafx.scene.control.ListCell;

class FilesListCell extends ListCell<Path> {

	@Override
	protected void updateItem(Path item, boolean empty) {
		super.updateItem(item, empty);
		updateView();
	}
	
	private void updateView() {
        if (getItem() != null) {
            setText(String.valueOf(getItem().getFileName()));
        }
    }
}
