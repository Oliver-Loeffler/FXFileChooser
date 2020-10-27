package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class DirectoryWalker {
		
	private Path current;
	
	private int maxDepth;
	
	private int currentDepth;
	
	private DirectoryTreeItem rootNode;
	
	private static Filter<Path> pathFilter = getPathFilter();
	
	public DirectoryWalker(Path start) {
		this(start,0);
	}
	
	public DirectoryWalker(Path start, int maxDepth) {
		this.current = start;
		this.maxDepth = maxDepth;
		this.currentDepth = 0;
		this.rootNode = new DirectoryTreeItem(start);
	}
	
	private DirectoryWalker(DirectoryWalker walker, Path subDir) {
		this.current = walker.current.resolve(subDir);
		this.maxDepth = walker.maxDepth;
		this.currentDepth = walker.currentDepth+1;
		this.rootNode = new DirectoryTreeItem(current);
	}
	
	public DirectoryTreeItem read() {
		return read(new SimpleBooleanProperty(false));
	}
	
	protected DirectoryTreeItem read(ReadOnlyBooleanProperty cancelled) {
		try (DirectoryStream<Path> dirs = Files.newDirectoryStream(current, pathFilter)) {
			for (Path path : dirs) {
				if (cancelled.getValue())
					break;
				addNode(path);
			}
		} catch (IOException e) {
			// consume error
		}
		return this.rootNode;
	}
	

	private void addNode(Path path) {
		if (currentDepth <= maxDepth) {
			
			DirectoryTreeItem leaf = new DirectoryWalker(this, path).read();
			rootNode.getChildren().add(leaf);	
		
		}
	}
	
	private static Filter<Path> getPathFilter() {
		return DirectoryWalker::isDirectory;
	}
	
	private static boolean isDirectory(Path path) {
		/*
		 * On Windows, testing for junctions requires testing for existence of the file system 
		 * entry. Here path.toFile().exists() does not work, whereas Files.exists(path) works fine. 
		 */
		return path.toFile().isDirectory() && Files.exists(path);
	}
}
