package parser;

import java.util.ArrayList;
import java.util.Calendar;
import parser.MyParser.CommandType;

public class ShowParser extends InputParser {
	private static final String NOT_KEYWORDS_REGEX = NOT_TITLE_REGEX + "|" + TASK_TYPE_REGEX;
	
	private static String getKeywordsFromString(String input) {
		return removeRegexPatternFromString(input, NOT_KEYWORDS_REGEX);
	}
	

	private static boolean hasTaskId(int taskId) {
		return taskId >= 0;
	}
	
	
	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommand(CommandType.GUI_SHOW);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = getTaskIdFromString(inputArgs);
			if (hasTaskId(taskId)) { // show task details
				return ShowParser.createParsedCommandShowDisplay(taskId);
			} else if (containsOnlyTaskId(inputArgs)) { // invalid taskId - how to get this???
				return InputParser.createParsedCommandError(ERROR); 
			} else { // search	
				return ShowParser.createParsedCommandShowSearch(inputArgs);
			}
		}
	}

	static boolean containsOnlyTaskId(String inputArgs) {
		return removeRegexPatternFromString(inputArgs, StringParser.TASK_ID_REGEX).trim().equals("");
	}

	static ParsedCommand createParsedCommandShowSearch(String inputArgs) {
		ParsedCommand pc;
		try {
			String parsedKeywords = getKeywordsFromString(inputArgs);
			Calendar[] parsedTimes = getDatesTimesFromString(inputArgs);
			parsedTimes = convertToSearchTimes(parsedTimes, inputArgs);
			ArrayList<String> parsedTags = getTagsFromString(inputArgs);
			Boolean parsedStatus = getTaskStatusFromString(inputArgs);
			ParsedCommand.TaskType taskType = getTaskTypeFromString(inputArgs);
			
			pc = new ParsedCommand.Builder(MyParser.CommandType.SEARCH)
								  .searchKeywords(parsedKeywords)
								  .times(parsedTimes)
								  .tags(parsedTags)
								  .isCompleted(parsedStatus)
								  .taskType(taskType)
								  .build();
			return pc;
		
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}

	private static Calendar[] convertToSearchTimes(Calendar[] parsedTimes, String input) {
		if (parsedTimes == null) {
			return null;
		}
		if (parsedTimes[INDEX_FOR_START] != null && parsedTimes[INDEX_FOR_END] == null) {
			if (TimeParser.hasTime(input)) { // user input start date and time only
				Calendar endTime = (Calendar) parsedTimes[INDEX_FOR_START].clone();
				endTime.set(Calendar.HOUR_OF_DAY, 23);
				endTime.set(Calendar.MINUTE, 59);
				parsedTimes[INDEX_FOR_END] = endTime;
			} else { // user input start date only
				Calendar startTime = (Calendar) parsedTimes[INDEX_FOR_START].clone();
				startTime.set(Calendar.HOUR_OF_DAY, 0);
				startTime.set(Calendar.MINUTE,  0);
				parsedTimes[INDEX_FOR_END] = parsedTimes[INDEX_FOR_START];
				parsedTimes[INDEX_FOR_START] = startTime;
			}
		}
		return parsedTimes;
	}
	
	static ParsedCommand createParsedCommandShowDisplay(int taskId) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.Builder(MyParser.CommandType.DISPLAY)
	  							  .taskId(taskId)
	  							  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}

}
