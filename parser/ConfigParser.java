//@@author A0114620X

package parser;

import parser.MyParser.CommandType;
import parser.ParsedCommand.ConfigType;

public class ConfigParser extends InputParser {

	ParsedCommand parse(String[] input) {
		if (isMissingArguments(input)) {
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			String[] subInput = input[INDEX_FOR_ARGS].split(" ", 2);
			String subCommand = subInput[INDEX_FOR_SUBCMD];
			if (subCommand.equalsIgnoreCase("folder")) {
				return createParsedCommandConfigData(subInput);
			} else {
				return createParsedCommandConfigImg(subInput);
			}
		}
	}

	static ParsedCommand createParsedCommandConfigImg(String[] input) {
		ConfigType configType = determineConfigImgType(input[INDEX_FOR_SUBCMD]);
		if (configType != ConfigType.INVALID) {
			if (isMissingArguments(input)) {
				return createParsedCommandError(ERROR_MISSING_ARGS);
			}
			try {
				String fileName = input[INDEX_FOR_SUBARGS];
				ParsedCommand pc = new ParsedCommand.ParsedCommandBuilder(CommandType.CONFIG_IMG)
													.configType(configType)
													.configPath(fileName)
													.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e) {
				return createParsedCommandError(e.getMessage());
			}
		} else {
			return InputParser.createParsedCommandError(InputParser.ERROR_INVALID_COMMAND);
		}
	}

	static ParsedCommand createParsedCommandConfigData(String[] input) {
		if (isMissingArguments(input)) { 
			return createParsedCommandError(ERROR_MISSING_ARGS);
		} else {
			try {
				String fileName = input[INDEX_FOR_SUBARGS];
				ParsedCommand pc = new ParsedCommand.ParsedCommandBuilder(MyParser.CommandType.CONFIG_DATA)
						  			  				.configPath(fileName)
						  			  				.build();
				return pc;
			} catch (InvalidArgumentsForParsedCommandException e){
				return createParsedCommandError(e.getMessage());
			}
		}
	}

	static ParsedCommand.ConfigType determineConfigImgType(String subCommand) {
		if (subCommand.equalsIgnoreCase("background")) {
			return ParsedCommand.ConfigType.BACKGROUND;
		} else if (subCommand.equalsIgnoreCase("avatar")) {
			return ParsedCommand.ConfigType.AVATAR;
		} else {
			return ParsedCommand.ConfigType.INVALID;
		}
	}

}
