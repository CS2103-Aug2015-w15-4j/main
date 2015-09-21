package parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringParserTest {
	StringParser sp = new StringParser();
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

}
