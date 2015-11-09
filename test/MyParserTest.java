//@@author A0114620X

package test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import gui.GUIController;
import parser.MyParser;
import parser.ParsedCommand;
import parser.MyParser.CommandType;
import parser.ParsedCommand.ConfigType;
import parser.ParsedCommand.TaskType;

public class MyParserTest {
	private ParsedCommand pc;
	private ParsedCommand pcAdd;
	private ParsedCommand pcUndo;
	private ParsedCommand pcEdit;
	private ParsedCommand pcDelete;
	private ParsedCommand pcDone;
	private ParsedCommand pcDisplay;
	private ParsedCommand pcConfig;
	private ParsedCommand pcHelp;
	private ParsedCommand pcGui;
	private ParsedCommand pcSearch;
	private ParsedCommand pcExit;
	
	private ArrayList<String> emptyArrayList = new ArrayList<String>();
	
	@Before
	public void setUp() throws Exception {
	    ParserTestingMethods.initLogging();
	}

	@Test
	public void testParseCommand() {
		// Check null input returns no user input error
		pc = MyParser.parseCommand(null);
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Check empty string returns no user input error
		pc = MyParser.parseCommand("");
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Check empty input returns no user input error
		pc = MyParser.parseCommand(" ");
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Check unrecognised command returns invalid command error
		pc = MyParser.parseCommand("hello");
		assertEquals(MyParser.CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Invalid command", pc.getErrorMessage());
	}

	@Test
	public void testParseCommandAdd() {
		ArrayList<String> taskTags = new ArrayList<String>();
		taskTags.add("cs2103");
		taskTags.add("proj");
		taskTags.add("cs2101");
		
		// Floating tasks
		// Check support for floating task
		pcAdd = MyParser.parseCommand("Add meeting with john #cs2103 #proj \"rmb to \"bring\" notes\" #cs2101");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("rmb to \"bring\" notes", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(TaskType.FLOATING_TASK, pcAdd.getTaskType());

		// Check support for floating task containing keyword
		pcAdd = MyParser.parseCommand("Add meeting with \\\"john\\\" on software requirements #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with \"john\" on software requirements", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(TaskType.FLOATING_TASK, pcAdd.getTaskType());
		

		// Deadline tasks
		// Check support for deadline task formatted date (no keyword)
		pcAdd = MyParser.parseCommand("InSERT meeting with john 1/4/15 from 3pm until 7.30pm");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseStringToDate("1/4/15 15:00"), pcAdd.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("1/4/15 19:30"), pcAdd.getSecondDate().getTime());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.EVENT, pcAdd.getTaskType());
		
		// Check support for deadline task tmr without keyword
		pcAdd = MyParser.parseCommand("+ meeting with john tmr at 12pm");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseNDaysToDate(1, 12, 0), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.DEADLINE_TASK, pcAdd.getTaskType());
		
		// Check support for deadline task natty
		pcAdd = MyParser.parseCommand("Add meeting with john on tmr");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseNDaysToDate(1, 23, 59), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.DEADLINE_TASK, pcAdd.getTaskType());
		
		
		// Events
		// Check support for event spanning 2 days
		pcAdd = MyParser.parseCommand("add meeting with john 23/11/10 12:00h to 24/11/10 13:30H  #cs2103 #proj #cs2101");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/10 12:00"), pcAdd.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("24/11/10 13:30"), pcAdd.getSecondDate().getTime());
		assertEquals(TaskType.EVENT, pcAdd.getTaskType());
		
