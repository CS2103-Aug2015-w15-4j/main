package parser;

import parser.MyParser.CommandType;

public class OpenParser extends GuiParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String tabToOpen = input[INDEX_FOR_ARGS];
			if (tabToOpen.equalsIgnoreCase("all")) {
				return createParsedCommand(CommandType.GUI_OPEN_ALL);
			} else {
				return createParsedCommandGuiTabAction(CommandType.GUI_OPEN, tabToOpen);
			}			
		}
	}
}
