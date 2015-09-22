package logic;
import java.util.List;

public class Task implements Comparable<Task>{

	protected String name;
	protected String details;
	protected int id;
	protected boolean isCompleted;
	protected String tags;
	
	public Task() {
		
	}

	public Task(userData parsedInput) {
		this.name = parsedInput.name;
		this.details = parsedInput.details;
		this.id = parsedInput.id;
		this.isCompleted = parsedInput.isCompleted;
		this.tags = parsedInput.tags;
	}
	
	
	public void createTask(Task newTask) {
		Storage storage = new Storage();
		storage.add(newTask);
	}
	
	public void deleteTask(int id) {
		Storage storage = new Storage();
		storage.delete(id);
	}
	
	public Task getTask(userData parsedInput) {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		return taskList.get(parsedInput.id);		
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

	public String getTags() {
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
	
	protected void setTags(String tags) {
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
