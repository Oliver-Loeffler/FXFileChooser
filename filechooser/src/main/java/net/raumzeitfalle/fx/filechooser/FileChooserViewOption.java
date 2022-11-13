/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2020 Oliver Loeffler, Raumzeitfalle.net
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
