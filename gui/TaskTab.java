package gui;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import logic.Task;

public class TaskTab {
	final static int TABNUM = GUI.TASK; // for the array check
	final static String TABNAME = GUI.tabNames[TABNUM]; 
	final static int PADDING = 8;
	final static int WIDTH = GUI.TABPANE_WIDTH;
	final static int WIDTH_SIDEBAR = GUI.TABPANE_SIDEBAR_WIDTH;
	final static int WIDTH_WINDOW = WIDTH-WIDTH_SIDEBAR-2*PADDING;
	
	final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	final static Pos ALIGNMENT = Pos.TOP_LEFT;
	
	final static String ID_GRID = "taskGrid";
	final static String STYLE_TEXT = GUI.STYLE_TEXT;
	
	// grid locations
	final static int SIDEBAR_COL_START = 0;
	final static int SIDEBAR_COL_HEADER = 1;
	final static int SIDEBAR_COL_CONTENT = 2;
	final static int SIDEBAR_COL_SIZE = 3;
	final static int GRID_ROW_NAME = 0;
	final static int GRID_ROW_ID = 1;
	final static int GRID_ROW_DESCRIPTION = 2;
	final static int GRID_ROW_DATE = 3;
	final static int GRID_ROW_TAGS = 4;
	final static int GRID_COL_HEADER = 0;
	final static int GRID_COL_CONTENT = 1;
	final static int GRID_COL_SIZE = 2;
	final static VPos GRID_HEADER_VERT_ALIGNMENT = VPos.TOP;
	final static int GRID_COL_HEADER_FINAL_LENGTH = 90; // header col's fixed length
	
	final static String HEADER_COLOR = "rgba(255, 255, 0, 0.9)";
	final static Color LABEL_COLOR = Color.BLACK;
	
	protected HBox master; // the overall TaskTab manager
	protected GridPane main; // main window
	protected ScrollPane sp; // a scrollpane managing the main window
	protected ListView<Node> listView; // the sidebar
	protected ObservableList<Node> items; // the items within the sidebar
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	
	protected Image[] imageCompletion = new Image[2];
	protected final String DONE = "Check.png";
	protected final String NOT_DONE = "Delete.png";
	protected final int DONE_SIZE = 16; 
	
	public TaskTab() {
		master = new HBox();
		//master.setMaxWidth(WIDTH);
		// sidebar
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		listView.getStyleClass().add(GUI.STYLE_TRANSPARENT);
		master.getChildren().add(listView);
		listView.prefWidthProperty().bind(master.widthProperty().divide(6));
		listView.maxWidthProperty().bind(master.maxWidthProperty().divide(6));
		
		main = new GridPane();
		main.setAlignment(ALIGNMENT);
		main.setId(ID_GRID);
		sp = new ScrollPane(main);
		sp.setFitToHeight(true);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.getStyleClass().add(GUI.STYLE_TRANSPARENT);
		master.getChildren().add(sp);
		sp.prefWidthProperty().bind(master.widthProperty().multiply(5).divide(6));
		main.prefWidthProperty().bind(sp.prefWidthProperty());
		main.setPadding(new Insets(PADDING));
	    main.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
	    
	    // define the images for done and not done
	    imageCompletion[0] = new Image(TaskTab.class.getResourceAsStream(NOT_DONE),DONE_SIZE, DONE_SIZE, true, true);
	    imageCompletion[1] = new Image(TaskTab.class.getResourceAsStream(DONE),DONE_SIZE, DONE_SIZE, true, true);
		
		// now add a listener for the sidebar
		listView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Node>() {
					public void changed(ObservableValue<? extends Node> ov, Node old_val, Node new_val) {
						refreshMainDisplay();
					}
				});
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Region getNode() { 
		return master;
	}
	
	/**
	 * Adds a task to the display
	 * @param task Task to be added
	 */
	public void addTask(Task task) {
		addTask(task, false);
	}
	
	/**
	 * Adds a task to the display
	 * @param task Task to be added
	 * @param refresh If true, display refreshes and the newly added item is selected
	 */
	public void addTask(Task task, boolean refresh) {
		if (task!=null) {
			listOfTasks.add(task);
			items.add(createSidebarDisplay(task));
			// then change focus to task tab
			GUI.tabPane.getSelectionModel().select(TABNUM);
			
			if (refresh) {
				refresh();
				selectNode(items.size()-1);
			}
		}
	}
	
	/**
	 * Highlights/Selects the first node in the list
	 */
	public void selectFirstNode() {
		selectNode(0);
	}
	
	/**
	 * Highlights/Selects the node in location i in the list
	 * If i > list size, function does nothing
	 */
	public void selectNode(int i) {
		if (items.size()>i) {
			listView.scrollTo(i);
			listView.getSelectionModel().select(i);
		}
	}
	
	/**
	 * Refreshes the entire TaskTab display to the latest version
	 */
	public void refresh() {
		listView.setItems(items);
		refreshMainDisplay();
	}
	
