
package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import storage.Storage;
import parser.ParsedCommand;

//@@author A0124777W
public class Model {

	public static final int DEFAULT_FOCUS = 1;
	private String consoleMessage;
	private List<Task> searchList;
	private List<Task> todayList;
	private List<Task> allTasks;
	private List<Task> mainList;
	private List<Task> floatingList;
	private List<Task> overdueList;
	private int focusId;
	private String avatarLocation;
	private ParsedCommand searchQuery;
	
	private Storage storage;

	/*
	 *	Constuctor used for testing
	 */
	public Model() {

	}

	public Model(Storage storage) {
		this.storage = storage;
		this.consoleMessage = "";
		this.focusId = DEFAULT_FOCUS;
		this.searchList = new ArrayList<Task>();
		this.todayList = Logic.updateTodayList();
		this.mainList = Logic.updateMainList();
		this.allTasks = storage.getAllTasks();
		avatarLocation = storage.getAvatarPath();
	}

	public String getAvatarLocation() {
		return avatarLocation;
	}

	public void setAvatarLocation(String newLocation) {
		avatarLocation = newLocation;
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

	/*
	 *	Updates the searchQuery so that Model can automatically refresh the searchList through
	 * 	future user commands.
	 */
	public void updateSearch(String consoleMessage, ParsedCommand searchQuery, List<Task> searchList) {
		this.consoleMessage = consoleMessage;
		this.searchQuery = searchQuery;
		this.searchList = searchList;
	}

	/*
	 *	Method to automate updating the searchList whenever updateModel is called.
	 */
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

	/*
	 *	Updates all possible fields in the Model and assigns the new message as the consoleMessage.
	 */
	public void updateModel(String consoleMessage) {
		this.consoleMessage = consoleMessage;
		updateModel();
	}

	/*
	 *	Updates all possible fields in the Model and assigns the new message as the consoleMessage
	 * 	as well as the new task id as the focusId.
	 */
	public void updateModel(String consoleMessage,int focusId) {
		this.consoleMessage = consoleMessage;
		updateModel();
		this.focusId = focusId;
	}

	/*
	 *	Updates all of : allTask, mainList, todayList, floatingList, overdueList and searchList if possible.
	 */
	public void updateModel() {
		this.allTasks = storage.getAllTasks();
		this.mainList = Logic.updateMainList();
		this.todayList = Logic.updateTodayList();
		this.floatingList = Logic.updateFloatingList();
		this.overdueList = Logic.updateOverdueList();
		if (searchQuery != null) {
			updateSearchList();
		}
	}

	/*
	 *	Sets a particular task as the focus for the focus view.
	 */
	public void updateFocus(int focusId) {
		this.focusId = focusId;
	}


	/*
	 *	Sets the consoleMessage to the new message without updating the view.
	 */
	public void setConsoleMessage(String consoleMessage) {
		this.consoleMessage = consoleMessage;
	}
}
