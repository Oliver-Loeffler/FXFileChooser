package net.raumzeitfalle.fx.util;

import java.nio.file.Path;

public interface Location {
	
	String getName();
	
	boolean exists();
	
	Path getPath();
}
