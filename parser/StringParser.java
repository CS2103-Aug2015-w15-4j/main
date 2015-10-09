package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class StringParser {
	private static final String DD = "(0?[1-9]|[12][0-9]|3[01])";
	private static final String MM = "(0?[1-9]|1[012])";
	private static final String YY = "(\\d\\d)";
	private static final String DATE_DELIM = "([-/.])";
	private static final String WHITESPACES = "\\s*";
	private static final String TIME_REGEX = "(@((0[0-9]|1[0-9]|2[0-3])([0-5][0-9]))(-((0[0-9]|1[0-9]|2[0-3])([0-5][0-9])))?)";
	private static final String TAG_REGEX = "(#(\\w+))";
	private static final String DESCRIPTION_REGEX = "(\"[^\"]*?\")";
	private static final String NATTY_REGEX = "([(]([a-zA-Z0-9.\\s]+)[)])";
	
	private static Pattern quotes = Pattern.compile("\"([^\"]*)\"");
	private static Pattern taskId = Pattern.compile("(^[0-9]+)");
	
	private static Pattern tags = Pattern.compile(TAG_REGEX);
	
	private static String notTitleRegex = "(" + WHITESPACES + "(" + DD + DATE_DELIM + MM + DATE_DELIM + YY + ")" + WHITESPACES + "|" +
                                           TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX + "|" + NATTY_REGEX + ")";  	                                        
	
	// Note that second date is currently unnecessary, assumption that events do
	// not span across days
	
	public static Date parseStringToDate(String input) {
		Date date = new Date();
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss zzz yyyy");
			date = format.parse(input);
			return date;
		} catch (ParseException | java.text.ParseException pe) {
			throw new IllegalArgumentException();
		}
	}
	
    // returns null if not found
	public static String getTitleFromString(String inputArgs) {
		String regex = notTitleRegex;
		inputArgs = inputArgs.replaceAll(regex, "");
		if (inputArgs.equals("")) {
			return null;
		}
		return inputArgs.trim();
	}

	public static String getDescriptionFromString(String inputArgs) {
		Matcher m = quotes.matcher(inputArgs);
		String description = null;

		if (m.find()) {
			description = m.group();
		}

		if (description != null) {
			return description.substring(1, description.length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * Parses string to return taskId (integer at beginning of string), returns 0 if not found.
	 * @param inputArgs
	 * @return
	 */
	public static int getTaskIdFromString(String inputArgs) {
		Matcher m = taskId.matcher(inputArgs);
		int taskId = -1;

		if (m.find()) {
			taskId = Integer.parseInt(m.group());
		}

		return taskId;
	}

	/**
	 * Parses string and returns ArrayList of tags (alphanumeric with no whitespace), 
	 * returns empty ArrayList if not found.
	 * 
	 * @param inputArgs
	 * @return
	 */
	public static ArrayList<String> getTagsFromString(String inputArgs) {
		Matcher m = tags.matcher(inputArgs);
		ArrayList<String> tags = new ArrayList<String>();

		while (m.find()) {
			String tag = m.group().substring(1);
			tags.add(tag);
		}

		return tags;
	}
	
	public static Calendar[] getDatesTimesFromString(String input) {
		DateTimeParser dtp;
		if (DateTimeParser.isNattyDateTime(input)) {
			dtp = new NattyDateTimeParser();
		} else {
			dtp = new FormattedDateTimeParser();
		}
		return dtp.getDatesTimes(input);
	}
}
