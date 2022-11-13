/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
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
import java.io.File;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private Button chooseFiles;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField goToTextField;

    private ObjectProperty<Path> selectedDirectoryProperty = new SimpleObjectProperty<Path>(null);

    private DirectoryTreeItem root;

    private DirectoryTreeItem localRoot;

    private DirectoryTreeItem networkRoot;

    private Runnable onSelect;

    private Runnable onCancel;

    private Map<Path, Task<Void>> runningUpdateTasks = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private boolean dontExpandOnSelect;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerShutdownHook();
        String hostName = getHostName();
        root = new DirectoryTreeItem("root");
        localRoot = new DirectoryTreeItem(hostName);
        localRoot.setGraphic(DirectoryIcons.HOST.get());

        networkRoot = new DirectoryTreeItem("Network");
        networkRoot.setGraphic(DirectoryIcons.HOST.get());

        root.getChildren().add(localRoot);

        directoryTree.setRoot(root);
        directoryTree.showRootProperty().set(false);

        localRoot.setExpanded(true);
        networkRoot.setExpanded(false);

        StringBinding sb = Bindings.createStringBinding(() -> {
            Path selection = selectedDirectoryProperty.get();
            return (selection == null) ? "" : selection.toAbsolutePath().toString();
        }, selectedDirectoryProperty);
        selectedDirectory.textProperty().bind(sb);
        selectedDirectoryProperty.set(null);
        okButton.disableProperty().bind(selectedDirectoryProperty.isNull());
        okButton.setOnAction(e -> okayAction());
        cancelButton.setOnAction(e -> cancelAction());
        goToTextField.setOnAction(this::handleGotoAction);
        this.goToTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleGotoAction(null);
                keyEvent.consume();
            }
        });
        
        chooseFiles.setOnAction(e->{
            if (okButton.isDisable()) {
                cancelAction();
            } else {
                okayAction();
            }
        });

        this.okButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelAction();
                keyEvent.consume();
            }
        });

        this.cancelButton.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelAction();
                keyEvent.consume();
            }
        });

        this.selectedDirectory.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelAction();
                keyEvent.consume();
            }
        });

        this.directoryTree.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                keyEvent.consume();
                readSubDirsForSelectedItem();
                expandSelectedItem();
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                okayAction();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelAction();
                keyEvent.consume();
            }
        });

        this.directoryTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                mouseEvent.consume();
                readSubDirsForSelectedItem();
            }
        });

        this.directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {
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
                
                if (!dontExpandOnSelect) {                    
                    readSubDirsForSelectedItem();
                }
                
                goToTextField.textProperty().setValue(item.getFullPath());
            }
        });

        initDirTree();

    }

    private void expandSelectedItem() {
        expandItem(this.directoryTree.getSelectionModel().selectedItemProperty().get());
    }

    private void readSubDirsForSelectedItem() {           
        DirectoryTreeItem item = (DirectoryTreeItem) this.directoryTree
                                                         .getSelectionModel()
                                                         .selectedItemProperty()
                                                         .get();
        if (null != item && item.isHuge()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Huge directory");
            alert.setHeaderText("Scanning for subdirs may take a while!");
            alert.setContentText(String.format("This directory holds %s items.",Integer.toString(item.size())));
            Optional<ButtonType> userResponse = alert.showAndWait();
            if (userResponse.get() == ButtonType.CANCEL) {
                return;
            }
        }
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
        /*
         * TODO: Progress can be determined as per file system entry, so that
         * indeterminate state is not needed for update icon.
         */
        return new DirectoryTreeUpdateTask(path, item, runningUpdateTasks::remove);
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
                Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
                for (Path path : rootDirectories) {
                    // DirectoryTreeItem dirItem = new DirectoryWalker(path).read();
                    DirectoryTreeItem dirItem = new DirectoryTreeItem(path);
                    localRoot.getChildren().add(dirItem);
                    /*
                     * Possible useful API classes and functions:
                     * FileSystemView.getSystemTypeDescription FileSystemView.getSystemDisplayName
                     * Files.getFileStore FileStore.getAttribute("volume:isRemovable")
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

    void shutdown() {
        Logger.getLogger(DirectoryChooserController.class.getName()).log(Level.INFO,
                "shutting down tasks and executors");
        executor.shutdownNow();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void handleGotoAction(ActionEvent event) {
        File possibleLocation = getPathFromText();
        if (possibleLocation.exists()) {
            Path location = possibleLocation.toPath();
            DirectoryTreeItem share = new DirectoryTreeItem(location);
            share.setGraphic(DirectoryIcons.HOST.get());
            updateSharesIfNeeded(share);
            scrollToItem(share);
        }
    }

    private void scrollToItem(DirectoryTreeItem share) {
        int index = directoryTree.getRow(share);
        directoryTree.scrollTo(index);
        Platform.runLater(()->{
            int i = directoryTree.getRow(share);
            directoryTree.scrollTo(i);
        });
    }

    /*
     * TODO: When a network location is added, group all directories which belong to
     * the same "share" below the "shares" node.
     */
    private void updateSharesIfNeeded(DirectoryTreeItem share) {
        List<TreeItem<String>> knownShares = networkRoot.getChildren();
        Optional<DirectoryTreeItem> optionalShare = knownShares.stream()
                .filter(h -> h.getValue().equalsIgnoreCase(share.getFullPath())).map(i -> (DirectoryTreeItem) i)
                .findAny();
        if (!optionalShare.isPresent()) {
            Path path = Paths.get(share.getFullPath());
            File file = path.getRoot().toFile();
            FileSystemView fsView = FileSystemView.getFileSystemView();
            boolean isFsRoot = fsView.isFileSystemRoot(file);
            if (!isFsRoot) {
                Platform.runLater(() -> {
                    if (!root.getChildren().contains(networkRoot)) {
                        root.getChildren().add(networkRoot);
                    }
                    localRoot.setExpanded(false);
                    networkRoot.getChildren().add(share);
                    networkRoot.setExpanded(true);
                });
            } else {
                expandTreeFor(path);
            }
        }
    }

    private void expandTreeFor(Path path) {
        System.out.println("expanding for: " + path);
        collapseAll();
        Platform.runLater(() -> {
            localRoot.setExpanded(false);
            networkRoot.setExpanded(false);
            root.setExpanded(false);
            DirectoryTreeItem item = expandAll(path, 0, localRoot);
            if (item != null) {               
                Platform.runLater(()->{
                    selectButNotExpand(item);
                    scrollToItem(item);
                });
            }
        });
    }

    private void selectButNotExpand(DirectoryTreeItem item) {
        dontExpandOnSelect = true;
        directoryTree.getSelectionModel().select(item);
        dontExpandOnSelect = false;
    }

    private void collapseAll() {
        collapse(root);
    }

    private void collapse(TreeItem<String> treeItem) {
        for (TreeItem<String> d : treeItem.getChildren()) {
            if (d.isExpanded()) {
                d.setExpanded(false);
                collapse(d);
            }
        }
    }

    private DirectoryTreeItem expandAll(Path path, int depth, DirectoryTreeItem treeItem) {
        Path full = resolvePath(path, depth);
        for (TreeItem<String> d : treeItem.getChildren()) {
            DirectoryTreeItem child = (DirectoryTreeItem) d;
            Path other = Paths.get(child.getFullPath());
            if (full.equals(other)) {
                child.setExpanded(true);
                if (depth < path.getNameCount()) {
                    return expandAll(path, depth += 1, child);
                }
                return child;
            }
        }
        return null;
    }

    private Path resolvePath(Path path, int depth) {
        if (depth == 0) {
            return path.getRoot();
        }
        return path.getRoot().resolve(path.subpath(0, depth));
    }

    private File getPathFromText() {
        String value = goToTextField.getText().replace("\"", "");
        if (value.length() == 2 && value.charAt(1) == ':') {
            return new File(value+"\\");
        }
        return new File(value);
    }
}
