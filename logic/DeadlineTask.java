package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;

public class DeadlineTask extends Task {

	protected Calendar end;

	public DeadlineTask(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,int taskType, Calendar end) {
		super(name, details,id,isCompleted,tags,taskType);
		setEnd(end);
	}
	
	public DeadlineTask() {
		
	}
	

	public DeadlineTask(ParsedCommand parsedInput) {
		super(parsedInput);
		this.end = parsedInput.getFirstDate();
	}
	
	public ArrayList<String[]> getTaskDetails() {
		ArrayList<String[]> task = super.getTaskDetails();;
		String[] array = new String[2];
		
		array[0] = "end";
		array[1] = end.getTime().toString();
		task.add(array);
		
		return task;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
}
