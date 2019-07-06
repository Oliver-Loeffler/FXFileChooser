package net.raumzeitfalle.fx.filechooser;

import java.io.File;

import javafx.embed.swing.SwingNode;
import javafx.stage.FileChooser;

public class SwingNodeFileChooser {

	private final FileChooser dialog;
	
	private final FileSystemDialogAdapter<FileChooser, SwingNode, File> wrapper;
	
	public SwingNodeFileChooser() {
		this.dialog = new FileChooser();
		this.wrapper = new FileSystemDialogAdapter<>(dialog,
				(FileChooser chooser, SwingNode node)->chooser.showOpenDialog(node.getScene().getWindow()));
	}
	
	public int showOpenDialog(SwingNode node) {
		return this.wrapper.runDialog(node);
	}
	
	public File getSelectedFile() {
		return this.wrapper.getResult();
	}
	
}
