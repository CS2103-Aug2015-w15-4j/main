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
import java.util.List;

import logic.Task;

// Main class for file storage
public class FileStorage {

	static File filePath = new File("./Path/path.txt"); // store current
														// location
	static String fileName;
	static File fileInput;

	public FileStorage() {
	}

	// Get the path at the start, create the file for the first time
	public FileStorage(String fileName) {
		this.fileName = fileName;
		createFile(fileName);
	}

	// Move the file to the new location
	public static void changeLocation(String filePath) {
		copyAndDelete(filePath + "/myData.txt");
	}

	// Read the file
	public static List<Task> readFile() {
		ReadFile rf = new ReadFile();
		return rf.readFile(fileInput);
	}

	// ADD
	public static void saveFileForAdd(List<Task> task) {
		SaveFile sf = new SaveFile();
		sf.addAndSaveFile(fileInput, task);
	}

	// DELETE
	public static void saveFileForDelete(Task task) {
		SaveFile sf = new SaveFile();
		sf.deleteAndSaveFile(fileInput, task);
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

}
