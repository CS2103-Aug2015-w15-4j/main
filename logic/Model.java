package logic;

import java.util.ArrayList;
import java.util.List;

import storage.Storage;

public class Model {

	private static Model instance = null;
	private String consoleMessage;
	private List<Task> tasksToDisplay;
	private List<Task> allTasks;
	private String avatarLocation;
	private String backgroundLocation;

	public static Model getInstance(String consoleMessage, List<Task> allTasks) {
		if(instance == null) {
			instance = new Model(consoleMessage,allTasks);
		}
		return instance;
	}

	protected Model(String consoleMessage, List<Task> allTasks) {
		this.consoleMessage = consoleMessage;
		this.tasksToDisplay = new ArrayList<Task>();
		this.allTasks = allTasks;
		Storage storage = new Storage();
		avatarLocation = storage.getAvatarPath();
		backgroundLocation = storage.getBackgroundPath();
	}

	public String getAvatarLocation() {
		return avatarLocation;
	}

	public void setAvatarLocation(String newLocation) {
		avatarLocation = newLocation;
	}

	public String getBackgroundLocation() {
		return backgroundLocation;
	}

	public void setBackgroundLocation(String newLocation) {
		backgroundLocation = newLocation;
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

	public void updateModel(String consoleMessage,List<Task> tasksToDisplay,
			List<Task> allTasks) {
		this.consoleMessage = consoleMessage;
		this.tasksToDisplay = tasksToDisplay;
		this.allTasks = allTasks;

	}

	public void updateModel(String consoleMessage, List<Task> allTasks) {
		this.consoleMessage = consoleMessage;
		this.allTasks = allTasks;

	}

	public void setConsoleMessage(String consoleMessage) {
		this.consoleMessage = consoleMessage;
	}
}
