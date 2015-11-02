package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

public class Event extends Task {

	protected Calendar start;
	protected Calendar end;

	public Event(Task task, Calendar start, Calendar end) {
		super(task.getName(), task.getDetails(), task.getId(), task.getIsCompleted(), task.getTags(), TaskType.EVENT);
		this.start = start;
		this.end = end;
	}

	public Event(DeadlineTask dlTask, Calendar start) {
		super(dlTask.getName(), dlTask.getDetails(), dlTask.getId(), dlTask.getIsCompleted(), dlTask.getTags(), TaskType.EVENT);
		this.start = start;
		this.end = dlTask.getEnd();
	}
	
	public Event(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,
			TaskType taskType, Calendar start, Calendar end) {
		super(name, details,id,isCompleted,tags,taskType);
		setStart(start);
		setEnd(end);
	}
	
	public Event(Event newTask) {
		super(newTask.getName(),newTask.getDetails(),newTask.getId(),newTask.getIsCompleted(),newTask.getTags(),newTask.getTaskType());
		this.start = newTask.getStart();
		this.end = newTask.getEnd();
	}

	public Event(ParsedCommand parsedInput) {
		super(parsedInput);
		this.start = parsedInput.getFirstDate();
		this.end = parsedInput.getSecondDate();
	}
	
	public ArrayList<String[]> getTaskDetails() {
		ArrayList<String[]> task = super.getTaskDetails();
		String[] array = new String[2];
		
		array[0] = "start";
		array[1] = start.getTime().toString();
		task.add(array);
		array = new String[2];
		array[0] = "end";
		array[1] = end.getTime().toString();
		task.add(array);
		
		return task;
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {return end;}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
}
