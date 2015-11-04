//@@author A0114620X
package parser;

import parser.MyParser.CommandType;

public class CloseParser extends GuiParser {

	ParsedCommand parse(String[] input) {
		return createParsedCommandGuiTabAction(CommandType.GUI_CLOSE, input);
	}

}
