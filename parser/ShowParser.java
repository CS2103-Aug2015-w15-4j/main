//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public class ShowParser extends InputParser {
	private static final String NOT_KEYWORDS_REGEX = NOT_TITLE_REGEX + "|" + TASK_TYPE_REGEX;
	
	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommand(CommandType.GUI_SHOW);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = getTaskIdFromString(inputArgs);
			if (hasTaskId(taskId)) { // show task details
				return createParsedCommandDisplay(taskId);
			} else { // search	
				return createParsedCommandSearch(inputArgs);
			}
		}
	}

	private static ParsedCommand createParsedCommandSearch(String inputArgs) {
		ParsedCommand pc;
		try {
			String parsedKeywords = getSearchKeywordsFromString(inputArgs);
			Calendar[] parsedTimes = getDatesTimesFromString(inputArgs);
			parsedTimes = convertToSearchTimes(parsedTimes, inputArgs);
			ArrayList<String> parsedTags = getTagsFromString(inputArgs);
			Boolean parsedStatus = getTaskStatusFromString(inputArgs);
			TaskType taskType = getTaskTypeFromString(inputArgs);
			
			pc = new ParsedCommand.Builder(CommandType.SEARCH)
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
	
	private static ParsedCommand createParsedCommandDisplay(int taskId) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.Builder(CommandType.DISPLAY)
	  							  .taskId(taskId)
	  							  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}
	
	private static String getSearchKeywordsFromString(String input) {
		return removeRegexPatternFromString(input, NOT_KEYWORDS_REGEX);
	}
	
	private static boolean hasTaskId(int taskId) {
		return taskId >= 0;
	}

}
