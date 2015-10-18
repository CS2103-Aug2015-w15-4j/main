package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.util.Status;

import parser.StringParser.TaskStatus;

public class ParsedCommand {
	public enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR, UNDO, DONE, INVALID, CONFIG_DATA, CONFIG_IMG, EXIT, CONFIG, SEARCH, SHOW;
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

			case SHOW: 
				return createParsedCommandShow(input);
			
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
			if (configType != ConfigType.INVALID) {
				try {
					String fileName = input[1];
					ParsedCommand pc = new ParsedCommand.Builder(CommandType.CONFIG_IMG)
														.configType(configType)
														.configPath(fileName)
														.build();
					return pc;
				} catch (InvalidArgumentsForParsedCommandException e) {
					return createParsedCommandError(e.getMessage());
				}
			} else {
				return createParsedCommandError(ERROR_INVALID_COMMAND);
			}
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
			try {
				String fileName = input[INDEX_FOR_ARGS];
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.CONFIG_DATA)
						  			  .configPath(fileName)
						  			  .build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e){
				return createParsedCommandError(e.getMessage());
			}
		}
	}

	private static ParsedCommand createParsedCommandUndo() {
		return new ParsedCommand.Builder(CommandType.UNDO).build();
	}

	private static ParsedCommand createParsedCommandExit() {
		return new ParsedCommand.Builder(CommandType.EXIT).build();
	}
	
	private static ParsedCommand createParsedCommandEdit(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs[] = input[INDEX_FOR_ARGS].split(" ", 2);
			if (isMissingArguments(inputArgs)) {
				return createParsedCommandError(ERROR_MISSING_FIELDS);
			} else {
				try {
					int parsedTaskId = StringParser.getTaskIdFromString(inputArgs[0]);
					String parsedTitle = StringParser.getTitleFromString(inputArgs[1]);
					Calendar[] parsedTimes = StringParser.getDatesTimesFromString(inputArgs[1]);
					String parsedDescription = StringParser.getDescriptionFromString(inputArgs[1]);
					ArrayList<String> parsedTags = StringParser.getTagsFromString(inputArgs[1]);
				
					ParsedCommand pc = new ParsedCommand.Builder(CommandType.EDIT)
														.taskId(parsedTaskId)
														.title(parsedTitle)
														.times(parsedTimes)
														.description(parsedDescription)
														.tags(parsedTags)
														.build();
					return pc;
				} catch (InvalidArgumentsForParsedCommandException e) {
					return createParsedCommandError(e.getMessage());
				}
			}
		}
	}
	
	private static boolean hasTaskId(int taskId) {
		return taskId >= 0;
	}

	private static boolean isMissingArguments(String[] input) {
		return input.length < 2;
	}

	private static ParsedCommand createParsedCommandDelete(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			try {
				String inputArgs = input[INDEX_FOR_ARGS].trim();
				int taskId = StringParser.getTaskIdFromString(inputArgs);
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.DELETE)
													.taskId(taskId)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
	}
	
	private static ParsedCommand createParsedCommandShow(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = StringParser.getTaskIdFromString(inputArgs);
			if (hasTaskId(taskId)) {
				return createParsedCommandShowDisplay(taskId);
			} else if (containsOnlyTaskId(inputArgs)) {
				return createParsedCommandError(ERROR_INVALID_TASKID);
			} else { // search	
				return createParsedCommandShowSearch(inputArgs);
			}
		}
	}

	private static ParsedCommand createParsedCommandShowSearch(String inputArgs) {
		ParsedCommand pc;
		try {
			String parsedKeywords = StringParser.getTitleFromString(inputArgs);
			Calendar[] parsedTimes = StringParser.getDatesTimesFromString(inputArgs);
			ArrayList<String> parsedTags = StringParser.getTagsFromString(inputArgs);
			TaskStatus parsedStatus = StringParser.getTaskStatusFromString(inputArgs);
			
			pc = new ParsedCommand.Builder(CommandType.SEARCH)
								  .searchKeywords(parsedKeywords)
								  .times(parsedTimes)
								  .tags(parsedTags)
								  .taskStatus(parsedStatus)
								  .build();
			return pc;
		
		} catch (InvalidArgumentsForParsedCommandException e) {
			return createParsedCommandError(e.getMessage());
		}
	}

	private static ParsedCommand createParsedCommandShowDisplay(int taskId) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.Builder(CommandType.DISPLAY)
	  							  .taskId(taskId)
	  							  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return createParsedCommandError(e.getMessage());
		}
	}

	private static boolean containsOnlyTaskId(String inputArgs) {
		return StringParser.removeRegexPatternFromString(inputArgs, StringParser.TASK_ID_REGEX).trim().equals("");
	}

	private static ParsedCommand createParsedCommandDone(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			try {
				int taskId = StringParser.getTaskIdFromString(inputArgs);
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.DONE)
													.taskId(taskId)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
	}

	private static ParsedCommand createParsedCommandAdd(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			
			try {
				String parsedTitle = StringParser.getTitleFromString(inputArgs);
				Calendar[] parsedTimes = StringParser.getDatesTimesFromString(inputArgs);
				String parsedDescription = StringParser.getDescriptionFromString(inputArgs);
				ArrayList<String> parsedTags = StringParser.getTagsFromString(inputArgs);
				
				ParsedCommand pc = new ParsedCommand.Builder(CommandType.ADD)
													.title(parsedTitle)
													.times(parsedTimes)
													.description(parsedDescription)
													.tags(parsedTags)
													.build();
				
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
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
			return CommandType.SHOW;
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
		
		public Builder title(String title) throws InvalidArgumentsForParsedCommandException {
			if (this.cmdType == CommandType.ADD && title == null) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_TITLE);
			}
			this.title = title;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder times(Calendar[] times) throws InvalidArgumentsForParsedCommandException {
			if (times == null) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_DATE);
			}
			this.firstDate = times[INDEX_FOR_START];
			this.secondDate = times[INDEX_FOR_END];
			return this;
		}
		
		public Builder tags(ArrayList<String> tags) {
			this.tags = tags;
			return this;
		}
		
		public Builder taskId(int taskId) throws InvalidArgumentsForParsedCommandException {
			if (taskId < 0) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_TASKID);
			}
			this.taskId = taskId;
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
		
		public Builder configPath(String path) throws InvalidArgumentsForParsedCommandException {
			if (path.equals("")) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_ARGS);
			}
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
			if (this.cmdType == CommandType.ADD) {
				int taskType = determineTaskType(this.firstDate, this.secondDate);
				this.taskType = taskType;
			}
			return new ParsedCommand(this);
		}
	}
}