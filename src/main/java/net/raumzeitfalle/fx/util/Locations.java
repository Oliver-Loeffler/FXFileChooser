package net.raumzeitfalle.fx.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class Locations {

	public static Location at(Path path) {
		if (Files.isRegularFile(path) && null != path.getParent()) {
			Path parent = path.getParent();
			return new NamedLocation(parent);
		}
		return withName(path.toString(), path);
	}

	public static Location withName(String name, Path path) {
		return new NamedLocation(name, path);
	}

	private Locations() {
		/*
		 * Collection of static factory methods, not intended for instantiation.
		 */
	}
}
