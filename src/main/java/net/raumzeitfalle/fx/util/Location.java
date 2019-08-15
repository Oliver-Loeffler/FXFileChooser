package net.raumzeitfalle.fx.util;

import java.nio.file.Path;
import java.util.Comparator;

public interface Location extends Comparator<Location> {
	
	String getName();
	
	boolean exists();
	
	Path getPath();
	
	default public int compare(Location a, Location b) {
		return a.getName().compareToIgnoreCase(b.getName());
	}
}
