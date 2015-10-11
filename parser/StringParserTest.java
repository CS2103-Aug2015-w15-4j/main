package parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

public class StringParserTest {
	
	@Test
	public void testGetTitleFromString() {
		assertEquals("hello this is my task", StringParser.getTitleFromString(" hello this is my task @1200"));
		assertEquals("hello this is my task", StringParser.getTitleFromString("@1330hello this is my task"));
		assertEquals("hello this ismy task", StringParser.getTitleFromString(" hello this is 12/12/12 my task"));
		assertEquals("hello  task", StringParser.getTitleFromString(" hello \"this is my\" task @1200"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("12.2-13 Meet John about proposal #cs2103 @1200"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("#cs2101 Meet John about proposal #cs2103 @1200 12/2/13"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString(" @1200 12/2/13 Meet John about proposal #cs2103"));
	}

	@Test
	public void testGetDatesTimesFromString() {
		// Check supported formats
		
		// Check support for formatted input (has start and end time)
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 13:30:00 SGT 2013"), 
				     StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[1].getTime());
		
		 
		// Test only has start time
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"),
				StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[1]);
		
		
		// Test only has start date
		assertEquals(StringParser.parseStringToDate("Wed Apr 1 23:59:00 SGT 2015"), StringParser.getDatesTimesFromString("Add meeting with john 1/4/15")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("Add meeting with john 1/4/15")[1]);
		
		// Test only has start date
		assertEquals(StringParser.parseStringToDate("Thu Feb 12 23:59:00 SGT 2015"), StringParser.getDatesTimesFromString("Meet John about proposal on feb 12 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[1]);
		
		// Check support for no date & time
		//assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2103 #cs2101")[0].getTime());
		//assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal by tmr 2pm #cs2101")[1]);
				
		// Check support for no date & time
		//assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[0].getTime());
		//assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal 1200 #cs2103 #cs2101")[1]);
	}

	@Test
	public void testGetDescriptionFromString() {
		assertEquals("and this my description", StringParser.getDescriptionFromString("this is my task \"and this my description\" blah blah 23/3/12"));
		assertEquals("my desc", StringParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
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
}
