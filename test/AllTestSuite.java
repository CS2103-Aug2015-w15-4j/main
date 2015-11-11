//@@author A0114620X

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	ParserTestSuite.class,
	IntegrationTest.class,
	StorageTest.class,
	LogicTestSuite.class,
	GuiTestSuite.class
})


public class AllTestSuite {

}