package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public class MainWindow {
	private boolean isChanging = false;
	protected int previouslySelected = 0;
	protected List<TaskList> listOfTaskLists = new ArrayList<TaskList>();	

	final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.NEVER;
	final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	protected ScrollPane sp;
	protected VBox master;
	
	public MainWindow() {
		master = new VBox();
		sp = new ScrollPane(master);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		master.prefWidthProperty().bind(sp.widthProperty());
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public ScrollPane getNode() { 
		return sp;
	}
	
	/**
	 * Adds a node to the list
	 * @param item Must be an item that can set bind its width
	 */
	public void addToList(TaskList list) {
		listOfTaskLists.add(list);
		list.isPinnedWindow = false;
		list.clearHighlight(); // clear the detailed View
		list.closeList(); // start off closed
		list.getNode().prefWidthProperty().bind(master.widthProperty().subtract(8));
		master.getChildren().add(list.getNode());
	}
	
	/**
	 * Remove a node from the list
	 */
	public void removeFromList(TaskList list) {
		listOfTaskLists.remove(list);
		master.getChildren().remove(list);
	}
	
	/**
	 * Remove a node from the list
	 */
	public void removeFromList(int pos) {
		listOfTaskLists.remove(pos);
		master.getChildren().remove(pos);
	}
	
	/**
	 * Remove all nodes from the list
	 */
	public void removeAllFromList() {
		listOfTaskLists.clear();
		master.getChildren().clear();
	}
	
	/**
	 * Recalculates the entire TaskList display size
	 */
	public void recalculate() {
		if (!isChanging) {
			isChanging = true;
			for (TaskList list : listOfTaskLists) {
				list.recalculate();
			}
			isChanging = false;
		}
	}
}
