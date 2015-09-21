package parser;

import static org.junit.Assert.*;

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

}
