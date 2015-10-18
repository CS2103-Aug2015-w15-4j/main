package test;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.TimeParser;

public class TimeParserTest {

	@Test
	public void testGetStandardTimesFromString() {
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have 10 class tmr 12.30pm-2.30pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30pm-2.30pm 1322")[1]);
		assertEquals("12:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30-2.30 pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 12.30-2.30 pm")[1]);
		assertEquals("8:00am", TimeParser.getStandardTimesFromString("hello i have class tmr 8am-2.30pm")[0]);
		assertEquals("2:30pm", TimeParser.getStandardTimesFromString("hello i have class tmr 8am-2.30pm")[1]);
		assertEquals("9:30am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 - 11am")[0]);
		assertEquals("11:00am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 - 11am")[1]);
		assertEquals("9:30am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 to 11am")[0]);
		assertEquals("11:00am", TimeParser.getStandardTimesFromString("hello i have class tmr 9.30 to 11am")[1]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have class tmr 12:30-14:30pm")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("hello i have class tmr 12:30-14:30pm")[1]);
		assertEquals("12:00", TimeParser.getStandardTimesFromString("hello i have class tmr 12:00 lalala 13:20")[0]);
		assertEquals("13:20", TimeParser.getStandardTimesFromString("hello i have class tmr 12:00 lalala 13:20")[1]);
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00 h - 13:30H")[0]);
		assertEquals("13:30", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30H")[1]);
		assertEquals(null, TimeParser.getStandardTimesFromString("12/2/13 Meet John about 1200 proposal #cs2103")[0]);
		assertEquals(null, TimeParser.getStandardTimesFromString("12/2/13 Meet John about 1200 proposal #cs2103")[1]);
		assertEquals("12:00", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30")[0]);
		assertEquals("13:30", TimeParser.getStandardTimesFromString("12/2/13 Meet John about proposal #cs2103 12:00-13:30")[1]);
	}
}
