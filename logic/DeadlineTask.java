package logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

//@author A0124777W
public class DeadlineTask extends Task {

	protected Calendar end;

	public DeadlineTask(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,
			TaskType taskType, Calendar end) {
		super(name, details,id,isCompleted,tags,taskType);
		setEnd(end);
	}
	
	public DeadlineTask(Task task, Calendar end) {
		super(task.getName(), task.getDetails(), task.getId(), task.getIsCompleted(), task.getTags(), TaskType.DEADLINE_TASK);
		this.end = end;
	}
	
	public DeadlineTask(DeadlineTask newTask) {
		super(newTask.getName(),newTask.getDetails(),newTask.getId(),newTask.getIsCompleted(),newTask.getTags(),newTask.getTaskType());
		this.end = newTask.getEnd();
	}
	

	public DeadlineTask(ParsedCommand parsedInput) {
		super(parsedInput);
		this.end = parsedInput.getFirstDate();
	}
	
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
