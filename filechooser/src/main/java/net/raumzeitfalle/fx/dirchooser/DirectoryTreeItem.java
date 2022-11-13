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
import java.nio.file.Path;

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

    private boolean isDrive;

    private int size = 0;

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
        this.isDrive = determineIfIsDrive();
        String[] contents = new File(fullPath).list();
        if (contents != null) {
            size = contents.length;
        }
        configureIcon();

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

    public void configureIcon() {
        if (isExpanded()) {
            configureExpandedIcon();
        } else {
            configureCollapsedIcon();
        }
    }

    private void configureCollapsedIcon() {
        if (isDrive && !getChildren().isEmpty()) {
            this.setGraphic(DirectoryIcons.DRIVE_PLUS.get());
        } else if (isDrive && size == 0) {
            this.setGraphic(DirectoryIcons.DRIVE_EMPTY.get());
        } else if (isDrive && size > 1000) {
            this.setGraphic(DirectoryIcons.DRIVE_XL.get());
        } else if (isDrive) {
            this.setGraphic(DirectoryIcons.DRIVE.get());
        } else if (getChildren().isEmpty() && size > 1000) {
            this.setGraphic(DirectoryIcons.NO_SUBDIRS_XL.get());
        } else if (size == 0) {
            this.setGraphic(DirectoryIcons.EMPTY.get());
        } else if (getChildren().size() > 1000) {
            this.setGraphic(DirectoryIcons.CLOSED_XL.get());
        } else if (!getChildren().isEmpty()) {
            this.setGraphic(DirectoryIcons.CLOSED_PLUS.get());
        } else {
            this.setGraphic(DirectoryIcons.CLOSED.get());
        }
    }

    private void configureExpandedIcon() {
        if (!isDrive) {
            this.setGraphic(DirectoryIcons.OPEN.get());
        }
    }

    public boolean isDrive() {
        return this.isDrive;
    }

    private boolean determineIfIsDrive() {
        if (fullPath.length() != 3) {
            return false;
        }
        if (!Character.isAlphabetic(fullPath.charAt(0))) {
            return false;
        }
        if (fullPath.charAt(1) != ':') {
            return false;
        }
        return fullPath.charAt(2) == '\\' || fullPath.charAt(2) == '/';
    }

    private void handleExpansion(Event e) {
        DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
        item.configureIcon();
    }

    private void handleCollapse(Event e) {
        DirectoryTreeItem item = (DirectoryTreeItem) e.getSource();
        if (null != item) {
            item.configureIcon();
        }
    }

    boolean isHuge() {
        return size > 1000L;
    }

    public int size() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
