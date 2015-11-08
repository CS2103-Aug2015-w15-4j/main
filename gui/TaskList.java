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

//@@author A0122534R
public class TaskList {
	// details about the TaskList's type
	protected int listNumber = -1;
	protected String listName = "";
	protected int listSize = 0;
	public final int INVALID_SELECTION = -1; // invalid number for selected item in list

	// default messages/formats
	protected final static String MSG_NO_CONTENT = "No Content";
	protected final static String FORMAT_TIME = "HH:mm, dd/MM E";
	protected final static String FORMAT_NAME = "%1$s (%2$d)";
	
	// default alignments and displayable settings
	protected final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.NEVER;
	protected final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	protected final static Pos ALIGNMENT = Pos.TOP_LEFT;
	protected final static int PADDING = 6;
	
	protected final static String STYLE_FLOATING = "floating";
	protected final static String STYLE_DEADLINE = "deadline";
	protected final static String STYLE_EVENT = "todo";
	protected final static String STYLE_CURVED = "-fx-background-radius: 9,8,5,4;";
	
	// grid locations
	protected final static int COL_HEADER = 0;
	protected final static int COL_CONTENT = 1;
	protected final static int COL_CONTENT_SIZE = 2;
	protected final static int COL_SIZE = 2;
	protected final static int ROW_NAME = 0;
	protected final static int ROW_ID = 1;
	protected final static int ROW_TAGS = 2;
	protected final static int ROW_DATE = 3;
	protected final static int SIDEBAR_COL_ID = 0;
	protected final static int SIDEBAR_COL_NAME = 1;
	protected final static int SIDEBAR_COL_DATE = 2;
	protected final static int SIDEBAR_COL_DONE = 3;
	protected final static int SIDEBAR_COL_SIZE = 4; 
	protected final static VPos GRID_HEADER_VERT_ALIGNMENT = VPos.TOP;
	protected final static int GRID_COL_HEADER_FINAL_LENGTH = 70; // header col's fixed length
	
	// asthetic settings
	protected final static String COLOR_OVERDUE = "-fx-text-fill: rgba(255, 0, 0, 1);";
	protected final static String COLOR_HEADER = "rgba(255, 255, 0, 0.9)";
	protected final static Color COLOR_LABEL = Color.BLACK;
	protected final static double ROW_CELL_HEIGHT = 33.3;
	protected final static double BORDER_HEIGHT = 2.0;
	protected final static double IMAGE_SIZE = 10.0;
	protected final static double DATE_WIDTH = 280.0;
	
	// images for done and not done
	protected static Image[] imageCompletion = null;
	protected final static String DONE = "resources/Check.png";
	protected final static String NOT_DONE = "resources/Delete.png";
	protected final static int DONE_IMAGE_SIZE = 16; 
	
	// displayables
	protected VBox master; // the overall TaskList manager
	protected Button name; // button to press;
	protected ListView<Node> listView; // the list of tasks
	protected ObservableList<Node> items; // the items within the list
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	protected ScrollPane detailedView;
	
	protected static SimpleDateFormat format;
	protected boolean isChanging = false;
	protected boolean isPinnedWindow = false; // by default assume that it is not pinned 
	protected boolean isListOpen = true; // open by default
	
	public TaskList() {
		master = new VBox();
		
		initDoneImages();
		initNameButton();
		initListView();
		initScrollPane();
		
		// add the Nodes to master
		master.getChildren().add(listView);
	    
		// Time
		format = new SimpleDateFormat(FORMAT_TIME);
	    
		addHandlers();
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
			master.getChildren().add(0,name); // add on top of any other node already in master
		}
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public VBox getNode() { 
		return master;
	}
	
