package parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

public class StringParserTest {
	@Test
	public void testGetFormattedDates() {
		// Check invalid day
		assertEquals(null, StringParser.getFormattedDates("Hello how 32/12/14 are you today?")[0]);
		assertEquals(null, StringParser.getFormattedDates("Hello how 32/12/14 are you today?")[1]);
		
		// Check support both 02 and 2 etc. for day
		assertEquals("2/12/14", StringParser.getFormattedDates("Hello how 2/12/14 are you today?")[0]);
		assertEquals("02/12/14", StringParser.getFormattedDates("Hello how 02/12/14 are you today?")[0]);

		// Check support both 02 and 2 etc. for month
		assertEquals("2/02/14", StringParser.getFormattedDates("Hello how 2/02/14 are you today?")[0]);
		assertEquals("2/2/14", StringParser.getFormattedDates("Hello how 2/2/14 are you today?")[0]);

		
		// Check invalid month
		assertEquals(null, StringParser.getFormattedDates("Hello how 10/13/14 are you today?")[0]);
		assertEquals(null, StringParser.getFormattedDates("Hello how 10/13/14 are you today?")[1]);
		
		// Check invalid year
		assertEquals(null, StringParser.getFormattedDates("Hello how 10/12/1 are you today?")[0]);
		assertEquals(null, StringParser.getFormattedDates("Hello how 10/12/1 are you today?")[1]);

		// Check whitespace
		assertEquals(null, StringParser.getFormattedDates("Hello how 12/3/14are you1/10/12 today?")[0]);
		assertEquals(null, StringParser.getFormattedDates("Hello how 12/3/14are you1/10/12 today?")[1]);
		
		// Check 2 valid dates
		assertEquals("23/12/14", StringParser.getFormattedDates("Hello how 23/12/14 are you 1/5/12 today?")[0]);
		assertEquals("1/5/12", StringParser.getFormattedDates("Hello how 23/12/14 are you 1/5/12 today?")[1]);
		
		// Check if more than 2 valid, we get first 2 dates
		assertEquals("1/10/12", StringParser.getFormattedDates("Hello how are you 1/10/12 today 2/3/12 2/4/15?")[0]);
		assertEquals("2/3/12", StringParser.getFormattedDates("Hello how are you 1/10/12 today 2/3/12 2/4/15?")[1]);
		
	}
	
