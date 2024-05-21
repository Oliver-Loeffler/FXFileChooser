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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class FXFileChooserStage1Test extends ApplicationTest {

    protected FXFileChooserStage stageUnderTest;
    
    @Override
    public void start(Stage stage) {
        stageUnderTest = FXFileChooserStage.create(Skin.DARK);
        stage = stageUnderTest;
        stage.show(); 
    }
    
    @AfterEach
    void closeStage() throws Exception {
        Invoke.andWait(() -> stageUnderTest.close());
    }

    @Test
    void that_defaults_for_filter_and_search_path_are_working() {
        assertDoesNotThrow(() -> lookup("#okButton").query());
        Parent root = stageUnderTest.getScene().getRoot();
        captureImage(root, "ScreenshotFXFileChooserStageDark.png");
        
        FileChooserModel model = stageUnderTest.getModel();
        assertEquals(1, model.getPathFilter().size());
        assertEquals("*.*", model.getPathFilter().get(0).getName());
        assertNotNull(model.getPathFilter().get(0).getPredicate());
        assertEquals(Paths.get("."), model.currentSearchPath().get());
    }
    
    protected void captureImage(Parent put, String filename) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(capture(put).getImage(), null);
        try {
            ImageIO.write(bImage, "png", new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
