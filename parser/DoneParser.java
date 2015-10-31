package parser;

public class DoneParser extends FlagParser {
	ParsedCommand parse(String[] input) {
		return createParsedCommandFlagDone(input);
	}
}
