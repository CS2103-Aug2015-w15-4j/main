//@@author A0114620X

package parser;

import parser.MyParser.CommandType;

public class OpenParser extends GuiParser {

	protected ParsedCommand parse(String[] input) {
		return createParsedCommandGuiTabAction(CommandType.GUI_OPEN, input);
	}
	
}
