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
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The PathFilter interface is used by {@link FileChooserModel} to provide filter predicates matching specific file types. Each PathFilter also provides a name to populate e.g. text fields in {@link javafx.scene.control.MenuItem} or plain {@link javafx.scene.control.Label} fields.<br>
 */
public interface PathFilter {

    /**
     * @return A name to be used in user interface dialogs.
     */
    String getName();

    /**
     * @return The {@link Predicate} to match a specific file type or a group of
     *         file types.
     */
    Predicate<String> getPredicate();

    /**
     * @param pathName {@link Path} to test
     * @return true in case the given {@link Path} matches with the
     *         {@link Predicate}.
     */
    default boolean matches(String pathName) {
        return getPredicate().test(pathName);
    }

    /**
     * @param file {@link File}
     * @return true in case the given {@link File} matches with the
     *         {@link Predicate}.
     */
    default boolean matches(File file) {
        return this.matches(file.toString());
    }

    /**
     * Creates a new PathFilter as a combination of the this one and any other.
     * 
     * @param other {@link PathFilter} to be combined with this one
     * @return {@link PathFilter} where this ones {@link Predicate} and the others
     *         {@link PathFilter} {@link Predicate} are combined using a logical OR.
     */
    default PathFilter combine(PathFilter other) {
        String label = getName() + ", " + other.getName();
        Predicate<String> thisOne = this.getPredicate();
        return create(label, thisOne.or(other.getPredicate()));
    }

    /**
     * Creates a new {@link PathFilter} which generally matches with all files and is named {@code *.*}.
     * This filter is intended to be used as graceful default for cases where no path filters are provided.
     * @return {@link PathFilter}
     */
    static PathFilter acceptAllFiles() {
        return acceptAllFiles("*.*");
    }

    /**
     * Creates a new {@link PathFilter} which generally matches with all files.
     * 
     * @param name String value intended to be used as GUI text.
     * @return {@link PathFilter}
     */
    static PathFilter acceptAllFiles(String name) {
        return create(name, p -> true);
    }

    static PathFilter create(Predicate<String> p) {
        return create(String.valueOf(p), p);
    }

    static PathFilter create(String label, Predicate<String> p) {
        return new PathFilter() {

            @Override
            public Predicate<String> getPredicate() {
                return p;
            }

            @Override
            public String getName() {
                return label;
            }
        };
    }

    /**
     * Creates a new {@link PathFilter} for file name extensions such as (.html,
     * .xls, .xml or .pdf). The label text will be automatically the extension with
     * a &quot;*.&quot; prefix so for extension txt the label will be *.txt.
     * 
     * @param extension {@link String} the file name extension
     * @return new {@link PathFilter}
     */
    static PathFilter forFileExtension(String extension) {
        return forFileExtension("*." + extension, extension);
    }

    /**
     * Creates a new {@link PathFilter} for file name extensions such as (.html,
     * .xls, .xml or .pdf).
     * 
     * @param label     GUI label text
     * @param extension {@link String} the file name extension
     * @return new {@link PathFilter}
     */
    static PathFilter forFileExtension(String label, String extension) {
        return create(label, p -> {
            if (null != p) {
                String name = p.toLowerCase();
                int lastDot = name.lastIndexOf('.');
                if (lastDot > 0) {
                    return name.substring(lastDot).matches("[.]" + extension + "$");
                }
            }
            return false;
        });
    }
}
