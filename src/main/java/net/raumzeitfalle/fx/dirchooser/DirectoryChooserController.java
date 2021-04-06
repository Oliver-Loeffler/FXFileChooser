/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;

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
	
	private double iconSize = 32.0;
	
	private Map<Path,Task<Void>> runningUpdateTasks = new ConcurrentHashMap<>();
	
	private final ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		registerShutdownHook();
		String hostName = getHostName();
		root = new DirectoryTreeItem(hostName);
		root.setGraphic(DirectoryIcons.HOST.get(iconSize));
		directoryTree.setRoot(root);
		directoryTree.showRootProperty().set(true);
		root.setExpanded(true);
		
		// TODO: Avoid that the text field states "null" when nothing is selected.
		selectedDirectory.textProperty().bind(selectedDirectoryProperty.asString());
		
		selectedDirectoryProperty.set(null);
		
		okButton.disableProperty().bind(selectedDirectoryProperty.isNull());
		okButton.setOnAction(e -> okayAction());
		
        cancelButton.setOnAction(e -> cancelAction());
        
        this.okButton.setOnKeyPressed(keyEvent->{
        	if (keyEvent.getCode() == KeyCode.ESCAPE) {
        		cancelAction();
        		keyEvent.consume();
        	}
        });
        
        this.cancelButton.setOnKeyPressed(keyEvent->{
        	if (keyEvent.getCode() == KeyCode.ESCAPE) {
        		cancelAction();
        		keyEvent.consume();
        	}
        });
        
        this.selectedDirectory.setOnKeyPressed(keyEvent->{
        	if (keyEvent.getCode() == KeyCode.ESCAPE) {
        		cancelAction();
        		keyEvent.consume();
        	}
        });

        this.directoryTree.setOnKeyPressed(keyEvent->{
        	if (keyEvent.getCode() == KeyCode.RIGHT) {
        		keyEvent.consume();
        		readSubDirsForSelectedItem();
        		expandSelectedItem();
        	}
        	else if (keyEvent.getCode() == KeyCode.ENTER) {
        		okayAction();
        		keyEvent.consume();
        	}
        	else if (keyEvent.getCode() == KeyCode.ESCAPE) {
        		cancelAction();
        		keyEvent.consume();
        	}
        	
        });
        
        
        this.directoryTree.setOnMouseClicked(mouseEvent->{
        	if (mouseEvent.getClickCount() == 2) {
        		mouseEvent.consume();
        		readSubDirsForSelectedItem();
        	}
        });
        
		this.directoryTree.getSelectionModel().selectedItemProperty().addListener((observable,oldItem,newItem)->{
			
			if (null == newItem)
				selectedDirectoryProperty.set(null);
			
			if (null != newItem && null == ((DirectoryTreeItem) newItem).getFullPath())
				selectedDirectoryProperty.set(null);
			
			if (null != newItem && null != ((DirectoryTreeItem) newItem).getFullPath()) {
				DirectoryTreeItem item = (DirectoryTreeItem) newItem;
				
				if (null == item.getFullPath())
					selectedDirectoryProperty.set(null);
				else
					selectedDirectoryProperty.set(Paths.get(item.getFullPath()));
				
				
				readSubDirsForSelectedItem();

			}
		});
		
		initDirTree();
		
		
	}

	private void expandSelectedItem() {
		expandItem(this.directoryTree.getSelectionModel().selectedItemProperty().get());
	}
	
	private void readSubDirsForSelectedItem() {
		DirectoryTreeItem item = (DirectoryTreeItem) this.directoryTree.getSelectionModel().selectedItemProperty().get();

		if (null != item && null != item.getFullPath()) {
			Path path = Paths.get(item.getFullPath());
			if (item.getChildren().isEmpty()) {
				Task<Void> update = runningUpdateTasks.get(path);
						
				if (null == update)
					update = createUpdateTask(path, item);
				
				startUpdate(path, update);
			}
		}	
	}
	
	private void startUpdate(Path path, Task<Void> update) {
		runningUpdateTasks.put(path, update);
		executor.submit(update);		
	}

	private Task<Void> createUpdateTask(Path path, DirectoryTreeItem item) {
		
		Node graphic = item.getGraphic();
		
		SimpleBooleanProperty cancelled = new SimpleBooleanProperty(false);
		
		/*
		 * TODO: Extract the task
		 * TODO: Progress can be determined as per file system entry, 
		 * 		 so that indeterminate state is not needed for update icon.
		 */
		Task<Void> update = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				DirectoryWalker walker = new DirectoryWalker(path);
				List<TreeItem<String>> items = walker.read(cancelled).getChildren();
				item.getChildren().clear();
				item.getChildren().addAll(items);
				return null;
			}
			
		};
			
		update.setOnRunning(event->{
			ProgressIcon progressIcon = new ProgressIcon(32, evt->update.cancel(true));
			Platform.runLater(()->item.setGraphic(progressIcon));
		});
		
		update.setOnSucceeded(event->{
			Platform.runLater(()->{
				item.setGraphic(graphic);
				if (!item.getChildren().isEmpty()) {
					item.setGraphic(DirectoryIcons.OPEN.get(32));
					expandItem(item);
				}
				runningUpdateTasks.remove(path);
			});
		});
		
		update.setOnFailed(event->{
			Platform.runLater(()->item.setGraphic(graphic));
			runningUpdateTasks.remove(path);
		});
		
		update.setOnCancelled(event->{
			cancelled.setValue(true);
			Platform.runLater(()->item.setGraphic(graphic));
			runningUpdateTasks.remove(path);
		});
		
		return update;
	}

	private void expandItem(TreeItem<?> item) {
		if (null != item)
			item.setExpanded(true);
	}

	private void cancelAction() {
		Platform.runLater(onCancel);
	}

	private void okayAction() {
		Platform.runLater(onSelect);
	}

	public void initDirTree() {
		Task<Void> init = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
				for (Path path : rootDirectories) {
					//DirectoryTreeItem dirItem = new DirectoryWalker(path).read();
					DirectoryTreeItem dirItem = new DirectoryTreeItem(path);

					root.getChildren().add(dirItem);
					
					/*
					 * Possible useful API classes and functions:
					 * FileSystemView.getSystemTypeDescription
					 * FileSystemView.getSystemDisplayName
					 * Files.getFileStore
					 * FileStore.getAttribute("volume:isRemovable") 
					 * 
					 */
				}
				
				return null;
			}
			
		};
		
		executor.submit(init);
		
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
	
	private void registerShutdownHook() {
		Runnable shutDownAction = () -> Platform.runLater(()->executor.shutdown());
		Thread shutdownThread = new Thread(shutDownAction);
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

}
