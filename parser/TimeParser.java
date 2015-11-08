//@@author A0114620X

package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;

public class TimeParser extends DateTimeParser {
	private static final int INDEX_FOR_END = 1;

	private static final int INDEX_FOR_START = 0;

	private static final int INDEX_FOR_NONTIME_STRING = 2;
	
	static final String TWELVE_HR_REGEX = "(?<=\\s|^)([0-9]?[0-9])([.:][0-9][0-9])?\\s?(am|pm)?(\\s?(?:-|to|until|til|till)\\s?([0-9]?[0-9])([.:][0-9][0-9])?\\s?)?(am|pm)(?=\\s|$)";
	static final String TWENTYFOUR_HR_REGEX = "(?<=\\s|^)(([0-9]?[0-9])[:]([0-9][0-9]))\\s?[?:h|H]?\\s?((?:-|to|until|til|till)?\\s?(([0-9]?[0-9])[:]([0-9][0-9])))?\\s?[?:h|H]?(?=\\s|$)";
		
	static final String TIME_REGEX = "(" + TWELVE_HR_REGEX + "|" + TWENTYFOUR_HR_REGEX + ")";
	
	static final Pattern HHMM = Pattern.compile(TWENTYFOUR_HR_REGEX);
	static final Pattern HMMA = Pattern.compile(TWELVE_HR_REGEX);
	
	static final String TWELVE_HR_FORMAT = "h:mma";
	static final String TWENTY_FOUR_HR_FORMAT = "HH:mm";

	private static final int TIME_H1 = 1;
	private static final int TIME_M1 = 2;
	private static final int TIME_APM1 = 3;
	private static final int TIME_H2 = 5;
	private static final int TIME_M2 = 6;
	private static final int TIME_APM2 = 7;
	
	private static final Logger logger = Logger.getLogger(TimeParser.class.getName() );
	
	@Override
	protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed) {
		String input = currentlyParsed.getUnparsedInput();	
		if (input == null) {
			return null;
		}
		System.out.println("In formatted");
		String[] times = new String[3];
		times = getTwelveHrTimesFromString(input);
		logger.log(Level.FINE, "12HR for " + input + " : " + times[0]);
		System.out.println("12HR for " + input + " : " + times[0] + " " + times[1]);
		if (times[INDEX_FOR_START] == null) {
			times = getTwentyfourHrTimesFromString(input);
			logger.log(Level.FINE, "24HR for " + input + " : " + times[0]);
		} 
		
		times[INDEX_FOR_NONTIME_STRING] = removeTimesFromString(input);
		// System.out.println(times[3]);
		if (isInvalidTimes(times)) {
			times = null;
		}
		currentlyParsed.times(times);
		return currentlyParsed;
	}
	
	private static String[] getTwelveHrTimesFromString(String input) {
		Matcher m = HMMA.matcher(input);
		String[] timeArr = new String[3];
		System.out.println("String " + input);
		int i = 0;
		
		while (m.find() & i < 2) {
			String time1 = m.group(TIME_H1);
			String min1 = m.group(TIME_M1);
			String apm1 = m.group(TIME_APM1);
			String apm2 = m.group(TIME_APM2);
			String h2 = m.group(TIME_H2);
			String m2 = m.group(TIME_M2);
		
			if (min1 != null) {
				time1 = time1 + ":" + min1.substring(1);
			} else {
				time1 = time1 + ":00";
			}
			
			if (apm1 != null) {
				time1 = time1 + apm1;
			} else {
				time1 = time1 + apm2;
			}
			
			timeArr[i] = time1;
		
		    if (i < 1 & (h2 != null)) {
				String time2 = h2;
				if (m2 != null) {
					time2 = time2 + ":" + m2.substring(1) + apm2;
				} else {
					time2 = time2 + ":00" + apm2;
				}
				timeArr[1] = time2;
			}
			/*for (int a = 1; a <= 7; a++) {
				System.out.print(m.group(a) + "; ");
			}
			System.out.println("");*/
			i++;
		}
		timeArr[INDEX_FOR_START] = convertToTwentyFourHr(timeArr[INDEX_FOR_START]);
		timeArr[INDEX_FOR_END] = convertToTwentyFourHr(timeArr[INDEX_FOR_END]);
		return timeArr;
	}
	
	private static String convertToTwentyFourHr(String twelveHrTime) {
		if (twelveHrTime == null) {
			return null;
		}
		SimpleDateFormat twentyFourHrFormat = new SimpleDateFormat("HH:mm");
	    SimpleDateFormat twelveHrFormat = new SimpleDateFormat("hh:mma");
	    Date twelveHr;
	    String twentyFourHrTime;
		try {
			twelveHrFormat.setLenient(false);
			twentyFourHrFormat.setLenient(false);
			twelveHr = twelveHrFormat.parse(twelveHrTime);
			twentyFourHrTime = twentyFourHrFormat.format(twelveHr);
			return twentyFourHrTime;
		} catch (ParseException e) {
			return twelveHrTime;
		} 
	    // System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
	}

	private static String[] getTwentyfourHrTimesFromString(String input) {
		Matcher m = HHMM.matcher(input);
		String[] ans = new String[4];
		
		int i = 0;
		while (m.find() & i < 2) {
			if (i == 0) {
				ans[INDEX_FOR_START] = m.group(1);
				ans[INDEX_FOR_END] = m.group(5);
			} else {
				ans[INDEX_FOR_END] = m.group(1);
			}
			i++;
		}

		return ans;
	}
	
	private static String removeTimesFromString(String input) {
		input = input.replaceAll(TIME_REGEX, "");
		return input.trim();
	}
	
	private static boolean isInvalidTimes(String[] times) {
		if (times[INDEX_FOR_START] == null) {
			return false;
		}

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-mm-yy HH:mm");
		try {
			dateTimeFormat.setLenient(false);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateTimeFormat.parse("1-1-15 " + times[0]));
			if (times[INDEX_FOR_END] == null) {
				return false;
			}
			cal.setTime(dateTimeFormat.parse("1-1-15 " + times[1]));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return true; // failed to parse
		}
		return false;
	}

	

}
