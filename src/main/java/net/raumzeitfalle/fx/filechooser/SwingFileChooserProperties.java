package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

enum SwingFileChooserProperties {
    INSTANCE;

    static {
        INSTANCE.load();
    }
    
    static final String PROPERTIES_FILE = "swingfilechooser.properties";
    static final String PROPERTY_USE_JAVAFX_DIRCHOOSER = "use.javafx.platform.directory.chooser";

    private final Properties properties;

    private SwingFileChooserProperties() {
        this.properties = new Properties();
    }

    public static boolean usesJavaFXDirectoryChooser() {
        return INSTANCE.getBooleanProperty(PROPERTY_USE_JAVAFX_DIRCHOOSER, false);
    }

    public static void setUseJavaFXDirectoryChooser(boolean toggle) {
        INSTANCE.properties
        .setProperty(PROPERTY_USE_JAVAFX_DIRCHOOSER, Boolean.toString(toggle));
    }

    
    boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    void load() {
        URL resource = SwingFileChooserProperties.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (resource != null) {           
            try (FileInputStream fis = new FileInputStream(new File(resource.toURI()))) {
                properties.load(fis);
            } catch (Exception error) {
                String message = String.format("Failed to read SwingFileChooserProperties size from %s (via resource: %s)",
                                        new Object[] {PROPERTIES_FILE, resource});
                Logger.getLogger(SwingFileChooserProperties.class.getName())
                      .log(Level.WARNING, message, error);
            }
        }
    }
}
