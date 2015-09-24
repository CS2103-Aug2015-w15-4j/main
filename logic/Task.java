package logic;
import java.util.ArrayList;
import java.util.List;

import parser.ParsedCommand;

public class Task implements Comparable<Task>{

	protected String name;
	protected String details;
	protected int id;
	protected boolean isCompleted;
	protected ArrayList<String> tags;
	
	public Task() {
		
	}

	public Task(ParsedCommand parsedInput) {
		this.name = parsedInput.getTitle();
		this.details = parsedInput.getDescription();
		this.id = parsedInput.getTaskId();
		this.isCompleted = false;
		this.tags = parsedInput.getTags();
	}
	
	
	public void createTask(Task newTask) {
		Storage storage = new Storage();
		storage.add(newTask);
	}
	
	public void deleteTask(int id) {
		Storage storage = new Storage();
		storage.delete(id);
	}
	
	public Task getTask(ParsedCommand parsedInput) {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		return taskList.get(id);		
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

	protected void setName(String name) {
		this.name = name;
	}
	
	protected void setDetails(String details) {
		this.details = details;
	}
	
	protected void setId(int id) {
		this.id = id;
	}
	
	protected void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	protected void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	// Returns 0 if equal to and -1 otherwise
	@Override
	public int compareTo(Task o) {
		if (id == o.getId()) {
			return 0;
		} else {
			return -1;
		}
	}

	
}
