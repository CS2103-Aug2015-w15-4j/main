package parser;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import parser.MyParser.CommandType;

public class ShowParserTest {
	public static Date parseStringToDate(String input) {
		Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss zzz yyyy");
			try {
				date = format.parse(input);
			} catch (ParseException e) {
				return null;
			}
			return date;
	}
	
	@Test
	public void testParse() {
		fail("Not yet implemented");
	}

	@Test
	public void testContainsOnlyTaskId() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateParsedCommandShowSearch() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("tag");
		ParsedCommand pc = ShowParser.createParsedCommandShowSearch(" my search keywords from 1/4/16 3pm to 5pm #tag \"description\"");
		assertEquals(CommandType.SEARCH, pc.getCommandType());
		assertEquals("my search keywords", pc.getKeywords());
		assertEquals(parseStringToDate("Fri Apr 1 15:00:00 SGT 2016"), pc.getFirstDate().getTime());
		assertEquals(parseStringToDate("Fri Apr 1 17:00:00 SGT 2016"), pc.getSecondDate().getTime());
		assertEquals(tags, pc.getTags());		

		pc = ShowParser.createParsedCommandShowSearch(" my search keywords #tag \"description\"");
		assertEquals(CommandType.SEARCH, pc.getCommandType());
		assertEquals("my search keywords", pc.getKeywords());
		assertEquals(null, pc.getFirstDate());
		assertEquals(null, pc.getSecondDate());
		assertEquals(tags, pc.getTags());		

		pc = ShowParser.createParsedCommandShowSearch(" my search keywords on apr 1 #tag \"description\"");
		assertEquals(CommandType.SEARCH, pc.getCommandType());
		assertEquals("my search keywords", pc.getKeywords());
		assertEquals(parseStringToDate("Fri Apr 1 00:00:00 SGT 2016"), pc.getFirstDate().getTime());
		assertEquals(parseStringToDate("Fri Apr 1 23:59:00 SGT 2016"), pc.getSecondDate().getTime());
		assertEquals(tags, pc.getTags());		

		pc = ShowParser.createParsedCommandShowSearch(" my search keywords from 1/4/16 3pm to 2/4/16 5pm #tag \"description\"");
		assertEquals(CommandType.SEARCH, pc.getCommandType());
		assertEquals("my search keywords", pc.getKeywords());
		assertEquals(parseStringToDate("Fri Apr 1 15:00:00 SGT 2016"), pc.getFirstDate().getTime());
		assertEquals(parseStringToDate("Sat Apr 2 17:00:00 SGT 2016"), pc.getSecondDate().getTime());
		assertEquals(tags, pc.getTags());		


		pc = ShowParser.createParsedCommandShowSearch(" my search keywords from 31/4/16 3pm to 5pm #tag \"description\"");
		assertEquals(CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Invalid date(s) input", pc.getErrorMessage());
	}

	@Test
	public void testCreateParsedCommandShowDisplay() {
		ParsedCommand pc = ShowParser.createParsedCommandShowDisplay(234);
		assertEquals(MyParser.CommandType.DISPLAY, pc.getCommandType());
		assertEquals(234, pc.getTaskId());
		
		pc = ShowParser.createParsedCommandShowDisplay(0);
		assertEquals(MyParser.CommandType.DISPLAY, pc.getCommandType());
		assertEquals(0, pc.getTaskId());

		pc = ShowParser.createParsedCommandShowDisplay(-1);
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pc.getErrorMessage());

	}

}
