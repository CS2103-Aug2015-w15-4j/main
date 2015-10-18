package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.util.Status;

import parser.StringParser.TaskStatus;

public class ParsedCommand {
	public enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR, UNDO, DONE, INVALID, CONFIG_DATA, CONFIG_IMG, EXIT, CONFIG, SEARCH;
	}
	
	public enum ConfigType {
		BACKGROUND, AVATAR, INVALID;
	}

	private CommandType cmdType;
	private String title;
	private Calendar firstDate;
	private Calendar secondDate;
	private String description;
	private ArrayList<String> tags;
	private int taskId;
	private int taskType;
	private StringParser.TaskStatus taskStatus;
	private ConfigType configType;

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
	
	private static final Logger logger = Logger.getLogger(ParsedCommand.class.getName() );
	private static final String ERROR_MISSING_TITLE = "Error: Missing task title";
	
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
	public ParsedCommand(Builder builder) {
		this.cmdType = builder.cmdType;
		this.title = builder.title;
		this.firstDate = builder.firstDate;
		this.secondDate = builder.secondDate;
		this.description = builder.description;
		this.tags = builder.tags;
		this.taskId = builder.taskId;
		this.taskType = builder.taskType;
		this.taskStatus = builder.taskStatus;
		this.configType = builder.configType;
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
			
			case CONFIG:
				return createParsedCommandConfig(input);
				
			case EXIT:
				return createParsedCommandExit();

			default:
				// is never visited
				throw new Error("ERROR");
			}
		}
	}

	private static ParsedCommand createParsedCommandConfig(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String[] subInput = input[1].split(" ");
			String subCommand = subInput[0];
			if (subCommand.equalsIgnoreCase("file")) {
				return createParsedCommandConfigData(subInput);
			} else {
				return createParsedCommandConfigImg(subInput);
			}
		}
	}

	private static ParsedCommand createParsedCommandConfigImg(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			ConfigType configType = determineConfigImgType(input[0]);
			String fileName = input[1];
			ParsedCommand pc;
			if (configType != ConfigType.INVALID) {
				pc = new ParsedCommand.Builder(CommandType.CONFIG_IMG)
									  .configType(configType)
									  .configPath(fileName)
									  .build();
			} else {
				pc = createParsedCommandError(ERROR_INVALID_COMMAND);
			}
			return pc;
		}
	}

	private static ConfigType determineConfigImgType(String subCommand) {
		if (subCommand.equalsIgnoreCase("background")) {
			return ConfigType.BACKGROUND;
		} else if (subCommand.equalsIgnoreCase("avatar")) {
			return ConfigType.AVATAR;
		} else {
			return ConfigType.INVALID;
		}
	}

	private static ParsedCommand createParsedCommandConfigData(String[] input) {
		if (isMissingArguments(input)) { // missing file name
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			ParsedCommand pc;
			String fileName = input[INDEX_FOR_ARGS];
			if (isInvalidPath(fileName)) {
				return createParsedCommandError(ERROR_MISSING_ARGS);
			} else {
				pc = new ParsedCommand.Builder(CommandType.CONFIG_DATA)
						  			  .configPath(fileName)
						  			  .build();
			}
			return pc;
		}
	}

	private static boolean isInvalidPath(String fileName) {
		return fileName.equals("");
	}

	private static ParsedCommand createParsedCommandUndo() {
		return new ParsedCommand.Builder(CommandType.UNDO).build();
	}

	private static ParsedCommand createParsedCommandExit() {
		return new ParsedCommand.Builder(CommandType.EXIT).build();
	}

	/*
	 * private static ParsedCommand createParsedCommandDisplay(String input) {
	 * ParsedCommand pc = new ParsedCommand(CommandType.DISPLAY, null, null,
	 * null, null, null, 0); return pc; }
	 */
	
	private static ParsedCommand createParsedCommandEdit(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			ParsedCommand pc;
			String inputArgs[] = input[INDEX_FOR_ARGS].split(" ", 2);
			
			try {
				pc = initEditParsedCommand(inputArgs);
			
				String fieldsInput = inputArgs[INDEX_FOR_FIELDS];
			
				pc.parseTitle(fieldsInput);
				pc.parseTimes(fieldsInput);
				pc.parseDescription(fieldsInput);
				pc.parseTags(fieldsInput);
			
				return pc;
			
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
	}
	
	private void parseTags(String fieldsInput) {
		ArrayList<String> tags = StringParser.getTagsFromString(fieldsInput);
		this.tags = tags;
	}

	private void parseDescription(String fieldsInput) {
		String description = StringParser.getDescriptionFromString(fieldsInput);
		this.description = description;
	}

	private void parseTimes(String fieldsInput) throws InvalidArgumentsForParsedCommandException {
		Calendar[] times = StringParser.getDatesTimesFromString(fieldsInput);
		if (isInvalidDateTime(times)) {
			throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_DATE);
		}
		Calendar start = times[INDEX_FOR_START];
		Calendar end = times[INDEX_FOR_END];
		this.firstDate = start;
		this.secondDate = end;
	}

	private void parseTitle(String fieldsInput) {
		String title = StringParser.getTitleFromString(fieldsInput);
		this.title = title;
	}

	private static ParsedCommand initEditParsedCommand(String[] inputArgs) throws InvalidArgumentsForParsedCommandException {
		int taskId = StringParser.getTaskIdFromString(inputArgs[INDEX_FOR_TASKID]);
		if (isInvalidTaskId(taskId)) {
			throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_TASKID);
		}
		
		if (isMissingArguments(inputArgs)) { //missing edit fields
			throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_FIELDS);
		}
		
		ParsedCommand pc = new ParsedCommand.Builder(CommandType.EDIT)
									   	    .taskId(taskId)
									   	    .build();
		return pc;
		
	}

	private static boolean isInvalidTaskId(int taskId) {
		return taskId < 0;
	}

	private static boolean isMissingArguments(String[] input) {
		return input.length < 2;
	}

	private static ParsedCommand createParsedCommandDelete(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (isInvalidTaskId(taskId)) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else {
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.DELETE)
						 							.taskId(taskId)
						 							.build();
				return pc;
			}
		}
	}
	
	private static ParsedCommand createParsedCommandDisplay(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (isInvalidTaskId(taskId)) {
				if (containsOnlyTaskId(inputArgs)) {
					return createParsedCommandError(ERROR_INVALID_TASKID);
				} else { // search	
					ParsedCommand pc;
					try {
						pc = initSearchParsedCommand(inputArgs);
					
						pc.parseKeywords(inputArgs);
						pc.parseTimes(inputArgs);
						pc.parseTags(inputArgs);
						pc.parseTaskStatus(inputArgs);
					
						return pc;
				
					} catch (InvalidArgumentsForParsedCommandException e) {
						return createParsedCommandError(e.getMessage());
					}
				}
			} else { // view task
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.DISPLAY)
													.taskId(taskId)
													.build();
				return pc;
			}
		}
	}

	private void parseTaskStatus(String inputArgs) {
		TaskStatus status = StringParser.getTaskStatusFromString(inputArgs);
		this.taskStatus = status;
	}

	private void parseKeywords(String inputArgs) {
		String searchKeywords = StringParser.getTitleFromString(inputArgs);
		this.setKeywords(searchKeywords);
	}

	private void setKeywords(String searchKeywords) {
		this.title = searchKeywords;
	}

	private static ParsedCommand initSearchParsedCommand(String inputArgs) throws InvalidArgumentsForParsedCommandException {
		String toSearch = StringParser.removeRegexPatternFromString(inputArgs, StringParser.DESCRIPTION_REGEX);
		if (toSearch.trim().equals("")) {
			throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_ARGS);
		}
		ParsedCommand pc = new ParsedCommand.Builder(CommandType.SEARCH)
											.build();
		return pc;
	}

	private static boolean containsOnlyTaskId(String inputArgs) {
		return StringParser.removeRegexPatternFromString(inputArgs, StringParser.TASK_ID_REGEX).trim().equals("");
	}

	private static ParsedCommand createParsedCommandDone(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (isInvalidTaskId(taskId)) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else {
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.DONE)
													.taskId(taskId)
													.build();
				return pc;
			}
		}
	}

	private static ParsedCommand createParsedCommandAdd(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			
			try {
				ParsedCommand pc = initAddParsedCommand(inputArgs);
				pc.parseTimes(inputArgs);
				pc.parseDescription(inputArgs);
				pc.parseTags(inputArgs);
				pc.parseTaskType();
			
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
	}
	
	private void parseTaskType() {
		int taskType = determineTaskType(this.firstDate, this.secondDate);
		this.taskType = taskType;
	}

	private static ParsedCommand initAddParsedCommand(String inputArgs) throws InvalidArgumentsForParsedCommandException {
		String title = StringParser.getTitleFromString(inputArgs);
		
		if (isMissingTitle(title)) {
			throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_TITLE);
		}
		
		ParsedCommand pc = new ParsedCommand.Builder(CommandType.ADD)
											.title(title)
											.build();
		return pc;
		
	}

	private static boolean isInvalidDateTime(Calendar[] times) {
		return times == null;
	}

	private static boolean isMissingTitle(String title) {
		return title == null;
	}

	private static int determineTaskType(Calendar start, Calendar end) {
		int taskType;
		if (end == null) {
			if (start == null) {
				taskType = TASK;
			} else {
				taskType = DEADLINE_TASK;
			}
		} else {
			taskType = EVENT;
		}
		return taskType;
	}

	private static ParsedCommand createParsedCommandError(String errorMsg) {
		ParsedCommand pc = new ParsedCommand.Builder(CommandType.ERROR)
											.errorMessage(errorMsg)
											.build();
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
		} else if (commandTypeString.equalsIgnoreCase("set")) {
			return CommandType.CONFIG;
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
	public String getErrorMessage() throws InvalidMethodForTaskTypeException {
		if (this.cmdType == CommandType.ERROR) {
			return this.title;
		} else {
			throw new InvalidMethodForTaskTypeException("error");
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
	
	/**
	 * Returns configuration type i.e. background or avatar for CONFIG_IMG, returns null if not applicable.
	 * @return
	 * @throws InvalidMethodForTaskTypeException
	 */
	public ConfigType getConfigType() throws InvalidMethodForTaskTypeException {
		if (this.cmdType == CommandType.CONFIG_IMG) {
			return this.configType;
		} else {
			throw new InvalidMethodForTaskTypeException("Not a CONFIG command");
		}
	}
	
	/**
	 * Returns path for customisation for set file/background/avatar, returns null if not applicable.
	 * @return
	 * @throws InvalidMethodForTaskTypeException
	 */
	public String getConfigPath() throws InvalidMethodForTaskTypeException {
		if (this.cmdType == CommandType.CONFIG_IMG || this.cmdType == CommandType.CONFIG_DATA) {
			return this.description;
		} else {
			throw new InvalidMethodForTaskTypeException("Not a CONFIG command");
		}
	}
	
	/**
	 * Returns task status TODO, COMPLETED, OVERDUE or null if not applicable.
	 * @return
	 */
	public TaskStatus getTaskStatus() {
		return this.taskStatus;
	}
	
	/**
	 * Returns search keywords for show command, returns null if not applicable.
	 * @return
	 */
	public String getKeywords() throws InvalidMethodForTaskTypeException {
		if (this.cmdType == CommandType.SEARCH) {
			return this.title;
		} else {
			throw new InvalidMethodForTaskTypeException("Not a SEARCH command, no search keywords.");
		}
	}
	
	private static class Builder {
		private ParsedCommand.CommandType cmdType;
		
		private String title = null;
		private Calendar firstDate = null;
		private Calendar secondDate = null;
		private String description = null;
		private ArrayList<String> tags = new ArrayList<String>();
		private int taskId = 0;
		private int taskType = 0;
		private TaskStatus taskStatus = null;
		private ConfigType configType = null;

		public Builder(ParsedCommand.CommandType cmdType) {
			this.cmdType = cmdType;
		}
		
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder firstDate(Calendar date) {
			this.firstDate = date;
			return this;
		}
		
		public Builder secondDate(Calendar date) {
			this.secondDate = date;
			return this;
		}
		
		public Builder tags(ArrayList<String> tags) {
			this.tags = tags;
			return this;
		}
		
		public Builder taskId(int taskId) {
			this.taskId = taskId;
			return this;
		}
		
		public Builder taskType(int taskType) {
			this.taskType = taskType;
			return this;
		}
		
		public Builder errorMessage(String msg) {
			this.title = msg;
			return this;
		}
		
		public Builder configType(ConfigType configType) {
			this.configType = configType;
			return this;
		}
		
		public Builder configPath(String path) {
			this.description = path;
			return this;
		}
		
		public Builder taskStatus(TaskStatus status) {
			this.taskStatus = status;
			return this;
		}
		
		public Builder searchKeywords(String keywords) {
			this.title = keywords;
			return this;
		}
		
		public ParsedCommand build() {
			return new ParsedCommand(this);
		}
	}
}