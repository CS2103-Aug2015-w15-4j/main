package parser;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
	private ArrayList<String> emptyArrayList = new ArrayList<String>();

	@Test
	public void testParsedCommand() {
		// Check empty input returns no user input error
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
	public void testParseCommandAdd() {
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

		// Check support for deadline task
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 1/4/15");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(StringParser.parseStringToDate("Wed Apr 1 23:59:00 SGT 2015"), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(2, pcAdd.getTaskType());

		// Check support for deadline task natty
		pcAdd = ParsedCommand.parseCommand("Add meeting with john (april 1 2015 at 12pm)");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(StringParser.parseStringToDate("Wed Apr 1 12:00:00 SGT 2015"), pcAdd.getFirstDate().getTime());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(2, pcAdd.getTaskType());

		// Check support for event
		pcAdd = ParsedCommand.parseCommand("Add  23/11/10 @1200-1330 meeting with john #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 12:00:00 SGT 2010"), pcAdd.getFirstDate().getTime());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 13:30:00 SGT 2010"), pcAdd.getSecondDate().getTime());
		assertEquals(3, pcAdd.getTaskType());

		// Check invalid date returns invalid date error, where invalid date is in proper format
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 31/4/10 @1200 #proj");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcAdd.getErrorMessage());

		/**********CHECK! better to detect all xx/xx/xx formats when checking title?********/
		// Check dates in improper format are ignored and assumed to be not date
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 41/4/10 @1200 #proj");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john 4", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(null, pcAdd.getFirstDate());
		assertEquals(null, pcAdd.getSecondDate());
		assertEquals(1, pcAdd.getTaskType());
		
		// Check missing arguments returns no arguments error
		pcAdd = ParsedCommand.parseCommand("Add");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: No arguments entered", pcAdd.getErrorMessage());
	}

	@Test
	public void testParseCommandDelete() {
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
	public void testParseCommandDisplay() {
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
	public void testParseCommandEdit() {
		// Check support for edit title
		pcEdit = ParsedCommand.parseCommand("Edit 234 meeting");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("meeting", pcEdit.getTitle());
		assertEquals(234, pcEdit.getTaskId());

		// Check support for edit description, date, time, tag
		pcEdit = ParsedCommand.parseCommand("Edit 234 \"hello\" 23/11/10 @1300-1500 #tag");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("hello", pcEdit.getDescription());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 13:00:00 SGT 2010"), pcEdit.getFirstDate().getTime());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 15:00:00 SGT 2010"), pcEdit.getSecondDate().getTime());
		ArrayList<String> list = new ArrayList<String>();
		list.add("tag");
		assertEquals(list, pcEdit.getTags());
		assertEquals("", pcEdit.getTitle());
		
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
	public void testParseCommandDone() {
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
	
	@Test
	public void testGetErrorMessage() {
		// Check not allowed to get errorMessage if not error
		pcDone = ParsedCommand.parseCommand("Done 234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals("Error: No error message as this is not an error", pcDone.getErrorMessage());
	}
}
