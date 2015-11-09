//@@author A0114620X

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	ParserTestSuite.class,
	GuiShortcutTest.class,
	IntegrationTest.class,
	StorageTest.class,
	LogicTestSuite.class
})


public class AllTestSuite {

}