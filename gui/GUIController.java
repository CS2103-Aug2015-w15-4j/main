package gui;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import logic.Logic;
import parser.ParsedCommand;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

public class GUIController extends Application {
	/*
	 private static volatile GUIController gui;
	 private GUIController(){};
	 
	 public static GUIController getInstance() {
		 if (gui==null) {
			 gui = new GUIController();
		 }
		 return gui;
	 }//*/
	 
	final static String[] taskListNames = {
		"All Tasks",  
		"Search list",
		"Temp 2",
		"Temp 3"
	};
	final static int TASKLIST_ALL = 0;
	final static int TASKLIST_SEARCH = 1;
	
	public static int TASKLIST_PINNED = -1;
	
	final static String APP_TITLE = "OraCle";
	final static String FILE_CSS = "application.css";
	
	final static String TAG_SIDEBAR = "sidebar";
	final static String TAG_SIDEBAR_TEXTBOX = "sidebar-textbox";
	final static String TAG_TABPANE = "tabpane";
	final static String STYLE_FANCYTEXT = "fancytext";
	final static String STYLE_HEADING = "heading";
	final static String STYLE_TRANSPARENT = "transparent";
	final static String STYLE_TEXT = "label";
	
	final static String MSG_PROMPT = "Type command here";
	final static String MSG_WINDOWSWITCH = "Switch"; // name for button
	
	// gui commands 
	final static String CMD_CLEAR = "clear"; // clears all log
	final static String CMD_PIN = "pin"; // pin XXX: pins a selected task list
	final static String CMD_OPEN = "open"; // open XXX: opens a selected task list
	final static String CMD_CLOSE = "close"; // close XXX: closes a selected task list
	final static String CMD_CLOSEALL = "close all"; // closes all open task lists
	final static String CMD_SHOW = "show"; // focuses on a selected task in the main view
	final static String CMD_SWITCH = "switch"; // switches between the log and the main window
	final static String CMD_LOG = "log"; // switches to the log
	final static String CMD_MAIN = "main"; // switches to the main window
	
	final static String STYLE_COLOR = "-fx-background-color: %1$s;";

	public static String AVATAR_IMAGENAME = "avatar.png";
	public static String BACKGROUND_NAME = "background.jpg";
	public static String ICON_IMAGE = "icon.png";
	
	final static int MINIMUM_WINDOW_SIZE = 600;
	
	// 1/ratio, ratio being the number to divide by
	final static int PINNED_WINDOW_RATIO = 3;
	final static int TEXTBOX_RATIO = 8;
	
	public ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
	final Pane window = new VBox();
	public Log logCommands;
	public Log logConsole;
	public VBox logObject;
	public Textbox textboxObject;
	public BorderPane pane;
	public MainWindow center;
	protected boolean isMainWindow = true; // true = main pane window, false = logObject
	public logic.View view;
	
	public static HBox bottomBar; // bottomMost bar
	public static TextField userTextField;
	public static Button windowSwitch;
	//public static TabPane tabPane;
	public static Logic controller = new Logic();
	
	@Override
	public void start(Stage primaryStage) {
		view = controller.executeCommand("");
		for (int i=0; i<taskListNames.length;i++) {
			taskLists.add(new TaskList(i));
		}
		
		// prevent resizing?
		//primaryStage.setResizable(false); // allow resizing?
		
		/**
		 * main interface manager
		 * Splits the different sections apart
		 */
		pane = new BorderPane();
		
		// create the text
		textboxObject = new Textbox();
		textboxObject.getNode().prefWidthProperty().bind(pane.widthProperty());
		textboxObject.getNode().maxWidthProperty().bind(pane.widthProperty());
		textboxObject.getNode().maxHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		textboxObject.getNode().prefHeightProperty().bind(pane.heightProperty().divide(TEXTBOX_RATIO));
		pane.setBottom(textboxObject.getNode());
		
		// intialise the all tasks tab
		taskLists.get(TASKLIST_ALL).addAllTasks(view.getAllTasks());
		taskLists.get(TASKLIST_ALL).selectFirstNode();

		// then pin it as the first task window
		pinWindow(taskLists.get(TASKLIST_ALL));
		
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

		// add all other lists to to the center
		center = new MainWindow();
		taskLists.get(TASKLIST_SEARCH).addAllTasks(view.getTasksToDisplay());
		for (int i=0; i<taskLists.size();i++) {
			if (i!=TASKLIST_ALL) {
				center.addToList(taskLists.get(i));
			}
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
		if (pane.getTop()!=null) {
			// unpin the top
			Region node = taskLists.get(TASKLIST_PINNED).getNode();
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.setPrefHeight(Region.USE_COMPUTED_SIZE);
			
			// then update the center's list
			center.removeAllFromList();
			for (int i=0; i<taskLists.size();i++) {
				if (i!=list.listNumber) {
					center.addToList(taskLists.get(i));
				}
			}
		}
		
		TASKLIST_PINNED = list.listNumber;
		list.isPinnedWindow = true;
		list.selectFirstNode(); // select the first node, since it is moving up now
		list.focusTask(); // create an instance of zoomed in task
		list.openList(); // open the list to see the main list
		Region node = list.getNode();
		node.prefWidthProperty().unbind();
		node.prefHeightProperty().unbind();
		node.prefWidthProperty().bind(pane.widthProperty());
		node.prefHeightProperty().bind(pane.heightProperty().divide(PINNED_WINDOW_RATIO));
		pane.setTop(node);
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
	            	taskLists.get(TASKLIST_ALL).focusTask();
	            }
	            
	            if ((keyEvent.getCode()==KeyCode.BACK_SLASH&&keyEvent.isShiftDown())||
	            		keyEvent.getCode()==KeyCode.BACK_SPACE) { // see the main list again
	            	taskLists.get(TASKLIST_ALL).openList();
	            }
	            
	            if (keyEvent.getCode()==KeyCode.Z&&
	            	(keyEvent.isControlDown()||keyEvent.isAltDown())) { // undo the last command
	            	executeCommand(ParsedCommand.UNDO_CHOICES[0]);
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
			pane.requestFocus();
		} else {
			window.getChildren().add(logObject);
			logObject.requestFocus();
		}
		window.getChildren().add(bottomBar);
		isMainWindow = !isMainWindow;
	}
	
