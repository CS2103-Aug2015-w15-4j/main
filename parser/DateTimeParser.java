package parser;

import java.util.Calendar;

public abstract class DateTimeParser {
	public static boolean isFormattedDateTime(String input) {
		return true;
	}
	
	public static boolean isNattyDateTime(String input) {
		return false;
	}
	
	abstract Calendar[] getDatesTimes(String input);
}
