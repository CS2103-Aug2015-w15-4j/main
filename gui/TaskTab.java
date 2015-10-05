package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.Task;

public class TaskTab {
	public static final int TABNAME = 1; // for the array check
	public static final int WIDTH = GUI.TABPANE_WIDTH;
	public static final int PADDING = 10;
	
	public static final ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	public static final ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	public static final Pos ALIGNMENT = Pos.TOP_LEFT;
	public static final String ID_VBOX = "taskVbox";
	public static final String ID_SCROLL = "taskScroll";
	
	protected VBox vbox;
	protected ScrollPane sp;
	protected List<Task> listOfTasks = new ArrayList<Task>(); // currently displayed tasks
	
	public TaskTab() {
		vbox = new VBox();
		vbox.setMinWidth(WIDTH);
		vbox.setMaxWidth(WIDTH);
		vbox.setAlignment(ALIGNMENT);
		vbox.setId(ID_VBOX);
		sp = new ScrollPane(vbox);
		sp.setPadding(new Insets(PADDING));
		sp.setFitToHeight(true);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Node getNode() { 
		return sp;
	}
	
	public void addTask(Task task) {
		if (task!=null) {
			listOfTasks.add(task);
			vbox.getChildren().add(createDisplay(task));
		}
	}
	
	public void addAllTasks(List<Task> tasks) {
		if (tasks!=null) {
			deleteAllTasks();
			
			for(Task task : tasks) {
				addTask(task);
			}
		}
	}
	
	public void deleteAllTasks() {
		vbox.getChildren().clear();
		listOfTasks.clear();		
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
				vbox.getChildren().remove(i);
				return true;
			}
		}
		return false;
	}
	
	protected Text createDisplay(Task task) {
		String text = "";
		// Name
		if (!task.getName().isEmpty()) {
			text += task.getName();
		} else {
			text += "Task	" + vbox.getChildren().size() + "\n";
		}
		
		// ID
		text += "ID		: " + task.getId() + "\n";
		
		// details
		if (!task.getDetails().isEmpty() ) {
			text += "Details	:" + task.getDetails() + "\n";
		} else {
			text += "Details	: None\n";
		}
		
		return new Text(text);
	}
}
