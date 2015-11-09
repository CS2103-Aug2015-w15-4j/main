//@@author A0114620X

package parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;

public abstract class DateTimeParser {
	static String DATE_KEYWORD_REGEX = "(?<=\\s|^)(on|by)\\s(?!.*( on | by ))([^\"#]*)";
	private static Pattern DATE_KEYWORD_PATTERN = Pattern.compile(DATE_KEYWORD_REGEX);
	static final String TOMORROW_REGEX = "((?<=\\s|^)(tmr|tomorrow|tomorow).*(?=\\s|$))";
	private static final String TAG_OR_DESCRIPTION_REGEX = "(" + InputParser.TAG_REGEX + "|" + InputParser.DESCRIPTION_REGEX + ")";  	                                        
	private static final String NO_KEYWORD_DATE_REGEX = FormattedDateParser.FORMATTED_DATE_REGEX + "|" + FlexibleDateParser.FLEXIBLE_DATE_REGEX + "|" + TOMORROW_REGEX;
	private static final Pattern NO_KEYWORD_DATE_PATTERN = Pattern.compile(NO_KEYWORD_DATE_REGEX);
	
	static final String NO_KEYWORD_DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + NO_KEYWORD_DATE_REGEX + ")";
	static final Pattern NO_KEYWORD_DATE_TIME_PATTERN = Pattern.compile(NO_KEYWORD_DATE_TIME_REGEX);

	static final String DATE_TIME_REGEX = "(" + TimeParser.TIME_REGEX + "|" + FormattedDateParser.FORMATTED_DATE_WITH_YEAR_REGEX + "|" + FlexibleDateParser.FLEXIBLE_DATE_REGEX + "|" + DATE_KEYWORD_REGEX + "|" + TOMORROW_REGEX + ")";
																				   
	protected DateTimeParser nextParser;
	
	abstract protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed);

	private static final Logger logger = Logger.getLogger(DateTimeParser.class.getName() );
	
	public static String extractDateTimeSectionFromString(String input) {
		String extract = removeTagsAndDescriptions(input); // for formatted input
		if (hasNoKeywordDate(extract)) { 
			String noKeywordDateTime = extractNonNattyDateTimeSection(extract);
			logger.log(Level.FINE, "Non-Natty date section: " + noKeywordDateTime);
			return noKeywordDateTime;
		} else {
			String keywordDateTime = extractSectionAfterDateKeyword(extract);
			logger.log(Level.FINE, "Date keyword section: " + keywordDateTime + ",");
			return keywordDateTime;
		}
	}
		
	private static String removeTagsAndDescriptions(String input) {
		return InputParser.removeRegexPatternFromString(input, TAG_OR_DESCRIPTION_REGEX);
	}
	
	private static Boolean hasNoKeywordDate(String input) {
		Matcher m = NO_KEYWORD_DATE_PATTERN.matcher(input);
		if (m.find()) {
			return true;
		}
		return false;
	}
	
	private static String extractNonNattyDateTimeSection(String input) {
		Matcher m = NO_KEYWORD_DATE_TIME_PATTERN.matcher(input);
		String dateSection = "";
		while (m.find()) {
			dateSection = dateSection + m.group() + " ; ";
		}
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
		if (datesTimes.isDoneParsing()) { 
			return datesTimes.build();
		} else { // not detected
			if (nextParser != null) {
				return nextParser.getDatesTimes(currentlyParsed);
			}
		}
		return datesTimes.build();
	}
	
		
	
}
