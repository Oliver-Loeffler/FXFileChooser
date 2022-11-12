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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.event.Event;
import javafx.scene.control.TreeItem;

/*
 * TODO: if an item has been updated is currently checked using getChildren, 
 *       but for some large directories with many files, this is the wrong 
 *       approach as the update is repeated over and over.
 *       
 * TODO: Tree updates shall be performed upon request. Refresh button is needed.
 * TODO: Tree shall be updated as lazy and late as possible.
 * 
 */
public class DirectoryTreeItem extends TreeItem<String> {

    private String fullPath;

    public String getFullPath() {
        return (this.fullPath);
    }

    private boolean isDirectory;

    private double iconSize = 32.0;

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
        if (subDirs > 0) {
            this.setGraphic(DirectoryIcons.CLOSED_PLUS.get(iconSize));
        } else {
            this.setGraphic(DirectoryIcons.CLOSED.get(iconSize));
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
            List<Path> subDirs = paths.filter(p -> p.toFile().isDirectory()).collect(Collectors.toList());
            return subDirs.size();
        } catch (IOException e) {
            return 0;
        }
    }

    private void handleExpansion(Event e) {
        DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
        if (null != item) {
            item.setGraphic(DirectoryIcons.OPEN.get(iconSize));
        }
    }

    private void handleCollapse(Event e) {
        DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
        if (null != item) {
            if (item.getChildren().isEmpty()) {
                this.setGraphic(DirectoryIcons.CLOSED.get(iconSize));
            } else {
                this.setGraphic(DirectoryIcons.OPEN.get(iconSize));
            }
        }
    }

}
