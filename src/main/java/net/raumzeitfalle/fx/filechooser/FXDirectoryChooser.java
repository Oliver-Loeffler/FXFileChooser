package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXDirectoryChooser implements PathSupplier {
    
    public static FXDirectoryChooser createIn(ObjectProperty<Path> startLocation, Window owner) {
        
        Path location = startLocation.get();
        
        if (null == location) {
            location = Paths.get("");
        }
        
        return new FXDirectoryChooser(location, owner);
    }
    
    public static FXDirectoryChooser createIn(Path startLocation, Window owner) {
        Objects.requireNonNull(startLocation,"startLocation for file search must not be null.");
        return new FXDirectoryChooser(startLocation, owner);
    }
    
    private final DirectoryChooser dc;
    
    private final Window owner;

    private FXDirectoryChooser(Path startLocation, Window owner) {
        this.dc = new DirectoryChooser();
        this.dc.setInitialDirectory(startLocation.toFile());
        this.owner = owner;
    }

    
    public void getUpdate(Consumer<Path> update) {
	    	Invoke.later(()->{
	    		Optional<File> selection = Optional.ofNullable(this.dc.showDialog(this.owner));
	            selection
	            	.map(File::toPath)
	            	.ifPresent(update::accept);
	    	});
    }
    
}
