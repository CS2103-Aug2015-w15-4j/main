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
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;
import parser.ParsedCommand;

import com.google.gson.Gson;

public class Storage {

	private static final String DEFAULT_BACKGROUND_FILEPATH = "background.jpg";
	private static final String DEFAULT_AVATAR_FILEPATH = "avatar.png";
	private static final String CONFIG_FILENAME = "config";
	private static final String DATA_FILENAME = "Data.txt";
	private static final boolean OVERWRITE = false;
	private static final boolean APPEND = true;
	private List<Task> taskList;

	private String dataFilePath;
	private String avatarFilePath;
	private String backgroundFilePath;

	private static String fileName;

	public Storage() {
		taskList = new ArrayList<Task>();
		initializeStorage();
	}

	public void add(Task task) {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(dataFilePath, APPEND);
			writer.write(gson.toJson(task));
			writer.write("\r\n");
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
				writer.write("\r\n");
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
	public boolean setAvatar(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (isImageFile(path)) {
				avatarFilePath = path;
				writeConfigDetails();
				return true;
			}
		}
		return false;
	}

	public boolean setBackground(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (isImageFile(path)) {
				backgroundFilePath = path;
				writeConfigDetails();
				return true;
			}
		}
		return false;
	}

	public String getBackgroundPath() {
		return backgroundFilePath;
	}

	public void getConfigDetails() {
		File configFile = new File(CONFIG_FILENAME);

		if (!configFile.exists()) {
			if (createFile(CONFIG_FILENAME)) {
				this.dataFilePath = DATA_FILENAME;
				this.avatarFilePath = DEFAULT_AVATAR_FILEPATH;
				this.backgroundFilePath = DEFAULT_BACKGROUND_FILEPATH;
				writeConfigDetails();
			}
		} else {
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
	}

	public void writeConfigDetails() {
		try {
			File configFile = new File(CONFIG_FILENAME);

			FileWriter writer = new FileWriter(configFile, OVERWRITE);
			writer.write(dataFilePath);
			writer.write("\r\n");
			writer.write(avatarFilePath);
			writer.write("\r\n");
			writer.write(backgroundFilePath);
			writer.write("\r\n");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean setFileLocation(String filePath) throws Exception {

		if (filePath.startsWith("/") || filePath.startsWith("\\")) {
			if (filePath.length() == 1) {
				return false;
			} else {
				filePath = "." + filePath;
			}
		}

		File newFile = new java.io.File(filePath);
		String separator = File.separator;
		String path = null, finalPath = null;

		if (!newFile.exists()) {
			newFile.mkdirs();
			path = newFile.getPath();
			finalPath = path + separator + DATA_FILENAME;
			File newPath = new File(finalPath);
			try {
				if (createFile(finalPath)) {
					if (newPath.canWrite()) {
						filePath = (newPath.toPath()).toString();
						copyAndDelete(dataFilePath, finalPath);

						this.dataFilePath = filePath;
						writeConfigDetails();
						return true;
					}
				}
			} catch (SecurityException se) {
				// throw new Exception();
				return false;
			}
		} else {
			if (newFile.isDirectory()) {
				path = newFile.getPath();
				finalPath = path + separator + DATA_FILENAME;
				try {
					if (newFile.canWrite()) {
						if (createFile(finalPath)) {
							createFile(finalPath);
							copyAndDelete(dataFilePath, finalPath);
							filePath = finalPath;

							this.dataFilePath = filePath;
							writeConfigDetails();
							return true;
						}
					}
				} catch (SecurityException se) {
					// throw new Exception();
					return false;
				}
			} else if (newFile.isFile()) {
				copyAndDelete(dataFilePath, filePath);
			}
		}
		// throw new Exception();
		return false;
	}

	public void initializeStorage() {

		getConfigDetails();

		Gson gson = new Gson();
		Task task = new Task();

		if (createFile(dataFilePath)) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						dataFilePath));
				String currentLine = reader.readLine();
				while (currentLine != null) {
					task = gson.fromJson(currentLine, Task.class);

					if (task.getTaskType().equals(
							ParsedCommand.TaskType.DEADLINE_TASK)) {
						task = (Task) gson.fromJson(currentLine,
								DeadlineTask.class);
					} else if (task.getTaskType().equals(
							ParsedCommand.TaskType.EVENT)) {
						task = (Task) gson.fromJson(currentLine, Event.class);
					}
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

	}

	// Copy and delete text file
	private static void copyAndDelete(String oldPath, String newFilePath) {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			File afile = new File(oldPath); // original path
			File bfile = new File(newFilePath); // new path
			File temp = File.createTempFile("temp", ".tmp"); // temp file

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(temp);

			byte[] buffer = new byte[65536];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// System.out.println("afile : " + afile.getCanonicalFile());
			// System.out.println("bfile : " + bfile.getCanonicalFile());

			// delete the original file
			if (!afile.getCanonicalFile().equals(bfile.getCanonicalFile())) {
				afile.delete();

				// delete folder is up to one level only
				try {
					// delete folder
					String absolutePath = afile.getAbsolutePath();
					String filePath = absolutePath.substring(0,
							absolutePath.lastIndexOf(File.separator));
					Path paths = Paths.get(filePath);
					Files.delete(paths);
				} catch (FileSystemException e) {
				}

			}

			buffer = new byte[65536];
			length = 0;
			inStream = new FileInputStream(temp);
			outStream = new FileOutputStream(bfile);

			buffer = new byte[65536];
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			temp.delete();
			fileName = newFilePath;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean createFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private boolean isImageFile(String fileName) {
		File f = new File(fileName);
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		try {
			String mimetype = fileNameMap.getContentTypeFor(fileName);
			String type = mimetype.split("/")[0];
			if (type.equals("image")) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}
}