package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.javafx.application.LauncherImpl;

import gui.GuiController;
import gui.PreloaderWindow;
import javafx.application.Platform;


public class GuiControllerTest extends GuiController {
	@BeforeClass
	public static void initialise() throws InterruptedException {
		Thread t = new Thread("JavaFX Init Thread") {
	        public void run() {
	    		LauncherImpl.launchApplication(GuiController.class, PreloaderWindow.class, new String[0]);
	        }
	    };
	    t.setDaemon(true);
	    t.start();
	    Thread.sleep(1500);
	}
	
	@Test
	public void helpMenuTest01() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				help.show(stage);
				assertEquals(true, help.isShowing());
			}
		});
	}
}
