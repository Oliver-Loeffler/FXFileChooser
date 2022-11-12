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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

class FileChooserModelFactoryMethodsFxTest extends FxTestTemplate {

    private FileChooserModel classUnderTest;

    private Path testRoot = Paths.get("./TestData/SomeFiles");

    @Override
    public void init() throws Exception {
        classUnderTest = FileChooserModel.startingInUsersHome();
    }

    @Test
    void startingInUsersHome() {
        Path searchPath = classUnderTest.currentSearchPath().get();
        Path usersHome = Paths.get(System.getProperty("user.home"));

        assertEquals(usersHome.toAbsolutePath(), searchPath.toAbsolutePath());
    }

    @Test
    void changingDirectory_updateFilesIn() throws InterruptedException, ExecutionException {
        assertNotEquals(testRoot.isAbsolute(),
                classUnderTest.currentSearchPath()
                              .get()
                              .toAbsolutePath(),
                "search path before directory change");

        // Consider moving the service into the controller out of the model
        Invoke.andWait(() -> classUnderTest.getUpdateService().restartIn(testRoot));

        assertEquals(testRoot.toAbsolutePath(),
                classUnderTest.currentSearchPath()
                              .get()
                              .toAbsolutePath(),
                "search path after directory change");
    }

}
