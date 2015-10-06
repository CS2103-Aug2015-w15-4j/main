package logic;

import java.util.ArrayList;
import java.util.List;

import parser.ParsedCommand;

public class Task implements Comparable<Task> {

	private String name;
	private String details;
	private int id;
	private boolean isCompleted;
	private ArrayList<String> tags;

	public Task() {
		
	}
	
	public Task(String name, String details,int id,boolean isCompleted,ArrayList<String> tags) {
		setName(name);
		setDetails(details);
		setId(id);
		setIsCompleted(isCompleted);
		setTags(tags);
	}

	public Task(ParsedCommand parsedInput) {
		this.name = parsedInput.getTitle();
		System.out.println(name);
		this.details = parsedInput.getDescription();
		this.id = parsedInput.getTaskId();
		this.isCompleted = false;
		this.tags = parsedInput.getTags();
	}
/*
	public Task getTask(ParsedCommand parsedInput) {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		return taskList.get(id);
	}
	*/

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
