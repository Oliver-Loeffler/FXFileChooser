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
package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import net.raumzeitfalle.fx.filechooser.ServiceWorkerStateListener.ServiceExecResult;

class FileUpdateServiceTest extends FxTestTemplate {

    public static @TempDir Path emptyTemporaryFolder;

    private ObservableList<IndexedPath> paths = FXCollections.observableArrayList();

    private FileUpdateService serviceUnderTest = new FileUpdateService(emptyTemporaryFolder, paths);

    @Test
    void runningInEmptyDirectory() throws Exception {

        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);

        serviceUnderTest.startUpdate();

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.SUCCEEDED, serviceExecResult.getServiceState());
        assertEquals(0, serviceExecResult.getResult(), "# of items in directory to be processed");
        assertEquals(0, paths.size(), "# of files collected");

        assertEquals(emptyTemporaryFolder, serviceUnderTest.searchPathProperty().getValue());

    }

    @Test
    void restartIn_directory() throws Exception {

        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);

        Path directoryWithTestFiles = Paths.get("TestData/SomeFiles");
        serviceUnderTest.restartIn(directoryWithTestFiles);

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.SUCCEEDED, serviceExecResult.getServiceState());
        assertEquals(12, serviceExecResult.getResult(), "# of items in directory to be processed");
        assertEquals(11, paths.size(), "# of files collected");

        assertEquals(directoryWithTestFiles, serviceUnderTest.searchPathProperty().getValue());

    }

    @Test
    void cancelUpdate() throws Exception {

        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);
        serviceUnderTest.cancelUpdate();

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.CANCELLED, serviceExecResult.getServiceState());
        assertEquals(null, serviceExecResult.getResult(), "# of items in directory to be processed");

    }

    @Test
    void restartIn_directory_withFileName() throws Exception {

        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);

        Path directoryWithTestFiles = Paths.get("TestData/SomeFiles/TestFile4.txt");
        serviceUnderTest.restartIn(directoryWithTestFiles);

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.SUCCEEDED, serviceExecResult.getServiceState());
        assertEquals(12, serviceExecResult.getResult(), "# of items in directory to be processed");
        assertEquals(11, paths.size(), "# of files collected");

    }

    @Test
    void restartIn_noTexistingDirectory() throws Exception {

        Path notExistingDir = Paths.get("AnotherFolder/ThisDirDoesNotExist/");
        serviceUnderTest.restartIn(notExistingDir);

        assertEquals(Worker.State.READY, serviceUnderTest.stateProperty().get());

    }

    @Test
    void restartIn_notExistingDirectoryButParentExists() throws Exception {

        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);

        Path directoryWithExistingParent = Paths.get("TestData/SomeFiles/NotExistingDir/");
        serviceUnderTest.restartIn(directoryWithExistingParent);

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.SUCCEEDED, serviceExecResult.getServiceState());
        assertEquals(12, serviceExecResult.getResult(), "# of items in directory to be processed");
        assertEquals(11, paths.size(), "# of files collected");

    }

    @Test
    void restartIn_notExistingDirectory_withoutParent() throws Exception {

        Path directoryWithoutParent = Paths.get("//server/share/");
        serviceUnderTest.restartIn(directoryWithoutParent);

        assertEquals(Worker.State.READY, serviceUnderTest.stateProperty().get());

    }

    @Test
    void restartIn_withNull_doesNotTriggerTheService() throws Exception {

        serviceUnderTest.restartIn(null);

        assertEquals(Worker.State.READY, serviceUnderTest.stateProperty().get());
    }

    @Test
    void createWithNullSearchPath() throws Exception {

        serviceUnderTest = new FileUpdateService(null, paths);

        assertEquals(Worker.State.READY, serviceUnderTest.stateProperty().get());
    }

    @Test
    void createWithFileAsSearchPath() throws Exception {

        Path file = Paths.get("TestData/SomeFiles/TestFile4.txt");
        serviceUnderTest = new FileUpdateService(file, paths);
        ServiceWorkerStateListener<Integer> stateListener = ServiceWorkerStateListener.with(serviceUnderTest);

        serviceUnderTest.start();

        ServiceExecResult<Integer> serviceExecResult = stateListener.getServiceResult().get(60, TimeUnit.SECONDS);

        assertEquals(Worker.State.SUCCEEDED, serviceExecResult.getServiceState());
        assertEquals(12, serviceExecResult.getResult(), "# of items in directory to be processed");
        assertEquals(11, paths.size(), "# of files collected");

    }

    @Test
    void removeShutDownHook() throws Exception {

        FileUpdateService serviceUnderTest = new FileUpdateService(Paths.get("./"),
                FXCollections.observableArrayList());

        boolean wasRemovedProperly = Runtime.getRuntime().removeShutdownHook(serviceUnderTest.getShutdownThread());

        /*
         * 
         * Will only work if the service previously properly registered its hook.
         * 
         */
        assertTrue(wasRemovedProperly);

    }

}
