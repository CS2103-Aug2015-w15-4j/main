//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public class ShowParser extends InputParser {
	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommand(CommandType.GUI_SHOW);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = getTaskIdFromString(inputArgs);
			if (hasTaskId(taskId)) { 
				return createParsedCommandDisplay(taskId);
			} else { 
				return createParsedCommandSearch(inputArgs);
			}
		}
	}

	private static ParsedCommand createParsedCommandSearch(String inputArgs) {
		ParsedCommand pc;
		try {
			// System.out.println(inputArgs);
			String parsedKeywords = getSearchKeywordsWithDateKeywords(inputArgs);
			logger.log(Level.FINE, "Search keywords: " + parsedKeywords);
			Calendar[] parsedTimes = getSearchDatesTimes(inputArgs);
			if (parsedTimes != null && parsedTimes[0] != null && parsedTimes.length > 2) { // date keyword used for date input
				parsedKeywords = removeDateKeywordSection(parsedKeywords);
			}
			ArrayList<String> parsedTags = getTagsFromString(inputArgs);
			Boolean parsedStatus = getTaskStatusFromString(inputArgs);
			Boolean parsedOverdue = getIsOverdueFromString(inputArgs);
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
