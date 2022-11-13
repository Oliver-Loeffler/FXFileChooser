package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;


public class FileChooserStage3Test extends ApplicationTest {

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
