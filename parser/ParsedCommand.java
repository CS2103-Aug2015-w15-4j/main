package parser;

import java.util.ArrayList;
import java.util.Calendar;

public class ParsedCommand {
	public enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR, UNDO, DONE, INVALID, EXIT;
	}

	private CommandType cmdType;
	private String title;
	private Calendar firstDate;
	private Calendar secondDate;
	private String description;
	private ArrayList<String> tags;
	private int taskId;
	private int taskType;

	private static final String ERROR_INVALID_COMMAND = "Error: Invalid command";
	private static final String ERROR_NO_INPUT = "Error: No user input";
	private static final String ERROR_MISSING_ARGS = "Error: No arguments entered";
	private static final String ERROR_NOT_AN_ERROR = "Error: No error message as this is not an error";
	private static final String ERROR_MISSING_FIELDS = "Error: No fields were entered for editing";
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_END = 1;
	private static final int INDEX_FOR_CMD = 0;
	private static final int INDEX_FOR_ARGS = 1;
	private static final int TASK = 1;
	private static final int DEADLINE_TASK = 2;
	private static final int EVENT = 3;
	private static final String ERROR_INVALID_DATE = "Error: Invalid date(s) input";
	private static final String ERROR_INVALID_TASKID = "Error: Invalid/Missing taskId";
	private static final int INDEX_FOR_TASKID = 0;
	private static final int INDEX_FOR_FIELDS = 1;
	
    /**
     * This method creates a ParsedCommand object (constructor).
     * 
     * @param cmdType Type of command or error.
     * @param title Title of task or error message (for error objects).
     * @param start Start date and time of task.
     * @param end End date and time of task.
     * @param description Description of task.
     * @param tags ArrayList of tags in task, tags must be alphanumeric with no whitespace.
     * @param taskId TaskId of task for edit, delete.
     * @param taskType Type of task, 1 for task, 2 for deadline task, 3 for event.
     */
	public ParsedCommand(CommandType cmdType, String title, Calendar start,
			Calendar end, String description, ArrayList<String> tags,
			int taskId, int taskType) {
		this.cmdType = cmdType;
		this.title = title;
		this.firstDate = start;
		this.secondDate = end;
		this.description = description;
		this.tags = tags;
		this.taskId = taskId;
		this.taskType = taskType;
	}
	
	/**
	 * 
	 * @param userInput Entire string input by user.
	 * @return ParsedCommand object, with type error if userInput is invalid.
	 */
	public static ParsedCommand parseCommand(String userInput) {
		if (userInput.trim().length() == 0) {
			return createParsedCommandError(ERROR_NO_INPUT);
		} else {
			String input[] = userInput.trim().split(" ", 2);
			String userCommand = input[INDEX_FOR_CMD];
			CommandType command = determineCommandType(userCommand);

			switch (command) {
			case ADD:
				return createParsedCommandAdd(input);

			case DELETE:
				return createParsedCommandDelete(input);

			case EDIT:
				return createParsedCommandEdit(input);

			case DISPLAY : return createParsedCommandDisplay(input);
			
			case UNDO:
				return createParsedCommandUndo();

			case DONE:
				return createParsedCommandDone(input);

			case INVALID:
				return createParsedCommandError(ERROR_INVALID_COMMAND);

			case EXIT:
				return createParsedCommandExit();

			default:
				// is never visited
				throw new Error("ERROR");
			}
		}
	}

	private static ParsedCommand createParsedCommandUndo() {
		ParsedCommand pc = new ParsedCommand(CommandType.UNDO, null, null,
				null, null, null, 0, 0);
		return pc;
	}

	private static ParsedCommand createParsedCommandExit() {
		ParsedCommand pc = new ParsedCommand(CommandType.EXIT, null, null,
				null, null, null, 0, 0);
		return pc;
	}

	/*
	 * private static ParsedCommand createParsedCommandDisplay(String input) {
	 * ParsedCommand pc = new ParsedCommand(CommandType.DISPLAY, null, null,
	 * null, null, null, 0); return pc; }
	 */
	
	private static ParsedCommand createParsedCommandEdit(String[] input) {
		if (input.length < 2) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs[] = input[INDEX_FOR_ARGS].trim().split(" ", 2);
			int taskId = StringParser.getTaskIdFromString(inputArgs[INDEX_FOR_TASKID]);
			if (taskId < 0) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			}
			
			if (inputArgs.length < 2) { //missing edit fields
				return createParsedCommandError(ERROR_MISSING_FIELDS);
			}
			
			String fieldsInput = inputArgs[INDEX_FOR_FIELDS];
			String title = StringParser.getTitleFromString(fieldsInput);
			Calendar[] times = StringParser.getDatesTimesFromString(fieldsInput);
			Calendar start = times[INDEX_FOR_START];
			Calendar end = times[INDEX_FOR_END];
			String description = StringParser
					.getDescriptionFromString(fieldsInput);
			ArrayList<String> tags = StringParser.getTagsFromString(fieldsInput);

			ParsedCommand pc = new ParsedCommand(CommandType.EDIT, title, start, end, description, tags, taskId, 0);
			return pc;
		}
	}

	private static ParsedCommand createParsedCommandDelete(String[] input) {
		if (input.length < 2) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (taskId < 0) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else {
				ParsedCommand pc = new ParsedCommand(CommandType.DELETE, null,
						null, null, null, null, taskId, 0);
				return pc;
			}
		}
	}
	
	private static ParsedCommand createParsedCommandDisplay(String[] input) {
		if (input.length < 2) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (taskId <= 0) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else {
				ParsedCommand pc = new ParsedCommand(CommandType.DISPLAY, null,
						null, null, null, null, taskId, 0);
				return pc;
			}
		}
	}

	private static ParsedCommand createParsedCommandDone(String[] input) {
		if (input.length < 2) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (taskId < 0) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else {
				ParsedCommand pc = new ParsedCommand(CommandType.DONE, null,
						null, null, null, null, taskId, 0);
				return pc;
			}
		}
	}

	private static ParsedCommand createParsedCommandAdd(String[] input) {
		if (input.length < 2) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			String title = StringParser.getTitleFromString(inputArgs);
			Calendar[] times = StringParser.getDatesTimesFromString(inputArgs);
			if (times == null) {
				return createParsedCommandError(ERROR_INVALID_DATE);
			}
			Calendar start = times[INDEX_FOR_START];
			Calendar end = times[INDEX_FOR_END];
			int taskType = 0;
			if (end == null) {
				if (start == null) {
					taskType = TASK;
				} else {
					taskType = DEADLINE_TASK;
				}
			} else {
				taskType = EVENT;
			}
			String description = StringParser
					.getDescriptionFromString(inputArgs);
			ArrayList<String> tags = StringParser.getTagsFromString(inputArgs);

			ParsedCommand pc = new ParsedCommand(CommandType.ADD, title, start,
					end, description, tags, 0, taskType);
			return pc;
		}
	}

	private static ParsedCommand createParsedCommandError(String errorMsg) {
		ParsedCommand pc = new ParsedCommand(CommandType.ERROR, errorMsg, null,
				null, null, null, 0, 0);
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
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return CommandType.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return CommandType.DONE;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return CommandType.EXIT;
		} else {
			return CommandType.INVALID;
		}
	}
	
	/**
	 * Returns command type of command, including error CommandType.
	 * @return
	 */
	public CommandType getCommandType() {
		return this.cmdType;
	}
	
	/**
	 * Returns title of task, null if not applicable.
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Returns start date and time of task in Calendar format, null if not applicable.
	 * @return
	 */
	public Calendar getFirstDate() {
		return this.firstDate;
	}
	
	/**
	 * Returns end date and time of task in Calendar format, null if not applicable.
	 * @return
	 */
	public Calendar getSecondDate() {
		return this.secondDate;
	}
	
	/**
	 * Returns description of task, null if not applicable.
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns ArrayList of tags, empty ArrayList if not applicable.
	 * @return
	 */
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	/**
	 * Returns error message if ParsedCommand is of type Error.
	 * @return
	 */
	public String getErrorMessage() {
		if (this.cmdType == CommandType.ERROR) {
			return this.title;
		} else {
			return ERROR_NOT_AN_ERROR;
		}
	}
	
	/**
	 * Returns taskId of task, 0 if not applicable.
	 * @return
	 */
	public int getTaskId() {
		return this.taskId;
	}
	
	/**
	 * Returns 1 for task, 2 for deadline task, 3 for event, 0 if not applicable.
	 * @return
	 */
	public int getTaskType() {
		return this.taskType;
	}
	
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
}
