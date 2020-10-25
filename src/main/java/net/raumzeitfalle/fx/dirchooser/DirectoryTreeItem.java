package net.raumzeitfalle.fx.dirchooser;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.*;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class DirectoryTreeItem extends TreeItem<String> {
	
	private static final String EMPTY_FOLDER = "M464 128H272l-64-64H48C21.49 64 0 85.49 0 112v288c0 26.51 21.49 48 48 48h416c26.51 0 48-21.49 48-48V176c0-26.51-21.49-48-48-48z";
	
	private static final String FOLDER_WITH_CONTENT = "M464,128H272L208,64H48A48,48,0,0,0,0,112V400a48,48,0,0,0,48,48H464a48,48,0,0,0,48-48V176A48,48,0,0,0,464,128ZM359.5,296a16,16,0,0,1-16,16h-64v64a16,16,0,0,1-16,16h-16a16,16,0,0,1-16-16V312h-64a16,16,0,0,1-16-16V280a16,16,0,0,1,16-16h64V200a16,16,0,0,1,16-16h16a16,16,0,0,1,16,16v64h64a16,16,0,0,1,16,16Z";
	
	private static final String FOLDER_OPEN = "M572.694 292.093L500.27 416.248A63.997 63.997 0 0 1 444.989 448H45.025c-18.523 0-30.064-20.093-20.731-36.093l72.424-124.155A64 64 0 0 1 152 256h399.964c18.523 0 30.064 20.093 20.73 36.093zM152 224h328v-48c0-26.51-21.49-48-48-48H272l-64-64H48C21.49 64 0 85.49 0 112v278.046l69.077-118.418C86.214 242.25 117.989 224 152 224z";
		
	
	// this stores the full path to the file or directory
	private String fullPath;

	public String getFullPath() {
		return (this.fullPath);
	}

	private boolean isDirectory;

	public boolean isDirectory() {
		return (this.isDirectory);
	}
	
	public DirectoryTreeItem(String root) {
		super(root);
	}

	public DirectoryTreeItem(Path file) {
		super(file.toString());
		this.fullPath = file.toString();
		this.isDirectory = file.toFile().isDirectory();
		
		Path path = Paths.get(this.fullPath);
		
		long subDirs = countSubDirs(path);
		
		if (subDirs > 1) {
			this.setGraphic(newIcon(FOLDER_WITH_CONTENT));						
		} else {
			this.setGraphic(newIcon(EMPTY_FOLDER));
		}
		

		if (!fullPath.endsWith(File.separator)) {
			String value = file.toString();
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > -1) {
				this.setValue(value.substring(indexOf + 1));
			} else {
				this.setValue(value);
			}
		}
		
		this.addEventHandler(TreeItem.branchExpandedEvent(), this::handleExpansion);
		this.addEventHandler(TreeItem.branchCollapsedEvent(), this::handleCollapse);

	}
	
	private long countSubDirs(Path path) {
		try (Stream<Path> paths = Files.list(path)) {
			List<Path> subDirs = paths.filter(p->p.toFile().isDirectory()).collect(Collectors.toList()); 
			return subDirs.size();
		} catch (IOException e) {			
			return 0;
		}
	}

	private static StackPane newIcon(String folder) {
		double witdh = 20.0;
		double height = 16.0;
		StackPane pane = new StackPane();
		pane.setMinSize(witdh, height);
		pane.setPrefSize(witdh, height);
		pane.setMaxSize(witdh, height);
		
		SVGPath path = new SVGPath();
		path.setContent(folder);
		path.setScaleX(0.03);
		path.setScaleY(0.03);

		pane.getChildren().add(path);
		
		return pane;
	}
	
	private void handleExpansion(Event e) {
		DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
		if (null != item) {
			item.setGraphic(newIcon(FOLDER_OPEN));
			Path path = Paths.get(item.getFullPath());
			item.getChildren().clear();
			
			item.getChildren().addAll(new DirectoryWalker(path, 1).read().getChildren());
		}
	}
	
	private void handleCollapse(Event e) {
		DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
		if (null != item) {
			if (item.getChildren().isEmpty()) {
				item.setGraphic(newIcon(EMPTY_FOLDER));
			} else {
				item.setGraphic(newIcon(FOLDER_WITH_CONTENT));
				for (TreeItem<String> child : item.getChildren()) {
					child.getChildren().clear();
				}
			}
		}
	}

}
