package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.FlexibleDateTimeParser;

public class FlexibleDateTimeParserTest {

	@Test
	public void testGetStandardFlexibleDates() {
		assertEquals("2 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("November 2 until nov 5")[0]);
		assertEquals("5 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("November 2 until nov 5")[1]);
		assertEquals("2 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("November 2nd to Nov 12th")[0]);
		assertEquals("12 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("November 2nd to Nov 12th")[1]);
		assertEquals("2 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("2 Nov")[0]);
		assertEquals(null, FlexibleDateTimeParser.getStandardFlexibleDates("2 Nov")[1]);
		assertEquals("2 nov 2016", FlexibleDateTimeParser.getStandardFlexibleDates("2nd november to 3rd nov 2016")[0]);
		assertEquals("3 nov 2016", FlexibleDateTimeParser.getStandardFlexibleDates("2nd november to 3rd nov 2016")[1]);
		assertEquals("2 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("november 2 2015 to nov 3 2016")[0]);
		assertEquals("3 nov 2016", FlexibleDateTimeParser.getStandardFlexibleDates("november 2 2015 to nov 3 2016")[1]);
		assertEquals("2 nov 2015", FlexibleDateTimeParser.getStandardFlexibleDates("2015 november 2")[0]);		
	}

}
