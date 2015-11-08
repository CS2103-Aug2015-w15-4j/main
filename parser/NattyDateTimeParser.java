//@@author A0114620X

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

import parser.DateTime.DateTimeBuilder;
import parser.DateTime.ParserType;

public class NattyDateTimeParser extends DateTimeParser{
	//private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");

	private static final String DAYS = "(mon(day)?|tue?(sday)?|tues|wed(nesday)?|thur(sday)?|thu|thurs|fri(day)?|sat(urday)?|sun(day)?|tmr|tomorrow|tomorow|today|tdy|ytd|yesterday)s?";
	private static final String RELATIVE_WORDS = "(next|nxt|prev|previous|last|coming|upcoming|before|after)";
	private static final String DURATION = "(day(s?)|month(s?)|year(s?)|yrs)";
	private static final String NUMBERS = "(\\d+\\s(" + DURATION + "|" + DAYS + "))";
	private static final String TO_ALTERNATIVES = "(?<=\\s|^)(until|til|till|-)(?=\\s|$)";
	
	private static final String NATTY_REGEX = "(?<=\\s|^)(" + NUMBERS + "|" + RELATIVE_WORDS + "|" + DURATION + "|" + DAYS + "|to)(?=\\s|$)";
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
		// System.out.println(stdForm);
		return stdForm;
	}
	
	public static Calendar[] parseDateTimeWithNatty(String input) {
		
		Calendar[] parsedDates = new Calendar[3];
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		
		if (groups.isEmpty()) { // failed to parse
			return parsedDates; 
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

    @Override
    protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed) {
    	String input = currentlyParsed.getUnparsedInput();
    	Calendar[] times = new Calendar[2];
		
    	logger.log(Level.FINE, "INPUT TO NATTY: " + input);
    	System.out.println("Natty: " + input);
    	// Times parsed by now, only dates left
    	input = standardiseNattyInput(input);
    	System.out.println("Standardised Natty: " + input);
    	times = parseDateTimeWithNatty(input);
    	System.out.println("Natty parses: [" + times[0] + ", " + times[1] +"]");
    	return currentlyParsed.calDates(times);
    }
}
