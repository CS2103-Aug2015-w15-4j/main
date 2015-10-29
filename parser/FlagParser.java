package parser;

public class FlagParser extends InputParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String[] subInput = input[1].split(" ");
			String subCommand = subInput[0];
			if (subCommand.equalsIgnoreCase("todo")) {
				return createParsedCommandFlagTodo(subInput);
			} else if (subCommand.equalsIgnoreCase("done") || subCommand.equalsIgnoreCase("completed")) {
				return createParsedCommandFlagDone(subInput);
			} else {
				return createParsedCommandError(InputParser.ERROR_INVALID_COMMAND);
			}
		}
	}

	static ParsedCommand createParsedCommandFlagTaskStatus(String[] input, Boolean status) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			try {
				int taskId = getTaskIdFromString(inputArgs);
				ParsedCommand pc = new ParsedCommand.Builder(MyParser.CommandType.FLAG)
													.taskId(taskId)
													.isCompleted(status)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return InputParser.createParsedCommandError(e.getMessage());
			}
		}
	}

	static ParsedCommand createParsedCommandFlagDone(String[] input) {
		return createParsedCommandFlagTaskStatus(input, true);
	}

	static ParsedCommand createParsedCommandFlagTodo(String[] input) {
		return createParsedCommandFlagTaskStatus(input, false);
	}

}
