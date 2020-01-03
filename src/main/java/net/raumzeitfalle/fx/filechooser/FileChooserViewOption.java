package net.raumzeitfalle.fx.filechooser;

enum FileChooserViewOption {

    /**
     * Configures the FileChooserView and Model so, that it will work in an independent JavaFX stage or inside a JFXPanel.
     * The view will provide its own OKAY and CANCEL buttons.
     */
    STAGE,

    /**
     * Configures the FileChooserView and Model so, that it will work inside a JavaFX Dialog.
     * E.g. JavaFX dialogs already provide OKAY and CANCEL buttons. In this case, the buttons provided by the view will be hidden.
     */
    DIALOG;
}
