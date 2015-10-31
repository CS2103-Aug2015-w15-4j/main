package parser;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public class InputParserTest {
	
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
		
	}

	@Test
	public void testCreateParsedCommandError() {
		ParsedCommand pc = InputParser.createParsedCommandError("ERROR");
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("ERROR", pc.getErrorMessage());
		
		pc = InputParser.createParsedCommandError(null);
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Missing error message", pc.getErrorMessage());
		
		pc = InputParser.createParsedCommandError("");
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Missing error message", pc.getErrorMessage());
	}

	@Test
	public void testCreateParsedCommand() {
		ParsedCommand pc = InputParser.createParsedCommand(CommandType.UNDO);
		assertEquals(CommandType.UNDO, pc.getCommandType());
		
		pc = InputParser.createParsedCommand(CommandType.ADD);
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Missing task title", pc.getErrorMessage());
		
		pc = InputParser.createParsedCommand(null);
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Missing command type for building ParsedCommand", pc.getErrorMessage());
		
	}

	@Test
	public void testIsMissingArguments() {
		assertEquals(true, InputParser.isMissingArguments(new String[] {""}));
		assertEquals(false, InputParser.isMissingArguments(new String[] {"", ""}));
		assertEquals(false, InputParser.isMissingArguments(new String[] {null, null, ""}));
	}
	
	@Test
	public void testGetTitleFromString() {
		assertEquals("hello this is my task", InputParser.getTitleWithKeywordsFromString(" hello this is my task 12:00 \"description\" #tags #tag1"));
		assertEquals("13:30hello this is my task", InputParser.getTitleWithKeywordsFromString("13:30hello this is my task"));
		assertEquals("hello this is   my task", InputParser.getTitleWithKeywordsFromString(" hello this is 12/12/12 5pm my task"));
		assertEquals("hello  task", InputParser.getTitleWithKeywordsFromString(" hello \"this is my\" task 12:00"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("12.2-13 Meet John about proposal #cs2103 12:00h"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("#cs2101 Meet John about proposal on 23 jan #cs2103 12:00 12/2/13"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString(" 12:00 12/2/13 Meet John about proposal #cs2103 todo"));
		assertEquals(null, InputParser.getTitleWithKeywordsFromString("23/1/15 2pm \"description\" #tag1 #tag2"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("#cs2101 Meet John about proposal 12:00 to 1:30pm 12/2/13"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("#cs2101 Meet John about proposal #cs2103 12:00 until 15:30 12/2/13"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("#cs2101 Meet John about proposal 23 jan #cs2103 from 12:00 to 12/2/13"));
		assertEquals("Meet John about proposal", InputParser.getTitleWithKeywordsFromString("#cs2101 Meet John about proposal 23 jan #cs2103 at 12:00 to 12/2/13"));	
	}

	@Test
	public void testGetDatesTimesFromString() {
		// Check supported formats
		
		// Check support for no date & time
		assertEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[0]);
		assertEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[1]);
		
		// Check support for flexible start date no time
		assertEquals(parseStringToDate("Fri Apr 1 23:59:00 SGT 2016"), InputParser.getDatesTimesFromString("Add meeting with john on 1st April")[0].getTime());
		assertEquals(null, InputParser.getDatesTimesFromString("Add meeting with john on 1st April")[1]);

		// Check support for natty start date no time, keyword
		LocalDateTime dt = LocalDateTime.now();
		dt = LocalDateTime.from(dt.plusDays(1));
		dt = dt.withHour(23).withMinute(59).withSecond(0);
		assertEquals(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()).toString(), InputParser.getDatesTimesFromString("finish homework by tmr")[0].getTime().toString());
		assertEquals(null, InputParser.getDatesTimesFromString("finish homework by tmr")[1]);
		
		// Check support for formatted input (has start and end time) out of order
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				InputParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 13:30:00 SGT 2013"), 
				InputParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00h -13:30 H")[1].getTime());		
		
		// Check support for flexible start date and time, keyword
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				InputParser.getDatesTimesFromString("Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101")[1]);
		
		// Check support for flexible start date and time, no keyword
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				InputParser.getDatesTimesFromString("Meet John about proposal feb 12 2013 12:00 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal feb 12 2013 12:00 #cs2103 #cs2101")[1]);
		
		// NO SUPPORT FOR NATTY EVENT!
		// Check support for Natty input start date & time
		dt = LocalDateTime.now();
		dt = LocalDateTime.from(dt.plusDays(1));
		dt = dt.withHour(14).withMinute(0).withSecond(0);
		assertEquals(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()).toString(), InputParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2103 #cs2101")[0].getTime().toString());
		assertEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2101")[1]);
				
		// Check invalid time for Natty input
		assertArrayEquals(null, InputParser.getDatesTimesFromString("Meet John about proposal by tmr 32pm #cs2101"));
				
		// Check support for flexible invalid date
		assertArrayEquals(null, InputParser.getDatesTimesFromString("Add meeting with john on 31st April"));

		// Check support for formatted input invalid time
		assertArrayEquals(null, InputParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 52:00-13:30"));		
				
		// Auto-add date should be upcoming date, not current year
		// Check invalid date detection
		// Natty cannot handle events!!!
		
		// Remove from/to etc from title for no keyword
		// Consider support for if only time indicated, assume is today
				
	}

	@Test
	public void testGetDescriptionFromString() {
		assertEquals("and this my description", InputParser.getDescriptionFromString("this is my task \"and this my description\" blah blah 23/3/12"));
		assertEquals("my desc\" #cs2101 \"lalala", InputParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101 \"lalala\""));
		assertEquals(null, InputParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 #cs2101"));
	}

	@Test
	public void testGetTaskIdFromString() {
		// Check getTaskId returns integer at front of line
		assertEquals(4242, InputParser.getTaskIdFromString("4242 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		
		// Check invalid/missing taskId returns -1
		assertEquals(-1, InputParser.getTaskIdFromString("Meet Task John about proposal @1200 #cs2103 #cs2101"));
		assertEquals(-1, InputParser.getTaskIdFromString("this is my 23 task \"and this my description\" blah blah 23/3/12"));
	}

	@Test
	public void testGetTagsFromString() {
		assertEquals("cs2103", InputParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(0));
		assertEquals("cs2101", InputParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(1));
		assertEquals(new ArrayList<String>(), InputParser.getTagsFromString("Meet Task John about proposal @1200"));
	}
	
	@Test
	public void testGetTaskStatusFromString() {
		assertEquals(false, InputParser.getTaskStatusFromString("Meet Task John about proposal #cs2103 @1200 #cs2101 todo"));
		assertEquals(false, InputParser.getTaskStatusFromString("meeting 23/11/15 #tag todo"));
	}
	
	@Test
	public void testGetTaskTypeFromString() {
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("floating task"));
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("floating tasks"));
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("lalala floating task lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalafloating task lalala"));

		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadline task"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadline tasks"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadlines"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadline"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("lalala deadline task lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaladeadline task lalala"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("lalala deadlines lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaladeadline lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("deadlineself"));

		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("event"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("events"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("lalala event lalala"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("lalala events lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaevent lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("eventsal"));

	}

}
