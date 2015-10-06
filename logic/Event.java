package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;

public class Event extends DeadlineTask {

	protected Calendar start;

	public Event() {

	}
	
	public Event(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,int taskType, Calendar end, Calendar start) {
		super(name, details,id,isCompleted,tags,taskType,end);
		setStart(start);
	}

	public Event(ParsedCommand parsedInput) {
		super();
		this.start = parsedInput.getStart();
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}
}
