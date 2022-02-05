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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class IndexedPath {

    private static final Logger LOGGER = Logger.getLogger(IndexedPath.class.getName());

    public static IndexedPath valueOf(Path path) {
        FileTime timestamp;
        try {
            timestamp = getTimestamp(path);
        } catch (IOException e) {
            timestamp = FileTime.from(0, TimeUnit.MICROSECONDS);
            String message = String.format("Could not determine lastModified timestamp for %s, returning %s instead.",
                    path, timestamp);
            LOGGER.log(Level.SEVERE, message, e);
        }
        return new IndexedPath(path, timestamp);
    }

    private static FileTime getTimestamp(Path path) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime lastModified = attributes.lastModifiedTime();
        FileTime created = attributes.creationTime();
        if (lastModified.compareTo(created) > 0) {
            return lastModified;
        } else {
            return created;
        }
    }

    private final byte[] fileName;
    private final long timestamp;

    IndexedPath(Path path, FileTime timestamp) {
        this.fileName = path.getFileName().toString().getBytes();
        this.timestamp = timestamp.to(TimeUnit.MILLISECONDS);
    }

    public final FileTime getTimestamp() {
        return FileTime.from(timestamp, TimeUnit.MILLISECONDS);
    }

    public final Path asPath(Path location) {
        return location.resolve(toString());
    }

    @Override
    public final String toString() {
        return new String(fileName);
    }
    
    int compareByName(IndexedPath other) {
        return toString().compareTo(other.toString());
    }
}
