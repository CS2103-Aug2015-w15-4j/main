package parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import parser.ParsedCommand.CommandType;

public class ParsedCommandTest {
	private ParsedCommand pc;
	private ParsedCommand pcAdd;
	private ParsedCommand pcUndo;
	private ParsedCommand pcEdit;
	private ParsedCommand pcDelete;
	private ParsedCommand pcDone;
	private ParsedCommand pcDisplay;
	private ParsedCommand pcConfig;
	private ArrayList<String> emptyArrayList = new ArrayList<String>();
	

	private void initLogging(){
		String config = "\nhandlers = java.util.logging.ConsoleHandler" + "\n.level = ALL"+"\n"+
				"java.util.logging.ConsoleHandler.level = FINE" + "\n" +
				"com.sun.level = INFO" + "\n" +
				"javax.level = INFO" + "\n" +
				"sun.level = INFO" + "\n";
 
		InputStream ins = new ByteArrayInputStream(config.getBytes());
 
		Logger logger = Logger.getLogger(ParsedCommandTest.class.getName());
		try {
			LogManager.getLogManager().readConfiguration(ins);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Log manager configuration failed: " + e.getMessage(),e);
		}
	}
	
	@Before
	public void setUp() throws Exception {
	    initLogging();
	}

	@Test
	public void testParsedCommand() throws InvalidMethodForTaskTypeException {
		// Check empty string returns no user input error
		pc = ParsedCommand.parseCommand("");
		assertEquals(CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Check empty input returns no user input error
		pc = ParsedCommand.parseCommand(" ");
		assertEquals(CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Check unrecognised command returns invalid command error
		pc = ParsedCommand.parseCommand("hello");
		assertEquals(CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: Invalid command", pc.getErrorMessage());
	}

	@Test
	public void testParseCommandAdd() throws InvalidMethodForTaskTypeException {
		ArrayList<String> taskTags = new ArrayList<String>();
		taskTags.add("cs2103");
		taskTags.add("proj");
		taskTags.add("cs2101");

		// Check support for floating task
		pcAdd = ParsedCommand.parseCommand("Add meeting with john #cs2103 #proj \"rmb to bring notes\" #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("rmb to bring notes", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(1, pcAdd.getTaskType());

        /* NOT SUPPORTED YET
		// Check support for floating task containing keyword
		pcAdd = ParsedCommand.parseCommand("Add meeting with john on software requirements #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(1, pcAdd.getTaskType());
		
		// Check support for floating task containing keyword
		pcAdd = ParsedCommand.parseCommand("Add meeting with john on 3 software requirements #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("", pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(1, pcAdd.getTaskType());
		
		*/
/*
		// Check support for deadline task formatted date (no keyword)
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 1/4/15");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(StringParser.parseStringToDate("Wed Apr 1 23:59:00 SGT 2015"), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(2, pcAdd.getTaskType());
*/
		// Check support for deadline task natty
		pcAdd = ParsedCommand.parseCommand("Add meeting with john on tmr at 12pm");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		LocalDateTime dt = LocalDateTime.now();
		dt = LocalDateTime.from(dt.plusDays(1));
		dt = dt.withHour(12).withMinute(0).withSecond(0);
		assertEquals(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()).toString(), pcAdd.getFirstDate().getTime().toString());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(2, pcAdd.getTaskType());
		
		/**********************FIX THIS!***************************/
		// Check support for event spanning 2 days
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 23/11/10 12:00h to 24/11/10 13:30H  #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john to", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 12:00:00 SGT 2010"), pcAdd.getFirstDate().getTime());
		assertEquals(StringParser.parseStringToDate("Wed Nov 24 13:30:00 SGT 2010"), pcAdd.getSecondDate().getTime());
		assertEquals(3, pcAdd.getTaskType());

		
		// Check invalid date returns invalid date error, where invalid date is in proper format
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 31/4/10 12:00h #proj");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcAdd.getErrorMessage());
		
		
		/**********CHECK! better to detect all xx/xx/xx formats when checking title?********/
		// Check dates in improper format are ignored and assumed to be not date
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 41/4/10 12:00 #proj");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john 41/4/10", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(1, pcAdd.getTaskType());
		
		// Check missing arguments returns no arguments error
		pcAdd = ParsedCommand.parseCommand("Add");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: No arguments entered", pcAdd.getErrorMessage());
	
		// Check missing title returns no title error
		pcAdd = ParsedCommand.parseCommand("Add 2/3/15 3pm \"hello hello\" #tag1 #tag2");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Missing task title", pcAdd.getErrorMessage());

		// Check missing title returns no title error
		pcAdd = ParsedCommand.parseCommand("Add on tmr 3pm \"hello hello\" #tag1 #tag2");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Missing task title", pcAdd.getErrorMessage());
	}

	@Test
	public void testParseCommandDelete() throws InvalidMethodForTaskTypeException {
		// Check delete
		pcDelete = ParsedCommand.parseCommand("Delete 234");
		assertEquals(CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Check allow extra whitespace
		pcDelete = ParsedCommand.parseCommand("Delete  234");
		assertEquals(CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Check missing arguments returns no arguments error
		pcDelete = ParsedCommand.parseCommand("delete");
		assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: No arguments entered", pcDelete.getErrorMessage());

		// Check invalid/missing taskId returns error
		pcDelete = ParsedCommand.parseCommand("delete abc");
		assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDelete.getErrorMessage());
	}

	@Test
	public void testParseCommandDisplay() throws InvalidMethodForTaskTypeException {
		pcDisplay = ParsedCommand.parseCommand("show 234");
		assertEquals(CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Check allow extra whitespace
		pcDisplay = ParsedCommand.parseCommand("show  234");
		assertEquals(CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Check missing arguments returns error
		pcDisplay = ParsedCommand.parseCommand("show");
		assertEquals(CommandType.ERROR, pcDisplay.getCommandType());
		assertEquals("Error: No arguments entered", pcDisplay.getErrorMessage());

		// Check invalid/missing taskId returns error
		pcDisplay = ParsedCommand.parseCommand("show abc");
		assertEquals(CommandType.ERROR, pcDisplay.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDisplay.getErrorMessage());
	}

	@Test
	public void testParseCommandEdit() throws InvalidMethodForTaskTypeException {
		// Check support for edit title, description, date, time, tag
		pcEdit = ParsedCommand.parseCommand("Edit 234 meeting \"hello\" 23/11/10 13:00-15:00 #tag");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("hello", pcEdit.getDescription());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 13:00:00 SGT 2010"), pcEdit.getFirstDate().getTime());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 15:00:00 SGT 2010"), pcEdit.getSecondDate().getTime());
		ArrayList<String> list = new ArrayList<String>();
		list.add("tag");
		assertEquals(list, pcEdit.getTags());
		assertEquals("meeting", pcEdit.getTitle());
		
		// Check support for edit description, date, time, tag (flexible)
		pcEdit = ParsedCommand.parseCommand("Edit 234 by nov 23 1-3pm \"hello\" #tag");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("hello", pcEdit.getDescription());
		assertEquals(StringParser.parseStringToDate("Mon Nov 23 13:00:00 SGT 2015"), pcEdit.getFirstDate().getTime());
		assertEquals(StringParser.parseStringToDate("Mon Nov 23 15:00:00 SGT 2015"), pcEdit.getSecondDate().getTime());
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("tag");
		assertEquals(list1, pcEdit.getTags());
		assertEquals(null, pcEdit.getTitle());
		
		// Check allow extra whitespace
		pcEdit = ParsedCommand.parseCommand("Edit  234 test");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals(234, pcEdit.getTaskId());
		assertEquals("test", pcEdit.getTitle());
		
		// Check missing field inputs returns error
		pcEdit = ParsedCommand.parseCommand("Edit 234");
		assertEquals(CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No fields were entered for editing", pcEdit.getErrorMessage());

		// Check missing arguments returns error
		pcEdit = ParsedCommand.parseCommand("edit");
		assertEquals(CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No arguments entered", pcEdit.getErrorMessage());

		// Check invalid taskId returns error
		pcEdit = ParsedCommand.parseCommand("edit abc #hello");
		assertEquals(CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcEdit.getErrorMessage());
	}

	@Test
	public void testParseCommandUndo() {
		pcUndo = ParsedCommand.parseCommand("Undo");
		assertEquals(CommandType.UNDO, pcUndo.getCommandType());
	}

	@Test
	public void testParseCommandDone() throws InvalidMethodForTaskTypeException {
		pcDone = ParsedCommand.parseCommand("Done 234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals(234, pcDone.getTaskId());

		// Check allow extra whitespace
		pcDone = ParsedCommand.parseCommand("Done  234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals(234, pcDone.getTaskId());

		// Check missing arguments returns error
		pcDone = ParsedCommand.parseCommand("done ");
		assertEquals(CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: No arguments entered", pcDone.getErrorMessage());

		// Check invalid/missing taskId returns error
		pcDone = ParsedCommand.parseCommand("delete abc");
		assertEquals(CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDone.getErrorMessage());
	}
	
	@Test(expected = InvalidMethodForTaskTypeException.class)
	public void testGetErrorMessage() throws InvalidMethodForTaskTypeException {
		// Check not allowed to get errorMessage if not error
		pcDone = ParsedCommand.parseCommand("Done 234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals("Error: No error message as this is not an error", pcDone.getErrorMessage());
	}
	
	@Test
	public void testParseCommandConfig() throws InvalidMethodForTaskTypeException {
		// Check missing config type
		pcConfig = ParsedCommand.parseCommand("Set");
		assertEquals(CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
		
		// Check missing config type
		pcConfig = ParsedCommand.parseCommand("Set  file");
		assertEquals(CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: Invalid command", pcConfig.getErrorMessage());
		
		// Check missing path
		pcConfig = ParsedCommand.parseCommand("set file");
		assertEquals(CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
		
		// Check missing path
		pcConfig = ParsedCommand.parseCommand("set background");
		assertEquals(CommandType.ERROR, pcConfig.getCommandType());
		assertEquals("Error: No arguments entered", pcConfig.getErrorMessage());
				
		// Check config data
		pcConfig = ParsedCommand.parseCommand("set file filePath");
		assertEquals(CommandType.CONFIG_DATA, pcConfig.getCommandType());
		assertEquals("filePath", pcConfig.getConfigPath());
		
		// Check config img background
		pcConfig = ParsedCommand.parseCommand("set background filePath");
		assertEquals(CommandType.CONFIG_IMG, pcConfig.getCommandType());
		assertEquals("background", pcConfig.getConfigType());
		assertEquals("filePath", pcConfig.getConfigPath());
						
		// Check config img avatar
		pcConfig = ParsedCommand.parseCommand("set avatar filePath");
		assertEquals(CommandType.CONFIG_IMG, pcConfig.getCommandType());
		assertEquals("avatar", pcConfig.getConfigType());
		assertEquals("filePath", pcConfig.getConfigPath());
		
	}
}
