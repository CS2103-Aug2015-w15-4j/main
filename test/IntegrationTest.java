package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import logic.Logic;
import logic.Logic.UnrecognisedCommandException;

import org.junit.Before;
import org.junit.Test;

import parser.MyParser;
import storage.Storage;

// CAUTION : Data.txt & config will be overwritten
// WILL FIX IT ASAP
public class IntegrationTest {

	private static String dataFile = "Data.txt";
	private static String configFile = "config";
	String output = "", expected = "";

	Logic logic = new Logic();
	Storage storage;
	logic.Model model;

	@Before
	public void init() throws UnrecognisedCommandException {
		// copy the config and data.txt
		model = logic.executeCommand(null);
	}

	@Test
	public void testAdd() throws UnrecognisedCommandException {

		// Add Floating task
		logic.executeCommand(MyParser
				.parseCommand("add do homework #mustTodo #veryHard"));
		output = model.getConsoleMessage();
		expected = "do homework added";
		assertEquals(expected, output);

		// clear();

		output = readFile(dataFile);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Deadline task - without time
		logic.executeCommand(MyParser
				.parseCommand("add do housekeeping on 12/12"));
		output = model.getConsoleMessage();
		expected = "do housekeeping added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Deadline task - with date and time
		logic.executeCommand(MyParser
				.parseCommand("add homework by 10pm 10/12"));
		output = model.getConsoleMessage();
		expected = "homework added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Deadline task - using NATTY DATE
		logic.executeCommand(MyParser
				.parseCommand("add homework by 10pm next fri"));
		output = model.getConsoleMessage();
		expected = "homework next fri added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Event task
		logic.executeCommand(MyParser
				.parseCommand("add project on 12/12 10am to 13/12 1pm"));
		output = model.getConsoleMessage();
		expected = "project added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		// Add Event without "to"
		logic.executeCommand(MyParser
				.parseCommand("add camp 12/10 10am 13/10 6pm"));
		output = model.getConsoleMessage();
		expected = "camp added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		// Add Event by using "-"
		logic.executeCommand(MyParser
				.parseCommand("add meeting john 10/12 10pm - 12/10 12pm"));
		output = model.getConsoleMessage();
		expected = "meeting john added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

	}

	@Test
	public void testDelete() throws UnrecognisedCommandException {

		// Delete a Task
		logic.executeCommand(MyParser.parseCommand("delete 3"));
		output = model.getConsoleMessage();
		expected = "Task 3 deleted";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"do homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		// Delete unexist TaskID
		/*
		 * logic.executeCommand(MyParser.parseCommand("delete 53")); output =
		 * model.getConsoleMessage(); expected = "Error: Invalid taskID";
		 * assertEquals(expected, output);
		 */

		/*
		 * clear();
		 * 
		 * output = readFile(dataFile); expected +=
		 * "{\"name\":\"do homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; assertEquals(expected, output);
		 */

		clear();
	}

	@Test
	public void testUpdate() throws UnrecognisedCommandException {

		// Edit a Task
		logic.executeCommand(MyParser.parseCommand("edit 1 homework"));
		output = model.getConsoleMessage();
		expected = "do homework updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("edit 2"));
		output = model.getConsoleMessage();
		expected = "Error: No fields were entered for editing";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		/*
		 * logic.executeCommand(MyParser.parseCommand("edit 53 homework"));
		 * output = model.getConsoleMessage(); expected = "Update Failed";
		 * assertEquals(expected, output);
		 * 
		 * clear();
		 * 
		 * output = readFile(dataFile); expected +=
		 * "{\"name\":\"do homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; expected += "\n"; expected +=
		 * "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}"
		 * ; assertEquals(expected, output);
		 * 
		 * clear();
		 */

		logic.executeCommand(MyParser.parseCommand("edit 2 23/12 4pm"));
		output = model.getConsoleMessage();
		expected = "do housekeeping updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("edit 2 by 23/12 @1500"));
		output = model.getConsoleMessage();
		expected = "do housekeeping updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFile);
		expected += "{\"name\":\"homework\",\"details\":\"\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"details\":\"\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":13,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework next fri\",\"details\":\"\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"details\":\"\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"details\":\"\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"details\":\"\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

	}

	private String readFile(String fileInput) {
		String sCurrentLine = "";
		StringBuilder result = new StringBuilder();
		File file = new File(fileInput);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((sCurrentLine = br.readLine()) != null) {
				result.append(sCurrentLine + "\n");
			}
			fr.close();
			br.close();

			result.setLength(result.length() - 1);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	private void clear() {
		output = "";
		expected = "";
	}
}
