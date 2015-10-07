package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.Task;

public class TaskTab {
	public static final int TABNUM = GUI.TASK; // for the array check
	public static final String TABNAME = GUI.tabNames[TABNUM]; 
	public static final int WIDTH = GUI.TABPANE_WIDTH;
	public static final int PADDING = 8;
	
	public static final String ID_LIST = "taskList";
	
	protected ListView<Node> listView;
	protected ObservableList<Node> items;
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	
	public TaskTab() {
		items = FXCollections.observableArrayList();
		listView = new ListView<Node>();
		listView.setMaxWidth(WIDTH);
		listView.setId(ID_LIST);
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Node getNode() { 
		return listView;
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
	 * @param refresh Decides whether or not the display refreshes
	 */
	public void addTask(Task task, boolean refresh) {
		if (task!=null) {
			listOfTasks.add(task);
			items.add(createDisplay(task));
			// then change focus to task tab
			GUI.tabPane.getSelectionModel().select(TABNUM);
			
			if (refresh) {
				refresh();
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
	
	protected Node createDisplay(Task task) {
		VBox subBox = new VBox();
		Text text = new Text();
		// Name
		if (!task.getName().isEmpty()) {
			text.setText(task.getName());
		} else {
			text.setText("Task	" + items.size());
		}
		subBox.getChildren().add(text);
		text = new Text();
		
		// ID
		text.setText("ID		: " + task.getId());
		subBox.getChildren().add(text);
		text = new Text();
		
		// details
		if (task.getDetails()!=null&&!task.getDetails().isEmpty() ) {
			text.setText("Details	:" + task.getDetails());
		} else {
			text.setText("Details	: None");
		}
		subBox.getChildren().add(text);
		
		return subBox;
	}
}
