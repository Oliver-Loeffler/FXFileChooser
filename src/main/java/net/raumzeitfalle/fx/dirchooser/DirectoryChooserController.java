package net.raumzeitfalle.fx.dirchooser;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.*;

public class DirectoryChooserController implements Initializable {
	
	@FXML
    private TextField selectedDirectory;
	
	@FXML
	private TreeView<String> directoryTree;
	
	public DirectoryChooserController(DirectoryChooserModel model) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		String hostName = getHostName();
		DirectoryTreeItem root = new DirectoryTreeItem(hostName);
		this.directoryTree.setRoot(root);
		this.directoryTree.showRootProperty().set(true);
		root.setExpanded(true);
		
		Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
		for (Path path : rootDirectories) {
			DirectoryTreeItem dirItem = new DirectoryWalker(path).read();
			root.getChildren().add(dirItem);	
		}
		
		this.directoryTree.getSelectionModel().selectedItemProperty().addListener((observable,oldItem,newItem)->{
			
			if (null != newItem) {
				DirectoryTreeItem item = (DirectoryTreeItem) newItem;
				selectedDirectory.setText(item.getFullPath());
			}
		});
		
		
		
		
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
