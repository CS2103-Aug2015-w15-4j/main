package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedDateTimeParser extends DateTimeParser{
	
	protected static final String DD = "(0?[1-9]|[12][0-9]|3[01])";
	protected static final String MM = "(0?[1-9]|1[012])";
	protected static final String YY = "(\\d\\d))";
	protected static final String DATE_DELIM = "([-/.])";
	protected static final String FORMATTED_DATE_REGEX = "(\\s" + DD + DATE_DELIM + MM + DATE_DELIM + YY + "\\s";
	protected static final String FORMATTED_DATE_FORMAT = "d/M/yy";
	private static Pattern ddmmyy = Pattern.compile(FORMATTED_DATE_REGEX);
	private static Pattern dateDelim = Pattern.compile(DATE_DELIM);
	
	private String[] formattedTimes;
	private String[] formattedDates;
	private String unparsedInput;

	public static String convertDateToStandardFormat(String date) {
		date = dateDelim.matcher(date).replaceAll("/");
		return date;
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
		Matcher m = ddmmyy.matcher(" " + userInput + " ");
		String[] ans = new String[4];

		int i = 0;

		while (m.find() & i < 2) {
			ans[i] = convertDateToStandardFormat(m.group().trim());
			i++;
		}
		ans[2] = FORMATTED_DATE_FORMAT;
		ans[3] = removeFormattedDatesFromString(userInput);
		return ans;
	}
	
	public static String removeFormattedDatesFromString(String input) {
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
		formattedTimes = TimeParser.getStandardTimesFromString(input); // all time parsing done
		formattedDates = getStandardFormattedDates(formattedTimes[3]);
		unparsedInput = formattedDates[3];
		return convertStringToCalendar(formattedDates, formattedTimes);
	}
}
