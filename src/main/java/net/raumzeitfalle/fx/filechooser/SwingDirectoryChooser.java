package net.raumzeitfalle.fx.filechooser;

import java.awt.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.swing.JFileChooser;

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

    @Override
    public Optional<Path> get() {
        int response = this.fc.showOpenDialog(this.owner);
        Path selection = null;
        if (response == JFileChooser.APPROVE_OPTION) {
            selection = this.fc.getSelectedFile().toPath();
        }
        return Optional.ofNullable(selection);
    }
    
}
