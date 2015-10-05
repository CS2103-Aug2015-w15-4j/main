package logic;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import parser.ParsedCommand;

public class Logic {

	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";

	private Storage storage;
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
	private ArrayList<Task> taskList;


	public Logic() {
		storage = new Storage();
	}

	public View executeCommand(String userCommand) {
		if(checkIfEmptyString(userCommand))
			return new View(MESSAGE_INVALID_FORMAT,storage.getAllTasks());

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
			/*	case CLEAR:
			return clear();			
		case SORT:
			return sort();
		case SEARCH:
			return search(removeFirstWord(userCommand));
			 */ 
		case ERROR:
			return new View(parsedCommand.getErrorMessage(), storage.getAllTasks());
		case EXIT:
			System.exit(0);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}

	private View executeUpdate(ParsedCommand userCommand) {

		Command command = new Update(userCommand);
		String consoleMessage = "";
		// Check if update id is correct
		View view = new View(consoleMessage,storage.getAllTasks());
		if (!Update.checkValid(userCommand, view)) {
			return view;		
		} else {
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);
		}

		consoleMessage = "Update Successful";
		return new View(consoleMessage,storage.getAllTasks());
	}

	private View executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			return new View("Nothing to undo", storage.getAllTasks());
		}
		return new View("Undo Successful",storage.getAllTasks());
	}

	private View executeDelete(ParsedCommand userCommand) {

		if (!Delete.checkValid(userCommand)) {
			String consoleMessage = "Error: Invalid taskID";
			return new View(consoleMessage,storage.getAllTasks());
		} else {
			Command command = new Delete(userCommand);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			String consoleMessage = "Delete Successful";
			return new View(consoleMessage,storage.getAllTasks());
		}
	}

	private View executeAdd(ParsedCommand userCommand) {
		String consoleMessage = "";
		int newId = getNewId();
		View view = new View(consoleMessage,storage.getAllTasks());
		if (!Add.checkValid(userCommand,view)) {
			return view;
		} else {
			Command command = new Add(userCommand,newId);
			invoke = new Invoker(command);
			invoke.execute();
			commandHistory.addFirst(invoke);

			consoleMessage = "Add Successful";
			return new View(consoleMessage,storage.getAllTasks());
		}
	}

	public static int getNewId() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		if (taskList.size() == 0) {
			return 0;
		} else {
			return taskList.get(taskList.size()-1).getId() + 1;
		}
	}
	
	
	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}
	
}
