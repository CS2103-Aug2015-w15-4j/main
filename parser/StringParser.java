package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
	private static Pattern ddmmyy = Pattern.compile("(\\s(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d))\\s");
	private static Pattern hhmm = Pattern.compile("@((0[0-9]|1[0-9]|2[0-3])([0-5][0-9]))(-((0[0-9]|1[0-9]|2[0-3])([0-5][0-9])))?");
	
	/** 
	 * This method looks for dd/mm/yy substrings of userInput, single digit inputs allowed for date and month.
	 *
	 * @param userInput Unparsed command arguments input by user.
	 * @return String array where ans[0] = first date found (if any) and ans[1] = second date found (if any),
	 * 		   If no dates are found, ans[0] = ans[1] = null and if only one date found, ans[1] = null.
	 */
	public static String[] getFormattedDates(String userInput) {		
		Matcher m = ddmmyy.matcher(userInput); 
		String[] ans = new String[2];
		
		int i = 0;
		
		while (m.find() & i < 2) {
			ans[i] = m.group().trim();
			i++;
		}
		
		return ans;
	}
	
	/** 
	 * This method looks for @HHMM-HHMM substrings of userInput, second time is optional.
	 *
	 * @param userInput Unparsed command arguments input by user.
	 * @return String array where ans[0] = start time (if any) (in HHMM format) and ans[1] = end time 
	 * 		   (if any)(in HHMM format), if no time, value is null; end time can only exists if start time 
	 *         exists.
	 */
	public static String[] getFormattedTimes(String userInput) {
		Matcher m = hhmm.matcher(userInput); 
		String[] ans = new String[2];
		
		if (m.find()) {
			ans[0] = m.group(1);
			ans[1] = m.group(5);
		}
		
		return ans;
	}
	
}
