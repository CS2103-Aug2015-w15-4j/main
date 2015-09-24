package logic;
import java.util.LinkedList;
import java.util.List;

import parser.ParsedCommand;

public class Logic {
	
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	
	private Storage storage;
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
	
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
	
	private View executeUndo() {
		if (commandHistory.size() != 0) {
			commandHistory.poll().undo();
		} else {
			return new View("Nothing to undo", storage.getAllTasks());
		}
		return new View("Undo Successful",storage.getAllTasks());
	}

	private View executeDelete(ParsedCommand userCommand) {
		
		int newId = getNewId();
		Command command = new Delete(userCommand,newId);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		String consoleMessage = "Delete Successful";
		return new View(consoleMessage,storage.getAllTasks());
	}

	private View executeAdd(ParsedCommand userCommand) {
		
		int newId = getNewId();
		Command command = new Add(userCommand,newId);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		String consoleMessage = "Add Successful";
		return new View(consoleMessage,storage.getAllTasks());
	}

	private static int getNewId() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		
		return taskList.get(taskList.size()-1).getId() + 1; 
	}
	
	
	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}
	
}
