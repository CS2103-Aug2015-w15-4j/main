//@@author A0114620X

package parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;
import parser.DateTime.ParserType;

public class FormattedDateTimeParser extends DateTimeParser{
	
	private static final String DELIM = "-/.";
	static final String START = "(?<=^|\\s)";
	static final String END = "(?=\\s|$)";
	static final String DD = "([0-9]?[0-9])";
	static final String MM = "([0-9]?[0-9])";
	static final String DATE_DELIM = "[" + DELIM + "]";
	static final String YY = "(\\d{4}|\\d{2})";
	static final String NO_YEAR_FORMATTED_DATE_REGEX = START + DD + "[-/]" + MM + END;
	static final String FORMATTED_DATE_WITH_YEAR_REGEX = START + DD + DATE_DELIM + MM + DATE_DELIM + YY + END;
	static final String FORMATTED_DATE_REGEX = NO_YEAR_FORMATTED_DATE_REGEX + "|" + FORMATTED_DATE_WITH_YEAR_REGEX;
	static final String FORMATTED_DATE_FORMAT = "d/M/yy";
	
	private static final int INDEX_FOR_DATE_INPUT = 1;
	private static final int INDEX_FOR_MONTH_INPUT = 2;
	private static final int INDEX_FOR_YEAR_INPUT = 3;
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private static final int INDEX_FOR_YEAR_ARR = 2;
	
	private static Pattern ddmmyy = Pattern.compile(FORMATTED_DATE_WITH_YEAR_REGEX);
	private static Pattern ddmm = Pattern.compile(NO_YEAR_FORMATTED_DATE_REGEX);
	
	private static final Logger logger = Logger.getLogger(FormattedDateTimeParser.class.getName() );
	
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_END = 1;
	private static final int INDEX_FOR_UNPARSED = 2;
	
	@Override
	protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed) {
		String input = currentlyParsed.getUnparsedInput();
		logger.log(Level.FINE, "date section: " + input + ".");
		String[][] formattedDates = getStandardFormattedDates(input);
		// logger.log(Level.FINE, "DATES:" + formattedDates[0] + ".");
		currentlyParsed = currentlyParsed.dates(formattedDates);
		// logger.log(Level.FINE, "TO FLEX:" + unparsedInput + ".");
		return currentlyParsed;
	}
	
	// Converts years to yyyy
	private static String convertYearToStandardFormat(String year) {
		if (year == null) {
			return null;
		} else if (year.length() < 4) {
			year = "20" + year;
		} else {
			return year;
		}
		return year;
	}
	
	private static String[][] getStandardFormattedDates(String input) {
		String[][] dates = getStandardFormattedDatesWithYear(input);
		if (dates[INDEX_FOR_START][INDEX_FOR_DATE_ARR] == null) {
			dates = getStandardFormattedDatesNoYear(input);
		}
		return processFormattedDates(input, dates);
	}

	private static String[][] getStandardFormattedDatesWithYear(String userInput) {
		return getFormattedDates(userInput, ddmmyy);
	}

	private static String[][] getStandardFormattedDatesNoYear(String userInput) {
		return getFormattedDates(userInput, ddmm);
	}

	private static String[][] getFormattedDates(String userInput, Pattern p) {
		Matcher m = p.matcher(userInput);
		
		int i = 0;
		String[][] tempDates = new String[2][3];
		while (m.find() & i < 2) {
			String[] dateArr = new String[3];
			dateArr[INDEX_FOR_DATE_ARR] = m.group(INDEX_FOR_DATE_INPUT);
			dateArr[INDEX_FOR_MONTH_ARR] = m.group(INDEX_FOR_MONTH_INPUT);
			String yr;
			if (m.groupCount() < 3) {
				yr = null;
			} else {
				yr = convertYearToStandardFormat(m.group(INDEX_FOR_YEAR_INPUT));
			}
			dateArr[INDEX_FOR_YEAR_ARR] = yr;
			tempDates[i] = dateArr;
			i++;
		}
		logDatesRead(tempDates);
		return tempDates;
	}

	private static String[][] processFormattedDates(String userInput, String[][] tempDates) {
		String[][] ans = new String[4][3];
		ans[0] = tempDates[INDEX_FOR_START];
		ans[1] = tempDates[INDEX_FOR_END];
		ans[INDEX_FOR_UNPARSED][0] = removeFormattedDatesFromString(userInput);
		return ans;
	}
	
	private static void logDatesRead(String[][] tempDates) {
		/*for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 3; b++) {
				System.out.println("TEMP[" + a + "]["+b+"]: " + tempDates[a][b]);
			}
		}*/
	}

	private static String removeFormattedDatesFromString(String input) {
		return InputParser.removeRegexPatternFromString(input, FORMATTED_DATE_WITH_YEAR_REGEX);
	}
		
	
}
