package gui;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

public class GUIController extends Application {
	public static class TaskListCustom extends TaskList {
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
		
		public void rebindMouseAction() {
			name.onActionProperty().unbind();
			name.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (isListOpen) {
						// focusTask(); // removed due to real time focus
						GUIController.closeList(listNumber);
					} else {
						GUIController.openList(listNumber);// new line that changes everything
					}
				}
		    });
		}
	}

	final public static String[] taskListNames = {
			"Overdue",
			"To-do",
			"Today",
			"Floating",
			"Search list"
	};
	final static int TASKLIST_OVERDUE = 0;
	final static int TASKLIST_TODO = 1;
	final static int TASKLIST_TODAY = 2;
	final static int TASKLIST_FLOATING = 3;
	final static int TASKLIST_SEARCH = 4;
	final static int TASKLIST_INVALID = -1;
	public static int TASKLIST_PINNED = TASKLIST_INVALID;
	public static int TASKLIST_OPENED = TASKLIST_INVALID; // task list last opened
	
	final public static String[] STYLE_BUTTON_NAMES = {
			"button-overdue",
			"button-todo",
			"button-today",
			"button-floating",
			"button-search"
	};
	
	final static String APP_TITLE = "OraCle";
	final static String FILE_CSS = "application.css";

	final static String TAG_BOTTOMBAR = "bottombar";
	final static String TAG_TEXTBOX = "textbox";
	final static String TAG_TABPANE = "tabpane";

	final static String STYLE_CURVED_VBOX = "vbox-curved";
	final static String STYLE_CURVED_LABEL = "label-curved";
	final static String STYLE_HEADING = "heading";
	final static String STYLE_TRANSPARENT = "transparent";
	final static String STYLE_TEXT = "label";
	final static String STYLE_COLOR = "-fx-background-color: %1$s;";

	final static String SEARCH_LIST_FORMAT = "%1$s (%2$d) - \"%3$s\""; // name, list size, keywords
	final static String MSG_SUGGESTED_COMMAND_FORMAT = "Did you mean the \"%1$s\" command?";
	final static String MSG_PROMPT = "Type command here";
	final static String MSG_WINDOWSWITCH = "Switch"; // name for button
	final static String ERR_TASKID = "ERROR: Task ID not found";

	// gui commands 
	public enum GUICommandType {
		CLEAR, PIN, OPEN, CLOSE, CLOSEALL, SHOW, SWITCH 
	}
	final static String CMD_CLEAR = "clear"; // clears all log
	final static String CMD_PIN = "pin"; // pin XXX: pins a selected task list
	final static String CMD_UNPIN = "unpin"; // pin XXX: pins a selected task list
	final static String CMD_OPEN = "open"; // open XXX: opens a selected task list
	final static String CMD_CLOSE = "close"; // close XXX: closes a selected task list
	final static String CMD_OPENALL = "open all"; // opens all task lists
	final static String CMD_CLOSEALL = "close all"; // closes all open task lists
	final static String CMD_SHOW = "show"; // focuses on a selected task in the main view
	final static String CMD_SEARCH = "search"; // for Ctrl+F and searching 
	final static String CMD_SWITCH = "switch"; // switches between the log and the main window
	final static String CMD_LOG = "log"; // switches to the log
	final static String CMD_MAIN = "main"; // switches to the main window

	public static String AVATAR_IMAGENAME;
	public static String ICON_IMAGE = "icon.png";
	public static boolean isMainWindow = true; // true = main pane window, false = logObject

	final static int MINIMUM_WINDOW_WIDTH = 600;
	final static int MINIMUM_WINDOW_HEIGHT = 650;
	final static int PADDING = 6;
	public static BooleanProperty isWindowShortcutTriggered = new SimpleBooleanProperty(false); // false until called

	// 1/ratio, ratio being the number to divide by
	final static int PINNED_WINDOW_RATIO = 3;
	final static int TEXTBOX_RATIO = 8;

	// Stores all lists
	public static ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
	
	// for getting old commands
	public static ListIterator<Node> commandIterator;
	final static boolean PREVIOUS = false;
	final static boolean NEXT = true;
	final static int NESTED_NODE_NUM = 3; // number of nesting nodes possible
	final static String EMPTY_STRING = "";
	
	// for activating focus view
	public static boolean isFocusView = false;
	
	final static Pane window = new VBox();
	public static Log logCommands;
	public static Log logConsole;
	public static VBox logObject;
	public static Textbox textboxObject;
	public static BorderPane pane;
	public static MainWindow center;
	public static logic.Model model;
	public static VBox pinnedWindow;

	public static HBox bottomBar; // bottomMost bar
	public static TextField userTextField;
	public static Button windowSwitch;
	//public static TabPane tabPane;
	public static Logic controller = new Logic();


	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		// prevent resizing?
		//primaryStage.setResizable(false); // allow resizing?

		/**
		 * main interface manager
		 * Splits the different sections apart
		 */
		pane = new BorderPane();

		/**
		 * Initialise the Model
		 */
		try {
			model = controller.executeCommand(null);
		} catch (Logic.UnrecognisedCommandException e) {
		}
		AVATAR_IMAGENAME = model.getAvatarLocation();

		for (int i=0; i<taskListNames.length;i++) {
			TaskList list = new TaskListCustom(i);
			taskLists.add(list); // use this version to allow to close all other task lists
			list.name.getStyleClass().add(STYLE_BUTTON_NAMES[i]);
		}
		
		// intialise the all task lists
		refreshLists();

		// then pin it as the first task window
		pinnedWindow = new VBox();
		pinnedWindow.prefWidthProperty().bind(pane.widthProperty());
		pinnedWindow.prefHeightProperty().bind(pane.heightProperty().divide(PINNED_WINDOW_RATIO));
		pinnedWindow.getStyleClass().add(STYLE_CURVED_VBOX);

		// create the text
		textboxObject = new Textbox();
		textboxObject.getNode().prefWidthProperty().bind(pane.widthProperty());
		textboxObject.getNode().maxWidthProperty().bind(pane.widthProperty());
		textboxObject.getNode().maxHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		//textboxObject.getNode().minHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		textboxObject.getNode().prefHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		pane.setBottom(textboxObject.getNode());

		// create the Log tab
		logObject = createLogTab();		

		// create the button to switch windows with
		windowSwitch = new Button(MSG_WINDOWSWITCH);

		// create input field
		userTextField = new TextField();
		//userTextField.setMinWidth(TABPANE_WIDTH);
		userTextField.setPromptText(MSG_PROMPT);
		//userTextField.prefWidthProperty().bind(window.widthProperty());

		// create the bottom bar
		bottomBar = new HBox();
		bottomBar.getChildren().add(userTextField);
		bottomBar.getChildren().add(windowSwitch);
		bottomBar.prefWidthProperty().bind(window.widthProperty());
		HBox.setHgrow(userTextField, Priority.ALWAYS);

		// add all lists to to the center
		center = new MainWindow();
		refreshLists();
		center.getNode().prefWidthProperty().bind(pane.widthProperty());
		center.getNode().maxWidthProperty().bind(pane.widthProperty());
		pane.setCenter(center.getNode());

		pane.prefWidthProperty().bind(window.widthProperty());
		pane.prefHeightProperty().bind(window.heightProperty());
		window.setMinWidth(MINIMUM_WINDOW_WIDTH);
		window.setMinHeight(MINIMUM_WINDOW_HEIGHT);

		window.getChildren().add(pane);
		window.getChildren().add(bottomBar);
		Scene scene = new Scene(window, window.getPrefWidth(), window.getPrefHeight()); //WINDOW_WIDTH+10, WINDOW_HEIGHT+10);
		//scene.getRoot().setStyle("-fx-background-image: url(\"" + BACKGROUND_NAME + "\");");
		userTextField.requestFocus();
		
		// Activate the scene
		primaryStage.setScene(scene);
		primaryStage.setTitle(APP_TITLE);
		scene.getStylesheets().add(GUIController.class.getResource(FILE_CSS).toExternalForm());
		primaryStage.getIcons().add(new Image(
				GUIController.class.getResourceAsStream( ICON_IMAGE )));
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
		
		// Add handlers
		addHandlers(scene, primaryStage);

		// After showing the scene, pin the overdue tab if not empty
		if (!taskLists.get(TASKLIST_OVERDUE).isListEmpty()) {
			pinWindow(taskLists.get(TASKLIST_OVERDUE));
		}//*/
	}
	/**
	 * Global Handlers
	 */
	public static class GlobalListener implements NativeKeyListener {
		// NativeKeyListeners
		@Override
		public void nativeKeyPressed(NativeKeyEvent e) {
			if (e.getKeyCode()==NativeKeyEvent.VC_SPACE&& // if space and
					(e.getModifiers()==NativeKeyEvent.CTRL_L_MASK||
					e.getModifiers()==NativeKeyEvent.CTRL_R_MASK)) { // alt
				GUIController.isWindowShortcutTriggered.setValue(true);
			}
		}
		
		// Unused methods
		@Override public void nativeKeyReleased(NativeKeyEvent e) {}
		@Override public void nativeKeyTyped(NativeKeyEvent e) {}
	}
	
	/**
	 * Adds handlers to the scene
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void addHandlers(Scene scene, Stage stage) {
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
		/*
		isWindowShortcutTriggered.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (stage.isFocused()) {
					stage.hide();
				} else {
					stage.requestFocus();
				}
				isWindowShortcutTriggered.setValue(false);
            }
        });//*/
		
		// event handler for userTextField
		userTextField.setOnAction((ActionEvent event) -> processUserTextField(userTextField));
		userTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode()==KeyCode.UP) { // get last item typed in, unless it is first
					String lastCommand = getCommandLog(PREVIOUS); // get previous
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userTextField.setText(lastCommand);
						userTextField.end(); // set cursor behind last chara
					}
				}
				
				if (keyEvent.getCode()==KeyCode.DOWN) { // get next item typed in, unless it is last
					String lastCommand = getCommandLog(NEXT); // get next item
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userTextField.setText(lastCommand);
						userTextField.end(); // set cursor behind last chara
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

		// Scene resize listener
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub				
				taskLists.get(TASKLIST_TODO).recalculate();
				center.recalculate();
			}
		};
		scene.widthProperty().addListener(listener);
		scene.heightProperty().addListener(listener);

		// Scene event handler
		scene.setOnKeyPressed((new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				//System.out.println(keyEvent.getCode().toString());
				if(keyEvent.getCode()==KeyCode.T) {
					if (keyEvent.isControlDown()){
						userTextField.requestFocus();
						userTextField.end(); // set cursor behind last chara
					} else if (keyEvent.isAltDown()) {
						// switch
						switchWindow();
					}
				}

				if(keyEvent.getCode()==KeyCode.BACK_SLASH) { // zoom in on a task
					if (TASKLIST_PINNED!=TASKLIST_INVALID&&isPinnedFocused(scene)) {
						// check if the pinned window is the focus of the scene
						taskLists.get(TASKLIST_PINNED).focusTask();
						taskLists.get(TASKLIST_PINNED).closeList();
					} else if (TASKLIST_OPENED!=TASKLIST_INVALID) {
						pinFocusView(taskLists.get(TASKLIST_OPENED).getFocusTask());
					} else {
						/*
						listLoop: for (TaskList list : taskLists) {
							for (Node node : list.getNode().getChildren()) {
								if (node.isFocused()) {
									list.focusTask();
									openList(list);
									break listLoop;
								}
							}
						}//*/
						Node focused = scene.getFocusOwner();
						if (focused!=null) {
							listLoop: for (TaskList list : taskLists) {
								Node node = focused;
								for (int i=0;i<NESTED_NODE_NUM;i++) {
									if (list.getNode().equals(node)) {
										openList(list);
										list.selectFirstNode();
										pinFocusView(list.getFocusTask());
										break listLoop;
									}
									node = node.getParent();
								}
							}
						}
					}
				}

				if ((keyEvent.getCode()==KeyCode.BACK_SLASH&&keyEvent.isShiftDown())||
						keyEvent.getCode()==KeyCode.BACK_SPACE) { // see the main list again
					if (TASKLIST_PINNED!=TASKLIST_INVALID) { // if there is pinned window, open that
						openList(TASKLIST_PINNED);
					} else {
						unpinWindow(); // else clear the focus task
					}
				}

				if (keyEvent.getCode()==KeyCode.Z&&
						(keyEvent.isControlDown()||keyEvent.isAltDown())) { // undo the last command
					executeCommand("undo");
				}
				
				if (keyEvent.getCode()==KeyCode.F&&
						(keyEvent.isControlDown())) { // undo the last command
					userTextField.requestFocus();
					userTextField.setText(CMD_SEARCH+" ");
					userTextField.end(); // set cursor behind last chara
				}
			}
		}));//*/
	}

	/**
	 * Pins the list to the top window
	 * @param list list to be pinned
	 */
	protected void pinWindow(TaskList list) {
		if (list!=null) {
			unpinWindow(list);
			pane.setTop(pinnedWindow);
	
			TASKLIST_PINNED = list.listNumber;
			list.isPinnedWindow = true;
			list.selectFirstNode(); // select the first node, since it is moving up now
			list.focusTask(); // create an instance of zoomed in task
			openList(list); // open the list to see the main list
			Region node = list.getNode();
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.prefWidthProperty().bind(pinnedWindow.widthProperty());
			node.prefHeightProperty().bind(pinnedWindow.heightProperty());
			pinnedWindow.getChildren().clear();
			pinnedWindow.getChildren().add(node);
		}
	}
	
	/**
	 * Pins a focused Task into the window
	 * @param Task to be pinned
	 */
	protected static void pinFocusView(Region focusedTask) {
		if (focusedTask!=null) {
			unpinWindow();
			pane.setTop(pinnedWindow);
			focusedTask.prefWidthProperty().unbind();
			focusedTask.prefHeightProperty().unbind();
			focusedTask.prefWidthProperty().bind(pinnedWindow.widthProperty());
			focusedTask.prefHeightProperty().bind(pinnedWindow.heightProperty());
			focusedTask.setPadding(new Insets(0, PADDING, 0, PADDING));
			pinnedWindow.getChildren().clear();
			pinnedWindow.getChildren().add(focusedTask);
			isFocusView = true;
		}
	}

	/**
	 * Unpins all windows from the pinnedWindow
	 */
	protected static void unpinWindow() {
		unpinWindow(null);
	}

	/**
	 * Unpins all windows from the pinnedWindow
	 * @param list List to not unpin
	 */
	protected static void unpinWindow(TaskList list) { // list will be left out of unpinned
		isFocusView = false;
		int pinned = TASKLIST_PINNED;
		TASKLIST_PINNED = TASKLIST_INVALID;
		if (pinned!=TASKLIST_INVALID){//pinnedWindow.getChildren().size()>0) {
			// focus view deactivate regardless of pinned window or task
			TaskList pinnedList = taskLists.get(pinned);
			Region node = pinnedList.getNode();
			pinnedList.isPinnedWindow = false;
			closeList(pinnedList);
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.setPrefHeight(Region.USE_COMPUTED_SIZE);

			if (list!=null) {
				TASKLIST_PINNED = list.listNumber; // to avoid being added during refreshList()
			}
			
			// then update the center's list
			refreshLists();
		}
		pane.setTop(null);
		
	}

	public VBox createLogTab() {
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
		return logObject;
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

	public void processUserTextField(TextField userTextField) {
		String temp = userTextField.getText();
		userTextField.clear();
		executeCommand(temp);
	}

	/**
	 * Performs an action based on the command. GUI only comamnds are processed here
	 * @param parsedCommand Command after being parsed by Parser
	 * @return true if GUI has performed the necessary action, false if it needs to be passed to Logic for further action
	 */
	protected boolean checkForGuiActions(ParsedCommand parsedCommand) {
		CommandType command = parsedCommand.getCommandType();
		try {
			switch (command) {
			case GUI_SHOW: // show a specific task in the pinned window
				if (TASKLIST_PINNED!=TASKLIST_INVALID) {
					TaskList list = taskLists.get(TASKLIST_PINNED);
					if (list.isListOpen) {
						list.focusTask();
					} else {
						openList(list);
					}
					return true;
				} else if (TASKLIST_OPENED!=TASKLIST_INVALID) {
					pinFocusView(taskLists.get(TASKLIST_OPENED).getFocusTask());
					return true;
				}
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
				unpinWindow();
				return true;
			case GUI_PIN:
				pinWindow(taskLists.get(getTaskListNumber(parsedCommand.getGuiType())));
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
	protected String checkCommandType(ParsedCommand parsedCommand) {
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
					unpinWindow(); // unpin to prevent focus on deleted item
				}
				break;
			case EDIT: // focus on editted task
				focusOnTaskID(model.getFocusId());
				break;
			case DISPLAY: // focus on task to be displayed
				focusOnTaskID(parsedCommand.getTaskId());
				break;
			case HELP:
				// help menu?
				break;
			case CONFIG_IMG:
				// if it had been a Set function, it might have been an avatar or background, so reload them
				AVATAR_IMAGENAME = model.getAvatarLocation();
				if(!textboxObject.loadAvatar()) {
					output = "Cannot find new avatar specified";
				}
				break;
			case UNDO:
				if (TASKLIST_PINNED!=TASKLIST_INVALID) { // same as delete
					openList(TASKLIST_PINNED);
				} else {
					unpinWindow();
				}
				break;
			case SEARCH: // search function
				closeAllLists();
				TaskList search = taskLists.get(TASKLIST_SEARCH);
				if (!search.isListEmpty()) { // if it is not empty
					// deactivate focus view
					unpinWindow();
					isFocusView = false; 
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
			if (!checkForGuiActions(parsedCommand)) {
				try {
					model = controller.executeCommand(parsedCommand);
					// refresh log command, and get new iterator
					logCommands.addToTextbox(input);
					commandIterator = logCommands.getLog().getChildren().listIterator(
							logCommands.getLog().getChildren().size()); // get last item
					
					refreshLists(); // only needed if sent to logic
				} catch (Logic.UnrecognisedCommandException e) {
					//model.setConsoleMessage(output);
				}
				
				String output = checkCommandType(parsedCommand);
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
		textboxObject.addToTextbox(model.getConsoleMessage());
		logConsole.addToTextbox(model.getConsoleMessage());
	}

	/**
	 * Refreshes all displayed lists
	 */
	protected static void refreshLists() {
		for (TaskList list : taskLists) {
			switch(list.listNumber) {
			case TASKLIST_TODO: // all tasks tab
				taskLists.get(TASKLIST_TODO).addAllTasks(model.getMainList());;
				break;
			case TASKLIST_SEARCH: // search list
				list.addAllTasks(model.getSearchList());
				break;
			case TASKLIST_OVERDUE: // overdue list
				list.addAllTasks(model.getOverdueList());
				break;
			case TASKLIST_FLOATING: // floating tasks list
				list.addAllTasks(model.getFloatingList());
				break;
			case TASKLIST_TODAY:// today list 
				list.addAllTasks(model.getTodayList());
				break;
			default:
				break;
			}
		}
		
		if (center!=null) {
			int open = TASKLIST_OPENED;
			center.removeAllFromList();
			for (int i=0; i<taskLists.size();i++) {
				if (i!=TASKLIST_PINNED) {
					center.addToList(taskLists.get(i));
				}
			}
			openList(open); // keep the list that was previously opened open
		}
	}

	/**
	 * 
	 * @param text Sets this text to FancyText css style
	 */
	public static void setHeading(Label text) {
		text.getStyleClass().add(STYLE_HEADING);
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
			if (isFocusView) { // if there had been a previously opened focus view
				list.selectFirstNode();
				pinFocusView(list.getFocusTask());
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
			if (listNumber==TASKLIST_OPENED) {
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
				if (TASKLIST_PINNED==TASKLIST_INVALID) { 
					// if no pinned window, order is same as initial
					return getTaskListNumberFromMainWindowLocation(i);
				} else if (TASKLIST_PINNED!=TASKLIST_INVALID&&i==0) { 
					// if there is a pinned window and is first element
					return TASKLIST_PINNED;
				} else if (TASKLIST_PINNED!=TASKLIST_INVALID) {
					// if there is pinned window and not zero, means one of the main window
					return getTaskListNumberFromMainWindowLocation(i-1);
				}
			}
		} catch (NumberFormatException e) {
			model.setConsoleMessage("Invalid command");
			throw new Logic.UnrecognisedCommandException("Unable to parse integer"); 
		} catch (IndexOutOfBoundsException e) {
			model.setConsoleMessage("Unable to find selected tab");
			//throw new Logic.UnrecognisedCommandException("Unable to parse integer");
		}
		return TASKLIST_INVALID; // return invalid otherwise
	}
	
	/**
	 * Returns the task number of the item indicated in the location
	 * @param mainWindowLocation
	 * @return
	 * @throws Exception 
	 */
	protected int getTaskListNumberFromMainWindowLocation(int mainWindowLocation) throws IndexOutOfBoundsException {
		return center.listOfTaskLists.get(mainWindowLocation).listNumber;
	}
	
	/**
	 * Creates a focusView for the id number selected
	 */
	protected void focusOnTaskID(int id) {
		try {
			Task taskToFocus = Logic.searchList(model.getAllTasks(), id);
			pinFocusView(TaskList.createDetailedDisplay(taskToFocus));
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
			}
		}
		return false;
	}
}
