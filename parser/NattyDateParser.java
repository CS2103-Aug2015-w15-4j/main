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

public class NattyDateParser extends DateTimeParser{
	//private static Pattern parantheses = Pattern.compile("([(]([a-zA-Z0-9.\\s]+)[)])");

	private static final String DAYS = "(mon(day)?|tue?(sday)?|tues|wed(nesday)?|thur(sday)?|thu|thurs|fri(day)?|sat(urday)?|sun(day)?|tmr|tomorrow|tomorow|today|tdy|ytd|yesterday)s?";
	private static final String RELATIVE_WORDS = "(next|nxt|prev|previous|last|coming|upcoming|before|after)";
	private static final String DURATION = "(day(s?)|month(s?)|year(s?)|yrs)";
	private static final String NUMBERS = "(\\d+\\s(" + DURATION + "|" + DAYS + "))";
	private static final String TO_ALTERNATIVES = "(?<=\\s|^)(until|til|till|-)(?=\\s|$)";
	
	private static final String NATTY_REGEX = "(?<=\\s|^)(" + NUMBERS + "|" + RELATIVE_WORDS + "|" + DURATION + "|" + DAYS + "|to)(?=\\s|$)";
	private static final Pattern NATTY_DATES = Pattern.compile(NATTY_REGEX);
	
	private static final Logger logger = Logger.getLogger(NattyDateParser.class.getName() );
	private static final int INDEX_FOR_END = 1;
	
	@Override
    protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed) {
    	String input = currentlyParsed.getUnparsedInput();
    	Calendar[] dates = new Calendar[2];
    	logger.log(Level.FINE, "Input to Natty: " + input);
    	input = standardiseNattyInput(input);
    	logger.log(Level.FINE,  "Standardised Natty: " + input);
    	dates = parseDateWithNatty(input);
    	return currentlyParsed.calDates(dates);
    }
	
	public static String standardiseNattyInput(String input) {
		input = input.replaceAll(TO_ALTERNATIVES, "to");
		input = input.toLowerCase();
		Matcher m = NATTY_DATES.matcher(input);
		String stdForm = "";
		while (m.find()) {
			stdForm = stdForm + m.group() + " ";
		}
		return stdForm;
	}
	
	public static Calendar[] parseDateWithNatty(String input) {
		
		Calendar[] parsedDates = new Calendar[3];
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input); // returns empty list if parse fails
		
		if (groups.isEmpty()) { 
			return parsedDates; 
		}
		
		List<Date> dates = groups.get(0).getDates();
		parsedDates[0] = convertDateToCalendar(dates.get(0));
		if (dates.size() < 2) {
			parsedDates[INDEX_FOR_END] = null; // no end time
		} else {
			parsedDates[INDEX_FOR_END] = convertDateToCalendar(dates.get(1));
		}
		return parsedDates;
	}

    private static Calendar convertDateToCalendar(Date date){ 
    	  Calendar cal = Calendar.getInstance();
    	  cal.setTime(date);
    	  return cal;
    }

    
}
