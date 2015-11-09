//@@author A0114620X

package parser;

public class TodoParser extends FlagParser {
	
	protected ParsedCommand parse(String[] input) {
		return createParsedCommandTodo(input);
	}
}
