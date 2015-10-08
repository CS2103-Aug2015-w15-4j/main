package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.stage.Stage;
import logic.Logic;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class GUI extends Application {
	final static String[] tabNames = {
		"Task",  
		"Log" // command log 
	};
	final static int TASK = 0;
	final static int LOG = 1;
	
	final static String APP_TITLE = "Prototype";
	final static String FILE_CSS = "application.css";
	
	final static String TAG_SIDEBAR = "sidebar";
	final static String TAG_SIDEBAR_TEXTBOX = "sidebar-textbox";
	final static String TAG_TABPANE = "tabpane";
	final static String ID_FANCYTEXT = "fancytext";
	
	final static String MSG_PROMPT = "Type command here";
	final static String CMD_CLEAR = "clear";
	
	final static String STYLE_COLOR = "-fx-background-color: %1$s;";
	
	final static int WINDOW_WIDTH = 16 * 60;
	final static int WINDOW_HEIGHT = 9 * 60;
	final static int TAB_WIDTH = 50;
	final static int TABPANE_WIDTH = 13* WINDOW_WIDTH/16;
	final static int TABPANE_HEIGHT = (int)(8.7*WINDOW_HEIGHT/9);
	final static int TABPANE_SIDEBAR_WIDTH = TABPANE_WIDTH/5;
	final static int TABPANE_SIDEBAR_HEIGHT = TABPANE_HEIGHT;
	final static int SIDEBAR_WIDTH = 3*WINDOW_WIDTH/16;
	final static int SIDEBAR_MIN_HEIGHT = 0;
	final static int SIDEBAR_MAX_HEIGHT = WINDOW_HEIGHT;
	final static Pos SIDEBAR_ORIENTATION = Pos.BOTTOM_CENTER;//Pos.BOTTOM_LEFT;

	public static String AVATAR_IMAGENAME = "avatar2.png";
	public static String BACKGROUND_NAME = "background.jpg";
	
	public TaskTab taskObject;
	public Log logCommands;
	public Log logConsole;
	public VBox logObject;
	public Sidebar sidebarObject;
	public AnchorPane center;
	
	public static TextField userTextField;
	public static TabPane tabPane;
	public static Logic controller = new Logic();
	
	@Override
	public void start(Stage primaryStage) {
		// prevent resizing
		primaryStage.setResizable(false);
		
		/**
		 * main interface manager
		 * Splits the different sections apart
		 */
		BorderPane border = new BorderPane();
		border.setMaxSize(WINDOW_WIDTH, WINDOW_HEIGHT);	
		// create the text
		sidebarObject = new Sidebar();
		border.setRight(sidebarObject.getNode()); // put the sidebar on the right side
		
		// Tab manager
		tabPane = new TabPane();//*
		tabPane.setMinWidth(TABPANE_WIDTH);
		tabPane.setMaxWidth(TABPANE_WIDTH);
		tabPane.setMinHeight(TABPANE_HEIGHT);
		tabPane.setMaxHeight(TABPANE_HEIGHT);//*/
		tabPane.setId(TAG_TABPANE);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		// Create tabs
		for (int i=0; i<tabNames.length;i++) {
			Tab tab = new Tab(String.format("%-10s", tabNames[i]));
			tab.setId(tabNames[i]);
			tabPane.getTabs().add(tab);
		}
		// add Task tab
		taskObject = new TaskTab();
		tabPane.getTabs().get(TASK).setContent(taskObject.getNode());
		taskObject.getNode().prefWidthProperty().bind(tabPane.prefWidthProperty());
		taskObject.getNode().maxWidthProperty().bind(tabPane.maxWidthProperty());
		// intialise tasks
		taskObject.addAllTasks(Logic.initializeTaskList().getAllTasks());
		taskObject.selectFirstNode();
		// add Log tab
		logObject = new VBox();
		logCommands = new Log();
		logConsole = new Log();
		logObject.getChildren().add(logCommands.getNode());
		logObject.getChildren().add(logConsole.getNode());
		VBox.setVgrow(logCommands.getNode(), Priority.ALWAYS);
		VBox.setVgrow(logConsole.getNode(), Priority.ALWAYS);
		tabPane.getTabs().get(LOG).setContent(logObject);
		
		// create input field
		userTextField = new TextField();
		userTextField.setMinWidth(TABPANE_WIDTH);
		userTextField.setPromptText(MSG_PROMPT);
		userTextField.setOnAction((ActionEvent event) -> 
			processUserTextField(userTextField, TABPANE_HEIGHT));
		
		// add them to the center
		center = new AnchorPane();
		center.getChildren().addAll(tabPane, userTextField);
		center.setMaxHeight(TABPANE_WIDTH);
		AnchorPane.setTopAnchor(tabPane, 0.0);
		AnchorPane.setLeftAnchor(tabPane, 0.0);
		AnchorPane.setBottomAnchor(userTextField, 0.0);
		AnchorPane.setLeftAnchor(userTextField, 0.0);
		border.setCenter(center);
		
		Scene scene = new Scene(border, WINDOW_WIDTH+10, WINDOW_HEIGHT+10);//border.getPrefWidth(), border.getPrefHeight());
		addHandlers(scene);
		//scene.getRoot().setStyle("-fx-background-image: url(\"" + BACKGROUND_NAME + "\");");
		userTextField.requestFocus();
		primaryStage.setScene(scene);
	    primaryStage.setTitle(APP_TITLE);
	    scene.getStylesheets().add(GUI.class.getResource(FILE_CSS).toExternalForm());
	    primaryStage.show();
	}
	
	/**
	 * Adds handlers to the scene
	 */
	public void addHandlers(Scene scene) {
		//*
		scene.setOnKeyPressed((new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent keyEvent) {
	            if(keyEvent.getCode().toString() == "`"){
	            	userTextField.requestFocus();
	            }
	        }
	    }));//*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void processUserTextField(TextField userTextField, int height) {
		String temp = userTextField.getText();
    	userTextField.clear();
		executeCommand(temp, height);
	}
	
	public void executeCommand(String input, int height) {
		if (input!= null && !input.isEmpty()) {
			if (input.trim().equalsIgnoreCase(CMD_CLEAR)) {
				//textbox.getChildren().clear();
				sidebarObject.textbox.getChildren().clear();
			}
			else {
				logic.View view = controller.executeCommand(input.trim());
				sidebarObject.addToTextbox(view.getConsoleMessage());
				logCommands.addToTextbox(input);
				logConsole.addToTextbox(view.getConsoleMessage());
				taskObject.addAllTasks(view.getAllTasks());
			}
	    }
	}
	
	/**
	 * 
	 * @param text Sets this text to FancyText css style
	 */
	public static void setFancyText(Node text) {
		text.setId(ID_FANCYTEXT);
	}
	
	/**
	 * 
	 * @param flow The TextFlow being added to
	 * @param text The Text which requires a \n
	 */
	public static void addParagraphToTextFlow(TextFlow flow, Text text) {
		text.setText(text.getText()+"\n");
		flow.getChildren().add(text);
	}
}
