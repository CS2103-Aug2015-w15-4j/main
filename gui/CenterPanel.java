package gui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.VBox;

//@@author A0122534R
public class CenterPanel {
	protected boolean isChanging = false; // is the list being editted at the moment?
	protected int previouslySelected = 0; // last selected item in the list
	protected List<TaskList> listOfTaskLists = new ArrayList<TaskList>();	
	protected VBox master;
	
	public CenterPanel() {
		master = new VBox();
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public VBox getNode() { 
		return master;
	}
	
	/**
	 * Adds a TaskList to the MainWindow
	 * @param list TaskList to be added
	 */
	public void addToList(TaskList list) {
		if (list.listSize>0) {// add only if it has more than one item
			listOfTaskLists.add(list);
			list.isPinnedWindow = false;
			closeAllLists();
			if (list.isListOpen) { // if it was open at the start, keep it open
				list.openList();
			}
			list.getNode().prefWidthProperty().bind(master.widthProperty().subtract(8));
			master.getChildren().add(list.getNode());
		}
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
	
	/**
	 * Closes all lists in the main window
	 */
	protected void closeAllLists() {
		GUIController.closeAllLists();
	}
}
