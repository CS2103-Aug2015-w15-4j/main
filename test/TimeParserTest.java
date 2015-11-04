//@@author A0114620X

package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.TimeParser;

public class TimeParserTest {

	@Test
	public void testGetStandardTimesFromString() {
		// Partition 1: 12 hr times
		// Test 12 hr format complete (formatted times), time at end
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have 10 class tmr 12.30pm-2.30pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have 10 class tmr 12.30pm-2.30pm 1322")[1]);
		
		// Test 12 hr format, no am/pm for first time, whitespace (formatted times) 
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30 - 2.30 pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30 - 2.30 pm")[1]);
		
		// Test no minutes indicated (start), am & pm (formatted times), time in middle
		assertEquals("8:00am", TimeParser.getStandardTimesFromString("hello i have 8am-2.30pm test")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have 8am-2.30pm test")[1]);
		
		// Test no minutes indicated (end), whitespace (formatted times), time at the beginning
		assertEquals("9:30am", TimeParser.getStandardTimesFromString("9.30 - 11am on 2 feb")[0]);
		assertEquals("11:00am", TimeParser.getStandardTimesFromString("9.30 - 11am on 2 feb")[1]);
		
		// Test 'to' is acceptable delimiter, don't need am/pm
		assertEquals("8:00pm", TimeParser.getStandardTimesFromString("8 to 3pm")[0]);
		assertEquals("3:00pm", TimeParser.getStandardTimesFromString("8 to 3pm")[1]);
		
		// Test separated times, only am/pm times are detected
		assertEquals("11:00am", TimeParser.getStandardTimesFromString("tmr 9.30 hello lala 11am test 3.30pm")[0]);
		assertEquals("3:30pm", TimeParser.getStandardTimesFromString("tmr 9.30 hello lala 11am test 3.30pm")[1]);
		
		// Test start time only
		assertEquals("9:30am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 am")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 am")[1]);
		
		
		// Partition 2: 24 hr times
		// Test separated times, no am/pm/h, time in beginning
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12:00 lalala 13:20 lala")[0]);
		assertEquals("13:20", TimeParser.getStandardTimesFromString("12:00 lalala 13:20 lala")[1]);

		// Test separated times, no am/pm/h, time in beginning
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12:00 until 13:20 lala")[0]);
		assertEquals("13:20", TimeParser.getStandardTimesFromString("12:00 until 13:20 lala")[1]);

		// Test with h, H, time at end
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00 h - 13:30H")[0]);
		assertEquals("13:30", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00 - 13:30H")[1]);
		
		// Test no h/H, not separated (formatted times), time in middle
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30 la la ")[0]);
		assertEquals("13:30", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30 la la ")[1]);
		
		
		// Partition 3: No times
		// Test non-time numbers are not detected
		assertEquals(null, TimeParser.getStandardTimesFromString("12/2/13 Meet John about 1200 proposal #cs2103")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("12/2/13 Meet John about 1200 proposal #cs2103")[1]);

		
		// Partition 4: Invalid times 
		// Test invalid 12h times are accepted
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12:30-14:30pm")[0]);
		assertEquals("14:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12:30-14:30pm")[1]);

		// Test invalid 24h times are accepted
		assertEquals("50:00", TimeParser.getStandardTimesFromString("hello i have class tmr 50:00-99:30h")[0]);
		assertEquals("99:30", TimeParser.getStandardTimesFromString("hello i have class tmr 50:00-99:30h")[1]);
				
		
		// Partition 5: Undetected formats
		// Test must have space before and after 24h time
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have $2:00 in my 3:00hello")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have $2:00 in my 3:00hello")[1]);
				
		// Test must have space before and after 12h time
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 2amphitheatres in3pm my pocket")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 2amphitheatres in3pm my pocket")[1]);		
		
		// Test invalid hour and minutes for 12 hr
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 009:00am in my 3:0pm hello")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 009:00am in my 3:0pm hello")[1]);
						
		// Test invalid first time means second time is ignored for -
		assertEquals("15:00", TimeParser.getStandardTimesFromString("hello i have 112:00-13:00 my 15:00 pocket")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 112:00-13:00 my 15:00 pocket")[1]);		
		// Test invalid minutes for 24h
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 13:0 my 15:000 pocket")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have 13:0 my 15:000 pocket")[1]);		
				
				
		// Boundary values
		// Test empty string (NOTE: impossible to get null value)
		assertEquals(null, TimeParser.getStandardTimesFromString("")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("")[1]);
		
		// Test empty string (NOTE: impossible to get null value)
		assertArrayEquals(null, TimeParser.getStandardTimesFromString(null));
	}
	
	@Test
	public void testGetStandardTimesFromStringNatty() {
		assertEquals("next monday  to next friday", TimeParser.getStandardTimesFromString("next monday 12.30pm to next friday 2.30pm")[3]);
		assertEquals("next tuesday", TimeParser.getStandardTimesFromString("next tuesday 3 until 7pm")[3]);
	}
		
		
}
