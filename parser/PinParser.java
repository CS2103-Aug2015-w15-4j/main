//@@author A0114620X

package parser;

import parser.MyParser.CommandType;

public class PinParser extends GuiParser {

	protected ParsedCommand parse(String[] input) {
		return createParsedCommandGuiTabAction(CommandType.GUI_PIN, input);			
	}
	
}
