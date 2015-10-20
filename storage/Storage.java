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

	private static final String DEFAULT_BACKGROUND_FILEPATH = "background.jpg";
	private static final String DEFAULT_AVATAR_FILEPATH = "avatar.png";
	private static final String CONFIG_FILENAME = "config";
	private static final boolean OVERWRITE = false;
	private static final boolean APPEND = true;
	private List<Task> taskList;
	
	private String dataFilePath;
	private String avatarFilePath;
	private String backgroundFilePath;
	
	private static String fileName;
	private static String oldFileName;

	private static final String DEFAULT_FILEPATH = "Data.txt";
	private static final String DEFAULT_CONFIG = "Config.txt";
	private static final String DEFAULT_INNERIMAGEFILE = "InnerImagePath.txt";
	private String oldLocation;

	public Storage() {
		taskList = new ArrayList<Task>();
		initializeStorage();
	}

	public void add(Task task) {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(dataFilePath, APPEND);
			writer.write(gson.toJson(task));
			writer.write('\n');
			writer.close();

			taskList.add(task);

			Collections.sort(taskList);
			rewriteFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sort() {
		Collections.sort(taskList);
		rewriteFile();
	}

	public void delete(int taskID) {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskID == taskList.get(i).getId()) {
				taskList.remove(i);
			}
		}
		Collections.sort(taskList);
		rewriteFile();
	}

	public void rewriteFile() {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(dataFilePath, OVERWRITE);
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
	
	public String getAvatarPath() {
		return avatarFilePath;
	}
	
	/*
	 * TODO ? : Maybe copy file into storage folder instead
	 */
	public void setAvatar(String path) {
		avatarFilePath = path;
		writeConfigDetails();
	}
	
	public void setBackground(String path) {
		backgroundFilePath = path;
		writeConfigDetails();
	}
	
	public String getBackgroundPath() {
		return backgroundFilePath;
	}
	
	public void getConfigDetails() {
		File configFile = new File(CONFIG_FILENAME);
		
		if(!configFile.exists()) {
			createFile(CONFIG_FILENAME);
			this.dataFilePath = DEFAULT_FILEPATH;
			this.avatarFilePath = DEFAULT_AVATAR_FILEPATH;
			this.backgroundFilePath = DEFAULT_BACKGROUND_FILEPATH;
			writeConfigDetails();
		} else {
			try {
			BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILENAME));
			
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
	}
	
	public void writeConfigDetails() {
		try {
		File configFile = new File(CONFIG_FILENAME);
		
		FileWriter writer = new FileWriter(configFile, OVERWRITE);
		writer.write(dataFilePath);
		writer.write('\n');
		writer.write(avatarFilePath);
		writer.write('\n');
		writer.write(backgroundFilePath);
		writer.write('\n');
		
		writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setFileLocation(String filePath) {
		File oldFile = new File(dataFilePath);
		this.dataFilePath = filePath;
		writeConfigDetails();
		taskList.clear();
		initializeStorage();
		oldFile.delete();
	}
	
	public void initializeStorage() {

		getConfigDetails();

		Gson gson = new Gson();
		Task task = new Task();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
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
