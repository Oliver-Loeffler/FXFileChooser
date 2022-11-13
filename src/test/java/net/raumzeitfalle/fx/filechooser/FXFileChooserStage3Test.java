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
package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;


public class FXFileChooserStage3Test extends ApplicationTest {

    @TempDir
    public static Path tempDir;
    
    protected FXFileChooserStage stageUnderTest;
    
    @Override
    public void start(Stage stage) {
        stageUnderTest = FXFileChooserStage.create(Skin.DARK, tempDir);
        stage = stageUnderTest;
    }

    @Test
    void that_pathfilter_defaults_are_used_when_configuring_only_directory() {
        FileChooserModel model = stageUnderTest.getModel();
        List<PathFilter> filter = model.getPathFilter();
        assertEquals(1, filter.size());
        assertEquals("*.*", filter.get(0).getName());
        assertTrue(filter.get(0).getPredicate().test("anyThing"));
        assertTrue(filter.get(0).getPredicate().test("anyThing.else"));
        assertTrue(filter.get(0).getPredicate().test("anyThing.txt"));
        assertTrue(filter.get(0).getPredicate().test("*.*&%%"));
        assertEquals(tempDir, model.currentSearchPath().get());
    }

}
