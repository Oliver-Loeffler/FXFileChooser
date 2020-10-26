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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

public class DirectoryChooserController implements Initializable {
	
	@FXML
    private TextField selectedDirectory;
	
	@FXML
	private TreeView<String> directoryTree;
	
	private DirectoryTreeItem root;
	
	public DirectoryChooserController(DirectoryChooserModel model) {
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		String hostName = getHostName();
		root = new DirectoryTreeItem(hostName);
		this.directoryTree.setRoot(root);
		this.directoryTree.showRootProperty().set(true);
		root.setExpanded(true);
		
		
		
		this.directoryTree.getSelectionModel().selectedItemProperty().addListener((observable,oldItem,newItem)->{
			
			if (null != newItem) {
				DirectoryTreeItem item = (DirectoryTreeItem) newItem;
				selectedDirectory.setText(item.getFullPath());
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
							item.setExpanded(false);
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
		});
		
		
		
		
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

}
