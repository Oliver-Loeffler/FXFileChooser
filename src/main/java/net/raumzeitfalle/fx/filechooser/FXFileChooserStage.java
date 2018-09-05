package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXFileChooserStage extends Stage implements HideableWindow {
    
    public static FXFileChooserStage create(Skin skin) throws IOException {
        return create(skin,null,new PathFilter[0]);
    }
    
    public static FXFileChooserStage create(Skin skin,PathFilter ...filter) throws IOException {
        return new FXFileChooserStage(FileChooserModel.get(),skin, filter);
    }
    
    public static FXFileChooserStage create(Skin skin, Path inLocation, PathFilter ...filter) throws IOException {
        return new FXFileChooserStage(FileChooserModel.startingIn(inLocation),skin, filter);
    }
    
    private final FileChooserModel model;
    
    private FXFileChooserStage(FileChooserModel model, Skin skin, PathFilter ...filter) throws IOException {
        this.model = model;
        for (PathFilter f : filter) {
            this.model.addFilter(f);
        }
        
        FXDirectoryChooser dirChooser = FXDirectoryChooser.createIn(model.currentSearchPath(), this.getOwner());
        Parent view = FileChooserView.create(model,dirChooser,this, skin);
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
