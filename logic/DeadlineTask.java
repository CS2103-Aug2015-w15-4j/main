package logic;
import java.util.Calendar;

public class DeadlineTask extends Task{
	
	protected Calendar end;

	public DeadlineTask() {
		super();
	}

	public DeadlineTask(userData parsedInput) {
		super();
		this.end = parsedInput.end;
	}
	
	public void createDeadlineTask(DeadlineTask newTask) {
		Storage storage = new Storage();
		storage.add(newTask);
	}

	public Calendar getEnd() {
		return end;
	}

	protected void setEnd(Calendar end) {
		this.end = end;
	}
}
