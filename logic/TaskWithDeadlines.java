package logic;
import java.sql.Date;
import java.sql.Time;


public class TaskWithDeadlines extends TaskWithoutDeadlines{
	
	protected Time endTime;
	protected Date endDate;

	public TaskWithDeadlines() {
		super();
	}

	public TaskWithDeadlines(userData parsedInput) {
		super();
		this.endTime = parsedInput.endTime;
		this.endDate = parsedInput.endDate;
	}

	public Time getEndTime() {
		return endTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	protected void setEndTime(Time time) {
		this.endTime = time;
	}

	protected void setEndDate(Date date) {
		this.endDate = date;
	}
}
