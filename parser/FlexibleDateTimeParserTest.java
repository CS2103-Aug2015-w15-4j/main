package parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class FlexibleDateTimeParserTest {

	@Test
	public void test() {
		//FlexibleDateTimeParser.getMonthFromString("hello how are you today this is march when apr how about janks february dec");

		//FlexibleDateTimeParser.getDateMonthFromString("Today is 12 Nov, tmr is 3 april 1981 , yesterday was 25 dec ");
		//FlexibleDateTimeParser.getMonthDateFromString("Today is 12 Nov, tmr is 3 april 2015, yesterday was 25 dec 1980 ");
		FlexibleDateTimeParser.getDateMonthFromString(" 12 feb ");
		
	}

}
