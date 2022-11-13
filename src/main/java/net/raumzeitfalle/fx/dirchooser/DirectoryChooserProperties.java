/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.fx.dirchooser;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum DirectoryChooserProperties {
    INSTANCE;

    static {
        INSTANCE.load();
    }

    static final String PROPERTIES_FILE = "directorychooser.properties";
    static final String PROPERTY_ICON_SIZE = "directory.chooser.icon.size";

    private final Properties properties;

    private DirectoryChooserProperties() {
        this.properties = new Properties();
    }
    
    public static int getIconSize() {
        return INSTANCE.getIntProperty(PROPERTY_ICON_SIZE, 24);
    }

    public static double getIconPaneSize() {
        return INSTANCE.getIntProperty(PROPERTY_ICON_SIZE, 24) * 1.5;
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
            Logger.getLogger(DirectoryChooserProperties.class.getName())
                  .log(Level.WARNING, "int property not configured properly", nfe);
            return 24;
        }
    }

    void load() {
        URL resource = DirectoryChooserProperties.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (resource != null) {           
            try (FileInputStream fis = new FileInputStream(new File(resource.toURI()))) {
                properties.load(fis); 
            } catch (Exception error) {
                String message = String.format("Failed to DirectoryChooserProperties from %s (via resource: %s)",
                                        new Object[] {PROPERTIES_FILE, resource});
                Logger.getLogger(DirectoryChooserProperties.class.getName())
                      .log(Level.WARNING, message, error);
            }
        }
    }
}
