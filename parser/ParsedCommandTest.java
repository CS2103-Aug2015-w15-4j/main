package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.ParsedCommand.CommandType;

public class ParsedCommandTest {
	private ParsedCommand pcAdd;
	private ParsedCommand pcUndo;
	private ParsedCommand pcEdit;
	private ParsedCommand pcDelete;
	
	/*@Test
	public void testParsedCommand() {
		fail("Not yet implemented");
	}*/
	
	@Test
	public void testParseCommandAdd() {
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 23/11/10 @1200");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 12:00:00 SGT 2010"), pcAdd.getStart().getTime());
		assertEquals(null, pcAdd.getEnd());
		assertEquals(2, pcAdd.getTaskType());
		
		// Test invalid date
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 31/4/10 @1200 #proj");
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
		
		// Test missing arguments
		pcDelete = ParsedCommand.parseCommand("delete");
    	assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: No arguments entered", pcDelete.getErrorMessage());
		
		// Test invalid arguments
		pcDelete = ParsedCommand.parseCommand("delete abc");
    	assertEquals(CommandType.ERROR, pcDelete.getCommandType());
		assertEquals("Error: Invalid/Missing taskId", pcDelete.getErrorMessage());		
	}
	
	@Test
	public void testParseCommandEdit() {
		pcEdit = ParsedCommand.parseCommand("Edit 234");
		assertEquals(CommandType.EDIT, pcEdit.getCommandType());
		assertEquals(234, pcEdit.getTaskId());
		
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
/*
	@Test
	public void testGetCommandType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTitle() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStart() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEnd() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTags() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetErrorMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskType() {
		fail("Not yet implemented");
	} */

}
