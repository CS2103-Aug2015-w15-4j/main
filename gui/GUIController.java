package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
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
						focusTask();
						closeList();
					} else {
						closeAllLists(); // new line that changes everything
						openList();
					}
				}
		    });
		}
	}

	final public static String[] taskListNames = {
			"All Tasks",  
			"Floating Tasks",
			"Completed Tasks",
			"Search list"
	};
	final static int TASKLIST_ALL = 0;
	final static int TASKLIST_FLOATING = 1;
	final static int TASKLIST_COMPLETED = 2;
	final static int TASKLIST_SEARCH = 3;
	final static int TASKLIST_INVALID = -1;
	public static int TASKLIST_PINNED = TASKLIST_INVALID;

	final static String APP_TITLE = "OraCle";
	final static String FILE_CSS = "application.css";

	final static String TAG_BOTTOMBAR = "bottombar";
	final static String TAG_TEXTBOX = "textbox";
	final static String TAG_TABPANE = "tabpane";

	final static String STYLE_CURVED_VBOX = "vbox-curved";
	final static String STYLE_CURVED_LABEL = "label-curved";
	final static String STYLE_FANCYTEXT = "fancytext";
	final static String STYLE_HEADING = "heading";
	final static String STYLE_TRANSPARENT = "transparent";
	final static String STYLE_TEXT = "label";
	final static String STYLE_COLOR = "-fx-background-color: %1$s;";

	final static String SEARCH_LIST_FORMAT = taskListNames[TASKLIST_SEARCH] + " - \"%1$s\"";
	final static String MSG_SUGGESTED_COMMAND_FORMAT = "Did you mean the \"%1$s\" command?";
	final static String MSG_PROMPT = "Type command here";
	final static String MSG_WINDOWSWITCH = "Switch"; // name for button

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
	final static String CMD_SWITCH = "switch"; // switches between the log and the main window
	final static String CMD_LOG = "log"; // switches to the log
	final static String CMD_MAIN = "main"; // switches to the main window

	public static String AVATAR_IMAGENAME;
	public static String BACKGROUND_NAME;
	public static String ICON_IMAGE = "icon.png";
	public static boolean isMainWindow = true; // true = main pane window, false = logObject

	final static int MINIMUM_WINDOW_SIZE = 600;

	// 1/ratio, ratio being the number to divide by
	final static int PINNED_WINDOW_RATIO = 3;
	final static int TEXTBOX_RATIO = 8;

	// Stores all lists
	public static ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
	
	// for getting old commands
	public static ListIterator<Node> commandIterator;
	final static boolean PREVIOUS = false;
	final static boolean NEXT = true;
	
	final Pane window = new VBox();
	public Log logCommands;
	public Log logConsole;
	public VBox logObject;
	public Textbox textboxObject;
	public BorderPane pane;
	public MainWindow center;
	public logic.Model model;
	public VBox pinnedWindow;

	public static HBox bottomBar; // bottomMost bar
	public static TextField userTextField;
	public static Button windowSwitch;
	//public static TabPane tabPane;
	public static Logic controller = new Logic();

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
		BACKGROUND_NAME = model.getBackgroundLocation();

		for (int i=0; i<taskListNames.length;i++) {
			taskLists.add(new TaskListCustom(i)); // use this version to allow to close all other task lists
		}

		// intialise the all task lists
		refreshLists();

		// then pin it as the first task window
		pinnedWindow = new VBox();
		pinnedWindow.prefWidthProperty().bind(pane.widthProperty());
		pinnedWindow.prefHeightProperty().bind(pane.heightProperty().divide(PINNED_WINDOW_RATIO));
		pinnedWindow.getStyleClass().add(STYLE_CURVED_VBOX);
		//pane.setTop(pinnedWindow);
		//pinWindow(taskLists.get(TASKLIST_ALL));

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
		taskLists.get(TASKLIST_SEARCH).addAllTasks(model.getTasksToDisplay());
		for (int i=0; i<taskLists.size();i++) {
			center.addToList(taskLists.get(i));
		}
		center.getNode().prefWidthProperty().bind(pane.widthProperty());
		center.getNode().maxWidthProperty().bind(pane.widthProperty());
		pane.setCenter(center.getNode());

		pane.prefWidthProperty().bind(window.widthProperty());
		pane.prefHeightProperty().bind(window.heightProperty());
		window.setMinWidth(MINIMUM_WINDOW_SIZE);
		window.setMinHeight(MINIMUM_WINDOW_SIZE);

		window.getChildren().add(pane);
		window.getChildren().add(bottomBar);
		Scene scene = new Scene(window, window.getPrefWidth(), window.getPrefHeight()); //WINDOW_WIDTH+10, WINDOW_HEIGHT+10);
		addHandlers(scene);
		//scene.getRoot().setStyle("-fx-background-image: url(\"" + BACKGROUND_NAME + "\");");
		userTextField.requestFocus();
		primaryStage.setScene(scene);
		primaryStage.setTitle(APP_TITLE);
		scene.getStylesheets().add(GUIController.class.getResource(FILE_CSS).toExternalForm());
		primaryStage.getIcons().add(new Image(
				GUIController.class.getResourceAsStream( ICON_IMAGE )));
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
	}

	protected void pinWindow(TaskList list) {
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

	/**
	 * Unpins all windows from the pinnedWindow
	 */
	protected void unpinWindow() { // list will be left out of unpinned
		unpinWindow(null);
	}

	/**
	 * Unpins all windows from the pinnedWindow
	 * @param list List to not unpin
	 */
	protected void unpinWindow(TaskList list) { // list will be left out of unpinned
		pane.setTop(null);
		if (pinnedWindow.getChildren().size()>0) {
			// unpin the top
			openList(TASKLIST_PINNED);
			Region node = taskLists.get(TASKLIST_PINNED).getNode();
			TASKLIST_PINNED = TASKLIST_INVALID;
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.setPrefHeight(Region.USE_COMPUTED_SIZE);

			if (list!=null) {
				list.isPinnedWindow = false;
			}

			// then update the center's list
			center.removeAllFromList();
			for (int i=0; i<taskLists.size();i++) {
				if (list==null||i!=list.listNumber) {
					center.addToList(taskLists.get(i));
				}
			}
		}
	}

	public VBox createLogTab() {
		logObject = new VBox();
		logCommands = new Log("Commands");
		logConsole = new Log("Console");
		logObject.getChildren().add(logCommands.getNode());
		logObject.getChildren().add(logConsole.getNode());
		logCommands.getNode().prefHeightProperty().bind(logObject.heightProperty().divide(2));
		logCommands.getNode().prefWidthProperty().bind(logObject.widthProperty());
		logConsole.getNode().prefHeightProperty().bind(logObject.heightProperty().divide(2));
		logConsole.getNode().prefWidthProperty().bind(logObject.widthProperty());
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
	 * Adds handlers to the scene
	 */
	public void addHandlers(Scene scene) {
		/*
		private static class HoverScrollHandler implements EventHandler<MouseEvent> {

		    @Override
		    public void handle(MouseEvent event) {
		        scrollpane.setHvalue(label.getWidth());
		    }
		}//*/

		// event handler for userTextField
		userTextField.setOnAction((ActionEvent event) -> processUserTextField(userTextField));
		userTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode()==KeyCode.UP) { // get last item typed in, unless it is first
					String lastCommand = getCommandLog(PREVIOUS); // get previous
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userTextField.setText(lastCommand);
					}
				}
				
				if (keyEvent.getCode()==KeyCode.DOWN) { // get next item typed in, unless it is last
					String lastCommand = getCommandLog(NEXT); // get next item
					if (!lastCommand.isEmpty()) { // if not empty, replace current userTextField data
						userTextField.setText(lastCommand);
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
				taskLists.get(TASKLIST_ALL).recalculate();
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
					} else if (keyEvent.isAltDown()) {
						// switch
						switchWindow();
					}
				}

				if(keyEvent.getCode()==KeyCode.BACK_SLASH) { // zoom in on a task
					if (TASKLIST_PINNED!=TASKLIST_INVALID) {
						taskLists.get(TASKLIST_PINNED).focusTask();
					} else {
						listLoop: for (TaskList list : taskLists) {
							for (Node node : list.getNode().getChildren()) {
								if (node.isFocused()) {
									list.focusTask();
									openList(list);
									break listLoop;
								}
							}
						}
					}
				}

				if ((keyEvent.getCode()==KeyCode.BACK_SLASH&&keyEvent.isShiftDown())||
						keyEvent.getCode()==KeyCode.BACK_SPACE) { // see the main list again
					openList(TASKLIST_PINNED);
				}

				if (keyEvent.getCode()==KeyCode.Z&&
						(keyEvent.isControlDown()||keyEvent.isAltDown())) { // undo the last command
					executeCommand("undo");
				}
			}
		}));//*/
	}

	public static void main(String[] args) {
		launch(args);
	}

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
			case GUI_OPEN_ALL:
				for (TaskList list : taskLists) {
					if (list.listNumber!=TASKLIST_PINNED) {
						openList(list);
					}
				}
				return true;
			case GUI_CLOSE_ALL:
				closeAllLists();
				return true;
			case GUI_SHOW: // show a specific task in the pinned window
				TaskList list = taskLists.get(TASKLIST_PINNED);
				if (list.isListOpen) {
					list.focusTask();
				} else {
					openList(list);
				}
				return true;
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
				openList(taskLists.get(getTaskListNumber(parsedCommand.getGuiType())));
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
			for (int i=0;i<ParsedCommand.COMMAND_CHOICES.length;i++) {
				if (command.equals(ParsedCommand.COMMAND_CHOICES[i].commandType)) {
					model.setConsoleMessage(
							String.format(MSG_SUGGESTED_COMMAND_FORMAT, 
									ParsedCommand.COMMAND_CHOICES[i].str[0]));
					break;
				}
			}
			return true; // because it was caught by GUI controller
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // if not valid, return false
	}

	protected int getTaskListNumber(String processedString) throws Logic.UnrecognisedCommandException {
		String[] split = processedString.trim().split("\\s+");
		if (split.length>=1) {
			try {
				int i = Integer.parseInt(split[0]);
				if (split.length>=2) { 
					// means that it should have been validated by parser. Can return immediately
					return i; 
				} else if (split.length==1) { // if it is only one item, means it is a number that needs processing to check positioning
					if (TASKLIST_PINNED==TASKLIST_INVALID) { 
						// if no pinned window, order is same as initial
						return i;
					} else if (TASKLIST_PINNED!=TASKLIST_INVALID&&i==0) { 
						// if there is a pinned window and is first element
						return TASKLIST_PINNED;
					} else if (TASKLIST_PINNED!=TASKLIST_INVALID&&i>TASKLIST_PINNED) {
						// if pinned window, but number is bigger
						return i;
					} else if (TASKLIST_PINNED!=TASKLIST_INVALID&&i<=TASKLIST_PINNED) {
						return i-1;
					}
				}
			} catch (NumberFormatException e) {
				model.setConsoleMessage("Invalid command");
				throw new Logic.UnrecognisedCommandException("Unable to parse integer"); 
			}
		}
		return TASKLIST_INVALID; // return invalid otherwise
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
			case ADD: // focus on the newly added task
				TaskList list = taskLists.get(TASKLIST_ALL); 
				if (list.isPinnedWindow) {
					list.selectNode(list.listOfTasks.size()-1);
					list.focusTask();
				}
				break;
			case DELETE:
				openList(TASKLIST_PINNED); // open the list to prevent focus on deleted item 
				break;
			case HELP:
				// help menu?
				break;
			case CONFIG_IMG:
				// if it had been a Set function, it might have been an avatar or background, so reload them
				AVATAR_IMAGENAME = model.getAvatarLocation();
				BACKGROUND_NAME = model.getBackgroundLocation(); 
				if(!textboxObject.loadAvatar()) {
					output = "Cannot find new avatar specified";
				}
				break;
			case UNDO:
				openList(TASKLIST_PINNED); // same as delete
				break;
			case SEARCH: // search function
				executeCommand(CMD_CLOSEALL); // close all
				openList(TASKLIST_SEARCH); // focus on search 
				// then modify the Search List name to include the search term
				taskLists.get(TASKLIST_SEARCH).setName(
					String.format(SEARCH_LIST_FORMAT,parsedCommand.getKeywords())
				);
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
			model.setConsoleMessage("");
		}
		textboxObject.addToTextbox(model.getConsoleMessage());
		logConsole.addToTextbox(model.getConsoleMessage());
	}

	/**
	 * Refreshes all displayed lists
	 */
	protected void refreshLists() {
		// all tasks tab
		taskLists.get(TASKLIST_ALL).addAllTasks(model.getAllTasks());

		// search list
		taskLists.get(TASKLIST_SEARCH).addAllTasks(model.getTasksToDisplay());

		// Completed list
		try {
			List<Task> tasks = logic.Search.search(model.getAllTasks(), "isCompleted:true");
			if (tasks!=null) {
				taskLists.get(TASKLIST_COMPLETED).addAllTasks(tasks);
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}

		// floating tasks list
		//*
		try {
			List<Task> floating = logic.Search.search(model.getAllTasks(), "taskType:FLOATING_TASK");
			floating = logic.Search.search(floating, "isCompleted:false");
			if (floating!=null) {
				taskLists.get(TASKLIST_FLOATING).addAllTasks(floating);
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}//*/
	}

	/**
	 * 
	 * @param text Sets this text to FancyText css style
	 */
	public static void setFancyText(Label text) {
		text.getStyleClass().add(STYLE_FANCYTEXT);
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
	protected static void openList(int listNumber) {
		if (listNumber>=0&&listNumber<taskListNames.length) { // if valid
			if (listNumber!=TASKLIST_PINNED) {
				// if it is for pinned window, no need to close anything
				// else close everything
				closeAllLists();
			}	
			taskLists.get(listNumber).openList();
		}
	}
	
	/**
	 * Opens a specified list and closes all other lists at the same time
	 * @param list list to open
	 */
	protected static void openList(TaskList list) {
		openList(list.listNumber);
	}
	
	/**
	 * Opens a specified list and closes all other lists at the same time
	 * @param listNumber list to open
	 */
	protected static void closeList(int listNumber) {
		if (listNumber>=0&&listNumber<taskListNames.length) { // if valid
			taskLists.get(listNumber).closeList();
		}
	}
	
	/**
	 * Closes this list
	 * @param list
	 */
	protected static void closeList(TaskList list) {
		closeList(list.listNumber);
	}
}
