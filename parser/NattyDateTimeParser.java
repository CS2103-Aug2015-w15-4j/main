package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyDateTimeParser extends DateTimeParser{
	
	// For reference only, not in use
    public static String parseDateTimeWithNatty(String input) {
		Parser parser = new Parser();
		//List<DateGroup> groups = parser.parse("the day before next wednesday 12pm");
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		System.out.println(groups);
		String parsedDateTime = "";
		List<Date> dates = groups.get(0).getDates();
		parsedDateTime = dates.get(0).toString();
		System.out.println(parsedDateTime);
		return parsedDateTime;
	}

	
	@Override
	Calendar[] getDatesTimes(String input) {
		parseDateTimeWithNatty("");
		return null;
	}

}
