package logic;
import java.util.Calendar;

public class userData {
	
	// Task/DeadlineTask/Event details
	public String name;
	public String details;
	public int id;
	public boolean isCompleted;
	public String tags;
	public String type;
	public Calendar start;
	public Calendar end;
	
	//Update details
	public String updateId;
	public String newName;
	public String newDetails;
	public boolean newIsCompleted;
	public String newTags;
	public String newType;
	public Calendar newStart;
	public Calendar newEnd;	
}