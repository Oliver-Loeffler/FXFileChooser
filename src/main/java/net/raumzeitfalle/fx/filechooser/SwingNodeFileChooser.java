package net.raumzeitfalle.fx.filechooser;

import java.io.File;

import javafx.scene.Node;
import javafx.stage.FileChooser;

public class SwingNodeFileChooser {

	private final FileChooser dialog;
	
	private final FileSystemDialogAdapter<FileChooser, Node, File> wrapper;
	
	public SwingNodeFileChooser() {
		this.dialog = new FileChooser();
		this.wrapper = new FileSystemDialogAdapter<>(dialog,
				(FileChooser chooser, Node node)->chooser.showOpenDialog(node.getScene().getWindow()));
	}
	
	public int showOpenDialog(Node ownerNode) {
		return this.wrapper.runDialog(ownerNode);
	}
	
	public File getSelectedFile() {
		return this.wrapper.getResult();
	}
	
}
