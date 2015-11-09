//@@author A0114620X

package parser;

import java.util.ArrayList;
import java.util.Calendar;

public class AddParser extends InputParser {

	protected ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			
			try {
				String parsedTitle = getTitleWithDateKeywords(inputArgs);
				Calendar[] parsedTimes = getStandardDatesTimes(inputArgs);
				if (mustRemoveDateKeywordSection(parsedTimes, inputArgs)) { 
					parsedTitle = removeDateKeywordSection(parsedTitle);
				}
				String parsedDescription = getDescriptionFromString(inputArgs);
				ArrayList<String> parsedTags = getTagsFromString(inputArgs);
				
				ParsedCommand pc = new ParsedCommand.ParsedCommandBuilder(MyParser.CommandType.ADD)
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
