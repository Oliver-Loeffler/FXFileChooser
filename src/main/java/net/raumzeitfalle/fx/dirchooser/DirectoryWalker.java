package net.raumzeitfalle.fx.dirchooser;

import java.io.IOException;
import java.nio.file.*;

class DirectoryWalker {
	
	private Path current;
	
	private int maxDepth;
	
	private int currentDepth;
	
	private DirectoryTreeItem rootNode;
	
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
		try (DirectoryStream<Path> dirs = Files.newDirectoryStream(current)) {
			dirs.forEach(this::addNode);
		} catch (IOException e) {
			// consume error
		}
		return this.rootNode;
	}
	
	private void addNode(Path path) {
		if (Files.isDirectory(path) && currentDepth <= maxDepth) {
			
			DirectoryTreeItem leaf = new DirectoryWalker(this, path).read();
			rootNode.getChildren().add(leaf);	
		
		}
	}
	
}
