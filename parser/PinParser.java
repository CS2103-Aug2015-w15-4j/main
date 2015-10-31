package parser;

import parser.MyParser.CommandType;

public class PinParser extends GuiParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return InputParser.createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String tabToOpen = input[INDEX_FOR_ARGS];
			return createParsedCommandGuiTabAction(CommandType.GUI_PIN, tabToOpen);			
		}
	}

}
