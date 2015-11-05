package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import storage.Storage;
import parser.ParsedCommand;

public class Model {

	public static final int DEFAULT_FOCUS = 1;
	private static Model instance = null;
	private String consoleMessage;
	private List<Task> searchList;
	private List<Task> todayList;
	private List<Task> allTasks;
	private List<Task> mainList;
	private List<Task> floatingList;
	private List<Task> overdueList;
	private List<Task> completedList;
	private int focusId;
	private String avatarLocation;
	private String backgroundLocation;
	private ParsedCommand searchQuery;
	
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

	public List<Task> getFloatingList() { return floatingList; }

	public List<Task> getOverdueList() { return overdueList; }

	public int getFocusId() { return focusId; }


	public void updateSearch(String consoleMessage, ParsedCommand searchQuery, List<Task> searchList) {
		this.consoleMessage = consoleMessage;
		this.searchQuery = searchQuery;
		this.searchList = searchList;
	}

	public void updateSearchList() {
		try {
			Search search = new Search();
			this.searchList = search.multiSearch(storage.getAllTasks(), searchQuery);
		} catch (ParseException e) {
			consoleMessage = "Error: Search ParseException";
		} catch (IOException e) {
			consoleMessage = "Error: Search IOException";
		}
	}
	
	public void updateModel(String consoleMessage) {
		this.consoleMessage = consoleMessage;
		updateModel();
	}

	public void updateModel(String consoleMessage,int focusId) {
		this.consoleMessage = consoleMessage;
		updateModel();
		this.focusId = focusId;
	}

	public void updateModel() {
		this.allTasks = storage.getAllTasks();
		this.mainList = Logic.updateMainList();
		this.todayList = Logic.updateTodayList();
		this.floatingList = Logic.updateFloatingList();
		this.overdueList = Logic.updateOverdueList();
		this.completedList = Logic.updateCompletedList();
		if (searchQuery != null) {
			updateSearchList();
		}
	}

	public void updateFocus(int focusId) {
		this.focusId = focusId;
	}

	public void setConsoleMessage(String consoleMessage) {
		this.consoleMessage = consoleMessage;
	}
}
