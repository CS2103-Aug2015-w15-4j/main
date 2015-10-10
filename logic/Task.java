package logic;

import java.util.ArrayList;

import parser.ParsedCommand;

public class Task implements Comparable<Task> {

	private String name;
	private String details;
	private int id;
	private boolean isCompleted;
	private ArrayList<String> tags;
	private int taskType;

	public Task() {
		name = details = "";
		id = 0;
		isCompleted = false;
		tags = null;
	}
	
	public Task(String name, String details,int id,boolean isCompleted,ArrayList<String> tags,int taskType) {
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
/*
	public Task getTask(ParsedCommand parsedInput) {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		return taskList.get(id);
	}
	*/
	
	public ArrayList<String[][]> getTaskDetails() {
		ArrayList<String[][]> task = new ArrayList<String[][]>();
		String[][] array = new String[6][2];
		String tagList = "";
		
		array[0][0] = "name";
		array[0][1] = name;
		array[1][0] = "ID";
		array[1][1] = "" + id;
		array[2][0] = "Details";
		array[2][1] = details;
		array[4][0] = "tags";
		for (int i = 0; i<tags.size(); i++) {
			tagList += tags.get(i) + "\n";
		}
		array[4][1] = tagList;
		
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
	
	public int getTaskType() {
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
	
	public void setTaskType(int taskType) {
		this.taskType = taskType;	
	}
	
	@Override
	public int compareTo(Task o) {
		return ((Integer)id).compareTo(o.getId());
	}


}
