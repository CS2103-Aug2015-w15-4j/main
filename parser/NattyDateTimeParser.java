package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyDateTimeParser extends DateTimeParser{
	//private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");

	private String unparsedInput;
	private String[] nattyTimes;
	private String[] nattyDates;
	
	// For reference only, not in use
    /*public static String parseDateTimeWithNatty(String input) {
		Parser parser = new Parser();
		//List<DateGroup> groups = parser.parse("the day before next wednesday 12pm");
		List<DateGroup> groups = parser.parse("tmr 2pm to 3pm"); // returns empty list if parse fails
		System.out.println(groups);
		String parsedDateTime1 = "";
		String parsedDateTime2 = "";
		List<Date> dates = groups.get(0).getDates();
		parsedDateTime1 = dates.get(0).toString();
		System.out.println(parsedDateTime1);
		//List<Date> dates2 = groups.get(1).getDates();
		parsedDateTime2 = dates.get(1).toString();
		System.out.println(parsedDateTime2);
		return parsedDateTime1;
	}*/
	
	public static Calendar[] parseDateTimeWithNatty(String input) {
		Calendar[] parsedDates = new Calendar[2];
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		
		if (groups.isEmpty()) { // failed to parse
			/*****************CHECK VALIDITY OF TIME MISSING***********************/
			return parsedDates; // assume no dates. should never see this? forgot to check validity of time!
		}
		
		/**********NOTE NO NATTY SUPPORT FOR EVENTS!!!*************************/
		List<Date> dates = groups.get(0).getDates();
		parsedDates[0] = convertDateToCalendar(dates.get(0));
		if (dates.size() < 2) {
			parsedDates[1] = null; // no end time
		} else {
			parsedDates[1] = convertDateToCalendar(dates.get(1));
		}
		return parsedDates;
	}

    private static Calendar convertDateToCalendar(Date date){ 
    	  Calendar cal = Calendar.getInstance();
    	  cal.setTime(date);
    	  return cal;
    }

    private static boolean canParseWithNatty(String input) {
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		if (groups.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

    
    @Override
    protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes) {
    	// Times parsed by now, only dates left
    	if (canParseWithNatty(input)) { // can parse without time, ie has date
    		String dateWithTime = input + " " + parsedTimes[0] + " to " + parsedTimes[1];
    		return parseDateTimeWithNatty(dateWithTime);
    	} else { // invalid input - assume no date/time ie is floating
    		Calendar[] times = new Calendar[2];
    		times[0] = null;
    		times[1] = null;
    		return times;
    	}
    }

	@Override
	protected String getUnparsedInput() {
		return unparsedInput;
	}

	@Override
	protected String[] getParsedTimes() {
		return nattyTimes;
	}

	@Override
	protected String[] getParsedDates() {
		return nattyDates;
	}

}
