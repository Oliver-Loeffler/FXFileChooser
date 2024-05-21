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

import java.util.function.Function;

import net.raumzeitfalle.fx.dirchooser.DirectoryChooser;

public enum DirectoryChooserOption implements Function<FileChooser, PathUpdateHandler> {
    JAVAFX_PLATFORM {       
        @Override
        public PathUpdateHandler apply(FileChooser fileChooser) {
            return FXDirectoryChooser.createIn(fileChooser.currentSearchPath(),
                                               () -> fileChooser.getWindow());
        }
    },
    CUSTOM {
        @Override
        public PathUpdateHandler apply(FileChooser fileChooser) {
           return new DirectoryChooser.DirChooserPathUpdateHandler(fileChooser);
        }
    };
}
