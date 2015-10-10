package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyDateTimeParser extends DateTimeParser{
	private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");
	
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
		if (groups.isEmpty()) {
			return null;
		}
		List<Date> dates = groups.get(0).getDates();
		parsedDates[0] = convertDateToCalendar(dates.get(0));
		if (dates.size() < 2) {
			parsedDates[1] = null; // no end date/time
		} else {
			parsedDates[1] = convertDateToCalendar(dates.get(1));
		}
		return parsedDates;
	}

    public static String getNattyFromString(String inputArgs) {
		Matcher m = parantheses.matcher(inputArgs);
		String nattyDate = null;

		if (m.find()) {
			nattyDate = m.group();
		}
		
		assert(nattyDate != null);
		
		if (nattyDate != null) {
			return nattyDate.substring(1, nattyDate.length() - 1);
		} else {
			return null; // should never be returned
		}
	}

    public static Calendar convertDateToCalendar(Date date){ 
    	  Calendar cal = Calendar.getInstance();
    	  cal.setTime(date);
    	  return cal;
    }
    /*
	@Override
	Calendar[] getDatesTimes(String input) {
		return parseDateTimeWithNatty(getNattyFromString(input));
	}*/

}
