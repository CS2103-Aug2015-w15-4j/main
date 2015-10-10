package parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class DateTimeParserTest {

	@Test
	public void testIsNattyDateTime() {
		assertTrue(DateTimeParser.isNattyDateTime("email john (tmr 3pm) \"description\" #tag1 hi #tag2"));
		assertTrue(DateTimeParser.isNattyDateTime("email john (tmr 3pm) \"description\" #tag1 23/1/12 #tag2"));
		assertFalse(DateTimeParser.isNattyDateTime("email john 23/12/12 \"description\" #tag1 hi #tag2"));
	}

}
