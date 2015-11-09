package logic;

import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

//@@author A0124777W
public class DeadlineTask extends Task {

	protected Calendar end;

	public DeadlineTask(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,
			TaskType taskType, Calendar end) {
		super(name, details,id,isCompleted,tags,taskType);
		setEnd(end);
	}

	/*
	 *	Constructor for testing
	 */
	public DeadlineTask() {

	}

	/*
	 *	Constructor for upgrading a Task into a DeadlineTask
	 */
	public DeadlineTask(Task task, Calendar end) {
		super(task.getName(), task.getDescription(), task.getId(), task.getIsCompleted(), task.getTags(), TaskType.DEADLINE_TASK);
		this.end = end;
	}

	/*
	 *	Constructor used for Cloning Tasks
	 */
	public DeadlineTask(DeadlineTask newTask) {
		super(newTask.getName(),newTask.getDescription(),newTask.getId(),newTask.getIsCompleted(),newTask.getTags(),newTask.getTaskType());
		this.end = newTask.getEnd();
	}


	/*
	 *	Constructor for creating a new DeadlineTask from user input
	 */
	public DeadlineTask(ParsedCommand parsedInput) {
		super(parsedInput);
		this.end = parsedInput.getFirstDate();
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
		ArrayList<String[]> task = super.getTaskDetails();;
		String[] array = new String[2];
		
		array[0] = "End";
		array[1] = Logic.displayDateFormatter.format(end.getTime());
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
