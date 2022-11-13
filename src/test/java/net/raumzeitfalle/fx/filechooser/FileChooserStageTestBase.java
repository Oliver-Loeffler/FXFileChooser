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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.testfx.framework.junit5.ApplicationTest;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

abstract class FileChooserStageTestBase extends ApplicationTest {

    protected Stage primaryStage;

    protected abstract PathFilter getPathFilter();

    protected abstract Skin getSkin();

    protected abstract Path getStartDirectory();

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        FileChooserModel model = FileChooserModel.startingIn(getStartDirectory(), getPathFilter());
        FXDirectoryChooser dirChooser = FXDirectoryChooser.createIn(model.currentSearchPath(), () -> stage.getOwner());
        Parent view = new FileChooser(dirChooser, () -> stage.close(), model, getSkin(), FileChooserViewOption.STAGE);
        Scene scene = new Scene(view, 700, 500);
        stage.setScene(scene);
        stage.show();
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
