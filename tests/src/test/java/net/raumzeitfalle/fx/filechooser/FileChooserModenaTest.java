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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.Parent;

class FileChooserModenaTest extends FileChooserTestBase {

    @TempDir
    public static Path emptyDirectory;

    public static Path withFiles = Paths.get("TestData/SomeFiles").toAbsolutePath();

    @Override
    protected PathFilter getPathFilter() {
        return PathFilter.acceptAllFiles("all files");
    }

    @Override
    protected Skin getSkin() {
        return Skin.MODENA;
    }

    @Override
    protected Path getStartDirectory() {
        return emptyDirectory;
    }

    @AfterEach
    void closeStage() throws Exception {
        Invoke.andWait(()->primaryStage.close());
    }

    /*
     * There seems to be an issue with TestFX when the FXML represents the root pane
     * of a control. If so, TestFX correctly approaches the positions of all nodes
     * but interaction such as click, write, press etc. is not working.
     * 
     * See StackOverflow:
     * https://stackoverflow.com/questions/57051778/how-to-get-testfx-to-find-the-
     * roots-fxid-in-testing-the-root-is-a-custom-cont
     * 
     * TODO: User Kleopatra asked for a minimal reproducible example, I think this
     * will be doable.
     */
    @Test
    void viewCanBeLoaded() {
        assertDoesNotThrow(() -> lookup("#okButton").query());
        Parent root = primaryStage.getScene().getRoot();
        captureImage(root, "ScreenshotModenaTheme.png");
    }

}
