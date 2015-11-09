//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;
import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;
import test.ParserTestingMethods;

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
		
	static final String TASK_ID_REGEX = "(^[0-9]+(?=\\s|$))";
	static final String SEARCH_TASK_ID_REGEX = "(^[0-9]+\\s*(?=$))";
	static final String TAG_REGEX = "(?<=\\s|^)#(\\w+)";
	static final String DESCRIPTION_REGEX = "(?<!\\\\)\"(.*)(?<!\\\\)\"(?!.*((?<!\\\\)\"))";
	private static final String TASK_STATUS_REGEX = "(?<=\\s|^)(todo|done|overdue)(?=\\s|$)";
	private static final String TASK_TYPE_REGEX = "(?<=\\s|^)(floating(?:task)?|deadline(?:task)?|event(?:task)?)(?:s)?(?=\\s|$)";
	private static final String OVERDUE_REGEX = "(?<=[^//s])(overdue)(?=\\s|$)";
	
	protected static final String NOT_TITLE_REGEX_KEYWORD_OK = "(" + "((?<=\\s|^)(from |fr |at |to |til |until |by |on |- ))?" + DateTimeParser.NO_KEYWORD_DATE_TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX + ")";  	
	
	static final Logger logger = Logger.getLogger(ParserTestingMethods.class.getName() );
	public static final String ERROR_INVALID_TABID = "Error: Invalid tab ID";
	
	protected static final int INDEX_FOR_START = 0;
	protected static final int INDEX_FOR_END = 1;

	protected abstract ParsedCommand parse(String[] input);
	
	protected static ParsedCommand createParsedCommandError(String errorMsg) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.ParsedCommandBuilder(MyParser.CommandType.ERROR)
						 		  .errorMessage(errorMsg)
						 		  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return createParsedCommandError(e.getMessage());
		}
	}

	protected static ParsedCommand createParsedCommand(CommandType cmd) {
		try {
			return new ParsedCommand.ParsedCommandBuilder(cmd).build();
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}
	
    
	// Methods to extract fields from string
	
	protected static String getTitleWithDateKeywords(String inputArgs) {
		if (inputArgs == null) {
			return null;
		}
		inputArgs = removeRegexPatternFromString(inputArgs, NOT_TITLE_REGEX_KEYWORD_OK);
		return inputArgs.trim();
	}
	
	static String getSearchKeywordsWithDateKeywords(String inputArgs) {
		inputArgs = removeRegexPatternFromString(inputArgs, NOT_TITLE_REGEX_KEYWORD_OK + "|" + TASK_TYPE_REGEX  +"|" + TASK_STATUS_REGEX);
		return inputArgs.trim();
	}
	
	protected static String getDescriptionFromString(String inputArgs) {
		if (inputArgs == null) {
			return null;
		}
		Pattern descriptionPattern = Pattern.compile(DESCRIPTION_REGEX);
		Matcher m = descriptionPattern.matcher(inputArgs);
		String description = "";
		
		if (m.find()) {
			description = m.group(1);
		}
		
		return description;
	}

	protected static int getTaskIdFromString(String inputArgs) {
		Pattern taskIdPattern = Pattern.compile(TASK_ID_REGEX);
		Matcher m = taskIdPattern.matcher(inputArgs);
		int taskId = -1;

		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}

		return taskId;
	}
	
	static int getSearchTaskIdFromString(String inputArgs) {
		Pattern taskIdPattern = Pattern.compile(SEARCH_TASK_ID_REGEX);
		Matcher m = taskIdPattern.matcher(inputArgs);
		int taskId = -1;

		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}

		return taskId;
	}

	protected static ArrayList<String> getTagsFromString(String inputArgs) {
		Pattern tagsPattern = Pattern.compile(TAG_REGEX);
		Matcher m = tagsPattern.matcher(inputArgs);
		ArrayList<String> tags = new ArrayList<String>();

		while (m.find()) {
			String tag = m.group(1);
			tags.add(tag);
		}

		return tags;
	}

	protected static Boolean getTaskStatusFromString(String inputArgs) {
		inputArgs = inputArgs.toLowerCase();
		Pattern taskStatusPattern = Pattern.compile(TASK_STATUS_REGEX);
		Matcher m = taskStatusPattern.matcher(inputArgs);
		String status = "";

		if (m.find()) {
			status = m.group();
		}

		return determineTaskStatus(status);
	}
	
	protected static Boolean getIsOverdueFromString(String inputArgs) {
		inputArgs = inputArgs.toLowerCase();
		Pattern taskStatusPattern = Pattern.compile(OVERDUE_REGEX);
		Matcher m = taskStatusPattern.matcher(inputArgs);

		if (m.find()) {
			return true;
		}

		return false;
	}

	protected static TaskType getTaskTypeFromString(String inputArgs) {
		inputArgs = inputArgs.toLowerCase();
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
		} else if (status.equalsIgnoreCase("done")) {
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
	
	
	protected static String removeDateKeywordSection(String input) {
		input = removeRegexPatternFromString(input, DateTimeParser.DATE_KEYWORD_REGEX);
		return input;
	}
	
	public static String removeRegexPatternFromString(String input, String regex) {
		if (input == null) {
			return null;
		}
		input = input.replaceAll("(?i)" + regex, "");
		return input.trim();
	}
	
	protected static boolean isMissingArguments(String[] input) {
		return input.length < 2;
	}
	
	protected static Calendar[] getStandardDatesTimes(String input) {
		DateTime parsedDatesTimes = getDatesTimesFromString(input);
		Calendar[] dates = parsedDatesTimes.getStdDatesTimes();
		return dates;
	}
	
	protected static Calendar[] getSearchDatesTimes(String input) {
		DateTime parsedDatesTimes = getDatesTimesFromString(input);
		Calendar[] dates = parsedDatesTimes.getSearchDatesTimes();
		return dates;
	}
	
	boolean mustRemoveDateKeywordSection(Calendar[] parsedTimes, String input) {
		return parsedTimes != null && parsedTimes[0] != null && parsedTimes.length > 2 && !containsTmr(input);
	}
	
	private boolean containsTmr(String input) {
		input = input.toLowerCase();
		Pattern taskTypePattern = Pattern.compile(DateTimeParser.TOMORROW_REGEX);
		Matcher m = taskTypePattern.matcher(input);

		if (m.find()) {
			return true;
		}
		
		return false;
	}
	
	// Date time parsing using chain of responsibility pattern
	private static DateTime getDatesTimesFromString(String input) {
		DateTimeParser dateTimeParserChain = getChainOfParsers();
		String dateSection = DateTimeParser.extractDateTimeSectionFromString(input.toLowerCase());
		DateTimeBuilder toParse = new DateTimeBuilder(dateSection);
		DateTime datesTimes = dateTimeParserChain.getDatesTimes(toParse);
		return datesTimes;
	}
	
	private static DateTimeParser getChainOfParsers() {
		DateTimeParser timeParser = new TimeParser();
		DateTimeParser formattedParser = new FormattedDateParser();
		DateTimeParser flexibleParser = new FlexibleDateParser();
		DateTimeParser nattyParser = new NattyDateParser();
		
		timeParser.setNextParser(formattedParser);
		formattedParser.setNextParser(flexibleParser);
		flexibleParser.setNextParser(nattyParser);
		return timeParser;
	}
}
