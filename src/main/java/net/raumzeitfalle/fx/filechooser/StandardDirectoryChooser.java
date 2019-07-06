package net.raumzeitfalle.fx.filechooser;

import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import javafx.stage.DirectoryChooser;

public class StandardDirectoryChooser {
	    
    /**
     * The wrapped system dialog from JavaFX.
     *  
     */
	private final DirectoryChooser dialog;

	private final FileSystemDialogAdapter<DirectoryChooser, Window, File> adapter;
	
	private final Consumer<Window> beforeOpen;
	
	private final Consumer<Window> afterClosing;
	
	public StandardDirectoryChooser() {
		this.dialog = new DirectoryChooser();
		this.adapter = new FileSystemDialogAdapter<>(dialog,
				(DirectoryChooser chooser, Window window)->chooser.showDialog(null));
		
		this.beforeOpen = window -> { 
			window.setFocusableWindowState(false);
			window.setEnabled(false);
		};
		
		this.afterClosing = window -> 		
			SwingUtilities.invokeLater(() -> {
				window.setEnabled(true);
				window.setFocusableWindowState(true);
				window.toFront();
			});
		
		this.adapter
		    .beforeOpenDialog(beforeOpen)
		    .afterClosingDialog(afterClosing);
	}
	
	public DirectoryChooser getDialog() {
		return dialog;
	}
	
	public int showDialog(Window window) {
		return this.adapter.runDialog(window);
	}
		
	public File getSelectedFile() {
		return this.adapter.getResult();
	}
	
}
