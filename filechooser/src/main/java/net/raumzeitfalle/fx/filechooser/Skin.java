/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
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

import java.net.URL;

import javafx.scene.Parent;
import javafx.scene.control.Dialog;

public enum Skin {

    NONE,
    MODENA,
    DARK;

    public static <T extends Parent> void applyTo(T parent, Skin skin) {
        if (NONE.equals(skin)) {
            return;
        }

        String css = skin.getCssLocation(parent);
        if (null != css) {
            parent.getStylesheets().add(css);
            parent.applyCss();
        }
    }
    
    public static <T extends Parent> void removeFrom(T parent, Skin skin) {
        if (NONE.equals(skin)) {
            return;
        }

        String css = skin.getCssLocation(parent);
        if (null != css) {
            parent.getStylesheets().remove(css);
            parent.applyCss();
        }
    }

    public static <T extends Dialog<?>> void applyToDialog(T dialog, Skin skin) {
        String css = skin.getCssLocation(dialog);
        if (null != css) {
            dialog.getDialogPane().getStylesheets().add(css);
        }
    }

    private String getStyleName() {
        String cssName = name().substring(1).toLowerCase();
        String first = new String(new char[] {name().charAt(0)});
        return first + cssName;
    }

    private static String getClassName(Object parent) {
        return parent.getClass().getSimpleName();
    }

    private String getCssLocation(Object parent) {

        String className = getClassName(parent);
        String styleName = getStyleName();
        String styleSheetName = className + styleName + ".css";

        URL url = parent.getClass().getResource(styleSheetName);

        if (url == null) {
            return null;
        }
        return url.toExternalForm();
    }
}
