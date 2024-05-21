/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2022 Oliver Loeffler, Raumzeitfalle.net
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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

class DirectoryTreeUpdateTask extends Task<Void> {

    private final Path path;
    private final DirectoryTreeItem item;
    private final BooleanProperty cancelled;
    private final Consumer<Path> removable;
    private final Node itemGraphic;
    
    DirectoryTreeUpdateTask(Path path, DirectoryTreeItem item, Consumer<Path> finalizeAction) {
        this.path = path;
        this.item = item;
        this.cancelled = new SimpleBooleanProperty(false);
        this.removable = finalizeAction;
        this.itemGraphic = item.getGraphic();
    }

    @Override
    protected Void call() throws Exception {
        DirectoryWalker walker = new DirectoryWalker(path);
        List<TreeItem<String>> items = walker.read(cancelled).getChildren();
        item.getChildren().clear();
        item.getChildren().addAll(items);
        return null;
    }

    @Override
    protected void running() {
        super.running();
        ProgressIcon progressIcon = new ProgressIcon(32, evt -> cancel(true));
        Platform.runLater(() -> item.setGraphic(progressIcon));
    }

    @Override
    protected void cancelled() {
        Logger.getLogger(DirectoryTreeUpdateTask.class.getName()).log(Level.INFO, "contents discovery in {0}", path);
        super.cancelled();
        cancelled.setValue(true);
        removable.accept(path);
        Platform.runLater(() -> item.setGraphic(itemGraphic));
    }

    @Override
    protected void failed() {
        super.failed();
        removable.accept(path);
        Platform.runLater(() -> item.setGraphic(itemGraphic));
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        Platform.runLater(() -> {
            item.setGraphic(itemGraphic);
            if (!item.getChildren().isEmpty() && item.isDirectory()) {
                item.setExpanded(true);
                item.configureIcon();
            }
            removable.accept(path);
        });
    }
}
