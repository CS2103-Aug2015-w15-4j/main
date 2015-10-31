package parser;

import java.util.HashMap;

public class MyParser {

	public enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR, UNDO, FLAG, DONE, TODO, HELP, INVALID, CONFIG_DATA, CONFIG_IMG, 
		EXIT, CONFIG, SEARCH, SHOW, 
		GUI_OPEN, GUI_CLOSE, GUI_PIN, GUI_UNPIN, GUI_SHOW, GUI_SWITCH, GUI_LOG, GUI_MAIN, GUI_OPEN_ALL, GUI_CLOSE_ALL;
	}

	static final int INDEX_FOR_CMD = 0;
	static final int INDEX_FOR_ARGS = 1;
	private static HashMap<String, MyParser.CommandType> _commandChoicesHashMap;

	
	public static class Pair {
		public String[] str;
		public MyParser.CommandType commandType;
		Pair(MyParser.CommandType _commandType, String[] stringChoices) {
			str = stringChoices;
			commandType = _commandType;
		}
		Pair() {
			str = null;
			commandType = MyParser.CommandType.INVALID;			
		}
	}

	public static final Pair[] COMMAND_CHOICES = {
		new Pair(MyParser.CommandType.ADD, new String[] {"add", "insert", "+"}),
		new Pair(MyParser.CommandType.DELETE, new String[] {"delete", "del", "remove", "cancel", "x"}),
		new Pair(MyParser.CommandType.EDIT, new String[] {"edit", "change"}),
		new Pair(MyParser.CommandType.SHOW, new String[] {"show", "search", "find"}),
		new Pair(MyParser.CommandType.EXIT, new String[] {"exit"}),
		new Pair(MyParser.CommandType.UNDO, new String[] {"undo"}),
		new Pair(MyParser.CommandType.DONE, new String[] {"done", "finished", "completed", "v"}),
		new Pair(MyParser.CommandType.FLAG, new String[] {"flag", "mark"}),
		new Pair(MyParser.CommandType.TODO, new String[] {"todo"}),
		new Pair(MyParser.CommandType.CONFIG, new String[] {"set"}),
		new Pair(MyParser.CommandType.HELP, new String[] {"help", "?"}),
		new Pair(MyParser.CommandType.GUI_OPEN, new String[] {"open"}),
		new Pair(MyParser.CommandType.GUI_CLOSE, new String[] {"close"}),
		new Pair(MyParser.CommandType.GUI_PIN, new String[] {"pin"}),
		new Pair(MyParser.CommandType.GUI_SWITCH, new String[] {"switch"}),
		new Pair(MyParser.CommandType.GUI_LOG, new String[] {"log"}),
		new Pair(MyParser.CommandType.GUI_MAIN, new String[] {"main"}),
		new Pair(MyParser.CommandType.GUI_UNPIN, new String[] {"unpin"})
	};

	static {
		setupCommandChoicesHashMap();		
	}
	
	
	/**
	 * Parses user input into ParsedCommand attributes.
	 * @param userInput Entire string input by user.
	 * @return ParsedCommand object, with type error if userInput is invalid.
	 */
	public static ParsedCommand parseCommand(String userInput) {
		if (userInput == null || userInput.trim().length() == 0) {
			return InputParser.createParsedCommandError(InputParser.ERROR_NO_INPUT);
		} else {
			userInput = userInput.trim().replaceAll("\\s+", " ");
			String input[] = userInput.trim().split(" ", 2);
			String userCommand = input[INDEX_FOR_CMD];
			MyParser.CommandType command = MyParser.getStandardCommandType(userCommand.toLowerCase());
			
			InputParser ip;
			
			switch (command) {
				case ADD:
					ip = new AddParser();
					return ip.parse(input);
					
				case DELETE:
					ip = new DeleteParser();
					return ip.parse(input);
					
				case EDIT:
					ip = new EditParser();
					return ip.parse(input);
					
				case SHOW: 
					ip = new ShowParser();
					return ip.parse(input);
					
				case UNDO:
					return InputParser.createParsedCommand(MyParser.CommandType.UNDO);
	
				case FLAG:
					ip = new FlagParser();
					return ip.parse(input);
					
				case DONE:
					ip = new DoneParser();
					return ip.parse(input);
	
				case TODO:
					ip = new TodoParser();
					return ip.parse(input);
					
				case GUI_OPEN:
					ip = new OpenParser();
					return ip.parse(input);
					
				case GUI_CLOSE:
					ip = new CloseParser();
					return ip.parse(input);
					
				case GUI_PIN:
					ip = new PinParser();
					return ip.parse(input);
					
				case GUI_SWITCH:
					return InputParser.createParsedCommand(CommandType.GUI_SWITCH);
					
				case GUI_LOG:
					return InputParser.createParsedCommand(CommandType.GUI_LOG);
					
				case GUI_MAIN:
					return InputParser.createParsedCommand(CommandType.GUI_MAIN);
				
				case GUI_UNPIN:
					return InputParser.createParsedCommand(CommandType.GUI_UNPIN);
					
				case INVALID:
					return InputParser.createParsedCommandError(InputParser.ERROR_INVALID_COMMAND);
			
				case CONFIG:
					ip = new ConfigParser();
					return ip.parse(input);
					
				case HELP:
					return InputParser.createParsedCommand(CommandType.HELP);
				
				case EXIT:
					return InputParser.createParsedCommand(CommandType.EXIT);
	
				default:
					// is never visited
					throw new Error("ERROR");
			}
		}
	}


	private static void setupCommandChoicesHashMap() {
		_commandChoicesHashMap = new HashMap<String, CommandType>();
		for (int i = 0; i < COMMAND_CHOICES.length; i++) {
		    String[] cmdChoiceList = COMMAND_CHOICES[i].str;
		    CommandType cmd = COMMAND_CHOICES[i].commandType;
		    for (int j = 0; j < cmdChoiceList.length; j++) {
		    	_commandChoicesHashMap.put(cmdChoiceList[j], cmd);
		    }
		}
	}


	static CommandType getStandardCommandType(String input) {
		CommandType cmd = _commandChoicesHashMap.get(input);
		if (cmd != null) {
			return cmd;
		} else {
			return CommandType.INVALID;
		}
	}

	
}
