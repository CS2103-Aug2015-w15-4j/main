package parser;

import parser.MyParser.CommandType;

public class PinParser extends GuiParser {

	ParsedCommand parse(String[] input) {
		return createParsedCommandGuiTabAction(CommandType.GUI_PIN, input);			
	}
	
}
