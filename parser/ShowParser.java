//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public class ShowParser extends InputParser {
	protected ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommand(CommandType.GUI_SHOW);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			int taskId = getSearchTaskIdFromString(inputArgs);
			if (hasTaskId(taskId)) { 
				return createParsedCommandDisplay(taskId);
			} else { 
				return createParsedCommandSearch(inputArgs);
			}
		}
	}

	private ParsedCommand createParsedCommandSearch(String inputArgs) {
		ParsedCommand pc;
		try {
			String parsedKeywords = getSearchKeywordsWithDateKeywords(inputArgs);
			logger.log(Level.FINE, "Search keywords: " + parsedKeywords);
			Calendar[] parsedTimes = getSearchDatesTimes(inputArgs);
			if (mustRemoveDateKeywordSection(parsedTimes, inputArgs)) { // date keyword used for date input
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
	
	private ParsedCommand createParsedCommandDisplay(int taskId) {
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
