package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum FileChooserProperties {
    INSTANCE;

    static {
        INSTANCE.load();
    }

    static final String PROPERTIES_FILE = "filechooser.properties";
    static final String PROPERTY_ICON_SIZE = "file.chooser.icon.size";

    private final Properties properties;


    private FileChooserProperties() {
        this.properties = new Properties();
    }
    
    public static int getIconSize() {
        return INSTANCE.getIntProperty(PROPERTY_ICON_SIZE, 32);
    }

    public static void setIconSize(int newIconSize) {
        INSTANCE.properties
        .setProperty(PROPERTY_ICON_SIZE,
                Integer.toString(newIconSize));
    }
    
    int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            Logger.getLogger(FileChooserProperties.class.getName())
                  .log(Level.WARNING, "int property not configured properly", nfe);
            return 24;
        }
    }

    void load() {
        URL resource = FileChooserProperties.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (resource != null) {           
            try (FileInputStream fis = new FileInputStream(new File(resource.toURI()))) {
                properties.load(fis); 
            } catch (Exception error) {
                String message = String.format("Failed to FileChooserProperties from %s (via resource: %s)",
                                        new Object[] {PROPERTIES_FILE, resource});
                Logger.getLogger(FileChooserProperties.class.getName())
                      .log(Level.WARNING, message, error);
            }
        }
    }
}
