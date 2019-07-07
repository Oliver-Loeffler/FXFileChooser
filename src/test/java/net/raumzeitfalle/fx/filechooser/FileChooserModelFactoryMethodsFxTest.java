package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class FileChooserModelFactoryMethodsFxTest extends ApplicationTest {
	
	private FileChooserModel classUnderTest;
	
	private Path testRoot = Paths.get("./TestData/SomeFiles");

	@Override
	public void init() throws Exception {
		classUnderTest = FileChooserModel.startingInUsersHome();
	}
	
	@Test
	public void startingInUsersHome() {
		
		
		
		Path searchPath = classUnderTest.currentSearchPath().get();
		Path usersHome = Paths.get(System.getProperty("user.home"));
		
		assertEquals(usersHome.toAbsolutePath(), searchPath.toAbsolutePath());
	}
	
	@Test
	public void changingDirectory_updateFilesIn() throws InterruptedException, ExecutionException {
		
	
		assertNotEquals(testRoot.isAbsolute(), 
					    classUnderTest.currentSearchPath().get().toAbsolutePath(),
					    "search path before directory change");
		
		// Consider moving the service into the controller out of the model
		Invoke.andWait(()->classUnderTest.updateFilesIn(testRoot));
			
		assertEquals(testRoot.toAbsolutePath(), 
					 classUnderTest.currentSearchPath().get().toAbsolutePath(),
					 "search path after directory change");

	}
	
}
