package logic;
import java.sql.Date;
import java.sql.Time;


public class Event extends TaskWithDeadlines {

	protected Time startTime;
	protected Date startDate;
	
	public Event() {
		
	}
	
	public Event(userData parsedInput) {
		super();
		this.startTime = parsedInput.startTime;
		this.startDate = parsedInput.startDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	protected void setStartTime(Time time) {
		this.startTime = time;
	}

	protected void setStartDate(Date date) {
		this.startDate = date;
	}
}
