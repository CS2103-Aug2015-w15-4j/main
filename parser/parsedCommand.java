package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class parsedCommand {
	private enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, ERROR;
	}
	
	private String input;
	private CommandType cmdType;
	private String title;
	private Calendar start;
	private Calendar end;
	private String description;
	private ArrayList<String> tags;
	private String errorMessage;
	private int taskId;
	
	private static final String ERROR_NO_INPUT = "Error: No user input";
	
	public parsedCommand(String userInput) {
		if (userInput.length() == 0) {
			setError(ERROR_NO_INPUT);
		}
	}

	private void setError(String errorMsg) {
		this.cmdType = CommandType.ERROR;
		this.errorMessage = errorMsg;
	}
	
	public CommandType getCommandType() {
		return this.cmdType;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Calendar getStart() {
		return this.start;
	}
	
	public Calendar getEnd() {
		return this.end;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public int getTaskId() {
		return this.taskId;
	}
}
