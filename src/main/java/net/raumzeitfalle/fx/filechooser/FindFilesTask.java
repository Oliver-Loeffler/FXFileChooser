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
import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

final class FindFilesTask extends Task<Integer>{
    
    private final ObservableList<IndexedPath> pathsToUpdate;
    
    private final Path directory;
    
    public FindFilesTask(Path searchFolder, ObservableList<IndexedPath> listOfPaths) {
        this.pathsToUpdate = Objects.requireNonNull(listOfPaths, "listOfPaths must not be null");
        this.directory = searchFolder;
    }

    /**
     * Even in case the directory to be processed is empty or does not exist, 
     * the consumer collection is always cleared as first step. 
     * 
     * @return number of files found and processed
     */
    @Override
    protected Integer call() throws Exception {
    	Invoke.andWait(pathsToUpdate::clear);
    	
    	if (null == directory)
    		return 0;
        
        File[] files = directory.toAbsolutePath().toFile().listFiles();
        if (null == files) 
        	return 0;
        
        updateProgress(0, files.length);
        
        RefreshBuffer buffer = RefreshBuffer.get(this,files.length, pathsToUpdate);
        for (int f = 0; f < files.length; f++) {
            if (isCancelled()) 
                break;
            
            updateProgress(f+1, files.length);
            if (!files[f].isDirectory() && files[f].exists()) 
                buffer.update(files[f].toPath());
            
        }
        buffer.flush();
        updateProgress(files.length, files.length);
          
        return files.length;
    }

}
