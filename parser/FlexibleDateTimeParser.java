package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlexibleDateTimeParser extends DateTimeParser {
	private String[] flexibleDates;
	private String[] flexibleTimes;
	private String unparsedInput;
	
	private static final int DM_INDEX_FOR_DATE = 1;
	private static final int DM_INDEX_FOR_MONTH = 2;
	private static final int DM_INDEX_FOR_YEAR = 3;
	
	private static final int MD_INDEX_FOR_DATE = 2;
	private static final int MD_INDEX_FOR_MONTH = 1;
	private static final int MD_INDEX_FOR_YEAR = 3;
	
	private static final String MONTHS = "(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)";
	private static final String DATES = "(\\d?\\d)(?:st|rd|nd|th)?";
	private static final String YEARS = "(\\d{4})?";
	private static final String DATE_MONTH_REGEX = "(?<=\\s|^)" + DATES + "\\s" + MONTHS + ",?\\s?" + YEARS + "(?=\\s|$)";
	private static final String MONTH_DATE_REGEX = "(?<=\\s|^)" + MONTHS + "\\s" + DATES + ",?\\s?" + YEARS + "(?=\\s|$)";
	
	private static final String DATE_MONTH_FORMAT = "d MMM yyyy";
	
	protected static final String FLEXIBLE_DATE_REGEX = "(" + DATE_MONTH_REGEX + "|" + MONTH_DATE_REGEX + ")";
	
	private static final Pattern DMMM = Pattern.compile(DATE_MONTH_REGEX);
	private static final Pattern MMMD = Pattern.compile(MONTH_DATE_REGEX);
	private static final String STANDARD_FLEXIBLE_DATE_FORMAT = "d MMM yyyy";

	private static final Logger logger = Logger.getLogger(FlexibleDateTimeParser.class.getName() );
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_YEAR_ARR = 2;

	/*public static void getMonthFromString(String input) {
		Matcher m = DMMM.matcher(input);
		while (m.find()) {
			System.out.println(m.group(3));
		}
	}*/
	
	private static String[] getDateMonthFromString(String input) {
		input = input.toLowerCase();
		Matcher m = DMMM.matcher(input);
		String[] dateArr = new String[4];
		dateArr[2] = DATE_MONTH_FORMAT;
		String[][] temp = new String[2][3];
		int i = 0;
		String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		while (m.find() & i < 2) {
			String date = m.group(DM_INDEX_FOR_DATE);
			String month = m.group(DM_INDEX_FOR_MONTH);
			String year = m.group(DM_INDEX_FOR_YEAR);
			temp[i][0] = date;
			temp[i][1] = month.substring(0,3);
			temp[i][2] = year;
			
			setEndYear(temp, i, currentYear);
			i++;
		}
		
		setStartYear(temp, currentYear);
		
		convertDateArrayToStdForm(dateArr, temp);
		
		dateArr[3] = removeDateMonthFromString(input);
		// System.out.println(dateArr[0]);
		// System.out.println(dateArr[1]);
		return dateArr;
	}

	private static void convertDateArrayToStdForm(String[] dateArr, String[][] temp) {
		if (temp[0][0] == null) { // has no start date
			dateArr[0] = null; // no flexible date detected
			dateArr[1] = null;
		} else { // flexible date detected
			dateArr[0] = temp[0][0] + " " + temp[0][1] + " " + temp[0][2]; // standard flexible date
			if (temp[1][0] == null) { // no end date detected
				dateArr[1] = null;
			} else {
				dateArr[1] = temp[1][0] + " " + temp[1][1] + " " + temp[1][2];		
			}
		}
	}

	private static void setStartYear(String[][] temp, String currentYear) {
		if (temp[0][2] == null) { // start date has no year
			setNextValidYear(temp[0]); // set to current year
		}
	}
	
	private static void setNextValidYear(String[] date) {
		LocalDate today = LocalDate.now();
		String currYear = String.valueOf(today.getYear());
		String newDateString = currYear + "-" + date[INDEX_FOR_MONTH_ARR] + "-" + date[INDEX_FOR_DATE_ARR];
		// System.out.println("Before formatting year: " + newDateString + ".");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-d"); //LocalDate.parse() MMM case sensitive??!
	    try {
			Date tempDate = format.parse(newDateString);
		    LocalDate newDate = tempDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (newDate.isAfter(today)) {
				date[INDEX_FOR_YEAR_ARR] = currYear;
			} else {
				date[INDEX_FOR_YEAR_ARR] = String.valueOf(today.getYear() + 1);
			}
		} catch (ParseException e){
			date = null;
		}
	}

	private static void setEndYear(String[][] temp, int i, String currentYear) {
		if (i == 1) { // end date
			if (temp[i][2] != null) { // has year
				if (temp[0][2] == null) { // start date has no year
					temp[0][2] = temp[i][2]; // set start year = end year
				}
			} else { // end date has no year
				setNextValidYear(temp[1]);
			}
		}
	}
		
	private static String[] getMonthDateFromString(String input) {
		String[][] temp = new String[2][3];
		String[] dateArr = new String[4];
		int i = 0;
		String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		
		input = input.toLowerCase();
		Matcher m = MMMD.matcher(input);
		dateArr[2] = DATE_MONTH_FORMAT;
		
		// System.out.println(input);
		
		while (m.find() & i < 2) {
			String date = m.group(MD_INDEX_FOR_DATE);
			String month = m.group(MD_INDEX_FOR_MONTH);
			String year = m.group(MD_INDEX_FOR_YEAR);
			temp[i][0] = date;
			temp[i][1] = month.substring(0,3);
			temp[i][2] = year;
			
			setEndYear(temp, i, currentYear);
			
			i++;
		}
		
		setStartYear(temp, currentYear);
		
		convertDateArrayToStdForm(dateArr, temp);
		dateArr[3] = removeMonthDateFromString(input);
		// System.out.println(dateArr[0]);
		// System.out.println(dateArr[1]);
		
		return dateArr;
	}
	
	private static String removeDateMonthFromString(String input) {
		return StringParser.removeRegexPatternFromString(input, DATE_MONTH_REGEX);
	}
	
	private static String removeMonthDateFromString(String input) {
		return StringParser.removeRegexPatternFromString(input, MONTH_DATE_REGEX);
	}
	
	@Override
	protected String[] getParsedDates() {
		return flexibleDates;
	}
	
	@Override
	protected String[] getParsedTimes() {
		return flexibleTimes;
	}
	
	@Override
	protected String getUnparsedInput() {
		return unparsedInput;
	}
	
	@Override
	protected Calendar[] parse(String input, String[] parsedDates, String[] parsedTimes) {
		flexibleTimes = parsedTimes; // all time parsing done previously
		flexibleDates = getStandardFlexibleDates(input);
		unparsedInput = flexibleDates[3];
		return convertStringToCalendar(flexibleDates, flexibleTimes);
	}

	public static String[] getStandardFlexibleDates(String input) {
		String[] dates = new String[4];
		dates = getMonthDateFromString(input);
		if (dates[0] == null) { // no MMM d detected
			dates = getDateMonthFromString(input);
		}
		dates[2] = STANDARD_FLEXIBLE_DATE_FORMAT;
		return dates;
	}
	
	
}
