package parser;

import java.util.ArrayList;
import java.util.Calendar;

public class EditParser extends InputParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs[] = input[INDEX_FOR_ARGS].split(" ", 2);
			if (isMissingArguments(inputArgs)) {
				return InputParser.createParsedCommandError(ERROR_MISSING_FIELDS);
			} else {
				try {
					int parsedTaskId = getTaskIdFromString(inputArgs[0]);
					String parsedTitle = getTitleFromString(inputArgs[1]);
					Calendar[] parsedTimes = getDatesTimesFromString(inputArgs[1]);
					String parsedDescription = getDescriptionFromString(inputArgs[1]);
					ArrayList<String> parsedTags = getTagsFromString(inputArgs[1]);
				
					ParsedCommand pc = new ParsedCommand.Builder(MyParser.CommandType.EDIT)
														.taskId(parsedTaskId)
														.title(parsedTitle)
														.times(parsedTimes)
														.description(parsedDescription)
														.tags(parsedTags)
														.build();
					return pc;
				} catch (InvalidArgumentsForParsedCommandException e) {
					return InputParser.createParsedCommandError(e.getMessage());
				}
			}
		}
	}

}
