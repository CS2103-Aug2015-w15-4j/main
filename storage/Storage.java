package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Task;

import com.google.gson.Gson;

import file.ReadFile;
import file.SaveFile;

public class Storage {

	private static final boolean OVERWRITE = false;
	private static final boolean APPEND = true;
	private static final String DEFAULT_FILENAME = "Data.txt";
	private List<Task> taskList;
	private static String fileName;
	private static String oldFileName;

	private static final String DEFAULT_FILEPATH = "Path.txt";
	private static final String DEFAULT_CONFIG = "Config.txt";
	private static final String DEFAULT_INNERIMAGEFILE = "InnerImagePath.txt";
	private String oldLocation;

	public Storage() {
		taskList = new ArrayList<Task>();
		initializeStorage();
	}

	public void add(Task task) {
		System.out.println("fileName : " + fileName);
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(fileName, APPEND);
			writer.write(gson.toJson(task));
			writer.write('\n');
			writer.close();

			taskList.add(task);
			System.out.println(taskList.size());
			Collections.sort(taskList);
			rewriteFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void delete(int taskID) {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskID == taskList.get(i).getId()) {
				taskList.remove(i);
			}
		}
		// Collections.sort(taskList);
		rewriteFile();
	}

	public void rewriteFile() {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(fileName, OVERWRITE);
			for (int i = 0; i < taskList.size(); i++) {
				writer.write(gson.toJson(taskList.get(i)));
				writer.write('\n');
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Task> getAllTasks() {
		return taskList;
	}

	public void initializeStorage() {

		if (checkPathFileExist()) {
			oldFileName = getPath();
			fileName = getPath();
		} else {
			createFile(DEFAULT_FILENAME);
			fileName = DEFAULT_FILENAME;
			oldFileName = getPath(DEFAULT_FILENAME);
		}

		Gson gson = new Gson();
		Task task = new Task();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String currentLine = reader.readLine();
			while (currentLine != null) {
				task = gson.fromJson(currentLine, Task.class);
				taskList.add(task);
				currentLine = reader.readLine();
			}
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------------------//

	public String getPath() {
		ReadFile rf = new ReadFile();
		String path = rf.readFile(DEFAULT_FILEPATH);
		return path;
	}

	public void addPath(String newPath) {
		SaveFile sf = new SaveFile();
		createFile(DEFAULT_FILEPATH);
		File file = new File(DEFAULT_FILEPATH);
		if (checkPathFileExist() && file.length() > 0) {
			oldLocation = getPath();
			oldFileName = oldLocation;
		} else {
			oldLocation = "Data.txt";
		}

		sf.saveFile(DEFAULT_FILEPATH, newPath);
		copyAndDelete(oldLocation, newPath);
	}

	public void addSetting(String input) {
		createFile(DEFAULT_CONFIG);
		SaveFile sf = new SaveFile();
		sf.saveFile(DEFAULT_CONFIG, input);
	}

	public void addInnerImage() {
		createFile(DEFAULT_INNERIMAGEFILE);
		/*
		 * Gson gson = new Gson();
		 * 
		 * ImagePath imagePath = new ImagePath();
		 * 
		 * try { BufferedReader reader = new BufferedReader(new
		 * FileReader(DEFAULT_INNERIMAGEFILE)); String currentLine =
		 * reader.readLine(); while (currentLine != null) { imagePath =
		 * gson.fromJson(currentLine, ImagePath.class); currentLine =
		 * reader.readLine(); } reader.close();
		 * 
		 * } catch (FileNotFoundException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
	}

	// Copy and delete
	public static void copyAndDelete(String oldPath, String newFilePath) {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			File afile = new File(oldPath); // original path
			File bfile = new File(newFilePath); // new path

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[65536];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();

			fileName = newFilePath;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean checkPathFileExist() {
		File file = new File(DEFAULT_FILEPATH);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	private String getPath(String name) {
		File f = new File(name);
		return f.getAbsolutePath();
	}

}
