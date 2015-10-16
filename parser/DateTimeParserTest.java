package parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class DateTimeParserTest {

	@Test
	public void testIsNattyDateTime() {
		//assertEquals("tmr 3pm", DateTimeParser.extractSectionAfterDateKeyword("do homework by tmr 3pm #tags \"descriptions...\""));
		//assertEquals("", DateTimeParser.extractSectionAfterDateKeyword("do homework 12/2/13 @1200-1300 #tags \"descriptions...\""));
		//assertEquals("12/2/13 @1200", DateTimeParser.extractSectionAfterDateKeyword("do homework on 12/2/13 @1200 #tags \"descriptions...\""));
		//assertEquals("2/12/13 1200 to 2/12/13 1300", DateTimeParser.extractFormattedDates("do homework 12/2/13 @1200-1300 #tags \"descriptions...\""));
		//assertEquals("2/12/13 1200 to ", DateTimeParser.extractFormattedDates("do homework 12/2/13 @1200 #tags \"descriptions...\""));
		
		/*******ISSUE: cannot handle 12/2/13 13/5/13 because insufficient space between 2 dates!!!**********/
		/*******ISSUE: cannot handle 12/2/13 @1200 13/2/13 @1500 events ************************************/
		
		//assertEquals("do homework 12/2/13 to 13/5/13 @1200-1300  ", DateTimeParser.extractFormattedDates("do homework 12/2/13 to 13/5/13 @1200-1300 #tags \"descriptions...\""));
	
		//assertEquals(StringParser.parseStringToDate("Sat Oct 10 12:00:00 SGT 2015"), DateTimeParser.getDatesTimes("do homework 10/10/15 @1200 \"desc\" #tag1 #tag2")[0].getTime());
		//assertEquals(null, DateTimeParser.getDatesTimes("do homework 10/10/15 @1200 \"desc\" #tag1 #tag2")[1]);
		//assertEquals(StringParser.parseStringToDate("Sat Oct 10 12:00:00 SGT 2015"), DateTimeParser.getDatesTimes("do homework by oct 10 12pm \"desc\" #tag1 #tag2")[0].getTime());
		//assertEquals(StringParser.parseStringToDate("Sat Oct 10 12:00:00 SGT 2015"), DateTimeParser.getDatesTimes("do homework by oct 10 12pm \"desc\" #tag1 #tag2")[0].getTime());
		
		//System.out.println(DateTimeParser.reverseOrder("13/2/14"));
	}
	
	@Test
	public void testExtractDateTimeSectionFromString() {
		// Test formatted dates
		assertEquals("12/2/13", DateTimeParser.extractDateTimeSectionFromString("do homework 12/2/13 @1200-1300 #tags \"descriptions...\""));
		assertEquals("2/12/13 2/12/13", DateTimeParser.extractDateTimeSectionFromString("do homework 2/12/13 #tags @1200 to 2/12/13 @1300 #tags \"descriptions...\""));
		
		// Test date keywords
		assertEquals("12/2/13 @1200", DateTimeParser.extractDateTimeSectionFromString("do homework on 12/2/13 @1200 #tags \"descriptions...\""));
		assertEquals("tmr 3pm", DateTimeParser.extractDateTimeSectionFromString("do homework by tmr 3pm #tags \"descriptions...\""));
		assertEquals("12/2/13 12pm", DateTimeParser.extractDateTimeSectionFromString("do homework by 12/2/13 12pm #tags \"descriptions...\""));
				
		// Test floating tasks
		//assertEquals("do homework", DateTimeParser.extractDateTimeSectionFromString("do homework #tags \"descriptions...\""));
		
		assertEquals("next tues from 16:00 to 18:00", DateTimeParser.extractDateTimeSectionFromString(" meet john on next tues from 16:00 to 18:00 #cs2103 #proj #cs2101"));
	}
	
}
