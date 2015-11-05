package logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.lucene.queryparser.classic.ParseException;

import parser.ParsedCommand;
import parser.ParsedCommand.ConfigType;
import storage.Storage;

public class Logic {

	// Date formatter for
	public static final SimpleDateFormat displayDateFormatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");

	// Error Messages
	public static final String ERROR_INVALID_ID = "Error: Invalid ID";
	public static final String ERROR_SEARCH_FAILED = "Error: Search Failed";

	// Constants for Searching to end of the day
	public static final int TODAY_LAST_HOUR = 23;
	public static final int TODAY_LAST_MINUTE = 59;
	public static final int TODAY_LAST_SECOND = 59;

	// Search String queries
	public static final String INCOMPLTETED_TASKS = "isCompleted: false";
	public static final String COMPLETED_TASKS = "isCompleted: true";
	public static final String FLOATING_TASKS = "taskType: FLOATING_TASK";

	// Messages
	public static final String MESSAGE_NO_RESULTS_FOUND = "No results found";
	public static final String MESSAGE_1_RESULT_FOUND = "1 result found";
	public static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo";
	public static final String MESSAGE_UNDO_SUCCESSFUL = "Undo Successful";
	public static final String MESSAGE_AVATAR_SWITCHED = "Avatar switched";
	public static final String MESSAGE_BACKGROUND_SWITCHED = "Background switched";
	public static final String MESSAGE_FAILED_TO_SET_NEW_PATH = "Failed to Set new Path";

	public static class UnrecognisedCommandException extends Exception {
		/**
		 *
		 */
		private static final long serialVersionUID = -297345203728757157L;

		public UnrecognisedCommandException(String message) {
			super(message);
		}
	}

	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";

	private Storage storage;
	private LinkedList<Command> commandHistory = new LinkedList<Command>();
	private Model model;

	public Logic() {
		storage = new Storage();
		model = Model.getInstance(storage);
	}

	public static Model initializeTaskList() {
		Storage storage = new Storage();
		return new Model(storage);
	}

	public Model executeCommand(ParsedCommand parsedCommand) throws UnrecognisedCommandException {

		if (checkIfEmptyCommand(parsedCommand)) {
			model.updateModel(MESSAGE_INVALID_FORMAT);
			return model;
		}

		switch (parsedCommand.getCommandType()) {
			case ADD:
				return executeAdd(parsedCommand);
			case UNDO:
				return executeUndo();
			case DELETE:
				return executeDelete(parsedCommand);
			// case CLEAR:
			// return clear();
			// case SORT:
			// return sort();
			case CONFIG_DATA:
				return executeSetData(parsedCommand);
			case CONFIG_IMG:
				return executeSet(parsedCommand);
			case FLAG:
				// Fall Over
			case EDIT:
				return executeUpdate(parsedCommand);
			case DISPLAY:
				// Fall Over
			case SEARCH:
				return executeSearch(parsedCommand);
			case ERROR:
				model.updateModel(parsedCommand.getErrorMessage());
				return model;
			case EXIT:
				System.exit(0);
			default:
				// throw an error if the command is not recognized
				throw new UnrecognisedCommandException("Unrecognized command type: " + parsedCommand.getCommandType());
		}
	}

	private Model executeSet(ParsedCommand parsedCommand) {
		String consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
		try {
			ParsedCommand.ConfigType type = parsedCommand.getConfigType();
			if (type == ConfigType.BACKGROUND) {
				model.setBackgroundLocation(parsedCommand.getConfigPath());
				consoleMessage = MESSAGE_BACKGROUND_SWITCHED;

			} else if (type == ConfigType.AVATAR) {
				model.setAvatarLocation(parsedCommand.getConfigPath());
				consoleMessage = MESSAGE_AVATAR_SWITCHED;
			}
		} catch (Exception e) {
			consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
			this.model.setConsoleMessage(consoleMessage);
			e.printStackTrace();
			return model;
		}

		model.setConsoleMessage(consoleMessage);
		return model;
	}

	private Model executeSetData(ParsedCommand parsedCommand) {

		String consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
		try {
			storage.setFileLocation(parsedCommand.getConfigPath());
			consoleMessage = "data file set to "
					+ parsedCommand.getConfigPath();
			this.model.setConsoleMessage(consoleMessage);
		} catch (Exception e) {
			this.model.setConsoleMessage(consoleMessage);
			e.printStackTrace();
			return model;
		}
		return model;
	}

