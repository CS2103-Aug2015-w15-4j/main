package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.DateTimeParser;

public class DateTimeParserTest {

	
	@Test
	public void testExtractDateTimeSectionFromString() {
		// Test formatted dates
		assertEquals("12/2/13", DateTimeParser.extractDateTimeSectionFromString("do homework 12/2/13 @1200-1300 #tags \"descriptions...\""));
		assertEquals("2/12/13 12:00 2/12/13 13:00", DateTimeParser.extractDateTimeSectionFromString("do homework 2/12/13 #tags 12:00 to 2/12/13 13:00 #tags \"descriptions...\""));
		
		// Test date keywords
		assertEquals("12/2/13 @1200", DateTimeParser.extractDateTimeSectionFromString("do homework on 12/2/13 @1200 #tags \"descriptions...\""));
		assertEquals("tmr 3pm", DateTimeParser.extractDateTimeSectionFromString("do homework by tmr 3pm #tags \"descriptions...\""));
		assertEquals("12/2/13 12pm", DateTimeParser.extractDateTimeSectionFromString("do homework by 12/2/13 12pm #tags \"descriptions...\""));
		assertEquals("next tues from 16:00 to 18:00", DateTimeParser.extractDateTimeSectionFromString(" meet john on next tues from 16:00 to 18:00 #cs2103 #proj #cs2101"));

		// Test floating tasks
		assertEquals("", DateTimeParser.extractDateTimeSectionFromString("do homework #tags \"descriptions...\""));
		
		
	}
	
}
