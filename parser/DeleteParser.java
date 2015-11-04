package parser;

//@@author A0114620X
public class DeleteParser extends InputParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			try {
				String inputArgs = input[INDEX_FOR_ARGS].trim();
				int taskId = getTaskIdFromString(inputArgs);
				ParsedCommand pc = new ParsedCommand.Builder(MyParser.CommandType.DELETE)
													.taskId(taskId)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return InputParser.createParsedCommandError(e.getMessage());
			}
		}
	}
}
