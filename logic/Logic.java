package logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import parser.ParsedCommand;
import storage.Storage;

public class Logic {

	private static final int EVENT = 3;

	private static final int DEADLINETASK = 2;

	private static final int TASK = 1;

	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";

	private Storage storage;
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
	private ArrayList<Task> taskList;

	public Logic() {
		storage = new Storage();
	}
	
	public static View initializeTaskList() {
		Storage storage = new Storage();
		return new View("",storage.getAllTasks());
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
		case EDIT:
			return executeUpdate(parsedCommand);
			/*
			 * case CLEAR: return clear(); case SORT: return sort(); case
			 * SEARCH: return search(removeFirstWord(userCommand));
			 */
		case ERROR:
			return new View(parsedCommand.getErrorMessage(),
					storage.getAllTasks());
		case EXIT:
			System.exit(0);
		default:
			// throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}

	private View executeUpdate(ParsedCommand userCommand) {

		Command command = new Update(userCommand,storage);
		String consoleMessage = "";
		// Check if update id is correct
		View view = new View(consoleMessage, storage.getAllTasks());
		if (!Update.checkValid(userCommand, view)) {
			return view;
		} else {
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);
		}

		consoleMessage = "Update Successful";
		return new View(consoleMessage, storage.getAllTasks());
	}

	private View executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			return new View("Nothing to undo", storage.getAllTasks());
		}
		return new View("Undo Successful", storage.getAllTasks());
	}

	private View executeDelete(ParsedCommand userCommand) {
		
		if (!Delete.checkValid(userCommand)) {
			String consoleMessage = "Error: Invalid taskID";
			return new View(consoleMessage, storage.getAllTasks());
		} else {
			Command command = new Delete(userCommand,storage);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			String consoleMessage = "Delete Successful";
			return new View(consoleMessage, storage.getAllTasks());
		}
	}

	private View executeAdd(ParsedCommand userCommand) {
		String consoleMessage = "";
		int newId = getNewId();
		View view = new View(consoleMessage, storage.getAllTasks());
		int taskType = checkTaskType(userCommand);
		
	/*	if (!Add.checkValid(userCommand, view)) {
			return view;
		} else { */
			Command command = new Add(userCommand, newId,storage);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			consoleMessage = "Add Successful";
			return new View(consoleMessage, storage.getAllTasks());
			//}
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
	
	private int checkTaskType(ParsedCommand userCommand) {
		
		if (userCommand.getEnd() == null && userCommand.getStart() == null) {
			return TASK;
		} else if (userCommand.getStart() == null && userCommand.getEnd() != null) {
			return DEADLINETASK;
		} else if (userCommand.getEnd() != null) {
			return EVENT;
		}
		
		return 0;
	}
	
	public static Task searchList(List<Task> taskList, int taskId) {
		
		for (int i=0; i<taskList.size(); i++) {
			if (taskList.get(i).getId() == taskId) {
				return taskList.get(i);
			}
		}
		
		return null;
	}

}
