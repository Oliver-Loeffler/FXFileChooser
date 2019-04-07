package net.raumzeitfalle.fx.filechooser;

import java.net.URL;

import javafx.scene.Parent;

public enum Skin {

    DEFAULT,
    DARK;

    public static void applyTo(Parent parent, Skin skin) {
        URL url = getCssLocation(skin);
        if (null != url) {
            parent.getStylesheets().add(url.toExternalForm());
            parent.applyCss();
        }
        
    }

	private static URL getCssLocation(Skin skin) {
		if (DARK.equals(skin)) {
        	return FileChooserView.class.getResource("FileChooserViewDark.css");
        }
		return FileChooserView.class.getResource("FileChooserViewDefault.css");
	}
}
