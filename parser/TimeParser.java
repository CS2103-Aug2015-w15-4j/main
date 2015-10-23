package parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
	// protected static final String TWELVE_HR_REGEX = "(?<=\\s|^)(1[012]|0?[1-9])([.:][0-5][0-9])?\\s?(am|pm)?(\\s?(?:-|to)\\s?(1[012]|0?[1-9])([.:][0-5][0-9])?\\s?)?(am|pm)(?=\\s|$)";
	// protected static final String TWENTYFOUR_HR_REGEX = "(?<=\\s|^)((0?[0-9]|1[0-9]|2[0-3])[:]([0-5][0-9]))\\s?[?:h|H]?\\s?(-\\s?((0[0-9]|1[0-9]|2[0-3])[:]([0-5][0-9])))?\\s?[?:h|H]?(?=\\s|$)";
	protected static final String TWELVE_HR_REGEX = "(?<=\\s|^)([0-9]?[0-9])([.:][0-9][0-9])?\\s?(am|pm)?(\\s?(?:-|to|until|til|till)\\s?([0-9]?[0-9])([.:][0-9][0-9])?\\s?)?(am|pm)(?=\\s|$)";
	protected static final String TWENTYFOUR_HR_REGEX = "(?<=\\s|^)(([0-9]?[0-9])[:]([0-9][0-9]))\\s?[?:h|H]?\\s?((?:-|to|until|til|till)?\\s?(([0-9]?[0-9])[:]([0-9][0-9])))?\\s?[?:h|H]?(?=\\s|$)";
		
	
	protected static final String TIME_REGEX = "(" + TWELVE_HR_REGEX + "|" + TWENTYFOUR_HR_REGEX + ")";
	
	protected static final Pattern HHMM = Pattern.compile(TWENTYFOUR_HR_REGEX);
	protected static final Pattern HMMA = Pattern.compile(TWELVE_HR_REGEX);
	
	protected static final String TWELVE_HR_FORMAT = "h:mma";
	protected static final String TWENTY_FOUR_HR_FORMAT = "HH:mm";

	private static final int TIME_H1 = 1;
	private static final int TIME_M1 = 2;
	private static final int TIME_APM1 = 3;
	private static final int TIME_H2 = 5;
	private static final int TIME_M2 = 6;
	private static final int TIME_APM2 = 7;
	
	private static final Logger logger = Logger.getLogger(TimeParser.class.getName() );

	private static String[] getTwentyfourHrTimesFromString(String input) {
		Matcher m = HHMM.matcher(input);
		String[] ans = new String[4];
		ans[2] = TWENTY_FOUR_HR_FORMAT;
		
		int i = 0;
		while (m.find() & i < 2) {
			if (i == 0) {
				ans[0] = m.group(1);
				ans[1] = m.group(5);
			} else {
				ans[1] = m.group(1);
			}
			i++;
		}

		return ans;
	}

	//private static Pattern times = Pattern.compile("(0?[1-9]|1[012])([.:][0-5][0-9])?([ap]m)?(-((0?[1-9]|1[012])([.:][0-5][0-9])?([ap]m)))?");
	private static String[] getTwelveHrTimesFromString(String input) {
		Matcher m = HMMA.matcher(input);
		String[] timeArr = new String[4];
		timeArr[2] = TWELVE_HR_FORMAT;
		
		int i = 0;
		
		while (m.find() & i < 2) {
			int j = i;
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
			timeArr[j] = time1;
			if (i < 1 & (h2 != null)) {
				String time2 = h2;
				if (m2 != null) {
					time2 = time2 + ":" + m2.substring(1) + apm2;
				} else {
					time2 = time2 + ":00" + apm2;
				}
				timeArr[1] = time2;
			}
			i++;
		}
		return timeArr;
	}
	
	public static String[] getStandardTimesFromString(String input) {
		if (input == null) {
			return null;
		}
		String[] times = new String[4];
		times = getTwelveHrTimesFromString(input);
		logger.log(Level.FINE, "12HR for " + input + " : " + times[0]);
		if (times[0] == null) {
			times = getTwentyfourHrTimesFromString(input);
			logger.log(Level.FINE, "24HR for " + input + " : " + times[0]);
		} 
		times[3] = removeTimesFromString(input);
		// System.out.println(times[3]);
		return times;
	}
	private static String removeTimesFromString(String input) {
		String regex = TIME_REGEX;
		input = input.replaceAll(regex, "");
		return input.trim();
	}
}