		// Check support for event spanning 2 days natty
		pcAdd = MyParser.parseCommand("add meeting with john on tMr Till 3 daYs later #cs2103 #proj #cs2101");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseNDaysToDate(1, 0, 0), pcAdd.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseNDaysToDate(3, 23, 59), pcAdd.getSecondDate().getTime());
		assertEquals(TaskType.EVENT, pcAdd.getTaskType());
		
		// Check support for event spanning 2 days
		pcAdd = MyParser.parseCommand("add meeting with john on 20 days after nov 2010  #cs2103 #proj #cs2101");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseNDaysToDate(20, 23, 59), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(TaskType.DEADLINE_TASK, pcAdd.getTaskType());
		assertEquals(taskTags, pcAdd.getTags());
				
		
		// Check date keywords are removed correctly
		// Date keywords not removed when no date
		pcAdd = MyParser.parseCommand("InSERT meeting with john on cats");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john on cats", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.FLOATING_TASK, pcAdd.getTaskType());
				
		// Last keyword section removed when there are dates (tmr)
		pcAdd = MyParser.parseCommand("InSERT meeting with john On cats on tmr");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john On cats", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.parseNDaysToDate(1, 23, 59), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.DEADLINE_TASK, pcAdd.getTaskType());

		// Last keyword section removed when there are dates (tmr)
		pcAdd = MyParser.parseCommand("InSERT meeting with john on cats on fri");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john on cats", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(ParserTestingMethods.nextDayOfWeek(6).getTime().toString(), pcAdd.getFirstDate().getTime().toString());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(TaskType.DEADLINE_TASK, pcAdd.getTaskType());

		// Invalid inputs
		// Check invalid date returns invalid date error, where invalid date is in proper format
		pcAdd = MyParser.parseCommand("Add meeting with john 31-4.10 12:00h #proj");
		assertEquals(MyParser.CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcAdd.getErrorMessage());
		
		// Check missing arguments returns no arguments error
		pcAdd = MyParser.parseCommand("Add");
		assertEquals(MyParser.CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: No arguments entered", pcAdd.getErrorMessage());
	
		// Check missing title returns no title error
		pcAdd = MyParser.parseCommand("Add 2/3/15 3pm \"hello hello\" #tag1 #tag2");
		assertEquals(MyParser.CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Missing task title", pcAdd.getErrorMessage());

		// Check missing title returns no title error
		pcAdd = MyParser.parseCommand("Add on tmr 3pm \"hello hello\" #tag1 #tag2");
		assertEquals(MyParser.CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Missing task title", pcAdd.getErrorMessage());

		// Check escape character usage
		// Check missing fields detected even with escape character
		pcAdd = MyParser.parseCommand("Add \\");
		assertEquals(MyParser.CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Missing task title", pcAdd.getErrorMessage());
		
		// Check escape character can be escaped
		pcAdd = MyParser.parseCommand("Add \\\\");
		assertEquals(MyParser.CommandType.ADD, pcAdd.getCommandType());
		assertEquals("\\", pcAdd.getTitle());	
	}

	@Test
	public void testParseCommandDelete() {
		// Check delete
		pcDelete = MyParser.parseCommand("Delete 234");
		assertEquals(MyParser.CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Check allow extra whitespace
		pcDelete = MyParser.parseCommand("Delete  234");
		assertEquals(MyParser.CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Check missing arguments returns no arguments error
		pcDelete = MyParser.parseCommand("delete");
		assertEquals(MyParser.CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: No arguments entered", pcDelete.getErrorMessage());

		// Check invalid/missing taskId returns error
		pcDelete = MyParser.parseCommand("delete abc");
		assertEquals(MyParser.CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDelete.getErrorMessage());
	}

	@Test
	public void testParseCommandDisplay() {
		pcDisplay = MyParser.parseCommand("show 234");
		assertEquals(MyParser.CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Check allow extra whitespace
		pcDisplay = MyParser.parseCommand("show  	234");
		assertEquals(MyParser.CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Check missing arguments returns GUI command
		pcDisplay = MyParser.parseCommand("show");
		assertEquals(MyParser.CommandType.GUI_SHOW, pcDisplay.getCommandType());
		
		ParsedCommand pcShow = MyParser.parseCommand("show 234");
		assertEquals(MyParser.CommandType.DISPLAY, pcShow.getCommandType());
		assertEquals(234, pcShow.getTaskId());
		
		pcShow = MyParser.parseCommand("display 0");
		assertEquals(MyParser.CommandType.DISPLAY, pcShow.getCommandType());
		assertEquals(0, pcShow.getTaskId());

		pcShow = MyParser.parseCommand("shoW -1");
		assertEquals(MyParser.CommandType.SEARCH, pcShow.getCommandType());
		assertEquals("-1", pcShow.getKeywords());		
	}
	
	@Test
	public void testCreateParsedCommandShowSearch() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("tag");
		
		// Search for date
		pcSearch = MyParser.parseCommand("search 23/11");
		assertEquals(MyParser.CommandType.SEARCH, pcSearch.getCommandType());
		assertEquals("", pcSearch.getKeywords());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 00:00"), pcSearch.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 23:59"), pcSearch.getSecondDate().getTime());
		
		pcSearch = MyParser.parseCommand("search 23 Nov");
		assertEquals(MyParser.CommandType.SEARCH, pcSearch.getCommandType());
		assertEquals("", pcSearch.getKeywords());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 00:00"), pcSearch.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 23:59"), pcSearch.getSecondDate().getTime());
		
		// Search multiple fields
		pcSearch = MyParser.parseCommand("show meeting on 23/11/15 #tag todo deadline");
		assertEquals(MyParser.CommandType.SEARCH, pcSearch.getCommandType());
		assertEquals("meeting", pcSearch.getKeywords());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 00:00"), pcSearch.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 23:59"), pcSearch.getSecondDate().getTime());
		ArrayList<String> list = new ArrayList<String>();
		list.add("tag");
		assertEquals(list, pcSearch.getTags());
		assertEquals(false, pcSearch.isCompleted());
		assertEquals(TaskType.DEADLINE_TASK, pcSearch.getTaskType());
		assertEquals(false, pcSearch.isOverdue());
		
		// Check search function works
		pcSearch = MyParser.parseCommand("show meeting 23/11/15 23:59 #tag Todo oveRdue");
		assertEquals(MyParser.CommandType.SEARCH, pcSearch.getCommandType());
		assertEquals("meeting", pcSearch.getKeywords());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 23:59"), pcSearch.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 23:59"), pcSearch.getSecondDate().getTime());
		list = new ArrayList<String>();
		list.add("tag");
		assertEquals(list, pcSearch.getTags());
		assertEquals(false, pcSearch.isCompleted());
		assertEquals(null, pcSearch.getTaskType());
		assertEquals(true, pcSearch.isOverdue());
		
		// Words in quotes are not detected
		ParsedCommand pcShow = MyParser.parseCommand(" Show my search keywords from 1/4/16 3pm to 5pm #tag");
		assertEquals(CommandType.SEARCH, pcShow.getCommandType());
		assertEquals("my search keywords", pcShow.getKeywords());
		assertEquals(ParserTestingMethods.parseStringToDate("1/4/16 15:00"), pcShow.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("1/4/16 17:00"), pcShow.getSecondDate().getTime());
		assertEquals(tags, pcShow.getTags());		
		
		// Invalid input returns error
		pcShow = MyParser.parseCommand("search my search keywords from 31/4/16 3pm to 5pm #tag");
		assertEquals(CommandType.ERROR, pcShow.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcShow.getErrorMessage());
	}

	@Test
	public void testParseCommandEdit() {
		// Check support for invalid date
		pcEdit = MyParser.parseCommand("Edit   234 meeting 31/2/15 \"hello\" #tag");
		assertEquals(MyParser.CommandType.ERROR, pcEdit.getCommandType());
						
		// Check support for edit title, description, date, time, tag
		pcEdit = MyParser.parseCommand("Edit 234 meeting \"hello\" 23/11/10 13:00-15:00 #tag");
		assertEquals(MyParser.CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("hello", pcEdit.getDescription());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/10 13:00"), pcEdit.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/10 15:00"), pcEdit.getSecondDate().getTime());
		ArrayList<String> list = new ArrayList<String>();
		list.add("tag");
		assertEquals(list, pcEdit.getTags());
		assertEquals("meeting", pcEdit.getTitle());

		// Check support for edit description, date, time, tag (flexible)
		pcEdit = MyParser.parseCommand("Edit 234 by nov 23 1-3pm \"hello\" #tag");
		assertEquals(MyParser.CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("hello", pcEdit.getDescription());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 13:00"), pcEdit.getFirstDate().getTime());
		assertEquals(ParserTestingMethods.parseStringToDate("23/11/15 15:00"), pcEdit.getSecondDate().getTime());
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("tag");
		assertEquals(list1, pcEdit.getTags());
		assertEquals("", pcEdit.getTitle());
		
		// Check allow extra whitespace
		pcEdit = MyParser.parseCommand("Edit 234 test");
		assertEquals(MyParser.CommandType.EDIT, pcEdit.getCommandType());
		assertEquals(234, pcEdit.getTaskId());
		assertEquals("test", pcEdit.getTitle());
		
		// Check missing field inputs returns error
		pcEdit = MyParser.parseCommand("Edit 234");
		assertEquals(MyParser.CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No fields were entered for editing", pcEdit.getErrorMessage());

		// Check missing arguments returns error
		pcEdit = MyParser.parseCommand("edit");
		assertEquals(MyParser.CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No arguments entered", pcEdit.getErrorMessage());

		// Check invalid taskId returns error
		pcEdit = MyParser.parseCommand("edit abc #hello");
		assertEquals(MyParser.CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcEdit.getErrorMessage());
		
		// Check invalid date returns error
		pcEdit = MyParser.parseCommand("edit 123 31/2/16 #hello");
		assertEquals(MyParser.CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcEdit.getErrorMessage());
	}

	@Test
	public void testParseCommandUndo() {
		pcUndo = MyParser.parseCommand("Undo");
		assertEquals(MyParser.CommandType.UNDO, pcUndo.getCommandType());
		
		pcUndo = MyParser.parseCommand("redO");
		assertEquals(MyParser.CommandType.REDO, pcUndo.getCommandType());
	}
	
	@Test
	public void testParseCommandExit() {
		pcExit = MyParser.parseCommand("Q;");
		assertEquals(MyParser.CommandType.EXIT, pcExit.getCommandType());
	}
	
	@Test
	public void testParseCommandHelp() {
		pcHelp = MyParser.parseCommand("help");
		assertEquals(MyParser.CommandType.HELP, pcHelp.getCommandType());
		
		pcHelp = MyParser.parseCommand("?");
		assertEquals(MyParser.CommandType.HELP, pcHelp.getCommandType());
	}

	@Test
	public void testParseCommandFlag() {
		// Partition 1: FLAG
		// Check flag command for done
		pcDone = MyParser.parseCommand("Flag completed 234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(true, pcDone.isCompleted());
		assertEquals(234, pcDone.getTaskId());
		
		// Check flag for todo
		pcDone = MyParser.parseCommand("flag   TODO  234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(false, pcDone.isCompleted());
		assertEquals(234, pcDone.getTaskId());

		
		// Partition 2: DONE
		// Check standard input
		pcDone = MyParser.parseCommand("Done 234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(true, pcDone.isCompleted());
		assertEquals(234, pcDone.getTaskId());

		// Check allow extra whitespace
		pcDone = MyParser.parseCommand("finished  234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(true, pcDone.isCompleted());
		assertEquals(234, pcDone.getTaskId());

		// Check missing arguments returns error
		pcDone = MyParser.parseCommand("done ");
		assertEquals(MyParser.CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: No arguments entered", pcDone.getErrorMessage());

		// Check invalid/missing taskId returns error
		pcDone = MyParser.parseCommand("done abc");
		assertEquals(MyParser.CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDone.getErrorMessage());
		
		
		// Partition 3: TODO (similar pathway as DONE)
		pcDone = MyParser.parseCommand("todo 234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(false, pcDone.isCompleted());
		assertEquals(234, pcDone.getTaskId());
	}
	
	@Test
	public void testGetErrorMessage() {
		// Check returns null if irrelevant (not an error)
		pcDone = MyParser.parseCommand("Done 234");
		assertEquals(MyParser.CommandType.FLAG, pcDone.getCommandType());
		assertEquals(null, pcDone.getErrorMessage());
	}
	
	@Test
	public void testParseCommandConfig() {
		// Check missing config type
		pcConfig = MyParser.parseCommand("Set");
		assertEquals(MyParser.CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
		
		// Check invalid config type
		pcConfig = MyParser.parseCommand("Set file");
		assertEquals(MyParser.CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: Invalid command", pcConfig.getErrorMessage());
		
		// Check missing path
		pcConfig = MyParser.parseCommand("set folder");
		assertEquals(MyParser.CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
		
		// Check missing path
		pcConfig = MyParser.parseCommand("set background");
		assertEquals(MyParser.CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
				
		// Check config data
		pcConfig = MyParser.parseCommand("set folder C:\\file name\\folder");
		assertEquals(MyParser.CommandType.CONFIG_DATA, pcConfig.getCommandType());
		assertEquals("C:\\file name\\folder", pcConfig.getConfigPath());
		
		// Check config img background
		pcConfig = MyParser.parseCommand("set background filePath");
		assertEquals(MyParser.CommandType.CONFIG_IMG, pcConfig.getCommandType());
		assertEquals(ConfigType.BACKGROUND, pcConfig.getConfigType());
		assertEquals("filePath", pcConfig.getConfigPath());
						
		// Check config img avatar
		pcConfig = MyParser.parseCommand("set avatar filePath");
		assertEquals(MyParser.CommandType.CONFIG_IMG, pcConfig.getCommandType());
		assertEquals(ConfigType.AVATAR, pcConfig.getConfigType());
		assertEquals("filePath", pcConfig.getConfigPath());
		
	}
	
	@Test
	public void testGuiCommands() {
		pcGui = MyParser.parseCommand("Open 1");
		assertEquals(CommandType.GUI_OPEN, pcGui.getCommandType());
		assertEquals("0", pcGui.getGuiType());
		
		String tabName = GUIController.taskListNames[2];
		pcGui = MyParser.parseCommand("Close " + tabName);
		assertEquals(CommandType.GUI_CLOSE, pcGui.getCommandType());
		assertEquals("-3", pcGui.getGuiType());
		
		pcGui = MyParser.parseCommand("Close random");
		assertEquals(CommandType.ERROR, pcGui.getCommandType());
		assertEquals("Error: Invalid tab ID", pcGui.getErrorMessage());
		
		pcGui = MyParser.parseCommand("pin");
		assertEquals(CommandType.ERROR, pcGui.getCommandType());
		assertEquals("Error: No arguments entered", pcGui.getErrorMessage());
		
		pcGui = MyParser.parseCommand("log");
		assertEquals(CommandType.GUI_LOG, pcGui.getCommandType());
		
		pcGui = MyParser.parseCommand("mAin");
		assertEquals(CommandType.GUI_MAIN, pcGui.getCommandType());
		
		pcGui = MyParser.parseCommand("switcH");
		assertEquals(CommandType.GUI_SWITCH, pcGui.getCommandType());
	}
}