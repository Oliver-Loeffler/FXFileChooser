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
package net.raumzeitfalle.fx.filechooser.locations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class NamedLocation implements Location {
	
	private final String name;
	
	private final Path directory;
	
	protected NamedLocation(Path parent) {
		this(createName(parent), parent);
	}
	
	private static String createName(Path parent) {
		return parent.toString();
	}

	protected NamedLocation(String name, Path path) {
		this.name = name;
		this.directory = path;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean exists() {
		return Files.exists(this.directory);
	}

	@Override
	public Path getPath() {
		return this.directory;
	}

	@Override
	public int hashCode() {
		return Objects.hash(directory, name);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (null == obj) {
			return false;
		}
		
		if (this == obj)
			return true;
		
		if (!implementsLocationInterface(obj))
			return false;
		
		Location other = (Location) obj;
		
		return     Objects.equals(directory, other.getPath()) 
				&& Objects.equals(name, other.getName());
	}

	private boolean implementsLocationInterface(Object obj) {
		Class<?>[] interfaces = obj.getClass().getInterfaces();
		for (Class<?> i : interfaces) {
			if (i.equals(Location.class))
				return true;
		}
		return false;
	}

}
