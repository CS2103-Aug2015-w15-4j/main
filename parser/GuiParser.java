//@@author A0114620X

package parser;

import parser.MyParser.CommandType;

public abstract class GuiParser extends InputParser {

	ParsedCommand createParsedCommandGuiTabAction(CommandType cmd, String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String tab = input[INDEX_FOR_ARGS];
			try {
				return new ParsedCommand.ParsedCommandBuilder(cmd)
										.guiType(tab)
										.build();
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		}
	}
}
