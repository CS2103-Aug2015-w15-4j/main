package file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;

// Main class for file storage
public class Storage {

	static File filePath = new File("./Path/path.txt"); // store current
														// location

	static String fileDirectory = "./Storage/";
	static String fileName = "myData.txt";
	static File fileInput;

	static List<Task> listOfTask = new ArrayList<Task>();

	// static HashMap<Integer, List<Task> task> = new HashMap();

	public Storage() {
		this.fileName = fileName;
		createFile(fileName);
	}

	// Get the path at the start, create the file for the first time
	// need to set a flag? to check this is first time calling
	/*
	 * public Storage(String fileName) { this.fileName = fileName;
	 * createFile(fileName); }
	 */

	// Open the folder which contain the path and read it
	public static void readFilePath() {
	}

	// Move the file to the new location
	public static void changeLocation(String filePath) {
		copyAndDelete(filePath + "/myData.txt");
	}

	// Read the file
	public static List<Task> readFile() {
		ReadFile rf = new ReadFile();
		listOfTask = rf.readFile(fileInput);
		return listOfTask;
	}

	// Add
	public void saveFileForAdd(Task task) {
		SaveFile sf = new SaveFile();
		sf.addAndSaveFile(fileInput, task);
	}

	// Delete
	public void saveFileForDelete(int id) {
		// compare with the list
		// get the id
		// delete
		// get the content
		SaveFile sf = new SaveFile();
		System.out.println(fileInput);
		sf.deleteAndSaveFile(fileInput, id);
	}

	// Edit
	public static void editFile() {
	}

	// Check whether the file exists
	public static void createFile(String fileName) {
		File fileDir = new File(fileDirectory);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		fileInput = new File(fileDirectory + fileName);

		// check the file size
		// if > 0
		// read the file
		// and save it to list
		if (fileInput.length() > 0) {
			readFile();
		}

		try {
			fileInput.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Create a folder to store myData.txt path
	public static void createFilePath() {
		try {
			filePath.createNewFile();
			FileWriter fw = new FileWriter(fileInput, true);
			BufferedWriter bw = new BufferedWriter(fw);
			Path fullPath = filePath.toPath();
			bw.write(String.valueOf(fullPath));
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Copy and delete
	public static void copyAndDelete(String newfilePath) {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			FileWriter fw = new FileWriter(filePath, true);
			fw.flush();
			BufferedWriter bw = new BufferedWriter(fw);

			File afile = new File(fileName); // original path
			File bfile = new File(newfilePath); // new path

			Path fullPath = bfile.toPath();
			bw.write(String.valueOf(fullPath));
			bw.close();
			fw.close();

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFieName() {
		return this.fileName;
	}

	public List<Task> getAllTasks() {
		ArrayList<Task> stubTaskList = new ArrayList<Task>();
		stubTaskList.add(new Event());
		stubTaskList.add(new DeadlineTask());
		stubTaskList.add(new DeadlineTask());
		stubTaskList.add(new Event());
		return stubTaskList;
	}

}
