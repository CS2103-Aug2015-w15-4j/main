package parser;

public class TodoParser extends FlagParser {
	ParsedCommand parse(String[] input) {
		return createParsedCommandFlagTodo(input);
	}
}
