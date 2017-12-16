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
        return new FXFileChooser(new FileChooserModel());
    }
    
    private final FileChooserModel model;
    
    private FXFileChooser(FileChooserModel model) throws IOException {
        this.model = model;
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
