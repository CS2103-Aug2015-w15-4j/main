//@@author A0114620X

package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import parser.InputParser;
import parser.MyParser;
import parser.ParsedCommand;
import parser.MyParser.CommandType;
import parser.ParsedCommand.TaskType;

public class InputParserTest extends InputParser {

	private static void checkTitleWithKeywords(String expected, String input) {
		assertEquals(expected, InputParser.getTitleWithDateKeywords(input));
	}
	
	private static void checkRemoveDateKeywords(String expected, String received) {
		assertEquals(expected, InputParser.removeDateKeywordSection(received));
	}

	private static void checkStdDatesTimes(String expectedStart, String expectedEnd, String received) {
		if (expectedStart == null) {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedStart), 
					     InputParser.getStandardDatesTimes(received)[0]);
		} else {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedStart), 
					     InputParser.getStandardDatesTimes(received)[0].getTime());
		}
		
		if (expectedEnd == null) {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedEnd), 
					     InputParser.getStandardDatesTimes(received)[1]);
		} else {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedEnd), 
					     InputParser.getStandardDatesTimes(received)[1].getTime());
		}
	}
	
	private static void checkStdDatesTimesError(String input) {
		assertArrayEquals(null, InputParser.getStandardDatesTimes(input));
	}
	
	private static void checkSearchDatesTimes(String expectedStart, String expectedEnd, String input) {
		if (expectedStart == null) {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedStart), 
					     InputParser.getSearchDatesTimes(input)[0]);
		} else {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedStart), 
					     InputParser.getSearchDatesTimes(input)[0].getTime());
		}
		
		if (expectedEnd == null) {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedEnd), 
					     InputParser.getSearchDatesTimes(input)[1]);
		} else {
			assertEquals(ParserTestingMethods.parseStringToDate(expectedEnd), 
					     InputParser.getSearchDatesTimes(input)[1].getTime());
		}
	}
	
	private static void checkSearchDatesTimesError(String input) {
		assertArrayEquals(null, InputParser.getSearchDatesTimes(input));
	}
	
	private static void checkDescription(String expected, String input) {
		assertEquals(expected, InputParser.getDescriptionFromString(input));
	}
	
	@Before
	public void setUp() throws Exception {
	    ParserTestingMethods.initLogging();
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
	public void testGetTitleWithKeywordsFromString() {
		// Check null input
		checkTitleWithKeywords(null, null);
		// Check empty string
		checkTitleWithKeywords("", "");
		
		
		// Check dates, times, descriptions and tags removed
		
		// formatted date, date keywords removed
		checkTitleWithKeywords("hello this is     my task", " hello this is By 12/12/12 5pm until 13/12/12 8am my task");
		checkTitleWithKeywords("hello  task \\12:00pm", " hello \"this is my\" task \\12:00pm 24/2");
		checkTitleWithKeywords("\\12.2-13 Meet John about proposal", "\\12.2-13 Meet John about proposal #cs2103 12:00h");
		checkTitleWithKeywords("Meet John about proposal", "#cs2101 Meet John about proposal on 23 jan #cs2103 at 12:00 12-2.13");
		checkTitleWithKeywords("", "23/1/15 2pm \"description\" #tAg1 #tag2");
		checkTitleWithKeywords("Meet John about proposal", "#cs2101 Meet John about proposal #cs2103 12:00 until 15:30 12/2/13");
		
		// flexible date, date keywords removed
		checkTitleWithKeywords("hello this is my task", " hello this is my task 2 feb 12:00 \"description\" #tags #tag1");
		checkTitleWithKeywords("Meet John about proposal \\23 Jan", "#cs2101 Meet John about proposal \\23 Jan #cs2103 from 12:00");
				
		// natty, date keywords not removed except for tmr
		checkTitleWithKeywords("watch", "watch tmr");
		checkTitleWithKeywords("watch on \\tmr", "watch on \\tmr");
		checkTitleWithKeywords("watch", "watch on Tomorrow");
		checkTitleWithKeywords("watch movie On next Fri", "watch movie On next Fri");

		// task status, type not removed
		checkTitleWithKeywords("lalala todo done floating deadline event", "lalala todo done floating deadline event");
	}
	
	@Test
	public void testRemoveDateKeywordSection() {
		checkRemoveDateKeywords("", "");
		checkRemoveDateKeywords(null, null);
		
		// Check string after keywords removed
		checkRemoveDateKeywords("", "on hello");
		checkRemoveDateKeywords("", "by lalala");
		
		// Check last possible section is removed when more than one keyword
		checkRemoveDateKeywords("on something by something", "on something by something by another");
		checkRemoveDateKeywords("by something", "by something on lala");
		
		// Check escape characters work
		checkRemoveDateKeywords("\\on hello \\by bye bye", "\\on hello \\by bye bye");
		
		// Check keywords must not be part of another word
		checkRemoveDateKeywords("bye bacon", "bye bacon");
		
	}

	@Test
	public void testGetStdDatesTimesFromString() {
		// Check supported formats
		
		// Check support for no date & time
		checkStdDatesTimes(null, null, "Meet John about proposal 1200 #cs2103 #cs2101");
		
		// Check support for formatted input (has start and end time) out of order
		checkStdDatesTimes("12/2/13 12:00", "12/2/13 13:30", "12/2/13 Meet John about proposal #cs2103 12:00-13:30");
		
		// Check support for formatted input no year, year set such that date is nearest upcoming date
		checkStdDatesTimes("12/2/16 12:00", "12/2/16 13:30", "12/2 Meet John about proposal #cs2103 12 until 1:30pm");
		
		// Check support for formatted input, event with no times, default start 0000, default end 2359
		checkStdDatesTimes("12/2/16 00:00", "14/2/16 23:59", "12/2 Meet John about proposal #cs2103 14/2");
				
		// Check support for flexible start date no time, default set to 23:59
		checkStdDatesTimes("1/4/2016 23:59", null, "Add meeting with john on 1st April");
				
		// Check support for flexible start date and time, keyword
		checkStdDatesTimes("12/2/13 12:00", null, "Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101");
		
		// Check end must be after start if no year
		checkStdDatesTimes("12/1/16 12:00", "31/12/16 23:59", "Meet John about proposal JAN 12th 12:00 til dec 31 #cs2103 #cs2101");
		
		// Check support for natty start date no time, default time is 2359
		checkStdDatesTimes(ParserTestingMethods.inNDays(1, 23, 59), null, "finish homework by tmr");
		
		// Check support for Natty input start date & time
		checkStdDatesTimes(ParserTestingMethods.inNDays(1, 14, 0), null, "Meet John about proposal by tmr 2pm #cs2103 #cs2101");
		
		// Check support for Natty events
		checkStdDatesTimes(ParserTestingMethods.inNDays(1, 12, 0), ParserTestingMethods.inNDays(3, 13, 0), "event on tmr 12pm to 3 days 1pm");
		
		// Check invalid time for Natty input
		checkStdDatesTimesError("Meet John about proposal by tmr 32pm #cs2101");
				
		// Check support for flexible invalid date
		checkStdDatesTimesError("Add meeting with john on 31st April");

		// Check support for formatted input invalid time
		checkStdDatesTimesError("12/2/13 Meet John about proposal #cs2103 52:00-13:30");		
				
		// Consider having if only time is detected, set default to today		
	}
	
	@Test
	public void testGetSearchDatesTimesFromString() {
		// Check supported formats
		
		// Check support for no date & time
		checkSearchDatesTimes(null, null, "Meet John about proposal 1200 #cs2103 #cs2101");
		
		// Check support for formatted input (has start and end time) out of order
		checkSearchDatesTimes("12/2/13 12:00", "12/2/13 13:30", "12/2/13 Meet John about proposal #cs2103 12:00-13:30");
		
		// Check support for formatted input no year, year set to this year even if it's over already
		checkSearchDatesTimes("12/2/15 12:00", "12/2/15 13:30", "12/2 Meet John about proposal #cs2103 12 until 1:30pm");
		
		// Check support for formatted input, event with no times, default start 0000, default end 2359
		checkSearchDatesTimes("12/2/15 00:00", "14/2/15 23:59", "12/2 Meet John about proposal #cs2103 14/2");
				
		// Check support for flexible start date no time, time set to 0000 - 2359
		checkSearchDatesTimes("1/4/2015 00:00", "1/4/2015 23:59", "Add meeting with john on 1st April");
				
		// Check support for flexible start date and time, set end time to 2359
		checkSearchDatesTimes("12/2/13 12:00", "12/2/13 23:59", "Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101");
		
		// Check if one time given, time is for start, end time is 2359 even if user inputs default time
		checkSearchDatesTimes("12/2/13 23:59", "12/2/13 23:59", "Meet John about proposal on feb 12 2013 11.59pm #cs2103 #cs2101");
				
		// Check end must be after start if no year, default end time 23:59
		checkSearchDatesTimes("12/1/15 12:00", "31/12/15 23:59", "Meet John about proposal JAN 12th 12:00 til dec 31 #cs2103 #cs2101");
		
		// Check support for natty start date no time, default time is 0000 - 2359
		checkSearchDatesTimes(ParserTestingMethods.inNDays(1, 0, 0), ParserTestingMethods.inNDays(1, 23, 59), "finish homework by tmr");
		
		// Check support for Natty input start date & time
		checkSearchDatesTimes(ParserTestingMethods.inNDays(1, 14, 0), ParserTestingMethods.inNDays(1, 23, 59), "Meet John about proposal by tmr 2pm #cs2103 #cs2101");
		
		// Check support for Natty events
		checkSearchDatesTimes(ParserTestingMethods.inNDays(1, 12, 0), ParserTestingMethods.inNDays(3, 13, 0), "event on tmr 12pm to 3 days 1pm");
		
		// Check invalid time for Natty input
		checkSearchDatesTimesError("Meet John about proposal by tmr 32pm #cs2101");
				
		// Check support for flexible invalid date
		checkSearchDatesTimesError("Add meeting with john on 31st April");

		// Check support for formatted input invalid time
		checkSearchDatesTimesError("12/2/13 Meet John about proposal #cs2103 52:00-13:30");			
	}
	
	@Test
	public void testGetDescriptionFromString() {
		checkDescription(null, null);
		checkDescription("", "");
		// Check no description
		checkDescription("", "Meet John about proposal @1200 #cs2103 #cs2101");

		// Check gets description
		checkDescription("and this my description", "this is my task \"and this my description\" blah blah 23/3/12");
		
		// Check gets outermost quotes
		checkDescription("my desc\" #cs2101 \"lalala", "Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101 \"lalala\"");
		
		// Check escape characters work
		checkDescription("actual quote \\\"esc quote actual quote 2", 
						 "lalala \\\"esc quote \"actual quote \\\"esc quote actual quote 2\" hello");
	}

	@Test
	public void testGetTaskIdFromString() {
		// Check getTaskId returns integer at front of line
		assertEquals(4242, InputParser.getTaskIdFromString("4242 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		
		assertEquals(0, InputParser.getTaskIdFromString("0 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		
		// Negative numbers not detected
		assertEquals(-1, InputParser.getTaskIdFromString("-2 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		
		// Check invalid/missing taskId returns -1
		assertEquals(-1, InputParser.getTaskIdFromString("Meet Task John about proposal @1200 #cs2103 #cs2101"));
		assertEquals(-1, InputParser.getTaskIdFromString("this is my 23 task \"and this my description\" blah blah 23/3/12"));
	}

	@Test
	public void testGetTagsFromString() {
		assertEquals("Cs2103", InputParser.getTagsFromString("#Cs2103 Meet Task John about proposal @1200 #cs2101").get(0));
		assertEquals("cs2101", InputParser.getTagsFromString("#cs2103 Meet Task John about proposal @1200 #cs2101").get(1));
		assertEquals(new ArrayList<String>(), InputParser.getTagsFromString("Meet Task John about proposal @1200"));
	}
	
	@Test
	public void testGetTaskStatusFromString() {
		assertEquals(false, InputParser.getTaskStatusFromString("Meet Task John about proposal #cs2103 @1200 #cs2101 todo"));
		assertEquals(false, InputParser.getTaskStatusFromString("meeting 23/11/15 #tag Todo"));
		assertEquals(null, InputParser.getTaskStatusFromString("meeting 23/11/15 #tag"));
		assertEquals(true, InputParser.getTaskStatusFromString("meeting 23/11/15 #tag doNe"));

	}
	
	@Test
	public void testGetIsOverdueFromString() {
		assertEquals(false, InputParser.getIsOverdueFromString("Meet Task John about proposal #cs2103 @1200 #cs2101"));
		assertEquals(true, InputParser.getIsOverdueFromString("meeting 23/11/15 #tag Overdue"));
		assertEquals(true, InputParser.getIsOverdueFromString("meeting overdue lala 23/11/15 #tag"));
	}
	
	@Test
	public void testGetTaskTypeFromString() {
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("Floating task"));
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("floating tasks"));
		assertEquals(TaskType.FLOATING_TASK, InputParser.getTaskTypeFromString("lalala floating task lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalafloating task lalala"));

		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deaDline task"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadline tasks"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadlines"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("deadline"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("lalala deadline task lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaladeadline task lalala"));
		assertEquals(TaskType.DEADLINE_TASK, InputParser.getTaskTypeFromString("lalala deadlines lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaladeadline lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("deadlineself"));

		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("eVent"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("events"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("lalala event lalala"));
		assertEquals(TaskType.EVENT, InputParser.getTaskTypeFromString("lalala events lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("lalaevent lalala"));
		assertEquals(null, InputParser.getTaskTypeFromString("eventsal"));

	}
	
	// For testing to be able to access InputParser protected methods 
	protected ParsedCommand parse(String[] input) {
		return null;
	}
	
}
