package logic;

import java.io.IOException;
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
		private static final long serialVersionUID = 356113122590795999L;

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
		model = Model.getInstance("", storage.getAllTasks());
	}

	public static Model initializeTaskList() {
		Storage storage = new Storage();
		return new Model("", storage.getAllTasks());
	}

	public Model executeCommand(ParsedCommand parsedCommand) throws UnrecognisedCommandException {
		if (checkIfEmptyCommand(parsedCommand))
			return new Model(MESSAGE_INVALID_FORMAT, storage.getAllTasks());

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
				return new Model(parsedCommand.getErrorMessage(),
						storage.getAllTasks());
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
			this.model.updateModel(consoleMessage, storage.getAllTasks());
			e.printStackTrace();
			return model;
		}

		model.updateModel(consoleMessage, storage.getAllTasks());
		return model;
	}

	private Model executeSetData(ParsedCommand parsedCommand) {

		String consoleMessage = "Failed to Set new Path";
		try {
			storage.setFileLocation(parsedCommand.getConfigPath());
			consoleMessage = "data file set to "
						+ parsedCommand.getConfigPath();
			this.model.updateModel(consoleMessage, storage.getAllTasks());
		} catch (Exception e) {
			this.model.updateModel(consoleMessage, storage.getAllTasks());
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
			model.updateModel(consoleMessage, tasksToDisplay,
					storage.getAllTasks());
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
		Command command = new Update(userCommand, storage);
		String consoleMessage = "Update Failed";
		// Check if update id is correct
		if (!Update.checkValid(userCommand, model)) {
			return model;
		} else {
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);
		}

		consoleMessage = taskName + " updated";
		model.updateModel(consoleMessage, storage.getAllTasks());
		return model;
	}

	private Model executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			model.updateModel("Nothing to undo", storage.getAllTasks());
			return model;
		}
		model.updateModel("Undo Successful", storage.getAllTasks());
		return model;
	}

	private Model executeDelete(ParsedCommand userCommand) {

		String taskName = searchList(storage.getAllTasks(),
				userCommand.getTaskId()).getName();
		if (!Delete.checkValid(userCommand)) {
			String consoleMessage = "Error: Invalid taskID";
			model.updateModel(consoleMessage, storage.getAllTasks());
			return model;
		} else {
			Command command = new Delete(userCommand, storage);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			String consoleMessage = taskName + " deleted";
			model.updateModel(consoleMessage, storage.getAllTasks());
			return model;
		}
	}

	private Model executeAdd(ParsedCommand userCommand) {
		String consoleMessage = "Add failed";
		int newId = getNewId();

		/*
		 * if (!Add.checkValid(userCommand, model)) { return model; } else {
		 */
		Command command = new Add(userCommand, newId, storage);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);

		consoleMessage = userCommand.getTitle() + " added";
		model.updateModel(consoleMessage, storage.getAllTasks());
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

}