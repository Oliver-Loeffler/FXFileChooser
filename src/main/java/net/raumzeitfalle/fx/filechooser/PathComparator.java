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

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Provides various {@link Comparator} variants to compare {@link Path} objects.
 */
class PathComparator {

    static Comparator<IndexedPath> byName() {
        return (IndexedPath a, IndexedPath b) -> a.compareByName(b);
    }

    static Comparator<IndexedPath> byTimestamp() {
        return (IndexedPath a, IndexedPath b) -> a.getTimestamp().compareTo(b.getTimestamp());
    }

    private PathComparator() {
        // provides short cuts for commonly used comparators
    }

}
