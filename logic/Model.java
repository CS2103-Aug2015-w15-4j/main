package logic;

import java.util.ArrayList;
import java.util.List;

import storage.Storage;

public class Model {

	public static final int DEFAULT_FOCUS = 1;
	private static Model instance = null;
	private String consoleMessage;
	private List<Task> searchList;
	private List<Task> todayList;
	private List<Task> allTasks;
	private List<Task> mainList;
	private int focusId;
	private String avatarLocation;
	private String backgroundLocation;
	
	private Storage storage;
	public static Model getInstance(Storage storage) {
		if(instance == null) {
			instance = new Model(storage);
		}
		return instance;
	}

	protected Model(Storage storage) {
		this.storage = storage;
		this.consoleMessage = "";
		this.focusId = DEFAULT_FOCUS;
		this.searchList = new ArrayList<Task>();
		this.todayList = Logic.updateTodayList();
		this.mainList = Logic.updateMainList();
		this.allTasks = storage.getAllTasks();
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
	
	public List<Task> getTodayList() {
		return todayList;
	}
	
	public List<Task> getMainList() {
		return mainList;
	}
	
	public List<Task> getSearchList() {
		return searchList;
	}

	public List<Task> getAllTasks() {
		return allTasks;
	}

	public int getFocusId() { return focusId; }

	/*
	 * Updates the model with the 
	 */
	public void updateSearchList(String consoleMessage,List<Task> tasksToDisplay) {
		this.consoleMessage = consoleMessage;
		this.searchList = tasksToDisplay;
	}
	
	public void updateModel(String consoleMessage) {
		this.consoleMessage = consoleMessage;
		this.allTasks = storage.getAllTasks();
		this.mainList = Logic.updateMainList();
		this.todayList = Logic.updateTodayList();
	}

	public void updateModel(String consoleMessage,int focusId) {
		this.consoleMessage = consoleMessage;
		this.allTasks = storage.getAllTasks();
		this.mainList = Logic.updateMainList();
		this.todayList = Logic.updateTodayList();
		this.focusId = focusId;
	}

	public void updateModel() {
		this.allTasks = storage.getAllTasks();
		this.mainList = Logic.updateMainList();
		this.todayList = Logic.updateTodayList();
	}

	public void updateFocus(int focusId) {
		this.focusId = focusId;
	}

	public void setConsoleMessage(String consoleMessage) {
		this.consoleMessage = consoleMessage;
	}
}
