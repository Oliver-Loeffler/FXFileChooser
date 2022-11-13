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
                                                PROPERTIES_FILE, resource);
                Logger.getLogger(SwingFileChooserProperties.class.getName())
                      .log(Level.WARNING, message, error);
            }
        }
    }
}
