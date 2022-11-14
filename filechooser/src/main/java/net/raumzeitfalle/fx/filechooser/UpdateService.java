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

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

interface UpdateService {
    void restartIn(Path location);
    ObjectProperty<Path> searchPathProperty();
    void refresh();
    void startUpdate();
    void cancelUpdate();
    ReadOnlyBooleanProperty runningProperty();
    ReadOnlyDoubleProperty progressProperty();

    /**
     * Waits by default 1000ms.
     * Only to be used for testing or debugging.
     */
    default void waitUntilFinished() {
        waitUntilFinished(1000);
    }

    private void waitUntilFinished(long millis) {
        if (this instanceof FileUpdateService service) {
            service.waitUntilFinished();
        } else {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.getLogger(UpdateService.class.getName())
                      .log(Level.WARNING, "Unexpected interruption during wait...", e);
            }
        }
    }
}
