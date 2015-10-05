package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;

public class DeadlineTask extends Task {

	protected Calendar end;

	public DeadlineTask(String name, String details,int id,boolean isCompleted,ArrayList<String> tags, Calendar end) {
		super(name, details,id,isCompleted,tags);
		setEnd(end);
	}
	
	public DeadlineTask() {
		
	}

	public DeadlineTask(ParsedCommand parsedInput) {
		super();
		this.end = parsedInput.getEnd();
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
}
