//@@author A0114620X
package parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;

public abstract class DateTimeParser {
	static String DATE_KEYWORD_REGEX = "(?<=\\s|^)(on|by)\\s(?!.*( on | by ))([^\"#]*)";
	private static Pattern DATE_KEYWORD_PATTERN = Pattern.compile(DATE_KEYWORD_REGEX);
	
	private static String TAG_OR_DESCRIPTION_REGEX = "(" + StringParser.TAG_REGEX + "|" + StringParser.DESCRIPTION_REGEX + ")";  	                                        
	private static String CHECK_NON_NATTY_DATE_TIME_REGEX = "(" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow).*(?=\\s|$)))";
	private static Pattern CHECK_NON_NATTY_DATE_TIME_PATTERN = Pattern.compile(CHECK_NON_NATTY_DATE_TIME_REGEX);
	
	private static String NON_NATTY_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow).*(?=\\s|$)))";
	private static Pattern NON_NATTY_DATE_OR_TIME_PATTERN = Pattern.compile(NON_NATTY_DATE_TIME_REGEX);

	static String DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_WITH_YEAR_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|" + DATE_KEYWORD_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow).*(?=\\s|$)))";
	static String NO_KEYWORD_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateTimeParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateTimeParser.FLEXIBLE_DATE_REGEX + "|((?<=\\s|^)(tmr|tomorrow|tomorow).*(?=\\s|$)))";
	
	protected DateTimeParser nextParser;
	
	abstract protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed);

	private static final Logger logger = Logger.getLogger(DateTimeParser.class.getName() );
	
	public static String extractDateTimeSectionFromString(String input) {
		String extract = removeTagsAndDescriptions(input); // for formatted input
		// System.out.println("EXTRACTED: " + extract);
		if (hasNonNattyDateTimeSection(extract)) { 
			String nonNattyDateTime = extractNonNattyDateTimeSection(extract);
			logger.log(Level.FINE, "FORMATTED/FLEX: " + nonNattyDateTime);
			// System.out.println("NONNATTY:" + nonNattyDateTime);
			return nonNattyDateTime;
		} else {
			String keywordDateSection = extractSectionAfterDateKeyword(extract);
			logger.log(Level.FINE, "KEYWORD:" + keywordDateSection + ",");
			// System.out.println("NATTY:" + keywordDateSection);
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
			dateSection = dateSection + m.group() + " ; ";
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
	
	public DateTime getDatesTimes(DateTimeBuilder currentlyParsed) {
		DateTimeBuilder datesTimes = parse(currentlyParsed);
		if (datesTimes.isDoneParsing()) { // error
			return datesTimes.build();
		} else { // not detected
			if (nextParser != null) {
				return nextParser.getDatesTimes(currentlyParsed);
			}
		}
		return datesTimes.build();
	}
	
		
	
}
