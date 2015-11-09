//@@author A0114620X

package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.DateTime.DateTimeBuilder;
import parser.DateTime.ParserType;

public class FlexibleDateTimeParser extends DateTimeParser {
	
	private static final int INDEX_FOR_UNPARSED = 2;

	private enum DateFormat {
		DATE_MONTH, MONTH_DATE;
	}
	
	private static final String MONTHS = "(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)";
	private static final String DATES = "(\\d?\\d)(?:st|rd|nd|th)?";
	private static final String YEARS = "(\\d{4})?";
	
	private static final String DATE_MONTH_REGEX = "(?<=\\s|^)" + DATES + "\\s" + MONTHS + ",?\\s?" + YEARS + "(?=\\s|$)";
	private static final String MONTH_DATE_REGEX = "(?<=\\s|^)" + MONTHS + "\\s" + DATES + ",?\\s?" + YEARS + "(?=\\s|$)";
	
	private static final Pattern DMMM = Pattern.compile(DATE_MONTH_REGEX);
	private static final Pattern MMMD = Pattern.compile(MONTH_DATE_REGEX);
	
	protected static final String FLEXIBLE_DATE_REGEX = "(" + DATE_MONTH_REGEX + "|" + MONTH_DATE_REGEX + ")";
	
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private static final int INDEX_FOR_YEAR_ARR = 2;
	
	private static final int DM_INDEX_FOR_DATE = 1;
	private static final int DM_INDEX_FOR_MONTH = 2;
	private static final int DM_INDEX_FOR_YEAR = 3;
	
	private static final int MD_INDEX_FOR_DATE = 2;
	private static final int MD_INDEX_FOR_MONTH = 1;
	private static final int MD_INDEX_FOR_YEAR = 3;
	
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_END = 1;
	
	private static final int[] DM_INDICES = {DM_INDEX_FOR_DATE, DM_INDEX_FOR_MONTH, DM_INDEX_FOR_YEAR};
	private static final int[] MD_INDICES = {MD_INDEX_FOR_DATE, MD_INDEX_FOR_MONTH, MD_INDEX_FOR_YEAR};
	
	private static final Logger logger = Logger.getLogger(FlexibleDateTimeParser.class.getName() );
	
	@Override
	protected DateTimeBuilder parse(DateTimeBuilder currentlyParsed) {
		String input = currentlyParsed.getUnparsedInput();
		String[][] flexibleDates = getStandardFlexibleDates(input);
		logger.log(Level.FINE, "Flexible parse: [" + flexibleDates[0][0] +"/" + flexibleDates[0][1]);
		currentlyParsed = currentlyParsed.dates(flexibleDates);
		return currentlyParsed;
	}
	
	private static String[][] getStandardFlexibleDates(String input) {
		String[][] dates = new String[3][3];
		dates = getMonthDateFromString(input);
		if (dates[INDEX_FOR_START][INDEX_FOR_DATE_ARR] == null) { 
			dates = getDateMonthFromString(input);
		}
		return dates;
	}
	
	private static String[][] getMonthDateFromString(String input) {
		String[][] dateArr = parseStringToDateArray(input, DateFormat.MONTH_DATE);
		dateArr = convertDateArrayToStdFormat(dateArr);
		setUnparsedInput(DateFormat.MONTH_DATE, input, dateArr);
		// logger.log(Level.FINE, "MD start: " + dateArr[0] + " end: " + dateArr[1] + " format: " + dateArr[2] + " unparsed: " + dateArr[3]);		
		return dateArr;
	}
	
	private static String[][] getDateMonthFromString(String input) {
		String[][] dateArr = parseStringToDateArray(input, DateFormat.DATE_MONTH);
		dateArr = convertDateArrayToStdFormat(dateArr);
		setUnparsedInput(DateFormat.DATE_MONTH, input, dateArr);
		//logger.log(Level.FINE, "DM start: " + dateArr[0] + " end: " + dateArr[1] + " format: " + dateArr[2] + " unparsed: " + dateArr[3]);
		return dateArr;
	}

	private static String[][] parseStringToDateArray(String input, DateFormat df) {
		input = input.toLowerCase();
		String[][] dateArr = new String[2][3];
		Matcher m;
		int[] dmyIndices;
		
		if (df == DateFormat.MONTH_DATE) {
			m = MMMD.matcher(input);
			dmyIndices = MD_INDICES;
		} else if (df == DateFormat.DATE_MONTH) {
			m = DMMM.matcher(input);
			dmyIndices = DM_INDICES;
		} else {
			return null;
		}
		
		int i = 0;
		while (m.find() & i < 2) {
			String date = m.group(dmyIndices[INDEX_FOR_DATE_ARR]);
			String month = m.group(dmyIndices[INDEX_FOR_MONTH_ARR]);
			String year = m.group(dmyIndices[INDEX_FOR_YEAR_ARR]);
			updateDateArray(dateArr, i, date, month, year);
			i++;
		}
		return dateArr;
	}

	private static void updateDateArray(String[][] temp, int i, String date, String month, String year) {
		temp[i][INDEX_FOR_DATE_ARR] = date;
		temp[i][INDEX_FOR_MONTH_ARR] = month.substring(0,3);
		temp[i][INDEX_FOR_YEAR_ARR] = year;
	}
	
	private static String[][] convertDateArrayToStdFormat(String[][] temp) {
		String[][] dateArr = new String[3][3];
		dateArr[0] = convertToStandardDateFormat(temp[0]);
		dateArr[1] = convertToStandardDateFormat(temp[1]);
		return dateArr;
	}

	private static String[] convertToStandardDateFormat(String[] date) {
		date[INDEX_FOR_MONTH_ARR] = convertMonthToInt(date[INDEX_FOR_MONTH_ARR]);
		return date;
	}

	private static String convertMonthToInt(String monthName) {
		int month = -1;
		if (monthName == null) {
			return null;
		}
		try {
			Date date = new SimpleDateFormat("MMM").parse(monthName);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			month = cal.get(Calendar.MONTH) + 1;		    
		} catch (ParseException e) {
			// is never reached
			// do nothing
		}
		return String.valueOf(month);
	}
	
	private static void setUnparsedInput(DateFormat df, String input, String[][] dateArr) {
		if (df == DateFormat.MONTH_DATE) {
			dateArr[INDEX_FOR_UNPARSED][0] = removeMonthDateFromString(input);
		} else if (df == DateFormat.DATE_MONTH) {
			dateArr[INDEX_FOR_UNPARSED][0] = removeDateMonthFromString(input);
		}
	}
	
	private static String removeMonthDateFromString(String input) {
		return InputParser.removeRegexPatternFromString(input, MONTH_DATE_REGEX);
	}

	// double direction dependency!
	private static String removeDateMonthFromString(String input) {
		return InputParser.removeRegexPatternFromString(input, DATE_MONTH_REGEX);
	}
}
