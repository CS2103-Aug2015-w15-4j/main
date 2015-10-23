package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import parser.ParsedCommand.TaskType;

public class StringParser {

	protected static final String TASK_ID_REGEX = "(^[0-9]+)";
	protected static final String TAG_REGEX = "(#(\\w+))";
	protected static final String DESCRIPTION_REGEX = "(\"[^\"]*?\")";
	protected static final String TASK_STATUS_REGEX = "(?<=[^//s])(todo|completed|overdue)(?=\\s|$)";
	protected static final String TASK_TYPE_REGEX = "(?<=\\s|^)(floating(?:task)?|deadline(?:task)?|event(?:task)?)(?:s)?(?=\\s|$)";
	
	private static Pattern description = Pattern.compile(DESCRIPTION_REGEX);
	private static Pattern taskId = Pattern.compile(TASK_ID_REGEX);
	protected static Pattern tags = Pattern.compile(TAG_REGEX);
	protected static Pattern taskStatus = Pattern.compile(TASK_STATUS_REGEX);
	private static final Pattern taskType = Pattern.compile(TASK_TYPE_REGEX);
	
	private static final String notTitleRegex = "(" + DateTimeParser.DATE_TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX  + "|(" + TASK_STATUS_REGEX + "))";  	
	
	private static final Logger logger = Logger.getLogger(StringParser.class.getName() );
	private static final String NOT_KEYWORDS_REGEX = notTitleRegex + "|" + TASK_TYPE_REGEX;
	
	// Used for testing purposes
	public static Date parseStringToDate(String input) {
		Date date = new Date();
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss zzz yyyy");
			date = format.parse(input);
			return date;
		} catch (ParseException | java.text.ParseException pe) {
			throw new IllegalArgumentException();
		}
	}
	
    // returns null if not found
	public static String getTitleFromString(String inputArgs) {
		String regex = notTitleRegex;
		inputArgs = inputArgs.replaceAll(regex, "");
		if (inputArgs.trim().equals("")) {
			return null;
		}
		
		/*Matcher m = beforeKeyword.matcher(inputArgs);
		if (m.find()) {
			inputArgs = m.group();
		}*/
		return inputArgs.trim();
	}

	public static String getDescriptionFromString(String inputArgs) {
		Matcher m = description.matcher(inputArgs);
		String description = null;

		if (m.find()) {
			description = m.group();
		}

		if (description != null) {
			return description.substring(1, description.length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * Parses string to return taskId (integer at beginning of string), returns 0 if not found.
	 * @param inputArgs
	 * @return
	 */
	public static int getTaskIdFromString(String inputArgs) {
		Matcher m = taskId.matcher(inputArgs);
		int taskId = -1;

		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}

		return taskId;
	}

	/**
	 * Parses string and returns ArrayList of tags (alphanumeric with no whitespace), 
	 * returns empty ArrayList if not found.
	 * 
	 * @param inputArgs
	 * @return
	 */
	public static ArrayList<String> getTagsFromString(String inputArgs) {
		Matcher m = tags.matcher(inputArgs);
		ArrayList<String> tags = new ArrayList<String>();

		while (m.find()) {
			String tag = m.group().substring(1);
			tags.add(tag);
		}

		return tags;
	}
	
	public static Calendar[] getDatesTimesFromString(String input) {
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
	
	public static String removeRegexPatternFromString(String input, String regex) {
		input = input.replaceAll(regex, "");
		return input.trim();
	}

	public static Boolean getTaskStatusFromString(String inputArgs) {
		Matcher m = taskStatus.matcher(inputArgs);
		String status = null;

		if (m.find()) {
			status = m.group();
		}

		if (status != null) {
			return determineTaskStatus(status);
		} else {
			return null;
		}
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

	public static TaskType getTaskTypeFromString(String inputArgs) {
		Matcher m = taskType.matcher(inputArgs);
		String type = null;

		if (m.find()) {
			type = m.group(1);
		}
		System.out.println(type);

		return taskTypeToEnum(type);
	}

	private static TaskType taskTypeToEnum(String type) {
		if (type == null) {
			return null; 
		} else if (type.equals("floating")) {
			return TaskType.FLOATING_TASK;
		} else if (type.equals("deadline")) {
			return TaskType.DEADLINE_TASK;
		} else if (type.equals("event")) {
			return TaskType.EVENT;
		} else {
			return null;
		}
	}

	static String getKeywordsFromString(String input) {
		return StringParser.removeRegexPatternFromString(input, NOT_KEYWORDS_REGEX);
	}

}
