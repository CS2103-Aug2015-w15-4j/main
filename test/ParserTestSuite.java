//@@author A0114620X

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	MyParserTest.class, 
	DateTimeParserTest.class,
	test.InputParserTest.class
})

public class ParserTestSuite {

}
