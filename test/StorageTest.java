package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;

import org.junit.Before;
import org.junit.Test;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;
import storage.Storage;

public class StorageTest {
	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	private static final String DEFAULT_BACKGROUND_FILEPATH = "background.jpg";
	private static final String DEFAULT_AVATAR_FILEPATH = "avatar.png";
	private static final String CONFIG_FILENAME = "config";
	private static final String DEFAULT_FILEPATH = "Data.txt";

	private static final boolean OVERWRITE = false;
	private static final boolean APPEND = true;

	private List<Task> taskList;

	private String dataFilePath;
	private String avatarFilePath;
	private String backgroundFilePath;

	Storage storage;
	ParsedCommand parser;

	String output = null;

	@Before
	public void init() {
		storage = new Storage();
	}

	// Testing with normal adding
	@Test
	public void testAddToFile() throws Exception {
		Task task = new Task("dinner with friends", "at centrepoint", 1, false,
				null, TASK);
		storage.add(task);

		getConfigDetails();

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

	}

	@Test
	public void testDeleteLine() throws Exception {

		storage.delete(1);

		getConfigDetails();
		String output = readFile(dataFilePath);

		String expected = "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

		expected = "";
		storage.delete(4);
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);
		expected = "";

	}

	@Test
	public void testSetFolder() throws Exception {

		boolean output = storage.setFileLocation(".\\");
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
		output = storage
				.setFileLocation("C:\\Users\\jiaminn\\Desktop\\Eclipse\\MyWorkspace\\main\\temp");
		assertEquals(true, output);

		getConfigDetails();
		String path = readFile(dataFilePath);
		String expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, path);

		output = storage.setFileLocation(".//");
		assertEquals(true, output);

		// folder exists
		output = storage.setFileLocation("C:\\Users\\jiaminn\\Desktop\\temp");
		assertEquals(true, output);

		output = storage.setFileLocation("C:\\Users\\jiaminn\\Desktop\\temp");
		assertEquals(true, output);

		expected = "";
		getConfigDetails();
		path = readFile(dataFilePath);
		expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected += "\n";
		expected += "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected += "\n";
		expected += "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, path);

	}

	@Test
	public void testSetAvatar() throws Exception {
		boolean output = storage.setAvatar("c:\\");
		assertEquals(false, output);

		output = storage
				.setAvatar("C:\\Users\\jiaminn\\Desktop\\Eclipse\\MyWorkspace\\main\\gui\\Log.java");
		assertEquals(false, output);

		output = storage
				.setAvatar("C:\\Users\\jiaminn\\Desktop\\Eclipse\\MyWorkspace\\main\\gui\\testing.jpg");
		assertEquals(false, output);

		output = storage
				.setAvatar("C:\\Users\\jiaminn\\Desktop\\Eclipse\\MyWorkspace\\main\\gui\\avatar2.png");
		assertEquals(true, output);

		output = storage
				.setAvatar("C:\\Users\\jiaminn\\Desktop\\Eclipse\\MyWorkspace\\main\\gui\\avatar.jpg");
		assertEquals(true, output);

	}

	public void getConfigDetails() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					CONFIG_FILENAME));

			this.dataFilePath = reader.readLine();
			this.avatarFilePath = reader.readLine();
			this.backgroundFilePath = reader.readLine();

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

	public void clearText(String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}