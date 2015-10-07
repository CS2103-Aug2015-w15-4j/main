package parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedDateTimeParser extends DateTimeParser{
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yy HHmm");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yy");

	private static Pattern ddmmyy = Pattern.compile("(\\s(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d))\\s");
	private static Pattern hhmm = Pattern.compile("@((0[0-9]|1[0-9]|2[0-3])([0-5][0-9]))(-((0[0-9]|1[0-9]|2[0-3])([0-5][0-9])))?");
	
	//private String userInput;
	
	public FormattedDateTimeParser() {
		//this.userInput = input;
	}
	
	/**
	 * This method looks for @HHMM-HHMM substrings of userInput, second time is
	 * optional.
	 *
	 * @param userInput
	 *            Unparsed command arguments input by user.
	 * @return String array where ans[0] = start time (if any) (in HHMM format)
	 *         and ans[1] = end time (if any)(in HHMM format), if no time, value
	 *         is null; end time can only exists if start time exists.
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
	
	// Default time: 2359
	public static Calendar[] convertStringToCalendar(String[] dates,
			String[] times) {
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
				if (times[0] == null) { // no start time, assume deadline 2359h
					//String startDate = dates[0];
					String startDate = dates[0] + " 2359";
					startCal.setTime(dateTimeFormat.parse(startDate));
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

	/**
	 * Returns Calendar array of first 2 dates and times encountered in string.
	 * @param inputArgs String user input.
	 * @return Calendar array with arr[0] = first date and first time, arr[1] = second date and second time, 
	 *         or returns null if dates are invalid
	 */
	@Override
	public Calendar[] getDatesTimes(String inputArgs) {
		// Add whitespace before string for case where string starts with date
		// Regex only detects date surrounded with whitespace
		// Cannot make whitespace optional else cannot remove cases e.g. 32/3/12
		// which are detected as 2/3/12
		String input = " " + inputArgs;
		String[] dates = getFormattedDates(input);
		String[] times = getFormattedTimes(input);
		Calendar[] cal = new Calendar[2];
		cal = convertStringToCalendar(dates, times);
		return cal;
	}
	
	/**
	 * This method looks for dd/mm/yy substrings of userInput, single digit
	 * inputs allowed for date and month.
	 *
	 * @param userInput
	 *            Unparsed command arguments input by user.
	 * @return String array where ans[0] = first date found (if any) and ans[1]
	 *         = second date found (if any), If no dates are found, ans[0] =
	 *         ans[1] = null and if only one date found, ans[1] = null.
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
}
