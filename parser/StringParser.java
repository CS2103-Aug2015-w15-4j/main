package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import parser.ParsedCommand.TaskType;

public class StringParser {
	protected static final String TASK_ID_REGEX = "(^[0-9]+(?=\\s|$))";
	protected static final String TAG_REGEX = "(#(\\w+))";
	protected static final String DESCRIPTION_REGEX = "(\"[^\"]*?\")";
	protected static final String TASK_STATUS_REGEX = "(?<=[^//s])(todo|completed|overdue)(?=\\s|$)";
	protected static final String TASK_TYPE_REGEX = "(?<=\\s|^)(floating(?:task)?|deadline(?:task)?|event(?:task)?)(?:s)?(?=\\s|$)";
	
	private static Pattern description = Pattern.compile(DESCRIPTION_REGEX);
	private static Pattern taskId = Pattern.compile(TASK_ID_REGEX);
	protected static Pattern tags = Pattern.compile(TAG_REGEX);
	protected static Pattern taskStatus = Pattern.compile(TASK_STATUS_REGEX);
	private static final Pattern taskType = Pattern.compile(TASK_TYPE_REGEX);
	
	private static final String notTitleRegex = "(" + "( from | fr | at | to | til | until | by | on )?" + DateTimeParser.DATE_TIME_REGEX + "|" + TAG_REGEX + "|" + DESCRIPTION_REGEX  + "|(" + TASK_STATUS_REGEX + "))";  	
	
	// private static final Logger logger = Logger.getLogger(StringParser.class.getName() );
	private static final String NOT_KEYWORDS_REGEX = notTitleRegex + "|" + TASK_TYPE_REGEX;
	
	// Used for testing purposes
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
}
