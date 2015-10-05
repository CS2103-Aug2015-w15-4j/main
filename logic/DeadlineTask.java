package logic;
import java.util.Calendar;

import parser.ParsedCommand;

public class DeadlineTask extends Task{
	
	protected Calendar end;

	public DeadlineTask() {
		super();
	}

	public DeadlineTask(ParsedCommand parsedInput) {
		super();
		this.end = parsedInput.getEnd();
	}

	public Calendar getEnd() {
		return end;
	}

	protected void setEnd(Calendar end) {
		this.end = end;
	}
}
