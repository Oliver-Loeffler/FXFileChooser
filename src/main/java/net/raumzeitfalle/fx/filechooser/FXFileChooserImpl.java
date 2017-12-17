package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXFileChooserImpl extends Stage implements HideableStage {
    
    public static FXFileChooserImpl create() throws IOException {
        return create(new PathFilter[0]);
    }
    
    public static FXFileChooserImpl create(PathFilter ...filter) throws IOException {
        return new FXFileChooserImpl(new FileChooserModel(), filter);
    }
    
    private final FileChooserModel model;
    
    private FXFileChooserImpl(FileChooserModel model, PathFilter ...filter) throws IOException {
        this.model = model;
        for (PathFilter f : filter) {
            this.model.addFilter(f);
        }
        Parent view = FileChooserView.create(model, this);
        Scene scene = new Scene(view);
        this.setScene(scene);
        initModality(Modality.APPLICATION_MODAL);
    }
    
    public Optional<Path> showOpenDialog(Window ownerWindow) {
        if (null == this.getOwner()) {
            this.initOwner(ownerWindow);    
        }
        this.showAndWait();
        return this.getSelectedPath();
    }
    
    private Optional<Path> getSelectedPath() {
        return Optional.ofNullable(this.model.getSelectedFile());
    }    
    
}
