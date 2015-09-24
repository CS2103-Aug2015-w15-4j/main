package logic;
import java.util.LinkedList;

public class Logic {

	private static final String COMMAND_UNDO = "Undo";
	// User commands
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SORT = "sort";
	
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	
	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID, SORT, SEARCH, UNDO
	}
	
	private Storage storage;
	private Invoker invoke;
	private LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
	
	public Logic() {
		storage = new Storage();
	}
	
	public View executeCommand(String userCommand) {
		if(checkIfEmptyString(userCommand))
			return new View(MESSAGE_INVALID_FORMAT,storage.getAllTasks());

		COMMAND_TYPE commandType = getCommandType(userCommand);
		switch (commandType) {
		case ADD:
			return executeAdd(removeFirstWord(userCommand));
		case UNDO:
			return executeUndo();
		case DELETE:
			return executeDelete(removeFirstWord(userCommand));
	/*	case CLEAR:
			return clear();			
		case SORT:
			return sort();
		case SEARCH:
			return search(removeFirstWord(userCommand));
	*/ 
		case INVALID:
			return new View(MESSAGE_INVALID_FORMAT, storage.getAllTasks());
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

	private View executeDelete(String userCommand) {
		Parser parser = new Parser();
		userData specifications = parser.parse(userCommand);
		Command command = new Delete(specifications);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		String consoleMessage = "Delete Successful";
		return new View(consoleMessage,storage.getAllTasks());
	}

	private View executeAdd(String userCommand) {
		Parser parser = new Parser();
		userData specifications = parser.parse(userCommand);
		Command command = new Add(specifications);
		invoke = new Invoker(command);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		String consoleMessage = "Add Successful";
		return new View(consoleMessage,storage.getAllTasks());
	}

	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		if (commandTypeString.equalsIgnoreCase(COMMAND_ADD)) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_DISPLAY)) {
			return COMMAND_TYPE.DISPLAY;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_CLEAR)) {
			return COMMAND_TYPE.CLEAR;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_DELETE)) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_SORT)) {
			return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_SEARCH)) {
			return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_EXIT)) {
			return COMMAND_TYPE.EXIT;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_UNDO)) {
			return COMMAND_TYPE.UNDO;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static COMMAND_TYPE getCommandType(String userCommand) {
		String commandTypeString = getFirstWord(userCommand);
		COMMAND_TYPE commandType = determineCommandType(commandTypeString);
		return commandType;
	}
	
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}
	
	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
}
