package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

//@@author A0124777W
public class Event extends Task {

	protected Calendar start;
	protected Calendar end;

	public Event() {

	}

	/*
	 *	Constructor for upgrading Task to Event
	 */
	public Event(Task task, Calendar start, Calendar end) {
		super(task.getName(), task.getDescription(), task.getId(), task.getIsCompleted(), task.getTags(), TaskType.EVENT);
		this.start = start;
		this.end = end;
	}

	/*
	 *	Constructor for upgrading DeadlineTask to Event
	 */
	public Event(DeadlineTask dlTask, Calendar start) {
		super(dlTask.getName(), dlTask.getDescription(), dlTask.getId(), dlTask.getIsCompleted(), dlTask.getTags(), TaskType.EVENT);
		this.start = start;
		this.end = dlTask.getEnd();
	}


	public Event(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,
			TaskType taskType, Calendar start, Calendar end) {
		super(name, details,id,isCompleted,tags,taskType);
		setStart(start);
		setEnd(end);
	}

	/*
	 *	Constructor used for Cloning Tasks
	 */
	public Event(Event newTask) {
		super(newTask.getName(),newTask.getDescription(),newTask.getId(),newTask.getIsCompleted(),newTask.getTags(),newTask.getTaskType());
		this.start = newTask.getStart();
		this.end = newTask.getEnd();
	}

	/*
	 *	Constructor for creating a new Event from user input
	 */
	public Event(ParsedCommand parsedInput) {
		super(parsedInput);
		this.start = parsedInput.getFirstDate();
		this.end = parsedInput.getSecondDate();
	}

	/*
	 *	Returns all task fields in a String array format for displaying. The String array is formatted such that
	 *	array[0] contains the field name and array[1] contains the field data value. An ArrayList containing the
	 *	String array fields is returned.
	 *
	 *	@return an Arraylist of two field arrays(i.e. array[2]) containing the String data
	 *
	 */
	public ArrayList<String[]> getTaskDetails() {
		ArrayList<String[]> task = super.getTaskDetails();
		String[] array = new String[2];
		
		array[0] = "Start";
		array[1] = Logic.displayDateFormatter.format(start.getTime());
		task.add(array);
		array = new String[2];
		array[0] = "End";
		array[1] = Logic.displayDateFormatter.format(end.getTime());
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
