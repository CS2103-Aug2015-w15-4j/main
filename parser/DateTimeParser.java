//@@author A0114620X
package parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DateTimeParser {
	private static final String DEFAULT_TWENTYFOUR_HOUR_TIME = "23:59";
	private static final String DEFAULT_TWELVE_HR__TIME = "11:59pm";
	private static final int INDEX_FOR_END = 1;
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_TIME_FORMAT = 2;
	private static final int INDEX_FOR_DATE_FORMAT = 2;
	static String DATE_KEYWORD_REGEX = "(?<=\\s|^)(on|by)\\s(?!.*( on | by ))([^\"#]*)";
	private static Pattern DATE_KEYWORD_PATTERN = Pattern.compile(DATE_KEYWORD_REGEX);
	
	private static String TAG_OR_DESCRIPTION_REGEX = "(" + StringParser.TAG_REGEX + "|" + StringParser.DESCRIPTION_REGEX + ")";  	                                        
	private static String CHECK_NON_NATTY_DATE_TIME_REGEX = "(" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow)(?=\\s|$)))";
	private static Pattern CHECK_NON_NATTY_DATE_TIME_PATTERN = Pattern.compile(CHECK_NON_NATTY_DATE_TIME_REGEX);
	
	private static String NON_NATTY_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow)(?=\\s|$)))";
	private static Pattern NON_NATTY_DATE_OR_TIME_PATTERN = Pattern.compile(NON_NATTY_DATE_TIME_REGEX);

	static String DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|" + DATE_KEYWORD_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow)(?=\\s|$)))";
	static String NO_KEYWORD_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow)(?=\\s|$)))";
	
	protected DateTimeParser nextParser;
	
	abstract String getUnparsedInput();
	abstract String[] getParsedTimes();
	abstract String[] getParsedDates();

	abstract protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes);

	private static final Logger logger = Logger.getLogger(DateTimeParser.class.getName() );
	
	public static String extractDateTimeSectionFromString(String input) {
		String extract = removeTagsAndDescriptions(input); // for formatted input
		if (hasNonNattyDateTimeSection(extract)) { 
			String nonNattyDateTime = extractNonNattyDateTimeSection(extract);
			logger.log(Level.FINE, "FORMATTED/FLEX: " + nonNattyDateTime);
			return nonNattyDateTime;
		} else {
			String keywordDateSection = extractSectionAfterDateKeyword(extract);
			logger.log(Level.FINE, "KEYWORD:" + keywordDateSection + ",");
			return keywordDateSection;
		}
	}
		
	private static String removeTagsAndDescriptions(String input) {
		return InputParser.removeRegexPatternFromString(input, TAG_OR_DESCRIPTION_REGEX);
	}
	
	private static Boolean hasNonNattyDateTimeSection(String input) {
		Matcher m = CHECK_NON_NATTY_DATE_TIME_PATTERN.matcher(input);
		
		if (m.find()) {
			return true;
		}
		
		return false;
	}
	
	private static String extractNonNattyDateTimeSection(String input) {
		Matcher m = NON_NATTY_DATE_OR_TIME_PATTERN.matcher(input);
		String dateSection = "";

		while (m.find()) {
			dateSection = dateSection + m.group() + " ";
		}
		
		logger.log(Level.FINE, "DATESECTION:" + dateSection.trim() + ".");
		return dateSection.trim();
	}
	
	private static String extractSectionAfterDateKeyword(String input) {
		Matcher m = DATE_KEYWORD_PATTERN.matcher(input);
		String dateSection = "";

		if (m.find()) {
			dateSection = m.group(3);
		}
		return dateSection.trim();
	}
		
	public void setNextParser(DateTimeParser nextParser) {
		this.nextParser = nextParser;
	}
	
	public Calendar[] getDatesTimes(String input, String[] parsedDates, String[] parsedTimes) {
		Calendar[] datesTimes = new Calendar[2];
		datesTimes = parse(input, parsedDates, parsedTimes);
		if (datesTimes == null) { // error
			return null;
		} else if (datesTimes[0] == null) { // not detected
			if (nextParser != null) {
				return nextParser.getDatesTimes(getUnparsedInput(), getParsedDates(), getParsedTimes());
			}
		}
		return datesTimes;
	}
	
		
	public static Calendar[] convertStringToCalendar(String[] dates, String[] times) {
		Calendar[] calTimes = new Calendar[2];
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		String dateFormat = dates[INDEX_FOR_DATE_FORMAT];
		String timeFormat = times[INDEX_FOR_TIME_FORMAT];
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat + " " + timeFormat);
		
		try {
			if (dates[INDEX_FOR_START] == null) { // no date and time
				startCal = null;
				endCal = null;
			} else { // has start date
				dateTimeFormat.setLenient(false);
				if (times[INDEX_FOR_START] == null) { // no start time, assume deadline 2359h
					String startDate = dates[INDEX_FOR_START] + " ";
					startDate = addDefaultTime(timeFormat, startDate);
					startCal.setTime(dateTimeFormat.parse(startDate));
				} else { // has start date and time
					String startDateTime = dates[INDEX_FOR_START] + " " + times[INDEX_FOR_START];
					startCal.setTime(dateTimeFormat.parse(startDateTime));
				}
				
				if (dates[INDEX_FOR_END] != null) { // has end date
					if (times[INDEX_FOR_END] == null) { // no end time, assume deadline 2359h
						String endDate = dates[INDEX_FOR_END] + " ";
						endDate = addDefaultTime(timeFormat, endDate);
						endCal.setTime(dateTimeFormat.parse(endDate));
					} else { // has end date and time
						String endDateTime = dates[INDEX_FOR_END] + " " + times[INDEX_FOR_END];
						endCal.setTime(dateTimeFormat.parse(endDateTime));
					}	
				} else { // no end date
					if (times[INDEX_FOR_END] != null) { // but has end time
						String endDateTime = dates[INDEX_FOR_START] + " " + times[INDEX_FOR_END]; // end date same as start date
						endCal.setTime(dateTimeFormat.parse(endDateTime));
					} else { // no end date and no end time
						endCal = null;
					}
				}
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return null; // failed to parse
		}
		
		calTimes[0] = startCal;
		calTimes[1] = endCal;

		return calTimes;
	}
	
	private static String addDefaultTime(String timeFormat, String endDate) {
		if (timeFormat.equals(TimeParser.TWELVE_HR_FORMAT)) {
			endDate = endDate + DEFAULT_TWELVE_HR__TIME;
		} else {
			endDate = endDate + DEFAULT_TWENTYFOUR_HOUR_TIME;
		}
		return endDate;
	}
}
