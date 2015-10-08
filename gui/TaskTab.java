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
import javafx.scene.layout.*;
import logic.Task;

public class TaskTab {
	public static final int TABNUM = GUI.TASK; // for the array check
	public static final String TABNAME = GUI.tabNames[TABNUM]; 
	public static final int PADDING = 8;
	public static final int WIDTH = GUI.TABPANE_WIDTH;
	public static final int WIDTH_SIDEBAR = GUI.TABPANE_SIDEBAR_WIDTH;
	public static final int WIDTH_WINDOW = WIDTH-WIDTH_SIDEBAR-2*PADDING;
	
	public static final ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	public static final ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	public static final Pos ALIGNMENT = Pos.TOP_LEFT;
	
	public static final String ID_VBOX = "taskVbox";
	public static final String ID_SCROLL = "taskScroll";	
	public static final String ID_LIST = "taskList";
	
	// grid locations
	public static final int GRID_ROW_NAME = 0;
	public static final int GRID_ROW_ID = 1;
	public static final int GRID_ROW_DESCRIPTION = 2;
	public static final int GRID_ROW_DATE = 3;
	public static final int GRID_ROW_TAGS = 4;
	public static final int GRID_COL_HEADER = 0;
	public static final int GRID_COL_CONTENT = 1;
	public static final VPos GRID_HEADER_VERT_ALIGNMENT = VPos.TOP;
	public static final int GRID_COL_HEADER_FINAL_LENGTH = 90; // header col's fixed length
	
	public static final String HEADER_COLOR = "YELLOW";//"#0000FF";

	protected HBox master; // the overall TaskTab manager
	protected GridPane main; // main window
	protected ScrollPane sp; // a scrollpane managing the main window
	protected ListView<Node> listView; // the sidebar
	protected ObservableList<Node> items; // the items within the sidebar
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	
	public TaskTab() {
		master = new HBox();
		//master.setMaxWidth(WIDTH);
		// sidebar
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		//listView.setMinWidth(WIDTH_SIDEBAR);
		//listView.setMaxWidth(WIDTH_SIDEBAR);
		listView.setId(ID_LIST);
		master.getChildren().add(listView);
		listView.prefWidthProperty().bind(master.widthProperty().divide(6));
		listView.maxWidthProperty().bind(master.maxWidthProperty().divide(6));
		
		main = new GridPane();
		main.setAlignment(ALIGNMENT);
		main.setId(ID_VBOX);
		sp = new ScrollPane(main);
		sp.setFitToHeight(true);
		sp.setPadding(new Insets(PADDING));
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
		master.getChildren().add(sp);
		sp.prefWidthProperty().bind(master.widthProperty().multiply(5).divide(6));
		main.prefWidthProperty().bind(sp.widthProperty());

	    main.getColumnConstraints().add(new ColumnConstraints(GRID_COL_HEADER_FINAL_LENGTH)); 
		
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
	
	public void refresh() {
		listView.setItems(items);
		refreshMainDisplay();
	}
	
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
	
	public void deleteAllTasks() {
		items.clear();
		listOfTasks.clear();
		refresh();
	}
	
	/**
	 * 
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
		return createDisplay(task, grid);
	}
	
	/**
	 * creates a label that is bounded by a Region
	 * @param boundingBox Region item to be bounded to
	 * @return a Label that has wrapText and width properties ready
	 */
	protected Label createLabel(Region boundingBox) {
		Label label = new Label();
		label.setWrapText(true);
		label.prefWidthProperty().bind(boundingBox.prefWidthProperty());
		return label;
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @param wrappingWidth
	 * @return VBox with Name and ID
	 */
	protected Node createDisplay(Task task, GridPane grid) {		
		Label text = new Label();
		// Name
		if (!task.getName().isEmpty()) {
			text.setText(task.getName());
		} else {
			text.setText("Task	" + items.size());
		}
		grid.add(text, GRID_COL_HEADER, GRID_ROW_NAME, 2, 1); // span 2 col and 1 row
		
		// ID header
		text = new Label("ID		: ");
		GridPane.setValignment(text, GRID_HEADER_VERT_ALIGNMENT);
		grid.add(text, GRID_COL_HEADER, GRID_ROW_ID);
		// ID content
		text = new Label();
		text.setText(""+ task.getId());
		grid.add(text, GRID_COL_CONTENT, GRID_ROW_ID);
		
		return grid;
	}
	
	/**
	 * Creates a node for the main TaskTab display
	 * @param task
	 * @return VBox with Name, ID and Description
	 */
	protected Node createMainDisplay(Task task, GridPane grid) {
		grid = (GridPane) createDisplay(task, grid);
		Label label = (Label) grid.getChildren().remove(0);
		label.setWrapText(true);
		GUI.setFancyText(label);
		VBox header = new VBox();
		header.setStyle(String.format(GUI.STYLE_COLOR, HEADER_COLOR));
		header.getChildren().add(label);
		grid.add(header, GRID_COL_HEADER, GRID_ROW_NAME, 2, 1); // span 2 col and 1 row
		
		// headers
		label = new Label("Details	: ");
		grid.add(label, GRID_COL_HEADER, GRID_ROW_DESCRIPTION); 
		GridPane.setValignment(label, GRID_HEADER_VERT_ALIGNMENT);
		label = new Label("Tags		: ");
		grid.add(label, GRID_COL_HEADER, GRID_ROW_TAGS); 
		GridPane.setValignment(label, GRID_HEADER_VERT_ALIGNMENT);
		
		// details
		label = new Label();
		label.setWrapText(true);
		if (task.getDetails()!=null&&!task.getDetails().isEmpty() ) {
			label.setText(task.getDetails());
		} else {
			label.setText("None");
		}
		grid.add(label, GRID_COL_CONTENT, GRID_ROW_DESCRIPTION); // 1st col, 2nd row
		//label.maxWidthProperty().bind();
		
		// tags
		label = new Label();
		label.setWrapText(true);
		if (task.getTags()!=null&&!task.getTags().isEmpty() ) {
			String temp = "";
			for (String tag : task.getTags()) {
				if (tag!=null) {
					temp += tag + "\n";
				}
			}
			label.setText(temp);
		} else {
			label.setText("None");
		}
		grid.add(label, GRID_COL_CONTENT, GRID_ROW_TAGS); 
		
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
}
