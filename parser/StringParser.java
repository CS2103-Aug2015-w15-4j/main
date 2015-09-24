package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class StringParser {
	private static Pattern ddmmyy = Pattern.compile("(\\s(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d))\\s");
	private static Pattern hhmm = Pattern.compile("@((0[0-9]|1[0-9]|2[0-3])([0-5][0-9]))(-((0[0-9]|1[0-9]|2[0-3])([0-5][0-9])))?");
	private static Pattern quotes = Pattern.compile("\"([^\"]*)\"");
	private static Pattern taskId = Pattern.compile("(^[0-9]+)");
	private static Pattern tags = Pattern.compile("#\\w+");
	
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yy HHmm");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yy");

	
	// Note that second date is currently unnecessary, assumption that events do not span across days
	/** 
	 * This method looks for dd/mm/yy substrings of userInput, single digit inputs allowed for date and month.
	 *
	 * @param userInput Unparsed command arguments input by user.
	 * @return String array where ans[0] = first date found (if any) and ans[1] = second date found (if any),
	 * 		   If no dates are found, ans[0] = ans[1] = null and if only one date found, ans[1] = null.
	 */
	public static String[] getFormattedDates(String userInput) {		
		Matcher m = ddmmyy.matcher(userInput); 
		String[] ans = new String[2];
		
		int i = 0;
		
		while (m.find() & i < 2) {
			ans[i] = m.group().trim();
			i++;
		}
		
		return ans;
	}
	
	/** 
	 * This method looks for @HHMM-HHMM substrings of userInput, second time is optional.
	 *
	 * @param userInput Unparsed command arguments input by user.
	 * @return String array where ans[0] = start time (if any) (in HHMM format) and ans[1] = end time 
	 * 		   (if any)(in HHMM format), if no time, value is null; end time can only exists if start time 
	 *         exists.
	 */
	public static String[] getFormattedTimes(String userInput) {
		Matcher m = hhmm.matcher(userInput); 
		String[] ans = new String[2];
		
		if (m.find()) {
			ans[0] = m.group(1);
			ans[1] = m.group(5);
		}
		
		return ans;
	}
	
	// Note this does not distinguish between tasks due at 0000 and tasks with no timing
	public static Calendar[] convertStringToCalendar(String[] dates, String[] times) {
		Calendar[] calTimes = new Calendar[2];
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
				
		try {
			if (dates[0] == null) { // no date and time
				startCal = null;
				endCal = null;
			} else {
				dateFormat.setLenient(false);
				dateTimeFormat.setLenient(false);
				if (times[0] == null) { // no start time
					String startDate = dates[0];
					startCal.setTime(dateFormat.parse(startDate));
				} else { // has start date and time
					String startDateTime = dates[0] + " " + times[0];
					startCal.setTime(dateTimeFormat.parse(startDateTime));
				}

				if (times[1] != null) { // has end time
					String endDateTime = dates[0] + " " + times[1];
					endCal.setTime(dateTimeFormat.parse(endDateTime));
				} else {
					endCal = null;
				}
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return null; // failed to parse - should never be reached?
		}
		
		calTimes[0] = startCal;
		calTimes[1] = endCal;
		
		return calTimes;
	}
	
	
	// For reference only, not in use
	public static Calendar parseNattyDateTime(String date) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
			System.out.println("Failed to parse date");
			return null;
		}
		return cal;
	}
	
	// For reference only, not in use
	public static String parseDateTimeWithNatty(String input) {
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse("the day before next wednesday 12pm");
		String parsedDateTime = "";
		
		List<Date> dates = groups.get(0).getDates();
		parsedDateTime = dates.get(0).toString();
		
		System.out.println(parsedDateTime);
		return parsedDateTime;
	}
	
	public static Date parseStringToDate(String input) {
		Date date = new Date();
		try {
		    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		    date = format.parse(input);
		    return date;
		} catch(ParseException | java.text.ParseException pe) {
		    throw new IllegalArgumentException();
		}
	}

	public static String getTitleFromString(String inputArgs) {
		String regex = "((\\s*(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d))\\s*)|(@((0[0-9]|1[0-9]|2[0-3])([0-5][0-9]))(-((0[0-9]|1[0-9]|2[0-3])([0-5][0-9])))?)|(\"[^\"]*?\")|(#(\\w+))";
		inputArgs = inputArgs.replaceAll(regex, ""); 
		return inputArgs.trim();
	}

	public static Calendar[] getDatesTimesFromString(String inputArgs) {
		// Add whitespace before string for case where string starts with date
		// Regex only detects date surrounded with whitespace
		// Cannot make whitespace optional else cannot remove cases e.g. 32/3/12 which are detected as 2/3/12
		String input = " " + inputArgs; 
		String[] dates = getFormattedDates(input);
		String[] times = getFormattedTimes(input);
		Calendar[] cal = new Calendar[2];
		cal = convertStringToCalendar(dates, times);
		return cal;
	}

	public static String getDescriptionFromString(String inputArgs) {
		Matcher m = quotes.matcher(inputArgs); 
		String description = null;
		
		if (m.find()) {
			description = m.group();
		}
		
		if (description != null) {
			return description.substring(1, description.length()-1);
		} else {
			return null;
		}
	}

	public static int getTaskIdFromString(String inputArgs) {
		Matcher m = taskId.matcher(inputArgs); 
		int taskId = 0;
		
		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}
		
		return taskId;
	}

	public static ArrayList<String> getTagsFromString(String inputArgs) {
		Matcher m = tags.matcher(inputArgs);
		ArrayList<String> tags = new ArrayList<String>();
		
		while (m.find()) {
			String tag = m.group().substring(1);
			tags.add(tag);
		}
		
		return tags;
	}
}
