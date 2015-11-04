//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public abstract class InputParser {
	static final String ERROR_INVALID_COMMAND = "Error: Invalid command";
	static final String ERROR_NO_INPUT = "Error: No user input";
	static final String ERROR_MISSING_ARGS = "Error: No arguments entered";
	static final String ERROR_MISSING_FIELDS = "Error: No fields were entered for editing";
	static final int INDEX_FOR_CMD = 0;
	static final int INDEX_FOR_ARGS = 1;
	static final int INDEX_FOR_SUBCMD = 0;
	static final int INDEX_FOR_SUBARGS = 1;
	static final String ERROR = "TEMP";
	static final String ERROR_INVALID_TASKID = "Error: Invalid/Missing taskId";
	
	
	static final String TO_REGEX = "-|to|until|till";
		
	protected static final String TASK_ID_REGEX = "(^[0-9]+(?=\\s|$))";
	protected static final String TAG_REGEX = "(?<=\\s|^)#(\\w+)";
	protected static final String DESCRIPTION_REGEX = "(?<!\\\\)\"(.*)(?<!\\\\)\"(?!.*((?<!\\\\)\"))";
	protected static final String TASK_STATUS_REGEX = "(?<=[^//s])(todo|completed|overdue)(?=\\s|$)";
	protected static final String TASK_TYPE_REGEX = "(?<=\\s|^)(floating(?:task)?|deadline(?:task)?|event(?:task)?)(?:s)?(?=\\s|$)";
	
	
	protected static final String NOT_TITLE_REGEX_KEYWORD_OK = "(" + "((?<=\\s|^)(from |fr |at |to |til |until |by |on |- ))?" + DateTimeParser.NO_KEYWORD_DATE_TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX  + "|(" + TASK_STATUS_REGEX + ")|((?<=\\s|^)(on )?(tmr|tomorrow|tomorow)(?=\\s|$)))";  	
	protected static final String NOT_TITLE_REGEX = "(" + "( from | fr | at | to | til | until | by | on | - )?" + DateTimeParser.DATE_TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX  + "|(" + TASK_STATUS_REGEX + "))";  	
	
	// private static final Logger logger = Logger.getLogger(StringParser.class.getName() );
	public static final String ERROR_INVALID_TABID = "Error: Invalid tab ID";
	
	protected static final int INDEX_FOR_START = 0;
	protected static final int INDEX_FOR_END = 1;

	abstract ParsedCommand parse(String[] input);
	
	static ParsedCommand createParsedCommandError(String errorMsg) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.Builder(MyParser.CommandType.ERROR)
						 		  .errorMessage(errorMsg)
						 		  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return createParsedCommandError(e.getMessage());
		}
	}

	static ParsedCommand createParsedCommand(CommandType cmd) {
		try {
			return new ParsedCommand.Builder(cmd).build();
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}
	
    
	// Methods to extract fields from string
	
	static String getTitleWithKeywordsFromString(String inputArgs) {
		// System.out.print("Parsing: " + inputArgs);
		inputArgs = removeRegexPatternFromString(inputArgs, NOT_TITLE_REGEX_KEYWORD_OK);
		// System.out.println(" to : " + inputArgs);
		return inputArgs.trim();
	}
	
	static String getSearchKeywordsWithDateKeywordsFromString(String inputArgs) {
		inputArgs = removeRegexPatternFromString(inputArgs, NOT_TITLE_REGEX_KEYWORD_OK + "|" + TASK_TYPE_REGEX);
		return inputArgs.trim();
	}
	
	static String getDescriptionFromString(String inputArgs) {
		Pattern descriptionPattern = Pattern.compile(DESCRIPTION_REGEX);
		Matcher m = descriptionPattern.matcher(inputArgs);
		String description = null;
		
		if (m.find()) {
			description = m.group(1);
		}
		
		return description;
	}

	static int getTaskIdFromString(String inputArgs) {
		Pattern taskIdPattern = Pattern.compile(TASK_ID_REGEX);
		Matcher m = taskIdPattern.matcher(inputArgs);
		int taskId = -1;

		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}

		return taskId;
	}

	static ArrayList<String> getTagsFromString(String inputArgs) {
		Pattern tagsPattern = Pattern.compile(TAG_REGEX);
		Matcher m = tagsPattern.matcher(inputArgs);
		ArrayList<String> tags = new ArrayList<String>();

		while (m.find()) {
			String tag = m.group(1);
			tags.add(tag);
		}

		return tags;
	}

	static Boolean getTaskStatusFromString(String inputArgs) {
		Pattern taskStatusPattern = Pattern.compile(TASK_STATUS_REGEX);
		Matcher m = taskStatusPattern.matcher(inputArgs);
		String status = "";

		if (m.find()) {
			status = m.group();
		}

		return determineTaskStatus(status);
	}

	static TaskType getTaskTypeFromString(String inputArgs) {
		Pattern taskTypePattern = Pattern.compile(TASK_TYPE_REGEX);
		Matcher m = taskTypePattern.matcher(inputArgs);
		String type = "";

		if (m.find()) {
			type = m.group(1);
		}
		
		return taskTypeToEnum(type);
	}

	
	private static Boolean determineTaskStatus(String status) {
		if (status.equalsIgnoreCase("todo")) {
			return false;
		} else if (status.equalsIgnoreCase("completed")) {
			return true;
		} else {
			return null;
		}
	}

	
	private static TaskType taskTypeToEnum(String type) {
		if (type.equals("floating")) {
			return TaskType.FLOATING_TASK;
		} else if (type.equals("deadline")) {
			return TaskType.DEADLINE_TASK;
		} else if (type.equals("event")) {
			return TaskType.EVENT;
		} else {
			return null;
		}
	}
	
	
	static String removeKeywordSection(String input) {
		input = removeRegexPatternFromString(input, DateTimeParser.DATE_KEYWORD_REGEX);
		return input;
	}
	
	public static String removeRegexPatternFromString(String input, String regex) {
		if (input == null) {
			return null;
		}
		input = input.replaceAll(regex, "");
		return input.trim();
	}
	
	static boolean isMissingArguments(String[] input) {
		return input.length < 2;
	}
	

	// Date time parsing using chain of responsibility pattern
	
	static Calendar[] getDatesTimesFromString(String input) {
		DateTimeParser dateTimeParserChain = getChainOfParsers();
		String[] emptyArr = new String[4];
		String dateSection = DateTimeParser.extractDateTimeSectionFromString(input);
		Calendar[] datesTimes = dateTimeParserChain.getDatesTimes(dateSection, emptyArr, emptyArr);
		return datesTimes;
	}
	
	private static DateTimeParser getChainOfParsers() {
		DateTimeParser formattedParser = new FormattedDateTimeParser();
		DateTimeParser flexibleParser = new FlexibleDateTimeParser();
		DateTimeParser nattyParser = new NattyDateTimeParser();
		
		formattedParser.setNextParser(flexibleParser);
		flexibleParser.setNextParser(nattyParser);
		return formattedParser;
	}
}
