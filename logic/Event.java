package logic;
import java.util.Calendar;

import parser.ParsedCommand;

public class Event extends DeadlineTask {

	protected Calendar start;
	
	public Event() {
		
	}
	
	public Event(ParsedCommand parsedInput) {
		super();
		this.start = parsedInput.getStart();
	}

	public Calendar getStart() {
		return start;
	}

	protected void setStart(Calendar start) {
		this.start = start;
	}
}
