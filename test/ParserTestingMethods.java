//@@author A0114620X

package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ParserTestingMethods {
	//@@author A0114620X reused
	static void initLogging(){
		String config = "\nhandlers = java.util.logging.ConsoleHandler\n.level = ALL\n" +
						"java.util.logging.ConsoleHandler.level = FINE\n" +
						"com.sun.level = INFO\n" +
						"javax.level = INFO\n" +
						"sun.level = INFO\n";
	
		InputStream ins = new ByteArrayInputStream(config.getBytes());
	
		Logger logger = Logger.getLogger(MyParserTest.class.getName());
		try {
			LogManager.getLogManager().readConfiguration(ins);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Log manager configuration failed: " + e.getMessage(),e);
		}
	}
		
	//@@author A0114620X
	static Calendar nextDayOfWeek(int day) {
        Calendar date = Calendar.getInstance();
        int difference = day - date.get(Calendar.DAY_OF_WEEK);
        if (difference < 0) {
            difference += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, difference);
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 0);
        return date;
    }
	
	static Date parseStringToDate(String input) {
		if (input == null) {
			return null;
		}
		Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("d/M/yy HH:mm");
			try {
				date = format.parse(input);
			} catch (java.text.ParseException e) {
				return null;
			}
			return date;
	}
	
	static String inNDays(int n, int h, int m) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy HH:mm");
		LocalDateTime date = LocalDateTime.now();
		date = LocalDateTime.from(date.plusDays(n));
		date = date.withHour(h).withMinute(m).withSecond(0);
		return date.format(formatter);
	}
	
	static Date parseNDaysToDate(int n, int h, int m) {
		String dateString = inNDays(n, h, m);
		return parseStringToDate(dateString);
	}


}
