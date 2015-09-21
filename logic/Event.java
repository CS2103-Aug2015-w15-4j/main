package logic;
import java.util.Calendar;


public class Event extends TaskWithDeadlines {

	protected Calendar start;
	
	public Event() {
		
	}
	
	public Event(userData parsedInput) {
		super();
		this.start = parsedInput.start;
	}

	public Calendar getStart() {
		return start;
	}

	protected void setStart(Calendar start) {
		this.start = start;
	}
}
