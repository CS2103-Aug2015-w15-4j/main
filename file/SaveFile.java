package file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import logic.Task;

public class SaveFile {

	public SaveFile() {
	}

	// Save file
	public static void saveFile(File fileInput, List<Task> list) {
		clearFile(fileInput);
		try {
			FileWriter fw = new FileWriter(fileInput, true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Task temp : list) {
				// getDate(), getTime() ???
				bw.write(temp.getName() + "|" + temp.getDetails() + "|"
						+ temp.getId() + "|" + temp.getIsCompleted() + "|"
						+ temp.getTags() + "\r\n");
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// Clear the text
	public static void clearFile(File fileInput) {
		try {
			FileWriter fw = new FileWriter(fileInput);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
