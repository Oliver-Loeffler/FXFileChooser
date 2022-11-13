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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;

public class FXFileChooserStage2Test extends ApplicationTest {

    protected FXFileChooserStage stageUnderTest;

    @Override
    public void start(Stage stage) {
        stageUnderTest = FXFileChooserStage.create(Skin.DARK, PathFilter.create("only-text", p -> p.toString().endsWith(".txt")),
                PathFilter.create("only-html", p -> p.toString().endsWith(".html")));
        stage = stageUnderTest;
    }

    @Test
    void that_adding_multiple_custom_filters_works() {
        FileChooserModel model = stageUnderTest.getModel();
        assertEquals(2, model.getPathFilter().size());

        PathFilter first = model.getPathFilter().get(0);
        assertEquals("only-text", first.getName());
        assertTrue(first.getPredicate().test("my-special-file.txt"));
        assertFalse(first.getPredicate().test("my-other-file.html"));

        PathFilter second = model.getPathFilter().get(1);
        assertEquals("only-html", second.getName());
        assertFalse(second.getPredicate().test("my-special-file.txt"));
        assertTrue(second.getPredicate().test("my-other-file.html"));
    }

}
