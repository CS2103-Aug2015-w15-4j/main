package logic;

import java.util.ArrayList;
import java.util.List;

public class View {

	private String consoleMessage;
	private List<Task> tasksToDisplay;
	private List<Task> allTasks;

	public View(String consoleMessage, List<Task> allTasks) {
		this.consoleMessage = consoleMessage;
		this.tasksToDisplay = new ArrayList<Task>();
		this.allTasks = allTasks;
	}

	/*
	 * Used for getting completed/incomplete Task list tasksToDisplay contains
	 * either completedTasks or incompleteTasks
	 */
	public View(String consoleMessage, List<Task> tasksToDisplay,
			List<Task> allTasks) {
		this.consoleMessage = consoleMessage;
		this.tasksToDisplay = tasksToDisplay;
		this.allTasks = allTasks;
		System.out.println(allTasks.size());
	}

	public String getConsoleMessage() {
		return consoleMessage;
	}

	public List<Task> getTasksToDisplay() {
		return tasksToDisplay;
	}

	public List<Task> getAllTasks() {
		return allTasks;
	}

	public void setConsoleMessage(String consoleMessage) {
		this.consoleMessage = consoleMessage;
	}
}
