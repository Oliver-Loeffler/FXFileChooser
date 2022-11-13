package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;

public class FileChooserStage2Test extends ApplicationTest {

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
