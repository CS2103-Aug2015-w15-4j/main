package logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.queryparser.classic.ParseException;

import parser.ParsedCommand;
import parser.ParsedCommand.ConfigType;
import storage.Storage;

//@@author A0124777W
public class Logic {

	// Date formatter for
	public static final SimpleDateFormat displayDateFormatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");

	// Error Messages
	public static final String ERROR_INVALID_ID = "Error: Invalid ID";
	public static final String ERROR_CANNOT_REDO = "Error: Cannot redo without first doing an undo";
	public static final String ERROR_SEARCH_FAILED = "Error: Search Failed";

	// Constants for Searching to end of the day
	public static final int TODAY_LAST_HOUR = 23;
	public static final int TODAY_LAST_MINUTE = 59;
	public static final int TODAY_LAST_SECOND = 59;

	// Search String queries
	public static final String INCOMPLTETED_TASKS = "isCompleted: false";
	public static final String FLOATING_TASKS = "taskType: FLOATING_TASK";

	// Messages
	public static final String MESSAGE_NO_RESULTS_FOUND = "No results found";
	public static final String MESSAGE_1_RESULT_FOUND = "1 result found";
	public static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo";
	public static final String MESSAGE_UNDO_SUCCESSFUL = "Undo Successful";
	public static final String MESSAGE_AVATAR_SWITCHED = "Avatar switched";
	public static final String MESSAGE_FAILED_TO_SET_NEW_PATH = "Failed to Set new Path";
	public static final String MESSAGE_NOTHING_TO_REDO = "Oops! Nothing to redo!";
	public static final String MESSAGE_REDO_SUCCESSFUL = "Redo successful";
	public static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	public static final String MESSAGE_X_RESULTS_FOUND = "%1$s results found";
	public static final String MESSAGE_DATA_FILE_SET_TO = "data file set to %1$s";

	public static class UnrecognisedCommandException extends Exception {
		/**
		 *
		 */
		private static final long serialVersionUID = -297345203728757157L;

		public UnrecognisedCommandException(String message) {
			super(message);
		}
	}

	private Storage storage;
	private LinkedList<Command> commandHistory = new LinkedList<Command>();
	private Model model;
	private boolean canRedo = false;
	private LinkedList<Command> redoList = new LinkedList<Command>();

