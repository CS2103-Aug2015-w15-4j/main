package test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import parser.StringParser;
import parser.StringParser.TaskStatus;

public class StringParserTest {
	
	@Test
	public void testGetTitleFromString() {
		assertEquals("hello this is my task", StringParser.getTitleFromString(" hello this is my task 12:00 \"description\" #tags #tag1"));
		assertEquals("13:30hello this is my task", StringParser.getTitleFromString("13:30hello this is my task"));
		assertEquals("hello this is   my task", StringParser.getTitleFromString(" hello this is 12/12/12 5pm my task"));
		assertEquals("hello  task", StringParser.getTitleFromString(" hello \"this is my\" task 12:00"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("12.2-13 Meet John about proposal #cs2103 12:00"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("#cs2101 Meet John about proposal on 23 jan #cs2103 12:00 12/2/13"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString(" 12:00 12/2/13 Meet John about proposal #cs2103 todo"));
		assertEquals(null, StringParser.getTitleFromString("23/1/15 2pm \"description\" #tag1 #tag2"));
	}

	@Test
	public void testGetDatesTimesFromString() {
		// Check supported formats
		
		// Check support for no date & time
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[0]);
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[1]);
		
		// SHOULD BE 2016!
		// Check support for flexible start date no time
		assertEquals(StringParser.parseStringToDate("Wed Apr 1 23:59:00 SGT 2015"), StringParser.getDatesTimesFromString("Add meeting with john on 1st April")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("Add meeting with john on 1st April")[1]);

		// Check support for natty start date no time, keyword
		LocalDateTime dt = LocalDateTime.now();
		dt = LocalDateTime.from(dt.plusDays(1));
		dt = dt.withHour(23).withMinute(59).withSecond(0);
		assertEquals(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()).toString(), StringParser.getDatesTimesFromString("finish homework by tmr")[0].getTime().toString());
		assertEquals(null, StringParser.getDatesTimesFromString("finish homework by tmr")[1]);

		
		// Check support for formatted input (has start and end time) out of order
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 13:30:00 SGT 2013"), 
				     StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00h -13:30 H")[1].getTime());		
		
		// Check support for flexible start date and time, keyword
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				StringParser.getDatesTimesFromString("Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal on feb 12 2013 12:00 #cs2103 #cs2101")[1]);
		
		
		// NO SUPPORT FOR NATTY EVENT!
		// Check support for Natty input start date & time
		dt = LocalDateTime.now();
		dt = LocalDateTime.from(dt.plusDays(1));
		dt = dt.withHour(14).withMinute(0).withSecond(0);
		assertEquals(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()).toString(), StringParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2103 #cs2101")[0].getTime().toString());
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2101")[1]);
				
		
		
		// Auto-add date should be upcoming date, not current year
		// Check invalid date detection
		// Natty cannot handle events!!!
		
		// Remove from/to etc from title for no keyword
		// Consider support for if only time indicated, assume is today
				
	}

	@Test
	public void testGetDescriptionFromString() {
		assertEquals("and this my description", StringParser.getDescriptionFromString("this is my task \"and this my description\" blah blah 23/3/12"));
		assertEquals("my desc", StringParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101 \"lalala\""));
		assertEquals(null, StringParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 #cs2101"));
	}

	@Test
	public void testGetTaskIdFromString() {
		// Check getTaskId returns integer at front of line
		assertEquals(4242, StringParser.getTaskIdFromString("4242 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		
		// Check invalid/missing taskId returns -1
		assertEquals(-1, StringParser.getTaskIdFromString("Meet Task John about proposal @1200 #cs2103 #cs2101"));
		assertEquals(-1, StringParser.getTaskIdFromString("this is my 23 task \"and this my description\" blah blah 23/3/12"));
	}

	@Test
	public void testGetTagsFromString() {
		assertEquals("cs2103", StringParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(0));
		assertEquals("cs2101", StringParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(1));
		assertEquals(new ArrayList<String>(), StringParser.getTagsFromString("Meet Task John about proposal @1200"));
	}
	
	@Test
	public void testGetTaskStatusFromString() {
		assertEquals(TaskStatus.TODO, StringParser.getTaskStatusFromString("Meet Task John about proposal #cs2103 @1200 #cs2101 todo"));
		assertEquals(TaskStatus.TODO, StringParser.getTaskStatusFromString("meeting 23/11/15 #tag todo"));
	}
}
