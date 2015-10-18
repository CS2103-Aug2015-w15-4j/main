package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
   ParsedCommandTest.class,
   DateTimeParserTest.class,
   TimeParserTest.class,
   FormattedDateTimeParserTest.class,
   FlexibleDateTimeParserTest.class,
   NattyDateTimeParserTest.class
})
public class ParserTestSuite {

}
