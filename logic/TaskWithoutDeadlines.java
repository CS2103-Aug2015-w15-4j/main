package logic;

public class TaskWithoutDeadlines {

	protected String name;
	protected String details;
	protected int id;
	protected boolean isCompleted;
	protected String tags;
	
	public TaskWithoutDeadlines() {
	
	}

	public TaskWithoutDeadlines(UserData parsedInput) {
		this.name = parsedInput.name;
		this.details = parsedInput.details;
		this.id = parsedInput.id;
		this.isCompleted = parsedInput.isCompleted;
		this.tags = parsedInput.tags;
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
	
}
