//@@author A0114620X
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
					String task = inputArgs[0];
					String changes = inputArgs[1];
					int parsedTaskId = getTaskIdFromString(task);
					String parsedTitle = getTitleWithDateKeywords(changes);
					Calendar[] parsedTimes = getStandardDatesTimes(changes);
					if (mustRemoveDateKeywordSection(parsedTimes)) { // date keyword used for date input
						parsedTitle = removeDateKeywordSection(parsedTitle);
					}
					String parsedDescription = getDescriptionFromString(changes);
					ArrayList<String> parsedTags = getTagsFromString(changes);
				
					ParsedCommand pc = new ParsedCommand.ParsedCommandBuilder(MyParser.CommandType.EDIT)
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
