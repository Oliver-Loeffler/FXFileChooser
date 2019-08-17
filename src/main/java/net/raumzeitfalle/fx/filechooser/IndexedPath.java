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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

class IndexedPath {
	
	private final Path path;
	
	private final FileTime timestamp;
	
	public static IndexedPath valueOf(Path path) {
		FileTime timestamp = getTimestamp(path);
		return new IndexedPath(path, timestamp);
	}

	private static FileTime getTimestamp(Path path) {
		try {			
			return Files.getLastModifiedTime(path);
		} catch (IOException e) {
			Instant fallback = LocalDateTime.MAX.atZone(ZoneId.systemDefault()).toInstant();
			return FileTime.from(fallback);
		}
	}

	private IndexedPath(Path path, FileTime timestamp) {
		this.path = path;
		this.timestamp = timestamp;
	}
	
	public FileTime getTimestamp() {
		return this.timestamp;
	}
	
	public Path asPath() {
		return this.path;
	}
	
	@Override
	public String toString() {
		return this.path.toString();
	}

}
