package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import logic.Logic;
import logic.Task;
import parser.ParsedCommand;
import parser.ParsedCommand.CommandType;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

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
	
	/*
	 private static volatile GUIController gui;
	 private GUIController(){};

	 public static GUIController getInstance() {
		 if (gui==null) {
			 gui = new GUIController();
		 }
		 return gui;
	 }//*/

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

	public static int TASKLIST_PINNED = -1;

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

	public static ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
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
		model = controller.executeCommand(null);
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
			Region node = taskLists.get(TASKLIST_PINNED).getNode();
			TASKLIST_PINNED = -1;
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

		// event handler for button
		windowSwitch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switchWindow();
			}
		});

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
					if (TASKLIST_PINNED!=-1) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // if not valid, return false
	}

	protected int getTaskListNumber(String processedString) {
		String[] split = processedString.trim().split("\\s+");
		if (split.length>=1) {
			try {
				int i = Integer.parseInt(split[0]);
				if (split.length>=2) { 
					// means that it should have been validated by parser. Can return immediately
					return i; 
				} else if (split.length==1) { // if it is only one item, means it is a number that needs processing to check positioning
					if (TASKLIST_PINNED==-1) { 
						// if no pinned window, order is same as initial
						return i;
					} else if (TASKLIST_PINNED!=-1&&i==0) { 
						// if there is a pinned window and is first element
						return TASKLIST_PINNED;
					} else if (TASKLIST_PINNED!=-1&&i>TASKLIST_PINNED) {
						// if pinned window, but number is bigger
						return i;
					} else if (TASKLIST_PINNED!=-1&&i<=TASKLIST_PINNED) {
						return i-1;
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return -1; // return invalid otherwise
	}

	/**
	 * Checks for what kind of command it was, so that it can be used to focus on the correct object
	 * @param command The first word of the command
	 */
	protected void checkCommandType(ParsedCommand parsedCommand) {
		CommandType command = parsedCommand.getCommandType();
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
			case CONFIG:
				// if it had been a Set function, it might have been an avatar or background, so reload them
				AVATAR_IMAGENAME = model.getAvatarLocation();
				BACKGROUND_NAME = model.getBackgroundLocation(); 
				textboxObject.loadAvatar();
				break;
			case UNDO:
				openList(TASKLIST_PINNED); // same as delete
				break;
			case SHOW: // search function
				executeCommand(CMD_CLOSEALL); // close all
				openList(TASKLIST_SEARCH); // focus on search
				break;
			default:
				break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// do nothing
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//*/
	/**
	 * Runs the command input. 
	 * @param input 
	 */
	protected void executeCommand(String input) {
		if (input!= null && !input.isEmpty()) {
			ParsedCommand parsedCommand = ParsedCommand.parseCommand(input.trim());
			if (!checkForGuiActions(parsedCommand)) {
				model = controller.executeCommand(parsedCommand);
				textboxObject.addToTextbox(model.getConsoleMessage());
				logCommands.addToTextbox(input);
				logConsole.addToTextbox(model.getConsoleMessage());
				refreshLists();
				checkCommandType(parsedCommand);
			}
		}
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
