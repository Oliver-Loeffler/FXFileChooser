package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXFileChooser extends Stage implements ClosableStage {
    
    public static FXFileChooser create() throws IOException {
        return create(new PathFilter[0]);
    }
    
    public static FXFileChooser create(PathFilter ...filter) throws IOException {
        return new FXFileChooser(new FileChooserModel(), filter);
    }
    
    private final FileChooserModel model;
    
    private FXFileChooser(FileChooserModel model, PathFilter ...filter) throws IOException {
        this.model = model;
        for (PathFilter f : filter) {
            this.model.addFilter(f);
        }
        Parent view = FileChooserView.create(model, this);
        Scene scene = new Scene(view);
        this.setScene(scene);
        initModality(Modality.APPLICATION_MODAL);
    }
    
    public Optional<Path> getSelectedPath() {
        this.showAndWait();
        return Optional.ofNullable(this.model.getSelectedFile());
    }    
}