	/**
	 * Initialises the name Button node and binds it to the parent node of this TaskList
	 * @param name
	 */
	protected void initNameButton() {
		name = new Button();
		name.prefWidthProperty().bind(getNode().widthProperty());
		name.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Initialisation of the image for Done and Not Done if they are not yet initialised
	 * @param imageCompletion
	 */
	protected void initDoneImages() {
		// define the images for done and not done if not defined
		if (imageCompletion==null) {
			imageCompletion = new Image[2];
			imageCompletion[0] = new Image(TaskList.class.getResourceAsStream(NOT_DONE),DONE_IMAGE_SIZE, DONE_IMAGE_SIZE, true, true);
			imageCompletion[1] = new Image(TaskList.class.getResourceAsStream(DONE),DONE_IMAGE_SIZE, DONE_IMAGE_SIZE, true, true);
		}
	}
	
	/**
	 *  Initialises the main list and binds it to the parent node
	 *  @param items
	 *  @param listView
	 */
	protected void initListView() {
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		listView.setPlaceholder(new Label(MSG_NO_CONTENT));
		listView.getStyleClass().add(GUIController.CSS_STYLE_TRANSPARENT);
		listView.prefWidthProperty().bind(getNode().widthProperty());
	}
	
	/**
	 * Initialises the ScrollPane in which focus view is located in
	 * @param detailedView
	 */
	protected void initScrollPane() {
	    detailedView = new ScrollPane();
		//detailedView.setFitToHeight(true);
		detailedView.setVbarPolicy(V_POLICY);
		detailedView.setHbarPolicy(H_POLICY);
		detailedView.setPadding(new Insets(0, PADDING, 0, PADDING)); // left and right padding only
		detailedView.prefWidthProperty().bind(getNode().widthProperty());
		detailedView.prefHeightProperty().bind(getNode().heightProperty());
		detailedView.getStyleClass().add(GUIController.CSS_STYLE_TRANSPARENT);
	}
	
	/**
	 * Function to hold all handlers and listeners
	 */
	protected void addHandlers() {
		// When name button is clicked, open/close the list
	    name.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (isListOpen) { 
					closeList();
				} else {
					openList();
				}
			}
	    });
	    
	    // Dynamically change the item size located on name button when list is modified
	    items.addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				listSize = items.size();
				if (isListEmpty()) {
					name.setText(listName);
				} else {
					name.setText(String.format(FORMAT_NAME, listName, listSize));
				}
			}
		});//*/

		listView.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<Node>() {
				public void changed(ObservableValue<? extends Node> ov, Node old_val, Node new_val) {
					if (isListOpen||isPinnedWindow) { 
						// if the list is open or is pinned, generate the focus task in real time
						focusTask(); // generate a focus view
					}
				}
			});
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
	 * @return true if a selection in the listView already exists 
	 */
	public boolean hasSelection() {
		return !listView.getSelectionModel().isEmpty();
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
				items.add(createMinimisedDisplay(task));
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
			items.add(createMinimisedDisplay(task));
			// then change focus to task tab
			
			if (refresh) {
				refresh();
				selectNode(items.size()-1);
			}
		}
	}
	
	/**
	 * 
	 */
	/**
	 * Takes in a list and adds them all to the display
	 * @param tasks List of Tasks to be added
	 * @param newList if true, means old data is deleted
	 */
	public void addAllTasks(List<Task> tasks, boolean newList) {
		int selected = listView.getSelectionModel().getSelectedIndex();
		deleteAllTasks();
		addAllTasks(tasks);
		listView.getSelectionModel().clearAndSelect(selected);
	}
	
	/**
	 * Takes in a list and adds them all to the display
	 * @param tasks list of Tasks to be added
	 */
	public void addAllTasks(List<Task> tasks) {
		if (tasks!=null) {			
			for(Task task : tasks) {
				addTask(task, false);
			}
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
	 * Creates a new more detailed object of the highlighted task and adds it to detailedView
	 */
	public void focusTask() {
		int selected = listView.getSelectionModel().getSelectedIndex();
		if (selected>INVALID_SELECTION) {
			// create the detailed view
			updateDetailedView(createDetailedDisplay(listOfTasks.get(selected)));
		}
	}
	
	/**
	 * Adds a node to the detailedView and binds its properties to it
	 * @param node
	 */
	public void updateDetailedView(Region node) {
		detailedView.setContent(node);
		node.prefWidthProperty().bind(detailedView.widthProperty().subtract(2*PADDING));
		node.prefHeightProperty().bind(detailedView.heightProperty().subtract(2*PADDING));
	}
	
	/**
	 * Create a focused view that shows all details of the task 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 *///*// old version using the arraylist from Logic
	public static GridPane createDetailedDisplay(Task task) {
		// Create an object ot return and set its borders/constraints
		GridPane grid = new GridPane();
	    grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
		grid.setPadding(new Insets(0, PADDING, 0, 0)); // set some buffer to the right
		
		// ID content
		Label id = createLabel(Integer.toString(task.getId()));
		id.setPadding(new Insets(0,PADDING,0,PADDING));
		id.getStyleClass().add(getTaskStyle(task.getTaskType())); // set colour based on type
		grid.add(id, SIDEBAR_COL_ID, ROW_NAME);
		
		// Set name
		Label label = createLabel(task.getName());
		label.prefWidthProperty().bind(grid.widthProperty());
		label.setWrapText(true);
		label.setStyle("-fx-font-weight: bold");
		grid.add(label, SIDEBAR_COL_NAME, ROW_NAME, 2, 1);

		// add whether completed
		grid.add(getTaskCompletion(task.getIsCompleted()), SIDEBAR_COL_DONE, ROW_NAME); // only need up to 1 row
		
		// Get the details from task details and display them
		ArrayList<String[]> details = task.getTaskDetails();		
		String[] array;
		for (int i=1;i<details.size();i++) { // i=1 to skip name element
			array = details.get(i);
			label = createLabel(array[COL_HEADER]);
			grid.add(label, COL_HEADER, i);
			GridPane.setValignment(label, GRID_HEADER_VERT_ALIGNMENT);

			label = createLabel(array[COL_CONTENT]);
			if (label.getText()==null||label.getText().trim().isEmpty()) {
				label.setText("None");
			}
			grid.add(label, COL_CONTENT, i, COL_CONTENT_SIZE, 1);
		}
		
		return grid;
	}//*/
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return GridPane with ID, Name, (Date, if available), Done/NotDone
	 */
	protected GridPane createMinimisedDisplay(Task task) {
		GridPane grid = new GridPane();
		grid.prefWidthProperty().bind(listView.widthProperty().subtract(100));
		grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH));
		
		// ID content
		Label id = createLabel(Integer.toString(task.getId()));
		id.setPadding(new Insets(0,PADDING,0,PADDING));
		id.getStyleClass().add(getTaskStyle(task.getTaskType())); // set colour based on type
		grid.add(id, SIDEBAR_COL_ID, ROW_NAME);
		
		// Wrap the later content in a HBox
		HBox header = new HBox();
		header.prefWidthProperty().bind(grid.widthProperty().
				subtract(GRID_COL_HEADER_FINAL_LENGTH).subtract(IMAGE_SIZE));
		header.setAlignment(Pos.CENTER_LEFT);
		grid.add(header, SIDEBAR_COL_NAME, ROW_NAME, 2, 1); // span 2 col and 1 row
		
		// Set name
		Label label = createLabel(task.getName());
		label.prefWidthProperty().bind(header.widthProperty());
		label.setWrapText(false);
		header.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		
		// set date if exists
		try {
			try {
				// Try to check for Event first, since Event is Deadline's child
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
			// else do nothing
		} catch (Exception e) {
			// if it is not a class cast exception, something else went wrong
			e.printStackTrace();
		}
		
		// add whether completed
		grid.add(getTaskCompletion(task.getIsCompleted()), SIDEBAR_COL_DONE, ROW_NAME);
		
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
	 * Returns true if the list is empty
	 */
	public boolean isListEmpty() {
		return listSize==0;
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