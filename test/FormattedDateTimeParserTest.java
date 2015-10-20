package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.FormattedDateTimeParser;

public class FormattedDateTimeParserTest {	
	@Test
	public void testGetFormattedDates() {
		// Check supported formats
		// Check support for / for date
		assertEquals("12/2/2013", FormattedDateTimeParser.getStandardFormattedDates("12/2/13 12:00-13:00")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("12/2/13 12:00-13:00")[1]);

		// Check support for / for date
		assertEquals("5/4/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 5/4/14 are you today?")[0]);
		assertEquals("02/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 02/12/14 are you today?")[0]);

		// Check support for . for date (and returns in standard format dd/MM/yy)
		assertEquals("2/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2.12.14 are you today?")[0]);
		assertEquals("02/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 02.12.14 are you today?")[0]);

		// Check support for - for date (and returns in standard format dd/MM/yy)
		assertEquals("2/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2-12-14 are you today?")[0]);
		assertEquals("02/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 02-12-14 are you today?")[0]);

		// Check support for mixed date delimiters for date (and returns in standard format dd/MM/yy)
		assertEquals("2/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2.12/14 are you today?")[0]);
		assertEquals("02/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 02-12.14 are you today?")[0]);
		
		// Check support both 02 and 2 etc. for day
		assertEquals("2/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2/12/14 are you today?")[0]);
		assertEquals("02/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 02/12/14 are you today?")[0]);

		// Check support both 02 and 2 etc. for month
		assertEquals("2/02/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2/02/14 are you today?")[0]);
		assertEquals("2/2/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2/2/14 are you today?")[0]);

		// Check not required to have whitespace
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 12/3/14are you1/10/12 today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 12/3/14are you1/10/12 today?")[1]);
		
		// Check has support for dd/MM
		assertEquals("2/02/2016", FormattedDateTimeParser.getStandardFormattedDates("Hello how 2/02 are you today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 2/02 are you today?")[1]);
		assertEquals("3/2/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello 3.2 how 2/2/14 are you today?")[0]);
		assertEquals("2/2/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello 3.2 how 2/2/14 are you today?")[1]);
		assertEquals("12/2/2016", FormattedDateTimeParser.getStandardFormattedDates("12/2 until 16/2")[0]);
		assertEquals("16/2/2016", FormattedDateTimeParser.getStandardFormattedDates("12/2 until 16/2")[1]);

		// Check no support for yyyy/MM/dd
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 2012/02/14 are you today?")[0]);
		

		// Check invalid inputs return null
		
		// Check invalid day returns null
		assertEquals("32/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 32/12/14 are you today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 32/12/14 are you today?")[1]);
		
	    // Check invalid month (i.e. numbers more than 12)
		assertEquals("10/13/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/13/14 are you today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/13/14 are you today?")[1]);
		
		// Check invalid year (i.e. non 2-digit number)
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/12/1 are you today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/12/1 are you today?")[1]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/12/123 are you today?")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("Hello how 10/12/123 are you today?")[1]);
		
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("hello")[0]);
		assertEquals(null, FormattedDateTimeParser.getStandardFormattedDates("hello")[0]);
		
		
		// Other checks
		
		// Check if more than 2 valid, we get first 2 dates
		assertEquals("1/10/2012", FormattedDateTimeParser.getStandardFormattedDates("Hello how are you 1/10/12 today 2/3/12 2/4/15?")[0]);
		assertEquals("2/3/2012", FormattedDateTimeParser.getStandardFormattedDates("Hello how are you 1/10/12 today 2/3/12 2/4/15?")[1]);
		
		// Check 2 valid dates are accepted
		assertEquals("23/12/2014", FormattedDateTimeParser.getStandardFormattedDates("Hello how 23/12/14 are you 1/5/12 today?")[0]);
		assertEquals("1/5/2012", FormattedDateTimeParser.getStandardFormattedDates("Hello how 23/12/14 are you 1/5/12 today?")[1]);
	}

	/*
	@Test
	public void testConvertStringToCalendar() {
		// Check supported formats
		
		// Check support for events (start and end time)
		String[] dates = FormattedDateTimeParser.getStandardFormattedDates("Hello how 23-12-14 are you @1200-1530 today?");
		String[] times = FormattedDateTimeParser.getFormattedTimes("Hello how 23-12-14 are you @1200-1530 today?");
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 12:00:00 SGT 2014"), FormattedDateTimeParser.convertStringToCalendar(dates, times)[0].getTime());
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 15:30:00 SGT 2014"),FormattedDateTimeParser.convertStringToCalendar(dates, times)[1].getTime());
		
		// Check support for deadline tasks (start time only)
		String[] dates2 = FormattedDateTimeParser.getStandardFormattedDates("Hello how 23-12-14 are you @1200 today?");
		String[] times2 = FormattedDateTimeParser.getFormattedTimes("Hello how 23-12-14 are you @1200 today?");
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 12:00:00 SGT 2014"), FormattedDateTimeParser.convertStringToCalendar(dates2, times2)[0].getTime());
		assertEquals(null, FormattedDateTimeParser.convertStringToCalendar(dates2, times2)[1]);
		
		// Check support for deadline tasks (start date no start time) default timing set to 2359
		String[] dates3 = FormattedDateTimeParser.getStandardFormattedDates("Hello how 23-12-14 are you today?");
		String[] times3 = FormattedDateTimeParser.getFormattedTimes("Hello how 23-12-14 are you today?");
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 23:59:00 SGT 2014"), FormattedDateTimeParser.convertStringToCalendar(dates3, times3)[0].getTime());
		assertEquals(null, FormattedDateTimeParser.convertStringToCalendar(dates3, times3)[1]);

		// Check support for floating tasks (no date/time)
		String[] dates4 = FormattedDateTimeParser.getStandardFormattedDates("Hello how are you today?");
		String[] times4 = FormattedDateTimeParser.getFormattedTimes("Hello how are you today?");
		assertEquals(null, FormattedDateTimeParser.convertStringToCalendar(dates4, times4)[0]);
		assertEquals(null, FormattedDateTimeParser.convertStringToCalendar(dates4, times4)[1]);

		
		// Check invalid inputs
		
		// Check invalid dates return null
		String[] invalidDates = FormattedDateTimeParser.getStandardFormattedDates("Hello how 31/2/14 are you @1200-1530 today?");
		String[] invalidTimes = FormattedDateTimeParser.getFormattedTimes("Hello how 31/2/14 are you @1200-1530 today?");
		assertArrayEquals(null, FormattedDateTimeParser.convertStringToCalendar(invalidDates, invalidTimes));
		
		// Check invalid times are ignored (treated as with date, no time) (if start time is incorrect, end time ignored)
		String[] invalidDates2 = FormattedDateTimeParser.getStandardFormattedDates("Hello how 23/12/14 are you @2900-1530 today?");
		String[] invalidTimes2 = FormattedDateTimeParser.getFormattedTimes("Hello how 23/12/14 are you @2900-1530 today?");
		assertEquals(StringParser.parseStringToDate("Tue Dec 23 23:59:00 SGT 2014"), FormattedDateTimeParser.convertStringToCalendar(invalidDates2, invalidTimes2)[0].getTime());
		assertEquals(null, FormattedDateTimeParser.convertStringToCalendar(invalidDates2, invalidTimes2)[1]);
	}*/
}
