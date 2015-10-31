package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyDateTimeParser extends DateTimeParser{
	//private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");

	private String unparsedInput;
	private String[] nattyTimes;
	private String[] nattyDates;
	private static final String DAYS = "(mon(day)?|tue?(sday)?|tues|wed(nesday)?|thur(sday)?|thu|thurs|fri(day)?|sat(urday)?|sun(day)?|tmr|tomorrow|tomorow|today|tdy|ytd|yesterday)s?";
	private static final String RELATIVE_WORDS = "(next|nxt|prev|previous|last|coming|upcoming)";
	private static final String DURATION = "(day(s?)|month(s?)|year(s?)|yrs)";
	private static final String NUMBERS = "(\\d+)(?=\\s" + "(DURATION" + "|" + DAYS + ")" + ")";
	private static final String TO_ALTERNATIVES = "(?<=\\s|^)(until|til|till|-)(?=\\s|$)";
	
	private static final String NATTY_REGEX = "(?<=\\s|^)(" + DAYS + "|" + RELATIVE_WORDS + "|" + DURATION + "|" + NUMBERS + "|to)(?=\\s|$)";
	private static final Pattern NATTY_DATES = Pattern.compile(NATTY_REGEX);
	
	private static final Logger logger = Logger.getLogger(NattyDateTimeParser.class.getName() );
	
	public static String standardiseNattyInput(String input) {
		input = input.replaceAll(TO_ALTERNATIVES, "to");
		input = input.toLowerCase();
		Matcher m = NATTY_DATES.matcher(input);
		String stdForm = "";
		while (m.find()) {
			stdForm = stdForm + m.group() + " ";
		}
		System.out.println(stdForm);
		return stdForm;
	}
	
	public static Calendar[] parseDateTimeWithNatty(String input) {
		
		Calendar[] parsedDates = new Calendar[2];
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		
		if (groups.isEmpty()) { // failed to parse
			return parsedDates; // assume no dates. should never see this? 
		}
		
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
		// System.out.println(input);
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		if (groups.isEmpty()) {
			logger.log(Level.FINE, "Parse " + input + " with Natty fail");
			return false;
		} else {
			logger.log(Level.FINE, "Parse " + input + " with Natty success");
			return true;
		}
	}
    
    @Override
    protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes) {
    	assert(parsedDates != null);
    	assert(parsedTimes != null);
    	logger.log(Level.FINE, "INPUT TO NATTY: " + input);
    	// Times parsed by now, only dates left
    	input = standardiseNattyInput(input);
    	if (canParseWithNatty(input)) { // can parse without time, ie has date
    		String[] startAndEndDates = input.split(" to | until | til | till | - ");
    		String dateWithTime;
    		if (startAndEndDates.length < 2) { // only has 1 Natty date
    			if (parsedTimes[0] == null) {
    	    		parsedTimes[0] = "23:59";
    	    		if (parsedTimes[1] == null) {
    	    			parsedTimes[1] = "";
    	    		}
    	    	} else if (parsedTimes[1] == null) {
    	    		parsedTimes[1] = "";
    	    	}
    			dateWithTime = input + " " + parsedTimes[0] + " to " + parsedTimes[1];    			
    		} else {
    			if (parsedTimes[0] == null) {
    	    		parsedTimes[0] = "23:59";
    	    		if (parsedTimes[1] == null) {
    	    			parsedTimes[1] = "23:59";
    	    		}
    	    	} else if (parsedTimes[1] == null) {
    	    		parsedTimes[1] = "23:59";
    	    	}
    			dateWithTime = startAndEndDates[0] + " " + parsedTimes[0] + " to " + startAndEndDates[1] + " " + parsedTimes[1];  
    		}
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
