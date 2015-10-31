package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
   MyParserTest.class,
   DateTimeParserTest.class,
   TimeParserTest.class,
   FormattedDateTimeParserTest.class,
   FlexibleDateTimeParserTest.class,
   NattyDateTimeParserTest.class,
   parser.InputParserTest.class
})
public class ParserTestSuite {

}
