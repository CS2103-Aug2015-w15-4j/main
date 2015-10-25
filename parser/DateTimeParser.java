package parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DateTimeParser {
	//private static Pattern parentheses = Pattern.compile("([(][a-zA-Z0-9\\s]+[)])");
	private static String DATE_KEYWORD_REGEX = "(?<=\\s|^)(on|by)\\s([^\"#]*)";
	private static Pattern DATE_KEYWORD_PATTERN = Pattern.compile(DATE_KEYWORD_REGEX);
	
	private static String TAG_OR_DESCRIPTION_REGEX = "(" + StringParser.TAG_REGEX + "|" + StringParser.DESCRIPTION_REGEX + ")";  	                                        
	private static String FORMATTED_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + ")";
	private static Pattern FORMATTED_DATE_OR_TIME_PATTERN = Pattern.compile(FORMATTED_DATE_TIME_REGEX);
	
	protected static String DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|" + DATE_KEYWORD_REGEX + ")";
	protected DateTimeParser nextParser;
	
	abstract protected String getUnparsedInput();
	abstract protected String[] getParsedTimes();
	abstract protected String[] getParsedDates();

	abstract protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes);

	private static final Logger logger = Logger.getLogger(DateTimeParser.class.getName() );
	
	public static String extractDateTimeSectionFromString(String input) {
		String extract = removeTagsAndDescriptions(input); // for formatted input
		String extractAfterKeyword = extractSectionAfterDateKeyword(extract); // returns "" if no keyword
		if (extractAfterKeyword.equals("")) { // no keyword
			String formattedExtract = getFormattedDateTimeSection(extract);
			logger.log(Level.FINE, "FORMATTED:" + formattedExtract + ",");
			return formattedExtract;
		} else {
			logger.log(Level.FINE, "KEYWORD: " + extractAfterKeyword);
			return extractAfterKeyword;
		}
	}
		
	private static String removeTagsAndDescriptions(String input) {
		return StringParser.removeRegexPatternFromString(input, TAG_OR_DESCRIPTION_REGEX);
	}
	
	private static String getFormattedDateTimeSection(String input) {
		Matcher m = FORMATTED_DATE_OR_TIME_PATTERN.matcher(input);
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
			dateSection = m.group(2);
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
		String dateFormat = dates[2];
		String timeFormat = times[2];
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat + " " + timeFormat);
		
		try {
			if (dates[0] == null) { // no date and time
				startCal = null;
				endCal = null;
			} else { // has start date
				dateTimeFormat.setLenient(false);
				if (times[0] == null) { // no start time, assume deadline 2359h
					String startDate = dates[0] + " ";
					startDate = addDefaultTime(timeFormat, startDate);
					startCal.setTime(dateTimeFormat.parse(startDate));
				} else { // has start date and time
					String startDateTime = dates[0] + " " + times[0];
					startCal.setTime(dateTimeFormat.parse(startDateTime));
				}
				
				if (dates[1] != null) { // has end date
					if (times[1] == null) { // no end time, assume deadline 2359h
						String endDate = dates[1] + " ";
						endDate = addDefaultTime(timeFormat, endDate);
						endCal.setTime(dateTimeFormat.parse(endDate));
					} else { // has end date and time
						String endDateTime = dates[1] + " " + times[1];
						endCal.setTime(dateTimeFormat.parse(endDateTime));
					}	
				} else { // no end date
					if (times[1] != null) { // but has end time
						String endDateTime = dates[0] + " " + times[1]; // end date same as start date
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
			endDate = endDate + "11:59pm";
		} else {
			endDate = endDate + "23:59";
		}
		return endDate;
	}
}
