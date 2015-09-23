package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import logic.Storage;
import logic.Task;

public class FileStorage {

	String fileName;
	static File fileInput;

	public FileStorage(String fileName) {
		this.fileName = fileName;
		createFile(fileName);
	}
	
	public static List<Task> readFile() {
		ReadFile rf = new ReadFile();
		return rf.readFile(fileInput);
	}

	public static void saveFile() {
		Storage storage = new Storage();
		SaveFile sf = new SaveFile();
		sf.saveFile(fileInput, storage.getAllTasks());
	}

	// Check whether the file exists
	public static void createFile(String fileName) {
		fileInput = new File(fileName);
		try {
			fileInput.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
