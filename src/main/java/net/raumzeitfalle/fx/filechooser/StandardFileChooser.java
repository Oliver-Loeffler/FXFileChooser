package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.stage.FileChooser;

public class StandardFileChooser {
    
    /**
     * 
     * The wrapped system dialog from JavaFX.
     *  
     */
	private final FileChooser dialog;

	private final FileSystemDialogAdapter<FileChooser, JFrame, File> adapter;
	
	private final Consumer<JFrame> beforeOpen;
	
	private final Consumer<JFrame> afterClosing;
	
	public StandardFileChooser() {
		this.dialog = new FileChooser();
		this.adapter = new FileSystemDialogAdapter<FileChooser, JFrame, File>(dialog,
				(FileChooser dialog, JFrame window)->dialog.showOpenDialog(null));
		
		this.beforeOpen = frame -> { 
			frame.setFocusableWindowState(false);
			frame.setEnabled(false);
		};
		
		this.afterClosing = frame -> {
			SwingUtilities.invokeLater(() -> {
				frame.setFocusableWindowState(true);
				frame.setEnabled(true);
			});
		};
		
		this.adapter
		    .beforeOpenDialog(beforeOpen)
		    .afterClosingDialog(afterClosing);
		
	}
	
	public FileChooser getDialog() {
		return dialog;
	}
	
	public int showOpenDialog(JFrame frame) {
		return this.adapter.runDialog(frame);
	}
	
	public int showSaveDialog(JFrame frame) {
		return this.adapter.runDialog((FileChooser dialog, JFrame window)->dialog.showSaveDialog(null), frame);
	}
	
	public File getSelectedFile() {
		return this.adapter.getResult();
	}
	
}
