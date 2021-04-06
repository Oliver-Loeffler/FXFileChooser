/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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

public enum Skin {

    MODENA,
    DARK;

    public static <T extends Parent> void applyTo(T parent, Skin skin) {
        URL url = getCssLocation(parent, skin);
        if (null != url) {
            parent.getStylesheets().add(url.toExternalForm());
            parent.applyCss();
        }
        
    }

    private static String getStyleName(Skin skin) {
        String name = skin.name().substring(1).toLowerCase();
        String first = new String(new char[]{skin.name().charAt(0)});
        return first+name;
    }

    private static <T extends Parent> String getClassName(T parent) {
        return parent.getClass().getSimpleName();
    }

    private static <T extends Parent> URL getCssLocation(T parent, Skin skin) {

        String className = getClassName(parent);
        String styleName = getStyleName(skin);

		return parent.getClass().getResource(className + styleName + ".css");
	}
}
