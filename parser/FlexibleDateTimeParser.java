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

public class FlexibleDateTimeParser extends DateTimeParser {
	private String[] flexibleDates;
	private String[] flexibleTimes;
	private String unparsedInput;
	
	private enum DateFormat {
		DATE_MONTH, MONTH_DATE;
	}
	
	private static final String CURRENT_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	
	private static final String MONTHS = "(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)";
	private static final String DATES = "(\\d?\\d)(?:st|rd|nd|th)?";
	private static final String YEARS = "(\\d{4})?";
	
	private static final String DATE_MONTH_REGEX = "(?<=\\s|^)" + DATES + "\\s" + MONTHS + ",?\\s?" + YEARS + "(?=\\s|$)";
	private static final String MONTH_DATE_REGEX = "(?<=\\s|^)" + MONTHS + "\\s" + DATES + ",?\\s?" + YEARS + "(?=\\s|$)";
	
	private static final Pattern DMMM = Pattern.compile(DATE_MONTH_REGEX);
	private static final Pattern MMMD = Pattern.compile(MONTH_DATE_REGEX);
	
	private static final String STANDARD_FLEXIBLE_DATE_FORMAT = "d MMM yyyy";
	
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
	
	private static String[] getDateMonthFromString(String input) {
		String[][] temp = parseStringToDateArray(input, DateFormat.DATE_MONTH);
		String[] dateArr = convertDateArrayToStdForm(temp);
		setDateFormatToFlexDateFormat(dateArr);
		setUnparsedInput(DateFormat.DATE_MONTH, input, dateArr);
		// System.out.println(dateArr[0]);
		// System.out.println(dateArr[1]);
		logger.log(Level.FINE, "DM start: " + dateArr[0] + " end: " + dateArr[1] + " format: " + dateArr[2] + " unparsed: " + dateArr[3]);
		return dateArr;
	}

	private static String[] getMonthDateFromString(String input) {
		String[][] tempDateArr = parseStringToDateArray(input, DateFormat.MONTH_DATE);
		String[] dateArr = convertDateArrayToStdForm(tempDateArr);
		setDateFormatToFlexDateFormat(dateArr);
		setUnparsedInput(DateFormat.MONTH_DATE, input, dateArr);
		// System.out.println(dateArr[0]);
		// System.out.println(dateArr[1]);
		logger.log(Level.FINE, "MD start: " + dateArr[0] + " end: " + dateArr[1] + " format: " + dateArr[2] + " unparsed: " + dateArr[3]);		
		return dateArr;
	}

	
	private static void updateDateArray(String[][] temp, int i, String date, String month, String year) {
		temp[i][INDEX_FOR_DATE_ARR] = date;
		temp[i][INDEX_FOR_MONTH_ARR] = month.substring(0,3);
		temp[i][INDEX_FOR_YEAR_ARR] = year;
	}

	private static String[] convertDateArrayToStdForm(String[][] temp) {
		String[] dateArr = new String[4];
		if (temp[INDEX_FOR_START][INDEX_FOR_DATE_ARR] == null) { // has no start date
			setStartAndEndDate(dateArr, null, null);
		} else { // flexible date detected
			String startDate = dateArrToStdString(temp, INDEX_FOR_START); // standard flexible date
			String endDate;
			if (temp[INDEX_FOR_END][INDEX_FOR_DATE_ARR] == null) { // no end date detected
				endDate = null;
			} else {
				endDate = dateArrToStdString(temp, INDEX_FOR_END);		
			}
			setStartAndEndDate(dateArr, startDate, endDate);
		}
		return dateArr;
	}

	private static void setStartAndEndDate(String[] dateArr, String startDate, String endDate) {
		dateArr[INDEX_FOR_START] = startDate;
		dateArr[INDEX_FOR_END] = endDate;
	}

	private static String dateArrToStdString(String[][] temp, int dateIndex) {
		return temp[dateIndex][INDEX_FOR_DATE_ARR] + " " + temp[dateIndex][INDEX_FOR_MONTH_ARR] + " " 
				+ temp[dateIndex][INDEX_FOR_YEAR_ARR];
	}
	
	private static void setYears(String[][] temp) {
		if (temp[INDEX_FOR_END][INDEX_FOR_YEAR_ARR] != null) { // has year
			if (temp[INDEX_FOR_START][INDEX_FOR_YEAR_ARR] == null) { // start date has no year
				temp[INDEX_FOR_START][INDEX_FOR_YEAR_ARR] = temp[INDEX_FOR_END][INDEX_FOR_YEAR_ARR]; // set start year = end year
			}
		} else { // end date has no year
			if (temp[INDEX_FOR_START][INDEX_FOR_YEAR_ARR] != null) { // start date has year
				temp[INDEX_FOR_END][INDEX_FOR_YEAR_ARR] = temp[INDEX_FOR_START][INDEX_FOR_YEAR_ARR]; // set end year = start year
			} else {
				setNextValidYear(temp[INDEX_FOR_END]);
				setNextValidYear(temp[INDEX_FOR_START]); 
			}			
		}
	}
		
	private static void setNextValidYear(String[] date) {
		LocalDate today = LocalDate.now();
		String newDateString = CURRENT_YEAR + "-" + date[INDEX_FOR_MONTH_ARR] + "-" + date[INDEX_FOR_DATE_ARR];
		// System.out.println("Before formatting year: " + newDateString + ".");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-d"); //LocalDate.parse() MMM case sensitive??!
	    try {
			Date tempDate = format.parse(newDateString);
		    LocalDate newDate = tempDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (newDate.isAfter(today)) {
				date[INDEX_FOR_YEAR_ARR] = CURRENT_YEAR;
			} else {
				date[INDEX_FOR_YEAR_ARR] = String.valueOf(today.getYear() + 1);
			}
		} catch (ParseException e){
			date = null;
		}
	}

	private static String[][] parseStringToDateArray(String input, DateFormat df) {
		input = input.toLowerCase();
		String[][] temp = new String[2][3];
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
			updateDateArray(temp, i, date, month, year);
			i++;
		}
		setYears(temp);
		return temp;
	}

	
	private static String removeDateMonthFromString(String input) {
		return StringParser.removeRegexPatternFromString(input, DATE_MONTH_REGEX);
	}
	
	private static String removeMonthDateFromString(String input) {
		return StringParser.removeRegexPatternFromString(input, MONTH_DATE_REGEX);
	}
	
	private static void setUnparsedInput(DateFormat df, String input, String[] dateArr) {
		if (df == DateFormat.MONTH_DATE) {
			dateArr[3] = removeMonthDateFromString(input);
		} else if (df == DateFormat.DATE_MONTH) {
			dateArr[3] = removeDateMonthFromString(input);
		}
	}
	
	private static void setDateFormatToFlexDateFormat(String[] dateArr) {
		dateArr[2] = STANDARD_FLEXIBLE_DATE_FORMAT;
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
		if (dates[INDEX_FOR_START] == null) { // no MMM d detected
			dates = getDateMonthFromString(input);
		}
		dates[2] = STANDARD_FLEXIBLE_DATE_FORMAT;
		return dates;
	}
	
	
}
