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
