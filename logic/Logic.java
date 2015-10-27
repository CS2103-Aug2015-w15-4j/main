package logic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import parser.ParsedCommand;
import parser.ParsedCommand.ConfigType;
import storage.Storage;

public class Logic {

	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";

	private Storage storage;
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
	private View view;

	public Logic() {
		storage = new Storage();
		view = View.getInstance("", storage.getAllTasks());
	}

	public static View initializeTaskList() {
		Storage storage = new Storage();
		return new View("", storage.getAllTasks());
	}

	public View executeCommand(String userCommand) {
		if (checkIfEmptyString(userCommand))
			return new View(MESSAGE_INVALID_FORMAT, storage.getAllTasks());

		ParsedCommand parsedCommand = ParsedCommand.parseCommand(userCommand);

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
		case SEARCH:
			return executeSearch(parsedCommand);
			// case SET:
			// return executeSetAvatar(parsedCommand);
		case ERROR:
			try {
				return new View(parsedCommand.getErrorMessage(),
						storage.getAllTasks());
			} catch (Exception e) {
				e.printStackTrace();
			}
		case EXIT:
			System.exit(0);
		default:
			// TODO: Change this line into ???
			// throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}

	private View executeSet(ParsedCommand parsedCommand) {
		String consoleMessage = "Failed to Set new Path";
		try {
			ParsedCommand.ConfigType type = parsedCommand.getConfigType();
			if (type == ConfigType.BACKGROUND) {
				storage.setBackground(parsedCommand.getConfigPath());
				view.setBackgroundLocation(parsedCommand.getConfigPath());
				consoleMessage = "Background switched";
			} else if (type == ConfigType.AVATAR) {
				storage.setAvatar(parsedCommand.getConfigPath());
				view.setAvatarLocation(parsedCommand.getConfigPath());
				consoleMessage = "Avatar switched";
			}
		} catch (Exception e) {
			consoleMessage = "Failed to Set new Path";
			this.view.updateView(consoleMessage, storage.getAllTasks());
			e.printStackTrace();
			return view;
		}

		view.updateView(consoleMessage, storage.getAllTasks());
		return view;
	}

	private View executeSetData(ParsedCommand parsedCommand) {

		String consoleMessage = "Failed to Set new Path";
		try {
			if (storage.setFileLocation(parsedCommand.getConfigPath())) {
				consoleMessage = "data file set to "
						+ parsedCommand.getConfigPath();
			}
			this.view.updateView(consoleMessage, storage.getAllTasks());
		} catch (Exception e) {
			this.view.updateView(consoleMessage, storage.getAllTasks());
			e.printStackTrace();
			return view;
		}
		return view;
	}

	private View executeSearch(ParsedCommand parsedCommand) {

		List<Task> tasksToDisplay;
		String consoleMessage = "Search failed";
		try {
			String query = parsedCommand.getKeywords(); // Stub, substitute with
														// parsedCommand.getQuery
			tasksToDisplay = Search.search(storage.getAllTasks(), query);
			if (tasksToDisplay.size() == 0) {
				consoleMessage = "No results found";
			} else if (tasksToDisplay.size() == 1) {
				consoleMessage = "Displaying 1 result for \"" + query + "\"";
			} else {
				consoleMessage = "Displaying " + tasksToDisplay.size()
						+ " results for \"" + query + "\"";
			}
			view.updateView(consoleMessage, tasksToDisplay,
					storage.getAllTasks());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;
	}

	private View executeUpdate(ParsedCommand userCommand) {

		String taskName = searchList(storage.getAllTasks(),
				userCommand.getTaskId()).getName();
		Command command = new Update(userCommand, storage);
		String consoleMessage = "Update Failed";
		// Check if update id is correct
		if (!Update.checkValid(userCommand, view)) {
			return view;
		} else {
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);
		}

		consoleMessage = taskName + " updated";
		view.updateView(consoleMessage, storage.getAllTasks());
		return view;
	}

	private View executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			view.updateView("Nothing to undo", storage.getAllTasks());
			return view;
		}
		view.updateView("Undo Successful", storage.getAllTasks());
		return view;
	}

	private View executeDelete(ParsedCommand userCommand) {

		String taskName = searchList(storage.getAllTasks(),
				userCommand.getTaskId()).getName();
		if (!Delete.checkValid(userCommand)) {
			String consoleMessage = "Error: Invalid taskID";
			view.updateView(consoleMessage, storage.getAllTasks());
			return view;
		} else {
			Command command = new Delete(userCommand, storage);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			String consoleMessage = taskName + " deleted";
			view.updateView(consoleMessage, storage.getAllTasks());
			return view;
		}
	}

	private View executeAdd(ParsedCommand userCommand) {
		String consoleMessage = "Add failed";
		int newId = getNewId();

		/*
		 * if (!Add.checkValid(userCommand, view)) { return view; } else {
		 */
		Command command = new Add(userCommand, newId, storage);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);

		consoleMessage = userCommand.getTitle() + " added";
		view.updateView(consoleMessage, storage.getAllTasks());
		return view;
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

	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}

	public static Task searchList(List<Task> taskList, int taskId) {

		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		return null;
	}

}