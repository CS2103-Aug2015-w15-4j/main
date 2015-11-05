package logic;

import java.util.ArrayList;
import java.util.Comparator;

import org.apache.lucene.queryparser.classic.ParseException;
import parser.ParsedCommand;

//@author A0124777W
public class Task implements Comparable<Task> {

	// Task Type Enums
	private static ParsedCommand.TaskType DEADLINE_TASK = ParsedCommand.TaskType.DEADLINE_TASK;
	private static ParsedCommand.TaskType EVENT = ParsedCommand.TaskType.EVENT;

	// Comparator constants
	public static final int LESSER = -1;
	public static final int GREATER = 1;
	public static final int NO_DATE = 999;

	// Class fields
	private String name;
	private String details;
	private int id;
	private boolean isCompleted;
	private ArrayList<String> tags;
	private ParsedCommand.TaskType taskType;

	public Task() {
		name = details = "";
		id = 0;
		isCompleted = false;
		tags = null;
	}

	public Task(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,ParsedCommand.TaskType taskType) {
		setName(name);
		setDetails(details);
		setId(id);
		setIsCompleted(isCompleted);
		setTags(tags);
		setTaskType(taskType);
	}

	public Task(Task newTask) {
		this.name = newTask.getName();
		this.details = newTask.getDetails();
		this.id = newTask.getId();
		this.isCompleted = newTask.getIsCompleted();
		this.tags = newTask.getTags();
		this.taskType = newTask.getTaskType();
	}

	public Task(ParsedCommand parsedInput) {
		this.name = parsedInput.getTitle();
		this.details = parsedInput.getDescription();
		this.id = parsedInput.getTaskId();
		this.isCompleted = false;
		this.tags = parsedInput.getTags();
		this.taskType = parsedInput.getTaskType();
	}

	public ArrayList<String[]> getTaskDetails() {
		ArrayList<String[]> task = new ArrayList<String[]>();
		String[] array = new String[2];
		String tagList = "";

		array[0] = "name";
		array[1] = name;
		task.add(array);
		array = new String[2];
		array[0] = "ID";
		array[1] = "" + id;
		task.add(array);
		array = new String[2];
		array[0] = "Type";
		array[1] = taskType.toString();
		task.add(array);
		array = new String[2];
		array[0] = "Details";
		array[1] = details;

		task.add(array);
		array = new String[2];
		array[0] = "Tags";
		if(tags != null) {	
			for (int i = 0; i<tags.size(); i++) {
				tagList += "[" + tags.get(i) + "]";
			}
		}
		array[1] = tagList;
		task.add(array);

		return task;	
	}

	public String getName() {
		return name;
	}

	public String getDetails() {
		return details;
	}

	public int getId() {
		return id;
	}

	public boolean getIsCompleted() {
		return isCompleted;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public ParsedCommand.TaskType getTaskType() {
		return taskType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public void setTaskType(ParsedCommand.TaskType taskType) {
		this.taskType = taskType;	
	}

	@Override
	public int compareTo(Task o) {
		return ((Integer)id).compareTo(o.getId());
	}


	/*
	 * 	Comparator to sort by date. Sorts by end date if Task is an event.
	 */
	public static final Comparator<Task> compareByDate = new Comparator<Task>() {

		@Override
		public int compare(Task task1, Task task2) {

			if(task1.getTaskType() == DEADLINE_TASK && task2.getTaskType() == DEADLINE_TASK) {
				DeadlineTask dlTask1 = (DeadlineTask) task1;
				DeadlineTask dlTask2 = (DeadlineTask) task2;

				if (dlTask1.getEnd().before(dlTask2.getEnd())) {
					return LESSER;
				} else {
					return GREATER;
				}
			} else if (task1.getTaskType() == EVENT && task2.getTaskType() == EVENT) {
				Event event1 = (Event) task1;
				Event event2 = (Event) task2;

				if (event1.getEnd().before(event2.getEnd())) {
					return LESSER;
				} else {
					return GREATER;
				}
			} else if (task1.getTaskType() == EVENT && task2.getTaskType() == DEADLINE_TASK) {
				Event event1 = (Event) task1;
				DeadlineTask dlTask2 = (DeadlineTask) task2;

				if (event1.getEnd().before(dlTask2.getEnd())) {
					return LESSER;
				} else {
					return GREATER;
				}
			} else if (task1.getTaskType() == DEADLINE_TASK && task2.getTaskType() == EVENT) {
				DeadlineTask dlTask1 = (DeadlineTask) task1;
				Event event2 = (Event) task2;

				if (dlTask1.getEnd().before(event2.getEnd())) {
					return LESSER;
				} else {
					return GREATER;
				}
			} else {
				return NO_DATE;
			}
		}
	};

}
