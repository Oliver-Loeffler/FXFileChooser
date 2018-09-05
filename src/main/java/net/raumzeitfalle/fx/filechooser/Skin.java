package net.raumzeitfalle.fx.filechooser;

import javafx.scene.Parent;

public enum Skin {

    DEFAULT,
    DARK;

    public static void applyTo(Parent parent, Skin skin) {
        String css;
        switch (skin) {
            case DARK: css = FileChooserView.class.getResource("FileChooserViewDark.css").toExternalForm(); break;
            default: {
                css = FileChooserView.class.getResource("FileChooserViewDefault.css").toExternalForm(); break;
            }
        }
        if (null != css) {
            parent.getStylesheets().add(css);
            parent.applyCss();
        }
    }
}
