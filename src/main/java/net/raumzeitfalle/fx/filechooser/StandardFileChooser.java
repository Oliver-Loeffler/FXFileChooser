package net.raumzeitfalle.fx.filechooser;

import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import javafx.stage.FileChooser;

public class StandardFileChooser {
    
    /**
     * 
     * The wrapped system dialog from JavaFX.
     *  
     */
	private final FileChooser dialog;

	private final FileSystemDialogAdapter<FileChooser, Window, File> adapter;
		
	public StandardFileChooser() {
		this.dialog = new FileChooser();
		this.adapter = new FileSystemDialogAdapter<>(dialog,
				(fileChooser, window)->fileChooser.showOpenDialog(null));
		
		Consumer<Window> beforeOpen = window -> { 
			window.setFocusableWindowState(false);
			window.setEnabled(false);	
		};
		
		Consumer<Window> afterClosing = window -> 
			SwingUtilities.invokeLater(() -> {
				window.setEnabled(true);
				window.setFocusableWindowState(true);
				window.toFront();
			});

		
		this.adapter
		    .beforeOpenDialog(beforeOpen)
		    .afterClosingDialog(afterClosing);
		
	}
	
	public FileChooser getDialog() {
		return dialog;
	}
	
	public int showDialog(Window window) {
		return this.adapter.runDialog(window);
	}
	
	public int showOpenDialog(Window window) {
		return this.adapter.runDialog(window);
	}
	
	public int showSaveDialog(Window ownerWindow) {
		return this.adapter
				   .runDialog((fileChooser, window)->fileChooser.showSaveDialog(null), ownerWindow);
	}
	
	public File getSelectedFile() {
		return this.adapter.getResult();
	}
	
}
