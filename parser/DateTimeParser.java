package parser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DateTimeParser {
	private static Pattern parantheses = Pattern.compile("([(][a-zA-Z0-9\\s]+[)])");
	
	public static boolean isNattyDateTime(String inputArgs) {
		Matcher m = parantheses.matcher(inputArgs);
		String nattyDate = null;

		if (m.find()) {
			nattyDate = m.group();
		}

		if (nattyDate != null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isFormattedDateTime(String input) {
		return true;
	}
	
	abstract Calendar[] getDatesTimes(String input);
}
