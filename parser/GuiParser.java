package parser;

import parser.MyParser.CommandType;

public abstract class GuiParser extends InputParser {

	ParsedCommand createParsedCommandGuiTabAction(CommandType cmd, String tab) {
		try {
			return new ParsedCommand.Builder(cmd)
									.guiType(tab)
									.build();
		} catch (InvalidArgumentsForParsedCommandException e) {
			return createParsedCommandError(e.getMessage());
		}
	}
}
