package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DateTimeParser {
	private static Pattern parantheses = Pattern.compile("([(][a-zA-Z0-9\\s]+[)])");
	private static Pattern dateKeyword = Pattern.compile("(on|by)([^\"#]*)");
	private static final String TAG_REGEX = "(#(\\w+))";
	private static final String DESCRIPTION_REGEX = "(\"[^\"]*?\")";
	
	private static String tagOrDescription = "(" + TAG_REGEX + "|" + DESCRIPTION_REGEX + ")";  	                                        

	public static boolean isNattyDateTime(String inputArgs) {
		Matcher m = parantheses.matcher(inputArgs);
		String nattyDate = null;

		if (m.find()) {
			nattyDate = m.group();
		}

		if (nattyDate != null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isFormattedDateTime(String input) {
		return true;
	}
	
	public static String extractDateSectionFromString(String input) {
		String extract = extractSectionAfterDateKeyword(input);
		if (extract.equals("")) { // does not contain date keywords
			return extractFormattedDates(input);
		} else {
			return extract;
		}
	}
	
	public static String extractFormattedDates(String input) {
		String[] dates = FormattedDateTimeParser.getFormattedDates(input);
		String[] times = FormattedDateTimeParser.getFormattedTimes(input);
		String firstDate = dates[0];
		String secondDate = dates[1];
		String firstTime = times[0];
		String secondTime = times[1];
		String firstDateTime = "";
		String secondDateTime = "";
		
		if (firstDate != null) { // not floating task
			firstDate = reverseOrder(firstDate);
			if (firstTime != null) {  // has time
				firstDateTime = firstDate + " " + firstTime + " to ";
			} else {
				firstDateTime = firstDate + " 2359 to "; // default time is 2359
			}
		} else { // is floating task, no date
			return "";
		}
		
		if (secondDate != null || secondTime != null) { // is event
			if (secondDate == null) { // has end time but not end date
				secondDateTime = firstDate + " " + secondTime; // assume start and end date is the same
			} else { // has end date
				secondDate = reverseOrder(secondDate);
				if (secondTime != null) {
					secondDateTime = secondDate + " " + secondTime;
				} else {
					secondDateTime = secondDate + " 2359 ";
				}
			}
		}
		return (firstDateTime + secondDateTime);
	}
	
	public static String removeTagsAndDescriptions(String inputArgs) {
		String regex = tagOrDescription;
		inputArgs = inputArgs.replaceAll(regex, "");
		if (inputArgs.equals("")) {
			return null;
		}
		return inputArgs.trim();
	}
	
	public static String extractSectionAfterDateKeyword(String input) {
		Matcher m = dateKeyword.matcher(removeTagsAndDescriptions(input));
		String dateSection = null;

		if (m.find()) {
			dateSection = m.group(2);
		}

		if (dateSection == null) {
			return "";
		} else {
			return dateSection.trim();
		}
	}

	public static String standardizeDateFormat(String input) {
		// replace dots, dashes etc. with slash
		// swap order to mm/dd/yy
		// remove @ signs -X
		return FormattedDateTimeParser.convertDateToStandardFormat(input);
	}
	
	public static String reverseOrder(String date) {
		String[] dateElements = date.split("-|\\.|/");
		String d = dateElements[0];
		String m = dateElements[1];
		String y = dateElements[2];
		return m + "/" + d + "/" + y;
	}
	
	public static Calendar[] getDatesTimes(String input) {
		Calendar[] datesTimes = new Calendar[2];
		String dateSection = extractDateSectionFromString(input);
		if (dateSection.equals("")) { //noDate
			datesTimes[0] = null;
			datesTimes[1] = null;
		} else { // has date
			// extract relevant parts
			String standardizedDateSection = standardizeDateFormat(dateSection);
			if (standardizedDateSection == null) { // error when standardizing
				datesTimes = null;
			} else {
				datesTimes = NattyDateTimeParser.parseDateTimeWithNatty(standardizedDateSection);
			}
		}
		return datesTimes;
	}
}
