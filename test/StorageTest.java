//@@author A0126276A

package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;
import storage.Storage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageTest {
	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	private static final String CONFIG_FILENAME = "config";

	private String dataFilePath;
	@SuppressWarnings("unused")
	private String avatarFilePath;

	static InputStream inStream = null;
	static OutputStream outStream = null;

	static File dataTemp;
	static File configTemp;

	static Storage storage;
	ParsedCommand parser;

	String output = null;

	@BeforeClass
	public static void init() {
		setUp();
		storage = new Storage();
		storage.clearList();
	}

	// Add
	@Test
	public void testA() throws Exception {

		getConfigDetails();

		Task task = new Task("dinner with friends", "at centrepoint", 1, false,
				null, TASK);
		storage.add(task);

		String output = readFile(dataFilePath);
		String expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		assertEquals(expected, output);

		Calendar calEnd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		calEnd.setTime(sdf.parse("Mon Mar 14 16:02:37 GMT 2015"));// all done

		Event event = new Event("dinner with friends", "at centrepoint", 2,
				false, null, EVENT, calEnd, calEnd);
		storage.add(event);
		output = "";
		expected = "";
		output = readFile(dataFilePath);
		expected += "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		DeadlineTask deadLine = new DeadlineTask("meeting Luxola",
				"at centrepoint", 3, false, null, DEADLINETASK, calEnd);
		storage.add(deadLine);
		output = "";
		expected = "";
		output = readFile(dataFilePath);
		expected += "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		deadLine = new DeadlineTask("meeting Luxola", "at centrepoint", 4,
				false, null, DEADLINETASK, calEnd);
		Event event2 = new Event(deadLine, calEnd);
		storage.add(event2);
		output = "";
		expected = "";
		output = readFile(dataFilePath);
		expected += "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":4,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

	}

	// Delete
	@Test
	public void testB() throws Exception {
		storage.delete(1);

		getConfigDetails();
		String output = readFile(dataFilePath);

		String expected = "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":4,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		output = "";
		expected = "";
		storage.delete(4);
		output = readFile(dataFilePath);
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

	}

	// Set folder
	@Test
	public void testC() throws Exception {

		boolean output = storage.setFileLocation(".\\");
		assertEquals(true, output);

		output = storage.setFileLocation(".//");
		assertEquals(true, output);

		output = storage.setFileLocation("//");
		assertEquals(true, output);

		output = storage.setFileLocation("./new");
		assertEquals(true, output);

		output = storage.setFileLocation("//new");
		assertEquals(true, output);

		output = storage.setFileLocation("\\new");
		assertEquals(true, output);

		// folder is not exist
		output = storage.setFileLocation(".\\temp");
		assertEquals(true, output);

		getConfigDetails();
		String path = readFile(dataFilePath);
		String expected = "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, path);

	}

	// Set Avatar
	@Test
	public void testD() throws Exception {

		boolean output = storage.setAvatar("c:\\");
		assertEquals(false, output);

		output = storage.setAvatar(".\\gui\\Log.java");
		assertEquals(false, output);

		output = storage.setAvatar(".\\gui\\testing.jpg");
		assertEquals(false, output);

		output = storage.setAvatar(".\\gui\\avatar2.png");
		assertEquals(true, output);

		output = storage.setAvatar(".\\gui\\avatar.jpg");
		assertEquals(true, output);

	}

	@AfterClass
	public static void end() throws IOException {
		reset();
	}

	public static void setUp() {
		File dataFile = new File("Data.txt");
		dataFile.renameTo(new File("temp.txt"));
		File testFile = new File("Test_Data2.txt");
		testFile.renameTo(new File("Data.txt"));
		File configFile = new File("config");
		configFile.renameTo(new File(("configTemp")));
	}

	public static void reset() throws IOException {
		File temp = new File(".\\temp\\Data.txt");
		temp.delete();
		String absolutePath = temp.getAbsolutePath();
		String filePath = absolutePath.substring(0,
				absolutePath.lastIndexOf(File.separator));
		Path paths = Paths.get(filePath);
		Files.delete(paths);

		File dataFile = new File("Data.txt");
		dataFile.renameTo(new File("Test_Data2.txt"));
		File testFile = new File("temp.txt");
		testFile.renameTo(new File("Data.txt"));
		File configCopy = new File("config");
		configCopy.delete();
		File configFile = new File("configTemp");
		configFile.renameTo(new File(("config")));
	}

	public void getConfigDetails() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					CONFIG_FILENAME));

			this.dataFilePath = reader.readLine();
			this.avatarFilePath = reader.readLine();

			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

}