	public void processUserTextField(TextField userTextField) {
		String temp = userTextField.getText();
    	userTextField.clear();
		executeCommand(temp);
	}
	
	protected boolean checkForGuiCommand(String command) {
		if (command.trim().equalsIgnoreCase(CMD_CLOSEALL)) {
			for (TaskList list : taskLists) {
				if (list.listNumber!=TASKLIST_PINNED) {
					list.closeList();
				}
			}
			return true;
		} else if (command.trim().equalsIgnoreCase(CMD_SHOW)) {
			TaskList list = taskLists.get(TASKLIST_ALL);
			if (list.isListOpen) {
				list.focusTask();
			} else {
				list.openList();
			}
			return true;
		} else if (command.trim().equalsIgnoreCase(CMD_SWITCH)|| // if switch command
				// else if it is the log command and is the main window currently
				(command.trim().equalsIgnoreCase(CMD_LOG)&&isMainWindow)|| 
				(command.trim().equalsIgnoreCase(CMD_MAIN)&&!isMainWindow)) {
			switchWindow();
			return true;
		} else {
			String input = getFirstWord(command).toLowerCase();
			if (input.equalsIgnoreCase(CMD_PIN)||
				input.equalsIgnoreCase(CMD_OPEN)||
				input.equalsIgnoreCase(CMD_CLOSE)) {
				String listName = removeFirstWord(command);
				for (int i=0;i<taskListNames.length;i++) {
					if (listName.equalsIgnoreCase(taskListNames[i])) {
						input = input.toLowerCase();
						switch(input) {
						case CMD_PIN: pinWindow(taskLists.get(i)); break;
						case CMD_OPEN: taskLists.get(i).openList();; break;
						case CMD_CLOSE: taskLists.get(i).closeList(); break;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks for what kind of command it was, so that it can be used to focus on the correct object
	 * @param command The first word of the command
	 */
	protected void checkCommandType(String command) {
		for (int i=0;i<5;i++) {
			if (ParsedCommand.ADD_CHOICES.length>i&&
					command.trim().equalsIgnoreCase(ParsedCommand.ADD_CHOICES[i])) {
				TaskList list = taskLists.get(TASKLIST_ALL);
				if (list.isPinnedWindow) {
					list.selectNode(list.listOfTasks.size()-1);
					list.focusTask();
				}
				break;
			} else if (ParsedCommand.DELETE_CHOICES.length>i&&
					command.trim().equalsIgnoreCase(ParsedCommand.DELETE_CHOICES[i])) {
				taskLists.get(TASKLIST_PINNED).openList(); 
				// open the list to prevent focus on an item that may not exist anymore
				break;
			} else if (ParsedCommand.HELP_CHOICES.length>i&&
					command.trim().equalsIgnoreCase(ParsedCommand.HELP_CHOICES[i])) {
				// help menu?
				break;
			} else if (ParsedCommand.SHOW_CHOICES.length>i&&
					command.trim().equalsIgnoreCase(ParsedCommand.SHOW_CHOICES[i])) {
				TaskList search = taskLists.get(TASKLIST_SEARCH);
				executeCommand(CMD_CLOSEALL);
				search.openList();
				break;
			}
		}
	}
	/**
	 * Runs the command input. 
	 * @param input 
	 */
	protected void executeCommand(String input) {
		if (input!= null && !input.isEmpty()) {
			if (!checkForGuiCommand(input)) {
				view = controller.executeCommand(input.trim());
				textboxObject.addToTextbox(view.getConsoleMessage());
				logCommands.addToTextbox(input);
				logConsole.addToTextbox(view.getConsoleMessage());
				taskLists.get(TASKLIST_ALL).addAllTasks(view.getAllTasks());
				taskLists.get(TASKLIST_SEARCH).addAllTasks(view.getTasksToDisplay());
				checkCommandType(getFirstWord(input));
			}
		}
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
	
	private static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}
	
	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
}