	@Test
	public void testGetFormattedTimes() {
		// Check support for start and end times
		assertEquals("1140", StringParser.getFormattedTimes("hello @1140-1329 blahblah")[0]);
		assertEquals("1329", StringParser.getFormattedTimes("hello @1140-1329 blahblah")[1]);
		
		// Check support for single time (start time)
		assertEquals("1140", StringParser.getFormattedTimes("hello @1140 blahblah")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @1140 blahblah")[1]);
		
		// Check missing whitespace does not affect parsing
		assertEquals("1140", StringParser.getFormattedTimes("hello@1140-1300blahblah")[0]);
		assertEquals("1300", StringParser.getFormattedTimes("hello@1140-1300blahblah")[1]);
				
		// Check extra numbers are ignored
		assertEquals("1140", StringParser.getFormattedTimes("hello @1140-13003 blahblah")[0]);
		assertEquals("1300", StringParser.getFormattedTimes("hello @1140-13003 blahblah")[1]);
		
		// Check invalid hours
		assertEquals(null, StringParser.getFormattedTimes("hello @3140-1300 blahblah")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @3140-1300 blahblah")[1]);
		assertEquals(null, StringParser.getFormattedTimes("hello @2540-1300 blahblah")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @2540-1300 blahblah")[1]);
		assertEquals("1140", StringParser.getFormattedTimes("hello @1140-2503 blahblah")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @1140-2503 blahblah")[1]);
		assertEquals("1140", StringParser.getFormattedTimes("hello @1140-1160 blahblah")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @1140-1160 blahblah")[1]);
		
		// Check if no times at all
		assertEquals(null, StringParser.getFormattedTimes("hello @blah-blah bye bye")[0]);
		assertEquals(null, StringParser.getFormattedTimes("hello @blah-blah bye bye")[1]);
	}
	
	@Test
	public void testConvertStringToCalendar() {
		String[] dates = StringParser.getFormattedDates("Hello how 23/12/14 are you @1200-1530 today?");
		String[] times = StringParser.getFormattedTimes("Hello how 23/12/14 are you @1200-1530 today?");
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 12:00:00 SGT 2014"), 
						StringParser.convertStringToCalendar(dates, times)[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 15:30:00 SGT 2014"), 
				StringParser.convertStringToCalendar(dates, times)[1].getTime());
		String[] invalidDates = StringParser.getFormattedDates("Hello how 31/2/14 are you @1200-1530 today?");
		String[] invalidTimes = StringParser.getFormattedTimes("Hello how 31/2/14 are you @1200-1530 today?");
		assertArrayEquals(null, StringParser.convertStringToCalendar(invalidDates, invalidTimes));
	}
	
	@Test
	public void testGetTitleFromString() {
		assertEquals("hello this is my task", StringParser.getTitleFromString(" hello this is my task @1200"));
		assertEquals("hello this is my task", StringParser.getTitleFromString("@1330hello this is my task"));
		assertEquals("hello this ismy task", StringParser.getTitleFromString(" hello this is 12/12/12 my task"));
		assertEquals("hello  task", StringParser.getTitleFromString(" hello \"this is my\" task @1200"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("12/2/13 Meet John about proposal #cs2103 @1200"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString("#cs2101 Meet John about proposal #cs2103 @1200 12/2/13"));
		assertEquals("Meet John about proposal", StringParser.getTitleFromString(" @1200 12/2/13 Meet John about proposal #cs2103"));
	}
	
	@Test
	public void testGetDatesTimesFromString() {
		// Test has start and end time
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"), StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 13:30:00 SGT 2013"), StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[1].getTime());
		// Test only has start time
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 12:00:00 SGT 2013"), StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[1]);
		// Test only has start date
		assertEquals(StringParser.parseStringToDate("Tue Feb 12 00:00:00 SGT 2013"), StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal #cs2103 #cs2101")[0].getTime());
		assertEquals(null, StringParser.getDatesTimesFromString("12/2/13 Meet John about proposal @1200 #cs2103 #cs2101")[1]);
		// Test no date & time
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal @1200 #cs2103 #cs2101")[0]);
		assertEquals(null, StringParser.getDatesTimesFromString("Meet John about proposal @1200 #cs2103 #cs2101")[1]);
	}
	
	@Test
	public void testGetDescriptionFromString() {
		assertEquals("and this my description", StringParser.getDescriptionFromString("this is my task \"and this my description\" blah blah 23/3/12"));
		assertEquals("my desc", StringParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		assertEquals(null, StringParser.getDescriptionFromString("Meet John about proposal @1200 #cs2103 #cs2101"));
	}
	
	@Test
	public void testGetTaskIdFromString() {
		assertEquals(0, StringParser.getTaskIdFromString("this is my 23 task \"and this my description\" blah blah 23/3/12"));
		assertEquals(4242, StringParser.getTaskIdFromString("4242 Meet John about proposal @1200 #cs2103 \"my desc\" #cs2101"));
		assertEquals(0, StringParser.getTaskIdFromString("Meet Task John about proposal @1200 #cs2103 #cs2101"));
	}
	
	@Test
	public void testGetTagsFromString() {
		assertEquals("cs2103", StringParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(0));
		assertEquals("cs2101", StringParser.getTagsFromString("Meet Task John about proposal #cs2103 @1200 #cs2101").get(1));
		assertEquals(new ArrayList<String>(), StringParser.getTagsFromString("Meet Task John about proposal @1200"));
		
	}
}
