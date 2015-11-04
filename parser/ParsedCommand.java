//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;

import gui.GUIController;

public class ParsedCommand {
	public enum ConfigType {
		BACKGROUND, AVATAR, INVALID;
	}
	
	public enum TaskType {
		FLOATING_TASK, DEADLINE_TASK, EVENT;
	}

	private MyParser.CommandType cmdType;
	private String title;
	private String errorMessage;
	private String searchKeywords;
	private String path;
	private Calendar firstDate;
	private Calendar secondDate;
	private String description;
	private ArrayList<String> tags;
	private int taskId;
	private String guiType;
	private TaskType taskType;
	private Boolean isCompleted;
	private Boolean isOverdue;
	private ConfigType configType;
	
	private static final String ERROR_INVALID_DATE = "Error: Invalid date(s) input";

	// private static final Logger logger = Logger.getLogger(ParsedCommand.class.getName() );
	private static final String ERROR_MISSING_TITLE = "Error: Missing task title";
	
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_END = 1;

	
	public static final String ERROR_INVALID_TASK_STATUS = null;
	public static final String ERROR_INVALID_CONFIG_TYPE = null;
	public static final String ERROR_INVALID_PATH = null;
	public static final String ERROR_INVALID_GUI_TAB_ID = null;
	
	
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
		this.errorMessage = builder.errorMessage;
		this.searchKeywords = builder.searchKeywords;
		this.path = builder.path;
		this.firstDate = builder.firstDate;
		this.secondDate = builder.secondDate;
		this.description = builder.description;
		this.tags = builder.tags;
		this.taskId = builder.taskId;
		this.guiType = builder.guiType;
		this.taskType = builder.taskType;
		this.isCompleted = builder.isCompleted;
		this.isOverdue = builder.isOverdue;
		this.configType = builder.configType;
	}

	/**
	 * Returns command type of command, including error CommandType.
	 * @return command type.
	 */
	public MyParser.CommandType getCommandType() {
		return this.cmdType;
	}
	
	/**
	 * Returns title of task, empty string if not found, null if not applicable.
	 * @return title.
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Returns first date and time of task in Calendar format, null if not applicable.
	 * @return first date/time.
	 */
	public Calendar getFirstDate() {
		return this.firstDate;
	}
	
	/**
	 * Returns second date and time of task in Calendar format, null if not applicable.
	 * @return second date/time.
	 */
	public Calendar getSecondDate() {
		return this.secondDate;
	}
	
	/**
	 * Returns description of task, empty string if not found, null if not applicable.
	 * @return task description.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns ArrayList of tags, empty ArrayList if not applicable.
	 * @return tags.
	 */
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	/**
	 * Returns error message if ParsedCommand is of type Error, null if not applicable.
	 * @return error message.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	/**
	 * Returns taskId of task, -1 if not applicable.
	 * @return task ID.
	 */
	public int getTaskId() {
		return this.taskId;
	}
	
	/**
	 * Returns gui tab's index, empty string if not found, null if not applicable.
	 * @return gui tab index.
	 */
	public String getGuiType() {
		return this.guiType;
	}
	
	/**
	 * Returns TaskType.EVENT, TaskType.DEADLINE_TASK, TaskType.FLOATING_TASK or null.
	 * @return task type.
	 */
	public TaskType getTaskType() {
		return this.taskType;
	}
	
	/**
	 * Sets taskType of ParsedCommand object to argument.
	 * @param taskType
	 */
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
	
	/**
	 * Returns configuration type i.e. background or avatar for CONFIG_IMG, returns null if not applicable.
	 * @return
	 * @throws InvalidMethodForTaskTypeException
	 */
	public ConfigType getConfigType() {
		return this.configType;
	}
	
	/**
	 * Returns path for customisation for set file/background/avatar, returns null if not applicable.
	 * @return
	 * @throws InvalidMethodForTaskTypeException
	 */
	public String getConfigPath() {
		return this.path;
	}
	
	/**
	 * Returns true if completed, false if todo, null if irrelevant.
	 * @return
	 */
	public Boolean isCompleted() {
		return this.isCompleted;
	}
	
	/**
	 * Returns true if overdue, false if not, null if irrelevant.
	 * @return
	 */
	public Boolean isOverdue() {
		return this.isOverdue;
	}
	
	/**
	 * Returns search keywords for show command, empty string if not found, returns null if not applicable.
	 * @return
	 */
	public String getKeywords() {
		return this.searchKeywords;
	}
	
	static class Builder {
		private static final String ERROR_MISSING_ERROR_MESSAGE = "Error: Missing error message";

		private MyParser.CommandType cmdType;
		
		private String title = null;
		private String searchKeywords = null;
		private String errorMessage = null;
		private String path = null;
		private Calendar firstDate = null;
		private Calendar secondDate = null;
		private String description = null;
		private ArrayList<String> tags = new ArrayList<String>();
		private int taskId = -1;
		private String guiType = null;
		private TaskType taskType = null;
		private Boolean isCompleted = null;
		private Boolean isOverdue = null;
		private ConfigType configType = null;
		
		static final String ESC_CHAR_REGEX = "(?<!\\\\)(\\\\)";
		

		public Builder(MyParser.CommandType cmdType) {
			this.cmdType = cmdType;
		}
		
		public Builder title(String title) {
			this.title = removeEscapeChars(title);
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
				throw new InvalidArgumentsForParsedCommandException(InputParser.ERROR_INVALID_TASKID);
			}
			this.taskId = taskId;
			return this;
		}
		
		public Builder guiType(String gui) {
			this.guiType = gui;
			return this;
		}
		
		public Builder errorMessage(String msg) {
			this.errorMessage = msg;
			return this;
		}
		
		public Builder configType(ConfigType configType) {
			this.configType = configType;
			return this;
		}
		
		public Builder configPath(String path) throws InvalidArgumentsForParsedCommandException {
			if (path.equals("")) {
				throw new InvalidArgumentsForParsedCommandException(InputParser.ERROR_MISSING_ARGS);
			}
			this.path = path;
			return this;
		}
		
		public Builder isCompleted(Boolean status) {
			this.isCompleted = status;
			return this;
		}
		
		public Builder isOverdue(Boolean status) {
			this.isOverdue = status;
			return this;
		}
		
		public Builder searchKeywords(String keywords) {
			this.searchKeywords = removeEscapeChars(keywords);
			return this;
		}
		
		public Builder taskType(TaskType type) {
			this.taskType = type;
			return this;
		}
		
		public ParsedCommand build() throws InvalidArgumentsForParsedCommandException {
			MyParser.CommandType command = this.cmdType;
			if (command == null) {
				throw new InvalidArgumentsForParsedCommandException("Error: Missing command type for building ParsedCommand");
			} else if (command == MyParser.CommandType.ADD) {
				validateTitle();
				setTaskType();
			} else if (command == MyParser.CommandType.EDIT || command == MyParser.CommandType.FLAG || command == MyParser.CommandType.DELETE) {
				validateTaskId();
				if (command == MyParser.CommandType.FLAG) {
					validateTaskStatus();
				}
			} else if (command == MyParser.CommandType.CONFIG_IMG) {
				validateConfigType();
				validatePath();
			} else if (command == MyParser.CommandType.GUI_OPEN || command == MyParser.CommandType.GUI_CLOSE 
					|| command == MyParser.CommandType.GUI_PIN) {
				validateGuiType();
			} else if (command == MyParser.CommandType.ERROR) {
				validateErrorMsg();
			}
			return new ParsedCommand(this);
		}

		private void validateErrorMsg() throws InvalidArgumentsForParsedCommandException {
			if (this.errorMessage == null || this.errorMessage.isEmpty()) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_ERROR_MESSAGE);
			}
		}

		private void validateTaskId() throws InvalidArgumentsForParsedCommandException {
			if (this.taskId < 0) {
				throw new InvalidArgumentsForParsedCommandException(InputParser.ERROR_INVALID_TASKID);
			}
		}
		
		private void validateGuiType() throws InvalidArgumentsForParsedCommandException {
			try {
				int tabNumber = Integer.parseInt(guiType);
				if (tabNumber <= GUIController.taskListNames.length && tabNumber > 0) {
					guiType = Integer.toString(tabNumber - 1); // convert to 0 indexing
					return;
				}
			} catch (NumberFormatException e) {
				// do nothing
			}
			
			for (int i = 0; i < GUIController.taskListNames.length; i++) {
				if (guiType.trim().equalsIgnoreCase(GUIController.taskListNames[i])) {
					guiType = Integer.toString(i - GUIController.taskListNames.length); // return specific format to indicate name call
					return;
				}
			}
			throw new InvalidArgumentsForParsedCommandException(InputParser.ERROR_INVALID_TABID);
		}

		private void validateTitle() throws InvalidArgumentsForParsedCommandException {
			if (this.title == null || this.title.equals("")) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_MISSING_TITLE);
			}
		}
		
		private void validateTaskStatus() throws InvalidArgumentsForParsedCommandException {
			if (this.isCompleted == null) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_TASK_STATUS);
			}
		}
		
		private void validateConfigType() throws InvalidArgumentsForParsedCommandException {
			if (this.configType == ConfigType.INVALID || this.configType == null) {
				throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_CONFIG_TYPE);
			}
		}
		
		private void validatePath() throws InvalidArgumentsForParsedCommandException {
			if (this.path == null || this.path.isEmpty()){
				throw new InvalidArgumentsForParsedCommandException(ERROR_INVALID_PATH);
			}
		}

		private void setTaskType() {
			this.taskType = determineTaskType(this.firstDate, this.secondDate);
		}
		
		private static String removeEscapeChars(String input) {
			if (input == null) {
				return null;
			} else {
				return input.replaceAll(ESC_CHAR_REGEX, "");
			}
		}
		
		private static TaskType determineTaskType(Calendar start, Calendar end) {
			TaskType taskType;
			if (end == null) {
				if (start == null) {
					taskType = TaskType.FLOATING_TASK;
				} else {
					taskType = TaskType.DEADLINE_TASK;
				}
			} else {
				taskType = TaskType.EVENT;
			}
			return taskType;
		}
	}
}