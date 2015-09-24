package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import MyTextBuddy.COMMAND_TYPE;

public class ParsedCommand {
	private enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR, INVALID, EXIT;
	}
	
	private String input;
	private CommandType cmdType;
	private String title;
	private Calendar start;
	private Calendar end;
	private String description;
	private ArrayList<String> tags;
	private String messageToUser;
	private int taskId;
	
	private static final String ERROR_INVALID_COMMAND = "Error: Invalid command";
	private static final String ERROR_NO_INPUT = "Error: No user input";
	private static final String ERROR_MISSING_ARGS = "Error: No arguments entered";
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_END = 1;
	private static final int INDEX_FOR_CMD = 0;
	private static final int INDEX_FOR_ARGS = 1;
	
	public ParsedCommand(CommandType cmdType, String title, Calendar start, Calendar end, 
			             String description, ArrayList<String> tags, String msg, int taskId) {
		this.cmdType = cmdType;
		this.title = title;
		this.start = start;
		this.end = end;
		this.description = description;
		this.tags = tags;
		this.messageToUser = msg;
		this.taskId = taskId;
	}
	
	public ParsedCommand parseCommand(String userInput) {
		if (userInput.length() == 0) {
			return createParsedCommandError(ERROR_NO_INPUT);
		} else {
			String input[] = userInput.split(" ", 2);
			String userCommand = input[INDEX_FOR_CMD];
			CommandType command = determineCommandType(userCommand);
			
			switch (command) {
		        case ADD :
		    	    return createParsedCommandAdd(input);
		    	    
		        case DELETE :
		        	return createParsedCommandDelete(input);
		        	
		        case EDIT :
		        	return createParsedCommandEdit(input);

		        case DISPLAY :
		        	return createParsedCommandDisplay(input);
		        	
		        case INVALID :
		        	return createParsedCommandError(ERROR_INVALID_COMMAND);
		        	
		        case EXIT : 
		        	return createParsedCommandExit();
		        
		        default :
		        	// is never visited
		        	throw new Error("ERROR");
			} 
		}
	}
	
	private ParsedCommand createParsedCommandExit() {
		ParsedCommand pc = new ParsedCommand(CommandType.EXIT, null, null, null, null, null, MESSAGE_EXIT, 0);
		return pc;
	}

	private ParsedCommand createParsedCommandDisplay(String input) {
		ParsedCommand pc = new ParsedCommand(CommandType.DISPLAY, null, null, null, null, null, null, 0);
		return pc;
	}

	private ParsedCommand createParsedCommandEdit(String[] input) {
		String inputArgs = input[INDEX_FOR_ARGS];
		if (inputArgs == null) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String title = StringParser.getTitleFromString(inputArgs);
			Calendar[] times = StringParser.getDatesTimesFromString(inputArgs);
			Calendar start = times[INDEX_FOR_START];
			Calendar end = times[INDEX_FOR_END];
			String description = StringParser.getDescriptionFromString(inputArgs);
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			ArrayList<String> tags = StringParser.getTagsFromString(inputArgs);
			
			ParsedCommand pc = new ParsedCommand(CommandType.EDIT, title, start, end, description,
					                             tags, null, taskId);
			return pc;
		}
	}

	private ParsedCommand createParsedCommandDelete(String[] input) {
		String inputArgs = input[INDEX_FOR_ARGS];
		if (inputArgs == null) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			ParsedCommand pc = new ParsedCommand(CommandType.DELETE, null, null, null, null,
                    tags, null, taskId);
			return pc;
		}
	}

	private ParsedCommand createParsedCommandAdd(String[] input) {
		String inputArgs = input[INDEX_FOR_ARGS];
		if (inputArgs == null) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String title = StringParser.getTitleFromString(inputArgs);
			Calendar[] times = StringParser.getDatesTimesFromString(inputArgs);
			Calendar start = times[INDEX_FOR_START];
			Calendar end = times[INDEX_FOR_END];
			String description = StringParser.getDescriptionFromString(inputArgs);
			ArrayList<String> tags = StringParser.getTagsFromString(inputArgs);
			
			ParsedCommand pc = new ParsedCommand(CommandType.ADD, title, start, end, description,
					                             tags, null, 0);
			return pc;
		}
	}

	private ParsedCommand createParsedCommandError(String errorMsg) {
		ParsedCommand pc = new ParsedCommand(CommandType.ERROR, null, null, null, null, null, errorMsg, 0);
		return pc;
	}
	
	private static CommandType determineCommandType(String commandTypeString) {
		if (commandTypeString.equalsIgnoreCase("add")) {
			return CommandType.ADD;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("show")) {
			return CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return CommandType.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return CommandType.EXIT;
		} else {
			return CommandType.INVALID;
		}
	}

	private void setError(String errorMsg) {
		this.cmdType = CommandType.ERROR;
		this.messageToUser = errorMsg;
	}
	
	public CommandType getCommandType() {
		return this.cmdType;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Calendar getStart() {
		return this.start;
	}
	
	public Calendar getEnd() {
		return this.end;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	public String getErrorMessage() {
		return this.messageToUser;
	}
	
	public int getTaskId() {
		return this.taskId;
	}
}
