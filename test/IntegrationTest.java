//@@author A0126276A

package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import logic.Logic;
import logic.Logic.UnrecognisedCommandException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import parser.MyParser;
import storage.Storage;

// CAUTION : Data.txt & config will be overwritten
// Data.txt must be empty or deleted

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTest {

	private String dataFilePath = "";
	private static String dataFilePathTemp;
	private static boolean hasConfigFile = false;
	private static boolean hasDataFile = false;

	static InputStream inStream = null;
	static OutputStream outStream = null;

	static File dataTemp;
	static File configTemp;

	private static final String CONFIG_FILENAME = "config";
	String output = "", expected = "";

	static Logic logic = new Logic();
	static Storage storage = new Storage();
	static logic.Model model;

	@BeforeClass
	public static void init() throws UnrecognisedCommandException {
		setUp();
		model = logic.executeCommand(null);
		storage.clearList();
		model.getTodayList().clear();
		model.getMainList().clear();
		model.getAllTasks().clear();
		model.getSearchList().clear();
	}

	// GUI
	@Test
	public void testA() {
		output = String.valueOf(model.getAllTasks().size());
		expected = "0";
		assertEquals(expected, output);

		clear();
	}

	// Add
	@Test
	public void testB() throws UnrecognisedCommandException, IOException {

		getConfigDetails();
		System.out.println(new File(dataFilePath).length());
		// Add Floating task
		logic.executeCommand(MyParser
				.parseCommand("add do homework #mustTodo #veryHard"));
		output = model.getConsoleMessage();
		expected = "do homework added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		output = String.valueOf(model.getFloatingList().size());
		expected = "1";
		assertEquals(expected, output);

		clear();

		// Add Deadline task - without time
		logic.executeCommand(MyParser
				.parseCommand("add do housekeeping on 12/12"));
		output = model.getConsoleMessage();
		expected = "do housekeeping added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
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

		output = readFile(dataFilePath);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Deadline task - using NATTY DATE
		logic.executeCommand(MyParser
				.parseCommand("add assignment by next friday 10pm"));
		output = model.getConsoleMessage();
		expected = "assignment added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected = "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Event task
		logic.executeCommand(MyParser
				.parseCommand("add project on 12/12 10am to 13/12 1pm"));
		output = model.getConsoleMessage();
		expected = "project added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
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

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
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

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		// Add Event by using backslash as escape key
		String keyword = "add do project " + "\\"
				+ "on software engineering principles";
		logic.executeCommand(MyParser.parseCommand(keyword));
		output = model.getConsoleMessage();
		expected = "do project on software engineering principles added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Event without title
		logic.executeCommand(MyParser.parseCommand("add \"at Orchard\""));
		output = model.getConsoleMessage();
		expected = "Error: Missing task title";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Event with invalid date
		logic.executeCommand(MyParser
				.parseCommand("add meeting kevin by 10/13"));
		output = model.getConsoleMessage();
		expected = "Error: Invalid date(s) input";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Add Event with invalid date
		logic.executeCommand(MyParser
				.parseCommand("add project proposal 23/12 10am 27/12 12pm \"Task management software\" #imprt"));
		output = model.getConsoleMessage();
		expected = "project proposal added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		clear();

		// Add Event with shortcut key, "+"
		logic.executeCommand(MyParser.parseCommand("+ dinner with parents"));
		output = model.getConsoleMessage();
		expected = "dinner with parents added";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"homework\",\"id\":3,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

	}

	// Delete
	@Test
	public void testC() throws UnrecognisedCommandException {

		getConfigDetails();

		// Delete a Task
		logic.executeCommand(MyParser.parseCommand("delete 3"));
		output = model.getConsoleMessage();
		expected = "Task 3 deleted";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"do project on software engineering principles\",\"id\":8,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Delete a Task by using shortcut key - "X"
		logic.executeCommand(MyParser.parseCommand("X 8"));
		output = model.getConsoleMessage();
		expected = "Task 8 deleted";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"do homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

	}

	// Update
	@Test
	public void testD() throws UnrecognisedCommandException {
		getConfigDetails();

		// Edit a Task
		logic.executeCommand(MyParser.parseCommand("edit 1 homework"));
		output = model.getConsoleMessage();
		expected = "do homework updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Edit a task without fields
		logic.executeCommand(MyParser.parseCommand("edit 2"));
		output = model.getConsoleMessage();
		expected = "Error: No fields were entered for editing";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Edit a invalid ID Task
		logic.executeCommand(MyParser.parseCommand("edit 53 homework"));
		output = model.getConsoleMessage();
		expected = "Error: Invalid TaskID";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Edit a DEADLINE
		logic.executeCommand(MyParser.parseCommand("edit 2 23/12 4pm"));
		output = model.getConsoleMessage();
		expected = "do housekeeping updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":10,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Edit Event (both start and end date, time)
		logic.executeCommand(MyParser
				.parseCommand("edit 7 18/12 2pm 20/12 12am"));
		output = model.getConsoleMessage();
		expected = "meeting john updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":14,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":20,\"hourOfDay\":0,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Edit Event with 1 date, time -> change to Deadline
		logic.executeCommand(MyParser.parseCommand("edit 7 16/12/15 8pm"));
		output = model.getConsoleMessage();
		expected = "meeting john updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":16,\"hourOfDay\":20,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Update Floating to Deadline
		logic.executeCommand(MyParser.parseCommand("e; 1 by 18/12 11:59pm"));
		output = model.getConsoleMessage();
		expected = "homework updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":16,\"hourOfDay\":20,\"minute\":0,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Update Deadline to Event
		logic.executeCommand(MyParser.parseCommand("edit 7 1/1 3/1"));
		output = model.getConsoleMessage();
		expected = "meeting john updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();
	}

	// Mark
	@Test
	public void testE() throws UnrecognisedCommandException {

		getConfigDetails();

		// Mark task as completed
		logic.executeCommand(MyParser.parseCommand("mark done 1"));
		output = model.getConsoleMessage();
		expected = "homework updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":true,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Mark task as completed by using invalid taskID

		logic.executeCommand(MyParser.parseCommand("mark done 23"));
		output = model.getConsoleMessage();
		expected = "Error: Invalid TaskID";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":true,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Mark task as todo by using valid taskID
		logic.executeCommand(MyParser.parseCommand("mark todo 1"));
		output = model.getConsoleMessage();
		expected = "homework updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Mark task as t6odo by using invalid taskID
		logic.executeCommand(MyParser.parseCommand("mark todo 23"));
		output = model.getConsoleMessage();
		expected = "Error: Invalid TaskID";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Mark task as todo by using shortcut command
		logic.executeCommand(MyParser.parseCommand("v 2"));
		output = model.getConsoleMessage();
		expected = "do housekeeping updated";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":true,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

	}

	// Undo
	@Test
	public void testF() throws UnrecognisedCommandException {

		getConfigDetails();

		// Undo done to todo
		logic.executeCommand(MyParser.parseCommand("undo"));
		output = model.getConsoleMessage();
		expected = "Undo Successful";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Undo add
		logic.executeCommand(MyParser.parseCommand("add testing"));
		output = model.getConsoleMessage();
		expected = "testing added";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("undo"));
		output = model.getConsoleMessage();
		expected = "Undo Successful";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);
	}

	// Redo
	@Test
	public void testG() throws UnrecognisedCommandException {

	}

	// Search or Show
	@Test
	public void testH() throws UnrecognisedCommandException {

		// Search with valid keyword
		logic.executeCommand(MyParser.parseCommand("search homework"));
		output = model.getConsoleMessage();
		expected = "1 result found";
		assertEquals(expected, output);

		clear();

		output = String.valueOf(model.getSearchList().size());
		expected = "1";
		assertEquals(expected, output);

		clear();

		// Search with valid keyword with UPPERCASE
		logic.executeCommand(MyParser.parseCommand("search HOMEWORK"));
		output = model.getConsoleMessage();
		expected = "1 result found";
		assertEquals(expected, output);

		clear();

		// Search with special character
		logic.executeCommand(MyParser.parseCommand("search ;"));
		output = model.getConsoleMessage();
		expected = "No results found";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("search Lucy"));
		output = model.getConsoleMessage();
		expected = "No results found";
		assertEquals(expected, output);

		clear();

		// search date
		logic.executeCommand(MyParser.parseCommand("search 23/12"));
		output = model.getConsoleMessage();
		expected = "2 results found";
		assertEquals(expected, output);

		clear();

		// search 2 dates
		logic.executeCommand(MyParser.parseCommand("search 1/1 3/1"));
		output = model.getConsoleMessage();
		expected = "1 result found";
		assertEquals(expected, output);

		clear();

		// search taskID
		logic.executeCommand(MyParser.parseCommand("search 1"));
		output = model.getConsoleMessage();
		expected = "1 result found";
		assertEquals(expected, output);
		clear();

		// search 2 or more than 2 keywords
		logic.executeCommand(MyParser
				.parseCommand("search housekeeping homework camp"));
		output = model.getConsoleMessage();
		expected = "3 results found";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("search isCompleted:false"));
		output = model.getConsoleMessage();
		expected = "8 results found";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("search isCompleted:23:59"));
		output = model.getConsoleMessage();
		expected = "Error: Search Failed";
		assertEquals(expected, output);

		clear();

		logic.executeCommand(MyParser.parseCommand("s; #veryHard"));
		output = model.getConsoleMessage();
		expected = "1 result found";
		assertEquals(expected, output);

		clear();

	}

	// Set file location
	@Test
	public void testI() throws UnrecognisedCommandException {

		getConfigDetails();

		// Set file location
		logic.executeCommand(MyParser.parseCommand("set folder /temp"));
		output = model.getConsoleMessage();
		expected = "data file set to /temp";
		assertEquals(expected, output);

		clear();

		getConfigDetails();

		output = readFile(CONFIG_FILENAME);
		expected += dataFilePath;
		expected += "\n";
		expected += "avatar.png";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();

		// Set invalid file location
		logic.executeCommand(MyParser.parseCommand("set folder c:\\"));
		output = model.getConsoleMessage();
		expected = "Failed to Set new Path";
		assertEquals(expected, output);

		clear();

		getConfigDetails();

		output = readFile(CONFIG_FILENAME);
		expected += dataFilePath;
		expected += "\n";
		expected += "avatar.png";
		assertEquals(expected, output);

		clear();

		output = readFile(dataFilePath);
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":18,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"homework\",\"id\":1,\"isCompleted\":false,\"tags\":[\"mustTodo\",\"veryHard\"],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":16,\"minute\":0,\"second\":0},\"name\":\"do housekeeping\",\"id\":2,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":10,\"dayOfMonth\":20,\"hourOfDay\":22,\"minute\":0,\"second\":0},\"name\":\"assignment\",\"id\":4,\"isCompleted\":false,\"tags\":[],\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":13,\"hourOfDay\":13,\"minute\":0,\"second\":0},\"name\":\"project\",\"id\":5,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":9,\"dayOfMonth\":12,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2016,\"month\":9,\"dayOfMonth\":13,\"hourOfDay\":18,\"minute\":0,\"second\":0},\"name\":\"camp\",\"id\":6,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2016,\"month\":0,\"dayOfMonth\":1,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"end\":{\"year\":2016,\"month\":0,\"dayOfMonth\":3,\"hourOfDay\":23,\"minute\":59,\"second\":0},\"name\":\"meeting john\",\"id\":7,\"isCompleted\":false,\"tags\":[],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":11,\"dayOfMonth\":23,\"hourOfDay\":10,\"minute\":0,\"second\":0},\"end\":{\"year\":2015,\"month\":11,\"dayOfMonth\":27,\"hourOfDay\":12,\"minute\":0,\"second\":0},\"name\":\"project proposal\",\"details\":\"Task management software\",\"id\":9,\"isCompleted\":false,\"tags\":[\"imprt\"],\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"name\":\"dinner with parents\",\"id\":10,\"isCompleted\":false,\"tags\":[],\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		clear();
	}

	// Invalid command
	@Test
	public void testJ() throws UnrecognisedCommandException {
		logic.executeCommand(MyParser.parseCommand("serach housekeeping"));
		output = model.getConsoleMessage();
		expected = "Error: Invalid command";
		assertEquals(expected, output);

		clear();
	}

	@AfterClass
	public static void end() {
		reset();
	}

	public static void setUp() {
		File dataCopy = null, configCopy = null;
		try {

			configCopy = new File("./config"); // original path
			dataCopy = new File("./Data.txt"); // original path for DATA.txt
			if (configCopy.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(
						CONFIG_FILENAME));
				dataFilePathTemp = reader.readLine();
				reader.close();

				dataCopy = new File(dataFilePathTemp);
				setDataFile(dataCopy);
				setConfigFile(configCopy);

				hasConfigFile = true;
				hasDataFile = true;
			} else if (!configCopy.exists() && dataCopy.exists()) {
				dataFilePathTemp = "./Data.txt";
				dataCopy = new File(dataFilePathTemp);
				setDataFile(dataCopy);
				hasDataFile = true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			configCopy.delete();
		}
	}

	public static void reset() {
		InputStream inStream = null;
		OutputStream outStream = null;
		try {

			File configCopy = new File("./config"); // original path
			configCopy.delete();
			if (hasConfigFile == true && hasDataFile == true) {
				configCopy.createNewFile();
				inStream = new FileInputStream(dataTemp);
				File out = new File(dataFilePathTemp);
				File parent = out.getParentFile();
				if (dataFilePathTemp.equals("Data.txt")) {
					out.createNewFile();
				} else if (!parent.exists()) {
					out.getParentFile().mkdir();
					out.createNewFile();
				}

				outStream = new FileOutputStream(out);

				byte[] buffer = new byte[65536];

				int length;
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				inStream.close();
				outStream.close();

				dataTemp.delete();
				File temp = new File(".\\temp\\Data.txt");
				temp.delete();
				String absolutePath = temp.getAbsolutePath();
				String filePath = absolutePath.substring(0,
						absolutePath.lastIndexOf(File.separator));
				Path paths = Paths.get(filePath);
				Files.delete(paths);

				inStream = new FileInputStream(configTemp);
				outStream = new FileOutputStream(configCopy);

				buffer = new byte[65536];

				length = 0;
				// copy the file content in bytes
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				inStream.close();
				outStream.close();

				configTemp.delete();
				hasConfigFile = false;
			} else if (hasConfigFile == false) {
				File temp = new File(".\\temp\\Data.txt");
				temp.delete();
				String absolutePath = temp.getAbsolutePath();
				String filePath = absolutePath.substring(0,
						absolutePath.lastIndexOf(File.separator));
				Path paths = Paths.get(filePath);
				Files.delete(paths);

				configCopy.delete();
				if (hasDataFile = true) {
					inStream = new FileInputStream(dataTemp);
					File out = new File(dataFilePathTemp);
					File parent = out.getParentFile();
					if (dataFilePathTemp.equals("Data.txt")) {
						out.createNewFile();
					} else if (!parent.exists()) {
						out.getParentFile().mkdir();
						out.createNewFile();
					}

					outStream = new FileOutputStream(out);

					byte[] buffer = new byte[65536];

					int length;
					while ((length = inStream.read(buffer)) > 0) {
						outStream.write(buffer, 0, length);
					}

					inStream.close();
					outStream.close();

					dataTemp.delete();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private static void setDataFile(File dataCopy) throws IOException {
		String path = dataCopy.toString();
		if (path.contains("Data.txt") || path.contains("/temp/Data.txt")) {
			dataTemp = File.createTempFile("data", ".tmp");
			inStream = new FileInputStream(dataCopy);
			outStream = new FileOutputStream(dataTemp);

			byte[] buffer = new byte[65536];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			dataCopy.delete();
		}
	}

	private static void setConfigFile(File configCopy) throws IOException {
		configTemp = File.createTempFile("config", ".tmp");
		inStream = new FileInputStream(configCopy);
		outStream = new FileOutputStream(configTemp);

		byte[] buffer = new byte[65536];

		int length = 0;
		// copy the file content in bytes
		while ((length = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
		}

		inStream.close();
		outStream.close();

	}

	public void getConfigDetails() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					CONFIG_FILENAME));

			this.dataFilePath = reader.readLine();

			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clear() {
		output = "";
		expected = "";
	}
}
