package gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.DeadlineTask;
import logic.Event;
import logic.Task;
import parser.ParsedCommand.TaskType;

public class TaskList {
	public int listNumber = -1;
	public String listName = "";
	public int listSize = 0;
	final static String NAME_FORMAT = "%1$s (%2$d)";
	
	final static int PADDING = 6;
	
	final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	final static Pos ALIGNMENT = Pos.TOP_LEFT;
	
	final static String ID_GRID = "taskGrid";
	final static String STYLE_TEXT = GUIController.STYLE_TEXT;
	final static String STYLE_FLOATING = "floating";
	final static String STYLE_DEADLINE = "deadline";
	final static String STYLE_EVENT = "event";
	
	final static String STYLE_CURVED = "-fx-background-radius: 9,8,5,4;";
	
	// grid locations
	final static int COL_HEADER = 0;
	final static int COL_CONTENT = 1;
	final static int COL_SIZE = 2;
	final static int ROW_NAME = 0;
	final static int ROW_ID = 1;
	final static int ROW_TAGS = 2;
	final static int ROW_DATE = 3;
	final static int SIDEBAR_COL_ID = 0;
	final static int SIDEBAR_COL_NAME = 1;
	final static int SIDEBAR_COL_DATE = 2;
	final static int SIDEBAR_COL_DONE = 3;
	final static int SIDEBAR_COL_SIZE = 4; 
	final static VPos GRID_HEADER_VERT_ALIGNMENT = VPos.TOP;
	final static int GRID_COL_HEADER_FINAL_LENGTH = 70; // header col's fixed length
	
	final static String COLOR_OVERDUE = "-fx-text-fill: rgba(255, 0, 0, 1);";
	final static String COLOR_HEADER = "rgba(255, 255, 0, 0.9)";
	final static Color COLOR_LABEL = Color.BLACK;
	final static double ROW_CELL_HEIGHT = 33.3;
	final static double BORDER_HEIGHT = 2.0;
	final static double IMAGE_SIZE = 10.0;
	final static double DATE_WIDTH = 280.0;
	public static SimpleDateFormat format;
	
	protected VBox master; // the overall TaskList manager
	protected Button name; // button to press;
	protected ListView<Node> listView; // the list of tasks
	protected ObservableList<Node> items; // the items within the list
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	protected ScrollPane detailedView;
	
	protected static Image[] imageCompletion = new Image[2];
	protected final static String DONE = "resources/Check.png";
	protected final static String NOT_DONE = "resources/Delete.png";
	protected final static int DONE_IMAGE_SIZE = 16; 
	
	public final int INVALID_SELECTION = -1; 
	protected boolean isChanging = false;
	public boolean isPinnedWindow = false; // by default assume that it is not pinned 
	public boolean isListOpen = true; // open by default
	
	public TaskList() {
		master = new VBox();
		name = new Button();
		name.prefWidthProperty().bind(master.widthProperty());
		name.setAlignment(Pos.CENTER);
		
		// sidebar
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		listView.setPlaceholder(new Label("No Content"));
		listView.getStyleClass().add(GUIController.STYLE_TRANSPARENT);
		master.getChildren().add(listView);
		listView.prefWidthProperty().bind(master.widthProperty());
		/*listView.prefHeightProperty().bind(
				Bindings.size(items).multiply(ROW_CELL_HEIGHT).add(BORDER_HEIGHT));//*/
	    // define the images for done and not done
	    imageCompletion[0] = new Image(TaskList.class.getResourceAsStream(NOT_DONE),DONE_IMAGE_SIZE, DONE_IMAGE_SIZE, true, true);
	    imageCompletion[1] = new Image(TaskList.class.getResourceAsStream(DONE),DONE_IMAGE_SIZE, DONE_IMAGE_SIZE, true, true);
	    
	    // scrollpane
	    detailedView = new ScrollPane();
		//detailedView.setFitToHeight(true);
		detailedView.setVbarPolicy(V_POLICY);
		detailedView.setHbarPolicy(H_POLICY);
		detailedView.setPadding(new Insets(0, PADDING, 0, PADDING)); // left and right padding only
		detailedView.prefWidthProperty().bind(master.widthProperty());
		detailedView.prefHeightProperty().bind(master.heightProperty());
		detailedView.getStyleClass().add(GUIController.STYLE_TRANSPARENT);
		
		// Time
		format = new SimpleDateFormat("HH:mm, dd/MM E");
	    
		addHandlers();
	}
	
