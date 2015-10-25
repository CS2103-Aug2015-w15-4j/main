package gui;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import logic.Logic;
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
	
	// gui commands 
	final static String CMD_CLEAR = "clear"; // clears all log
	final static String CMD_CLOSEALL = "close all"; // closes all open task lists
	final static String CMD_PIN_WINDOW = "pin"; // pin XXX: pins a selected task list
	final static String CMD_SHOW = "show"; // highlights a selected task in the main view
	
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
	
	public static TextField userTextField;
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
				
		// create input field
		userTextField = new TextField();
		//userTextField.setMinWidth(TABPANE_WIDTH);
		userTextField.setPromptText(MSG_PROMPT);
		userTextField.prefWidthProperty().bind(window.widthProperty());

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
	    window.getChildren().add(userTextField);
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
	            	} else {//if (keyEvent.isAltDown()) {
	            		// switch
	            		window.getChildren().clear();
	            		if (isMainWindow) {
	            			window.getChildren().add(pane);
	            			pane.requestFocus();
	            		} else {
	            			window.getChildren().add(logObject);
	            			logObject.requestFocus();
	            		}
	            		window.getChildren().add(userTextField);
	            		isMainWindow = !isMainWindow;
	            	}
	            }
	            
	            if(keyEvent.getCode()==KeyCode.BACK_SLASH) { // zoom in on a task
	            	taskLists.get(TASKLIST_ALL).focusTask();
	            }
	            
	            if ((keyEvent.getCode()==KeyCode.BACK_SLASH&&keyEvent.isShiftDown())||
	            		keyEvent.getCode()==KeyCode.BACK_SPACE) { // see the main list again
	            	taskLists.get(TASKLIST_ALL).openList();
	            }
	            
	        }
	    }));//*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void processUserTextField(TextField userTextField) {
		String temp = userTextField.getText();
    	userTextField.clear();
		executeCommand(temp);
	}
	
	protected boolean checkForGuiCommand(String command) {
		if (getFirstWord(command).equalsIgnoreCase(CMD_PIN_WINDOW)) {
			String input = removeFirstWord(command);
			for (int i=0;i<taskListNames.length;i++) {
				if (input.equalsIgnoreCase(taskListNames[i])) {
					pinWindow(taskLists.get(i));
					return true;
				}
			}
		} else if (command.trim().equalsIgnoreCase(CMD_CLOSEALL)) {
			for (TaskList list : taskLists) {
				if (list.listNumber!=TASKLIST_PINNED) {
					list.closeList();
				}
			}
			return true;
		} else if (command.trim().equalsIgnoreCase(CMD_SHOW)) {
			taskLists.get(TASKLIST_ALL).focusTask();
		}
		return false;
	}
	
	protected void executeCommand(String input) {
		if (input!= null && !input.isEmpty()) {
			if (!checkForGuiCommand(input)) { 
				view = controller.executeCommand(input.trim());
				textboxObject.addToTextbox(view.getConsoleMessage());
				logCommands.addToTextbox(input);
				logConsole.addToTextbox(view.getConsoleMessage());
				taskLists.get(TASKLIST_ALL).addAllTasks(view.getAllTasks());
				taskLists.get(TASKLIST_SEARCH).addAllTasks(view.getTasksToDisplay());
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
