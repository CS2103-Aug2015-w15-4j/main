//@@author A0114620X

package parser;

public class DoneParser extends FlagParser {
	ParsedCommand parse(String[] input) {
		return createParsedCommandDone(input);
	}
}
