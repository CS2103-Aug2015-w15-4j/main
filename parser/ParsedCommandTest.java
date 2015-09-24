package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import parser.ParsedCommand.CommandType;

public class ParsedCommandTest {
	private ParsedCommand pcAdd;
	private ParsedCommand pcUndo;
	
	/*@Test
	public void testParsedCommand() {
		fail("Not yet implemented");
	}*/

	@Test
	public void testParseCommand() {
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 23/11/10 @1200");
		assertEquals(CommandType.ADD, pcAdd.getCommandType());
		assertEquals("meeting with john", pcAdd.getTitle());
		assertEquals(StringParser.parseStringToDate("Tue Nov 23 12:00:00 SGT 2010"), pcAdd.getStart().getTime());
		assertEquals(null, pcAdd.getEnd());
		assertEquals(2, pcAdd.getTaskType());
		pcUndo = ParsedCommand.parseCommand("Undo");
		assertEquals(CommandType.UNDO, pcUndo.getCommandType());
	}
	
	@Test
	public void testInvalidAddCommands() {
		pcAdd = ParsedCommand.parseCommand("Add meeting with john 31/4/10 @1200 #proj");
		assertEquals(CommandType.ERROR, pcAdd.getCommandType());
		assertEquals("Error: Invalid date(s) input", pcAdd.getErrorMessage());
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
