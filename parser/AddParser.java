package parser;

import java.util.ArrayList;
import java.util.Calendar;

public class AddParser extends InputParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS];
			
			try {
				String parsedTitle = getTitleFromString(inputArgs);
				Calendar[] parsedTimes = getDatesTimesFromString(inputArgs);
				String parsedDescription = getDescriptionFromString(inputArgs);
				ArrayList<String> parsedTags = getTagsFromString(inputArgs);
				
				ParsedCommand pc = new ParsedCommand.Builder(MyParser.CommandType.ADD)
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