	/**
	 * Function to hold all handlers and listeners
	 */
	protected void addHandlers() {
	    name.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (isListOpen) {
					// focusTask(); // removed due to real time focus 
					closeList();
				} else {
					openList();
				}
			}
	    });
	    
	    items.addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				listSize = items.size();
				if (listSize==0) {
					name.setText(listName);
				} else {
					name.setText(String.format(NAME_FORMAT, listName, listSize));
				}
			}
		});//*/

		listView.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<Node>() {
				public void changed(ObservableValue<? extends Node> ov, Node old_val, Node new_val) {
					if (isListOpen) { // if the list is open, generate the focus task in real time
						focusTask(); // generate a focus view
					}
				}
			});
		

	    /*
	    name.addEventHandler(MouseEvent.MOUSE_ENTERED, 
	    		new EventHandler<MouseEvent>() {
	    	@Override public void handle(MouseEvent e) {
	    		// change to close/open list
	    		if (isListOpen) {
	    			if (isPinnedWindow) {
						name.setText("Focus on highlighted task!");
					} else {
						name.setText("Close list!");
					}
				} else {
					name.setText("Open list!");
				}
	    	}
	    });
	    //Removing the shadow when the mouse cursor is off
	    name.addEventHandler(MouseEvent.MOUSE_EXITED, 
	    		new EventHandler<MouseEvent>() {
	    	@Override public void handle(MouseEvent e) {
	    		// change back to default
	    		name.setText(GUIController.taskListNames[listNumber]);
	    	}
	    });//*/		
	}
	
	/**
	 * Creates a TaskList with a name
	 * @param _name Name of task list
	 */
	public TaskList(String _name) {
		this();
		setName(_name);
	}
	
	/**
	 * Creates a TaskList with a reference number to GUIController
	 * @param num The number allocated to this task list
	 */
	public TaskList(int num) {
		this();
		if (num>=0) {
			listNumber = num;
			setName(GUIController.taskListNames[num]);
		}
	}
	
	/**
	 * Sets name of the TaskList
	 * @param _name Name of TaskList
	 */
	public void setName(String _name) {
		listName = _name.trim();
		name.setText(listName);
		if (master.getChildren().size()==1) { // add the name if it does not exist
			master.getChildren().add(0,name);
		}
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public VBox getNode() { 
		return master;
	}
	
	/**
	 * Calls focusTask() in this function
	 * @return the window of the FocusTask
	 */
	public ScrollPane getFocusTask() {
		focusTask();
		return detailedView;
	}
	
	/**
	 * Closes the excess list by removing it from the VBox
	 */
	public void closeList() {
		if (isListOpen&&!name.getText().isEmpty()) {
			master.getChildren().clear();
			master.getChildren().add(name);
			isListOpen = false;
			if (isPinnedWindow) {
				if (detailedView.getContent()!=null) {
					master.getChildren().add(detailedView);
				}
			}
		}
	}
	
	/**
	 * Opens up the list view
	 */
	public void openList() {
		if (!isListOpen) {
			master.getChildren().clear();
			if (!name.getText().isEmpty()) { // if name is not empty
				master.getChildren().add(name);
			}
			master.getChildren().add(listView);
			isListOpen = true;
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
	 * Recalculates the entire TaskList display size
	 */
	public void recalculate() {
		if (!isChanging) {
			isChanging = true;
			int selected = listView.getSelectionModel().getSelectedIndex();
			items.clear();
			for (Task task : listOfTasks) {
				items.add(createSidebarDisplay(task));
			}
			listView.getSelectionModel().select(selected);
			refresh();
			isChanging = false;
		}
	}
	
	/**
	 * Refreshes the entire TaskList display to the latest version
	 */
	public void refresh() {
		listView.setItems(items);
		if (isPinnedWindow) {
			boolean open = isListOpen;
			focusTask();
			if (open) { // if it was supposed to be open, make sure it ends opened
				openList();
			}
		}
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
			
			if (refresh) {
				refresh();
				selectNode(items.size()-1);
			}
		}
	}
	
	/**
	 * Takes in a list and adds them all to the display
	 * @param tasks List of Tasks to be added
	 */
	public void addAllTasks(List<Task> tasks) {
		if (tasks!=null) {
			int selected = listView.getSelectionModel().getSelectedIndex();
			deleteAllTasks();
			
			for(Task task : tasks) {
				addTask(task, false);
			}
			
			listView.getSelectionModel().select(selected);
			refresh();
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
	 * Clears the detailed window
	 */
	public void clearDetailedWindow() {
		listView.getSelectionModel().clearSelection();
		detailedView.setContent(null); // clear the stored view
	}
	
	/**
	 * Creates a detailed view in the list
	 */
	public void focusTask() {
		int selected = listView.getSelectionModel().getSelectedIndex();
		if (selected>INVALID_SELECTION) {
			// create the detailed view
			updateDetailedView(createDetailedDisplay(listOfTasks.get(selected)));
		}
	}
	
	public void updateDetailedView(Region node) {
		detailedView.setContent(node);
		node.prefWidthProperty().bind(detailedView.widthProperty().subtract(2*PADDING));
	}
	
	/**
	 * Create a focused view that shows all details of the task 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 *//*
	public static GridPane createDetailedDisplay(Task task) {
		GridPane grid = new GridPane();
	    grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
		grid.setPadding(new Insets(0, 9, 0, 0));
		
		// Set name
		Label label = createLabel(task.getName());
		label.setText(" " + label.getText());
		label.setWrapText(false);
		label.setStyle(String.format(GUIController.STYLE_COLOR, COLOR_HEADER)+
				STYLE_CURVED+"-fx-font-weight: bold;");
		grid.add(label, COL_CONTENT, ROW_NAME); 
		// add id
		Label id = createLabel(Integer.toString(task.getId()));
		id.setPadding(new Insets(0,5,0,5));
		id.getStyleClass().add(getTaskStyle(task.getTaskType())); // set colour based on type
		grid.add(id, COL_HEADER, ROW_NAME); 
		
		// add id
		TextFlow tagFlow = new TextFlow();
		tagFlow.prefWidthProperty().bind(grid.widthProperty());
		grid.add(tagFlow, COL_HEADER, ROW_TAGS, COL_SIZE, 1); // span 2 col and 1 row
		Label type = createLabel(task.getTaskType().toString());
		type.setPadding(new Insets(0,5,0,5));
		type.getStyleClass().add("label-tags"); // set colour based on type
		tagFlow.getChildren().add(type);
		
		for (String tag : task.getTags()) {
			type = createLabel("#"+tag);
			type.getStyleClass().add("label-tags"); // set colour based on type
			tagFlow.getChildren().add(type);
		}
		
		return grid;
	}//*/
	
	/**
	 * Create a focused view that shows all details of the task 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 *///*// old version using the arraylist from Logic
	public static GridPane createDetailedDisplay(Task task) {
		GridPane grid = new GridPane();
	    grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
		grid.setPadding(new Insets(0, 9, 0, 0));

		ArrayList<String[]> details = task.getTaskDetails();
		
		// Set name
		Label label = createLabel(task.getName());
		label.setText(" " + label.getText());
		label.setWrapText(false);
		label.setStyle("-fx-font-weight: bold");
		HBox header = new HBox();
		label.prefWidthProperty().bind(header.widthProperty());
		header.prefWidthProperty().bind(grid.widthProperty());
		header.setStyle(String.format(GUIController.STYLE_COLOR, COLOR_HEADER)+STYLE_CURVED);
		// add completed icon
		header.getChildren().add(getTaskCompletion(task.getIsCompleted())); 
		header.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		header.setAlignment(Pos.CENTER_LEFT);
		grid.add(header, COL_HEADER, ROW_NAME, COL_SIZE, 1); // span 2 col and 1 row
		
		String[] array;
		for (int i=1;i<details.size();i++) { // skip name element
			array = details.get(i);
			label = createLabel(array[COL_HEADER]);
			grid.add(label, COL_HEADER, i);
			GridPane.setValignment(label, GRID_HEADER_VERT_ALIGNMENT);

			label = createLabel(array[COL_CONTENT] + " ");
			if (label.getText()==null||label.getText().trim().isEmpty()) {
				label.setText("None");
			}
			grid.add(label, COL_CONTENT, i);
		}
		
		return grid;
	}//*/
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return GridPane with ID, Name, (Date, if available), Done/NotDone
	 */
	protected GridPane createSidebarDisplay(Task task) {
		GridPane grid = new GridPane();
		grid.prefWidthProperty().bind(listView.widthProperty().subtract(100));
		grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH));
		
		// ID content
		Label id = createLabel(Integer.toString(task.getId()));
		id.setPadding(new Insets(0,5,0,5));
		id.getStyleClass().add(getTaskStyle(task.getTaskType())); // set colour based on type
		grid.add(id, SIDEBAR_COL_ID, ROW_NAME);

		HBox header = new HBox();
		header.prefWidthProperty().bind(grid.widthProperty().subtract(id.widthProperty()).subtract(IMAGE_SIZE)); // 8 is size of image
		header.setAlignment(Pos.CENTER_LEFT);
		grid.add(header, SIDEBAR_COL_NAME, ROW_NAME, 2, 1);
		
		// Set name
		Label label = createLabel(task.getName());
		label.prefWidthProperty().bind(header.widthProperty());
		label.setWrapText(false);
		header.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		
		
		// set date if exists
		try {
			try {
				label = createLabel(format.format(((Event) task).getStart().getTime()));
			} catch (ClassCastException e) {
				// if fail to cast to Event, try DeadlineTask
				label = createLabel(format.format(((DeadlineTask) task).getEnd().getTime()));
			}
			// if any of them are successful, carry on adding to header
			label.setWrapText(false);
			label.setPadding(new Insets(0, 0, 0, PADDING));
			label.setMaxWidth(DATE_WIDTH);
			HBox.setHgrow(label, Priority.SOMETIMES);
			label.prefWidthProperty().bind(header.widthProperty());
			header.getChildren().add(label);
		} catch (ClassCastException e) {
			// do nothing
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// add whether completed
		grid.add(getTaskCompletion(task.getIsCompleted()), SIDEBAR_COL_DONE, ROW_NAME); // only need up to 1 row 
		// old settings: , COL_SIZE, 1); // span 2 col and 1 row
		
		return grid;
	}
	
	/**
	 * Returns the tick or cross depending on whether task is done
	 * @param isCompleted Whether the task is completed or not
	 * @return ImageView of a tick or cross
	 */
	public static ImageView getTaskCompletion(boolean isCompleted) {
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
	protected static Label createLabel(String text) {
		Label label = new Label(text);
		label.setWrapText(true);
		label.setTextFill(COLOR_LABEL);
		return label;
	}
	
	/**
	 * Return colour of TaskType
	 * @param type
	 * @return colour style string
	 */
	protected static String getTaskStyle(TaskType type) {
		switch(type) {
		case FLOATING_TASK:
			return STYLE_FLOATING;
		case DEADLINE_TASK:
			return STYLE_DEADLINE;
		case EVENT:
			return STYLE_EVENT;
		default:
			return "";
		}
	}
}