package net.raumzeitfalle.fx.dirchooser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

public class DirectoryChooserController implements Initializable {
	
	@FXML
    private TextField selectedDirectory;
	
	@FXML
	private TreeView<String> directoryTree;
	
	@FXML
    private Button okButton;
    
    @FXML
    private Button cancelButton;
	
	private ObjectProperty<Path> selectedDirectoryProperty = new SimpleObjectProperty<Path>(null);
	
	private DirectoryTreeItem root;

	private Runnable onSelect;

	private Runnable onCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		String hostName = getHostName();
		root = new DirectoryTreeItem(hostName);
		this.directoryTree.setRoot(root);
		this.directoryTree.showRootProperty().set(true);
		root.setExpanded(true);
		
		selectedDirectory.textProperty().bind(selectedDirectoryProperty.asString());
		
		selectedDirectoryProperty.set(null);
		
		okButton.setOnAction(e -> okayAction());
		
        cancelButton.setOnAction(e -> cancelAction());
		
        this.directoryTree.setOnKeyPressed(keyEvent->{
        	if (keyEvent.getCode() == KeyCode.RIGHT) {
        		readSubDirsForSelectedItem(true);        		
        	}
        	else if (keyEvent.getCode() == KeyCode.ENTER) {
        		okayAction();
        	}
        	else if (keyEvent.getCode() == KeyCode.ESCAPE) {
        		cancelAction();
        	}
        });
        
        
        this.directoryTree.setOnMouseClicked(mouseEvent->{
        	if (mouseEvent.getClickCount() == 1)
        		readSubDirsForSelectedItem(false);
        	else if (mouseEvent.getClickCount() == 2)
        		readSubDirsForSelectedItem(true);
        });
        
		this.directoryTree.getSelectionModel().selectedItemProperty().addListener((observable,oldItem,newItem)->{
			
			if (null != newItem && null != ((DirectoryTreeItem) newItem).getFullPath()) {
				DirectoryTreeItem item = (DirectoryTreeItem) newItem;
				
				if (null == item.getFullPath())
					selectedDirectoryProperty.set(null);
				else
					selectedDirectoryProperty.set(Paths.get(item.getFullPath()));
				
				
				readSubDirsForSelectedItem(false);

			}
		});
		
		
		
		
	}
	
	private void readSubDirsForSelectedItem(boolean expandNode) {
		DirectoryTreeItem item = (DirectoryTreeItem) this.directoryTree.getSelectionModel().selectedItemProperty().get();
		
		if (null != item) {
			if (null == item.getFullPath()) {
				item.setExpanded(expandNode);
			} else {
				Path path = Paths.get(item.getFullPath());
				
				Node graphic = item.getGraphic();
				
				if (item.getChildren().isEmpty()) {
					Task<Void> update = new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							List<TreeItem<String>> items = new DirectoryWalker(path).read().getChildren();
							item.getChildren().clear();
							item.getChildren().addAll(items);
							return null;
						}
						
					};
					
					update.setOnRunning(event->{
						ProgressBar progress = new ProgressBar();
						progress.setProgress(-1);
						progress.setMaxSize(32, 12);
						progress.setPrefSize(32, 12);
						Button cancel = new Button("X");
						cancel.setOnAction(evt->update.cancel(true));
						HBox box = new HBox(progress, cancel);
						Platform.runLater(()->item.setGraphic(box));
					});
					
					update.setOnSucceeded(event->{
						Platform.runLater(()->{
							item.setGraphic(graphic);
							item.setExpanded(expandNode);
						});
					});
					
					update.setOnFailed(event->{
						Platform.runLater(()->item.setGraphic(graphic));
					});
					
					update.setOnCancelled(event->{
						Platform.runLater(()->item.setGraphic(graphic));
					});
					
					Executors.newCachedThreadPool().submit(update);
				}
			}	
		}
	}
	
	private void cancelAction() {
		Platform.runLater(onCancel);
	}

	private void okayAction() {
		Platform.runLater(onSelect);
	}

	public void initDirTree() {
		Runnable treeInit = ()->{
			Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
			for (Path path : rootDirectories) {
				DirectoryTreeItem dirItem = new DirectoryWalker(path).read();
				root.getChildren().add(dirItem);	
			}			
		};
		Thread thread = new Thread(treeInit);
		thread.start();
	}

	private String getHostName() {
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			return localhost.getHostName();
			
		} catch (UnknownHostException e1) {
			// ignore here and try again
		}
		
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("hostname");
			InputStreamReader in = new InputStreamReader(process.getInputStream());
			BufferedReader reader = new BufferedReader(in);
			return reader.readLine();
			
		} catch (IOException e) {
			
			return "Computer";
			
		}
		
	}

	public ReadOnlyObjectProperty<Path> selectedDirectoryProperty() {
		return selectedDirectoryProperty;
	}

	public void setOnSelect(Runnable action) {
		this.onSelect = action;
	}

	public void setOnCancel(Runnable action) {
		this.onCancel = action;
	}

}