	/**
	 * Takes in a list and adds them all to the display
	 * @param tasks List of Tasks to be added
	 */
	public void addAllTasks(List<Task> tasks) {
		if (tasks!=null) {
			deleteAllTasks();
			
			for(Task task : tasks) {
				addTask(task, false);
			}
			
			refresh();
			selectFirstNode();
		}
	}
	
	/**
	 * Deletes all items from the display
	 */
	public void deleteAllTasks() {
		items.clear();
		listOfTasks.clear();
		refresh();
	}
	
	/**
	 * Deletes a specific Task from the display
	 * @param task
	 * @return true if successful deletion
	 */
	public boolean deleteTask(Task task) {
		for (int i=0;i<listOfTasks.size();i++) {
			if (listOfTasks.get(i).equals(task)) {
				listOfTasks.remove(i);
				items.remove(i);
				refresh();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 */
	protected Node createSidebarDisplay(Task task) {
		GridPane grid = new GridPane();
		grid.prefWidthProperty().bind(listView.widthProperty());
		
		Label text = createLabel();
		text.setWrapText(false);
		// Name
		if (!task.getName().isEmpty()) {
			text.setText(task.getName());
		} else {
			task.setName("Task	" + items.size());
		}
		grid.add(text, SIDEBAR_COL_START, GRID_ROW_NAME, SIDEBAR_COL_SIZE, 1); // span 2 col and 1 row
		
		// ID header
		text = createLabel(" ID : ");
		GridPane.setValignment(text, GRID_HEADER_VERT_ALIGNMENT);
		grid.add(text, SIDEBAR_COL_HEADER, GRID_ROW_ID);
		// ID content
		text = createLabel(""+ task.getId());
		grid.add(text, SIDEBAR_COL_CONTENT, GRID_ROW_ID);
		
		// isDone
		grid.add(getTaskCompletion(task.getIsCompleted()), SIDEBAR_COL_START, GRID_ROW_ID);
		
		return grid;
	}
	
	/**
	 * Creates a node for the main TaskTab display
	 * @param task The task details to add
	 * @param grid The grid to be added to
	 * @return grid after it is modified
	 */
	protected Node createMainDisplay(Task task, GridPane grid) {
		ArrayList<String[]> details = task.getTaskDetails(); 
		// Set name
		Label label = createLabel(task.getName());
		if (label.getText().isEmpty()) {
			task.setName("Task	" + items.size());
		}
		label.setWrapText(false);
		GUI.setFancyText(label);
		label.setStyle("-fx-font-weight: bold");
		HBox header = new HBox();
		label.prefWidthProperty().bind(header.widthProperty());
		header.prefWidthProperty().bind(grid.widthProperty());
		header.setStyle(String.format(GUI.STYLE_COLOR, HEADER_COLOR));
		header.getChildren().add(label);
		header.getChildren().add(getTaskCompletion(task.getIsCompleted()));
		header.setAlignment(Pos.CENTER);
		grid.add(header, GRID_COL_HEADER, GRID_ROW_NAME, GRID_COL_SIZE, 1); // span 2 col and 1 row
		
		String[] array;
		for (int i=1;i<details.size();i++) { // skip name element
			array = details.get(i);
			label = createLabel(array[GRID_COL_HEADER]);
			grid.add(label, GRID_COL_HEADER, i);
			GridPane.setValignment(label, GRID_HEADER_VERT_ALIGNMENT);

			label = createLabel(array[GRID_COL_CONTENT]);
			if (label.getText().isEmpty()) {
				label.setText("None");
			}
			grid.add(label, GRID_COL_CONTENT, i);
		}
		
		return grid;
	}
	
	/**
	 * Refreshes the window so that the task description is updated
	 */
	public void refreshMainDisplay() {
		main.getChildren().clear();
		if (!items.isEmpty()&&listView.getSelectionModel().getSelectedIndex()!=-1) {
			createMainDisplay(
				listOfTasks.get(listView.getSelectionModel().getSelectedIndex()),
				main
			);
		}
	}
	
	/**
	 * Returns the tick or cross depending on whether task is done
	 * @param isCompleted Whether the task is completed or not
	 * @return ImageView of a tick or cross
	 */
	protected ImageView getTaskCompletion(boolean isCompleted) {
		if (isCompleted) {
			return new ImageView(imageCompletion[1]); 
		} else {
			return new ImageView(imageCompletion[0]);
		}
	}
	
	/**
	 * Creates a label that has required settings built in
	 * @return a Label that has wrapText and width properties ready
	 */
	protected Label createLabel() {
		return createLabel("");
	}
	
	/**
	 * Creates a label has required settings built in with string Text inside
	 * @param text Text to be added
	 * @return a Label that has wrapText and width properties ready
	 */
	protected Label createLabel(String text) {
		Label label = new Label(text);
		label.setWrapText(true);
		label.setTextFill(LABEL_COLOR);
		return label;
	}
}
