package storage;

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;

import logic.Task;

import org.junit.Before;
import org.junit.Test;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;
import file.ReadFile;

public class StorageTest {
	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	private static final String DEFAULT_BACKGROUND_FILEPATH = "background.jpg";
	private static final String DEFAULT_AVATAR_FILEPATH = "avatar.png";
	private static final String CONFIG_FILENAME = "config";
	private static final String DEFAULT_FILEPATH = "Data.txt";
	private static final String DEFAULT_PATHFILE = "Path.txt";

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

		ReadFile rf = new ReadFile();
		String output = rf.readFile(DEFAULT_FILEPATH);
		String expected = "{\"name\":\"dinner with friends\",\"details\":\"at centrepoint\",\"id\":1,\"isCompleted\":false,\"taskType\":0}";
		assertEquals(expected, output);
	}

	@Test
	public void testDeleteLine() throws Exception {

		storage.delete(1);

		ReadFile rf = new ReadFile();
		String output = rf.readFile(DEFAULT_FILEPATH);

		String expected = null;
		assertEquals(expected, output);

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