	private Model executeSearch(ParsedCommand parsedCommand) {
		List<Task> tasksToDisplay;
		String consoleMessage;
		try {
			Search search = new Search();
			tasksToDisplay = search.multiSearch(storage.getAllTasks(), parsedCommand);
			if (tasksToDisplay.size() == 0) {
				consoleMessage = MESSAGE_NO_RESULTS_FOUND;
			} else if (tasksToDisplay.size() == 1) {
				consoleMessage = MESSAGE_1_RESULT_FOUND;
			} else {
				consoleMessage = tasksToDisplay.size() + " results found";
			}
			model.updateSearch(consoleMessage, parsedCommand, tasksToDisplay);
		} catch (IOException | ParseException e) {
			model.updateModel(ERROR_SEARCH_FAILED);
			e.printStackTrace();
			return model;
		}

		return model;
	}

	private Model executeUpdate(ParsedCommand userCommand) {

		Command command = new Update(userCommand, storage, model);
		if (!Update.checkValid(userCommand, model)) {
			return model;
		} else {
			command.execute();
			commandHistory.addFirst(command);
		}

		return model;
	}

	private Model executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			model.updateModel(MESSAGE_NOTHING_TO_UNDO);
			return model;
		}
		model.updateModel(MESSAGE_UNDO_SUCCESSFUL);
		return model;
	}

	private Model executeDelete(ParsedCommand userCommand) {

		if (!Delete.checkValid(userCommand,model)) {
			return model;
		} else {
			Command command = new Delete(userCommand, storage, model);
			command.execute();
			commandHistory.addFirst(command);

			return model;
		}
	}

	private Model executeAdd(ParsedCommand userCommand) {

		if (!Add.checkValid(userCommand, model)) {
			return model;

		} else {
			Command command = new Add(userCommand, storage, model);
			command.execute();
			commandHistory.addFirst(command);

			return model;
		}
	}

	/*
	 * Issues the next available ID. IDs take integer values and are not replaced when deleted.
	 */
	public static int getNewId() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		if (taskList.size() == 0) {
			return 1;
		} else {
			return taskList.get(taskList.size() - 1).getId() + 1;
		}
	}

	/*
	 * Checks if the ID specified is a currently assigned ID.
	 */
	public static boolean checkID(int id) {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		boolean isFound = false;
		for (Task task : taskList) {
			if (task.getId() == id) {
				isFound = true;
			}
		}
		return isFound;
	}

	private static boolean checkIfEmptyCommand(ParsedCommand parsedCommand) {
		return parsedCommand == null;
	}

	public static Task searchList(List<Task> taskList, int taskId) throws IndexOutOfBoundsException {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		return null;
	}

	/*
	 *	Searches for all tasks from now to the end of the day
	 */
	public static List<Task> updateTodayList() {
		try {
			Storage storage = new Storage();
			Calendar fromCal = Calendar.getInstance();

			Calendar toCal = Calendar.getInstance();
			toCal.set(Calendar.HOUR_OF_DAY, TODAY_LAST_HOUR);
			toCal.set(Calendar.MINUTE, TODAY_LAST_MINUTE);
			toCal.set(Calendar.SECOND, TODAY_LAST_SECOND);

			List<Task> todayList = Search.searchDate(storage.getAllTasks(), fromCal, toCal);
			Collections.sort(todayList,Task.compareByDate);

			return todayList;
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	/*
	 *	Updates the To-do tab
	 */
	public static List<Task> updateMainList() {
		try {
			Storage storage = new Storage();
			Calendar fromCal = Calendar.getInstance();
			Calendar toCal = Calendar.getInstance();
			toCal.setTime(new Date(Long.MAX_VALUE));

			List<Task> mainList = Search.searchDate(storage.getAllTasks(), fromCal, toCal);
			Collections.sort(mainList,Task.compareByDate);

			return mainList;

		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static List<Task> updateFloatingList() {
		try {
			Storage storage = new Storage();

			return Search.search(storage.getAllTasks(), FLOATING_TASKS);

		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static List<Task> updateOverdueList() {
		try {

			Storage storage = new Storage();
			Calendar toCal = Calendar.getInstance();

			Calendar fromCal = Calendar.getInstance();
			fromCal.setTime(new Date(0));

			List<Task> incompleteOverdue = Search.searchDate(storage.getAllTasks(), fromCal, toCal);
			List<Task> overdue = Search.search(incompleteOverdue, INCOMPLTETED_TASKS);
			Collections.sort(overdue,Task.compareByDate);

			return overdue;

		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static List<Task> updateCompletedList() {
		try {
			Storage storage = new Storage();
			return Search.search(storage.getAllTasks(), COMPLETED_TASKS);

		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
}