package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.NattyDateTimeParser;
import parser.StringParser;

public class NattyDateTimeParserTest {

	@Test
	public void test() {
		//DateTimeParser dtp = new NattyDateTimeParser();
		//dtp.getDatesTimes("tmr 2pm");
		//assertEquals("tmr 3pm to 4.30pm", NattyDateTimeParser.getNattyFromString("meeting for proj (tmr 3pm to 4.30pm) #cs2101"));
		assertEquals(StringParser.parseStringToDate("Sun Oct 11 15:00:00 SGT 2015"),
			     NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj (11 oct 3pm to 4.30pm) #cs2101")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Sun Oct 11 16:30:00 SGT 2015"),
			     NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj (11 oct 3pm to 4.30pm) #cs2101")[1].getTime());
		assertEquals(StringParser.parseStringToDate("Sun Oct 11 15:00:00 SGT 2015"),
			     NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj (11 oct 3pm to 12 oct 4.30pm) #cs2101")[0].getTime());
		assertEquals(StringParser.parseStringToDate("Mon Oct 12 16:30:00 SGT 2015"),
			     NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj (11 oct 3pm to 12 oct 4.30pm) #cs2101")[1].getTime());
		assertEquals(null, NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj")[0]);
		assertEquals(null, NattyDateTimeParser.parseDateTimeWithNatty("meeting for proj")[1]);
		
		assertEquals(null, NattyDateTimeParser.parseDateTimeWithNatty("year")[0]);
		assertEquals(null, NattyDateTimeParser.parseDateTimeWithNatty("year")[1]);
		
	}

}