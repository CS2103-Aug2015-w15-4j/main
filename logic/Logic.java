package logic;

import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import parser.ParsedCommand;
import parser.ParsedCommand.ConfigType;
import storage.Storage;

public class Logic {
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
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
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
		case SHOW:
			return executeShow(parsedCommand);
		case SEARCH:
			return executeSearch(parsedCommand);
			// case SET:
			// return executeSetAvatar(parsedCommand);
		case ERROR:
			try {
				model.updateModel(parsedCommand.getErrorMessage());
				return model;
			} catch (Exception e) {
				e.printStackTrace();
			}
		case EXIT:
			System.exit(0);
		default:
			// TODO: Change this line into ???
			// throw an error if the command is not recognized
			throw new UnrecognisedCommandException("Unrecognized command type: " + parsedCommand.getCommandType());
		}
	}

	private Model executeShow(ParsedCommand parsedCommand) {
		// TODO Auto-generated method stub
		return null;
	}

	private Model executeSet(ParsedCommand parsedCommand) {
		String consoleMessage = "Failed to Set new Path";
		try {
			ParsedCommand.ConfigType type = parsedCommand.getConfigType();
			if (type == ConfigType.BACKGROUND) {
				model.setBackgroundLocation(parsedCommand.getConfigPath());
				consoleMessage = "Background switched";

			} else if (type == ConfigType.AVATAR) {
				model.setAvatarLocation(parsedCommand.getConfigPath());
				consoleMessage = "Avatar switched";
			}
		} catch (Exception e) {
			consoleMessage = "Failed to Set new Path";
			this.model.setConsoleMessage(consoleMessage);
			e.printStackTrace();
			return model;
		}

		model.setConsoleMessage(consoleMessage);
		return model;
	}

	private Model executeSetData(ParsedCommand parsedCommand) {

		String consoleMessage = "Failed to Set new Path";
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

		List<Task> tasksToDisplay = null;
		String consoleMessage = "Search failed";
		try {
			String query = "";
			if (parsedCommand.getKeywords() != null && !parsedCommand.getKeywords().equals("")) {
				query = parsedCommand.getKeywords();
				tasksToDisplay = Search.search(storage.getAllTasks(), query);
			} else if (parsedCommand.getFirstDate() != null && parsedCommand.getSecondDate() != null) {
				query = "Displaying range of dates";
				tasksToDisplay = Search.searchDate(storage.getAllTasks(),parsedCommand.getFirstDate(),parsedCommand.getSecondDate());
			}
			if (tasksToDisplay.size() == 0) {
				consoleMessage = "No results found";
			} else if (tasksToDisplay.size() == 1) {
				consoleMessage = "Displaying 1 result for \"" + query + "\"";
			} else {
				consoleMessage = "Displaying " + tasksToDisplay.size()
						+ " results for \"" + query + "\"";
			}
			model.updateSearchList(consoleMessage, tasksToDisplay);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	private Model executeUpdate(ParsedCommand userCommand) {
		String taskName = searchList(storage.getAllTasks(),
				userCommand.getTaskId()).getName();
		Command command = new Update(userCommand, storage, model);
		String consoleMessage = "Update Failed";
		// Check if update id is correct
		if (!Update.checkValid(userCommand, model)) {
			return model;
		} else {
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);
		}

		return model;
	}

	private Model executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			model.updateModel("Nothing to undo");
			return model;
		}
		model.updateModel("Undo Successful");
		return model;
	}

	private Model executeDelete(ParsedCommand userCommand) {

		String taskName = searchList(storage.getAllTasks(),
				userCommand.getTaskId()).getName();
		if (!Delete.checkValid(userCommand)) {
			String consoleMessage = "Error: Invalid taskID";
			model.updateModel(consoleMessage);
			return model;
		} else {
			Command command = new Delete(userCommand, storage, model);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			return model;
		}
	}

	private Model executeAdd(ParsedCommand userCommand) {
		String consoleMessage = "Add failed";
		int newId = getNewId();

		/*
		 * if (!Add.checkValid(userCommand, model)) { return model; } else {
		 */
		Command command = new Add(userCommand, newId, storage, model);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);

		return model;
		// }
	}

	public static int getNewId() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		if (taskList.size() == 0) {
			return 1;
		} else {
			return taskList.get(taskList.size() - 1).getId() + 1;
		}
	}

	private static boolean checkIfEmptyCommand(ParsedCommand parsedCommand) {
		return parsedCommand == null;
	}

	public static Task searchList(List<Task> taskList, int taskId) {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		return null;
	}

	public static List<Task> updateTodayList() {
		try {
			Storage storage = new Storage();
			Calendar fromCal = Calendar.getInstance();
			Calendar toCal = Calendar.getInstance();
			toCal.set(Calendar.HOUR_OF_DAY,23);
			toCal.set(Calendar.MINUTE,59);
			toCal.set(Calendar.SECOND,59);

			return Search.searchDate(storage.getAllTasks(), fromCal, toCal);
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static List<Task> updateMainList() {
		try {
			Storage storage = new Storage();
			Calendar fromCal = Calendar.getInstance();

			Calendar toCal = Calendar.getInstance();
			toCal.setTime(new Date(Long.MAX_VALUE));

			return Search.searchDate(storage.getAllTasks(), fromCal, toCal);
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
}