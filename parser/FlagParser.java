//@@author A0114620X

package parser;

public class FlagParser extends InputParser {

	protected ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String[] subInput = input[INDEX_FOR_ARGS].split(" ");
			String subCommand = subInput[INDEX_FOR_SUBCMD];
			if (subCommand.equalsIgnoreCase("todo")) {
				return createParsedCommandTodo(subInput);
			} else if (subCommand.equalsIgnoreCase("done") || subCommand.equalsIgnoreCase("completed")) {
				return createParsedCommandDone(subInput);
			} else {
				return createParsedCommandError(InputParser.ERROR_INVALID_COMMAND);
			}
		}
	}

	private static ParsedCommand createParsedCommandFlagTaskStatus(String[] input, Boolean status) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String inputArgs = input[INDEX_FOR_ARGS].trim();
			try {
				int taskId = getTaskIdFromString(inputArgs);
				ParsedCommand pc = new ParsedCommand.ParsedCommandBuilder(MyParser.CommandType.FLAG)
													.taskId(taskId)
													.isCompleted(status)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return InputParser.createParsedCommandError(e.getMessage());
			}
		}
	}

	static ParsedCommand createParsedCommandDone(String[] input) {
		return createParsedCommandFlagTaskStatus(input, true);
	}

	static ParsedCommand createParsedCommandTodo(String[] input) {
		return createParsedCommandFlagTaskStatus(input, false);
	}

}
