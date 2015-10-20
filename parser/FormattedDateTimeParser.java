package parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedDateTimeParser extends DateTimeParser{
	
	private static final String DELIM = "-/.";
	protected static final String DD = "(?<=^|\\s)([0-9]?[0-9])";
	protected static final String MM = "([0-9]?[0-9])";
	protected static final String DATE_DELIM = "[" + DELIM + "]";
	protected static final String YY = "("+ DATE_DELIM + "(\\d{4}|\\d{2}))?(?=\\s|$)";
	protected static final String FORMATTED_DATE_REGEX = DD + DATE_DELIM + MM + YY;
	protected static final String FORMATTED_DATE_FORMAT = "d/M/yy";
	private static final int INDEX_FOR_DATE_INPUT = 1;
	private static final int INDEX_FOR_MONTH_INPUT = 2;
	private static final int INDEX_FOR_YEAR_INPUT = 3;
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private static final int INDEX_FOR_YEAR_ARR = 2;
	
	private static Pattern ddmmyy = Pattern.compile(FORMATTED_DATE_REGEX);
	private static Pattern dateDelim = Pattern.compile(DATE_DELIM);
	
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
			year = "20" + year.substring(1);
		}
		return year;
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
	public static String[] getStandardFormattedDates(String userInput) {
		System.out.println(userInput);
		Matcher m = ddmmyy.matcher(userInput);
		String[] ans = new String[4];

		int i = 0;
		String[][] tempDates = new String[2][3];
		while (m.find() & i < 2) {
			String[] dateArr = new String[3];
			dateArr[INDEX_FOR_DATE_ARR] = m.group(INDEX_FOR_DATE_INPUT);
			dateArr[INDEX_FOR_MONTH_ARR] = m.group(INDEX_FOR_MONTH_INPUT);
			if (m.groupCount() > 2) {
				dateArr[INDEX_FOR_YEAR_ARR] = convertYearToStandardFormat(m.group(INDEX_FOR_YEAR_INPUT));
			}
			tempDates[i] = dateArr;
			i++;
		}
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 3; b++) {
				System.out.println("TEMP[" + a + "]["+b+"]: " + tempDates[a][b]);
			}
		}
		ans = standardizeDatesArray(tempDates);
		ans[2] = FORMATTED_DATE_FORMAT;
		ans[3] = removeFormattedDatesFromString(userInput);
		return ans;
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
		System.out.println("Before formatting year: " + newDateString);
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
		return StringParser.removeRegexPatternFromString(input, FORMATTED_DATE_REGEX);
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
		formattedDates = getStandardFormattedDates(formattedTimes[3]);
		logger.log(Level.FINE, "DATES:" + formattedDates[0] + ".");
		unparsedInput = formattedDates[3];
		logger.log(Level.FINE, "TO FLEX:" + unparsedInput + ".");
		return convertStringToCalendar(formattedDates, formattedTimes);
	}
}
