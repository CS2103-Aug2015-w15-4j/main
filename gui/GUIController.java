package gui;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Logic;
import logic.Task;
import parser.ParsedCommand;
import parser.MyParser;
import parser.MyParser.CommandType;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.javafx.application.LauncherImpl;

//@@author A0122534R
public class GUIController extends Application {
	/**
	 * Create a child of the TaskList class to use GUIController's class's functions for openList and closeList 
	 */
	protected static class TaskListCustom extends TaskList {
		public TaskListCustom() {
			super();
			rebindMouseAction();
		}

		public TaskListCustom(String _name) {
			super(_name);
			rebindMouseAction();
		}

		public TaskListCustom(int num) {
			super(num);
			rebindMouseAction();
		}

		protected void rebindMouseAction() {
			name.onActionProperty().unbind();
			name.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (isListOpen) {
						GUIController.closeList(listNumber); // new line that is changed from original
					} else {
						GUIController.openList(listNumber);// new line that is changed from original
					}
				}
			});
		}
	}

	/**
	 * Class to handle Global Handlers
	 */
	protected static class GlobalListener implements NativeKeyListener {
		// NativeKeyListeners
		@Override
		public void nativeKeyPressed(NativeKeyEvent e) {
			if (e.getKeyCode()==NativeKeyEvent.VC_SPACE&& // if space and
					(e.getModifiers()==NativeKeyEvent.CTRL_L_MASK||
					e.getModifiers()==NativeKeyEvent.CTRL_R_MASK)) { // alt
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (stage!=null) { // in case the buttons were triggered during loading screen
							if (stage.isFocused()) {
								stage.setIconified(true); // minimise
							} else {
								stage.setIconified(true); 
								// if it was behind another window, this allows it to regain focus when brought up
								stage.setIconified(false); 
							}
						}
					}
				});
			}
		}

		// Unused methods
		@Override public void nativeKeyReleased(NativeKeyEvent e) {}
		@Override public void nativeKeyTyped(NativeKeyEvent e) {}
	}

	// Logic components
	protected static Logic logic;
	protected static logic.Model model;

	protected final static String APP_TITLE = "OraCle";
	protected final static String FILE_CSS = "application.css";

	protected final static String CSS_TAG_BOTTOMBAR = "bottombar";
	protected final static String CSS_TAG_TEXTBOX = "textbox";

	protected final static String CSS_STYLE_CURVED_VBOX = "vbox-curved";
	protected final static String CSS_STYLE_CURVED_LABEL = "label-curved";
	protected final static String CSS_STYLE_HEADING = "heading";
	protected final static String CSS_STYLE_TRANSPARENT = "transparent";
	protected final static String CSS_STYLE_COLOR = "-fx-background-color: %1$s;";

	// Formats and default messages
	protected final static String SEARCH_LIST_FORMAT = "%1$s (%2$d) - \"%3$s\""; // name, list size, keywords
	protected final static String MSG_SUGGESTED_COMMAND_FORMAT = "Did you mean the \"%1$s\" command?";
	protected final static String MSG_PROMPT = "Type command here";
	protected final static String MSG_WINDOWSWITCH = "Switch"; // name for Switch button
	protected final static String ERR_TASKID = "ERROR: Task ID not found";
	protected final static String EMPTY_STRING = "";
	protected final static String CMD_SEARCH = "search "; // for Ctrl+F and searching

	// TaskList variables
	// static because there should only be one list of TaskLists at any point of time
	protected static ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
	public final static String[] taskListNames = {
			"Overdue",
			"Today",
			"Floating",
			"To-do",
			"Search list"
	};
	public final static String[] STYLE_BUTTON_NAMES = {
			"button-overdue",
			"button-today",
			"button-floating",
			"button-todo",
			"button-search"
	};
	protected final static int TASKLIST_OVERDUE = 0;
	protected final static int TASKLIST_TODAY = 1;
	protected final static int TASKLIST_FLOATING = 2;
	protected final static int TASKLIST_TODO = 3;
	protected final static int TASKLIST_SEARCH = 4;
	public final static int TASKLIST_INVALID = -1;
	public static int TASKLIST_PINNED = TASKLIST_INVALID;
	public static int TASKLIST_OPENED = TASKLIST_INVALID; // task list last opened

	public static String AVATAR_IMAGENAME;
	public static String ICON_IMAGE = "icon.png";
	public static boolean isMainWindow = true; // true = main pane window, false = logObject

	// Window sizes and ratios
	protected final static int PINNED_WINDOW_RATIO = 3; // 1/ratio, ratio being the number to divide by
	protected final static int TEXTBOX_RATIO = 8; // 1/ratio, ratio being the number to divide by
	protected final static int MINIMUM_WINDOW_WIDTH = 600;
	protected final static int MINIMUM_WINDOW_HEIGHT = 650;
	protected final static int PADDING = 6;

	// for getting previous commands
	protected static ListIterator<Node> commandIterator;
	protected final static boolean PREVIOUS = false;
	protected final static boolean NEXT = true;
	protected final static int NESTED_NODE_NUM = 5; // number of nested nodes possible

	// for activating focus view
	protected static boolean isFocusView = false;

	/**
	 * Displayables
	 */	
	// the overall managers/parents
	protected static final Pane window = new VBox();
	protected static Stage stage = null;
	protected static Scene scene = null;  

	// the help menu
	protected static final HelpMenu help = new HelpMenu();

	// the log tab
	protected static VBox logObject;
	protected static Log logCommands;
	protected static Log logConsole;

	// pane, the default view with all TaskLists 
	protected static BorderPane pane;
	protected static PinnedPanel pinnedPanel;
	protected static CenterPanel centerPanel;
	protected static ConsolePanel consolePanel;

	// the bar under pane/log
	protected static HBox bottomBar;
	protected static TextField userInputField;
	protected static Button windowSwitch;

	public static void main(String[] args) {
		// launch the GUIController with a PreloaderWindow
		LauncherImpl.launchApplication(GUIController.class, PreloaderWindow.class, args);
	}

	@Override
	public void init() {
		// initialise data variables
		initCoreComponents(); // if fail, system will exit

		// initialise displayables
		initPane();
		initLogTab();
		initBottomBar();
		initMainWindow();

		// initialise the taskLists after displayables are ready
		initTaskLists();

		initScene();
		addSceneHandlers();
	}

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setScene(scene);
		stage.setTitle(APP_TITLE);
		stage.getIcons().add(new Image(
				GUIController.class.getResourceAsStream( ICON_IMAGE )));
		addStageHandlers();
		stage.show();
		stage.setMinWidth(stage.getWidth());
		stage.setMinHeight(stage.getHeight());
	}

	/**
	 * @return the master/parent node for this object
	 */
	public Pane getWindow() {
		return window;
	}

	/**
	 * Initialises the scene so that things can be added to it
	 */
	protected void initScene() {
		scene = new Scene(window, window.getPrefWidth(), window.getPrefHeight());
		scene.getStylesheets().add(GUIController.class.getResource(FILE_CSS).toExternalForm());
	}

	/**
	 * The following are initialised: 
	 * @param logic
	 * @param model
	 * @param AVATAR_IMAGENAME
	 */
	protected void initCoreComponents() {
		try {
			logic = new Logic();
			model = logic.executeCommand(null);
			AVATAR_IMAGENAME = model.getAvatarLocation(); 
		} catch (Exception e) {
			// if fail to initialise terminate program
			System.err.println("Unable to create logic components");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initialises the TaskLists and gets the information for them from the model
	 * @taskLists
	 */
	protected void initTaskLists() {
		// Create the list of TaskLists
		for (int i=0; i<taskListNames.length;i++) {
			TaskList list = new TaskListCustom(i); // use the new child instead
			taskLists.add(list); // use this version to allow to close all other task lists
			list.name.getStyleClass().add(STYLE_BUTTON_NAMES[i]);
		}
		refreshLists(); // initalise all lists

		// pin the overdue tab if not empty
		if (!taskLists.get(TASKLIST_OVERDUE).isListEmpty()) {
			pin(taskLists.get(TASKLIST_OVERDUE));
		}//*/
		openList(TASKLIST_TODAY); // open today first
	}

	/**
	 * Initalises the pinned window's frame
	 * @param pinnedPanel
	 */
	protected void initPinnedPanel() {
		pinnedPanel = new PinnedPanel();
		pinnedPanel.getNode().prefWidthProperty().bind(pane.widthProperty());
		pinnedPanel.getNode().prefHeightProperty().bind(pane.heightProperty().divide(PINNED_WINDOW_RATIO));
		pinnedPanel.getNode().getStyleClass().add(CSS_STYLE_CURVED_VBOX);
	}

	/**
	 * Initialises the displayables in the center 
	 * @param centerPanel Binds to the next node object below
	 * @param pane
	 */
	protected void initCenter() {
		centerPanel = new CenterPanel();
		centerPanel.getNode().prefWidthProperty().bind(pane.widthProperty());
		centerPanel.getNode().maxWidthProperty().bind(pane.widthProperty());
	}

	/**
	 * Initialises the bottom panel
	 * @param consolePanel Binds to the next node object below
	 * @param pane
	 */
	protected void initConsolePanel() {
		consolePanel = new ConsolePanel();
		consolePanel.getNode().prefWidthProperty().bind(pane.widthProperty());
		consolePanel.getNode().maxWidthProperty().bind(pane.widthProperty());
		consolePanel.getNode().maxHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		consolePanel.getNode().prefHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
	}

	/**
	 * Initialises the default Pane, which is where all the TaskLists are displayed
	 * @param pane Binds to the next node object below
	 * @param window
	 */
	protected void initPane() {
		pane = new BorderPane();
		initPinnedPanel();
		initCenter();
		initConsolePanel();

		// add the other panels except for pinnedWindow to the pane
		pane.setCenter(centerPanel.getNode());
		pane.setBottom(consolePanel.getNode());

		// Bind to window
		pane.prefWidthProperty().bind(window.widthProperty());
		pane.prefHeightProperty().bind(window.heightProperty());
	}

	/**
	 * Initialises the Log page of the application
	 * @param logObject
	 * @param logConsole
	 * @param logCommands
	 */
	protected void initLogTab() {
		logObject = new VBox();
		logConsole = new Log("Console");
		logCommands = new Log("Commands");
		logObject.getChildren().add(logConsole.getNode());
		logObject.getChildren().add(logCommands.getNode());
		logConsole.getNode().prefHeightProperty().bind(logObject.heightProperty().divide(2));
		logConsole.getNode().prefWidthProperty().bind(logObject.widthProperty());
		logCommands.getNode().prefHeightProperty().bind(logObject.heightProperty().divide(2));
		logCommands.getNode().prefWidthProperty().bind(logObject.widthProperty());
		logObject.prefWidthProperty().bind(window.widthProperty());
		logObject.maxWidthProperty().bind(window.widthProperty());
		logObject.prefHeightProperty().bind(window.heightProperty());
		logObject.maxHeightProperty().bind(window.heightProperty());
		VBox.setVgrow(logObject, Priority.ALWAYS);
		VBox.setVgrow(logCommands.getNode(), Priority.ALWAYS);
		VBox.setVgrow(logConsole.getNode(), Priority.ALWAYS);
	}

	/**
	 * Initialises displayables under the ConsolePanel
	 * @param userInputField Added to bottomBar
	 * @param windowSwitch Added to bottomBar
	 * @param bottomBar Binds to the next node object below
	 * @param window
	 */
	protected void initBottomBar() {
		bottomBar = new HBox();
		// initialise the input field
		userInputField = new TextField();
		userInputField.setPromptText(MSG_PROMPT);
		HBox.setHgrow(userInputField, Priority.ALWAYS);
		bottomBar.getChildren().add(userInputField);

		// then create the switch button to toggle between log and pane
		windowSwitch = new Button(MSG_WINDOWSWITCH);
		bottomBar.getChildren().add(windowSwitch);

		// bind to main window
		bottomBar.prefWidthProperty().bind(window.widthProperty());
	}

	/**
	 * Initialises the main container for everything
	 * @window Adds the following objects below to the window
	 * @pane
	 * @bottomBar
	 */
	protected void initMainWindow() {
		window.setMinWidth(MINIMUM_WINDOW_WIDTH);
		window.setMinHeight(MINIMUM_WINDOW_HEIGHT);
		// add the nodes to window
		window.getChildren().add(pane);
		window.getChildren().add(bottomBar);
	}

	/**
	 * Adds handlers to the scene
	 * @scene
	 * 
	 * 
	 */
	protected void addSceneHandlers() {
		// event handler for userTextField
		userInputField.setOnAction((ActionEvent event) -> processUserTextField());
		userInputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode()==KeyCode.UP) { // get last item typed in, unless it is first
					String lastCommand = getCommandLog(PREVIOUS); // get previous
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userInputField.setText(lastCommand);
						userInputField.end(); // set cursor behind last chara
					}
				}

				if (keyEvent.getCode()==KeyCode.DOWN) { // get next item typed in, unless it is last
					String lastCommand = getCommandLog(NEXT); // get next item
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userInputField.setText(lastCommand);
						userInputField.end(); // set cursor behind last chara
					}
				}
			}
		});

		// event handler for button
		windowSwitch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switchWindow();
			}
		});

		// Scene event handler for shortcuts
		scene.setOnKeyPressed((new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				switch (ShortcutManager.processKeyEvent(keyEvent)) {
				case FOCUS_MODE: 
					showFocusTask(isPinnedFocused(stage.getScene()) );
					break;
				case FOCUS_MODE_CLEAR:
					if (TASKLIST_PINNED!=TASKLIST_INVALID) { // if there is pinned window, open that
						openList(TASKLIST_PINNED);
						taskLists.get(TASKLIST_PINNED).requestFocus();
					} else {
						unpin(); // else clear the focus task
					}
					break;
				case HELP:
					showHelpMenu();
					break;
				case SEARCH:
					userInputField.requestFocus();
					userInputField.setText(CMD_SEARCH); 
					userInputField.end(); // set cursor behind last chara
					break;
				case SWITCH:
					switchWindow();
					break;
				case INPUT_FIELD:
					userInputField.requestFocus();
					userInputField.end(); // set cursor behind last chara
					break;
				case UNDO:
					executeCommand("undo");
					break;
				case UNPIN:
					unpin();
					break;
				default:
					break;
				}
			}
		}));//*/
	}

	/**
	 * Add handlers for the stage
	 * @param stage
	 * 
	 */
	protected void addStageHandlers() {
		// create global hook
		try {
			// Get the logger for "org.jnativehook" and set the level to off to remove log from it
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			// Activate the global listener and add it
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new GlobalListener());// Clear previous logging configurations.
		} catch (NativeHookException ex) {
			// if there was a problem ignore the native hook and don't support it
		}

		// unhook it when application closing
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
				}
			}
		});

		// Scene resize listener
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				recalculatePinned();
				centerPanel.recalculate();
			}
		};
		stage.widthProperty().addListener(listener);
		stage.heightProperty().addListener(listener);
	}
	
	/**
	 * Activates the help menu
	 */
	protected void showHelpMenu() {
		if (!help.getNode().isShowing()) {
			help.getNode().show(stage);
		}
	}

	/**
	 * Pins the list to the top window
	 * @param list list to be pinned
	 */
	protected void pin(TaskList list) {
		if (list!=null) {
			unpin();
			pinnedPanel.pin(list);
			TASKLIST_PINNED = list.listNumber;
			pane.setTop(pinnedPanel.getNode());
			openList(list); // open the list to see the main list
			refreshLists();
		}
	}
	
	/**
	 * Pins a focused Task into the window
	 * @param Task to be pinned
	 */
	protected static void pinFocusTask(Region focusedTask) {
		if (focusedTask!=null) {
			unpin();
			pinnedPanel.pin(focusedTask);
			isFocusView = true;
			pane.setTop(pinnedPanel.getNode());
		}
	}
	
	/**
	 * Unpins object from the pinnedWindow
	 */
	protected static void unpin() {
		pinnedPanel.unpin();
		TASKLIST_PINNED = TASKLIST_INVALID;
		isFocusView = false;
		pane.setTop(null);
		refreshLists(); // re-add the newly freed list into the center panel
	}
	
	/**
	 * Recalculates the size of the pined window
	 */
	protected void recalculatePinned() {
		pinnedPanel.recalculate();
	}

	/**
	 * Returns the string from the command log. 
	 * @param next if true, get next command. If false, get previous command
	 * @return command last inputted
	 */
	public String getCommandLog(boolean next) {
		Text command = null;
		String output = "";
		if (commandIterator!=null) { // if commandIterator empty, do nothing
			if (next==NEXT&&commandIterator.hasNext()) {
				command = (Text)commandIterator.next();
			} else if (next==PREVIOUS&&commandIterator.hasPrevious()) {
				command = (Text)commandIterator.previous();
			} // if there are no valid commands, do nothing

			if (command!=null) {
				output = command.getText().replaceFirst(">",""); // delete the > in the log
			}
		}
		return output;
	}

	/**
	 * Switch between the main view and the log view
	 */
	protected void switchWindow() {
		window.getChildren().clear();
		if (!isMainWindow) {
			window.getChildren().add(pane);
		} else {
			window.getChildren().add(logObject);
		}
		window.getChildren().add(bottomBar);
		isMainWindow = !isMainWindow;
	}

	/**
	 * Takes the data from userInputField and processes it in executeCommand()
	 */
	public void processUserTextField() {
		String temp = userInputField.getText();
		userInputField.clear();
		executeCommand(temp);
	}

	/**
	 * Returns the task number of the item indicated in the Main Window
	 * @param locationInCenterPanel
	 * @return the TaskList's number
	 * @throws IndexOutOfBoundsException 
	 */
	protected int getTaskListNumberFromLocationInCenterPanel(int locationInCenterPanel) throws IndexOutOfBoundsException {
		return centerPanel.listOfTaskLists.get(locationInCenterPanel).listNumber;
	}

	/**
	 * Gets task list number from the input by user
	 * @param processedString
	 * @return
	 * @throws Logic.UnrecognisedCommandException
	 */
	protected int getTaskListNumber(String processedString) throws Logic.UnrecognisedCommandException {
		try {
			int i = Integer.parseInt(processedString);
			if (i<=TASKLIST_INVALID) { 
				// means that it should have been validated by parser. Can return immediately
				return i+taskListNames.length; 
			} else { // it must be a number inputted by the user based on the location of items on the screen
				if (TASKLIST_PINNED!=TASKLIST_INVALID&&i==0) { 
					// if there is a pinned window and is first element
					return TASKLIST_PINNED;
				} else {
					// if no pinned window, order is same as initial
					// if there is pinned panel and not zero, means one of the center panel
					if (TASKLIST_PINNED!=TASKLIST_INVALID) { 
						i--;  // i-1 because exclude pinned
					}
					return getTaskListNumberFromLocationInCenterPanel(i); 
				}
			}
		} catch (NumberFormatException e) {
			model.setConsoleMessage("Invalid command");
			throw new Logic.UnrecognisedCommandException("Unable to parse integer"); 
		} catch (IndexOutOfBoundsException e) {
			model.setConsoleMessage("Unable to find selected tab");
		}
		return TASKLIST_INVALID; // return invalid otherwise
	}

	/**
	 * Performs an action based on the command. GUI only commands are processed here
	 * @param parsedCommand Command after being parsed by Parser
	 * @return true if GUI has performed the necessary action, false if it needs to be passed to Logic for further action
	 */
	protected boolean performGuiActions(ParsedCommand parsedCommand) {
		CommandType command = parsedCommand.getCommandType();
		try {
			switch (command) {
			case GUI_SHOW: // show a specific task in the pinned window
				return showFocusTask(true);
			case GUI_SWITCH:
				switchWindow();
				return true;
			case GUI_LOG:
				if (isMainWindow) {
					switchWindow();
					return true;
				}
				break;
			case GUI_MAIN:
				if (!isMainWindow) {
					switchWindow();
					return true;
				}
				break;
			case GUI_UNPIN:
				unpin();
				return true;
			case GUI_PIN:
				pin(taskLists.get(getTaskListNumber(parsedCommand.getGuiType())));
				return true;
			case GUI_OPEN:
				TaskList list = taskLists.get(getTaskListNumber(parsedCommand.getGuiType()));
				openList(list.listNumber);
				list.getNode().getChildren().get(0).requestFocus(); // have the first node of the Vbox request focus
				return true;
			case GUI_CLOSE:
				closeList(taskLists.get(getTaskListNumber(parsedCommand.getGuiType())));
				return true;
			default:
				break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// do nothing
		} catch (Logic.UnrecognisedCommandException e) {
			for (int i=0;i<MyParser.COMMAND_CHOICES.length;i++) {
				if (command.equals(MyParser.COMMAND_CHOICES[i].commandType)) {
					model.setConsoleMessage(
							String.format(MSG_SUGGESTED_COMMAND_FORMAT, 
									MyParser.COMMAND_CHOICES[i].str[0]));
					break;
				}
			}
			return true; // because it was caught by GUI controller
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // if not valid, return false
	}

	/**
	 * Checks for what kind of command it was, so that it can be used to focus on the correct object
	 * @param command The first word of the command
	 */
	protected String performPostLogicActions(ParsedCommand parsedCommand) {
		CommandType command = parsedCommand.getCommandType();
		String output = "";
		try {
			switch(command) {
			case ADD: // focus on the newly added task by putting it at the top
				focusOnTaskID(model.getFocusId());
				break;
			case DELETE:
				if (TASKLIST_PINNED!=TASKLIST_INVALID) { // open the list to prevent focus on deleted item
					openList(TASKLIST_PINNED);
				} else {
					unpin(); // unpin to prevent focus on deleted item
				}
				break;
			case EDIT: // focus on editted task
				focusOnTaskID(model.getFocusId());
				break;
			case DISPLAY: // focus on task to be displayed
				focusOnTaskID(parsedCommand.getTaskId());
				break;
			case HELP:
				// help menu
				if (!help.getNode().isShowing()) {
					help.getNode().show(stage);
				}
				break;
			case CONFIG_IMG:
				// if it had been a Set function, it might have been an avatar or background, so reload them
				AVATAR_IMAGENAME = model.getAvatarLocation();
				if(!consolePanel.loadAvatar()) {
					output = "Cannot find new avatar specified";
				}
				break;
			case UNDO:
				if (TASKLIST_PINNED!=TASKLIST_INVALID) { // same as delete
					openList(TASKLIST_PINNED);
				} else {
					unpin();
				}
				break;
			case SEARCH: // search function
				closeAllLists();
				TaskList search = taskLists.get(TASKLIST_SEARCH);
				if (!search.isListEmpty()) { // if it is not empty
					// deactivate focus view
					if (isFocusView) {
						unpin();
					}
					openList(TASKLIST_SEARCH); // focus on search 
					// then modify the Search List name to include the search term
					String keywords = parsedCommand.getKeywords();
					if (!keywords.isEmpty()) {
						search.name.setText(
								String.format(SEARCH_LIST_FORMAT,
										taskListNames[TASKLIST_SEARCH],
										search.listSize,
										keywords)
								);
					}
				}
				break;
			default:
				break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// do nothing
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}//*/

	/**
	 * Runs the command input. 
	 * @param input 
	 */
	protected void executeCommand(String input) {
		if (input!= null && !input.isEmpty()) {
			model.setConsoleMessage(null); // set empty
			ParsedCommand parsedCommand = MyParser.parseCommand(input.trim());
			if (!performGuiActions(parsedCommand)) {
				try {
					model = logic.executeCommand(parsedCommand);
					// refresh log command, and get new iterator
					logCommands.addToLog(input);
					commandIterator = logCommands.getLog().getChildren().listIterator(
							logCommands.getLog().getChildren().size()); // get last item

					refreshLists(); // only needed if sent to logic
				} catch (Logic.UnrecognisedCommandException e) {
					//model.setConsoleMessage(output);
				}

				String output = performPostLogicActions(parsedCommand);
				if (!output.isEmpty()) { // override previous error
					model.setConsoleMessage(output);
				}
			}

			// output console message
			outputToScreen();
		}
	}

	/**
	 * Output to console and store consoleMessage
	 */
	protected void outputToScreen() {
		if (model.getConsoleMessage()==null) {
			model.setConsoleMessage(EMPTY_STRING);
		}
		consolePanel.addToConsole(model.getConsoleMessage());
		logConsole.addToLog(model.getConsoleMessage());
	}

	/**
	 * Refreshes all displayed lists
	 */
	protected static void refreshLists() {
		for (TaskList list : taskLists) {
			switch(list.listNumber) {
			case TASKLIST_TODO: // all tasks tab
				taskLists.get(TASKLIST_TODO).addAllTasks(model.getMainList(),true);;
				break;
			case TASKLIST_SEARCH: // search list
				list.addAllTasks(model.getSearchList(),true);
				break;
			case TASKLIST_OVERDUE: // overdue list
				list.addAllTasks(model.getOverdueList(),true);
				break;
			case TASKLIST_FLOATING: // floating tasks list
				list.addAllTasks(model.getFloatingList(),true);
				break;
			case TASKLIST_TODAY:// today list 
				list.addAllTasks(model.getTodayList(),true);
				break;
			default:
				break;
			}
		}
		
		// Add the lists to the centerPanel
		if (centerPanel!=null) {
			int open = TASKLIST_OPENED;
			centerPanel.removeAllFromList();
			for (int i=0; i<taskLists.size();i++) {
				if (i!=TASKLIST_PINNED) {
					centerPanel.addToList(taskLists.get(i));
				}
			}
			openList(open); // keep the list that was previously opened open
		}
	}

	/**
	 * Opens a specified list and closes all other lists at the same time
	 * @param listNumber list to open
	 */
	public static void openList(int listNumber) {
		if (listNumber>TASKLIST_INVALID&&listNumber<taskListNames.length) { // if valid
			if (listNumber!=TASKLIST_PINNED) {
				// if it is for pinned window, no need to close anything
				// else close everything
				closeAllLists();
				TASKLIST_OPENED = listNumber;
			}
			TaskList list = taskLists.get(listNumber);
			list.openList();
			if (isFocusView&&TASKLIST_PINNED==TASKLIST_INVALID) { // if there had been a previously opened focus view
				if (!list.hasSelection()) { // if the list has none selected, select first
					list.selectFirstNode();
				}
				pinFocusTask(list.getFocusTask());
			}
		}
	}

	/**
	 * Opens a specified list and closes all other lists at the same time
	 * @param list list to open
	 */
	public static void openList(TaskList list) {
		openList(list.listNumber);
	}

	/**
	 * Opens a specified list and closes all other lists at the same time
	 * @param listNumber list to open
	 */
	protected static void closeList(int listNumber) {
		if (listNumber>=0&&listNumber<taskListNames.length) { // if valid
			taskLists.get(listNumber).closeList();
			if (listNumber!=TASKLIST_PINNED) {
				TASKLIST_OPENED = TASKLIST_INVALID;
			}
		}
	}

	/**
	 * Closes this list
	 * @param list
	 */
	protected static void closeList(TaskList list) {
		closeList(list.listNumber);
	}

	/**
	 * Closes all lists except the pinned list
	 */
	protected static void closeAllLists() {
		for (TaskList list : taskLists) {
			if (list.listNumber!=TASKLIST_PINNED) {
				closeList(list);
			}
		}
	}

	/**
	 * Creates a focusView for the id number selected
	 */
	protected void focusOnTaskID(int id) {
		try {
			Task taskToFocus = Logic.searchList(model.getAllTasks(), id);
			GridPane grid = TaskList.createDetailedDisplay(taskToFocus);
			// Create a scrollPane for the task
			ScrollPane sp = new ScrollPane(grid);
			TaskList.setScrollPane(sp);
			//grid.prefWidthProperty().bind(sp.widthProperty().subtract(2*PADDING));
			//grid.prefHeightProperty().bind(sp.heightProperty().subtract(2*PADDING));
			pinFocusTask(sp);
		} catch (IndexOutOfBoundsException e) {
			// if out of range
			model.setConsoleMessage(ERR_TASKID);
		}
	}

	/**
	 * Checked if the pinned window has focus
	 */
	protected boolean isPinnedFocused(Scene scene) {
		Node focused = scene.getFocusOwner();
		if (focused!=null) {
			TaskList list = taskLists.get(TASKLIST_PINNED);
			for (int i=0;i<NESTED_NODE_NUM;i++) {
				if (focused==list.getNode()) {
					return true;
				}
				focused = focused.getParent();
				
				if (focused==null) {
					break;
				}
			}
		}
		return false;
	}

	/**
	 * Opens the focus Task view if valid
	 * @param focusPinned focus on the pinned window if exists?
	 */
	protected boolean showFocusTask(boolean focusPinned) {
		if (TASKLIST_PINNED!=TASKLIST_INVALID&&focusPinned) {
			// check if the pinned window is the focus of the scene
			TaskList pinned = taskLists.get(TASKLIST_PINNED);
			pinned.focusTask();
			pinned.closeList();
			pinned.getNode().requestFocus();
			return true;
		} else if (TASKLIST_OPENED!=TASKLIST_INVALID) {
			pinFocusTask(taskLists.get(TASKLIST_OPENED).getFocusTask());
			return true;
		} else {
			Node focused = stage.getScene().getFocusOwner();
			if (focused!=null) {
				for (TaskList list : taskLists) {
					Node node = focused;
					for (int i=0;i<NESTED_NODE_NUM;i++) {
						if (list.getNode().equals(node)) {
							openList(list);
							list.selectFirstNode();
							pinFocusTask(list.getFocusTask());
							return true;
						}
						node = node.getParent();
					}
				}
			}
		}
		return false;
	}
}
