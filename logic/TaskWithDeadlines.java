package logic;
import java.util.Calendar;


public class TaskWithDeadlines extends TaskWithoutDeadlines{
	
	protected Calendar end;

	public TaskWithDeadlines() {
		super();
	}

	public TaskWithDeadlines(UserData parsedInput) {
		super();
		this.end = parsedInput.end;
	}

	public Calendar getEnd() {
		return end;
	}

	protected void setEnd(Calendar end) {
		this.end = end;
	}
}
