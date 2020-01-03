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

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FileUpdateService extends javafx.concurrent.Service<Void> implements UpdateService {
     
    private ObjectProperty<Path> rootFolder = new SimpleObjectProperty<>();
    
    private ObservableList<IndexedPath> pathsToUpdate;

    public FileUpdateService(Path folderToStart, ObservableList<IndexedPath> paths) {
        setSearchLocation(folderToStart);        
        this.pathsToUpdate = paths;
        registerShutdownHook();
    }

    private void setSearchLocation(Path folderToStart) {
        if (folderToStart.toFile().isDirectory()) {
            this.rootFolder.setValue(folderToStart);
        } else {
            this.rootFolder.setValue(folderToStart.getParent());
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new FindFilesTask(rootFolder.getValue(), pathsToUpdate);
    }

    @Override
    public void restartIn(Path location) {
        setSearchLocation(location);
        this.refresh();
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
    
	private void registerShutdownHook() {
		Runnable shutDownAction = ()->Platform.runLater(this::cancelUpdate);
        Runtime.getRuntime().addShutdownHook(new Thread(shutDownAction));
	}

}
