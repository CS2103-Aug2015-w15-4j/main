package gui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
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
import logic.Task;

public class TaskList {
	public int listNumber = -1;
	
	final static int PADDING = 6;
	
	final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	final static Pos ALIGNMENT = Pos.TOP_LEFT;
	
	final static String ID_GRID = "taskGrid";
	final static String STYLE_TEXT = GUIController.STYLE_TEXT;
	
	// grid locations
	final static int COL_HEADER = 0;
	final static int COL_CONTENT = 1;
	final static int COL_SIZE = 2;
	final static int ROW_NAME = 0;
	final static int ROW_ID = 1;
	final static VPos GRID_HEADER_VERT_ALIGNMENT = VPos.TOP;
	final static int GRID_COL_HEADER_FINAL_LENGTH = 60; // header col's fixed length
	
	final static String HEADER_COLOR = "rgba(255, 255, 0, 0.9)";
	final static Color LABEL_COLOR = Color.BLACK;
	
	protected VBox master; // the overall TaskList manager
	protected Button name; // button to press;
	protected ListView<Node> listView; // the list of tasks
	protected ObservableList<Node> items; // the items within the list
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	protected ScrollPane detailedView;
	
	protected Image[] imageCompletion = new Image[2];
	protected final String DONE = "Check.png";
	protected final String NOT_DONE = "Delete.png";
	protected final int DONE_IMAGE_SIZE = 16; 
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
		listView.getStyleClass().add(GUIController.STYLE_TRANSPARENT);
		master.getChildren().add(listView);
		listView.prefWidthProperty().bind(master.widthProperty());
		listView.prefHeight(0);
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
	    
	    name.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (isListOpen) {
					focusTask();
					closeList();
				} else {
					openList();
				}
			}
	    });
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
		name.setText(_name);
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

			System.out.println("Grid " + ((GridPane)items.get(0)).getPrefWidth());
			System.out.println("ListView " + listView.getWidth());
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
			//selectFirstNode();
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
	
	public void clearHighlight() {
		listView.getSelectionModel().clearSelection();
		detailedView.setContent(null); // clear the stored view
	}
	
	public void focusTask() {
		int selected = listView.getSelectionModel().getSelectedIndex();
		if (selected>=0) {
			updateDetailedView(createDetailedDisplay(listOfTasks.get(selected)));
			closeList();
		}
	}
	
	public void updateDetailedView(Region node) {
		detailedView.setContent(node);
		node.prefWidthProperty().bind(detailedView.widthProperty().subtract(2*PADDING));
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 */
	protected GridPane createDetailedDisplay(Task task) {
		GridPane grid = new GridPane();
	    grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
		grid.setPadding(new Insets(0, 9, 0, 0));

		ArrayList<String[]> details = task.getTaskDetails(); 
		// Set name
		Label label = createLabel(task.getName());
		if (label.getText().isEmpty()) {
			label.setText("Task	" + items.size());
		}
		label.setText(" " + label.getText());
		label.setWrapText(false);
		GUIController.setFancyText(label);
		label.setStyle("-fx-font-weight: bold");
		HBox header = new HBox();
		label.prefWidthProperty().bind(header.widthProperty());
		header.prefWidthProperty().bind(grid.widthProperty());
		header.setStyle(String.format(GUIController.STYLE_COLOR, HEADER_COLOR));
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

			label = createLabel(array[COL_CONTENT]);
			if (label.getText()==null||label.getText().isEmpty()) {
				label.setText("None");
			}
			grid.add(label, COL_CONTENT, i);
		}//*/
		
		return grid;
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 */
	protected GridPane createSidebarDisplay(Task task) {
		GridPane grid = new GridPane();
		grid.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH));
		
		// ID content
		Label id = createLabel(""+ task.getId());
		grid.add(id, COL_HEADER, ROW_NAME);

		// Set name
		HBox header = new HBox();
		//
		header.prefWidthProperty().bind(grid.widthProperty().subtract(id.widthProperty()));
		Label label = createLabel(task.getName());
		if (label.getText().isEmpty()) {
			label.setText("Task	" + items.size());
		}
		label.prefWidthProperty().bind(header.widthProperty());
		label.setText(" " + label.getText());
		label.setWrapText(false);
		header.getChildren().add(label);
		header.getChildren().add(getTaskCompletion(task.getIsCompleted()));
		HBox.setHgrow(label, Priority.ALWAYS);
		header.setAlignment(Pos.CENTER_LEFT);
		grid.add(header, COL_CONTENT, ROW_NAME, COL_SIZE, 1); // span 2 col and 1 row
		
		return grid;
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
