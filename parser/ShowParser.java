package parser;

import java.util.ArrayList;
import java.util.Calendar;

import parser.MyParser.CommandType;

public class ShowParser extends InputParser {

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
