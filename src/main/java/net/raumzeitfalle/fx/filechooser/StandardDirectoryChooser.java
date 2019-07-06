package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.stage.DirectoryChooser;

public class StandardDirectoryChooser {
    
    /**
     * 
     * The wrapped system dialog from JavaFX.
     *  
     */
	private final DirectoryChooser dialog;

	private final FileSystemDialogAdapter<DirectoryChooser, JFrame, File> adapter;
	
	private final Consumer<JFrame> beforeOpen;
	
	private final Consumer<JFrame> afterClosing;
	
	public StandardDirectoryChooser() {
		this.dialog = new DirectoryChooser();
		this.adapter = new FileSystemDialogAdapter<DirectoryChooser, JFrame, File>(dialog,
				(DirectoryChooser dialog, JFrame window)->dialog.showDialog(null));
		
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
	
	public DirectoryChooser getDialog() {
		return dialog;
	}
	
	public int showDialog(JFrame frame) {
		return this.adapter.runDialog(frame);
	}
		
	public File getSelectedFile() {
		return this.adapter.getResult();
	}
	
}
