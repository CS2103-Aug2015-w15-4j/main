package storage;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;

import org.junit.Before;
import org.junit.Test;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

public class StorageTest {
	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	private static final String DEFAULT_BACKGROUND_FILEPATH = "background.jpg";
	private static final String DEFAULT_AVATAR_FILEPATH = "avatar.png";
	private static final String CONFIG_FILENAME = "config";
	private static final String DEFAULT_FILEPATH = "Data.txt";

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
		output = null;
		expected = null;
		output = readFile(dataFilePath);
		expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected = "\r\n";
		expected = "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		assertEquals(expected, output);

		DeadlineTask deadLine = new DeadlineTask("meeting Luxola",
				"at centrepoint", 3, false, null, DEADLINETASK, calEnd);
		storage.add(deadLine);
		output = null;
		expected = null;
		output = readFile(dataFilePath);
		expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":\"FLOATING_TASK\"}";
		expected = "\r\n";
		expected = "{\"start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT\"}";
		expected = "\r\n";
		expected = "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

	}

	@Test
	public void testDeleteLine() throws Exception {

		storage.delete(1);

		String output = readFile(DEFAULT_FILEPATH);

		String expected = "{\"[start\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":2,\"isCompleted\":false,\"taskType\":\"EVENT]\"}";
		expected = "\r\n";
		expected = "{\"end\":{\"year\":2015,\"month\":2,\"dayOfMonth\":15,\"hourOfDay\":0,\"minute\":2,\"second\":37},\"name\":\"meeting Luxola\",\"details\":\"at centrepoint\",\"id\":3,\"isCompleted\":false,\"taskType\":\"DEADLINE_TASK\"}";
		assertEquals(expected, output);

	}

	public void getConfigDetails() {
		File configFile = new File(CONFIG_FILENAME);
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

	public String readFile(String fileInput) {
		String sCurrentLine = "", result = null;
		File file = new File(fileInput);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((sCurrentLine = br.readLine()) != null) {
				result = sCurrentLine;
			}
			fr.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
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