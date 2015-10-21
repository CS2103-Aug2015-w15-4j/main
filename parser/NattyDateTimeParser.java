package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyDateTimeParser extends DateTimeParser{
	//private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");

	private String unparsedInput;
	private String[] nattyTimes;
	private String[] nattyDates;
	
	private static final Logger logger = Logger.getLogger(NattyDateTimeParser.class.getName() );

	public static Calendar[] parseDateTimeWithNatty(String input) {
		
		Calendar[] parsedDates = new Calendar[2];
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		
		if (groups.isEmpty()) { // failed to parse
			return parsedDates; // assume no dates. should never see this? 
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
			logger.log(Level.FINE, "Parse " + input + " with Natty fail");
			return false;
		} else {
			logger.log(Level.FINE, "Parse " + input + " with Natty success");
			return true;
		}
	}
    
    private static String convertToStandardNattyForm(String input) {
    	String regex = "(from|to)";
    	input = StringParser.removeRegexPatternFromString(input, regex);
    	return input;
    }

    
    @Override
    protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes) {
    	assert(parsedDates != null);
    	assert(parsedTimes != null);
    	logger.log(Level.FINE, "INPUT TO NATTY: " + input);
    	// Times parsed by now, only dates left
    	if (parsedTimes[0] == null) {
    		parsedTimes[0] = "23:59";
    		if (parsedTimes[1] == null) {
    			parsedTimes[1] = "";
    		}
    	} else if (parsedTimes[1] == null) {
    		parsedTimes[1] = "";
    	}
    	input = convertToStandardNattyForm(input);
    	if (canParseWithNatty(input)) { // can parse without time, ie has date
    		String dateWithTime = input + " " + parsedTimes[0] + " to " + parsedTimes[1];
    		logger.log(Level.FINE, "Parse with Natty: " + dateWithTime);
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
