package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;


public interface UpdateService {
	void restartIn(Path location);
	ObjectProperty<Path> searchPathProperty();
	
	void refresh();
	void startUpdate();
	void cancelUpdate();
	
	ReadOnlyBooleanProperty runningProperty();
	ReadOnlyDoubleProperty progressProperty();
}
