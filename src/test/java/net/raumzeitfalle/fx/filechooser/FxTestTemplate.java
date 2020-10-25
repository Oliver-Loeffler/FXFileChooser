package net.raumzeitfalle.fx.filechooser;

import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;

abstract class FxTestTemplate extends ApplicationTest {

	protected Stage primaryStage = null;
	
	@Override
	public void start(Stage stage) throws Exception {
		/* please override when needed */
	}

}
