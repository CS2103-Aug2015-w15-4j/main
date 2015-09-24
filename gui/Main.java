package gui;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;


public class Main extends Application {
	final static String[] tabNames = { 
		"Calender", 
		"Task", 
		"Today", 
		"Log" // command log 
	};
	final static int CAL = 0;
	final static int TASK = 1;
	final static int TODAY = 2;
	final static int LOG = 3;
	
	final static String APP_TITLE = "Prototype";
	final static String FILE_CSS = "application.css";
	
	final static String TAG_SIDEBAR = "sidebar";
	final static String TAG_SIDEBAR_TEXTBOX = "sidebar-textbox";
	
	final static String MSG_PROMPT = "Type command here";
	final static String CMD_CLEAR = "clear";
	
	final static int WINDOW_WIDTH = 800;
	final static int WINDOW_HEIGHT = 600;
	final static int TAB_WIDTH = 50;
	final static int TABPANE_WIDTH = 600;
	final static int TABPANE_HEIGHT = 400;
	final static int SIDEBAR_WIDTH = 150;
	final static int SIDEBAR_MIN_HEIGHT = 0;
	final static int SIDEBAR_MAX_HEIGHT = 400;
	final static int SIDEBAR_PADDING_LATERAL = 12;
	final static int SIDEBAR_PADDING_VERTICAL = 10;
	final static Pos SIDEBAR_ORIENTATION = Pos.BOTTOM_LEFT;
	
	@Override
	public void start(Stage primaryStage) {
		// prevent resizing
		primaryStage.setResizable(false);
		
		/**
		 * main interface manager
		 * Splits the different sections apart
		 */
		BorderPane border = new BorderPane();
		border.maxHeight(WINDOW_HEIGHT);
		border.maxWidth(WINDOW_WIDTH);

		VBox sidebar = createVBox(TAG_SIDEBAR);
		border.setRight(sidebar); // put the sidebar on the right side
		
		// create the text
		TextFlow textbox = new TextFlow();
		textbox.setId(TAG_SIDEBAR_TEXTBOX);
		textbox.setMaxWidth(SIDEBAR_WIDTH);
		textbox.setMinWidth(SIDEBAR_WIDTH);
		textbox.setMaxHeight(SIDEBAR_MAX_HEIGHT);
		textbox.setMinHeight(SIDEBAR_MIN_HEIGHT);
		sidebar.getChildren().add(textbox); 
		
		// Tab manager
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		// Create tabs
		for (int i=0; i<tabNames.length;i++) {
			Tab tab = new Tab(String.format("%-10s", tabNames[i]));
			tab.setId(tabNames[i]);
			tab.setContent(createRect(TABPANE_WIDTH, TABPANE_HEIGHT, tabNames[i]));
			tabPane.getTabs().add(tab);
		}
		border.setCenter(tabPane);
		
		// create input field
		TextField userTextField = new TextField();
		userTextField.setPromptText(MSG_PROMPT);
		userTextField.setOnAction((ActionEvent event) -> executeCommand(textbox, userTextField));
		border.setBottom(userTextField);
		
		// edit Log tab
		
		
		
		
		
		Scene scene = new Scene(border, border.getPrefWidth(), border.getPrefHeight());
		userTextField.requestFocus();
		primaryStage.setScene(scene);
	    primaryStage.setTitle(APP_TITLE);
	    scene.getStylesheets().add(Main.class.getResource(FILE_CSS).toExternalForm());
	    primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void executeCommand(TextFlow textbox, TextField userTextField) {
		if (userTextField.getText() != null && !userTextField.getText().isEmpty()) {
			if (userTextField.getText().trim().equalsIgnoreCase(CMD_CLEAR)) {
				textbox.getChildren().clear();
			}
			else {
				textbox.getChildren().add(new Text(userTextField.getText().trim()+"\n"));
				// delete lines as they exceed sidebar's limit
				maintainTextboxLimit(textbox, SIDEBAR_MAX_HEIGHT);
			}
			
	    	userTextField.clear();
	    }
	}
	
	public static void maintainTextboxLimit(TextFlow textbox, int maxHeight) {
		int height = 0; // initialise a num to start the sum from
		int delete = -1; 
		for (int i=0;i<textbox.getChildren().size();i++) {
			height += textbox.getChildren().get(i).getBoundsInParent().getHeight();
			while (height > maxHeight) {
				delete++;
				height -= textbox.getChildren().get(delete).getBoundsInParent().getHeight();
			}
		}
		
		if (delete>=0) {
			textbox.getChildren().remove(0, delete+1);
		}
	}
	
	public static VBox createVBox(String tagname) {
		VBox sidebar = new VBox();
		sidebar.setPadding(new Insets(SIDEBAR_PADDING_VERTICAL, SIDEBAR_PADDING_LATERAL, 
				SIDEBAR_PADDING_VERTICAL, SIDEBAR_PADDING_LATERAL));
		sidebar.setSpacing(SIDEBAR_PADDING_VERTICAL);
		sidebar.setAlignment(SIDEBAR_ORIENTATION);
		sidebar.getStyleClass().add(tagname);
		return sidebar;
	}
	
	public static Rectangle createRect(double width, double height, String name) {
		Rectangle rect = new Rectangle(width,height);
		if (name!="") {
			rect.getStyleClass().add("rect-"+name);
		}
		return rect;
	}
	// Overloading
	public static Rectangle createRect(double width, double height) {
		return createRect(width, height, "");
	}
		
}

