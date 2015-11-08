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
			System.out.println("taskId: " + taskId);
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
			// System.out.println(inputArgs);
			String parsedKeywords = getSearchKeywordsWithDateKeywordsFromString(inputArgs);
			System.out.println("KEYWORDS: " + parsedKeywords);
			Calendar[] parsedTimes = getSearchDatesTimes(inputArgs);
			if (parsedTimes != null && parsedTimes[0] != null && parsedTimes.length > 2) { // date keyword used for date input
				parsedKeywords = removeKeywordSection(parsedKeywords);
			}
			ArrayList<String> parsedTags = getTagsFromString(inputArgs);
			Boolean parsedStatus = getTaskStatusFromString(inputArgs);
			Boolean parsedOverdue = getOverdueFromString(inputArgs);
			TaskType taskType = getTaskTypeFromString(inputArgs);
			
			pc = new ParsedCommand.ParsedCommandBuilder(CommandType.SEARCH)
								  .searchKeywords(parsedKeywords)
								  .times(parsedTimes)
								  .tags(parsedTags)
								  .isCompleted(parsedStatus)
								  .isOverdue(parsedOverdue)
								  .taskType(taskType)
								  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}
	
	private static ParsedCommand createParsedCommandDisplay(int taskId) {
		ParsedCommand pc;
		try {
			pc = new ParsedCommand.ParsedCommandBuilder(CommandType.DISPLAY)
	  							  .taskId(taskId)
	  							  .build();
			return pc;
		} catch (InvalidArgumentsForParsedCommandException e) {
			return InputParser.createParsedCommandError(e.getMessage());
		}
	}
	
	private static boolean hasTaskId(int taskId) {
		return taskId >= 0;
	}

}
