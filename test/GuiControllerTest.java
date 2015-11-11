package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.javafx.application.LauncherImpl;

import gui.GuiController;
import gui.PreloaderWindow;
import javafx.application.Platform;
import logic.Logic.UnrecognisedCommandException;

//@@author A0122534R
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
	
	@Test
	public void helpMenuTest02() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				help.show(stage);
				help.hide();
				assertEquals(false, help.isShowing());
			}
		});
	}
	
	@Test
	public void getTaskListNumberTests() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pin(taskLists.get(TASKLIST_OVERDUE));
				
				try {
					assertEquals(TASKLIST_OVERDUE, getTaskListNumber("1"));
				} catch (UnrecognisedCommandException e) {
					
				}
				
				pin(taskLists.get(TASKLIST_TODO));
				try {
					assertEquals(TASKLIST_TODO, getTaskListNumber("1"));
					assertEquals(TASKLIST_OVERDUE, getTaskListNumber("2"));
				} catch (UnrecognisedCommandException e) {
					
				}
			}
		});
	}
	
	@Test
	public void commandIteratorTests() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String[] breakfast = {
					"turkey",
					"bacon",
					"with",
					"cheese"
				};
				for (int i=0; i< breakfast.length;i++) {
					logCommands.addToLog("cheese");
				}
				commandIterator = logCommands.getLog().getChildren().listIterator(
						logCommands.getLog().getChildren().size()); // get last item
				
				assertEquals(breakfast[3], getCommandLog(PREVIOUS));
				assertEquals(breakfast[2], getCommandLog(PREVIOUS));
				assertEquals(breakfast[3], getCommandLog(NEXT));
				assertEquals(breakfast[3], getCommandLog(NEXT));
				assertEquals(breakfast[2], getCommandLog(PREVIOUS));
				assertEquals(breakfast[1], getCommandLog(PREVIOUS));
				assertEquals(breakfast[0], getCommandLog(PREVIOUS));
				assertEquals(breakfast[0], getCommandLog(PREVIOUS));
				assertEquals(breakfast[1], getCommandLog(NEXT));
			}
		});
	}

}
