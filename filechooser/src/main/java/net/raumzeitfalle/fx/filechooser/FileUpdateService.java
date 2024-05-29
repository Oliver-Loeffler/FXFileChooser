/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FileUpdateService extends javafx.concurrent.Service<Integer> implements UpdateService {

    private static final Logger LOGGER = Logger.getLogger(FileUpdateService.class.getName()); 
    
    private ObjectProperty<Path> rootFolder = new SimpleObjectProperty<>();

    private ObservableList<IndexedPath> pathsToUpdate;

    private Thread shutdownThread = null;

    public FileUpdateService(Path folderToStart, ObservableList<IndexedPath> paths) {
        setSearchLocation(folderToStart);
        assignTargetCollection(paths);
        registerShutdownHook();
    }

    private void assignTargetCollection(ObservableList<IndexedPath> paths) {
        pathsToUpdate = Objects.requireNonNull(paths, "Target collection paths must not be null");
    }

    private void setSearchLocation(Path folderToStart) {
        rootFolder.setValue(obtainDirectory(folderToStart));
    }

    private Path obtainDirectory(Path folderToStart) {
        if (null == folderToStart)
            return null;

        if (folderToStart.toFile().isDirectory())
            return folderToStart;
        else
            return folderToStart.getParent();
    }

    @Override
    protected Task<Integer> createTask() {
        return new FindFilesTask(rootFolder.getValue(), pathsToUpdate);
    }

    @Override
    public void restartIn(Path directory) {
        if (null != directory)
            restartInDirectory(directory);
    }

    @Override
    public ObjectProperty<Path> searchPathProperty() {
        return this.rootFolder;
    }

    @Override
    public void refresh() {
        this.restart();
    }

    @Override
    public void cancelUpdate() {
        this.cancel();
    }

    @Override
    public void startUpdate() {
        this.start();
    }

    private void restartInDirectory(Path directory) {
        if (directory.toFile().isDirectory())
            refreshWhenExists(directory);
        else
            attemptRefreshUsingParent(directory);

    }

    private void attemptRefreshUsingParent(Path directory) {
        Path parent = directory.getParent();
        if (null != parent) {
            refreshWhenExists(parent);
        } else {
            LOGGER.log(Level.WARNING, "Not existing location: {0}", directory);
        }
    }

    protected void refreshWhenExists(Path location) {
        if (location.toFile().exists()) {
            setLocationAndRefresh(location);
        } else {
            LOGGER.log(Level.WARNING, "Not existing location: {0}", location);
        }
    }

    private void setLocationAndRefresh(Path location) {
        setSearchLocation(location);
        this.refresh();
    }

    private void registerShutdownHook() {
        Runnable shutDownAction = () -> Platform.runLater(this::cancelUpdate);
        shutdownThread = new Thread(shutDownAction);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    protected Thread getShutdownThread() {
        return this.shutdownThread;
    }
    
    /**
     * Polls the service running state and waits until service changes its state.
     * This is inly intended to be used for debugging.
     */
    public void waitUntilFinished() {
        Invoke.andWaitWithoutException(() -> {
            long start = System.currentTimeMillis();
            while (isRunning()) {
                if (start % 1000 == 0) {
                    Logger.getLogger(getClass().getName())
                          .log(Level.INFO, "Waiting for service to finish....");
                }
            }
        }, 2000);
    }

}
