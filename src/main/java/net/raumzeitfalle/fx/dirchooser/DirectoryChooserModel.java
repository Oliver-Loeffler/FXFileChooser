package net.raumzeitfalle.fx.dirchooser;

import java.nio.file.*;

public class DirectoryChooserModel {

	public DirectoryChooserModel() {
		
		Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
		
		
		
		for (Path p : rootDirectories ) {
			System.out.println(p);
		}
		
	}
}
