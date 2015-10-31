package parser;

import parser.MyParser.CommandType;

public class OpenParser extends GuiParser {

	ParsedCommand parse(String[] input) {
		return createParsedCommandGuiTabAction(CommandType.GUI_OPEN, input);
	}
}
