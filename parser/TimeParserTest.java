package parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeParserTest {

	@Test
	public void test() {
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have 10 class tmr 12.30pm-2.30pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30pm-2.30pm 1322")[1]);
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30-2.30 pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30-2.30 pm")[1]);
		assertEquals("8:00am", TimeParser.getStandardTimesFromString("hello i have class tmr 8am-2.30pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 8am-2.30pm")[1]);
		assertEquals("9:30am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 - 11am")[0]);
		assertEquals("11:00am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 - 11am")[1]);
		assertEquals("1230", TimeParser.getStandardTimesFromString("hello i have class tmr @1230-1430pm")[0]);
		assertEquals("1430", TimeParser.getStandardTimesFromString("hello i have class tmr @1230-1430pm")[1]);
		assertEquals("1200", TimeParser.getStandardTimesFromString("hello i have class tmr @1200pm lalala @1320")[0]);
		assertEquals("1320", TimeParser.getStandardTimesFromString("hello i have class tmr @1200pm lalala @1320")[1]);
		assertEquals("1200", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[0]);
		assertEquals("1330", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 @1200-1330")[1]);
		/*************NOTE INTERPRETATION OF @ ASSUMES IS 24HR***************************/
	}
	
	/*
	@Test
	public void testGetFormattedTimes() {
		// Check supported formatting/syntax
		
		// Check missing whitespace does not affect parsing
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello@1140-1300blahblah")[0]);
		assertEquals("1300", FormattedDateTimeParser.getFormattedTimes("hello@1140-1300blahblah")[1]);

		// Check extra numbers are ignored
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello @1140-13003 blahblah")[0]);
		assertEquals("1300", FormattedDateTimeParser.getFormattedTimes("hello @1140-13003 blahblah")[1]);


        // Check support for different task types (event, deadline task, floating task)
		
		// Check support for start and end times
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello @1140-1329 blahblah")[0]);
		assertEquals("1329", FormattedDateTimeParser.getFormattedTimes("hello @1140-1329 blahblah")[1]);

		// Check support for single time (start time)
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello @1140 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @1140 blahblah")[1]);

		// Check if no times at all
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @blah-blah bye bye")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @blah-blah bye bye")[1]);
		

		// Check invalid inputs return null
		
		// Check invalid hours
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @3140-1300 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @3140-1300 blahblah")[1]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @2540-1300 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @2540-1300 blahblah")[1]);
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello @1140-2503 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @1140-2503 blahblah")[1]);
		assertEquals("1140", FormattedDateTimeParser.getFormattedTimes("hello @1140-1160 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @1140-1160 blahblah")[1]);
		
		// Check invalid input with similar syntax
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @114 blahblah")[0]);
		assertEquals(null, FormattedDateTimeParser.getFormattedTimes("hello @114 blahblah")[1]);
		assertEquals("1200", FormattedDateTimeParser.getFormattedTimes("hello @school blahblah 2/3/12 @1200-1300")[0]);
		assertEquals("1300", FormattedDateTimeParser.getFormattedTimes("hello @school blahblah 2/3/12 @1200-1300")[1]);

	}*/


}