	private static final Logger logger = Logger.getGlobal();
	static {
		try {
			logger.addHandler(new FileHandler("Oracle log", 100000, 1, true));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// Contructor used for testing purposes
	public Logic(Storage storage) {
		this.storage = storage;
		this.model = new Model(storage);
	}

	// Constructor
	public Logic() {
		storage = new Storage();
		model = new Model(storage);
	}

	/* 	Executes the user command based on the parsed input. Updates the Model based on
	 *	the command executed.
	 *	@param ParsedCommand
	 *	@return Model
	 */
	public Model executeCommand(ParsedCommand parsedCommand) throws UnrecognisedCommandException {
		logger.log(Level.INFO, "executing user input ", parsedCommand);
		if (checkIfEmptyCommand(parsedCommand)) {
			model.updateModel(MESSAGE_INVALID_FORMAT);
			return model;
		}

		assert(parsedCommand != null);

		switch (parsedCommand.getCommandType()) {
			case ADD:
				canRedo = false;
				return executeAdd(parsedCommand);
			case UNDO:
				canRedo = false;
				return executeUndo();
			case REDO:
				return executeRedo();
			case DELETE:
				return executeDelete(parsedCommand);
			case CONFIG_DATA:
				return executeSetData(parsedCommand);
			case CONFIG_IMG:
				return executeSet(parsedCommand);
			case FLAG:
				// Fall Over
			case EDIT:
				canRedo = false;
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

	/*
	 *	Method for changing Avatar
	 */
	private Model executeSet(ParsedCommand parsedCommand) {
		String consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
		try {
			ParsedCommand.ConfigType type = parsedCommand.getConfigType();
			 if (type == ConfigType.AVATAR) {
				 storage.setAvatar(parsedCommand.getConfigPath());
				model.setAvatarLocation(parsedCommand.getConfigPath());
				consoleMessage = MESSAGE_AVATAR_SWITCHED;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, MESSAGE_FAILED_TO_SET_NEW_PATH, e);
			consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
			this.model.setConsoleMessage(consoleMessage);
			e.printStackTrace();
			return model;
		}

		model.setConsoleMessage(consoleMessage);
		return model;
	}

	/*
	 *	Method for changing file folder
	 */
	private Model executeSetData(ParsedCommand parsedCommand) {

		String consoleMessage = MESSAGE_FAILED_TO_SET_NEW_PATH;
		try {
			storage.setFileLocation(parsedCommand.getConfigPath());
			consoleMessage = String.format(MESSAGE_DATA_FILE_SET_TO,parsedCommand.getConfigPath());
			this.model.setConsoleMessage(consoleMessage);
		} catch (Exception e) {
			logger.log(Level.WARNING, MESSAGE_FAILED_TO_SET_NEW_PATH, e);
			this.model.setConsoleMessage(consoleMessage);
			e.printStackTrace();
			return model;
		}
		return model;
	}

	/*
	 *	Method for executing search
	 */
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
				consoleMessage = String.format(MESSAGE_X_RESULTS_FOUND,tasksToDisplay.size());
			}
			model.updateSearch(consoleMessage, parsedCommand, tasksToDisplay);
		} catch (IOException | ParseException e) {
			logger.log(Level.WARNING, ERROR_SEARCH_FAILED, e);
			model.updateModel(ERROR_SEARCH_FAILED);
			e.printStackTrace();
			return model;
		}

		return model;
	}

	/*
	 *	Method for executing edit
	 */
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

	/*
	 *	Method for executing undo
	 */
	private Model executeUndo() {
		canRedo = true;
		if (commandHistory.size() != 0) {
			Command toUndo = commandHistory.poll();
			redoList.addFirst(toUndo);
			toUndo.undo();
			model.updateModel(MESSAGE_UNDO_SUCCESSFUL);
		} else {
			model.updateModel(MESSAGE_NOTHING_TO_UNDO);
		}
		return model;
	}

	/*
	 *	Method for executing Redo
	 */
	private Model executeRedo() {
		if (canRedo) {
			if (redoList.size() != 0) {
				Command toRedo = redoList.poll();
				commandHistory.addFirst(toRedo);
				toRedo.execute();
				model.updateModel(MESSAGE_REDO_SUCCESSFUL);
			} else {
				model.updateModel(MESSAGE_NOTHING_TO_REDO);
			}
		} else {
			model.updateModel(ERROR_CANNOT_REDO);
		}
		return model;
	}

	/*
	 *	Method for executing delete
	 */
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

	/*
	 *	Method for executing Add.
	 */
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
		if (id >= getNewId() || id < 1){
			return false;
		}
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

	/*
	 *	Searches the taskList and finds the task specified.
	 *	@param taskList The task list to search in.
	 *	@param taskId	The id of the task to find.
	 * 	@return task specified
	 */
	public static Task searchList(List<Task> taskList, int taskId) throws IndexOutOfBoundsException {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		return null;
	}

	/*
	 *	Searches for all tasks from now to the end of the day. Returns the results in a List.
	 *	Results are sorted by date with the earliest tasks appearing first while the later tasks
	 *	appearing later.
	 *
	 *	@returns List<Task> returns the updated today List.
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
			logger.log(Level.WARNING, "update TodayList failed", e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "update TodayList failed", e);
			e.printStackTrace();
			return null;
		}
	}

	/*
	 *	Searches for all tasks that are due from today onwards. Returns the results in a List.
	 *	Returns the results in a List. Results are sorted by date with the earliest tasks appearing
	 *	first while the later tasks	appearing later.
	 *
	 *	@return List<Task> returns the updated Main List.
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
			logger.log(Level.WARNING, "update main List failed", e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "update main List failed", e);
			e.printStackTrace();
			return null;
		}
	}

	/*
	 *	Searches for all floating tasks. Results are returned in a List.
	 *	@returns List<Task> returns the updated floating List.
	 */
	public static List<Task> updateFloatingList() {
		try {
			Storage storage = new Storage();

			return Search.search(storage.getAllTasks(), FLOATING_TASKS);

		} catch (ParseException e) {
			logger.log(Level.WARNING, "update floating list failed", e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "update floating list failed", e);
			e.printStackTrace();
			return null;
		}
	}

	/*
	 *	Searches for all Overdue tasks. Results are returned in a List.
	 *	Results are sorted by date with the earliest tasks appearing
	 *	first while the later tasks	appearing later.
	 *
	 *	@return List<Task> returns the updated Overdue List.
	 */
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
			logger.log(Level.WARNING, "update overdue List failed", e);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "update overdue List failed", e);
			e.printStackTrace();
			return null;
		}
	}
}