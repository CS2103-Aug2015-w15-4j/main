//@@author A0114620X

package parser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedDateTimeParser extends DateTimeParser{
	
	private static final int INDEX_FOR_START = 0;
	private static final String DELIM = "-/.";
	static final String START = "(?<=^|\\s)";
	static final String END = "(?=\\s|$)";
	static final String DD = "([0-9]?[0-9])";
	static final String MM = "([0-9]?[0-9])";
	static final String DATE_DELIM = "[" + DELIM + "]";
	static final String YY = "(\\d{4}|\\d{2})";
	static final String NO_YEAR_FORMATTED_DATE_REGEX = START + DD + "[-/]" + MM + END;
	static final String FORMATTED_DATE_REGEX = START + DD + DATE_DELIM + MM + DATE_DELIM + YY + END;
	static final String FORMATTED_DATE_FORMAT = "d/M/yy";
	
	private static final int INDEX_FOR_DATE_INPUT = 1;
	private static final int INDEX_FOR_MONTH_INPUT = 2;
	private static final int INDEX_FOR_YEAR_INPUT = 3;
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private static final int INDEX_FOR_YEAR_ARR = 2;
	
	private static Pattern ddmmyy = Pattern.compile(FORMATTED_DATE_REGEX);
	private static Pattern ddmm = Pattern.compile(NO_YEAR_FORMATTED_DATE_REGEX);
	
	private String[] formattedTimes;
	private String[] formattedDates;
	private String unparsedInput;

	private static final Logger logger = Logger.getLogger(FormattedDateTimeParser.class.getName() );

	private static String convertDateToStandardFormat(String[] dateArr) {
		String date = dateArr[INDEX_FOR_DATE_ARR];
		if (date == null) {
			return null;
		}
		String month = dateArr[INDEX_FOR_MONTH_ARR];
		String year = dateArr[INDEX_FOR_YEAR_ARR];
		
		return date + "/" + month + "/" + year;
	}
	
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
	
	public static String[] getStandardFormattedDates(String input) {
		String[][] dates = getStandardFormattedDatesWithYear(input);
		if (dates[INDEX_FOR_START][0] == null) {
			dates = getStandardFormattedDatesNoYear(input);
		}
		return processFormattedDates(input, dates);
	}
	/**
	 * This method looks for dd/mm/yy or dd.mm.yy or dd-mm-yy) substrings of userInput, single digit
	 * inputs allowed for date and month and returns dates formated dd/MM/yy.
	 *
	 * @param userInput
	 *            Unparsed command arguments input by user.
	 * @return String array where ans[0] = first date found (if any) and ans[1]
	 *         = second date found (if any), If no dates are found, ans[0] =
	 *         ans[1] = null and if only one date found, ans[1] = null.
	 */
	public static String[][] getStandardFormattedDatesWithYear(String userInput) {
		return getFormattedDates(userInput, ddmmyy);
	}

	public static String[][] getStandardFormattedDatesNoYear(String userInput) {
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

	private static String[] processFormattedDates(String userInput, String[][] tempDates) {
		String[] ans = new String[4];
		ans = standardizeDatesArray(tempDates);
		ans[2] = FORMATTED_DATE_FORMAT;
		ans[3] = removeFormattedDatesFromString(userInput);
		return ans;
	}
	
	private static void logDatesRead(String[][] tempDates) {
		/*for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 3; b++) {
				System.out.println("TEMP[" + a + "]["+b+"]: " + tempDates[a][b]);
			}
		}*/
	}
	
	private static String[] standardizeDatesArray(String[][] tempDates) {
		String[] ans = new String[4];
		String[] firstDate = tempDates[0];
		String[] secondDate = tempDates[1];
		if (firstDate[INDEX_FOR_DATE_ARR] == null) { // no date detected
			ans[0] = null;
			ans[1] = null;
			return ans;
		} else {
			if (secondDate[INDEX_FOR_DATE_ARR] == null && firstDate[INDEX_FOR_YEAR_ARR] == null) { // only has start date
				setNextValidYear(firstDate);
			} else { // has start and end date
				if (secondDate[INDEX_FOR_YEAR_ARR] != null) { // has end year
					if (firstDate[INDEX_FOR_YEAR_ARR] == null) { // no start year
						firstDate[INDEX_FOR_YEAR_ARR] = secondDate[INDEX_FOR_YEAR_ARR];
					} 
				} else {
					if (firstDate[INDEX_FOR_YEAR_ARR] == null) {
						setNextValidYear(firstDate);
					}
					if (secondDate[INDEX_FOR_DATE_ARR] != null) {
						setNextValidYear(secondDate);
					}
				}
			}
		}
		ans[0] = convertDateToStandardFormat(firstDate);
		ans[1] = convertDateToStandardFormat(secondDate);
		return ans;
	}

	private static void setNextValidYear(String[] date) {
		LocalDate today = LocalDate.now();
		String currYear = String.valueOf(today.getYear());
		String newDateString = currYear + "-" + date[INDEX_FOR_MONTH_ARR] + "-" + date[INDEX_FOR_DATE_ARR];
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		try {
			LocalDate newDate = LocalDate.parse(newDateString, formatter);
			if (newDate.isAfter(today)) {
				date[INDEX_FOR_YEAR_ARR] = currYear;
			} else {
				date[INDEX_FOR_YEAR_ARR] = String.valueOf(today.getYear() + 1);
			}
		} catch (DateTimeParseException e){
			date = null;
		}
	}

	private static String removeFormattedDatesFromString(String input) {
		return InputParser.removeRegexPatternFromString(input, FORMATTED_DATE_REGEX);
	}
	
	@Override
	protected String[] getParsedDates() {
		return formattedDates;
	}
	
	@Override
	protected String[] getParsedTimes() {
		return formattedTimes;
	}
	
	@Override
	protected String getUnparsedInput() {
		return unparsedInput;
	}
	
	@Override
	protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes) {
		logger.log(Level.FINE, "TO FORM:" + input + ".");
		formattedTimes = TimeParser.getStandardTimesFromString(input); // all time parsing done
		if (invalidTimes(formattedTimes)) {
			return null;
		}
		formattedDates = getStandardFormattedDates(formattedTimes[3]);
		logger.log(Level.FINE, "DATES:" + formattedDates[0] + ".");
		unparsedInput = formattedDates[3];
		logger.log(Level.FINE, "TO FLEX:" + unparsedInput + ".");
		return convertStringToCalendar(formattedDates, formattedTimes);
	}

	private boolean invalidTimes(String[] times) {
		if (times[0] == null) {
			return false;
		}
		String timeFormat = times[2];
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-mm-yy" + timeFormat);
		try {
			dateTimeFormat.setLenient(false);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateTimeFormat.parse("1-1-15" + times[0]));
			if (times[1] == null) {
				return false;
			}
			cal.setTime(dateTimeFormat.parse("1-1-15" + times[1]));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return true; // failed to parse
		}
		return false;
	}
}
