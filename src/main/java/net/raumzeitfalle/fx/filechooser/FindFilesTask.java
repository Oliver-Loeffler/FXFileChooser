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

import java.io.File;
import java.nio.file.Path;


import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FindFilesTask extends Task<Void>{
    
    private final ObservableList<IndexedPath> pathsToUpdate;
    
    private final Path directory;
    
    public FindFilesTask(Path searchFolder, ObservableList<IndexedPath> listOfPaths) {
        this.pathsToUpdate = listOfPaths;
        this.directory = searchFolder;
    }

    @Override
    protected Void call() throws Exception {
            Invoke.andWait(pathsToUpdate::clear);
            
            File dir = new File(directory.toAbsolutePath().toString());
            File[] files = dir.listFiles();
            if (null == files) {
            		files = new File[0];
            }
            updateProgress(0, files.length);
            
            int cacheSize = determineCacheSize(files);
            
            RefreshBuffer buffer = RefreshBuffer.get(this,cacheSize, pathsToUpdate);
            
            for (int f = 0; f < files.length; f++) {
                if (isCancelled()) {
                    break;
                }
                updateProgress(f+1, files.length);
                if (!files[f].isDirectory() && files[f].exists()) {
                    buffer.update(files[f].toPath());
                }
            }
            buffer.flush();
            updateProgress(files.length, files.length);
          
        return null;
    }

    private int determineCacheSize(File[] files) {
        int items = files.length;
        if (items > 100_000) {
            return 500;
        }
        if (items > 50_000) {
            return 200;
        }
        if (items > 15_000) {
            return 100;
        }
        if (items > 5_000) {
            return 50;
        }
        if (items > 1_000) {
            return 20;
        }
        return 10;
    }
}
