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
		// Test empty input
		pc = ParsedCommand.parseCommand("");
		assertEquals(CommandType.ERROR, pc.getCommandType());
		assertEquals("Error: No user input", pc.getErrorMessage());

		// Test unrecognised command
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

		// Test task
		pcAdd = ParsedCommand
				.parseCommand("Add meeting with john #cs2103 #proj \"rmb to bring notes\" #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals("rmb to bring notes", pcAdd.getDescription());
		assertEquals(null, pcAdd.getStart());
		assertEquals(null, pcAdd.getEnd());
		assertEquals(taskTags, pcAdd.getTags());
		assertEquals(1, pcAdd.getTaskType());

		// Test deadline task
		pcAdd = ParsedCommand
				.parseCommand("Add meeting with john 1/4/15 @1200");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(
				StringParser.parseStringToDate("Wed Apr 1 12:00:00 SGT 2015"),
				pcAdd.getStart().getTime());
		assertEquals(null, pcAdd.getEnd());
		assertEquals(emptyArrayList, pcAdd.getTags());
		assertEquals(2, pcAdd.getTaskType());

		// Test event
		pcAdd = ParsedCommand
				.parseCommand("Add  23/11/10 @1200-1330 meeting with john #cs2103 #proj #cs2101");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(null, pcAdd.getDescription());
		assertEquals(
				StringParser.parseStringToDate("Tue Nov 23 12:00:00 SGT 2010"),
				pcAdd.getStart().getTime());
		assertEquals(
				StringParser.parseStringToDate("Tue Nov 23 13:30:00 SGT 2010"),
				pcAdd.getEnd().getTime());
		assertEquals(3, pcAdd.getTaskType());

		// Test invalid date
		pcAdd = ParsedCommand
				.parseCommand("Add meeting with john 31/4/10 @1200 #proj");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcAdd.getErrorMessage());

		// Test missing arguments
		pcAdd = ParsedCommand.parseCommand("Add");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: No arguments entered", pcAdd.getErrorMessage());
	}

	@Test
	public void testParseCommandDelete() {
		pcDelete = ParsedCommand.parseCommand("Delete 234");
		assertEquals(CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Test allow extra whitespace
		pcDelete = ParsedCommand.parseCommand("Delete  234");
		assertEquals(CommandType.DELETE, pcDelete.getCommandType());
		assertEquals(234, pcDelete.getTaskId());

		// Test missing arguments
		pcDelete = ParsedCommand.parseCommand("delete");
		assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: No arguments entered", pcDelete.getErrorMessage());

		// Test invalid/missing taskId
		pcDelete = ParsedCommand.parseCommand("delete abc");
		assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: Invalid/Missing taskId",
				pcDelete.getErrorMessage());
	}

	@Test
	public void testParseCommandDisplay() {
		pcDisplay = ParsedCommand.parseCommand("show 234");
		assertEquals(CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Test allow extra whitespace
		pcDisplay = ParsedCommand.parseCommand("show  234");
		assertEquals(CommandType.DISPLAY, pcDisplay.getCommandType());
		assertEquals(234, pcDisplay.getTaskId());

		// Test missing arguments
		pcDisplay = ParsedCommand.parseCommand("show");
		assertEquals(CommandType.ERROR, pcDisplay.getCommandType());
		assertEquals("Error: No arguments entered", pcDisplay.getErrorMessage());

		// Test invalid/missing taskId
		pcDisplay = ParsedCommand.parseCommand("show abc");
		assertEquals(CommandType.ERROR, pcDisplay.getCommandType());
		assertEquals("Error: Invalid/Missing taskId",
				pcDisplay.getErrorMessage());
	}

	@Test
	public void testParseCommandEdit() {
		pcEdit = ParsedCommand.parseCommand("Edit 234 meeting");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals("meeting", pcEdit.getTitle());
		
		pcEdit = ParsedCommand.parseCommand("Edit 234 test");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals(234, pcEdit.getTaskId());

		// Test allow extra whitespace
		pcEdit = ParsedCommand.parseCommand("Edit  234 test");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals(234, pcEdit.getTaskId());
		
		// Test missing field inputs
		pcEdit = ParsedCommand.parseCommand("Edit 234");
		assertEquals(CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No fields were entered for editing", pcEdit.getErrorMessage());

		// Test missing arguments
		pcEdit = ParsedCommand.parseCommand("edit");
		assertEquals(CommandType.ERROR, pcEdit.getCommandType());
		assertEquals("Error: No arguments entered", pcEdit.getErrorMessage());

		// Test invalid arguments
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

		// Test allow extra whitespace
		pcDone = ParsedCommand.parseCommand("Done  234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals(234, pcDone.getTaskId());

		// Test missing arguments
		pcDone = ParsedCommand.parseCommand("delete");
		assertEquals(CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: No arguments entered", pcDone.getErrorMessage());

		// Test invalid/missing taskId
		pcDone = ParsedCommand.parseCommand("delete abc");
		assertEquals(CommandType.ERROR, pcDone.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDone.getErrorMessage());
	}
	@Test
	public void testGetErrorMessage() {
		// Test not allowed to get errorMessage if not error
		pcDone = ParsedCommand.parseCommand("Done 234");
		assertEquals(CommandType.DONE, pcDone.getCommandType());
		assertEquals("Error: No error message as this is not an error", pcDone.getErrorMessage());
	}
}
