package gui;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

//@@author A0122534R
public class PinnedPanel {
	protected final static int PADDING = GUIController.PADDING;

	protected VBox master;
	protected TaskList pinnedList;

	public PinnedPanel() {
		master = new VBox();
		pinnedList = null;
	}

	/**
	 * @return the master/parent node for this object
	 */
	public VBox getNode() {
		return master;
	}

	/**
	 * Pins the list to the top window
	 * 
	 * @param list
	 *            list to be pinned
	 */
	public void pin(TaskList list) {
		if (list != null) {
			unpin();
			list.isPinnedWindow = true;
			list.selectFirstNode(); // select the first node, since it is moving
									// up now
			list.focusTask(); // create an instance of zoomed in task
			Region node = list.getNode();
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.prefWidthProperty().bind(master.widthProperty());
			node.prefHeightProperty().bind(master.heightProperty());
			master.getChildren().add(node);
			pinnedList = list; // store it
		}
	}

	/**
	 * Pins a focused Task into the window
	 * 
	 * @param Task
	 *            to be pinned
	 */
	public void pin(Region focusedTask) {
		if (focusedTask != null) {
			unpin();
			focusedTask.prefWidthProperty().unbind();
			focusedTask.prefHeightProperty().unbind();
			focusedTask.prefWidthProperty().bind(master.widthProperty());
			focusedTask.prefHeightProperty().bind(master.heightProperty());
			focusedTask.setPadding(new Insets(0, PADDING, 0, PADDING));
			master.getChildren().add(focusedTask);
		}
	}

	/**
	 * Unpins object from the pinnedWindow
	 */
	public void unpin() {
		if (pinnedList != null) {
			// focus view deactivate regardless of pinned window or task
			pinnedList.isPinnedWindow = false;
			// force it to close
			pinnedList.closeList();
			Region node = pinnedList.getNode();
			node.prefWidthProperty().unbind();
			node.prefHeightProperty().unbind();
			node.setPrefHeight(Region.USE_COMPUTED_SIZE);
			pinnedList = null;
		}
		master.getChildren().clear();
	}

	/**
	 * Recalculates the size of the pined window
	 */
	protected void recalculate() {
		if (pinnedList != null) {
			pinnedList.recalculate();
		}
	}
}
