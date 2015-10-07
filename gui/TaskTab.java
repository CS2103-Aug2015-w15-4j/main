package gui;

import java.util.ArrayList;
import java.util.List;

import com.sun.prism.paint.Color;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
	
	public static final String HEADER_COLOR = "YELLOW";//"#0000FF";

	protected HBox master; // the overall TaskTab manager
	protected VBox main; // main window
	protected ScrollPane sp; // a scrollpane managing the main window
	protected ListView<Node> listView; // the sidebar
	protected ObservableList<Node> items; // the items within the sidebar
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	
	public TaskTab() {
		master = new HBox();
		master.setMaxWidth(WIDTH);
		// sidebar
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		listView.setMaxWidth(WIDTH_SIDEBAR);
		listView.setMinWidth(WIDTH_SIDEBAR);
		listView.setId(ID_LIST);
		master.getChildren().add(listView);
		
		main = new VBox();
		main.setMinWidth(WIDTH_WINDOW);
		main.setMaxWidth(WIDTH_WINDOW);
		main.setAlignment(ALIGNMENT);
		main.setId(ID_VBOX);
		sp = new ScrollPane(main);
		sp.setFitToHeight(true);
		sp.setMinWidth(WIDTH-WIDTH_SIDEBAR);
		sp.setMaxWidth(WIDTH-WIDTH_SIDEBAR);
		sp.setPadding(new Insets(PADDING));
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
		master.getChildren().add(sp);
		
		// now add a listener for the sidebar
		listView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Node>() {
					public void changed(ObservableValue<? extends Node> ov, Node old_val, Node new_val) {
						main.getChildren().clear();
						main.getChildren().add(
							createMainDisplay(
								listOfTasks.get(listView.getSelectionModel().getSelectedIndex())
							)
						);
					}
				});
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Node getNode() { 
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
				listView.scrollTo(items.size()-1);
				listView.getSelectionModel().select(items.size()-1);
			}
		}
	}
	
	public void refresh() {
		listView.setItems(items);
	}
	
	public void addAllTasks(List<Task> tasks) {
		if (tasks!=null) {
			deleteAllTasks();
			
			for(Task task : tasks) {
				addTask(task, false);
			}
			
			refresh();
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
	 * Creates a VBox node for the main TaskTab display
	 * @param task
	 * @return VBox with Name, ID and Description
	 */
	protected Node createMainDisplay(Task task) {
		VBox subBox = (VBox) createDisplay(task, WIDTH_WINDOW); // get the name and ID
		
		// modify the header
		Text text = (Text) subBox.getChildren().remove(0);
		GUI.setFancyText(text);
		VBox header = new VBox();
		header.setStyle(String.format(GUI.STYLE_COLOR, HEADER_COLOR));
		header.getChildren().add(text);
		subBox.getChildren().add(0, header);
		
		// details
		text = new Text();
		text.setWrappingWidth(WIDTH_WINDOW);
		if (task.getDetails()!=null&&!task.getDetails().isEmpty() ) {
			text.setText("Details	:" + task.getDetails());
		} else {
			text.setText("Details	: None");
		}
		subBox.getChildren().add(text);
		
		return subBox;
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @return VBox with Name and ID
	 */
	protected Node createSidebarDisplay(Task task) {
		return createDisplay(task, WIDTH_SIDEBAR-PADDING);
	}
	
	/**
	 * 
	 * @param task Task to take information from
	 * @param wrappingWidth
	 * @return VBox with Name and ID
	 */
	protected Node createDisplay(Task task, int wrappingWidth) {
		VBox subBox = new VBox();
		subBox.setMaxWidth(wrappingWidth);
		Text text = new Text();
		text.setWrappingWidth(wrappingWidth);
		// Name
		if (!task.getName().isEmpty()) {
			text.setText(task.getName());
		} else {
			text.setText("Task	" + items.size());
		}
		subBox.getChildren().add(text);
		
		// ID
		text = new Text();
		text.setText("ID		: " + task.getId());
		//GUI.addParagraphToTextFlow(subBox, text);
		subBox.getChildren().add(text);
		
		
		/*
		// details
		text = new Text();
		if (task.getDetails()!=null&&!task.getDetails().isEmpty() ) {
			text.setText("Details	:" + task.getDetails());
		} else {
			text.setText("Details	: None");
		}
		// add to the vbox
		subBox.getChildren().add(text);//*/
		return subBox;
	}
}
