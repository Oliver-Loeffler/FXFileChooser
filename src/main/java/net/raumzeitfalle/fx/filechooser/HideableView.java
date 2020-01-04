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

/**
 * Any container (e.g. Stage, Dialog, JFXPanel) which can be hidden, shall implement this interface.
 * In some cases, it is not possible to directly close the window which contains the file chooser.
 * Therefore either the type which represents the window should implement this interface or a
 * Lambda can be created which ensures, that if requested, the window is closed properly.
 */
@FunctionalInterface
interface HideableView {
    void closeView();
}
