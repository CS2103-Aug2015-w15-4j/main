package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
	private static Pattern ddmmyy = Pattern.compile("(\\s(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d))\\s");
	
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
		
		while (m.find()) {
			ans[i] = m.group().trim();
			i++;
		}
		
		return ans;
	}
	
}
