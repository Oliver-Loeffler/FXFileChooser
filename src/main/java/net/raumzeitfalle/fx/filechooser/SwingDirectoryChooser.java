package net.raumzeitfalle.fx.filechooser;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class SwingDirectoryChooser implements PathSupplier {
    
    public static SwingDirectoryChooser createIn(Path startLocation, Component owner) {
        Path location = (null == startLocation) ? Paths.get("") : startLocation;
        return new SwingDirectoryChooser(location, owner);
    }
    
    private final JFileChooser fc;
    
    private final Component owner;

    private SwingDirectoryChooser(Path startLocation, Component owner) {
        this.fc = new JFileChooser(startLocation.toFile());
        this.fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.owner = owner;
    }

    public void getUpdate(Consumer<Path> update) {
    	
    	
		SwingUtilities.invokeLater(()->{
			Optional<File> selection = askForSelection();
	        
	        Invoke.later(()->{
	        		selection.map(File::toPath).ifPresent(update::accept);
	        });
		});
		
		
    }

	private Optional<File> askForSelection() {
		int response = this.fc.showOpenDialog(this.owner);
		File selection = null;
		if (response == JFileChooser.APPROVE_OPTION) {
		    selection = this.fc.getSelectedFile();
		}
		return Optional.ofNullable(selection);
	}
    
}
